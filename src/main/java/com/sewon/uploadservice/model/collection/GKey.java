package com.sewon.uploadservice.model.collection;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum GKey {

    ALC("ALC"),
    ITEM_CODE("자재"),
    CATEGORY("구분"),

    CONSUMER_STOCK("고객재고"),
    DOMESTIC_STOCK("국내재고"),
    PREVIOUS_DAY_RESULT("전일실적"),

    DAY("D"),

    UPDATE_ITEM_CODE("ild_part"),
    UPDATE_CONSUMER_STOCK("ild_qty_oem_line"),
    UPDATE_DOMESTIC_STOCK("ild_qty_oem_cust"),

    ;

    private final String header;



}
