package com.sewon.uploadservice.model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PurchaseOutsourcingCost {

    private final String priceList;

    private final String name;

    private final String priceUnit;

    private final String productCode;

    private final String partNo;

    private final String groups;

    private final String groups2;

    private final String unit;

    private final LocalDate startDate;

    private final LocalDate endDate;

    private final String etc;

    private final String explain;

    private final BigDecimal outSourcingCost;

    private final LocalDate modifyDate;

    private final String factory;

    private final String carPropsType;

    private final String stdUnit;

    private final LocalDate createdDate;




    public static PurchaseOutsourcingCost of (String priceList,
                                         String name,
                                         String priceUnit,
                                         String productCode,
                                         String partNo,
                                         String groups,
                                         String groups2,
                                         String unit,
                                         LocalDate startDate,
                                         LocalDate endDate,
                                         String etc,
                                         String explain,
                                         BigDecimal outSourcingCost,
                                         LocalDate modifyDate,
                                         String factory,
                                         String carPropsType,
                                         String stdUnit){
        return new PurchaseOutsourcingCost(
            priceList,
            name,
            priceUnit,
            productCode,
            partNo,
            groups,
            groups2,
            unit,
            startDate,
            endDate,
            etc,
            explain,
            outSourcingCost,
            modifyDate,
            factory,
            carPropsType,
            stdUnit,
            LocalDate.now()
        );
    }
}
