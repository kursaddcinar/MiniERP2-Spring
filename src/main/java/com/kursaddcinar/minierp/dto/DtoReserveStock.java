package com.kursaddcinar.minierp.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DtoReserveStock {
    private Integer productId;
    private Integer warehouseId;
    private BigDecimal quantity;
}