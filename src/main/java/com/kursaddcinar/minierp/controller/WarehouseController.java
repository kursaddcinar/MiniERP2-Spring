package com.kursaddcinar.minierp.controller;

import com.kursaddcinar.minierp.common.ApiResponse;
import com.kursaddcinar.minierp.dto.DtoCreateWarehouse;
import com.kursaddcinar.minierp.dto.DtoUpdateWarehouse;
import com.kursaddcinar.minierp.dto.DtoWarehouse;
import com.kursaddcinar.minierp.service.IStockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class WarehouseController {

    private final IStockService stockService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<DtoWarehouse>>> getWarehouses(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(stockService.getWarehouses(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DtoWarehouse>> getWarehouse(@PathVariable Integer id) {
        return ResponseEntity.ok(stockService.getWarehouseById(id));
    }

    @GetMapping("/by-code/{warehouseCode}")
    public ResponseEntity<ApiResponse<DtoWarehouse>> getWarehouseByCode(@PathVariable String warehouseCode) {
        return ResponseEntity.ok(stockService.getWarehouseByCode(warehouseCode));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<DtoWarehouse>> createWarehouse(@Valid @RequestBody DtoCreateWarehouse createDto) {
        return ResponseEntity.ok(stockService.createWarehouse(createDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<DtoWarehouse>> updateWarehouse(
            @PathVariable Integer id,
            @Valid @RequestBody DtoUpdateWarehouse updateDto) {
        return ResponseEntity.ok(stockService.updateWarehouse(id, updateDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteWarehouse(@PathVariable Integer id) {
        return ResponseEntity.ok(stockService.deleteWarehouse(id));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<DtoWarehouse>>> getActiveWarehouses() {
        return ResponseEntity.ok(stockService.getActiveWarehouses());
    }
}