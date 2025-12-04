package com.kursaddcinar.minierp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DtoInvoiceCancellation {
    @NotBlank(message = "İptal nedeni zorunludur")
    private String reason;
}