package com.sewon.uploadservice.service;

import java.util.List;
import java.util.Objects;
import org.springframework.web.multipart.MultipartFile;


public abstract class CSVFileValidator {

    private CSVFileValidator() {
    }

    public static boolean csvFileListInValid(List<MultipartFile> files, String prefix) {
        if (filesEmptyCheck(files)) {
            return true;
        } else {
            for (MultipartFile file : files) {
                if (csvInValid(file, prefix)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static boolean csvFileInValid(MultipartFile file, String prefix) {
        if (fileEmptyCheck(file)) {
            return true;
        } else {
            return csvInValid(file, prefix);
        }
    }


    public static boolean csvInValid(MultipartFile file, String prefix) {
        String contentType = file.getContentType();
        String filename = file.getOriginalFilename();
        return contentType == null
            || filename == null
            || !filename.startsWith(prefix)
            || !filename.toLowerCase().endsWith(".csv")
            || !contentType.equals("text/csv");
    }

    public static boolean fileEmptyCheck(MultipartFile file){
        return Objects.isNull(file) || file.isEmpty();
    }

    public static boolean filesEmptyCheck(List<MultipartFile> files){
        return Objects.isNull(files) || files.isEmpty();
    }
}
