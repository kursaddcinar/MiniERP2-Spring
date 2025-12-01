package com.kursaddcinar.minierp.service;

import com.kursaddcinar.minierp.common.ApiResponse;
import com.kursaddcinar.minierp.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface ICariAccountService {

    // --- CariAccount Operations ---
    ApiResponse<Page<DtoCariAccount>> getCariAccounts(Pageable pageable, String searchTerm, Integer typeId);
    ApiResponse<DtoCariAccount> getCariAccountById(Integer id);
    ApiResponse<DtoCariAccount> getCariAccountByCode(String code);
    ApiResponse<DtoCariAccount> createCariAccount(DtoCreateCariAccount createDto);
    ApiResponse<DtoCariAccount> updateCariAccount(Integer id, DtoUpdateCariAccount updateDto);
    ApiResponse<Boolean> deleteCariAccount(Integer id);
    ApiResponse<Boolean> activateCariAccount(Integer id);
    ApiResponse<Boolean> deactivateCariAccount(Integer id);
    ApiResponse<List<DtoCariAccount>> getCustomers();
    ApiResponse<List<DtoCariAccount>> getSuppliers();
    ApiResponse<List<DtoCariBalance>> getCariBalances(boolean includeZeroBalance);

    // --- CariType Operations ---
    ApiResponse<Page<DtoCariType>> getCariTypes(Pageable pageable);
    ApiResponse<DtoCariType> getCariTypeById(Integer id);
    ApiResponse<DtoCariType> createCariType(DtoCreateCariType createDto);
    ApiResponse<DtoCariType> updateCariType(Integer id, DtoUpdateCariType updateDto);
    ApiResponse<Boolean> deleteCariType(Integer id);

    // --- CariTransaction Operations ---
    ApiResponse<Page<DtoCariTransaction>> getCariTransactions(Integer cariId, Pageable pageable);
    ApiResponse<DtoCariTransaction> createCariTransaction(DtoCreateCariTransaction createDto);
    // updateCariBalance -> Bunu Transaction create içinde otomatik yapacağız ama manuel tetiklemek istersen:
    ApiResponse<Boolean> updateCariBalanceManual(Integer cariId, BigDecimal amount, String transactionType);
    ApiResponse<DtoCariStatement> getCariStatement(Integer cariId, LocalDateTime startDate, LocalDateTime endDate);

    // --- Reports and Analytics ---
    ApiResponse<BigDecimal> getTotalReceivables();
    ApiResponse<BigDecimal> getTotalPayables();
    // TopCustomers için SalesInvoice modülü gerektiğinden şimdilik pas geçiyoruz veya boş liste döneceğiz.
}