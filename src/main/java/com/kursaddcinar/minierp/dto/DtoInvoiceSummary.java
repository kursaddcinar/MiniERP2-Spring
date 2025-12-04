package com.kursaddcinar.minierp.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DtoInvoiceSummary {
    private int totalInvoices;
    private BigDecimal totalAmount;
    
    private int draftInvoices;
    private int approvedInvoices;
    private int cancelledInvoices;
    
    private BigDecimal draftAmount;
    private BigDecimal approvedAmount;
}