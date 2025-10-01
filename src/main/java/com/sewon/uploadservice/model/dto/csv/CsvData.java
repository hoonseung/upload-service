package com.sewon.uploadservice.model.dto.csv;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Builder
@Setter
@Getter
public class CsvData {

    private String category;

    private String itemName;

    private String alc;

    private String itemCode;

    private Ttime ttime;

    private Integer result;

    private Integer line;

    private Integer yra;

    private DayPlusData dayplusData;

}
