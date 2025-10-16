package com.sewon.uploadservice.model.entity;

import com.sewon.uploadservice.model.dto.csv.OutboundTargetData;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OutboundTarget {

    private String itemCode;
    private Integer emQuantity;
    private Integer firstQuantity;
    private Integer secondQuantity;
    private Integer dPlus3Quantity;
    private LocalDate uploadDate;

    private LocalDateTime createdDate;

    private LocalDateTime modifyDate;


    public static OutboundTarget from(OutboundTargetData data) {
        return new OutboundTarget(
            data.itemCode(),
            data.emQuantity(),
            data.firstQuantity(),
            data.secondQuantity(),
            data.dPlus3Quantity(),
            data.uploadDate(),
            LocalDateTime.now(), LocalDateTime.now());
    }
}
