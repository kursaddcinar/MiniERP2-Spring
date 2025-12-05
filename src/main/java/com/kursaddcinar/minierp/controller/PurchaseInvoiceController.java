package com.kursaddcinar.minierp.controller;

import com.kursaddcinar.minierp.common.ApiResponse;
import com.kursaddcinar.minierp.dto.*;
import com.kursaddcinar.minierp.service.IPurchaseInvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/purchase-invoices")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class PurchaseInvoiceController {

    private final IPurchaseInvoiceService purchaseInvoiceService;

    // --- Listeleme ve Detay ---

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PURCHASE', 'FINANCE')")
    public ResponseEntity<ApiResponse<Page<DtoPurchaseInvoice>>> getInvoices(
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Integer cariId) {
        
        return ResponseEntity.ok(purchaseInvoiceService.getInvoices(pageable, status, startDate, endDate, cariId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PURCHASE', 'FINANCE')")
    public ResponseEntity<ApiResponse<DtoPurchaseInvoice>> getInvoice(@PathVariable Integer id) {
        return ResponseEntity.ok(purchaseInvoiceService.getInvoiceById(id));
    }

    @GetMapping("/by-invoice-no/{invoiceNo}")
    public ResponseEntity<ApiResponse<DtoPurchaseInvoice>> getInvoiceByNo(@PathVariable String invoiceNo) {
        return ResponseEntity.ok(purchaseInvoiceService.getInvoiceByNo(invoiceNo));
    }

    // --- CRUD İşlemleri ---

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PURCHASE')")
    public ResponseEntity<ApiResponse<DtoPurchaseInvoice>> createInvoice(@Valid @RequestBody DtoCreatePurchaseInvoice createDto) {
        return ResponseEntity.ok(purchaseInvoiceService.createInvoice(createDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PURCHASE')")
    public ResponseEntity<ApiResponse<DtoPurchaseInvoice>> updateInvoice(
            @PathVariable Integer id,
            @Valid @RequestBody DtoUpdatePurchaseInvoice updateDto) {
        return ResponseEntity.ok(purchaseInvoiceService.updateInvoice(id, updateDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PURCHASE')")
    public ResponseEntity<ApiResponse<Boolean>> deleteInvoice(@PathVariable Integer id) {
        return ResponseEntity.ok(purchaseInvoiceService.deleteInvoice(id));
    }

    // --- Onay / İptal ---

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PURCHASE')")
    public ResponseEntity<ApiResponse<Boolean>> approveInvoice(
            @PathVariable Integer id,
            @Valid @RequestBody DtoInvoiceApproval approvalDto) {
        return ResponseEntity.ok(purchaseInvoiceService.approveInvoice(id, approvalDto));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Boolean>> cancelInvoice(
            @PathVariable Integer id,
            @Valid @RequestBody DtoInvoiceCancellation cancelDto) {
        return ResponseEntity.ok(purchaseInvoiceService.cancelInvoice(id, cancelDto));
    }

    // --- Raporlama ---

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<DtoInvoiceSummary>> getInvoiceSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate) {
        return ResponseEntity.ok(purchaseInvoiceService.getInvoiceSummary(fromDate, toDate));
    }

    @GetMapping("/total-amount")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalPurchaseAmount(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate) {
        return ResponseEntity.ok(purchaseInvoiceService.getTotalPurchaseAmount(fromDate, toDate));
    }

    @GetMapping("/generate-invoice-no")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<String>> generateInvoiceNo() {
        return ResponseEntity.ok(purchaseInvoiceService.generateInvoiceNo());
    }
}