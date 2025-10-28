package com.sewon.uploadservice.repository.erp;

import com.sewon.uploadservice.model.dto.erp.CarGroupProps;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ERPItemMapper {

    List<CarGroupProps> findGroupListByPartType(@Param("carProps") List<String> carProps);
}
