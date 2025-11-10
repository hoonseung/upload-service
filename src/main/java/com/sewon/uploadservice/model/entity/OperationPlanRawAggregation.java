package com.sewon.uploadservice.model.entity;

import com.sewon.uploadservice.model.dto.car.MonthProductAgg;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OperationPlanRawAggregation {

    private Long id;

    private LocalDate stDate;

    private String carProps;

    private String groupProps;

    private Integer stMonth1Agg;

    private Integer stMonth2Agg;

    private Integer stMonth3Agg;

    private Integer stMonth4Agg;

    private Integer stMonth5Agg;

    private String etc;

    private LocalDateTime createdDate;

    private LocalDateTime modifyDate;


    public static OperationPlanRawAggregation of(LocalDate stDate, String carProps, String groupProps, MonthProductAgg products, String etc) {
        if (products == null) return null;
        return new OperationPlanRawAggregation(
            null,
            stDate,
            carProps,
            groupProps,
            products.month1Agg(),
            products.month2Agg(),
            products.month3Agg(),
            products.month4Agg(),
            products.month5Agg(),
            etc,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }

    public static OperationPlanRawAggregation of(LocalDate stDate, String carProps, String groupProps, MonthProductAgg products) {
        if (products == null) return null;
        return new OperationPlanRawAggregation(
            null,
            stDate,
            carProps,
            groupProps,
            products.month1Agg(),
            products.month2Agg(),
            products.month3Agg(),
            products.month4Agg(),
            products.month5Agg(),
            products.etc(),
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }



}
