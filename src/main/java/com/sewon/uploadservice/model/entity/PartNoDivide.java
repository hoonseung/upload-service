package com.sewon.uploadservice.model.entity;

import com.sewon.uploadservice.model.dto.csv.PartNoDivideData;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PartNoDivide {

    private String item;

    private String partNo;

    private String site;

    private String etc;

    private String fixed;

    private LocalDate stDate;

    private LocalDateTime createdDate;

    private LocalDateTime modifyDate;


    public static PartNoDivide from(PartNoDivideData partNoDivideData) {
        return new PartNoDivide(
            partNoDivideData.item(),
            partNoDivideData.partNo(),
            partNoDivideData.site(),
            partNoDivideData.etc(),
            partNoDivideData.fixed(),
            partNoDivideData.stDate(),
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }
}
