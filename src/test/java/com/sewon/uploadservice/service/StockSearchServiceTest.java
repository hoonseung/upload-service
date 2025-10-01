package com.sewon.uploadservice.service;

import static org.assertj.core.api.Assertions.assertThat;


import com.sewon.uploadservice.model.dto.erp.ERPStockRecord;
import com.sewon.uploadservice.model.dto.mes.MESInboundAllBoxStockRecord;
import com.sewon.uploadservice.model.dto.mes.MESInboundBoxStockRecord;
import com.sewon.uploadservice.model.dto.mes.MesBoxData;
import com.sewon.uploadservice.model.dto.erp.TargetLocationDto;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class StockSearchServiceTest {

    @Autowired
    private StockSearchService stockSearchService;

    @DisplayName("완제품 조회 단건, 실행 시 암호화 비밀 키 환경 변수 주입 필요")
    @Test
    void testFindStockSummary() {
        LocalDate date = LocalDate.of(2025, 9, 17);
        String site = "10";
        String location = "6000";
        String partNo = "91000-L1930";
        String category = "1000";

        Optional<ERPStockRecord> result =
            stockSearchService.findStockSummary(date, site, location, partNo, category);

        assertThat(result).isNotNull().isPresent();  // AssertJ 사용
        result.ifPresent(r -> {
            System.out.println("조회 결과 = " + r);
        });
    }

    @DisplayName("완제품 조회, 실행 시 암호화 비밀 키 환경 변수 주입 필요")
    @Test
    void testFindStockSummary_검사완료재고_조회() {
        LocalDate date = LocalDate.of(2025, 9, 18);
        List<String> partNo = List.of("91000-L1930", "91001-L1090", "91001-L1110");

        List<ERPStockRecord> result =
            stockSearchService.검사완료재고_조회(date, partNo);

        assertThat(result).isNotNull();  // AssertJ 사용

        System.out.println("조회 결과 = " + result);
    }

    @DisplayName("ERP_검사대기재고 조회, 실행 시 암호화 비밀 키 환경 변수 주입 필요")
    @Test
    void testFindStockSummary_ERP_검사대기재고_조회() {
        LocalDate date = LocalDate.of(2025, 9, 22);
        List<String> partNo = List.of("91400-L1510");

        List<ERPStockRecord> result =
            stockSearchService.ERP_검사대기재고_조회(date, partNo);


        System.out.println("조회 결과 = " + result);
    }


    @DisplayName("국내입고대기재고 조회, 실행 시 암호화 비밀 키 환경 변수 주입 필요")
    @Test
    void testFindStockSummary_국내입고대기재고_조회() {
        LocalDate date = LocalDate.of(2025, 9, 22);
        List<String> partNo = List.of("91400-L1510");

        List<ERPStockRecord> result =
            stockSearchService.국내입고대기재고_조회(date, partNo);


        System.out.println("조회 결과 = " + result);
    }


    @DisplayName("장기성재고 조회, 실행 시 암호화 비밀 키 환경 변수 주입 필요")
    @Test
    void testFindStockSummary_장기성재고_조회() {
        LocalDate date = LocalDate.of(2025, 9, 18);
        List<String> partNo = List.of("91102-DF830");

        List<ERPStockRecord> result =
            stockSearchService.장기성재고_조회(date, partNo);


        System.out.println("조회 결과 = " + result);
    }

    @DisplayName("완제품 조회 여러 건(이걸로 단건 대체됨), 실행 시 암호화 비밀 키 환경 변수 주입 필요")
    @Test
    void testFindStockSummaryByTargets() {
        LocalDate date = LocalDate.of(2025, 9, 15);
        List<TargetLocationDto> targetList = List.of(
            TargetLocationDto.of("12", "5100"),
            TargetLocationDto.of("14", "5100"),
            TargetLocationDto.of("15", "5100"),
            TargetLocationDto.of("10", "5200"));
        List<String> partNo = List.of("91102-DF830");
//        List<TargetLocationDto> targetList = List.of(
//            TargetLocationDto.of("10", "6000"),
//            TargetLocationDto.of("10", "6010")
//        );
//        String partNo = "91100-BEAM1";
        String category = "1000";

        List<ERPStockRecord> result =
            stockSearchService.findStockSummaryByTargets(date, targetList, partNo, category);

        assertThat(result).isNotNull().isNotEmpty();// AssertJ 사용

        System.out.println("조회 결과 = " + result);
    }

    @DisplayName("ERP 데이터 벌크 조회")
    @Test
    void testGetBulkErpStock() {
        LocalDate date = LocalDate.of(2025, 9, 18);
        List<String> partNo = List.of("91000-L1930", "91001-L1090", "91001-L1110");

        List<ERPStockRecord> result =
            stockSearchService.getBulkErpStock(date, partNo);

        assertThat(result).isNotNull().isNotEmpty();// AssertJ 사용

        result.forEach(data ->
        System.out.println("조회 결과 = " + data));
    }

    @DisplayName("MES 입고 대기 상태인 재고 조회")
    @Test
    void testFindInboundStockSummaryByTargets() {
        String itemCode = "91215-O6090";
        List<MESInboundBoxStockRecord> result = stockSearchService.findInboundStockSummaryByTargets(
            itemCode);

        assertThat(result).isNotNull();

        result.forEach(item -> System.out.println("조회 결과 = " + item));
    }

//    @Disabled
//    @DisplayName("MES_검사대기재고_조회")
//    @Test
//    void testFindInboundStockSummaryByTargets_MES_검사대기재고_조회() {
//        String itemCode = "91000-L1930";
//        MesBoxData result = stockSearchService.MES_검사대기재고_조회(
//            itemCode);
//
//        assertThat(result).isNotNull();
//
//        System.out.println("조회 결과 = " + result);
//    }

//    @DisplayName("MES 입고 대기 상태인 재고 조회 (벌크 조회)")
//    @Test
//    void testGetBulkMESStock() {
//        List<String> itemCodes = List.of("91230-GX171", "91230-GX531", "91530-GX190");
//        Map<String, MesBoxData> result = stockSearchService.getBulkMESWaitingStock(itemCodes);
//
//        assertThat(result).isNotNull();
//        System.out.println("조회 결과 = " + result);
//    }


    @Test
    void getMesBoxDataMapTest(){
        List<MESInboundAllBoxStockRecord> stockRecords = List.of(
            MESInboundAllBoxStockRecord.of(
                "F1", "F1Y9120029", "WAITING", "AX1E", "AX1 FRONT RHD", "91230-GX371", 1),
            MESInboundAllBoxStockRecord.of(
                "F1", "F1Y9120029", "WAITING", "AX1E", "AX1 FRONT RHD", "91230-GX371", 1),
            MESInboundAllBoxStockRecord.of(
                "F1", "F1Y9120029", "WAITING", "AX1E", "AX1 FRONT RHD", "91230-GX371", 1),
            MESInboundAllBoxStockRecord.of(
                "F1", "F1Y9120029", "WAITING", "AX1E", "AX1 FRONT RHD", "91230-GX371", 1),
            MESInboundAllBoxStockRecord.of(
                "F1", "F1Y9120029", "WAITING", "AX1E", "AX1 FRONT RHD", "91230-GX371", 1),
            MESInboundAllBoxStockRecord.of(
                "F1", "F1Y9120029", "WAITING", "AX1E", "AX1 FRONT RHD", "91230-GX371", 1),
            MESInboundAllBoxStockRecord.of(
                "F1", "F1Y9120029", "WAITING", "AX1E", "AX1 FRONT RHD", "91230-GX371", 1),
            MESInboundAllBoxStockRecord.of(
                "F1", "F1Y9120029", "WAITING", "AX1E", "AX1 FRONT RHD", "91230-GX371", 1),
            MESInboundAllBoxStockRecord.of(
                "F1", "F3Y9120049", "WAITING","SK3", "SK3 MAIN", "91166-K0090", 1),
            MESInboundAllBoxStockRecord.of(
                "F1", "F3Y9120049", "WAITING","SK3", "SK3 MAIN", "91166-K0090", 1),
            MESInboundAllBoxStockRecord.of(
                "F1", "F3Y9120049", "WAITING","SK3", "SK3 MAIN", "91166-K0090", 1),
            MESInboundAllBoxStockRecord.of(
                "F1", "F3Y9120049", "WAITING","SK3", "SK3 MAIN", "91166-K0090", 1),
            MESInboundAllBoxStockRecord.of(
                "E1", "E1Y9190027", "WAITING","AX1", "AX1 FLOOR", "91550-O6700", 1),
            MESInboundAllBoxStockRecord.of(
                "E1", "E1Y9190027", "WAITING","AX1", "AX1 FLOOR", "91550-O6700", 1),
            MESInboundAllBoxStockRecord.of(
                "E1", "E1Y9190051", "WAITING","AX1", "AX1 FLOOR", "91550-O6700", 1),

            MESInboundAllBoxStockRecord.of(
                "B1", "ZDY9230054", "PACKING","AM", "AM BUMPER-EXTN", "91890-2K070", 1),
            MESInboundAllBoxStockRecord.of(
                "B1", "ZDY9230054", "PACKING","AM", "AM BUMPER-EXTN", "91890-2K070", 1),
            MESInboundAllBoxStockRecord.of(
                "B1", "ZDY9230054", "PACKING","AM", "AM BUMPER-EXTN", "91890-2K070", 1),
            MESInboundAllBoxStockRecord.of(
                "B1", "ZDY9230054", "PACKING","AM", "AM BUMPER-EXTN", "91890-2K070", 1),
            MESInboundAllBoxStockRecord.of(
                "B1", "ZDY9230054", "PACKING","AM", "AM BUMPER-EXTN", "91890-2K070", 1)
            );

        Map<String, MesBoxData> result = stockSearchService.getMesBoxDataMap(stockRecords);
        //List<String> result = stockSearchService.getWaitingGroupingBoxCount(stockRecords);
        Assertions.assertThat(result).isNotNull();
        System.out.println("result : " + result);

        /*
      result : {
      91550-O6700=MesBoxData[itemCode=91550-O6700, box1=E1Y9190027-2, box2=E1Y9190051-1, box3=null, box4=null, box5=null, waitingQuantity=3, packingQuantity=0],
      91890-2K070=MesBoxData[itemCode=91890-2K070, box1=null, box2=null, box3=null, box4=null, box5=null, waitingQuantity=0, packingQuantity=5],
      91166-K0090=MesBoxData[itemCode=91166-K0090, box1=F3Y9120049-4, box2=null, box3=null, box4=null, box5=null, waitingQuantity=4, packingQuantity=0],
      91230-GX371=MesBoxData[itemCode=91230-GX371, box1=F1Y9120029-8, box2=null, box3=null, box4=null, box5=null, waitingQuantity=8, packingQuantity=0]}

         */
    }
}