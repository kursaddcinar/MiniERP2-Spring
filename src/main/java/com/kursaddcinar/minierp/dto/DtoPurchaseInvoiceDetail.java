package com.kursaddcinar.minierp.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DtoPurchaseInvoiceDetail {
    private Integer detailId;
    private Integer invoiceId;
    private Integer productId;
    private String productCode;
    private String productName;
    private String unitName;
    
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal vatRate;
    
    private BigDecimal lineTotal;
    private BigDecimal vatAmount;
    private BigDecimal netTotal;
    
    private String description;
}