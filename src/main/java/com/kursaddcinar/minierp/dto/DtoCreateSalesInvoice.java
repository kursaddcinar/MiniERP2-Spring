package com.kursaddcinar.minierp.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class DtoCreateSalesInvoice {
    
    @NotBlank(message = "Fatura numarası zorunludur")
    private String invoiceNo;
    
    @NotNull(message = "Cari seçimi zorunludur")
    private Integer cariId;
    
    @NotNull(message = "Depo seçimi zorunludur")
    private Integer warehouseId;
    
    @NotNull(message = "Fatura tarihi zorunludur")
    private LocalDateTime invoiceDate;
    
    private LocalDateTime dueDate;
    private String description;
    
    @Valid // İçindeki DTO'ları da validate et
    @NotNull(message = "Fatura kalemleri boş olamaz")
    @Size(min = 1, message = "En az bir ürün eklenmelidir")
    private List<DtoCreateSalesInvoiceDetail> details;
}