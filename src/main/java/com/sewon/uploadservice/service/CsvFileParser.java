package com.sewon.uploadservice.service;

import static com.sewon.uploadservice.model.collection.DKey.T_TIME_FIRST;
import static com.sewon.uploadservice.model.collection.DKey.T_TIME_LAST;
import static com.sewon.uploadservice.model.collection.GKey.ALC;
import static com.sewon.uploadservice.model.collection.GKey.CATEGORY;
import static com.sewon.uploadservice.model.collection.GKey.CONSUMER_STOCK;
import static com.sewon.uploadservice.model.collection.GKey.DOMESTIC_STOCK;
import static com.sewon.uploadservice.model.collection.GKey.ITEM_CODE;
import static com.sewon.uploadservice.model.collection.GKey.PREVIOUS_DAY_RESULT;

import com.sewon.uploadservice.model.collection.DKey;
import com.sewon.uploadservice.model.collection.GKey;
import com.sewon.uploadservice.model.dto.csv.CsvData;
import com.sewon.uploadservice.model.dto.csv.DayPlusData;
import com.sewon.uploadservice.model.dto.csv.OperationPlan;
import com.sewon.uploadservice.model.dto.csv.OutboundTargetData;
import com.sewon.uploadservice.model.dto.csv.SalesPriceUnit;
import com.sewon.uploadservice.model.entity.SapOrderPlan;
import com.sewon.uploadservice.model.entity.PurchaseOutsourcingCost;
import com.sewon.uploadservice.model.entity.StdOutsourcingCost;
import com.sewon.uploadservice.model.dto.csv.Ttime;
import com.sewon.uploadservice.model.dto.csv.UpdateLineAndCustomerStock;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class CsvFileParser {


    public List<CsvData> g2000Parsing(MultipartFile file) {
        if (isEnableParse(file.getOriginalFilename(), "g-2000")) {
            return getGCsvDataList(file, Charset.forName("EUC-KR"));
        }
        return List.of();
    }

    public List<CsvData> g3000Parsing(MultipartFile file) {
        if (isEnableParse(file.getOriginalFilename(), "g-3000")) {
            return getGCsvDataList(file, Charset.forName("EUC-KR"));
        }
        return List.of();
    }

    public List<Ttime> dFileParsing(List<MultipartFile> files) {
        List<Ttime> ttimeList = new ArrayList<>();
        if (CSVFileValidator.filesEmptyCheck(files)){
            return ttimeList;
        }
        for (MultipartFile file : files) {
            if (isEnableParse(file.getOriginalFilename(), "d-")) {
                try (CSVParser csvReader = getParser(file, Charset.forName("EUC-KR"))) {

                    int lastIdx = csvReader.getHeaderMap().get(T_TIME_LAST.getHeader()); // 84
                    int initIdx = csvReader.getHeaderMap().get(T_TIME_FIRST.getHeader()); // 20

                    for (CSVRecord row : csvReader.getRecords()) {
                        String value = findFirstNonZeroHeader(csvReader, row, initIdx, lastIdx);
                        ttimeList.add(Ttime.of(row.get(DKey.ITEM_CODE.getHeader()), value));
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
            // 21, 85 (20, 84) () 친 부분은 인덱스
            // 21 ~ 32 (20 ~ 31)
            // 21 + 11 = 32
            // 34 ~ 45 (33 ~ 44)
            // 32 + 13 = 45
            // 47 ~ 58 (46 ~ 57)
            // 45 + 13 = 58
            // 60 ~ 71 (59 ~ 70)
            // 58 + 13 = 71
            // 73 ~ 84 (72 ~ 83)
            // 71 + 13 = 84
        }
        return ttimeList;
    }

    public Set<OutboundTargetData> OutboundTargetFileParsing(MultipartFile file, LocalDate date) {
        try (CSVParser parser = getParser(file, Charset.defaultCharset())) {
            Set<OutboundTargetData> dataSet = new HashSet<>();
            for (CSVRecord csvRecord : parser.getRecords()) {
                dataSet.add(
                    OutboundTargetData.of(
                        csvRecord.get(0),
                        getIntegerByRecord(csvRecord.get(1)),
                        getIntegerByRecord(csvRecord.get(2)),
                        getIntegerByRecord(csvRecord.get(3)),
                        getIntegerByRecord(csvRecord.get(4)),
                        date)
                );
            }
            return dataSet;
        } catch (IOException e) {
            log.error("error message: {}", e.getMessage());
            throw new UncheckedIOException(e);
        }
    }

    public List<UpdateLineAndCustomerStock> lineAndCustomerStockFileParsing(MultipartFile file) {
        try (CSVParser parser = getParser(file, Charset.defaultCharset())) {
            List<UpdateLineAndCustomerStock> dataList = new ArrayList<>();
            for (CSVRecord csvRecord : parser.getRecords()) {
                dataList.add(
                    UpdateLineAndCustomerStock.of(
                        LocalDate.parse(csvRecord.get(0)),
                        csvRecord.get(GKey.UPDATE_ITEM_CODE.getHeader()),
                        getIntegerByRecord(csvRecord.get(GKey.UPDATE_CONSUMER_STOCK.getHeader())),
                        getIntegerByRecord(csvRecord.get(GKey.UPDATE_DOMESTIC_STOCK.getHeader()))
                    )
                );
            }
            return dataList;
        } catch (IOException e) {
            log.error("error message: {}", e.getMessage());
            throw new UncheckedIOException(e);
        }
    }

    public List<OperationPlan> parsingOperationPlanFile (MultipartFile file, LocalDate date) {
        try (CSVParser parser = getParser(file, Charset.defaultCharset())) {
            List<OperationPlan> dataList = new ArrayList<>();
            for (CSVRecord csvRecord : parser.getRecords()) {
                dataList.add(
                    OperationPlan.of(
                        LocalDate.parse(csvRecord.get(0)),
                        csvRecord.get(1),
                        convertRegion(csvRecord.get(2)),
                        csvRecord.get(3),
                        convertDoorType(csvRecord.get(4)),
                        csvRecord.get(5),
                        csvRecord.get(6),
                        csvRecord.get(7),
                        getIntegerByRecord(csvRecord.get(8)),
                        getIntegerByRecord(csvRecord.get(9)),
                        getIntegerByRecord(csvRecord.get(10)),
                        getIntegerByRecord(csvRecord.get(11)),
                        getIntegerByRecord(csvRecord.get(12)),
                        csvRecord.get(13),
                        csvRecord.get(14),
                        csvRecord.get(15),
                        date
                    )
                );
            }
            return dataList;
        } catch (IOException e) {
            log.error("error message: {}", e.getMessage());
            throw new UncheckedIOException(e);
        }
    }

    public List<SalesPriceUnit> parsingSalesPriceUnitFile(MultipartFile file) {
        try (CSVParser parser = getParser(file, Charset.forName("EUC-KR"))) {
            List<SalesPriceUnit> dataList = new ArrayList<>();
            for (CSVRecord csvRecord : parser.getRecords()) {
                dataList.add(
                    SalesPriceUnit.of(
                        csvRecord.get(0),
                        csvRecord.get(1),
                        csvRecord.get(2),
                        csvRecord.get(3),
                        csvRecord.get(4),
                        csvRecord.get(5),
                        csvRecord.get(6),
                        csvRecord.get(7),
                        csvRecord.get(8),
                        csvRecord.get(9),
                        getBigDecimal(csvRecord.get(10)),
                        LocalDate.parse(csvRecord.get(11)),
                        LocalDate.parse(csvRecord.get(12))
                    )
                );
            }
            return dataList;
        } catch (IOException e) {
            log.error("error message: {}", e.getMessage());
            throw new UncheckedIOException(e);
        }
    }

    public List<StdOutsourcingCost> parsingStdOutsourcingCostFile(MultipartFile file) {
        try (CSVParser parser = getParser(file, Charset.forName("EUC-KR"))) {
            List<StdOutsourcingCost> dataList = new ArrayList<>();
            for (CSVRecord csvRecord : parser.getRecords()) {
                dataList.add(
                    StdOutsourcingCost.of(
                        csvRecord.get(0),
                        csvRecord.get(1),
                        csvRecord.get(2),
                        getBigDecimal(csvRecord.get(3)),
                        getBigDecimal(csvRecord.get(4)),
                        getBigDecimal(csvRecord.get(5)),
                        getBigDecimal(csvRecord.get(6)),
                        getBigDecimal(csvRecord.get(7)),
                        getBigDecimal(csvRecord.get(8)),
                        getBigDecimal(csvRecord.get(9)),
                        getBigDecimal(csvRecord.get(10)),
                        getBigDecimal(csvRecord.get(11)),
                        getBigDecimal(csvRecord.get(12)),
                        getBigDecimal(csvRecord.get(13)),
                        LocalDate.parse(csvRecord.get(14)),
                        csvRecord.get(15),
                        csvRecord.get(16),
                        csvRecord.get(17),
                        csvRecord.get(18),
                        csvRecord.get(19)
                    )
                );
            }
            return dataList;
        } catch (IOException e) {
            log.error("error message: {}", e.getMessage());
            throw new UncheckedIOException(e);
        }
    }

    public List<PurchaseOutsourcingCost> parsingPurchaseOutsourcingCostFile(MultipartFile file) {
        try (CSVParser parser = getParser(file, Charset.forName("EUC-KR"))) {
            List<PurchaseOutsourcingCost> dataList = new ArrayList<>();
            for (CSVRecord csvRecord : parser.getRecords()) {
                dataList.add(
                    PurchaseOutsourcingCost.of(
                        csvRecord.get(0),
                        csvRecord.get(1),
                        csvRecord.get(2),
                        csvRecord.get(3),
                        csvRecord.get(4),
                        csvRecord.get(5),
                        csvRecord.get(6),
                        csvRecord.get(7),
                        LocalDate.parse(csvRecord.get(8)),
                        LocalDate.parse(csvRecord.get(9)),
                        csvRecord.get(10),
                        csvRecord.get(11),
                        getBigDecimal(csvRecord.get(12)),
                        LocalDate.parse(csvRecord.get(13)),
                        csvRecord.get(14),
                        csvRecord.get(15),
                        csvRecord.get(16)
                    )
                );
            }
            return dataList;
        } catch (IOException e) {
            log.error("error message: {}", e.getMessage());
            throw new UncheckedIOException(e);
        }
    }

    public List<SapOrderPlan> parsingSapOrderPlanFile(MultipartFile file) {
        try (CSVParser parser = getParser(file, StandardCharsets.UTF_8)) {
            List<SapOrderPlan> dataList = new ArrayList<>();
            for (CSVRecord csvRecord : parser.getRecords()) {
                dataList.add(
                    SapOrderPlan.of(
                        LocalDate.parse(csvRecord.get(0)),
                        csvRecord.get(1),
                        csvRecord.get(2),
                        csvRecord.get(3),
                        csvRecord.get(4),
                        csvRecord.get(5),
                        csvRecord.get(6),
                        csvRecord.get(7),
                        csvRecord.get(8),
                        csvRecord.get(9),
                        getIntegerByRecord(csvRecord.get(10)),
                        getIntegerByRecord(csvRecord.get(11)),
                        getIntegerByRecord(csvRecord.get(12)),
                        getIntegerByRecord(csvRecord.get(13)),
                        getIntegerByRecord(csvRecord.get(14)),
                        getIntegerByRecord(csvRecord.get(15)),
                        getIntegerByRecord(csvRecord.get(16)),
                        getIntegerByRecord(csvRecord.get(17)),
                        getIntegerByRecord(csvRecord.get(18)),
                        getIntegerByRecord(csvRecord.get(19)),
                        getIntegerByRecord(csvRecord.get(20)),
                        getIntegerByRecord(csvRecord.get(21)),
                        getIntegerByRecord(csvRecord.get(22)),
                        getIntegerByRecord(csvRecord.get(23)),
                        getIntegerByRecord(csvRecord.get(24)),
                        getIntegerByRecord(csvRecord.get(25)),
                        getIntegerByRecord(csvRecord.get(26)),
                        getIntegerByRecord(csvRecord.get(27)),
                        getIntegerByRecord(csvRecord.get(28)),
                        getIntegerByRecord(csvRecord.get(29)),
                        getIntegerByRecord(csvRecord.get(30)),
                        getIntegerByRecord(csvRecord.get(31)),
                        getIntegerByRecord(csvRecord.get(32)),
                        getIntegerByRecord(csvRecord.get(33)),
                        getIntegerByRecord(csvRecord.get(34)),
                        getIntegerByRecord(csvRecord.get(35)),
                        getIntegerByRecord(csvRecord.get(36)),
                        getIntegerByRecord(csvRecord.get(37)),
                        getIntegerByRecord(csvRecord.get(38)),
                        getIntegerByRecord(csvRecord.get(39)),
                        getIntegerByRecord(csvRecord.get(40)),
                        getIntegerByRecord(csvRecord.get(41)),
                        getIntegerByRecord(csvRecord.get(42)),
                        getIntegerByRecord(csvRecord.get(43)),
                        getIntegerByRecord(csvRecord.get(44)),
                        getIntegerByRecord(csvRecord.get(45)),
                        getIntegerByRecord(csvRecord.get(46)),
                        getIntegerByRecord(csvRecord.get(47)),
                        getIntegerByRecord(csvRecord.get(48)),
                        getIntegerByRecord(csvRecord.get(49)),
                        getIntegerByRecord(csvRecord.get(50)),
                        getIntegerByRecord(csvRecord.get(51)),
                        getIntegerByRecord(csvRecord.get(52)),
                        getIntegerByRecord(csvRecord.get(53)),
                        getIntegerByRecord(csvRecord.get(54)),
                        getIntegerByRecord(csvRecord.get(55)),
                        getIntegerByRecord(csvRecord.get(56)),
                        getIntegerByRecord(csvRecord.get(57)),
                        getIntegerByRecord(csvRecord.get(58)),
                        getIntegerByRecord(csvRecord.get(59)),
                        getIntegerByRecord(csvRecord.get(60)),
                        getIntegerByRecord(csvRecord.get(61)),
                        getIntegerByRecord(csvRecord.get(62)),
                        getIntegerByRecord(csvRecord.get(63)),
                        getIntegerByRecord(csvRecord.get(64)),
                        getIntegerByRecord(csvRecord.get(65))
                    )
                );
            }
            return dataList;
        } catch (IOException e) {
            log.error("error message: {}", e.getMessage());
            throw new UncheckedIOException(e);
        }
    }

    private String convertDoorType(String data){
        switch (data) {
            case "LHD" -> {
                return "D01";
            }
            case "RHD" -> {
                return "D02";
            }
            default -> {
                return data;
            }
        }
    }

    private String convertRegion(String data){
        switch (data) {
            case "DOM" -> {
                return "R01";
            }
            case "MID" -> {
                return "R02";
            }
            case "GEN" -> {
                return "R03";
            }
            case "EEC" -> {
                return "R04";
            }
            case "AUS" -> {
                return "R05";
            }
            case "HAC", "CAN", "KCI" -> {
                return "R06";
            }
            case "HMA", "KMA", "USA" -> {
                return "R07";
            }
            case "RUS" -> {
                return "R09";
            }
            default -> {
                return data;
            }
        }
    }

    private String findFirstNonZeroHeader(CSVParser csvReader, CSVRecord row, int initIdx,
        int lastIdx) {
        int i = initIdx;
        while (i <= lastIdx) {
            int limitIdx = Math.min(i + 12, lastIdx);
            for (int j = i; j < limitIdx; j++) {
                if (getIntegerByRecord(row.get(j)) > 0) {
                    return csvReader.getHeaderNames().get(j);
                }
            }
            i = limitIdx + 1; // 다음 블록으로 이동
        }
        return null; // 못 찾으면 빈 값
    }


    private List<CsvData> getGCsvDataList(MultipartFile file, Charset charset) {
        try (CSVParser csvReader = getParser(file, charset)) {
            List<CsvData> dataList = new ArrayList<>();
            for (CSVRecord csvRecord : csvReader.getRecords()) {
                if (csvRecord.get(CATEGORY.getHeader()).isBlank() ||
                    csvRecord.get(ITEM_CODE.getHeader()).startsWith("∑")) {
                    continue;
                }
                CsvData csvData = CsvData.builder()
                    .category(csvRecord.get(CATEGORY.getHeader()))
                    .alc(csvRecord.get(ALC.getHeader()))
                    .itemCode(csvRecord.get(ITEM_CODE.getHeader()))
                    .result(getIntegerByRecord(csvRecord.get(PREVIOUS_DAY_RESULT.getHeader())))
                    .line(getIntegerByRecord(csvRecord.get(CONSUMER_STOCK.getHeader())))
                    .yra(getIntegerByRecord(csvRecord.get(DOMESTIC_STOCK.getHeader())))
                    .dayplusData(addDPlusDays(
                        getRowIdxByKey(csvReader, PREVIOUS_DAY_RESULT.getHeader()),
                        csvRecord))
                    .build();
                csvData.getDayplusData().setRem(getIntegerByRecord(csvRecord.get("rem")));
                dataList.add(csvData);
            }
            return dataList;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public boolean isEnableParse(String fileName, String expression) {
        return fileName != null && expression != null && fileName.contains(expression);
    }


    public CSVParser getParser(MultipartFile file, Charset charset) throws IOException {
        Reader reader = new InputStreamReader(file.getInputStream(), charset);
        return CSVFormat.DEFAULT.builder()
            .setHeader()
            .setIgnoreHeaderCase(true)
            .setTrim(true).get()
            .parse(reader);

    }

    private Integer getIntegerByRecord(String value) {
        if (value == null || value.isBlank()) {
            return 0;
        }
        String clean = value.replaceAll("[,\\s]", "");
        try {
            // 정수만으로 이루어진 경우 바로 변환
            if (clean.matches("-?\\d+")) {
                return Integer.valueOf(clean);
            }
            // 소수점을 포함한 숫자 -> BigDecimal로 파싱 후 정수로 변환
            BigDecimal bd = new BigDecimal(clean);
            // 소수점 버림(기본): intValue()
            return bd.intValue();
        } catch (NumberFormatException | ArithmeticException e) {
            log.error("type parsing error value: {}", value);
            throw new RuntimeException("숫자 타입이 아닙니다.", e);
        }
    }

    private BigDecimal getBigDecimal(String value) {
        if (value == null || value.isBlank()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(value.replaceAll("[,\\s]", ""));
        } catch (NumberFormatException e) {
            log.error("type parsing error value: {}", value);
            throw new RuntimeException("숫자 타입이 아닙니다.", e);
        }
    }

    private DayPlusData addDPlusDays(int start, CSVRecord csvRecord) {
        DayPlusData dayPlusData = new DayPlusData();
        try {
            Class<DayPlusData> clazz = DayPlusData.class;
            int startIndex = start + 1;
            String key = csvRecord.getParser().getHeaderNames().get(startIndex);
            int i = 0;

            while (headMatches(key)) {
                Method method = clazz.getMethod("setDPlus" + i, Integer.class);
                method.invoke(dayPlusData, getIntegerByRecord(csvRecord.get(startIndex)));
                startIndex++;
                i++;
                key = csvRecord.getParser().getHeaderNames().get(startIndex);
            }
        } catch (Exception e) {
            log.error("reflection error:{}", e.getMessage());
            throw new RuntimeException(e);
        }

        return dayPlusData;
    }

    private boolean headMatches(String header) {
        // 09/06(토) 와 유사한지
        return header.matches("\\d{2}/\\d{2}\\([가-힣]+\\)");
    }

    private int getRowIdxByKey(CSVParser csvParser, String key) {
        return csvParser.getHeaderMap().get(key);
    }
}
