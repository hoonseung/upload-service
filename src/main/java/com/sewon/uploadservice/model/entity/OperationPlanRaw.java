package com.sewon.uploadservice.model.entity;

import com.sewon.uploadservice.model.dto.csv.OperationPlan;
import com.sewon.uploadservice.model.dto.mes.MesBoxData;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OperationPlanRaw {

    private Long id;

    private LocalDate stDate;

    private String carProps;

    private String region;

    private String nation;

    private String doorType;

    private String engineCapa;

    private String transmission;

    private String mc;

    private Integer stMonth1;

    private Integer stMonth2;

    private Integer stMonth3;

    private Integer stMonth4;

    private Integer stMonth5;

    private String etc;

    private String customer;

    private String responder;

    private LocalDate uploadDate;

    private LocalDateTime createdDate;

    private LocalDateTime modifyDate;


    public static OperationPlanRaw from(OperationPlan plan) {
        return new OperationPlanRaw(
            null,
            plan.stDate(),
            plan.carProps(),
            plan.region(),
            plan.nation(),
            plan.doorType(),
            plan.engineCapa(),
            plan.transmission(),
            plan.mc(),
            plan.stMonth1(),
            plan.stMonth2(),
            plan.stMonth3(),
            plan.stMonth4(),
            plan.stMonth5(),
            plan.etc(),
            plan.customer(),
            plan.responder(),
            plan.uploadDate(),
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }

}
