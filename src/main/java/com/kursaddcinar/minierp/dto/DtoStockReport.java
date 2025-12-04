package com.kursaddcinar.minierp.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class DtoStockReport {
    private String reportType;
    private LocalDateTime reportDate;
    private List<DtoStockCard> stockCards;
    private BigDecimal totalValue;
    private int totalProducts;
    private int criticalStockProducts;
    private int overStockProducts;
    private int outOfStockProducts;
    private int totalItems;
}