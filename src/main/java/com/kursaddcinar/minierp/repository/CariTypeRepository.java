package com.kursaddcinar.minierp.repository;

import com.kursaddcinar.minierp.entity.CariType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CariTypeRepository extends JpaRepository<CariType, Integer> {
    
    // Aktif türleri getir
    List<CariType> findByIsActiveTrue();
    
    // Cari Sayısını getirmek için (DTO'daki count alanı için)
    @Query("SELECT t.typeId, COUNT(c) FROM CariType t LEFT JOIN t.cariAccounts c GROUP BY t.typeId")
    List<Object[]> countCariAccountsByType();
}