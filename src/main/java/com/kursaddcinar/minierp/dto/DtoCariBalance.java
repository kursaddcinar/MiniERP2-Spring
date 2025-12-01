package com.kursaddcinar.minierp.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DtoCariBalance {
    private Integer cariId;
    private String cariCode;
    private String cariName;
    private String typeName;
    private BigDecimal currentBalance;
    private BigDecimal creditLimit;
    private BigDecimal creditUsed;
    private BigDecimal creditAvailable;
    private String balanceType;
    private LocalDateTime lastTransactionDate;
}
