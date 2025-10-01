package com.sewon.uploadservice.service;



import static org.assertj.core.api.Assertions.assertThat;

import com.sewon.uploadservice.model.dto.csv.CsvData;
import com.sewon.uploadservice.model.dto.csv.Ttime;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class CsvFileParserTest {

    private final String pathC2000 = "src/test/resources/g-2000.csv";
    private final String pathC3000 = "src/test/resources/g-3000.csv";
    private final String pathD2000 = "src/test/resources/d-2000.csv";

    private final CsvFileParser parser = new CsvFileParser();

    @DisplayName("파일 가져오기")
    @Test
    void getFile(){
        String path1 = Path.of(pathC2000).getFileName().toString();
        String path2 = Path.of(pathC3000).getFileName().toString();
        String path3 = Path.of(pathD2000).getFileName().toString();

        System.out.println(path1);
        System.out.println(path2);
        System.out.println(path3);
    }

    @DisplayName("g-2000 파일 파싱 후 객체로 변환")
    @Test
    void g2000FileParsing() throws IOException {
        String fileName = "g-2000";
        MultipartFile file = new MockMultipartFile(
            "file",
            fileName,
            "text/csv",
            Files.readAllBytes(Path.of(pathC2000)));

        assertThat(file).isNotNull();
        List<CsvData> dataList = parser.g2000Parsing(file);

        assertThat(dataList).isNotEmpty();

        dataList.forEach(data -> System.out.println("[" + data + "]"));
    }

    @DisplayName("g-3000 파일 파싱 후 객체로 변환")
    @Test
    void g3000FileParsing() throws IOException {
        String fileName = "g-3000";
        MultipartFile file = new MockMultipartFile(
            "file",
            fileName,
            "text/csv",
            Files.readAllBytes(Path.of(pathC3000)));

        assertThat(file).isNotNull();
        List<CsvData> dataList = parser.g3000Parsing(file);

        assertThat(dataList).isNotEmpty();

        dataList.forEach(data -> System.out.println("[" + data + "]"));
    }

    @Disabled
    @DisplayName("09/06(요일) 이런 날짜 타입과 유사한지")
    @Test
    void regexMatch(){
        // 현재 private
        //assertThat(parser.headMatches("09/06(토)")).isTrue();
        //assertThat(parser.headMatches("REM")).isFalse();
    }


    @DisplayName("d-2000, d-3000 파일 파싱")
    @Test
    void aVoid() throws IOException {
        String fileName = "d-2000";
        MultipartFile file = new MockMultipartFile(
            "file",
            fileName,
            "text/csv",
            Files.readAllBytes(Path.of(pathD2000)));


        assertThat(file).isNotNull();
        List<Ttime> ttimeList = parser.dFileParsing(List.of(file));

        ttimeList.forEach(data -> System.out.println("[" + data + "]"));
    }
}


