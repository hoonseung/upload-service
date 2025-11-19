package com.sewon.uploadservice.model.entity;


import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OperationPlanProductionRate {

    private Long id;

    private LocalDate stDate;

    private String carItem;

    private String doorType;

    private String region;

    private String partNo;

    private Integer rate;

    private String responder;

    private Integer rateByMonth1;

    private Integer rateByMonth2;

    private Integer rateByMonth3;

    private Integer rateByMonth4;

    private Integer rateByMonth5;

    private LocalDateTime createdDate;

    private LocalDateTime modifyDate;


    public static OperationPlanProductionRate of(LocalDate stDate, String carItem, String doorType, String region,
        String partNo, Integer rate, String responder, Integer rateByMonth1, Integer rateByMonth2,
        Integer rateByMonth3, Integer rateByMonth4, Integer rateByMonth5) {
        return new OperationPlanProductionRate(null, stDate,
            carItem, doorType, region, partNo, rate, responder,
            rateByMonth1, rateByMonth2, rateByMonth3, rateByMonth4, rateByMonth5,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }
}
