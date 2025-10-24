package com.sewon.uploadservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handelRuntimeException(RuntimeException ex){
        log.error("occurs error.. message: {}", ex.getMessage(), ex);
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> handleMaxSizeException(MaxUploadSizeExceededException ex, Model model) {
        log.error("occurs error.. message: {}", ex.getMessage());
        return ResponseEntity.badRequest().body("업로드 가능한 최대 파일 크기를 초과했습니다.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handelException(Exception ex){
        log.error("occurs error.. message: {}", ex.getMessage(), ex);
        return  ResponseEntity.internalServerError().build();
    }

}
