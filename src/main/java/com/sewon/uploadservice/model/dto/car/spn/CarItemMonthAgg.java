package com.sewon.uploadservice.model.dto.car.spn;

public record CarItemMonthAgg(
    String carItem,

    String doorType,

    String region,

    Integer month1Agg,

    Integer month2Agg,

    Integer month3Agg,

    Integer month4Agg,

    Integer month5Agg
) {

}
