package com.kursaddcinar.minierp.service;

import com.kursaddcinar.minierp.common.ApiResponse;
import com.kursaddcinar.minierp.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface IStockService {

    // --- StockCard Operations ---
    ApiResponse<Page<DtoStockCard>> getStockCards(Pageable pageable);
    ApiResponse<DtoStockCard> getStockCardById(Integer id);
    ApiResponse<DtoStockCard> getStockCardByProductAndWarehouse(Integer productId, Integer warehouseId);
    ApiResponse<List<DtoStockCard>> getStockCardsByProductId(Integer productId);
    ApiResponse<List<DtoStockCard>> getStockCardsByWarehouseId(Integer warehouseId);
    ApiResponse<DtoStockCard> createStockCard(DtoCreateStockCard createDto);
    ApiResponse<DtoStockCard> updateStockCard(Integer id, DtoUpdateStockCard updateDto);
    ApiResponse<Boolean> deleteStockCard(Integer id);

    // --- Stock Status Operations ---
    ApiResponse<List<DtoStockCard>> getCriticalStockCards();
    ApiResponse<List<DtoStockCard>> getOverStockCards();
    ApiResponse<List<DtoStockCard>> getOutOfStockCards();
    ApiResponse<Boolean> updateStock(Integer productId, Integer warehouseId, BigDecimal quantity, String transactionType);
    ApiResponse<Boolean> updateStockWithTransaction(DtoDetailedUpdateStock updateDto);
    ApiResponse<Boolean> reserveStock(Integer productId, Integer warehouseId, BigDecimal quantity);
    ApiResponse<Boolean> releaseReservedStock(Integer productId, Integer warehouseId, BigDecimal quantity);

    // --- StockTransaction Operations ---
    ApiResponse<Page<DtoStockTransaction>> getStockTransactions(Pageable pageable);
    ApiResponse<DtoStockTransaction> getStockTransactionById(Integer id);
    ApiResponse<DtoStockTransaction> createStockTransaction(DtoCreateStockTransaction createDto);
    ApiResponse<List<DtoStockTransaction>> getTransactionsByProductId(Integer productId);
    ApiResponse<List<DtoStockTransaction>> getTransactionsByWarehouseId(Integer warehouseId);
    ApiResponse<List<DtoStockTransaction>> getTransactionsByStockCardId(Integer stockCardId); // Bu metod .NET'te yoktu ama mantıken olmalı, ekleyelim mi? (Yoksa pass geçebilirsin)
    ApiResponse<List<DtoStockTransaction>> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    ApiResponse<List<DtoStockTransaction>> getIncomingTransactions(LocalDateTime fromDate, LocalDateTime toDate);
    ApiResponse<List<DtoStockTransaction>> getOutgoingTransactions(LocalDateTime fromDate, LocalDateTime toDate);

    // --- Stock Movement (Transfer) ---
    ApiResponse<Boolean> processStockMovement(DtoCreateStockMovement movementDto);

    // --- Warehouse Operations ---
    ApiResponse<Page<DtoWarehouse>> getWarehouses(Pageable pageable);
    ApiResponse<DtoWarehouse> getWarehouseById(Integer id);
    ApiResponse<DtoWarehouse> getWarehouseByCode(String warehouseCode);
    ApiResponse<DtoWarehouse> createWarehouse(DtoCreateWarehouse createDto);
    ApiResponse<DtoWarehouse> updateWarehouse(Integer id, DtoUpdateWarehouse updateDto);
    ApiResponse<Boolean> deleteWarehouse(Integer id);
    ApiResponse<List<DtoWarehouse>> getActiveWarehouses();

    // --- Reports and Statistics ---
    ApiResponse<DtoStockSummary> getStockSummary();
    ApiResponse<DtoStockReport> getStockReport(Integer warehouseId, Integer categoryId);
    ApiResponse<BigDecimal> getTotalStockValue();
    ApiResponse<BigDecimal> getTotalStockValueByWarehouse(Integer warehouseId);
    ApiResponse<BigDecimal> getTotalIncomingValue(LocalDateTime fromDate, LocalDateTime toDate);
    ApiResponse<BigDecimal> getTotalOutgoingValue(LocalDateTime fromDate, LocalDateTime toDate);
}