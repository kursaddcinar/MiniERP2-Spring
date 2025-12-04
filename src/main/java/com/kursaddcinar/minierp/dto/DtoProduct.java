package com.kursaddcinar.minierp.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DtoProduct {
    private Integer productId;
    private String productCode;
    private String productName;
    private Integer categoryId;
    private String categoryName;
    private Integer unitId;
    private String unitName;
    
    private BigDecimal salePrice;
    private BigDecimal purchasePrice;
    private BigDecimal vatRate;
    private BigDecimal minStockLevel;
    private BigDecimal maxStockLevel;
    
    private boolean isActive;
    private LocalDateTime createdDate;
    
    // Stok Durumu (Service tarafında hesaplanıp set edilecek)
    private BigDecimal currentStock;
    private BigDecimal reservedStock;
    private BigDecimal availableStock;
}