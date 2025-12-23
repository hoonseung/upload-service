package com.sewon.uploadservice.repository.car;

import com.sewon.uploadservice.model.dto.car.spn.CarItemMonthAgg;
import com.sewon.uploadservice.model.dto.csv.UpdateLineAndCustomerStock;
import com.sewon.uploadservice.model.dto.car.sgn.CarPropsCombineSpec;
import com.sewon.uploadservice.model.dto.car.sgn.CarPropsGroupSpecCombineSpec;
import com.sewon.uploadservice.model.dto.car.sgn.MonthProductAgg;
import com.sewon.uploadservice.model.entity.CarOrder;
import com.sewon.uploadservice.model.entity.SapOrderPlan;
import com.sewon.uploadservice.model.entity.OperationLastMonthlyPlanAggregation;
import com.sewon.uploadservice.model.entity.MesBox;
import com.sewon.uploadservice.model.entity.MesInboundStockBox;
import com.sewon.uploadservice.model.entity.MesInboundStock;
import com.sewon.uploadservice.model.dto.mes.MesBoxData;
import com.sewon.uploadservice.model.entity.MesOutboundStock;
import com.sewon.uploadservice.model.entity.OperationPastMonthlyPlanAggregation;
import com.sewon.uploadservice.model.entity.OperationPlanProductionRate;
import com.sewon.uploadservice.model.entity.OperationPlanRaw;
import com.sewon.uploadservice.model.entity.OperationPlanRawAggregation;
import com.sewon.uploadservice.model.entity.OutboundTarget;
import com.sewon.uploadservice.model.entity.PurchaseOutsourcingCost;
import com.sewon.uploadservice.model.entity.SalesPrice;
import com.sewon.uploadservice.model.entity.StdOutsourcingCost;
import java.time.LocalDate;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CarOrderMapper {


    void insertCarOrder(CarOrder carOrder);

    void bulkInsertCarOrder(@Param("carOrders") List<CarOrder> carOrders);

    void bulkInsertMesBox(@Param("mesBoxes") List<MesBox> mesBoxes);

    void bulkInsertInboundMesStock(@Param("mesStocks") List<MesInboundStock> mesStocks);

    void bulkInsertInboundMesStockBox(@Param("mesStockBoxes") List<MesInboundStockBox> mesStocks);

    void bulkInsertOutboundMesStock(@Param("mesStocks") List<MesOutboundStock> mesStocks);

    void bulkInsertOutboundTarget(@Param("outbounds") List<OutboundTarget> outbounds);

    void bulkInsertOperationPlanRaw(@Param("operationPlanRaws") List<OperationPlanRaw> operationPlanRaws);

    void bulkInsertOperationPlanAgg(@Param("operationPlanAgg") List<OperationPlanRawAggregation> operationPlanAgg);

    void bulkInsertOperationLastMonthlyPlanAgg(@Param("operationPlanMAgg") List<OperationLastMonthlyPlanAggregation> operationPlanMAgg);

    void bulkInsertOperationPastMonthlyPlanAgg(@Param("operationPlanMAgg") List<OperationPastMonthlyPlanAggregation> operationPlanMAgg);

    void bulkInsertSalesPrice(@Param("salesPrice") List<SalesPrice> salesPrice);

    void bulkInsertStdOutsourcingCost(@Param("stdCost") List<StdOutsourcingCost> stdCost);

    void bulkInsertPurchaseOutsourcingCost(@Param("purchaseCost") List<PurchaseOutsourcingCost> purchaseCost);

    // .service.partitioningPartNoByOrderPlanRawOperation 용도
    void bulkInsertOperationPlanProductionRate(@Param("operationPlanRate") List<OperationPlanProductionRate> operationPlanRate);

    void bulkInsertSapOrderPlan(@Param("sapOrderPlans") List<SapOrderPlan> sapOrderPlans);

    void bulkUpdateMesBox(@Param("mesBoxes") List<MesBoxData> mesBoxes);

    void bulkUpdateMesInboundStock(@Param("mesStocks") List<MesInboundStock> mesStocks);

    void bulkUpdateMesInboundStockBox(@Param("mesStockBoxes") List<MesInboundStockBox> mesStocks);

    void bulkUpdateLineAndYraStock(@Param("updateStocks") List<UpdateLineAndCustomerStock> updateStocks);

    List<String> getMissingItemCodeByMesBox(@Param("itemCodeArray") String[] itemCodeArray);

    List<String> getMissingItemCodeByMesStock(@Param("itemCodeArray") String[] itemCodeArray);

    List<String> getMissingItemCodeByMesStockBox(@Param("itemCodeArray") String[] itemCodeArray);

    List<MesBox> findAllMesBox();

    List<MesInboundStock> findAllMesStock();

    List<CarPropsCombineSpec> findAllCarPropsByResponderAndStDate(@Param("responder") String responder, @Param("stDate") LocalDate stDate);

    List<MonthProductAgg> aggregationMonthProduction(@Param("stDate") LocalDate stDate, @Param("specs") List<CarPropsGroupSpecCombineSpec> specs);

    List<MonthProductAgg> aggregationMonthProductionNormal(@Param("stDate") LocalDate stDate, @Param("uniqueProps") List<String> uniqueProps);

    List<CarItemMonthAgg> findMonthlyAggByCarItem();

    LocalDate findRecentlyStDate();

    void deleteMesOutboundStock(@Param("date")LocalDate date);

    void deleteOutboundTarget(@Param("date")LocalDate date);

    void deleteOpsPlanRawByStDate(@Param("date") LocalDate date);

    void deleteOpsPlanRawAggByStDate(@Param("date") LocalDate date);

    void deleteOpsMonthlyPlanAggByStDate(@Param("date") LocalDate date);

    void deleteOpsPlanProductionRateByStDate(@Param("date") LocalDate date);

    void deleteOpsLastMonthlyPlanAgg();

    void deleteOpsPastMonthlyPlanAgg();

    void deleteSalesPrice();

    void deleteStdOutsourcingCost();

    void deletePurchaseOutsourcingCost();
}
