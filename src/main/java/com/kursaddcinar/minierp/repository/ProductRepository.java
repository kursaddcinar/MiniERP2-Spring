package com.kursaddcinar.minierp.repository;

import com.kursaddcinar.minierp.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    
    @EntityGraph(attributePaths = {"category", "unit", "stockCards"})
    Optional<Product> findByProductCode(String productCode);

    @EntityGraph(attributePaths = {"category", "unit", "stockCards", "stockCards.warehouse"})
    Optional<Product> findByProductId(Integer productId);

    @EntityGraph(attributePaths = {"category", "unit"})
    List<Product> findByIsActiveTrueAndCategoryCategoryId(Integer categoryId);

    @EntityGraph(attributePaths = {"category", "unit"})
    List<Product> findByIsActiveTrue();
    
    // GetLowStockProducts (Stok kartlarının toplamı min seviyenin altındaysa)
    @Query("SELECT p FROM Product p JOIN p.stockCards sc " +
           "WHERE p.isActive = true " +
           "GROUP BY p " +
           "HAVING SUM(sc.currentStock) <= p.minStockLevel")
    List<Product> findLowStockProducts();
    
    // GetCriticalStockProductsAsync (Min seviyeden küçükse)
    @Query("SELECT p FROM Product p JOIN p.stockCards sc " +
           "WHERE p.isActive = true " +
           "GROUP BY p " +
           "HAVING SUM(sc.currentStock) < p.minStockLevel")
    List<Product> findCriticalStockProducts();
    
    // IsProductCodeUnique
    boolean existsByProductCode(String productCode);

    // SearchProducts
    @EntityGraph(attributePaths = {"category", "unit"})
    Page<Product> findByIsActiveTrueAndProductCodeContainingIgnoreCaseOrProductNameContainingIgnoreCase(
            String productCode, String productName, Pageable pageable);

    // GetTotalStockValue (Tüm aktif ürünlerin toplam değeri)
    @Query("SELECT COALESCE(SUM(sc.currentStock * p.purchasePrice), 0) " +
           "FROM Product p JOIN p.stockCards sc WHERE p.isActive = true")
    BigDecimal getTotalStockValue();
}