package com.kursaddcinar.minierp.service;

import com.kursaddcinar.minierp.common.ApiResponse;
import com.kursaddcinar.minierp.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IProductService {

    // --- Product Operations ---
    ApiResponse<Page<DtoProduct>> getProducts(Pageable pageable, String searchTerm, Integer categoryId);
    ApiResponse<DtoProduct> getProductById(Integer id);
    ApiResponse<DtoProduct> getProductByCode(String code);
    ApiResponse<DtoProduct> createProduct(DtoCreateProduct createDto);
    ApiResponse<DtoProduct> updateProduct(Integer id, DtoUpdateProduct updateDto);
    ApiResponse<Boolean> deleteProduct(Integer id);
    ApiResponse<Boolean> activateProduct(Integer id);
    ApiResponse<Boolean> deactivateProduct(Integer id);
    ApiResponse<List<DtoProduct>> getLowStockProducts();

    // --- Product Category Operations ---
    ApiResponse<Page<DtoProductCategory>> getCategories(Pageable pageable);
    ApiResponse<DtoProductCategory> getCategoryById(Integer id);
    ApiResponse<DtoProductCategory> createCategory(DtoCreateProductCategory createDto);
    ApiResponse<DtoProductCategory> updateCategory(Integer id, DtoUpdateProductCategory updateDto);
    ApiResponse<Boolean> deleteCategory(Integer id);

    // --- Unit Operations ---
    ApiResponse<Page<DtoUnit>> getUnits(Pageable pageable);
    ApiResponse<DtoUnit> getUnitById(Integer id);
    ApiResponse<DtoUnit> createUnit(DtoCreateUnit createDto);
    ApiResponse<DtoUnit> updateUnit(Integer id, DtoUpdateUnit updateDto);
    ApiResponse<Boolean> deleteUnit(Integer id);
}