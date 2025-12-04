package com.kursaddcinar.minierp.repository;

import com.kursaddcinar.minierp.entity.PurchaseInvoiceDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseInvoiceDetailRepository extends JpaRepository<PurchaseInvoiceDetail, Integer> {
    // Fatura güncellenirken eski detayları silmek için kullanılabilir
    void deleteByPurchaseInvoiceInvoiceId(Integer invoiceId);
}