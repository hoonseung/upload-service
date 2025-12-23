package com.sewon.uploadservice.model.dto.mes;

public record UniqueFactoryItemCode(
    String factory,
    String itemCode
) {


    public static UniqueFactoryItemCode of (String factory, String itemCode) {
        return new UniqueFactoryItemCode(factory, itemCode);
    }
}
