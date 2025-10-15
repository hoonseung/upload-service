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
import com.sewon.uploadservice.model.collection.OutboundKey;
import com.sewon.uploadservice.model.dto.csv.CsvData;
import com.sewon.uploadservice.model.dto.csv.DayPlusData;
import com.sewon.uploadservice.model.dto.csv.SecondOutboundData;
import com.sewon.uploadservice.model.dto.csv.Ttime;
import com.sewon.uploadservice.model.dto.csv.UpdateLineAndCustomerStock;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
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

    public Set<SecondOutboundData> secondOutboundFileParsing(MultipartFile file, LocalDate date) {
        try (CSVParser parser = getParser(file, Charset.defaultCharset())) {
            Set<SecondOutboundData> dataSet = new HashSet<>();
            for (CSVRecord csvRecord : parser.getRecords()) {
                dataSet.add(
                    SecondOutboundData.of(
                        csvRecord.get(0),
                        getIntegerByRecord(csvRecord.get(1)),
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
        if (value != null) {
            return Integer.valueOf(value);
        }
        log.error("type parsing error");
        throw new RuntimeException("숫자 타입이 아닙니다.");
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
