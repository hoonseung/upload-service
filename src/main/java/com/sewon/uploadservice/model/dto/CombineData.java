package com.sewon.uploadservice.model.dto;


import com.sewon.uploadservice.model.dto.csv.CsvData;
import com.sewon.uploadservice.model.dto.erp.ErpStockData;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class CombineData{
    private CsvData csvData;
    private ErpStockData erpStockData;
}
