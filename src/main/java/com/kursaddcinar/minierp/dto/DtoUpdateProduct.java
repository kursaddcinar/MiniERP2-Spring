package com.kursaddcinar.minierp.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class DtoUpdateProduct {
    @NotBlank(message = "Ürün adı zorunludur")
    private String productName;
    
    private Integer categoryId;
    
    @NotNull(message = "Birim seçimi zorunludur")
    private Integer unitId;
    
    @DecimalMin(value = "0.0")
    private BigDecimal salePrice;
    
    @DecimalMin(value = "0.0")
    private BigDecimal purchasePrice;
    
    private BigDecimal vatRate;
    private BigDecimal minStockLevel;
    private BigDecimal maxStockLevel;
    private boolean isActive = true;
}