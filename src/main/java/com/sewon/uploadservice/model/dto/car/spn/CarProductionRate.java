package com.sewon.uploadservice.model.dto.car.spn;


public record CarProductionRate(
    String partNo,
    String carItem,
    String doorType,
    String region,
    String responder,
    Integer dPlusTotal,
    Integer rate
) {


    public static CarProductionRate of(String partNo, String carItem,
        String doorType, String region, String responder, Integer dPlusTotal, Integer rate) {
        return new CarProductionRate(
            partNo, carItem, doorType, region, responder, dPlusTotal, rate);
    }
}
