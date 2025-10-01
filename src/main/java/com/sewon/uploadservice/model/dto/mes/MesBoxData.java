package com.sewon.uploadservice.model.dto.mes;

public record MesBoxData(

    String itemCode,

    String box1,

    String box2,

    String box3,

    String box4,

    String box5,

    Integer waitingQuantity,

    Integer packingQuantity
) {


    public static MesBoxData of (String itemName, String box1, String box2, String box3, String box4,
        String box5, Integer waitingQuantity, Integer packingQuantity){
        return new MesBoxData(itemName, box1, box2, box3, box4, box5, waitingQuantity, packingQuantity);
    }
}
