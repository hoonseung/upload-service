package com.sewon.uploadservice.model.entity;

import com.sewon.uploadservice.model.dto.mes.MESInboundStockRecord;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MesInboundStock {

    private Long id;

    private String factory;

    private String itemCode;

    private Integer quantity;

    private LocalDateTime createdDate;

    private LocalDateTime modifyDate;


    public static MesInboundStock from(MESInboundStockRecord stockRecord) {
        return new MesInboundStock(null, stockRecord.factory(), stockRecord.itemCode(),
            stockRecord.quantity() == null ? 0 : stockRecord.quantity(),
            LocalDateTime.now(), LocalDateTime.now()
        );
    }

}
