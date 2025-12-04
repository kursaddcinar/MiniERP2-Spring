package com.kursaddcinar.minierp.repository;

import com.kursaddcinar.minierp.entity.SalesInvoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SalesInvoiceRepository extends JpaRepository<SalesInvoice, Integer> {

    @EntityGraph(attributePaths = {"cariAccount", "warehouse"})
    Page<SalesInvoice> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"cariAccount", "warehouse"})
    List<SalesInvoice> findByStatusOrderByInvoiceDateDesc(String status);

    @EntityGraph(attributePaths = {"cariAccount", "warehouse", "salesInvoiceDetails", "salesInvoiceDetails.product"})
    Optional<SalesInvoice> findByInvoiceNo(String invoiceNo);

    @EntityGraph(attributePaths = {"cariAccount", "warehouse", "salesInvoiceDetails", "salesInvoiceDetails.product", "salesInvoiceDetails.product.unit"})
    Optional<SalesInvoice> findByInvoiceId(Integer invoiceId);

    @EntityGraph(attributePaths = {"cariAccount", "warehouse"})
    List<SalesInvoice> findByCariAccountCariIdOrderByInvoiceDateDesc(Integer cariId);

    @EntityGraph(attributePaths = {"cariAccount", "warehouse"})
    List<SalesInvoice> findByInvoiceDateBetweenOrderByInvoiceDateDesc(LocalDateTime startDate, LocalDateTime endDate);

    boolean existsByInvoiceNo(String invoiceNo);

    // GetTotalSalesAmountAsync
    @Query("SELECT COALESCE(SUM(si.total), 0) FROM SalesInvoice si WHERE si.status = 'APPROVED' AND si.invoiceDate BETWEEN :fromDate AND :toDate")
    BigDecimal getTotalSalesAmount(LocalDateTime fromDate, LocalDateTime toDate);

    long countByStatus(String status);

    @Query("SELECT si.invoiceNo FROM SalesInvoice si WHERE si.invoiceNo LIKE CONCAT(:prefix, '%') ORDER BY si.invoiceNo DESC LIMIT 1")
    String findLastInvoiceNoByPrefix(String prefix);
}