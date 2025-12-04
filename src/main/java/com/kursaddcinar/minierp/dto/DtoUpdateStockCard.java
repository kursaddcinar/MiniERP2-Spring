package com.kursaddcinar.minierp.dto;

import jakarta.validation.constraints.DecimalMin;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class DtoUpdateStockCard {
    
    @DecimalMin(value = "0.0", message = "Stok miktarı 0'dan küçük olamaz")
    private BigDecimal currentStock;
    
    @DecimalMin(value = "0.0", message = "Rezerve stok 0'dan küçük olamaz")
    private BigDecimal reservedStock;
}