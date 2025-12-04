package com.kursaddcinar.minierp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DtoCreateStockTransaction {
    @NotNull
    private Integer productId;
    
    @NotNull
    private Integer warehouseId;
    
    @NotNull
    private LocalDateTime transactionDate;
    
    @NotNull // "GIRIS" veya "CIKIS"
    private String transactionType; 
    
    @NotNull
    private BigDecimal quantity;
    
    private BigDecimal unitPrice = BigDecimal.ZERO;
    private String description;
    private String documentType;
    private String documentNo;
}