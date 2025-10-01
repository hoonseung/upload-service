package com.sewon.uploadservice.model.dto.erp;

import java.math.BigDecimal;
import java.time.LocalDate;


public record ERPStockRecord(

    String siteCode,

    String location,

    String category,

    String partNo,

    BigDecimal quantity,

    LocalDate date,

    String type
) {

    public static ERPStockRecord groupingPartNo(String partNo, Integer sum){
        return new ERPStockRecord(null, null, null,
            partNo, BigDecimal.valueOf(sum), null, null);
    }

    public static ERPStockRecord empty(String partNo){
        return new ERPStockRecord(null, null, null,
            partNo, BigDecimal.ZERO, null, null);
    }


    public int getIntQuantity(){
        return quantity.intValue();
    }
}
