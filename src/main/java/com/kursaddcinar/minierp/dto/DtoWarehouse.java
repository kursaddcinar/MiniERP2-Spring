package com.kursaddcinar.minierp.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DtoWarehouse {
    private Integer warehouseId;
    private String warehouseCode;
    private String warehouseName;
    private String address;
    private String city;
    private String responsiblePerson;
    private boolean isActive;
    private LocalDateTime createdDate;
    
    // Raporlama alanları
    private int productCount;
    private BigDecimal totalStockValue;
}