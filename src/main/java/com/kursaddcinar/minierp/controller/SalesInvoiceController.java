package com.kursaddcinar.minierp.controller;

import com.kursaddcinar.minierp.common.ApiResponse;
import com.kursaddcinar.minierp.dto.*;
import com.kursaddcinar.minierp.service.ISalesInvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
// import org.springframework.security.access.prepost.PreAuthorize;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/sales-invoices")
@RequiredArgsConstructor
// @PreAuthorize("isAuthenticated()")
public class SalesInvoiceController {

    private final ISalesInvoiceService salesInvoiceService;

    // --- Listeleme ve Detay ---

    @GetMapping
    // @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES', 'FINANCE')")
    public ResponseEntity<ApiResponse<Page<DtoSalesInvoice>>> getInvoices(
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Integer cariId) {
        
        return ResponseEntity.ok(salesInvoiceService.getInvoices(pageable, status, startDate, endDate, cariId));
    }

    @GetMapping("/{id}")
    // @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES', 'FINANCE')")
    public ResponseEntity<ApiResponse<DtoSalesInvoice>> getInvoice(@PathVariable Integer id) {
        return ResponseEntity.ok(salesInvoiceService.getInvoiceById(id));
    }

    @GetMapping("/by-invoice-no/{invoiceNo}")
    public ResponseEntity<ApiResponse<DtoSalesInvoice>> getInvoiceByNo(@PathVariable String invoiceNo) {
        return ResponseEntity.ok(salesInvoiceService.getInvoiceByNo(invoiceNo));
    }

    // --- CRUD İşlemleri ---

    @PostMapping
    // @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES')")
    public ResponseEntity<ApiResponse<DtoSalesInvoice>> createInvoice(@Valid @RequestBody DtoCreateSalesInvoice createDto) {
        return ResponseEntity.ok(salesInvoiceService.createInvoice(createDto));
    }

    @PutMapping("/{id}")
    // @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES')")
    public ResponseEntity<ApiResponse<DtoSalesInvoice>> updateInvoice(
            @PathVariable Integer id,
            @Valid @RequestBody DtoUpdateSalesInvoice updateDto) {
        return ResponseEntity.ok(salesInvoiceService.updateInvoice(id, updateDto));
    }

    @DeleteMapping("/{id}")
    // @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES')")
    public ResponseEntity<ApiResponse<Boolean>> deleteInvoice(@PathVariable Integer id) {
        return ResponseEntity.ok(salesInvoiceService.deleteInvoice(id));
    }

    // --- Onay / İptal ---

    @PostMapping("/{id}/approve")
    // @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES')")
    public ResponseEntity<ApiResponse<Boolean>> approveInvoice(
            @PathVariable Integer id,
            @Valid @RequestBody DtoInvoiceApproval approvalDto) {
        return ResponseEntity.ok(salesInvoiceService.approveInvoice(id, approvalDto));
    }

    @PostMapping("/{id}/cancel")
    // @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Boolean>> cancelInvoice(
            @PathVariable Integer id,
            @Valid @RequestBody DtoInvoiceCancellation cancelDto) {
        return ResponseEntity.ok(salesInvoiceService.cancelInvoice(id, cancelDto));
    }

    // --- Raporlama ---

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<DtoInvoiceSummary>> getInvoiceSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate) {
        return ResponseEntity.ok(salesInvoiceService.getInvoiceSummary(fromDate, toDate));
    }

    @GetMapping("/total-amount")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalSalesAmount(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate) {
        return ResponseEntity.ok(salesInvoiceService.getTotalSalesAmount(fromDate, toDate));
    }
}