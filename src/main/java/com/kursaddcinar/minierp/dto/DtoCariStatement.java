package com.kursaddcinar.minierp.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class DtoCariStatement {
    private Integer cariAccountId;
    private String cariCode;
    private String cariName;
    private BigDecimal openingBalance;
    private BigDecimal totalDebit;
    private BigDecimal totalCredit;
    private BigDecimal closingBalance;
    private List<DtoCariTransaction> transactions;
}