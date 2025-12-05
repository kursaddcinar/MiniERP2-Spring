package com.kursaddcinar.minierp.controller;

import com.kursaddcinar.minierp.common.ApiResponse;
import com.kursaddcinar.minierp.dto.*;
import com.kursaddcinar.minierp.service.IStockService;
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
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class StockCardController {

    private final IStockService stockService;

    // ==========================================
    // STOCK CARD ENDPOINTS
    // ==========================================

    @GetMapping("/cards")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE', 'SALES', 'PURCHASE')")
    public ResponseEntity<ApiResponse<Page<DtoStockCard>>> getStockCards(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(stockService.getStockCards(pageable));
    }

    @GetMapping("/cards/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE', 'SALES', 'PURCHASE')")
    public ResponseEntity<ApiResponse<DtoStockCard>> getStockCard(@PathVariable Integer id) {
        return ResponseEntity.ok(stockService.getStockCardById(id));
    }

    @GetMapping("/cards/by-product-warehouse")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE', 'SALES', 'PURCHASE')")
    public ResponseEntity<ApiResponse<DtoStockCard>> getStockCardByProductAndWarehouse(
            @RequestParam Integer productId, 
            @RequestParam Integer warehouseId) {
        return ResponseEntity.ok(stockService.getStockCardByProductAndWarehouse(productId, warehouseId));
    }

    @GetMapping("/cards/by-product/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE', 'SALES', 'PURCHASE')")
    public ResponseEntity<ApiResponse<List<DtoStockCard>>> getStockCardsByProduct(@PathVariable Integer productId) {
        return ResponseEntity.ok(stockService.getStockCardsByProductId(productId));
    }

    @GetMapping("/cards/by-warehouse/{warehouseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE', 'SALES', 'PURCHASE')")
    public ResponseEntity<ApiResponse<List<DtoStockCard>>> getStockCardsByWarehouse(@PathVariable Integer warehouseId) {
        return ResponseEntity.ok(stockService.getStockCardsByWarehouseId(warehouseId));
    }

    @PostMapping("/cards")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE')")
    public ResponseEntity<ApiResponse<DtoStockCard>> createStockCard(@Valid @RequestBody DtoCreateStockCard createDto) {
        return ResponseEntity.ok(stockService.createStockCard(createDto));
    }

    @PutMapping("/cards/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE')")
    public ResponseEntity<ApiResponse<DtoStockCard>> updateStockCard(
            @PathVariable Integer id,
            @Valid @RequestBody DtoUpdateStockCard updateDto) {
        return ResponseEntity.ok(stockService.updateStockCard(id, updateDto));
    }

    @DeleteMapping("/cards/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteStockCard(@PathVariable Integer id) {
        return ResponseEntity.ok(stockService.deleteStockCard(id));
    }

    // ==========================================
    // STOCK STATUS ENDPOINTS
    // ==========================================

    @GetMapping("/cards/critical")
    public ResponseEntity<ApiResponse<List<DtoStockCard>>> getCriticalStockCards() {
        return ResponseEntity.ok(stockService.getCriticalStockCards());
    }

    @GetMapping("/cards/over-stock")
    public ResponseEntity<ApiResponse<List<DtoStockCard>>> getOverStockCards() {
        return ResponseEntity.ok(stockService.getOverStockCards());
    }

    @GetMapping("/cards/out-of-stock")
    public ResponseEntity<ApiResponse<List<DtoStockCard>>> getOutOfStockCards() {
        return ResponseEntity.ok(stockService.getOutOfStockCards());
    }

    @PostMapping("/update-stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE')")
    public ResponseEntity<ApiResponse<Boolean>> updateStock(@Valid @RequestBody DtoUpdateStock updateDto) {
        return ResponseEntity.ok(stockService.updateStock(
                updateDto.getProductId(), 
                updateDto.getWarehouseId(), 
                updateDto.getQuantity(), 
                updateDto.getTransactionType()));
    }

    @PostMapping("/update-stock-detailed")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE')")
    public ResponseEntity<ApiResponse<Boolean>> updateStockDetailed(@Valid @RequestBody DtoDetailedUpdateStock updateDto) {
        return ResponseEntity.ok(stockService.updateStockWithTransaction(updateDto));
    }

    @PostMapping("/reserve")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE')")
    public ResponseEntity<ApiResponse<Boolean>> reserveStock(@Valid @RequestBody DtoReserveStock reserveDto) {
        return ResponseEntity.ok(stockService.reserveStock(
                reserveDto.getProductId(), 
                reserveDto.getWarehouseId(), 
                reserveDto.getQuantity()));
    }

    @PostMapping("/release-reserved")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Boolean>> releaseReservedStock(@Valid @RequestBody DtoReserveStock reserveDto) {
        return ResponseEntity.ok(stockService.releaseReservedStock(
                reserveDto.getProductId(), 
                reserveDto.getWarehouseId(), 
                reserveDto.getQuantity()));
    }

    // ==========================================
    // STOCK TRANSACTION ENDPOINTS
    // ==========================================

    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse<Page<DtoStockTransaction>>> getStockTransactions(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(stockService.getStockTransactions(pageable));
    }

    @GetMapping("/transactions/{id}")
    public ResponseEntity<ApiResponse<DtoStockTransaction>> getStockTransaction(@PathVariable Integer id) {
        return ResponseEntity.ok(stockService.getStockTransactionById(id));
    }

    @PostMapping("/transactions")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<DtoStockTransaction>> createStockTransaction(
            @Valid @RequestBody DtoCreateStockTransaction createDto) {
        return ResponseEntity.ok(stockService.createStockTransaction(createDto));
    }

    @GetMapping("/transactions/by-product/{productId}")
    public ResponseEntity<ApiResponse<List<DtoStockTransaction>>> getTransactionsByProduct(@PathVariable Integer productId) {
        return ResponseEntity.ok(stockService.getTransactionsByProductId(productId));
    }

    @GetMapping("/transactions/by-warehouse/{warehouseId}")
    public ResponseEntity<ApiResponse<List<DtoStockTransaction>>> getTransactionsByWarehouse(@PathVariable Integer warehouseId) {
        return ResponseEntity.ok(stockService.getTransactionsByWarehouseId(warehouseId));
    }

    @GetMapping("/transactions/by-stockcard/{stockCardId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE', 'SALES', 'PURCHASE')")
    public ResponseEntity<ApiResponse<List<DtoStockTransaction>>> getTransactionsByStockCard(@PathVariable Integer stockCardId) {
        return ResponseEntity.ok(stockService.getTransactionsByStockCardId(stockCardId));
    }

    @GetMapping("/transactions/by-date-range")
    public ResponseEntity<ApiResponse<List<DtoStockTransaction>>> getTransactionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(stockService.getTransactionsByDateRange(startDate, endDate));
    }

    @GetMapping("/transactions/incoming")
    public ResponseEntity<ApiResponse<List<DtoStockTransaction>>> getIncomingTransactions(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate) {
        return ResponseEntity.ok(stockService.getIncomingTransactions(fromDate, toDate));
    }

    @GetMapping("/transactions/outgoing")
    public ResponseEntity<ApiResponse<List<DtoStockTransaction>>> getOutgoingTransactions(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate) {
        return ResponseEntity.ok(stockService.getOutgoingTransactions(fromDate, toDate));
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Boolean>> processStockMovement(@Valid @RequestBody DtoCreateStockMovement movementDto) {
        return ResponseEntity.ok(stockService.processStockMovement(movementDto));
    }

    // ==========================================
    // REPORTS & STATISTICS ENDPOINTS
    // ==========================================

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE', 'SALES', 'PURCHASE')")
    public ResponseEntity<ApiResponse<DtoStockSummary>> getStockSummary() {
        return ResponseEntity.ok(stockService.getStockSummary());
    }

    @GetMapping("/report")
    public ResponseEntity<ApiResponse<DtoStockReport>> getStockReport(
            @RequestParam(required = false) Integer warehouseId,
            @RequestParam(required = false) Integer categoryId) {
        return ResponseEntity.ok(stockService.getStockReport(warehouseId, categoryId));
    }

    @GetMapping("/total-value")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalStockValue() {
        return ResponseEntity.ok(stockService.getTotalStockValue());
    }

    @GetMapping("/total-value/by-warehouse/{warehouseId}")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalStockValueByWarehouse(@PathVariable Integer warehouseId) {
        return ResponseEntity.ok(stockService.getTotalStockValueByWarehouse(warehouseId));
    }

    @GetMapping("/incoming-value")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalIncomingValue(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate) {
        return ResponseEntity.ok(stockService.getTotalIncomingValue(fromDate, toDate));
    }

    @GetMapping("/outgoing-value")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalOutgoingValue(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate) {
        return ResponseEntity.ok(stockService.getTotalOutgoingValue(fromDate, toDate));
    }
}