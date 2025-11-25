package com.sewon.uploadservice.service;

import com.sewon.uploadservice.model.collection.CarSpec;
import com.sewon.uploadservice.model.dto.car.sgn.CarGroupProps;
import com.sewon.uploadservice.model.dto.car.sgn.CarPropsCombineSpec;
import com.sewon.uploadservice.model.dto.car.sgn.CarPropsGroupSpecCombineSpec;
import com.sewon.uploadservice.model.dto.car.sgn.MonthProductAgg;
import com.sewon.uploadservice.model.dto.car.spn.CarItemMonthAgg;
import com.sewon.uploadservice.model.dto.car.spn.CarPartNoTotalAgg;
import com.sewon.uploadservice.model.dto.car.spn.CarProductionRate;
import com.sewon.uploadservice.model.entity.OperationLastMonthlyPlanAggregation;
import com.sewon.uploadservice.model.entity.OperationPlanProductionRate;
import com.sewon.uploadservice.model.entity.OperationPlanRawAggregation;
import com.sewon.uploadservice.repository.car.CarOrderMapper;
import com.sewon.uploadservice.repository.erp.ERPItemMapper;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
        List<CarPropsCombineSpec> carPropsCombineSpecList = carOrderMapper.findAllCarPropsByResponderAndStDate("세원",
            stDate);
        if (carPropsCombineSpecList.isEmpty()){
            return;
        }

        // '정상'과 '비정상' 데이터로 분할
        Map<Boolean, List<CarPropsCombineSpec>> partitionedSpecs = carPropsCombineSpecList.stream()
            .collect(Collectors.partitioningBy(item ->!CarSpec.isSWsPec(item.etc())));

        // SW1 사양이 아닌 것들
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

        List<OperationLastMonthlyPlanAggregation> aggregations = erpItemMapper.findPartNoTotalLast4Weeks(
                stDate, null, null, null)
            .stream()
            .map(agg -> OperationLastMonthlyPlanAggregation.of(stDate, agg))
            .toList();
        carOrderMapper.bulkInsertOperationLastMonthlyPlanAgg(
            aggregations
        );
    }

    @Transactional(transactionManager = "postgresqlTransactionManager")
    public void updateOperationPlanLastMonthAgg(){
        LocalDate recentlyStDate = carOrderMapper.findRecentlyStDate();
        LocalDate today = LocalDate.now();
        carOrderMapper.deleteOpsMonthlyPlanAgg();

        List<OperationLastMonthlyPlanAggregation> aggregations = erpItemMapper.findPartNoTotalLast4Weeks(
            null, null, null,
                today)
            .stream()
            .map(agg -> OperationLastMonthlyPlanAggregation.of(recentlyStDate, agg,
                today.minusMonths(1), today))
            .toList();
        carOrderMapper.bulkInsertOperationLastMonthlyPlanAgg(
            aggregations
        );
    }

    @Transactional(transactionManager = "postgresqlTransactionManager")
    public void updateOperationPlanLastMonthAggByPeriod(LocalDate startDate, LocalDate endDate){
        LocalDate recentlyStDate = carOrderMapper.findRecentlyStDate();
        carOrderMapper.deleteOpsMonthlyPlanAgg();

        List<OperationLastMonthlyPlanAggregation> aggregations = erpItemMapper.findPartNoTotalLast4Weeks(
            null,
                startDate, endDate, null)
            .stream()
            .map(agg -> OperationLastMonthlyPlanAggregation.of(recentlyStDate, agg,
                startDate, endDate))
            .toList();
        carOrderMapper.bulkInsertOperationLastMonthlyPlanAgg(
            aggregations
        );
    }

    /**
     * 품번 처리 섹션
     * - 최근 4주 기준 품번 합계에서 상위 20% 품번 추출
     * - 그룹 내 비중을 계산하여 품번별 생산 비율 산정
     * - 산출된 비율로 OperationPlanProductionRate를 생성·저장
     */
    @Transactional
    public void partitioningPartNoByOrderPlanRawOperation(LocalDate stDate){
        // 차종 품명 별 달 간 생산 해야 하는 양
        List<CarItemMonthAgg> monthlyAggByCarItem = carOrderMapper.findMonthlyAggByCarItem();

        // 한달 전 4주 서열에서 품번 합계 추출
        List<CarPartNoTotalAgg> partNoTotalLast4Weeks = erpItemMapper.findPartNoTotalLast4Weeks(stDate,
            null, null, null);

        if (monthlyAggByCarItem == null || monthlyAggByCarItem.isEmpty()) {
            return;
        }
        if (partNoTotalLast4Weeks == null || partNoTotalLast4Weeks.isEmpty()) {
            return;
        }

        // 차종을 그룹핑하여 상위 20퍼 아이템만 가져오기
        Map<String, List<CarPartNoTotalAgg>> topItems = getCarPartNoTotalAggsTop20p(partNoTotalLast4Weeks);

        // 상위 20퍼 아이템 중에서도 비율 나누기
        List<CarProductionRate> topRateItems = getCarPartNoTotalAggsRate(topItems);

        // 품명, 도어, 지역을 매칭하여 쌍 만들기
        List<Entry<CarItemMonthAgg, CarProductionRate>> pairList = monthlyAggByCarItem.stream()
            .flatMap(agg -> topRateItems.stream()
                .filter(rate ->
                    Objects.equals(agg.carItem(), rate.carItem()) &&
                        Objects.equals(rate.doorType(), agg.doorType()) &&
                        Objects.equals(rate.region(), agg.region())
                ).map(rate -> Map.entry(agg, rate))
            ).toList();


        // 최종 결과

         List<OperationPlanProductionRate> productionRates = pairList.stream()
            .map(pair -> {
                    CarItemMonthAgg agg = pair.getKey();
                    CarProductionRate rate = pair.getValue();

                    double percent = rate.rate() / 100.0;

                    int rateByMonth1 = (int) Math.round(agg.month1Agg() * percent);
                    int rateByMonth2 = (int) Math.round(agg.month2Agg() * percent);
                    int rateByMonth3 = (int) Math.round(agg.month3Agg() * percent);
                    int rateByMonth4 = (int) Math.round(agg.month4Agg() * percent);
                    int rateByMonth5 = (int) Math.round(agg.month5Agg() * percent);

                    return OperationPlanProductionRate.of(stDate, agg.carItem(), agg.doorType(), agg.region(),
                        rate.partNo(), rate.rate(), rate.responder(),
                        rateByMonth1,
                        rateByMonth2,
                        rateByMonth3,
                        rateByMonth4,
                        rateByMonth5);
                }
            ).toList();

        carOrderMapper.bulkInsertOperationPlanProductionRate(productionRates);
    }

    private List<CarProductionRate> getCarPartNoTotalAggsRate(Map<String,List<CarPartNoTotalAgg>> topItems) {
        return topItems
            .entrySet()
            .stream()
            .flatMap(entry -> {
                List<CarPartNoTotalAgg> groupList = entry.getValue();
                long groupSum = groupList.stream()
                    .mapToLong(CarPartNoTotalAgg::dPlusTotal)
                    .sum();

                if (groupSum <= 0) {
                    return Stream.empty();
                }

                return groupList.stream()
                    .map(agg -> {
                        int rate = (int)
                            Math.round(
                                (agg.dPlusTotal() / (double) groupSum) * 100);
                        return CarProductionRate.of(agg.partNo(), agg.carItem(),
                            agg.doorType(), agg.region(), agg.responder(), agg.dPlusTotal(), rate);
                    });
            }).toList();
    }


    private Map<String, List<CarPartNoTotalAgg>> getCarPartNoTotalAggsTop20p(
        List<CarPartNoTotalAgg> partNoTotalLast4Weeks) {
        return partNoTotalLast4Weeks.stream()
            .collect(Collectors.groupingBy(CarPartNoTotalAgg::carItem,
                Collectors.collectingAndThen(Collectors.toList(),
                    groupList -> {
                        long groupSum = groupList.stream()
                            .mapToLong(CarPartNoTotalAgg::dPlusTotal)
                            .sum();

                        if (groupSum == 0) {
                            // 합계가 0인 그룹은 빈 리스트를 반환합니다.
                            return Collections.emptyList();
                        }

                        int cutoffValue = (int) Math.round(groupSum * 0.2);
                        return groupList.stream()
                            .filter(agg -> agg.dPlusTotal() >= cutoffValue)
                            .toList();
                    }
                    )));
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
                .distinct()
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
                .distinct()
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
            item -> item.seqNo() + item.carProps() + item.groupProps() + item.etc(),
            CarPropsGroupSpecCombineSpec::groupProps,
            (existing, replacement) -> existing // 중복 키가 있을 경우 기존 값 유지
        ));

        // 집계결과, 그룹 밸류 가져와서 최종 집계 결과 생성
        List<OperationPlanRawAggregation> result = new ArrayList<>();
        for (CarPropsGroupSpecCombineSpec spec : combineSpecs){
            String key1 = spec.carProps() + spec.etc();
            String key2 = spec.seqNo() + spec.carProps() + spec.groupProps() + spec.etc();
            MonthProductAgg monthProductAgg = collectMap.get(key1);
            String groupProps = findGroupProps.get(key2);
            if (Objects.isNull(monthProductAgg) || (groupProps.isBlank()) ){
                continue;
            }
            OperationPlanRawAggregation aggregation = OperationPlanRawAggregation.of(
                stDate, spec.carProps(), groupProps,
                monthProductAgg);
            result.add(aggregation);
        }
        carOrderMapper.bulkInsertOperationPlanAgg(result.stream().distinct().toList());
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
