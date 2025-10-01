package com.sewon.uploadservice.model.dto.mes;

import java.time.LocalDate;

public record MESOutboundStockRecord(
    String itemCode,
    Integer outQuantity,
    LocalDate outboundDate
) {

}
