package com.kursaddcinar.minierp.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class DtoPurchaseInvoice {
    private Integer invoiceId;
    private String invoiceNo;
    private Integer cariId;
    private String cariCode;
    private String cariName;
    private Integer warehouseId;
    private String warehouseName;
    private LocalDateTime invoiceDate;
    private LocalDateTime dueDate;
    
    private BigDecimal subTotal;
    private BigDecimal vatAmount;
    private BigDecimal total;
    
    private String description;
    private String status;
    private LocalDateTime createdDate;
    
    private List<DtoPurchaseInvoiceDetail> details;
}