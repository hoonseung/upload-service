package com.sewon.uploadservice.model.dto.car.spn;

public record CarPartNoTotalAgg(
    String carItem,
    String partNo,
    String doorType,
    String region,
    String responder,
    String confirm,
    Integer dPlusTotal
) {

}
