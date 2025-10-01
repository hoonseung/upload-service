package com.sewon.uploadservice.model.dto.mes;

public record MESInboundStockBoxRecord(
    String boxNo,
    String itemCode,
    Integer quantity
) {


    public static MESInboundStockBoxRecord of(String boxNo, String itemCode, Integer quantity) {
        return new MESInboundStockBoxRecord(
            boxNo,
            itemCode,
            quantity
        );
    }
}
