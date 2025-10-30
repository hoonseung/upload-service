package com.sewon.uploadservice.service;

import com.sewon.uploadservice.model.dto.erp.CarGroupProps;
import com.sewon.uploadservice.model.dto.erp.MonthProductAgg;
import com.sewon.uploadservice.model.entity.OperationPlanRawAggregation;
import com.sewon.uploadservice.repository.car.CarOrderMapper;
import com.sewon.uploadservice.repository.erp.ERPItemMapper;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OrderOperationService {

    private final ERPItemMapper erpItemMapper;
    private final CarOrderMapper carOrderMapper;

    @Transactional(transactionManager = "postgresqlTransactionManager")
    public void orderPlanRawOperation(LocalDate stDate) {
        List<String> uniquCarPropsList = carOrderMapper.findAllCarPropsByResponderAndStDate("세원",
            stDate);
        if (uniquCarPropsList.isEmpty()){
            return;
        }

        List<CarGroupProps> groupListByPartType = erpItemMapper.findGroupListByPartType(
            uniquCarPropsList);

        Map<String, MonthProductAgg> monthProducts = carOrderMapper.aggregationMonthProduction(
                stDate,
                uniquCarPropsList)
            .stream().collect(
                Collectors.toMap(
                    MonthProductAgg::carProps,
                    Function.identity(),
                    (existing, replacement) -> existing
                )
            );

        carOrderMapper.bulkInsertOperationPlanAgg(
            groupListByPartType.stream()
            .map(item -> OperationPlanRawAggregation.of(
                stDate,
                item,
                monthProducts.get(item.carProps())
            ))
            .toList()
        );
    }
}
