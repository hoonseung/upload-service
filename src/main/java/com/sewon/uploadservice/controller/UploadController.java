package com.sewon.uploadservice.controller;

import com.sewon.uploadservice.service.CSVFileValidator;
import com.sewon.uploadservice.service.UploadService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/api")
@RequiredArgsConstructor

@RestController
public class UploadController {

    private final UploadService uploadService;

    @GetMapping()
    public String uploadHome() {
        return "upload";
    }

    // g파일, d파일 무조건 있어야 함
    @PostMapping("/v1/upload/plan")
    public ResponseEntity<String> uploadingPlanFile(@RequestParam("gFile") List<MultipartFile> gFile,
        @RequestParam("dFile") List<MultipartFile> dFile,
        @RequestParam(value = "date", required = false) LocalDate date) {
        if ((gFile.isEmpty() && dFile.isEmpty()) ||
            (gFile.size() > 2 || dFile.size() > 2) ||
            (CSVFileValidator.csvFileListInValid(gFile, "g-") || CSVFileValidator.csvFileListInValid(dFile, "d-"))) {
            return ResponseEntity.badRequest().body("fail");
        }

        uploadService.planUpload(gFile, dFile,
            date == null ? LocalDate.now() : date);
        return ResponseEntity.ok().body("success");
    }

    @PostMapping("/v1/upload/outbound/second")
    public ResponseEntity<String> upload2andFile(@RequestParam("file") MultipartFile file){
        String prefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        if (file.isEmpty() || CSVFileValidator.csvFileInValid(file, prefix + "-2차")) {
            return ResponseEntity.badRequest().body("fail");
        }
         uploadService.outboundTargetUpload(file, LocalDate.now());
        return ResponseEntity.ok().body("success");
    }

    @PostMapping("/v1/upload/line/customer/stock")
    public ResponseEntity<String>  updateLineAndCustomerStock(@RequestParam("file") MultipartFile file){
        String prefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        if (file.isEmpty() || CSVFileValidator.csvFileInValid(file, prefix)) {
            return ResponseEntity.badRequest().body("fail");
        }
        uploadService.updateLineAndCustomerStock(file);
        return ResponseEntity.ok().body("success");
    }




}
