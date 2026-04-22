package com.sewon.uploadservice.model.dto.car;

public record CarProps(
    String partNo,
    String partNoRev,
    String car,
    String carItem
) {


    public static CarProps of(String partNo, String partNoRev, String car, String carItem) {
        return new CarProps(partNo, partNoRev, car, carItem);
    }
}
