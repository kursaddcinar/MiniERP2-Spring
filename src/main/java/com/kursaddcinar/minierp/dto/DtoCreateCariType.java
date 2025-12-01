package com.kursaddcinar.minierp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DtoCreateCariType {
    @NotBlank(message = "Tür kodu zorunludur")
    private String typeCode;
    
    @NotBlank(message = "Tür adı zorunludur")
    private String typeName;
    
    private String description;
}