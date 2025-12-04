package com.kursaddcinar.minierp.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

//CARD GÖRÜNTÜLEME
@Data
public class DtoStockCard {
    private Integer stockCardId;
    private Integer productId;
    private String productCode;
    private String productName;
    private Integer warehouseId;
    private String warehouseCode;
    private String warehouseName;
    private String unitName;
    
    private BigDecimal currentStock;
    private BigDecimal reservedStock;
    private BigDecimal availableStock;
    
    private BigDecimal minStockLevel;
    private BigDecimal maxStockLevel;
    
    private LocalDateTime lastTransactionDate;
    private LocalDateTime createdDate;
    
    // NORMAL, CRITICAL, OVER
    private String stockStatus; 
}