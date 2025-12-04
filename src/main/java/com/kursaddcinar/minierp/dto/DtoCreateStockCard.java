package com.kursaddcinar.minierp.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class DtoCreateStockCard {
    
    @NotNull(message = "Ürün seçimi zorunludur")
    private Integer productId;
    
    @NotNull(message = "Depo seçimi zorunludur")
    private Integer warehouseId;
    
    @DecimalMin(value = "0.0", message = "Stok miktarı 0'dan küçük olamaz")
    private BigDecimal currentStock = BigDecimal.ZERO;
    
    @DecimalMin(value = "0.0", message = "Rezerve stok 0'dan küçük olamaz")
    private BigDecimal reservedStock = BigDecimal.ZERO;
}