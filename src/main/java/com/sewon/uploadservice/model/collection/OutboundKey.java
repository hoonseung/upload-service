package com.sewon.uploadservice.model.collection;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OutboundKey {

    ITEM_CODE("PartNo"),
    QUANTITY("Qty"),

    ;

    private final String header;
}
