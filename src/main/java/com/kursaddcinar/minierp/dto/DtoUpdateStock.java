package com.kursaddcinar.minierp.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DtoUpdateStock {
    private Integer productId;
    private Integer warehouseId;
    private BigDecimal quantity;
    private String transactionType; // GIRIS veya CIKIS
}