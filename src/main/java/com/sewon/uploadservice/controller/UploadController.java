package com.sewon.uploadservice.controller;

import static com.sewon.uploadservice.service.CSVFileValidator.csvFileInValid;
import static com.sewon.uploadservice.service.CSVFileValidator.csvFileListInValid;
import static com.sewon.uploadservice.service.CSVFileValidator.csvInValidByPart;
import static com.sewon.uploadservice.service.CSVFileValidator.fileEmptyCheck;
import static com.sewon.uploadservice.service.CSVFileValidator.filesEmptyCheck;

import com.sewon.uploadservice.service.CSVFileValidator;
import com.sewon.uploadservice.service.UploadService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public ResponseEntity<String> uploadingPlanFile(
        @RequestParam(value = "gFile", required = false) List<MultipartFile> gFile,
        @RequestParam(value = "dFile", required = false) List<MultipartFile> dFile,
        @RequestParam(value = "date", required = false) LocalDate date) {
        if ((filesEmptyCheck(gFile) && filesEmptyCheck(dFile)) ||
            (csvFileListInValid(gFile, "g-") && csvFileListInValid(dFile, "d-"))) {
            return ResponseEntity.badRequest().body("fail");
        }

        uploadService.planUpload(gFile, dFile,
            date == null ? LocalDate.now() : date);
        return ResponseEntity.ok().body("success");
    }

    @PostMapping("/v1/upload/outbound/second")
    public ResponseEntity<String> upload2andFile(@RequestParam("file") MultipartFile file) {
        String prefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        if (fileEmptyCheck(file) || csvFileInValid(file, prefix + "-2차")) {
            return ResponseEntity.badRequest().body("fail");
        }
        uploadService.outboundTargetUpload(file, LocalDate.now());
        return ResponseEntity.ok().body("success");
    }

    @PostMapping("/v1/upload/line/customer/stock")
    public ResponseEntity<String> updateLineAndCustomerStock(
        @RequestParam("file") MultipartFile file) {
        String prefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        if (fileEmptyCheck(file) || csvFileInValid(file, prefix)) {
            return ResponseEntity.badRequest().body("fail");
        }
        uploadService.updateLineAndCustomerStock(file);
        return ResponseEntity.ok().body("success");
    }

    @PostMapping("/v1/upload/operation/plan")
    public ResponseEntity<String> uploadingOperationPlanFile(
        @RequestParam("file") MultipartFile file,
        @RequestParam("date") LocalDate date) {
        if (fileEmptyCheck(file) || csvInValidByPart(file, "운영계획")) {
            return ResponseEntity.badRequest().body("fail");
        }
        uploadService.operationPlanUpload(file, date);
        return ResponseEntity.ok().body("success");
    }
}
