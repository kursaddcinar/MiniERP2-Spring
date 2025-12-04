package com.kursaddcinar.minierp.repository;

import com.kursaddcinar.minierp.entity.SalesInvoiceDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesInvoiceDetailRepository extends JpaRepository<SalesInvoiceDetail, Integer> {
    void deleteBySalesInvoiceInvoiceId(Integer invoiceId);
}