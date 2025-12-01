package com.kursaddcinar.minierp.controller;

import com.kursaddcinar.minierp.common.ApiResponse;
import com.kursaddcinar.minierp.dto.DtoCariType;
import com.kursaddcinar.minierp.dto.DtoCreateCariType;
import com.kursaddcinar.minierp.dto.DtoUpdateCariType;
import com.kursaddcinar.minierp.service.ICariAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
// import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/cari-types")
@RequiredArgsConstructor
// @PreAuthorize("isAuthenticated()")
public class CariTypeController {

    private final ICariAccountService cariAccountService;

    @GetMapping
    // @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES', 'PURCHASE', 'FINANCE')")
    public ResponseEntity<ApiResponse<Page<DtoCariType>>> getCariTypes(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(cariAccountService.getCariTypes(pageable));
    }

    @GetMapping("/{id}")
    // @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES', 'PURCHASE', 'FINANCE')")
    public ResponseEntity<ApiResponse<DtoCariType>> getCariType(@PathVariable Integer id) {
        return ResponseEntity.ok(cariAccountService.getCariTypeById(id));
    }

    @PostMapping
    // @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<DtoCariType>> createCariType(@Valid @RequestBody DtoCreateCariType createDto) {
        return ResponseEntity.ok(cariAccountService.createCariType(createDto));
    }

    @PutMapping("/{id}")
    // @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<DtoCariType>> updateCariType(
            @PathVariable Integer id,
            @Valid @RequestBody DtoUpdateCariType updateDto) {
        return ResponseEntity.ok(cariAccountService.updateCariType(id, updateDto));
    }

    @DeleteMapping("/{id}")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteCariType(@PathVariable Integer id) {
        return ResponseEntity.ok(cariAccountService.deleteCariType(id));
    }
}