package com.sewon.uploadservice.model.dto.erp;


import com.sewon.uploadservice.model.collection.ERPLocation;

import java.util.Set;

public record ErpStockData(

    Integer inhouse,        // 사내 (검사완료)
    Integer inspectionWait,   // 검사대기
    Integer container,     // CT
    Set<ERPLocation> containerLocation,
    Integer longTerm,      // 장기
    Integer oem            // OEM
) {

    public static ErpStockData of(Integer inhouse, Integer inspectionWait,
        Integer container, Set<ERPLocation> containerLocation,
        Integer longTerm, Integer oem) {
        return new ErpStockData(inhouse, inspectionWait, container, containerLocation, longTerm, oem);
    }

    public static ErpStockData allZero() {
        return new ErpStockData(0, 0, 0, Set.of(), 0, 0);
    }
}
