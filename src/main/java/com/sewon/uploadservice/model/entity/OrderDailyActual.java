package com.sewon.uploadservice.model.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDailyActual {

    private String planDate;

    private String partNo;

    private Integer actualQty;

    private LocalDateTime createdDate;

    @Setter
    private String car;
    @Setter
    private String carItem;

    public static OrderDailyActual of (String planDate, String partNo, Integer actualQty){
        return new OrderDailyActual(planDate, partNo, actualQty, LocalDateTime.now(), null, null);
    }
}
