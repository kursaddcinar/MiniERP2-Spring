package com.kursaddcinar.minierp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DtoInvoiceApproval {
    @NotNull
    private Integer invoiceId;
    private String approvalNote;
}