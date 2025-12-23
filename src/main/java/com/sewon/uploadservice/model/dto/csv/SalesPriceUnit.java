package com.sewon.uploadservice.model.dto.csv;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SalesPriceUnit(
    String partNo,
    String supplier,
    String partName,
    String carItem,
    String productCode,
    String explain,
    String carProps,
    String groups,
    String unit,
    String priceUnit,
    BigDecimal price,
    LocalDate toDate,
    LocalDate fromDate
) {

    public static SalesPriceUnit of(
        String partNo,
        String supplier,
        String partName,
        String carItem,
        String productCode,
        String explain,
        String carProps,
        String groups,
        String unit,
        String priceUnit,
        BigDecimal price,
        LocalDate toDate,
        LocalDate fromDate
    ) {
        return new SalesPriceUnit(
            partNo,
            supplier,
            partName,
            carItem,
            productCode,
            explain,
            carProps,
            groups,
            unit,
            priceUnit,
            price,
            toDate,
            fromDate
        );
    }
}
