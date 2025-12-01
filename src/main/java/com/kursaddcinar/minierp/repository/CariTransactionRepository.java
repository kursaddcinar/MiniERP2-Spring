package com.kursaddcinar.minierp.repository;

import com.kursaddcinar.minierp.entity.CariTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CariTransactionRepository extends JpaRepository<CariTransaction, Integer> {
    
    // Belirli bir cariye ait hareketler (Tarih sırasına göre)
    List<CariTransaction> findByCariAccount_CariIdOrderByTransactionDateDesc(Integer cariId);
    
    // Tarih aralığına göre hareketler
    List<CariTransaction> findByCariAccount_CariIdAndTransactionDateBetween(Integer cariId, LocalDateTime startDate, LocalDateTime endDate);
}