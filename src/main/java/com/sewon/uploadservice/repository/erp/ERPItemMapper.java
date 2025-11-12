package com.sewon.uploadservice.repository.erp;

import com.sewon.uploadservice.model.dto.car.sgn.CarGroupProps;
import com.sewon.uploadservice.model.dto.car.spn.CarPartNoTotalAgg;
import java.time.LocalDate;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ERPItemMapper {

    List<CarGroupProps> findGroupListByPartType(@Param("carProps") List<String> carProps);

    List<CarPartNoTotalAgg> findPartNoTotalLast4Weeks(@Param("stDate") LocalDate stDate);
}
