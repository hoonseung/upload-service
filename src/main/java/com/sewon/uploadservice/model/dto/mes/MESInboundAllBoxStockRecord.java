package com.sewon.uploadservice.model.dto.mes;



public record MESInboundAllBoxStockRecord(
    String factory,
    String boxNo,
    String status,
    String carCode,
    String itemName,
    String itemCode,
    Integer quantity
) {


    public static MESInboundAllBoxStockRecord of(String factory, String boxNo,
        String status, String carCode, String itemName, String itemCode, Integer quantity) {
        return new MESInboundAllBoxStockRecord(
            factory,
            boxNo,
            status,
            carCode,
            itemName,
            itemCode,
            quantity
        );
    }
}
