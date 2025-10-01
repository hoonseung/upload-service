package com.sewon.uploadservice.repository.erp;

import com.sewon.uploadservice.model.dto.erp.ERPStockRecord;
import com.sewon.uploadservice.model.dto.erp.TargetLocationDto;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ERPStockMapper {

    Optional<ERPStockRecord> findStockSummary
        (@Param("date") LocalDate date,
            @Param("site") String site,
            @Param("location") String location,
            @Param("partNo") String partNo,
            @Param("category") String category);

    List<ERPStockRecord> findStockSummaryByTargets1
        (@Param("date") LocalDate date,
            @Param("targetList") List<TargetLocationDto> targetLocationDtoList,
            @Param("partNo") String partNo,
            @Param("category") String category);


    List<ERPStockRecord> findStockSummaryByTargets
        (@Param("date") LocalDate date,
            @Param("targetList") List<TargetLocationDto> targetLocationDtoList,
            @Param("partNoList") List<String> targetPartNoList,
            @Param("category") String category);
}
