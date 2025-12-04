package com.kursaddcinar.minierp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DtoCreateStockMovement {
    @NotNull
    private Integer productId;
    @NotNull
    private Integer fromWarehouseId;
    @NotNull
    private Integer toWarehouseId;
    @NotNull
    private BigDecimal quantity;
    
    private String description;
    private LocalDateTime movementDate;
}