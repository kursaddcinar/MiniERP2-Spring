package com.kursaddcinar.minierp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DtoStockMovement {
    private Integer productId;
    private String productCode;
    private String productName;
    private Integer fromWarehouseId;
    private String fromWarehouseName;
    private Integer toWarehouseId;
    private String toWarehouseName;
    private BigDecimal quantity;
    private String description;
    private LocalDateTime movementDate;
}