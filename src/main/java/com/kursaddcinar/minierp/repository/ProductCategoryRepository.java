package com.kursaddcinar.minierp.repository;

import com.kursaddcinar.minierp.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Integer> {
    
    List<ProductCategory> findByIsActiveTrue();
    
    @Query("SELECT c.categoryId, COUNT(p) FROM ProductCategory c LEFT JOIN c.products p GROUP BY c.categoryId")
    List<Object[]> countProductsByCategory();
}