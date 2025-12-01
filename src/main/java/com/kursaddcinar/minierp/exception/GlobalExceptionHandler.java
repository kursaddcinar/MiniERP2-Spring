package com.kursaddcinar.minierp.exception;

import com.kursaddcinar.minierp.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Veri bulunamadığında (404 döner ama ApiResponse yapımız gereği 200 OK içinde success:false dönebiliriz,
    // ya da HTTP status'ü de değiştirebiliriz.
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.ok(ApiResponse.error(ex.getMessage()));
    }

    // İş kuralları hatası (Validasyon, mantık hatası vb.)
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessRule(BusinessRuleException ex) {
        return ResponseEntity.ok(ApiResponse.error(ex.getMessage()));
    }

    // Beklenmeyen genel hatalar (NullPointer, DB connection hatası vb.)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneralException(Exception ex) {
        // Loglama burada yapılmalı (örn: slf4j)
        ex.printStackTrace(); // Geliştirme aşaması için
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Beklenmeyen bir hata oluştu: " + ex.getMessage()));
    }
}