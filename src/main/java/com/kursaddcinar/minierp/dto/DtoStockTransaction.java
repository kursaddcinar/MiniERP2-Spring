package com.kursaddcinar.minierp.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DtoStockTransaction {
    private Integer transactionId;
    private Integer productId;
    private String productCode;
    private String productName;
    private Integer warehouseId;
    private String warehouseCode;
    private String warehouseName;
    private String unitName;
    
    private LocalDateTime transactionDate;
    private String transactionType; // GIRIS, CIKIS
    
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
    
    private String description;
    private String documentType;
    private String documentNo;
    private LocalDateTime createdDate;
}