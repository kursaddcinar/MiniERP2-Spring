package com.kursaddcinar.minierp.controller;

import com.kursaddcinar.minierp.common.ApiResponse;
import com.kursaddcinar.minierp.dto.DtoCreateProductCategory;
import com.kursaddcinar.minierp.dto.DtoProductCategory;
import com.kursaddcinar.minierp.dto.DtoUpdateProductCategory;
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
@RequestMapping("/api/product-categories")
@RequiredArgsConstructor
// @PreAuthorize("isAuthenticated()")
public class ProductCategoryController {

    private final IProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<DtoProductCategory>>> getProductCategories(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(productService.getCategories(pageable));
    }

    @GetMapping("/active")
	 // @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES', 'PURCHASE', 'FINANCE')") 
	 public ResponseEntity<ApiResponse<List<DtoProductCategory>>> getActiveProductCategories() {
	     // Artık Service katmanındaki özel metodu çağırıyoruz
	     List<DtoProductCategory> activeCategories = productService.getActiveProductCategories();
	     return ResponseEntity.ok(ApiResponse.success(activeCategories));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DtoProductCategory>> getProductCategory(@PathVariable Integer id) {
        return ResponseEntity.ok(productService.getCategoryById(id));
    }

    @PostMapping
    // @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<DtoProductCategory>> createProductCategory(@Valid @RequestBody DtoCreateProductCategory createDto) {
        return ResponseEntity.ok(productService.createCategory(createDto));
    }

    @PutMapping("/{id}")
    // @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<DtoProductCategory>> updateProductCategory(
            @PathVariable Integer id,
            @Valid @RequestBody DtoUpdateProductCategory updateDto) {
        return ResponseEntity.ok(productService.updateCategory(id, updateDto));
    }

    @DeleteMapping("/{id}")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteProductCategory(@PathVariable Integer id) {
        return ResponseEntity.ok(productService.deleteCategory(id));
    }

    @PostMapping("/{id}/activate")
    // @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Boolean>> activateProductCategory(@PathVariable Integer id) {
        // Not: IProductService'e activateCategory(id) metodunu eklemen gerekebilir.
        // Veya Update metodunu kullanabiliriz:
        DtoProductCategory category = productService.getCategoryById(id).getData();
        DtoUpdateProductCategory updateDto = new DtoUpdateProductCategory();
        updateDto.setCategoryName(category.getCategoryName());
        updateDto.setDescription(category.getDescription());
        updateDto.setActive(true);
        productService.updateCategory(id, updateDto);
        
        return ResponseEntity.ok(ApiResponse.success(true, "Kategori aktif edildi."));
    }

    @PostMapping("/{id}/deactivate")
    // @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Boolean>> deactivateProductCategory(@PathVariable Integer id) {
        DtoProductCategory category = productService.getCategoryById(id).getData();
        DtoUpdateProductCategory updateDto = new DtoUpdateProductCategory();
        updateDto.setCategoryName(category.getCategoryName());
        updateDto.setDescription(category.getDescription());
        updateDto.setActive(false);
        productService.updateCategory(id, updateDto);

        return ResponseEntity.ok(ApiResponse.success(true, "Kategori pasife alındı."));
    }
}