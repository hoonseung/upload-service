package com.sewon.uploadservice.model.dto.csv;

import java.time.LocalDate;

public record OperationPlan(
    LocalDate stDate,
    String carProps,
    String region,
    String nation,
    String doorType,
    String engineCapa,
    String transmission,
    String mc,
    Integer stMonth1,
    Integer stMonth2,
    Integer stMonth3,
    Integer stMonth4,
    Integer stMonth5,
    String etc,
    String customer,
    String responder,
    LocalDate uploadDate
) {


    public static OperationPlan of(LocalDate stDate, String carProps,
        String region, String nation, String doorType, String engineCapa, String transmission,
        String mc,
        Integer stMonth1,
        Integer stMonth2,
        Integer stMonth3,
        Integer stMonth4,
        Integer stMonth5,
        String etc,
        String customer,
        String responder,
        LocalDate uploadDate) {
        return new OperationPlan(
            stDate,
            carProps,
            region,
            nation,
            doorType,
            engineCapa,
            transmission,
            mc,
            stMonth1,
            stMonth2,
            stMonth3,
            stMonth4,
            stMonth5,
            etc,
            customer,
            responder,
            uploadDate
        );
    }
}
