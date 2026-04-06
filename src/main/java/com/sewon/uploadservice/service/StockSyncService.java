package com.sewon.uploadservice.service;

import com.sewon.uploadservice.model.dto.mes.UniqueFactoryItemCode;
import com.sewon.uploadservice.model.entity.MesBox;
import com.sewon.uploadservice.model.entity.MesOutboundStock;
import com.sewon.uploadservice.repository.car.CarOrderMapper;
import com.sewon.uploadservice.repository.mes.MESStockMapper;
import jakarta.annotation.PreDestroy;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class StockSyncService {

    private final StockSearchService stockSearchService;
    private final CarOrderMapper carOrderMapper;
    private final MESStockMapper mesStockMapper;

    /**
     * MES 동기화는 DB 업데이트를 포함하므로 무제한 병렬 실행 시 데드락/락 경합이 커질 수 있습니다.
     * ForkJoin commonPool 대신 제한된 스레드 풀을 사용해 동시 실행 수를 제어합니다.
     */
    private static final int MES_UPDATE_WORKERS = 4;
    private final ExecutorService mesUpdateExecutor = Executors.newFixedThreadPool(MES_UPDATE_WORKERS);

    // 월요일 ~ 토요일 오전 8시 ~ 오후 9시까지 5분 주기로 수행
    // 검사장 검사대기, 포장완료
    // 영업 입고 수량
    @Scheduled(cron = "0 */1 8-21 * * 1-6", zone = "Asia/Seoul")
    @Transactional(transactionManager = "postgresqlTransactionManager")
    public void asyncMesWaitingBox() {
        List<String> uniqueItemCode = carOrderMapper.findAllMesBox()
            .stream().map(MesBox::getItemCode)
            .distinct()
            .toList();
        List<List<String>> itemCodes = getUniqueItemCodeLists(uniqueItemCode);
        // 청크 단위 만큼 수행
        List<CompletableFuture<Void>> mesFutures = new ArrayList<>();
        itemCodes.forEach(codes ->
                mesFutures.add(CompletableFuture.supplyAsync(
                        () -> stockSearchService.getBulkMESAllBox(codes))
                    .thenAccept(box ->
                        carOrderMapper.bulkUpdateMesBox(box.values().stream().toList())
                    )
                )
        );

        CompletableFuture.allOf(mesFutures.toArray(CompletableFuture[]::new))
            .exceptionally(ex -> {
                    log.error("Bulk mex box query failed: {}", ex.getMessage());
                    return null;
                }
            )
            .join();
        log.info("asyncMesWaitingBox update success time: {}", LocalDateTime.now());
    }

    private List<List<String>> getUniqueItemCodeLists(List<String> uniqueItemCode) {
        List<List<String>> itemCodes = new ArrayList<>();
        int chunkSize = 500;
        for (int i = 0; i < uniqueItemCode.size(); i += chunkSize) {
            itemCodes.add(uniqueItemCode.subList(i,
                    Math.min(i + chunkSize, uniqueItemCode.size())
                )
            );
        }
        return itemCodes;
    }

    @Scheduled(cron = "0 */3 8-21 * * 1-6", zone = "Asia/Seoul")
    @Transactional(transactionManager = "postgresqlTransactionManager")
    public void asyncMesWaitingStock() {
        List<UniqueFactoryItemCode> uniqueCode = carOrderMapper.findAllMesStock()
            .stream().map(stock -> UniqueFactoryItemCode.of(stock.getFactory(), stock.getItemCode()))
            .distinct()
            .toList();

        // 이미 getFactoryUniqueLists()에서 chunking이 수행되므로 중복으로 add 하지 않습니다.
        List<List<UniqueFactoryItemCode>> factoryUniqueLists = getFactoryUniqueLists(uniqueCode);

        // 청크 단위 만큼 수행 (청크 내부에서는 항상 stock -> stock_box 순서로 업데이트)
        // 또한 Executor로 병렬도를 제한해 데드락/락 경합을 줄입니다.
        List<CompletableFuture<Void>> mesFutures = new ArrayList<>();
        factoryUniqueLists.forEach(codes -> mesFutures.add(
            CompletableFuture.runAsync(() -> {
                // 1) MES 조회
                var stocks = stockSearchService.getBulkMESStockUpdateOnly(codes);
                var itemCodes = codes.stream().map(UniqueFactoryItemCode::itemCode).toList();
                var stockBoxes = stockSearchService.getBulkMESStockBox(itemCodes);

                // 2) DB 업데이트 (락 순서 고정)
                carOrderMapper.bulkUpdateMesInboundStock(stocks);
                carOrderMapper.bulkUpdateMesInboundStockBox(stockBoxes);
            }, mesUpdateExecutor)
        ));

        CompletableFuture.allOf(mesFutures.toArray(CompletableFuture[]::new))
            .exceptionally(ex -> {
                    log.error("Bulk mes stock query failed: {}", ex.getMessage());
                    return null;
                }
            )
            .join();
        log.info("asyncMesWaitingStock update success time: {}", LocalDateTime.now());
    }

    /**
     * 애플리케이션 종료 시 스레드 누수 방지.
     */
    @PreDestroy
    public void shutdownExecutors() {
        mesUpdateExecutor.shutdown();
        try {
            if (!mesUpdateExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                mesUpdateExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            mesUpdateExecutor.shutdownNow();
        }
    }

    private List<List<UniqueFactoryItemCode>> getFactoryUniqueLists(List<UniqueFactoryItemCode> uniqueItemCode) {
        List<List<UniqueFactoryItemCode>> itemCodes = new ArrayList<>();
        int chunkSize = 500;
        for (int i = 0; i < uniqueItemCode.size(); i += chunkSize) {
            itemCodes.add(uniqueItemCode.subList(i,
                    Math.min(i + chunkSize, uniqueItemCode.size())
                )
            );
        }
        return itemCodes;
    }


    // 월요일 ~ 토요일 오전 9시 ~ 오후 9시까지 5분 주기로 수행
    // 영업 출고 수량
    @Scheduled(cron = "0 */5 9-21 * * 1-6", zone = "Asia/Seoul")
    @Transactional(transactionManager = "postgresqlTransactionManager")
    public void asyncMesOutboundStock() {
        List<MesOutboundStock> stocks = mesStockMapper.findOutboundStockTotalByDate(
                LocalDate.now(), LocalDate.now())
            .stream()
            .map(MesOutboundStock::from)
            .toList();
        // UPSERT
        if (stocks.isEmpty()) {
            return;
        }
        carOrderMapper.bulkInsertOutboundMesStock(stocks);
        log.info("asyncMesOutboundStock update success time: {}", LocalDateTime.now());
    }

    // 매일 오전 8시 삭제 수행
    @Scheduled(cron = "0 0 8 * * *", zone = "Asia/Seoul")
    @Transactional(transactionManager = "postgresqlTransactionManager")
    public void deleteOutbound(){
        carOrderMapper.deleteMesOutboundStock(LocalDate.now().minusDays(1));
        log.info("deleteMesOutboundStock success time: {}", LocalDateTime.now());
    }


    // 매일 오전 8시 삭제 수행
    @Scheduled(cron = "0 0 8 * * *", zone = "Asia/Seoul")
    @Transactional(transactionManager = "postgresqlTransactionManager")
    public void deleteOutboundTarget(){
        carOrderMapper.deleteOutboundTarget(LocalDate.now().minusDays(1));
        log.info("deleteOutboundTarget success time: {}", LocalDateTime.now());
    }

}
