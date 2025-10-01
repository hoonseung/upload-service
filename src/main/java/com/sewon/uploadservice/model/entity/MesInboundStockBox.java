package com.sewon.uploadservice.model.entity;

import com.sewon.uploadservice.model.dto.mes.MESInboundStockBoxRecord;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MesInboundStockBox {

    private String itemCode;

    private Integer box1;
    private Integer box2;
    private Integer box3;
    private Integer box4;
    private Integer box5;
    private Integer box6;
    private Integer box7;
    private Integer box8;
    private Integer box9;
    private Integer box10;
    private Integer box11;
    private Integer box12;
    private Integer box13;
    private Integer box14;
    private Integer box15;
    private Integer box16;
    private Integer box17;
    private Integer box18;
    private Integer box19;
    private Integer box20;
    private Integer box21;
    private Integer box22;
    private Integer box23;
    private Integer box24;
    private Integer box25;
    private Integer box26;
    private Integer box27;
    private Integer box28;
    private Integer box29;
    private Integer box30;

    private LocalDateTime createdDate;

    private LocalDateTime modifyDate;


    public static MesInboundStockBox from(List<MESInboundStockBoxRecord> boxStockRecords) {
        String itemCode = boxStockRecords.get(0).itemCode();
        return new MesInboundStockBox(
            itemCode,
            boxStockRecords.remove(0).quantity(),
            !boxStockRecords.isEmpty() ? boxStockRecords.remove(0).quantity() : null,
            !boxStockRecords.isEmpty() ? boxStockRecords.remove(0).quantity() : null,
            !boxStockRecords.isEmpty() ? boxStockRecords.remove(0).quantity() : null,
            !boxStockRecords.isEmpty() ? boxStockRecords.remove(0).quantity() : null,
            !boxStockRecords.isEmpty() ? boxStockRecords.remove(0).quantity() : null,
            !boxStockRecords.isEmpty() ? boxStockRecords.remove(0).quantity() : null,
            !boxStockRecords.isEmpty() ? boxStockRecords.remove(0).quantity() : null,
            !boxStockRecords.isEmpty() ? boxStockRecords.remove(0).quantity() : null,
            !boxStockRecords.isEmpty() ? boxStockRecords.remove(0).quantity() : null,
            !boxStockRecords.isEmpty() ? boxStockRecords.remove(0).quantity() : null,
            !boxStockRecords.isEmpty() ? boxStockRecords.remove(0).quantity() : null,
            !boxStockRecords.isEmpty() ? boxStockRecords.remove(0).quantity() : null,
            !boxStockRecords.isEmpty() ? boxStockRecords.remove(0).quantity() : null,
            !boxStockRecords.isEmpty() ? boxStockRecords.remove(0).quantity() : null,
            !boxStockRecords.isEmpty() ? boxStockRecords.remove(0).quantity() : null,
            !boxStockRecords.isEmpty() ? boxStockRecords.remove(0).quantity() : null,
            !boxStockRecords.isEmpty() ? boxStockRecords.remove(0).quantity() : null,
            !boxStockRecords.isEmpty() ? boxStockRecords.remove(0).quantity() : null,
            !boxStockRecords.isEmpty() ? boxStockRecords.remove(0).quantity() : null,
            !boxStockRecords.isEmpty() ? boxStockRecords.remove(0).quantity() : null,
            !boxStockRecords.isEmpty() ? boxStockRecords.remove(0).quantity() : null,
            !boxStockRecords.isEmpty() ? boxStockRecords.remove(0).quantity() : null,
            !boxStockRecords.isEmpty() ? boxStockRecords.remove(0).quantity() : null,
            !boxStockRecords.isEmpty() ? boxStockRecords.remove(0).quantity() : null,
            !boxStockRecords.isEmpty() ? boxStockRecords.remove(0).quantity() : null,
            !boxStockRecords.isEmpty() ? boxStockRecords.remove(0).quantity() : null,
            !boxStockRecords.isEmpty() ? boxStockRecords.remove(0).quantity() : null,
            !boxStockRecords.isEmpty() ? boxStockRecords.remove(0).quantity() : null,
            !boxStockRecords.isEmpty() ? boxStockRecords.remove(0).quantity() : null,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }
}
