package com.kursaddcinar.minierp.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DtoCariTransaction {
    private Integer transactionId;
    private Integer cariId;
    private String cariCode;
    private String cariName;
    private LocalDateTime transactionDate;
    private String transactionType;
    private BigDecimal amount;
    
    // Service katmanında hesaplanacak (Borç/Alacak kolonları için)
    private BigDecimal debitAmount; 
    private BigDecimal creditAmount;
    private BigDecimal balance; 
    
    private String description;
    private String documentType;
    private String documentNo;
    private LocalDateTime createdDate;
}