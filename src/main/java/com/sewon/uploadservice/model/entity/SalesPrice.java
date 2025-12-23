package com.sewon.uploadservice.model.entity;

import com.sewon.uploadservice.model.dto.csv.SalesPriceUnit;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SalesPrice{

    private String partNo;

    private String supplier;

    private String partName;

    private  String carItem;

    private String productCode;

    private String explain;

    private String carProps;

    private String groupProps;

    private String unit;

    private String priceUnit;

    private BigDecimal price;

    private LocalDate toDate;

    private LocalDate fromDate;

    private LocalDateTime createdDate;



    public static SalesPrice from(SalesPriceUnit priceUnit){
        return new SalesPrice(
            priceUnit.partNo(),
            priceUnit.supplier(),
            priceUnit.partName(),
            priceUnit.carItem(),
            priceUnit.productCode(),
            priceUnit.explain(),
            priceUnit.carProps(),
            priceUnit.groups(),
            priceUnit.unit(),
            priceUnit.priceUnit(),
            priceUnit.price(),
            priceUnit.toDate(),
            priceUnit.fromDate(),
            LocalDateTime.now()
        );
    }
}
