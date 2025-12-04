package com.kursaddcinar.minierp.controller;

import com.kursaddcinar.minierp.common.ApiResponse;
import com.kursaddcinar.minierp.dto.DtoCreateProduct;
import com.kursaddcinar.minierp.dto.DtoProduct;
import com.kursaddcinar.minierp.dto.DtoUpdateProduct;
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
@RequestMapping("/api/products")
@RequiredArgsConstructor
// @PreAuthorize("isAuthenticated()")
public class ProductController {

    private final IProductService productService;

    // --- Product Endpoints ---

    @GetMapping
    // @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES', 'PURCHASE', 'WAREHOUSE')")
    public ResponseEntity<ApiResponse<Page<DtoProduct>>> getProducts(
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) Integer categoryId) {
        
        return ResponseEntity.ok(productService.getProducts(pageable, searchTerm, categoryId));
    }

    @GetMapping("/{id}")
    // @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES', 'PURCHASE', 'WAREHOUSE')")
    public ResponseEntity<ApiResponse<DtoProduct>> getProduct(@PathVariable Integer id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/by-code/{code}")
    // @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES', 'PURCHASE', 'WAREHOUSE')")
    public ResponseEntity<ApiResponse<DtoProduct>> getProductByCode(@PathVariable String code) {
        return ResponseEntity.ok(productService.getProductByCode(code));
    }

    @PostMapping
    // @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<DtoProduct>> createProduct(@Valid @RequestBody DtoCreateProduct createDto) {
        return ResponseEntity.ok(productService.createProduct(createDto));
    }

    @PutMapping("/{id}")
    // @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<DtoProduct>> updateProduct(
            @PathVariable Integer id,
            @Valid @RequestBody DtoUpdateProduct updateDto) {
        return ResponseEntity.ok(productService.updateProduct(id, updateDto));
    }

    @DeleteMapping("/{id}")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteProduct(@PathVariable Integer id) {
        return ResponseEntity.ok(productService.deleteProduct(id));
    }

    @PostMapping("/{id}/activate")
    // @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Boolean>> activateProduct(@PathVariable Integer id) {
        return ResponseEntity.ok(productService.activateProduct(id));
    }

    @PostMapping("/{id}/deactivate")
    // @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Boolean>> deactivateProduct(@PathVariable Integer id) {
        return ResponseEntity.ok(productService.deactivateProduct(id));
    }

    @GetMapping("/low-stock")
    // @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES', 'PURCHASE', 'WAREHOUSE')")
    public ResponseEntity<ApiResponse<List<DtoProduct>>> getLowStockProducts() {
        return ResponseEntity.ok(productService.getLowStockProducts());
    }
}