package com.kursaddcinar.minierp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DtoUpdateProductCategory {
    @NotBlank(message = "Kategori adı zorunludur")
    private String categoryName;
    
    private String description;
    private boolean isActive = true;
}