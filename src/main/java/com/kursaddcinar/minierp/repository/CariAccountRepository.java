package com.kursaddcinar.minierp.repository;

import com.kursaddcinar.minierp.entity.CariAccount;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CariAccountRepository extends JpaRepository<CariAccount, Integer> {

    @EntityGraph(attributePaths = {"type", "cariTransactions"})
    Optional<CariAccount> findByCariCode(String cariCode);


    @EntityGraph(attributePaths = {"type", "cariTransactions"})
    Optional<CariAccount> findByCariId(Integer cariId);

    @Query("SELECT c FROM CariAccount c WHERE c.isActive = true AND (c.type.typeCode = 'MUSTERI' OR c.type.typeCode = 'HERSIKI')")
    List<CariAccount> findCustomers();

    @Query("SELECT c FROM CariAccount c WHERE c.isActive = true AND (c.type.typeCode = 'TEDARIKCI' OR c.type.typeCode = 'HERSIKI')")
    List<CariAccount> findSuppliers();

    @EntityGraph(attributePaths = {"type"})
    List<CariAccount> findByIsActiveTrue();

    // Güncelleme yaparken kendi ID'si hariç kontrol etmek için
    boolean existsByCariCodeAndCariIdNot(String cariCode, Integer cariId);
    
    // Yeni kayıt için direkt kontrol
    boolean existsByCariCode(String cariCode);

    // SearchCariAccountsAsync karşılığı
    @EntityGraph(attributePaths = {"type"})
    List<CariAccount> findByIsActiveTrueAndCariCodeContainingIgnoreCaseOrCariNameContainingIgnoreCase(String cariCode, String cariName);

    // GetCariAccountsWithBalanceAsync (Sıfır bakiyeler hariç)
    @EntityGraph(attributePaths = {"type"})
    List<CariAccount> findByIsActiveTrueAndCurrentBalanceNot(BigDecimal balance);

    // GetTotalReceivablesAsync (Toplam Alacaklar - Pozitif Bakiye)
    @Query("SELECT COALESCE(SUM(c.currentBalance), 0) FROM CariAccount c WHERE c.isActive = true AND c.currentBalance > 0")
    BigDecimal getTotalReceivables();

    // GetTotalPayablesAsync (Toplam Borçlar - Negatif Bakiye)
    @Query("SELECT COALESCE(SUM(ABS(c.currentBalance)), 0) FROM CariAccount c WHERE c.isActive = true AND c.currentBalance < 0")
    BigDecimal getTotalPayables();

    /* TODO: GetTopCustomersAsync metodu SalesInvoice entitisi eklendiğinde açılacak.
       
    @Query("SELECT c FROM CariAccount c " +
           "JOIN c.salesInvoices s " +
           "WHERE c.isActive = true AND (c.type.typeCode = 'MUSTERI' OR c.type.typeCode = 'HERSIKI') " +
           "AND s.invoiceDate BETWEEN :fromDate AND :toDate " +
           "GROUP BY c " +
           "ORDER BY SUM(s.total) DESC")
    List<CariAccount> findTopCustomers(@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate, Pageable pageable);
    */
}