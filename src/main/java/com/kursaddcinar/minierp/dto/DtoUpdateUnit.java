package com.kursaddcinar.minierp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DtoUpdateUnit {
    @NotBlank(message = "Birim adı zorunludur")
    private String unitName;
    
    private boolean isActive = true;
}