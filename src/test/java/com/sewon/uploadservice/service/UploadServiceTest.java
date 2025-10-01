package com.sewon.uploadservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.sewon.uploadservice.model.dto.CombineData;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
class UploadServiceTest {

    @Autowired
    private UploadService uploadService;

    private final String pathC2000 = "src/test/resources/g-2000.csv";
    private final String pathC3000 = "src/test/resources/g-3000.csv";
    private final String pathD2000 = "src/test/resources/d-2000.csv";
    private final String pathD3000 = "src/test/resources/d-3000.csv";


    @Test
    void uploadPlanTest() throws IOException {
        LocalDate date = LocalDate.now();

        MultipartFile g2000File = new MockMultipartFile(
            "file",
            "g-2000",
            "text/csv",
            Files.readAllBytes(Path.of(pathC2000)));
//        MultipartFile g3000File = new MockMultipartFile(
//            "file",
//            "g-3000",
//            "text/csv",
//            Files.readAllBytes(Path.of(pathC3000)));

        MultipartFile d2000File = new MockMultipartFile(
            "file",
            "d-2000",
            "text/csv",
            Files.readAllBytes(Path.of(pathD2000)));
//        MultipartFile d3000File = new MockMultipartFile(
//            "file",
//            "d-3000",
//            "text/csv",
//            Files.readAllBytes(Path.of(pathD3000)));

        List<MultipartFile> gFils = List.of(g2000File);
        List<MultipartFile> dFils = List.of(d2000File);

        List<CombineData> result = uploadService.combineCSV(gFils, dFils, date);

        assertThat(result).isNotNull();
        result.forEach(data -> System.out.println("결과: " + data));
    }
}