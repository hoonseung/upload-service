package com.sewon.uploadservice.model.entity;

import com.sewon.uploadservice.model.dto.mes.MESOutboundStockRecord;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MesOutboundStock {

    private Long id;

    private String itemCode;

    private Integer outQuantity;

    private LocalDate outboundDate;

    private LocalDateTime createdDate;

    private LocalDateTime modifyDate;


    public static MesOutboundStock from(MESOutboundStockRecord outboundStock) {
        return new MesOutboundStock(null, outboundStock.itemCode(), outboundStock.outQuantity(),
            outboundStock.outboundDate(),
            LocalDateTime.now(), LocalDateTime.now()
        );
    }
}
