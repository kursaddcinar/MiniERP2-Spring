package com.kursaddcinar.minierp.controller;

import com.kursaddcinar.minierp.common.ApiResponse;
import com.kursaddcinar.minierp.dto.DtoCreateUnit;
import com.kursaddcinar.minierp.dto.DtoUnit;
import com.kursaddcinar.minierp.dto.DtoUpdateUnit;
import com.kursaddcinar.minierp.service.IProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
// import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/units")
@RequiredArgsConstructor
// @PreAuthorize("isAuthenticated()")
public class UnitController {

    private final IProductService productService;

    @GetMapping
    // @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES', 'PURCHASE', 'WAREHOUSE')")
    public ResponseEntity<ApiResponse<Page<DtoUnit>>> getUnits(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(productService.getUnits(pageable));
    }

    @GetMapping("/active")
    // @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES', 'PURCHASE', 'WAREHOUSE')")
    public ResponseEntity<ApiResponse<List<DtoUnit>>> getActiveUnits() {
        return ResponseEntity.ok(ApiResponse.success(productService.getActiveUnits()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DtoUnit>> getUnit(@PathVariable Integer id) {
        return ResponseEntity.ok(productService.getUnitById(id));
    }

    @PostMapping
    // @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<DtoUnit>> createUnit(@Valid @RequestBody DtoCreateUnit createDto) {
        return ResponseEntity.ok(productService.createUnit(createDto));
    }

    @PutMapping("/{id}")
    // @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<DtoUnit>> updateUnit(
            @PathVariable Integer id,
            @Valid @RequestBody DtoUpdateUnit updateDto) {
        return ResponseEntity.ok(productService.updateUnit(id, updateDto));
    }

    @DeleteMapping("/{id}")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteUnit(@PathVariable Integer id) {
        return ResponseEntity.ok(productService.deleteUnit(id));
    }

    @PostMapping("/{id}/activate")
    // @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Boolean>> activateUnit(@PathVariable Integer id) {
        return ResponseEntity.ok(productService.activateUnit(id));
    }

    @PostMapping("/{id}/deactivate")
    // @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Boolean>> deactivateUnit(@PathVariable Integer id) {
        return ResponseEntity.ok(productService.deactivateUnit(id));
    }
}