package com.kursaddcinar.minierp.repository;

import com.kursaddcinar.minierp.entity.PurchaseInvoice;
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
public interface PurchaseInvoiceRepository extends JpaRepository<PurchaseInvoice, Integer> {

    // GetPagedWithCountAsync ve search/filter mantığı
    // EntityGraph ile ilişkili tabloları (Cari, Depo) tek sorguda çekiyoruz
    @EntityGraph(attributePaths = {"cariAccount", "warehouse"})
    Page<PurchaseInvoice> findAll(Pageable pageable);

    // Filter by Status (Draft, Approved)
    @EntityGraph(attributePaths = {"cariAccount", "warehouse"})
    List<PurchaseInvoice> findByStatusOrderByInvoiceDateDesc(String status);

    // GetByInvoiceNoAsync
    @EntityGraph(attributePaths = {"cariAccount", "warehouse", "purchaseInvoiceDetails", "purchaseInvoiceDetails.product"})
    Optional<PurchaseInvoice> findByInvoiceNo(String invoiceNo);

    // GetInvoiceWithDetailsAsync (ID ile detaylı çekim)
    @EntityGraph(attributePaths = {"cariAccount", "warehouse", "purchaseInvoiceDetails", "purchaseInvoiceDetails.product", "purchaseInvoiceDetails.product.unit"})
    Optional<PurchaseInvoice> findByInvoiceId(Integer invoiceId);

    // GetInvoicesByCariAsync
    @EntityGraph(attributePaths = {"cariAccount", "warehouse"})
    List<PurchaseInvoice> findByCariAccountCariIdOrderByInvoiceDateDesc(Integer cariId);

    // GetInvoicesByDateRangeAsync
    @EntityGraph(attributePaths = {"cariAccount", "warehouse"})
    List<PurchaseInvoice> findByInvoiceDateBetweenOrderByInvoiceDateDesc(LocalDateTime startDate, LocalDateTime endDate);

    // IsInvoiceNoUniqueAsync
    boolean existsByInvoiceNo(String invoiceNo);

    // GetTotalPurchaseAmountAsync (Sadece onaylı faturalar)
    @Query("SELECT COALESCE(SUM(pi.total), 0) FROM PurchaseInvoice pi WHERE pi.status = 'APPROVED' AND pi.invoiceDate BETWEEN :fromDate AND :toDate")
    BigDecimal getTotalPurchaseAmount(LocalDateTime fromDate, LocalDateTime toDate);

    // GetInvoiceCountAsync
    long countByStatus(String status); // status null ise count() kullanılır service tarafında

    // GenerateInvoiceNoAsync için son numarayı bulma
    @Query("SELECT pi.invoiceNo FROM PurchaseInvoice pi WHERE pi.invoiceNo LIKE CONCAT(:prefix, '%') ORDER BY pi.invoiceNo DESC LIMIT 1")
    String findLastInvoiceNoByPrefix(String prefix);
}