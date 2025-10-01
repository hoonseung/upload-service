package com.sewon.uploadservice.model.entity;

import com.sewon.uploadservice.model.dto.csv.SecondOutboundData;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SecondOutbound {

    String itemCode;
    Integer quantity;
    LocalDate uploadDate;

    private LocalDateTime createdDate;

    private LocalDateTime modifyDate;


    public static SecondOutbound from(SecondOutboundData data) {
        return new SecondOutbound(data.itemCode(), data.quantity(), data.uploadDate(),
            LocalDateTime.now(), LocalDateTime.now());
    }
}
