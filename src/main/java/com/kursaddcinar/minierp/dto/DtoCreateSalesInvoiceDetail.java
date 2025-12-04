package com.kursaddcinar.minierp.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class DtoCreateSalesInvoiceDetail {
    @NotNull(message = "Ürün seçimi zorunludur")
    private Integer productId;
    
    @NotNull
    @DecimalMin(value = "0.001", message = "Miktar 0'dan büyük olmalıdır")
    private BigDecimal quantity;
    
    @NotNull
    @DecimalMin(value = "0.0", message = "Birim fiyat 0'dan küçük olamaz")
    private BigDecimal unitPrice;
    
    @NotNull
    @DecimalMin(value = "0.0", message = "KDV oranı 0'dan küçük olamaz")
    private BigDecimal vatRate;
    
    private String description;
}