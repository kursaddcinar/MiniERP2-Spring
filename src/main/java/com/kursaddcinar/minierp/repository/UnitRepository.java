package com.kursaddcinar.minierp.repository;

import com.kursaddcinar.minierp.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Integer> {
    
    List<Unit> findByIsActiveTrue();
    
    // Product count için DTO'da kullanacağız
    @Query("SELECT u.unitId, COUNT(p) FROM Unit u LEFT JOIN u.products p GROUP BY u.unitId")
    List<Object[]> countProductsByUnit();
}