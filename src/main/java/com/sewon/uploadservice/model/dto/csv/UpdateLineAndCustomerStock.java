package com.sewon.uploadservice.model.dto.csv;

import java.time.LocalDate;

public record UpdateLineAndCustomerStock(
    LocalDate date,
    String itemCode,
    Integer lineStock,
    Integer customerStock
) {

    public static UpdateLineAndCustomerStock of(LocalDate date, String itemCode, Integer lineStock,
        Integer customerStock) {
        return new UpdateLineAndCustomerStock(date, itemCode, lineStock, customerStock);
    }
}
