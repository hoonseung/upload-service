package com.sewon.uploadservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@ControllerAdvice
public class GlobalControllerAdvice {


    @ExceptionHandler(RuntimeException.class)
    public String handelRuntimeException(RuntimeException ex){
        log.error("occurs error.. message: {}, trace: {}", ex.getMessage(), ex.getStackTrace());
        return "error/400error";
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSizeException(MaxUploadSizeExceededException ex, Model model) {
        log.error("occurs error.. message: {}", ex.getMessage());
        model.addAttribute("message", "업로드 가능한 최대 파일 크기를 초과했습니다.");
        return"error/400error";
    }

    @ExceptionHandler(Exception.class)
    public String handelException(Exception ex){
        log.error("occurs error.. message: {}", ex.getMessage());
        return "error/500error";
    }

}
