package com.sewon.uploadservice.service;

import com.sewon.uploadservice.model.dto.csv.SecondOutboundData;
import com.sewon.uploadservice.model.dto.csv.UpdateLineAndCustomerStock;
import com.sewon.uploadservice.model.dto.mes.MESInboundStockBoxRecord;
import com.sewon.uploadservice.model.entity.CarOrder;
import com.sewon.uploadservice.model.dto.erp.ERPStockRecord;
import com.sewon.uploadservice.model.dto.mes.MESInboundStockRecord;
import com.sewon.uploadservice.model.entity.MesBox;
import com.sewon.uploadservice.model.entity.MesInboundStock;
import com.sewon.uploadservice.model.collection.ERPLocation;
import com.sewon.uploadservice.model.dto.CombineData;
import com.sewon.uploadservice.model.dto.csv.CsvData;
import com.sewon.uploadservice.model.dto.csv.DayPlusData;
import com.sewon.uploadservice.model.dto.erp.ErpStockData;
import com.sewon.uploadservice.model.dto.mes.MesBoxData;
import com.sewon.uploadservice.model.dto.csv.Ttime;
import com.sewon.uploadservice.model.entity.MesInboundStockBox;
import com.sewon.uploadservice.model.entity.SecondOutbound;
import com.sewon.uploadservice.repository.car.CarOrderMapper;
import com.sewon.uploadservice.model.dto.erp.TargetLocationDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Service
public class UploadService {

    private final CsvFileParser csvFileParser;

    private final StockSearchService stockSearchService;
    private final CarOrderMapper carOrderMapper;

    private final Set<String> refUniqueItemCodeSet = new HashSet<>();

    @Transactional(transactionManager = "postgresqlTransactionManager")
    public void planUpload(List<MultipartFile> gFiles, List<MultipartFile> dFiles,
        LocalDate uploadingDate) {
        List<CombineData> dataList = combineCSV(gFiles, dFiles, LocalDate.now());

        List<CarOrder> carOrders = dataList.stream()
            .map(data -> {
                CsvData csv = data.getCsvData();
                DayPlusData dayplusData = csv.getDayplusData();
                ErpStockData erp = data.getErpStockData();

                return getCarOrder(csv, erp, dayplusData, uploadingDate);

            }).toList();

        int batchSize = 1000;
        for (int i = 0; i < carOrders.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, carOrders.size());
            List<CarOrder> orderList = carOrders.subList(i, endIndex);
            carOrderMapper.bulkInsertCarOrder(orderList);
        }

        // mes_box 에서 존재하지 않는 품번 조회
        List<String> missingItemCodesByMesBox = carOrderMapper.getMissingItemCodeByMesBox(
            refUniqueItemCodeSet.toArray(String[]::new));

        if (!missingItemCodesByMesBox.isEmpty()) {
            // 값이 있으면 추가
            List<MesBox> missingMesBoxes = getMissingMesBoxes(missingItemCodesByMesBox, 500);

            // 전체 리스트를 배치 사이즈만 큼 반복
            for (int i = 0; i < missingMesBoxes.size(); i += batchSize) {
                int endIdx = Math.min(i + batchSize, missingMesBoxes.size());
                List<MesBox> mesBoxes = missingMesBoxes.subList(i, endIdx);
                carOrderMapper.bulkInsertMesBox(mesBoxes);
            }
        }

        // mes_stock 에서 존재하지 않는 품번 조회
        List<String> missingItemCodesByMesStock = carOrderMapper.getMissingItemCodeByMesStock(
            refUniqueItemCodeSet.toArray(String[]::new));
        // mes_stock box 에서 존재하지 않는 품번 조회
        List<String> missingItemCodesByMesStockBox = carOrderMapper.getMissingItemCodeByMesStockBox(
            refUniqueItemCodeSet.toArray(String[]::new));
        if (!missingItemCodesByMesStock.isEmpty()) {
            List<MesInboundStock> missingMesStock = getMissingMesStock(missingItemCodesByMesStock,
                500);
            List<MesInboundStockBox> missingMesStockBox = getMissingMesStockBox(
                missingItemCodesByMesStockBox, 500);

            for (int i = 0; i < missingMesStock.size(); i += batchSize) {
                int stockEndIdx = Math.min(i + batchSize, missingMesStock.size());
                int boxEndIdx = Math.min(i + batchSize, missingMesStockBox.size());
                List<MesInboundStock> mesStocks = missingMesStock.subList(i, stockEndIdx);
                List<MesInboundStockBox> mesStockBoxes = missingMesStockBox.subList(i, boxEndIdx);
                carOrderMapper.bulkInsertInboundMesStock(mesStocks);
                carOrderMapper.bulkInsertInboundMesStockBox(mesStockBoxes);
            }
        }
    }

    @Transactional(transactionManager = "postgresqlTransactionManager")
    public void secondOutboundUpload(MultipartFile file, LocalDate date) {
        List<SecondOutbound> outbounds = csvFileParser.secondOutboundFileParsing(file, date)
            .stream()
            .map(SecondOutbound::from)
            .toList();
        carOrderMapper.bulkInsertSecondOutbound(outbounds);
    }

    @Transactional(transactionManager = "postgresqlTransactionManager")
    public void updateLineAndCustomerStock(MultipartFile file) {
        List<UpdateLineAndCustomerStock> updateLineAndCustomerStocks = csvFileParser.lineAndCustomerStockFileParsing(
            file);
        carOrderMapper.bulkUpdateLineAndYraStock(updateLineAndCustomerStocks);
    }


    public List<MesBox> getMissingMesBoxes(List<String> missingItemCodes, int chunkSize) {
        List<List<String>> itemCodeChunks = getItemCodeChunks(missingItemCodes, chunkSize);
        List<CompletableFuture<Map<String, MesBoxData>>> allMesStocksFuture = new ArrayList<>();
        for (List<String> codes : itemCodeChunks) {
            allMesStocksFuture.add(
                CompletableFuture.supplyAsync(() -> stockSearchService.getBulkMESAllBox(codes)
                ));
        }
        return CompletableFuture.allOf(allMesStocksFuture.toArray(CompletableFuture[]::new))
            .thenApply(v ->
                allMesStocksFuture.stream()
                    .map(CompletableFuture::join)
                    .map(Map::entrySet)
                    .flatMap(Collection::stream)
                    .map(entry -> MesBox.from(entry.getValue()))
            ).join()
            .toList();
    }

    public List<MesInboundStock> getMissingMesStock(List<String> missingItemCodes, int chunkSize) {
        List<List<String>> itemCodeChunks = getItemCodeChunks(missingItemCodes, chunkSize);
        List<CompletableFuture<List<MesInboundStock>>> allMesStocksFuture = new ArrayList<>();
        for (List<String> codes : itemCodeChunks) {
            allMesStocksFuture.add(
                CompletableFuture.supplyAsync(() -> stockSearchService.getBulkMESStock(codes)
                ));
        }
        return CompletableFuture.allOf(allMesStocksFuture.toArray(CompletableFuture[]::new))
            .thenApply(v ->
                allMesStocksFuture.stream()
                    .map(CompletableFuture::join)
                    .flatMap(Collection::stream)
            ).join()
            .toList();
    }

    public List<MesInboundStockBox> getMissingMesStockBox(List<String> missingItemCodes,
        int chunkSize) {
        List<List<String>> itemCodeChunks = getItemCodeChunks(missingItemCodes, chunkSize);
        List<CompletableFuture<List<MesInboundStockBox>>> allMesStocksFuture = new ArrayList<>();
        for (List<String> codes : itemCodeChunks) {
            allMesStocksFuture.add(
                CompletableFuture.supplyAsync(() -> stockSearchService.getBulkMESStockBox(codes)
                ));
        }

        return CompletableFuture.allOf(allMesStocksFuture.toArray(CompletableFuture[]::new))
            .thenApply(v ->
                allMesStocksFuture.stream()
                    .map(CompletableFuture::join)
            ).join()
            .flatMap(List::stream)
            .toList();
    }

    private List<List<String>> getItemCodeChunks(List<String> missingItemCodes, int chunkSize) {
        List<List<String>> itemCodeChunks = new ArrayList<>();
        for (int i = 0; i < missingItemCodes.size(); i += chunkSize) {
            itemCodeChunks.add(missingItemCodes.subList(i,
                Math.min(i + chunkSize, missingItemCodes.size())));
        }
        return itemCodeChunks;
    }

    private List<CsvData> parsePlan(List<MultipartFile> files) {
        List<CsvData> csvDataList = new ArrayList<>();
        for (MultipartFile file : files) {
            String name = file.getOriginalFilename();
            if (Objects.nonNull(name)) {
                if (name.contains("g-2000")) {
                    csvDataList.addAll(csvFileParser.g2000Parsing(file));
                } else if (name.contains("g-3000")) {
                    csvDataList.addAll(csvFileParser.g3000Parsing(file));
                }
            }
        }
        return csvDataList;
    }

    private List<Ttime> parseTTime(List<MultipartFile> files) {
        return csvFileParser.dFileParsing(files);
    }


    public List<CombineData> combineCSV(List<MultipartFile> gFile, List<MultipartFile> dFiles,
        LocalDate date) {

        // 1. CSV 파싱 및 g-file, d-file 조합
        CompletableFuture<List<CsvData>> combinedDataFuture =
            CompletableFuture.supplyAsync(() -> parsePlan(gFile))
                .thenCombine(CompletableFuture.supplyAsync(() -> parseTTime(dFiles)),
                    (plans, ttimes) -> {
                        Map<String, Ttime> ttimeMap = ttimes.stream()
                            .collect(Collectors.toMap(Ttime::itemCode, t -> t,
                                (existing, replacement) -> replacement));
                        plans.forEach(plan -> {
                            Ttime matchedTtime = ttimeMap.get(plan.getItemCode());
                            if (matchedTtime != null && plan.getCategory().equals("판매")) {
                                plan.setTtime(matchedTtime);
                            }
                        });
                        return plans;
                    });
        // 2. 조합된 데이터 기반으로 DB 재고 데이터를 한 번에 조회
        CompletableFuture<List<CombineData>> finalFuture = combinedDataFuture.thenCompose(
            csvCombineList -> {
                // 2-1. 중복 없는 itemCode 목록 추출
                List<String> uniqueItemCodes = getUniqueItemCodes(csvCombineList);
                refUniqueItemCodeSet.addAll(uniqueItemCodes);

                // 2-2. SQL 매개변수 제한을 피하기 위해 품번 리스트를 청크로 나눔 (예: 500개 단위)
                int chunkSize = 500;
                List<List<String>> itemCodeChunks = new ArrayList<>();
                for (int i = 0; i < uniqueItemCodes.size(); i += chunkSize) {
                    itemCodeChunks.add(uniqueItemCodes.subList(i,
                        Math.min(i + chunkSize, uniqueItemCodes.size())));
                }

                int targetChunkSize = 5;
                List<List<TargetLocationDto>> targetChunks = new ArrayList<>();
                List<TargetLocationDto> targetList = stockSearchService.erpAllTargetLocation();
                for (int i = 0; i < targetList.size(); i += targetChunkSize) {
                    targetChunks.add(
                        targetList.subList(i, Math.min(i + targetChunkSize, targetList.size())));
                }

                // 2-3. ERP 재고를 청크 조합별로 비동기 병렬 조회
                // 각 사이트, 위치 청크 개 수 당 품번 청크 조회
                // itemCodeChunks 총 2000개 (청크 단위 1000개) , targetChunks 총 15개 (청크 단위 5개)
                // targetChunks 총 3번 호출 되고 itemCodeChunk 1000개 씩 적용됨
                // 쿼리는 총 6번
                List<CompletableFuture<List<ERPStockRecord>>> erpFutures = new ArrayList<>();
                for (List<String> itemChunk : itemCodeChunks) {
                    for (List<TargetLocationDto> targetChunk : targetChunks) {
                        erpFutures.add(
                            CompletableFuture.supplyAsync(
                                () -> stockSearchService.getBulkErpStock(date, itemChunk,
                                    targetChunk))
                        );
                    }
                }

                // 2-4. 모든 ERP 비동기 작업이 완료되기를 기다리고, 결과를 하나의 리스트로 합침
                CompletableFuture<Map<String, ErpStockData>> allErpStocksFuture = CompletableFuture.allOf(
                        erpFutures.toArray(CompletableFuture[]::new))
                    .thenApply(v -> {
                        // 모든 청크의 결과를 하나의 스트림으로 합치고, 품번별로 그룹핑하여 ErpStockData 생성
                        List<ERPStockRecord> allRecords = erpFutures.stream()
                            .flatMap(future -> future.join().stream())
                            .toList();

                        return allRecords.stream()
                            .collect(Collectors.groupingBy(
                                ERPStockRecord::partNo,
                                Collectors.collectingAndThen(
                                    Collectors.toList(),
                                    records -> {
                                        // 이 records 리스트는 특정 partNo를 가진 모든 ERPStockRecord 객체들을 포함합니다.
                                        int inhouse = 0;
                                        int inspectionWait = 0;
                                        int container = 0;
                                        int longTerm = 0;
                                        int oem = 0;
                                        Set<ERPLocation> ctLocations = new HashSet<>();

                                        for (ERPStockRecord erpRow : records) {
                                            switch (erpRow.type()) {
                                                case "검사완료" -> inhouse += erpRow.getIntQuantity();
                                                case "ERP_검사대기" ->
                                                    inspectionWait += erpRow.getIntQuantity();
                                                case "국내입고대기" -> {
                                                    container += erpRow.getIntQuantity();
                                                    ctLocations.add(
                                                        ERPLocation.getLocationByFactoryAndLocation(
                                                            erpRow.siteCode(), erpRow.location()));
                                                }
                                                case "장기성재고" -> longTerm += erpRow.getIntQuantity();
                                                case "고객반송" -> oem += erpRow.getIntQuantity();
                                            }
                                        }
                                        return ErpStockData.of(inhouse, inspectionWait, container,
                                            ctLocations,
                                            longTerm, oem);
                                    }
                                )
                            ));
                    })
                    .exceptionally(ex -> {
                        log.error("Bulk ERP stock query failed: {}", ex.getMessage());
                        return new HashMap<>();
                    });

                // 2-6. 모든 비동기 작업이 완료되면 최종 데이터 조합
                return allErpStocksFuture.thenApply(
                    erpMap -> csvCombineList.stream()
                        .map(csvData -> {
                            String itemCode = csvData.getItemCode();
                            ErpStockData erpData = erpMap.getOrDefault(itemCode,
                                ErpStockData.allZero());
                            return CombineData.builder()
                                .csvData(csvData)
                                .erpStockData(erpData)
                                .build();
                        }).toList()
                );
            }
        );
        return finalFuture.join();
    }

    private List<String> getUniqueItemCodes(List<CsvData> csvCombineList) {
        return csvCombineList.stream()
            .map(CsvData::getItemCode)
            .distinct()
            .toList();
    }

    private CarOrder getCarOrder(CsvData csv, ErpStockData erp, DayPlusData dayplusData,
        LocalDate uploadDate) {
        return CarOrder.builder()
            .category(csv.getCategory())
            .alc(csv.getAlc())
            .itemCode(csv.getItemCode())
            .tTime(csv.getTtime() != null ? csv.getTtime().time() : null)
            .result(csv.getResult())
            .line(csv.getLine())
            .yra(csv.getYra())
            .inhouse(erp.inhouse())
            .inspection(erp.inspectionWait())
            .container(erp.container())
            .longTerm(erp.longTerm())
            .oem(erp.oem())
            .d(dayplusData.getDPlus0())
            .dPlus1(dayplusData.getDPlus1())
            .dPlus2(dayplusData.getDPlus2())
            .dPlus3(dayplusData.getDPlus3())
            .dPlus4(dayplusData.getDPlus4())
            .dPlus5(dayplusData.getDPlus5())
            .dPlus6(dayplusData.getDPlus6())
            .dPlus7(dayplusData.getDPlus7())
            .dPlus8(dayplusData.getDPlus8())
            .dPlus9(dayplusData.getDPlus9())
            .dPlus10(dayplusData.getDPlus10())
            .dPlus11(dayplusData.getDPlus11())
            .dPlus12(dayplusData.getDPlus12())
            .dPlus13(dayplusData.getDPlus13())
            .dPlus14(dayplusData.getDPlus14())
            .dPlus15(dayplusData.getDPlus15())
            .dPlus16(dayplusData.getDPlus16())
            .dPlus17(dayplusData.getDPlus17())
            .dPlus18(dayplusData.getDPlus18())
            .dPlus19(dayplusData.getDPlus19())
            .dPlus20(dayplusData.getDPlus20())
            .rem(dayplusData.getRem())
            .ctLocations(CarOrder.parseCtLocation(erp.containerLocation()))
            .uploadDate(uploadDate)
            .createdDate(LocalDateTime.now())
            .modifyDate(LocalDateTime.now())
            .build();
    }


}




