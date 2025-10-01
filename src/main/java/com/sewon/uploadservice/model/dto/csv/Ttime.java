package com.sewon.uploadservice.model.dto.csv;

public record Ttime(
    String itemCode,
    String time
) {


    public static Ttime of (String itemCode, String time){
        return new Ttime(itemCode, time);
    }
}
