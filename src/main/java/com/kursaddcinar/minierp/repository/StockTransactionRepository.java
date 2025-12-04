package com.kursaddcinar.minierp.repository;

import com.kursaddcinar.minierp.entity.StockTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockTransactionRepository extends JpaRepository<StockTransaction, Integer> {

    // Temel listelemeler (EntityGraph ile ilişkileri çekiyoruz)
    @EntityGraph(attributePaths = {"product", "product.unit", "warehouse"})
    List<StockTransaction> findByProductProductIdOrderByTransactionDateDesc(Integer productId);
    
    @EntityGraph(attributePaths = {"product", "warehouse"})
    List<StockTransaction> findByWarehouseWarehouseIdOrderByTransactionDateDesc(Integer warehouseId);
    
    @EntityGraph(attributePaths = {"product", "warehouse"})
    List<StockTransaction> findByTransactionDateBetweenOrderByTransactionDateDesc(LocalDateTime startDate, LocalDateTime endDate);
    
    // GetIncoming/Outgoing Quantity (Belirli bir ürün ve depo için toplam giriş/çıkış)
    @Query("SELECT COALESCE(SUM(st.quantity), 0) FROM StockTransaction st " +
           "WHERE st.product.productId = :productId AND st.warehouse.warehouseId = :warehouseId " +
           "AND st.transactionType = :type AND st.transactionDate BETWEEN :fromDate AND :toDate")
    BigDecimal getTotalQuantityByProductAndWarehouseAndTypeAndDateBetween(
            Integer productId, Integer warehouseId, String type, LocalDateTime fromDate, LocalDateTime toDate);
    
    // GetTotalIncomingValue (Tarih aralığındaki toplam giriş tutarı)
    @Query("SELECT COALESCE(SUM(st.totalAmount), 0) FROM StockTransaction st " +
           "WHERE st.transactionType = :type AND st.transactionDate BETWEEN :fromDate AND :toDate")
    BigDecimal getTotalValueByTypeAndDateBetween(String type, LocalDateTime fromDate, LocalDateTime toDate);
    
    // Paged listeleme (Ekstra)
    @EntityGraph(attributePaths = {"product", "warehouse"})
    Page<StockTransaction> findAll(Pageable pageable);
}