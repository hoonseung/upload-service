package com.sewon.uploadservice.repository.car;

import com.sewon.uploadservice.model.dto.csv.UpdateLineAndCustomerStock;
import com.sewon.uploadservice.model.entity.CarOrder;
import com.sewon.uploadservice.model.dto.mes.MESInboundStockRecord;
import com.sewon.uploadservice.model.entity.MesBox;
import com.sewon.uploadservice.model.entity.MesInboundStockBox;
import com.sewon.uploadservice.model.entity.MesInboundStock;
import com.sewon.uploadservice.model.dto.mes.MesBoxData;
import com.sewon.uploadservice.model.entity.MesOutboundStock;
import com.sewon.uploadservice.model.entity.SecondOutbound;
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

    void bulkInsertSecondOutbound(@Param("outbounds") List<SecondOutbound> outbounds);

    void bulkUpdateMesBox(@Param("mesBoxes") List<MesBoxData> mesBoxes);

    void bulkUpdateMesInboundStock(@Param("mesStocks") List<MesInboundStock> mesStocks);

    void bulkUpdateMesInboundStockBox(@Param("mesStockBoxes") List<MesInboundStockBox> mesStocks);

    void bulkUpdateLineAndYraStock(@Param("updateStocks") List<UpdateLineAndCustomerStock> updateStocks);

    List<String> getMissingItemCodeByMesBox(@Param("itemCodeArray") String[] itemCodeArray);

    List<String> getMissingItemCodeByMesStock(@Param("itemCodeArray") String[] itemCodeArray);

    List<String> getMissingItemCodeByMesStockBox(@Param("itemCodeArray") String[] itemCodeArray);

    List<MesBox> findAllMesBox();

    List<MesInboundStock> findAllMesStock();

    void deleteMesOutboundStock(@Param("date")LocalDate date);

    void deleteSecondOutbound(@Param("date")LocalDate date);
}
