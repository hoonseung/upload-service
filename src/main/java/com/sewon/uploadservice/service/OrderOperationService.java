package com.sewon.uploadservice.service;

import com.sewon.uploadservice.model.collection.CarSpec;
import com.sewon.uploadservice.model.dto.erp.CarGroupProps;
import com.sewon.uploadservice.model.dto.erp.CarPropsCombineSpec;
import com.sewon.uploadservice.model.dto.erp.CarPropsGroupSpecCombineSpec;
import com.sewon.uploadservice.model.dto.erp.MonthProductAgg;
import com.sewon.uploadservice.model.entity.OperationPlanRawAggregation;
import com.sewon.uploadservice.repository.car.CarOrderMapper;
import com.sewon.uploadservice.repository.erp.ERPItemMapper;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Service
public class OrderOperationService {

    private final ERPItemMapper erpItemMapper;
    private final CarOrderMapper carOrderMapper;

    @Transactional(transactionManager = "postgresqlTransactionManager")
    public void orderPlanRawOperation(LocalDate stDate) {
        List<CarPropsCombineSpec> carPropsCombineSpecList = carOrderMapper.findAllCarPropsByResponderAndStDate("세원",
            stDate);
        if (carPropsCombineSpecList.isEmpty()){
            return;
        }

        // '정상'과 '비정상' 데이터로 분할
        Map<Boolean, List<CarPropsCombineSpec>> partitionedSpecs = carPropsCombineSpecList.stream()
            .collect(Collectors.partitioningBy(item -> item.etc().isBlank()));

        // 사양이 없는 것들
        List<CarPropsCombineSpec> normalSpecs = partitionedSpecs.get(true);
        // 사양이 있는 것들
        List<CarPropsCombineSpec> unNormalSpecs = partitionedSpecs.get(false);

        // 각 분류에 따라 처리 메서드 호출
        if (!normalSpecs.isEmpty()) {
            processNormalSpecs(stDate, normalSpecs);
        }
        if (!unNormalSpecs.isEmpty()) {
            processUnNormalSpecs(stDate, unNormalSpecs);
        }
    }

    /**
     * '정상' 스펙(etc가 비어있음) 데이터를 처리하고 저장합니다.
     */
    private void processNormalSpecs(LocalDate stDate, List<CarPropsCombineSpec> normalSpecs) {
        List<String> normalCarPropsList = normalSpecs.stream()
            .map(CarPropsCombineSpec::carProps)
            .toList();

        List<CarGroupProps> normalGroupProps = erpItemMapper.findGroupListByPartType(
            normalCarPropsList);

        Map<String, MonthProductAgg> normalMap = carOrderMapper.aggregationMonthProductionNormal(
                stDate, normalCarPropsList)
            .stream().collect(
                Collectors.toMap(
                    MonthProductAgg::carProps,
                    Function.identity(),
                    (existing, replacement) -> existing
                )
            );

        carOrderMapper.bulkInsertOperationPlanAgg(
            normalGroupProps.stream()
                .filter(item -> Objects.nonNull(normalMap.get(item.carProps())))
                .map(item -> OperationPlanRawAggregation.of(
                    stDate, item.carProps(), getGroupProps(item.shouldSplitGroupProps()),
                    normalMap.get(item.carProps())
                ))
                .toList()
        );
    }


    private void processUnNormalSpecs(LocalDate stDate, List<CarPropsCombineSpec> unNormalSpecs) {
        List<String> unNormalCarPropsList = unNormalSpecs.stream()
            .map(CarPropsCombineSpec::carProps)
            .toList();

        List<CarGroupProps> unNormalGroupProps = erpItemMapper.findGroupListByPartType(
            unNormalCarPropsList);

        // '공통'(스펙 3개)과 '개별'(스펙 1~2개)로 다시 분할
        Map<Boolean, List<CarGroupProps>> partitionedGroups = unNormalGroupProps.stream()
            .collect(Collectors.partitioningBy(item -> CarSpec.getSpecSize(item) == 3));

        List<CarGroupProps> commonGroups = partitionedGroups.get(true);
        List<CarGroupProps> specificGroups = partitionedGroups.get(false);

        if (!commonGroups.isEmpty()) {
            processCommonUnNormalGroups(stDate, commonGroups);
        }
        if (!specificGroups.isEmpty()) {
            processSpecificUnNormalGroups(stDate, specificGroups);
        }
    }


    private void processCommonUnNormalGroups(LocalDate stDate, List<CarGroupProps> commonGroups) {
        List<String> extractCommonCarProps = commonGroups.stream()
            .map(CarGroupProps::carProps)
            .distinct()
            .toList();

        // 스펙 3개인 집계 결과 분류
        Map<String, MonthProductAgg> commonNormalMap = carOrderMapper.aggregationMonthProductionNormal(
                stDate, extractCommonCarProps)
            .stream().collect(
                Collectors.toMap(
                    MonthProductAgg::carProps,
                    Function.identity(),
                    (existing, replacement) -> existing
                )
            );

        carOrderMapper.bulkInsertOperationPlanAgg(
            commonGroups.stream()
                .filter(item -> Objects.nonNull(commonNormalMap.get(item.carProps())))
                .map(item -> OperationPlanRawAggregation.of(
                    stDate, item.carProps(), getGroupProps(item.shouldSplitGroupProps()),
                    commonNormalMap.get(item.carProps()), "공통"
                ))
                .toList()
        );
    }


    private void processSpecificUnNormalGroups(LocalDate stDate, List<CarGroupProps> specificGroups) {
        List<CarPropsGroupSpecCombineSpec> combineSpecs = specificGroups.stream()
            .map(CarPropsGroupSpecCombineSpec::from)
            .flatMap(Collection::stream)
            .toList();

        // 스펙 1~2인 집계 결과 분류
        Map<String, MonthProductAgg> collectMap = carOrderMapper.aggregationMonthProduction(stDate,
                combineSpecs)
            .stream().collect(Collectors.toMap(
                item -> item.carProps() + item.etc(),
                Function.identity(),
                (existing, replacement) -> existing
             ));

        // 그룹 + etc 키 / 그룹 밸류 설정
        Map<String, String> findGroupProps = combineSpecs.stream().collect(Collectors.toMap(
            item -> item.carProps() + item.groupProps() + item.etc(),
            CarPropsGroupSpecCombineSpec::groupProps,
            (existing, replacement) -> existing // 중복 키가 있을 경우 기존 값 유지
        ));

        // 집계결과, 그룹 밸류 가져와서 최종 집계 결과 생성
        List<OperationPlanRawAggregation> result = new ArrayList<>();
        for (CarPropsGroupSpecCombineSpec spec : combineSpecs){
            String key1 = spec.carProps() + spec.etc();
            String key2 = spec.carProps() + spec.groupProps() + spec.etc();
            MonthProductAgg monthProductAgg = collectMap.get(key1);
            String groupProps = findGroupProps.get(key2);
            if (Objects.isNull(monthProductAgg) || (groupProps != null && groupProps.isBlank()) ){
                continue;
            }
            OperationPlanRawAggregation aggregation = OperationPlanRawAggregation.of(
                stDate, spec.carProps(), groupProps,
                monthProductAgg);
            result.add(aggregation);
        }
        carOrderMapper.bulkInsertOperationPlanAgg(result);
    }


    private String getGroupProps(String shouldParsing) {
        if (shouldParsing == null) {
            return ""; // 혹은 예외 처리
        }
        int spaceIndex = shouldParsing.indexOf(" ");
        if (spaceIndex == -1 || spaceIndex + 1 >= shouldParsing.length()) {
            return shouldParsing; // " "가 없거나 마지막 문자면 원본 반환 (혹은 예외)
        }
        return shouldParsing.substring(spaceIndex + 1); // sw group 여기서 group만 가져오기
    }
}
