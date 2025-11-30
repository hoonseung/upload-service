package com.sewon.uploadservice.model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class StdOutsourcingCost {

    private final String partNo;

    private final String priceSet;

    private final String productCode;

    private final BigDecimal matCost;

    private final BigDecimal laborCost
        ;
    private final BigDecimal changingCost;

    private final BigDecimal fixedCost;

    private final BigDecimal outSourcingCost;

    private final BigDecimal totalCost;

    private final BigDecimal matCostDown;

    private final BigDecimal laborCostDown;

    private final BigDecimal changingCostDown;

    private final BigDecimal fixedCostDown;

    private final BigDecimal outSourcingCostDown;

    private final LocalDate costModifyDate;

    private final String groups;

    private final String groups2;

    private final String carPropsType;

    private final String carProps;

    private final String freezing;

    private final LocalDate createdDate;

    public static StdOutsourcingCost of(String partNo,
                                        String priceSet,
                                        String productCode,
                                        BigDecimal matCost,
                                        BigDecimal laborCost,
                                        BigDecimal changingCost,
                                        BigDecimal fixedCost,
                                        BigDecimal outSourcingCost,
                                        BigDecimal totalCost,
                                        BigDecimal matCostDown,
                                        BigDecimal laborCostDown,
                                        BigDecimal changingCostDown,
                                        BigDecimal fixedCostDown,
                                        BigDecimal outSourcingCostDown,
                                        LocalDate costModifyDate,
                                        String groups,
                                        String groups2,
                                        String carPropsType,
                                        String carProps,
                                        String freezing) {
        return new StdOutsourcingCost(
            partNo,
            priceSet,
            productCode,
            matCost,
            laborCost,
            changingCost,
            fixedCost,
            outSourcingCost,
            totalCost,
            matCostDown,
            laborCostDown,
            changingCostDown,
            fixedCostDown,
            outSourcingCostDown,
            costModifyDate,
            groups,
            groups2,
            carPropsType,
            carProps,
            freezing,
            LocalDate.now()
        );
    }
}
