package com.kursaddcinar.minierp.controller;

import com.kursaddcinar.minierp.common.ApiResponse;
import com.kursaddcinar.minierp.dto.*;
import com.kursaddcinar.minierp.service.ICariAccountService;
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
@RequestMapping("/api/cari-accounts")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()") // Class level authorization
public class CariAccountController {

    private final ICariAccountService cariAccountService;

    // ==========================================
    // CARI ACCOUNT ENDPOINTS
    // ==========================================

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES', 'PURCHASE', 'FINANCE')")
    public ResponseEntity<ApiResponse<Page<DtoCariAccount>>> getCariAccounts(
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) Integer typeId) {
        
        return ResponseEntity.ok(cariAccountService.getCariAccounts(pageable, searchTerm, typeId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES', 'PURCHASE', 'FINANCE')")
    public ResponseEntity<ApiResponse<DtoCariAccount>> getCariAccount(@PathVariable Integer id) {
        return ResponseEntity.ok(cariAccountService.getCariAccountById(id));
    }

    @GetMapping("/by-code/{code}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES', 'PURCHASE', 'FINANCE')")
    public ResponseEntity<ApiResponse<DtoCariAccount>> getCariAccountByCode(@PathVariable String code) {
        return ResponseEntity.ok(cariAccountService.getCariAccountByCode(code));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES', 'PURCHASE')")
    public ResponseEntity<ApiResponse<DtoCariAccount>> createCariAccount(@Valid @RequestBody DtoCreateCariAccount createDto) {
        return ResponseEntity.ok(cariAccountService.createCariAccount(createDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES', 'PURCHASE')")
    public ResponseEntity<ApiResponse<DtoCariAccount>> updateCariAccount(
            @PathVariable Integer id,
            @Valid @RequestBody DtoUpdateCariAccount updateDto) {
        return ResponseEntity.ok(cariAccountService.updateCariAccount(id, updateDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES', 'PURCHASE')")
    public ResponseEntity<ApiResponse<Boolean>> deleteCariAccount(@PathVariable Integer id) {
        return ResponseEntity.ok(cariAccountService.deleteCariAccount(id));
    }

    @PostMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Boolean>> activateCariAccount(@PathVariable Integer id) {
        return ResponseEntity.ok(cariAccountService.activateCariAccount(id));
    }

    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Boolean>> deactivateCariAccount(@PathVariable Integer id) {
        return ResponseEntity.ok(cariAccountService.deactivateCariAccount(id));
    }

    @GetMapping("/customers")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES')")
    public ResponseEntity<ApiResponse<List<DtoCariAccount>>> getCustomers() {
        return ResponseEntity.ok(cariAccountService.getCustomers());
    }

    @GetMapping("/suppliers")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PURCHASE')")
    public ResponseEntity<ApiResponse<List<DtoCariAccount>>> getSuppliers() {
        return ResponseEntity.ok(cariAccountService.getSuppliers());
    }

    @GetMapping("/balances")
    public ResponseEntity<ApiResponse<List<DtoCariBalance>>> getCariBalances(
            @RequestParam(defaultValue = "false") boolean includeZeroBalance) {
        return ResponseEntity.ok(cariAccountService.getCariBalances(includeZeroBalance));
    }

    // ==========================================
    // CARI TRANSACTION ENDPOINTS
    // ==========================================

    @GetMapping("/{cariId}/transactions")
    public ResponseEntity<ApiResponse<Page<DtoCariTransaction>>> getCariTransactions(
            @PathVariable Integer cariId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(cariAccountService.getCariTransactions(cariId, pageable));
    }

    @GetMapping("/{cariId}/statement")
    public ResponseEntity<ApiResponse<DtoCariStatement>> getCariStatement(
            @PathVariable Integer cariId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(cariAccountService.getCariStatement(cariId, startDate, endDate));
    }

    @PostMapping("/transactions")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'FINANCE')")
    public ResponseEntity<ApiResponse<DtoCariTransaction>> createCariTransaction(
            @Valid @RequestBody DtoCreateCariTransaction createDto) {
        return ResponseEntity.ok(cariAccountService.createCariTransaction(createDto));
    }

    @PostMapping("/{cariId}/update-balance")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Boolean>> updateCariBalance(
            @PathVariable Integer cariId,
            @RequestParam BigDecimal amount,
            @RequestParam String transactionType) {
        return ResponseEntity.ok(cariAccountService.updateCariBalanceManual(cariId, amount, transactionType));
    }

    // ==========================================
    // REPORTS AND ANALYTICS
    // ==========================================

    @GetMapping("/reports/total-receivables")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalReceivables() {
        return ResponseEntity.ok(cariAccountService.getTotalReceivables());
    }

    @GetMapping("/reports/total-payables")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalPayables() {
        return ResponseEntity.ok(cariAccountService.getTotalPayables());
    }

    /* NOT: getTopCustomers servisi henüz aktif olmadığı için bu endpointi de kapalı tutuyoruz.
    @GetMapping("/reports/top-customers")
    public ResponseEntity<ApiResponse<List<DtoCariAccount>>> getTopCustomers(...) { ... }
    */
}