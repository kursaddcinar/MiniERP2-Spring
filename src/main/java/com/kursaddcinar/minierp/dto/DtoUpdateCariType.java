package com.kursaddcinar.minierp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DtoUpdateCariType {
    @NotBlank(message = "Tür adı zorunludur")
    private String typeName;
    
    private String description;
    private boolean isActive = true;
}