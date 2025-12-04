package com.kursaddcinar.minierp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class DtoCreateUnit {
    @NotBlank(message = "Birim kodu zorunludur")
    private String unitCode;

    @NotBlank(message = "Birim adı zorunludur")
    private String unitName;
}