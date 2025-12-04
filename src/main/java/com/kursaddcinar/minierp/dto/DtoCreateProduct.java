package com.kursaddcinar.minierp.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class DtoCreateProduct {
    @NotBlank(message = "Ürün kodu zorunludur")
    private String productCode;

    @NotBlank(message = "Ürün adı zorunludur")
    private String productName;
    
    private Integer categoryId;
    
    @NotNull(message = "Birim seçimi zorunludur")
    private Integer unitId;
    
    @DecimalMin(value = "0.0", message = "Satış fiyatı 0'dan küçük olamaz")
    private BigDecimal salePrice = BigDecimal.ZERO;
    
    @DecimalMin(value = "0.0", message = "Alış fiyatı 0'dan küçük olamaz")
    private BigDecimal purchasePrice = BigDecimal.ZERO;
    
    private BigDecimal vatRate = new BigDecimal("18.00");
    private BigDecimal minStockLevel = BigDecimal.ZERO;
    private BigDecimal maxStockLevel = BigDecimal.ZERO;
}