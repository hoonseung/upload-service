package com.sewon.uploadservice.repository.mes;

import com.sewon.uploadservice.model.dto.mes.MESInboundAllBoxStockRecord;
import com.sewon.uploadservice.model.dto.mes.MESInboundBoxStockRecord;
import com.sewon.uploadservice.model.dto.mes.MESInboundStockBoxRecord;
import com.sewon.uploadservice.model.dto.mes.MESInboundStockRecord;
import com.sewon.uploadservice.model.dto.mes.MESOutboundStockRecord;
import com.sewon.uploadservice.model.dto.mes.UniqueFactoryItemCode;
import java.time.LocalDate;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MESStockMapper {

    List<MESInboundBoxStockRecord> findInboundWaitingStockSummaryByTargetsBulk
        (@Param("factory") String factory,
            @Param("itemCodeList") List<String> itemCodeList);

    List<MESInboundBoxStockRecord> findInboundPackingStockSummaryByTargetsBulk
        (@Param("factory") String factory,
            @Param("itemCodeList") List<String> itemCodeList);

    List<MESInboundAllBoxStockRecord> findInboundAllBoxSummaryByTargetsBulk
        (@Param("itemCodeList") List<String> itemCodeList);

    List<MESInboundStockRecord> findInboundStockSummaryByTargetsBulk
        (@Param("itemCodeList") List<String> itemCodeList);

    List<MESInboundStockRecord> findInboundStockSummaryByTargetsBulkUpdateOnly
        (@Param("codes") List<UniqueFactoryItemCode> codes);

    List<MESInboundStockBoxRecord> findInboundStockBoxSummaryByTargetsBulk
        (@Param("itemCodeList") List<String> itemCodeList);

    List<MESOutboundStockRecord> findOutboundStockTotalByDate(
        @Param("from") LocalDate from,
        @Param("to") LocalDate to);


}
