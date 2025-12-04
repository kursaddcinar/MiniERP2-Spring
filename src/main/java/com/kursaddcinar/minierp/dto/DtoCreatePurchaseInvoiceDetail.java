package com.kursaddcinar.minierp.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class DtoCreatePurchaseInvoiceDetail {
    @NotNull
    private Integer productId;
    
    @NotNull
    @DecimalMin(value = "0.001")
    private BigDecimal quantity;
    
    @NotNull
    @DecimalMin(value = "0.0")
    private BigDecimal unitPrice;
    
    @NotNull
    @DecimalMin(value = "0.0")
    private BigDecimal vatRate;
    
    private String description;
}