package com.kursaddcinar.minierp.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DtoStockSummary {
    private int totalProducts;
    private int activeProducts;
    private int criticalStockProducts;
    private int overStockProducts;
    private int outOfStockProducts;
    
    private BigDecimal totalStockValue;
    private BigDecimal totalSaleValue;
    
    private int totalTransactions;
    private BigDecimal totalIncoming;
    private BigDecimal totalOutgoing;
    private BigDecimal totalIncomingValue;
    private BigDecimal totalOutgoingValue;
}