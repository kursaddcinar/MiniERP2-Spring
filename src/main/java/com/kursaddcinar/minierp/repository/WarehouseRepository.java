package com.kursaddcinar.minierp.repository;

import com.kursaddcinar.minierp.entity.Warehouse;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Integer> {

    @EntityGraph(attributePaths = {"stockCards", "stockCards.product"})
    Optional<Warehouse> findByWarehouseCode(String warehouseCode);

    // GetActiveWarehouses karşılığı
    List<Warehouse> findByIsActiveTrueOrderByWarehouseName();

    // IsWarehouseCodeUnique karşılığı
    boolean existsByWarehouseCode(String warehouseCode);
    
    // SearchWarehouses karşılığı
    List<Warehouse> findByIsActiveTrueAndWarehouseCodeContainingIgnoreCaseOrWarehouseNameContainingIgnoreCaseOrderByWarehouseName(
        String warehouseCode, String warehouseName);

    @Query("SELECT COALESCE(SUM(sc.currentStock * sc.product.purchasePrice), 0) FROM StockCard sc WHERE sc.warehouse.warehouseId = :warehouseId")
    BigDecimal getTotalStockValueByWarehouseId(Integer warehouseId);
    
    @Query("SELECT COUNT(sc) FROM StockCard sc WHERE sc.warehouse.warehouseId = :warehouseId")
    int countProductsByWarehouseId(Integer warehouseId);
}