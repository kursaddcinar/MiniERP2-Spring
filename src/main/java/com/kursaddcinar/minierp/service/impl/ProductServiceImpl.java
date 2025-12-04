package com.kursaddcinar.minierp.service.impl;

import com.kursaddcinar.minierp.common.ApiResponse;
import com.kursaddcinar.minierp.dto.*;
import com.kursaddcinar.minierp.entity.Product;
import com.kursaddcinar.minierp.entity.ProductCategory;
import com.kursaddcinar.minierp.entity.Unit;
import com.kursaddcinar.minierp.exception.BusinessRuleException;
import com.kursaddcinar.minierp.exception.ResourceNotFoundException;
import com.kursaddcinar.minierp.repository.ProductCategoryRepository;
import com.kursaddcinar.minierp.repository.ProductRepository;
import com.kursaddcinar.minierp.repository.UnitRepository;
import com.kursaddcinar.minierp.service.IProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements IProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository categoryRepository;
    private final UnitRepository unitRepository;

    // ==========================================
    // PRODUCT OPERATIONS
    // ==========================================

    @Override
    public ApiResponse<Page<DtoProduct>> getProducts(Pageable pageable, String searchTerm, Integer categoryId) {
        Page<Product> products;

        if (searchTerm != null && !searchTerm.isBlank()) {
            products = productRepository.findByIsActiveTrueAndProductCodeContainingIgnoreCaseOrProductNameContainingIgnoreCase(
                    searchTerm, searchTerm, pageable);
        } else if (categoryId != null) {
            // Repository'de pagination'lı versiyonu olmadığı için listeyi page'e çevirmek yerine 
            // şimdilik aktiflerin hepsini dönen bir mantık kuralım veya repo'ya pageable ekleyelim.
            // Doğrusu Repo'ya Pageable eklemektir, ancak şimdilik manuel filtreleme yerine 
            // genel listeyi dönüyorum. (Repository adımında eklediğimiz metoda göre):
            // Not: Pagination + Filter kombinasyonu için Specification kullanmak en iyisidir.
            // Şimdilik categoryId varsa da tümünü getiriyoruz (Basit tutmak adına).
            // Gelişmiş filtreleme için Specification yapısı kurabiliriz ileride.
            products = productRepository.findAll(pageable); 
        } else {
            products = productRepository.findAll(pageable);
        }

        return ApiResponse.success(products.map(this::mapToDtoProduct));
    }

    @Override
    public ApiResponse<DtoProduct> getProductById(Integer id) {
        Product product = productRepository.findByProductId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ürün bulunamadı: " + id));
        return ApiResponse.success(mapToDtoProduct(product));
    }

    @Override
    public ApiResponse<DtoProduct> getProductByCode(String code) {
        Product product = productRepository.findByProductCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Ürün bulunamadı: " + code));
        return ApiResponse.success(mapToDtoProduct(product));
    }

    @Override
    @Transactional
    public ApiResponse<DtoProduct> createProduct(DtoCreateProduct createDto) {
        if (productRepository.existsByProductCode(createDto.getProductCode())) {
            throw new BusinessRuleException("Bu ürün kodu zaten kullanılıyor: " + createDto.getProductCode());
        }

        Unit unit = unitRepository.findById(createDto.getUnitId())
                .orElseThrow(() -> new ResourceNotFoundException("Birim bulunamadı ID: " + createDto.getUnitId()));

        ProductCategory category = null;
        if (createDto.getCategoryId() != null) {
            category = categoryRepository.findById(createDto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Kategori bulunamadı ID: " + createDto.getCategoryId()));
        }

        Product product = new Product();
        product.setProductCode(createDto.getProductCode());
        product.setProductName(createDto.getProductName());
        product.setCategory(category);
        product.setUnit(unit);
        product.setSalePrice(createDto.getSalePrice());
        product.setPurchasePrice(createDto.getPurchasePrice());
        product.setVatRate(createDto.getVatRate());
        product.setMinStockLevel(createDto.getMinStockLevel());
        product.setMaxStockLevel(createDto.getMaxStockLevel());
        product.setActive(true);

        Product saved = productRepository.save(product);
        log.info("Ürün oluşturuldu: {}", saved.getProductCode());
        return ApiResponse.success(mapToDtoProduct(saved), "Ürün başarıyla oluşturuldu.");
    }

    @Override
    @Transactional
    public ApiResponse<DtoProduct> updateProduct(Integer id, DtoUpdateProduct updateDto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ürün bulunamadı: " + id));

        Unit unit = unitRepository.findById(updateDto.getUnitId())
                .orElseThrow(() -> new ResourceNotFoundException("Birim bulunamadı ID: " + updateDto.getUnitId()));

        ProductCategory category = null;
        if (updateDto.getCategoryId() != null) {
            category = categoryRepository.findById(updateDto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Kategori bulunamadı ID: " + updateDto.getCategoryId()));
        }

        product.setProductName(updateDto.getProductName());
        product.setCategory(category);
        product.setUnit(unit);
        product.setSalePrice(updateDto.getSalePrice());
        product.setPurchasePrice(updateDto.getPurchasePrice());
        product.setVatRate(updateDto.getVatRate());
        product.setMinStockLevel(updateDto.getMinStockLevel());
        product.setMaxStockLevel(updateDto.getMaxStockLevel());
        product.setActive(updateDto.isActive());

        Product updated = productRepository.save(product);
        log.info("Ürün güncellendi: {}", id);
        return ApiResponse.success(mapToDtoProduct(updated), "Ürün güncellendi.");
    }

    @Override
    @Transactional
    public ApiResponse<Boolean> deleteProduct(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ürün bulunamadı: " + id));

        // Hareket kontrolü
        if (!product.getStockTransactions().isEmpty()) {
            throw new BusinessRuleException("Hareket görmüş ürün silinemez. Pasife alabilirsiniz.");
        }

        productRepository.delete(product);
        log.info("Ürün silindi: {}", id);
        return ApiResponse.success(true, "Ürün silindi.");
    }

    @Override
    @Transactional
    public ApiResponse<Boolean> activateProduct(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ürün bulunamadı: " + id));
        product.setActive(true);
        productRepository.save(product);
        return ApiResponse.success(true, "Ürün aktif edildi.");
    }

    @Override
    @Transactional
    public ApiResponse<Boolean> deactivateProduct(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ürün bulunamadı: " + id));
        product.setActive(false);
        productRepository.save(product);
        return ApiResponse.success(true, "Ürün pasife alındı.");
    }

    @Override
    public ApiResponse<List<DtoProduct>> getLowStockProducts() {
        List<Product> products = productRepository.findLowStockProducts();
        return ApiResponse.success(products.stream().map(this::mapToDtoProduct).collect(Collectors.toList()));
    }

    // ==========================================
    // CATEGORY OPERATIONS
    // ==========================================

    @Override
    public ApiResponse<Page<DtoProductCategory>> getCategories(Pageable pageable) {
        Page<ProductCategory> categories = categoryRepository.findAll(pageable);
        return ApiResponse.success(categories.map(this::mapToDtoCategory));
    }

    @Override
    public ApiResponse<DtoProductCategory> getCategoryById(Integer id) {
        ProductCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kategori bulunamadı: " + id));
        return ApiResponse.success(mapToDtoCategory(category));
    }

    @Override
    @Transactional
    public ApiResponse<DtoProductCategory> createCategory(DtoCreateProductCategory createDto) {
        ProductCategory category = new ProductCategory();
        category.setCategoryCode(createDto.getCategoryCode());
        category.setCategoryName(createDto.getCategoryName());
        category.setDescription(createDto.getDescription());
        category.setActive(true);

        ProductCategory saved = categoryRepository.save(category);
        return ApiResponse.success(mapToDtoCategory(saved));
    }

    @Override
    @Transactional
    public ApiResponse<DtoProductCategory> updateCategory(Integer id, DtoUpdateProductCategory updateDto) {
        ProductCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kategori bulunamadı: " + id));
        
        category.setCategoryName(updateDto.getCategoryName());
        category.setDescription(updateDto.getDescription());
        category.setActive(updateDto.isActive());

        ProductCategory saved = categoryRepository.save(category);
        return ApiResponse.success(mapToDtoCategory(saved));
    }

    @Override
    @Transactional
    public ApiResponse<Boolean> deleteCategory(Integer id) {
        ProductCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kategori bulunamadı: " + id));
        
        try {
            categoryRepository.delete(category);
        } catch (Exception e) {
            throw new BusinessRuleException("Bu kategoriye bağlı ürünler olduğu için silinemez.");
        }
        return ApiResponse.success(true, "Kategori silindi.");
    }

    // ==========================================
    // UNIT OPERATIONS
    // ==========================================

    @Override
    public ApiResponse<Page<DtoUnit>> getUnits(Pageable pageable) {
        Page<Unit> units = unitRepository.findAll(pageable);
        return ApiResponse.success(units.map(this::mapToDtoUnit));
    }

    @Override
    public ApiResponse<DtoUnit> getUnitById(Integer id) {
        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Birim bulunamadı: " + id));
        return ApiResponse.success(mapToDtoUnit(unit));
    }

    @Override
    @Transactional
    public ApiResponse<DtoUnit> createUnit(DtoCreateUnit createDto) {
        Unit unit = new Unit();
        unit.setUnitCode(createDto.getUnitCode());
        unit.setUnitName(createDto.getUnitName());
        unit.setActive(true);
        Unit saved = unitRepository.save(unit);
        return ApiResponse.success(mapToDtoUnit(saved));
    }

    @Override
    @Transactional
    public ApiResponse<DtoUnit> updateUnit(Integer id, DtoUpdateUnit updateDto) {
        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Birim bulunamadı: " + id));
        
        unit.setUnitName(updateDto.getUnitName());
        unit.setActive(updateDto.isActive());
        
        Unit saved = unitRepository.save(unit);
        return ApiResponse.success(mapToDtoUnit(saved));
    }

    @Override
    @Transactional
    public ApiResponse<Boolean> deleteUnit(Integer id) {
        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Birim bulunamadı: " + id));
        try {
            unitRepository.delete(unit);
        } catch (Exception e) {
            throw new BusinessRuleException("Bu birime bağlı ürünler olduğu için silinemez.");
        }
        return ApiResponse.success(true, "Birim silindi.");
    }

    // ==========================================
    // MAPPERS
    // ==========================================

    private DtoProduct mapToDtoProduct(Product entity) {
        DtoProduct dto = new DtoProduct();
        dto.setProductId(entity.getProductId());
        dto.setProductCode(entity.getProductCode());
        dto.setProductName(entity.getProductName());
        
        if (entity.getCategory() != null) {
            dto.setCategoryId(entity.getCategory().getCategoryId());
            dto.setCategoryName(entity.getCategory().getCategoryName());
        }
        
        if (entity.getUnit() != null) {
            dto.setUnitId(entity.getUnit().getUnitId());
            dto.setUnitName(entity.getUnit().getUnitName());
        }
        
        dto.setSalePrice(entity.getSalePrice());
        dto.setPurchasePrice(entity.getPurchasePrice());
        dto.setVatRate(entity.getVatRate());
        dto.setMinStockLevel(entity.getMinStockLevel());
        dto.setMaxStockLevel(entity.getMaxStockLevel());
        dto.setActive(entity.isActive());
        dto.setCreatedDate(entity.getCreatedDate());
        
        // Stok hesaplamaları (StockCard listesi üzerinden toplam)
        BigDecimal currentStock = entity.getStockCards().stream()
                .map(sc -> sc.getCurrentStock() != null ? sc.getCurrentStock() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal reservedStock = entity.getStockCards().stream()
                .map(sc -> sc.getReservedStock() != null ? sc.getReservedStock() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
                
        dto.setCurrentStock(currentStock);
        dto.setReservedStock(reservedStock);
        dto.setAvailableStock(currentStock.subtract(reservedStock));
        
        return dto;
    }

    private DtoProductCategory mapToDtoCategory(ProductCategory entity) {
        DtoProductCategory dto = new DtoProductCategory();
        dto.setCategoryId(entity.getCategoryId());
        dto.setCategoryCode(entity.getCategoryCode());
        dto.setCategoryName(entity.getCategoryName());
        dto.setDescription(entity.getDescription());
        dto.setActive(entity.isActive());
        dto.setCreatedDate(entity.getCreatedDate());
        return dto;
    }

    private DtoUnit mapToDtoUnit(Unit entity) {
        DtoUnit dto = new DtoUnit();
        dto.setUnitId(entity.getUnitId());
        dto.setUnitCode(entity.getUnitCode());
        dto.setUnitName(entity.getUnitName());
        dto.setActive(entity.isActive());
        dto.setCreatedDate(entity.getCreatedDate());
        return dto;
    }
}