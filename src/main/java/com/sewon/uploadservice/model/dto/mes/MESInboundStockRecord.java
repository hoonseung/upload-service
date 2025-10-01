package com.sewon.uploadservice.model.dto.mes;

public record MESInboundStockRecord(
    String factory,
    String location,
    String itemCode,
    Integer quantity
) {


    public static MESInboundStockRecord of(String factory, String location, String itemCode, Integer quantity) {
        return new MESInboundStockRecord(
            factory,
            location,
            itemCode,
            quantity
        );
    }
}
