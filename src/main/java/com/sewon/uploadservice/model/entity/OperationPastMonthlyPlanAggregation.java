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
public class OperationPastMonthlyPlanAggregation {

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


    public static OperationPastMonthlyPlanAggregation of(LocalDate stDate, CarPartNoTotalAgg agg) {
        LocalDate pastMonth = stDate.minusMonths(5);
        return new OperationPastMonthlyPlanAggregation(null, stDate,
            pastMonth.withDayOfMonth(1),  stDate,
            agg.carItem(), agg.partNo(), agg.doorType(), agg.region(), agg.dPlusTotal(), agg.responder(),
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }
}
