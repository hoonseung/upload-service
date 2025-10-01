package com.sewon.uploadservice.model.dto.erp;

public record TargetLocationDto(
    String siteCode,
    String location
) {


    public static TargetLocationDto of (String siteCode, String location){
        return new TargetLocationDto(siteCode, location);
    }
}
