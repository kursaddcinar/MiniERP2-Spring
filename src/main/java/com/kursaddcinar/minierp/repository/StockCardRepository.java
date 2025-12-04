package com.kursaddcinar.minierp.repository;

import com.kursaddcinar.minierp.entity.StockCard;
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
public interface StockCardRepository extends JpaRepository<StockCard, Integer> {

    // GetByProductAndWarehouse
    @EntityGraph(attributePaths = {"product", "product.unit", "warehouse"})
    Optional<StockCard> findByProductProductIdAndWarehouseWarehouseId(Integer productId, Integer warehouseId);

    @EntityGraph(attributePaths = {"product", "warehouse"})
    List<StockCard> findByProductProductId(Integer productId);

    @EntityGraph(attributePaths = {"product", "warehouse"})
    List<StockCard> findByWarehouseWarehouseId(Integer warehouseId);

    @Query("SELECT sc FROM StockCard sc JOIN FETCH sc.product p " +
           "WHERE sc.currentStock <= p.minStockLevel AND p.minStockLevel > 0")
    List<StockCard> findCriticalStockCards();

    @Query("SELECT sc FROM StockCard sc JOIN FETCH sc.product p " +
           "WHERE sc.currentStock >= p.maxStockLevel AND p.maxStockLevel > 0")
    List<StockCard> findOverStockCards();

    List<StockCard> findByCurrentStockLessThanEqual(BigDecimal amount);

    // GetPagedWithCount (Search destekli)
    @EntityGraph(attributePaths = {"product", "product.unit", "warehouse"})
    Page<StockCard> findByProductProductNameContainingIgnoreCaseOrProductProductCodeContainingIgnoreCaseOrWarehouseWarehouseNameContainingIgnoreCase(
            String productName, String productCode, String warehouseName, Pageable pageable);
}