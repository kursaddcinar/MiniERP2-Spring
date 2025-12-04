package com.kursaddcinar.minierp.service;

import com.kursaddcinar.minierp.common.ApiResponse;
import com.kursaddcinar.minierp.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface ISalesInvoiceService {
    ApiResponse<Page<DtoSalesInvoice>> getInvoices(Pageable pageable, String status, LocalDateTime startDate, LocalDateTime endDate, Integer cariId);
    ApiResponse<DtoSalesInvoice> getInvoiceById(Integer id);
    ApiResponse<DtoSalesInvoice> getInvoiceByNo(String invoiceNo);
    ApiResponse<DtoSalesInvoice> createInvoice(DtoCreateSalesInvoice createDto);
    ApiResponse<DtoSalesInvoice> updateInvoice(Integer id, DtoUpdateSalesInvoice updateDto);
    ApiResponse<Boolean> deleteInvoice(Integer id);
    ApiResponse<Boolean> approveInvoice(Integer id, DtoInvoiceApproval approvalDto);
    ApiResponse<Boolean> cancelInvoice(Integer id, DtoInvoiceCancellation cancelDto);
    ApiResponse<DtoInvoiceSummary> getInvoiceSummary(LocalDateTime fromDate, LocalDateTime toDate);
    ApiResponse<BigDecimal> getTotalSalesAmount(LocalDateTime fromDate, LocalDateTime toDate);
    ApiResponse<String> generateInvoiceNo();
}