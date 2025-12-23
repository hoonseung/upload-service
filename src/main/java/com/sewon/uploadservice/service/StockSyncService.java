package com.sewon.uploadservice.service;

import com.sewon.uploadservice.model.dto.mes.UniqueFactoryItemCode;
import com.sewon.uploadservice.model.entity.MesBox;
import com.sewon.uploadservice.model.entity.MesOutboundStock;
import com.sewon.uploadservice.repository.car.CarOrderMapper;
import com.sewon.uploadservice.repository.mes.MESStockMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
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

    // 월요일 ~ 토요일 오전 8시 ~ 오후 9시까지 5분 주기로 수행
    // 검사장 검사대기, 포장완료
    // 영업 입고 수량
    @Scheduled(cron = "0 */1 8-21 * * 1-6")
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

    @Scheduled(cron = "0 */1 8-21 * * 1-6")
    @Transactional(transactionManager = "postgresqlTransactionManager")
    public void asyncMesWaitingStock() {
        List<UniqueFactoryItemCode> uniqueCode = carOrderMapper.findAllMesStock()
            .stream().map(stock -> UniqueFactoryItemCode.of(stock.getFactory(), stock.getItemCode()))
            .distinct()
            .toList();

        List<List<UniqueFactoryItemCode>> factoryUniqueLists = getFactoryUniqueLists(uniqueCode);
        int chunkSize = 500;
        for (int i = 0; i < uniqueCode.size(); i += chunkSize) {
            factoryUniqueLists.add(uniqueCode.subList(i,
                    Math.min(i + chunkSize, uniqueCode.size())
                )
            );
        }

        // 청크 단위 만큼 수행
        List<CompletableFuture<Void>> mesFutures = new ArrayList<>();
        factoryUniqueLists.forEach(codes -> {
                mesFutures.add(CompletableFuture.supplyAsync(
                        () -> stockSearchService.getBulkMESStockUpdateOnly(codes))
                    .thenAccept(carOrderMapper::bulkUpdateMesInboundStock)
                );

                mesFutures.add(CompletableFuture.supplyAsync(
                        () -> stockSearchService.getBulkMESStockBox(codes.stream().map(
                            UniqueFactoryItemCode::itemCode).toList()))
                    .thenAccept(carOrderMapper::bulkUpdateMesInboundStockBox)
                );
            }
        );

        CompletableFuture.allOf(mesFutures.toArray(CompletableFuture[]::new))
            .exceptionally(ex -> {
                    log.error("Bulk mes stock query failed: {}", ex.getMessage());
                    return null;
                }
            )
            .join();
        log.info("asyncMesWaitingStock update success time: {}", LocalDateTime.now());
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
    @Scheduled(cron = "0 */5 9-21 * * 1-6")
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
    @Scheduled(cron = "0 0 8 * * *")
    @Transactional(transactionManager = "postgresqlTransactionManager")
    public void deleteOutbound(){
        carOrderMapper.deleteMesOutboundStock(LocalDate.now().minusDays(1));
        log.info("deleteMesOutboundStock success time: {}", LocalDateTime.now());
    }


    // 매일 오전 8시 삭제 수행
    @Scheduled(cron = "0 0 8 * * *")
    @Transactional(transactionManager = "postgresqlTransactionManager")
    public void deleteOutboundTarget(){
        carOrderMapper.deleteOutboundTarget(LocalDate.now().minusDays(1));
        log.info("deleteOutboundTarget success time: {}", LocalDateTime.now());
    }

}
