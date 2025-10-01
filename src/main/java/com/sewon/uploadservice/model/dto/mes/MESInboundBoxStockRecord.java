package com.sewon.uploadservice.model.dto.mes;

public record MESInboundBoxStockRecord(
    String status,
    String factory,
    String boxNo,
    String carCode,
    String itemName,
    String itemCode,
    String alc,
    Integer quantity
) {


    public static MESInboundBoxStockRecord of(String status, String factory, String boxNo,
        String carCode, String itemName, String itemCode, String alc, Integer quantity) {
        return new MESInboundBoxStockRecord(
            status,
            factory,
            boxNo,
            carCode,
            itemName,
            itemCode,
            alc,
            quantity
        );
    }
}
