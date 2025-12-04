package com.kursaddcinar.minierp.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class DtoUpdateSalesInvoice {
    @NotNull
    private Integer cariId;
    
    @NotNull
    private Integer warehouseId;
    
    @NotNull
    private LocalDateTime invoiceDate;
    
    private LocalDateTime dueDate;
    private String description;
    
    @Valid
    @NotNull
    @Size(min = 1, message = "En az bir ürün eklenmelidir")
    private List<DtoCreateSalesInvoiceDetail> details;
}