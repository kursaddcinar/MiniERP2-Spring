package com.kursaddcinar.minierp.exception;


import java.nio.file.AccessDeniedException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.kursaddcinar.minierp.common.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Veri bulunamadığında (404 döner ama ApiResponse yapımız gereği 200 OK içinde success:false dönebiliriz,
    // ya da HTTP status'ü de değiştirebiliriz.
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.ok(ApiResponse.error(ex.getMessage()));
    }
    
    // Giriş Hatası (Şifre yanlışsa)
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<Object> handleBadCredentials(BadCredentialsException e) {
        return ApiResponse.error("Kullanıcı adı veya şifre hatalı.", 401);
    }
    
    // Yetki Hatası (Admin yerine User girmeye çalışırsa)
    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse<Object> handleAccessDenied(Exception e) {
        return ApiResponse.error("Bu işlem için yetkiniz bulunmamaktadır.", 403);
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