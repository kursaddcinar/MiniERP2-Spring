package com.kursaddcinar.minierp.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DtoDetailedUpdateStock {
    private Integer productId;
    private Integer warehouseId;
    private BigDecimal quantity;
    private String transactionType;
    private BigDecimal unitPrice;
    private String documentNo;
    private String notes;
}