package com.sewon.uploadservice.service;

import static com.sewon.uploadservice.model.collection.ERPLocation.DEZHOU_IMPORT_WAITING;
import static com.sewon.uploadservice.model.collection.ERPLocation.GYEONGSAN_COMPLETION;
import static com.sewon.uploadservice.model.collection.ERPLocation.GYEONGSAN_DEZHOU_INSPECTION_WAITING;
import static com.sewon.uploadservice.model.collection.ERPLocation.GYEONGSAN_VINA_INSPECTION_WAITING;
import static com.sewon.uploadservice.model.collection.ERPLocation.GYEONGSAN_WEIHAI_INSPECTION_WAITING;
import static com.sewon.uploadservice.model.collection.ERPLocation.OEM_20;
import static com.sewon.uploadservice.model.collection.ERPLocation.OEM_30;
import static com.sewon.uploadservice.model.collection.ERPLocation.PYEONGTAEK_BOND;
import static com.sewon.uploadservice.model.collection.ERPLocation.PYEONGTAEK_COMPLETION;
import static com.sewon.uploadservice.model.collection.ERPLocation.PYEONGTAEK_DEZHOU_INSPECTION_WAITING;
import static com.sewon.uploadservice.model.collection.ERPLocation.PYEONGTAEK_LONG_TERM_STOCK;
import static com.sewon.uploadservice.model.collection.ERPLocation.PYEONGTAEK_VINA_INSPECTION_WAITING;
import static com.sewon.uploadservice.model.collection.ERPLocation.PYEONGTAEK_WEIHAI_INSPECTION_WAITING;
import static com.sewon.uploadservice.model.collection.ERPLocation.VINA_IMPORT_WAITING;
import static com.sewon.uploadservice.model.collection.ERPLocation.WEIHAI_IMPORT_WAITING;

import com.sewon.uploadservice.model.dto.erp.ERPStockRecord;
import com.sewon.uploadservice.model.dto.mes.MESInboundAllBoxStockRecord;
import com.sewon.uploadservice.model.dto.mes.MESInboundStockBoxRecord;
import com.sewon.uploadservice.model.dto.mes.MESOutboundStockRecord;
import com.sewon.uploadservice.model.dto.mes.MesBoxData;
import com.sewon.uploadservice.model.entity.MesInboundStock;
import com.sewon.uploadservice.model.entity.MesInboundStockBox;
import com.sewon.uploadservice.repository.erp.ERPStockMapper;
import com.sewon.uploadservice.repository.mes.MESStockMapper;
import com.sewon.uploadservice.model.dto.erp.TargetLocationDto;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StockSearchService {

    private final ERPStockMapper eRPStockMapper;
    private final MESStockMapper mesStockMapper;

    private static final String WAITING = "WAITING";
    private static final String PACKING = "PACKING";
    private static final String FACTORY = "ZA";
    private static final String CATEGORY = "1000";


    // 해당 품번 기말 재고 조회
    // 사내, 검사, CT, 장기성, 반송
    public List<ERPStockRecord> findStockSummaryByTargets(LocalDate date,
        List<TargetLocationDto> targetList, List<String> partNoList, String category) {
        return eRPStockMapper.findStockSummaryByTargets(date, targetList, partNoList, category);
    }

    public List<ERPStockRecord> getBulkErpStock(LocalDate date, List<String> partNoList) {
        return findStockSummaryByTargets(date, erpAllTargetLocation(), partNoList, CATEGORY);
    }

    public List<ERPStockRecord> getBulkErpStock(LocalDate date, List<String> partNoList,
        List<TargetLocationDto> targetList) {
        return findStockSummaryByTargets(date, targetList, partNoList, CATEGORY);
    }

//    public Map<String, MesBoxData> getBulkMESWaitingStock(List<String> itemCodes) {
//        return getMesBoxDataMap(mesStockMapper.findInboundWaitingStockSummaryByTargetsBulk("ZA", itemCodes));
//    }
//
//    public Map<String, MesBoxData> getBulkMESPackingStock(List<String> itemCodes) {
//        return getMesBoxDataMap(mesStockMapper.findInboundPackingStockSummaryByTargetsBulk("ZA", itemCodes));
//    }

    public Map<String, MesBoxData> getBulkMESAllBox(List<String> itemCodes) {
        List<MESInboundAllBoxStockRecord> inboundAllStockSummaryByTargetsBulk = mesStockMapper.findInboundAllBoxSummaryByTargetsBulk(
            FACTORY, itemCodes);
        return getMesBoxDataMap(inboundAllStockSummaryByTargetsBulk);
    }

    public List<MESOutboundStockRecord> getMESOutboundStock(LocalDate from, LocalDate to){
        return mesStockMapper.findOutboundStockTotalByDate(FACTORY, from, to);
    }

    public Map<String, MesBoxData> getMesBoxDataMap(List<MESInboundAllBoxStockRecord> stockRecords) {
        return stockRecords.stream()
            .collect(Collectors.groupingBy(MESInboundAllBoxStockRecord::itemCode,
                Collectors.collectingAndThen(Collectors.toList(),
                    this::getWaitingBoxAndQuantity)
            ));
    }

    private MesBoxData getWaitingBoxAndQuantity(List<MESInboundAllBoxStockRecord> boxStockRecord) {
        int totalWaitingQty = boxStockRecord.stream()
            .filter(it -> it.status().equals(WAITING))
            .mapToInt(MESInboundAllBoxStockRecord::quantity).sum();
        int totalPackingQty = boxStockRecord.stream()
            .filter(it -> it.status().equals(PACKING))
            .mapToInt(MESInboundAllBoxStockRecord::quantity).sum();
        List<String> waitingGroupingBoxCount = getWaitingGroupingBoxCount(boxStockRecord);
        return MesBoxData.of(
            boxStockRecord.get(0).itemCode(),
            !waitingGroupingBoxCount.isEmpty() ? waitingGroupingBoxCount.remove(0) : null,
            !waitingGroupingBoxCount.isEmpty() ? waitingGroupingBoxCount.remove(0) : null,
            !waitingGroupingBoxCount.isEmpty() ? waitingGroupingBoxCount.remove(0) : null,
            !waitingGroupingBoxCount.isEmpty() ? waitingGroupingBoxCount.remove(0) : null,
            !waitingGroupingBoxCount.isEmpty() ? waitingGroupingBoxCount.remove(0) : null,
            totalWaitingQty,
            totalPackingQty
        );
    }

    public List<String> getWaitingGroupingBoxCount(List<MESInboundAllBoxStockRecord> groupingRecord){
        return groupingRecord.stream()
            .collect(Collectors.groupingBy(it -> it.boxNo() + "-" + it.status(),
                Collectors.collectingAndThen(Collectors.toList(),
                    data ->
                            data.stream()
                                .filter(it -> it.status().equals(WAITING))
                                .mapToInt(MESInboundAllBoxStockRecord::quantity).sum()
                    )))
            .entrySet().stream()
            .filter(entry -> entry.getKey().endsWith(WAITING))
            .map(entry -> {
                if (entry.getValue().equals(0)){
                    return null;
                }else {
                    return entry.getKey().split("-")[0] + "-" + entry.getValue();
                }
            })
            .collect(Collectors.toList());
    }

    public List<MesInboundStock> getBulkMESStock(List<String> itemCode) {
        return mesStockMapper.findInboundStockSummaryByTargetsBulk(FACTORY, itemCode)
            .stream().map(MesInboundStock::from).toList();
    }

    public List<MesInboundStock> getBulkMESStockUpdateOnly(List<String> itemCode) {
        return mesStockMapper.findInboundStockSummaryByTargetsBulkUpdateOnly(FACTORY, itemCode)
            .stream().map(MesInboundStock::from).toList();
    }


    public List<MesInboundStockBox> getBulkMESStockBox(List<String> itemCode){
        return mesStockMapper.findInboundStockBoxSummaryByTargetsBulk(FACTORY, itemCode)
            .stream()
            .collect(Collectors.groupingBy(MESInboundStockBoxRecord::itemCode, Collectors.collectingAndThen(
                Collectors.toList(), this::getMesInboundStockBox))
                ).values().stream().toList();
    }

    public MesInboundStockBox getMesInboundStockBox(List<MESInboundStockBoxRecord> boxStockRecords){
        return MesInboundStockBox.from(boxStockRecords);
    }


    // 사내
    public List<ERPStockRecord> 검사완료재고_조회(LocalDate date, List<String> partNoList) {
        return findStockSummaryByTargets(date, getInspectionCompleteLocation(), partNoList, CATEGORY)
            .stream().collect(Collectors.groupingBy(ERPStockRecord::partNo,
                Collectors.summingInt(data -> data.quantity().intValue())))
            .entrySet().stream()
            .map(entry -> ERPStockRecord.groupingPartNo(entry.getKey(), entry.getValue()))
            .toList();
    }

    // 검사
    public List<ERPStockRecord> ERP_검사대기재고_조회(LocalDate date, List<String> partNoList) {
        return findStockSummaryByTargets(date, getInspectionWaitingLocation(), partNoList, CATEGORY)
            .stream().collect(Collectors.groupingBy(ERPStockRecord::partNo,
                Collectors.summingInt(data -> data.quantity().intValue())))
            .entrySet().stream()
            .map(entry -> ERPStockRecord.groupingPartNo(entry.getKey(), entry.getValue()))
            .toList();
    }

    // CT
    public List<ERPStockRecord> 국내입고대기재고_조회(LocalDate date, List<String> partNoList) {
        return findStockSummaryByTargets(date, getImportWaitingLocation(), partNoList, CATEGORY)
            .stream().collect(Collectors.groupingBy(ERPStockRecord::partNo,
                Collectors.summingInt(data -> data.quantity().intValue())))
            .entrySet().stream()
            .map(entry -> ERPStockRecord.groupingPartNo(entry.getKey(), entry.getValue()))
            .toList();
    }

    // 장기성재고
    public List<ERPStockRecord> 장기성재고_조회(LocalDate date, List<String> partNoList) {
        return findStockSummaryByTargets(date, getLongTermLocation(), partNoList, CATEGORY).stream()
            .toList();
    }

    // OEM
    public List<ERPStockRecord> 고객반송재고_조회(LocalDate date, List<String> partNoList) {
        return findStockSummaryByTargets(date, getOEMLocation(), partNoList, CATEGORY)
            .stream().collect(Collectors.groupingBy(ERPStockRecord::partNo,
                Collectors.summingInt(data -> data.quantity().intValue())))
            .entrySet().stream()
            .map(entry -> ERPStockRecord.groupingPartNo(entry.getKey(), entry.getValue()))
            .toList();
    }

//    // MES 제품 검사 대기 제품 조회
//    public MesBoxData MES_검사대기재고_조회(String partNo) {
//        List<MESInboundBoxStockRecord> records = findInboundStockSummaryByTargets(
//            partNo);
//        String status = "WAITING";
//        return MesBoxData.of(partNo,
//            !records.isEmpty() ? getBoxAndQuantity(records.remove(0)) : null,
//            !records.isEmpty() ? getBoxAndQuantity(records.remove(0)) : null,
//            !records.isEmpty() ? getBoxAndQuantity(records.remove(0)) : null,
//            !records.isEmpty() ? getBoxAndQuantity(records.remove(0)) : null,
//            !records.isEmpty() ? getBoxAndQuantity(records.remove(0)) : null,
//            status
//        );
//    }




    private List<TargetLocationDto> getInspectionCompleteLocation() {
        return List.of(
            TargetLocationDto.of(
                PYEONGTAEK_COMPLETION.getFactory(),
                PYEONGTAEK_COMPLETION.getLocation()),
            TargetLocationDto.of(
                GYEONGSAN_COMPLETION.getFactory(),
                GYEONGSAN_COMPLETION.getLocation())
        );
    }

    private List<TargetLocationDto> getInspectionWaitingLocation() {
        return List.of(
            TargetLocationDto.of(
                PYEONGTAEK_WEIHAI_INSPECTION_WAITING.getFactory(),
                PYEONGTAEK_WEIHAI_INSPECTION_WAITING.getLocation()),
            TargetLocationDto.of(
                PYEONGTAEK_DEZHOU_INSPECTION_WAITING.getFactory(),
                PYEONGTAEK_DEZHOU_INSPECTION_WAITING.getLocation()),
            TargetLocationDto.of(
                PYEONGTAEK_VINA_INSPECTION_WAITING.getFactory(),
                PYEONGTAEK_VINA_INSPECTION_WAITING.getLocation()),
            TargetLocationDto.of(
                GYEONGSAN_WEIHAI_INSPECTION_WAITING.getFactory(),
                GYEONGSAN_WEIHAI_INSPECTION_WAITING.getLocation()),
            TargetLocationDto.of(
                GYEONGSAN_DEZHOU_INSPECTION_WAITING.getFactory(),
                GYEONGSAN_DEZHOU_INSPECTION_WAITING.getLocation()),
            TargetLocationDto.of(
                GYEONGSAN_VINA_INSPECTION_WAITING.getFactory(),
                GYEONGSAN_VINA_INSPECTION_WAITING.getLocation())
        );
    }

    private List<TargetLocationDto> getImportWaitingLocation() {
        return List.of(
            TargetLocationDto.of(
                PYEONGTAEK_BOND.getFactory(),
                PYEONGTAEK_BOND.getLocation()),
            TargetLocationDto.of(
                WEIHAI_IMPORT_WAITING.getFactory(),
                WEIHAI_IMPORT_WAITING.getLocation()),
            TargetLocationDto.of(
                DEZHOU_IMPORT_WAITING.getFactory(),
                DEZHOU_IMPORT_WAITING.getLocation()),
            TargetLocationDto.of(
                VINA_IMPORT_WAITING.getFactory(),
                VINA_IMPORT_WAITING.getLocation())
        );
    }

    private List<TargetLocationDto> getOEMLocation() {
        return List.of(
            TargetLocationDto.of(
                OEM_20.getFactory(),
                OEM_20.getLocation()),
            TargetLocationDto.of(
                OEM_30.getFactory(),
                OEM_30.getLocation())
        );
    }

    private List<TargetLocationDto> getLongTermLocation() {
        return List.of(
            TargetLocationDto.of(
                PYEONGTAEK_LONG_TERM_STOCK.getFactory(),
                PYEONGTAEK_LONG_TERM_STOCK.getLocation()
            )
        );
    }


    public List<TargetLocationDto> erpAllTargetLocation() {
        List<TargetLocationDto> locations = new ArrayList<>(getInspectionCompleteLocation());
        locations.addAll(getInspectionWaitingLocation());
        locations.addAll((getImportWaitingLocation()));
        locations.addAll(getOEMLocation());
        locations.addAll(getLongTermLocation());
        return locations;
    }


}
