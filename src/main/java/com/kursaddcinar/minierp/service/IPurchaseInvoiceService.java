package com.kursaddcinar.minierp.service;

import com.kursaddcinar.minierp.common.ApiResponse;
import com.kursaddcinar.minierp.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface IPurchaseInvoiceService {
    ApiResponse<Page<DtoPurchaseInvoice>> getInvoices(Pageable pageable, String status, LocalDateTime startDate, LocalDateTime endDate, Integer cariId);
    ApiResponse<DtoPurchaseInvoice> getInvoiceById(Integer id);
    ApiResponse<DtoPurchaseInvoice> getInvoiceByNo(String invoiceNo);
    ApiResponse<DtoPurchaseInvoice> createInvoice(DtoCreatePurchaseInvoice createDto);
    ApiResponse<DtoPurchaseInvoice> updateInvoice(Integer id, DtoUpdatePurchaseInvoice updateDto);
    ApiResponse<Boolean> deleteInvoice(Integer id);
    ApiResponse<Boolean> approveInvoice(Integer id, DtoInvoiceApproval approvalDto);
    ApiResponse<Boolean> cancelInvoice(Integer id, DtoInvoiceCancellation cancelDto);
    ApiResponse<DtoInvoiceSummary> getInvoiceSummary(LocalDateTime fromDate, LocalDateTime toDate);
    ApiResponse<BigDecimal> getTotalPurchaseAmount(LocalDateTime fromDate, LocalDateTime toDate);
    ApiResponse<String> generateInvoiceNo();
}