package com.sewon.uploadservice.model.entity;


import com.sewon.uploadservice.model.dto.car.spn.CarPartNoTotalAgg;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OperationLastMonthlyPlanAggregation {

    private Long id;

    private LocalDate stDate;

    private LocalDate fromDate;

    private LocalDate toDate;

    private String carItem;

    private String partNo;

    private String doorType;

    private String region;

    private Integer dPlusTotal;

    private String responder;

    private LocalDateTime createdDate;

    private LocalDateTime modifyDate;


    public static OperationLastMonthlyPlanAggregation of(LocalDate stDate, CarPartNoTotalAgg agg) {
        LocalDate lastMonth = stDate.minusMonths(1);
        return new OperationLastMonthlyPlanAggregation(null, stDate,
            lastMonth.withDayOfMonth(1),  lastMonth.withDayOfMonth(lastMonth.lengthOfMonth()),
            agg.carItem(), agg.partNo(), agg.doorType(), agg.region(), agg.dPlusTotal(), agg.responder(),
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }

    public static OperationLastMonthlyPlanAggregation of(LocalDate stDate, CarPartNoTotalAgg agg,
        LocalDate startDate, LocalDate endDate
        ) {
        return new OperationLastMonthlyPlanAggregation(null, stDate,
            startDate, endDate,
            agg.carItem(), agg.partNo(), agg.doorType(), agg.region(), agg.dPlusTotal(), agg.responder(),
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }
}
