package com.kursaddcinar.minierp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DtoCreateCariTransaction {
    @NotNull
    private Integer cariId;
    
    @NotNull
    private LocalDateTime transactionDate;
    
    @NotNull
    private String transactionType; // ALACAK, BORC (Enum yapılabilir ileride)
    
    @NotNull
    private BigDecimal amount;
    
    private String description;
    private String documentType;
    private String documentNo;
}