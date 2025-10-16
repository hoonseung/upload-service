package com.sewon.uploadservice.model.dto.csv;

import java.time.LocalDate;

public record OutboundTargetData(
    String itemCode,
    Integer emQuantity,
    Integer firstQuantity,
    Integer secondQuantity,
    Integer dPlus3Quantity,
    LocalDate uploadDate
) {

    public static OutboundTargetData of (String itemCode, Integer emQuantity,
        Integer firstQuantity, Integer secondQuantity, Integer dPlus3Quantity,
        LocalDate uploadDate){
        return new OutboundTargetData(
            itemCode,
            emQuantity,
            firstQuantity,
            secondQuantity,
            dPlus3Quantity,
            uploadDate);
    }
}
