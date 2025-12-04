package com.kursaddcinar.minierp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DtoCreateProductCategory {
    @NotBlank(message = "Kategori kodu zorunludur")
    private String categoryCode;

    @NotBlank(message = "Kategori adı zorunludur")
    private String categoryName;
    
    private String description;
}