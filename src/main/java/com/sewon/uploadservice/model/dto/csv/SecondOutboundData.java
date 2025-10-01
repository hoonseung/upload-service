package com.sewon.uploadservice.model.dto.csv;

import java.time.LocalDate;

public record SecondOutboundData(
    String itemCode,
    Integer quantity,
    LocalDate uploadDate
) {

    public static SecondOutboundData of (String itemCode, Integer quantity, LocalDate uploadDate){
        return new SecondOutboundData(itemCode, quantity, uploadDate);
    }
}
