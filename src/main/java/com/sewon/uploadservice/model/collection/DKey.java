package com.sewon.uploadservice.model.collection;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum DKey {

    ITEM_CODE("자재코드"),

    T_TIME_FIRST("D일01T"),
    T_TIME_LAST("D+04일 TOTAL")
    ;


    private final String header;
}
