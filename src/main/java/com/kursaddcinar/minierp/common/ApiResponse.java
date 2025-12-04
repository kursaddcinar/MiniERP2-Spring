package com.kursaddcinar.minierp.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    // Başarılı dönüşler için yardımcı metodlar
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, data);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "İşlem başarılı", data);
    }

    // Hatalı dönüşler için yardımcı metodlar
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
    
 // 2. Mesaj ve Status Code ile hata dönme (Handler için gerekli olan bu)
    // Not: Status code zaten HTTP Header'da gidiyor (ResponseStatus ile).
    // O yüzden burada sadece parametre olarak alıyoruz, mevcut yapı bozulmasın diye kullanmıyoruz.
    public static <T> ApiResponse<T> error(String message, int statusCode) {
        return new ApiResponse<>(false, message, null);
    }
}