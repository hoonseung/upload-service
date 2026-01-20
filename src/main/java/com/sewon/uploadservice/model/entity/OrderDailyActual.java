package com.sewon.uploadservice.model.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDailyActual {

    private String planDate;

    private String partNo;

    private Integer actualQty;

    private LocalDateTime createdDate;

    public static OrderDailyActual of (String planDate, String partNo, Integer actualQty){
        return new OrderDailyActual(planDate, partNo, actualQty, LocalDateTime.now());
    }
}
