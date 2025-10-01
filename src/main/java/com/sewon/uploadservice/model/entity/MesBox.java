package com.sewon.uploadservice.model.entity;

import com.sewon.uploadservice.model.dto.mes.MesBoxData;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MesBox {

    private Long id;

    private String itemCode;

    private String box1;

    private String box2;

    private String box3;

    private String box4;

    private String box5;

    private Integer waitingQuantity;

    private Integer packingQuantity;

    private LocalDateTime createdDate;

    private LocalDateTime modifyDate;


    public static MesBox from(MesBoxData data) {
        return new MesBox(null, data.itemCode(),
            data.box1(), data.box2(), data.box3(), data.box4(), data.box5(),
            data.waitingQuantity(), data.packingQuantity(),
            LocalDateTime.now(), LocalDateTime.now()
        );
    }
}
