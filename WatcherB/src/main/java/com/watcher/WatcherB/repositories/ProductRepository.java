package com.watcher.WatcherB.repositories;

import com.watcher.WatcherB.models.Data.Goods.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;


public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Query("SELECT p FROM Product p " +
            "WHERE (:style IS NULL OR p.styleType.id = :style) " +
            "AND (:brand IS NULL OR p.brand.id = :brand) " +
            "AND (:caseType IS NULL OR p.caseType.id = :caseType) " +
            "AND (:glassType IS NULL OR p.glassType.id = :glassType) " +
            "AND (:waterproofMin IS NULL OR p.waterproof >= :waterproofMin) " +
            "AND (:waterproofMax IS NULL OR p.waterproof <= :waterproofMax) " +
            "AND (:priceMin IS NULL OR p.price >= :priceMin) " +
            "AND (:priceMax IS NULL OR p.price <= :priceMax) " +
            "AND (:inStock IS NULL OR (:inStock = TRUE AND p.stockQuantity > 0) OR (:inStock = FALSE AND p.stockQuantity = 0))")
    Page<Product> findWithFilters(
            @Param("style") Integer style,
            @Param("brand") Integer brand,
            @Param("caseType") Integer caseType,
            @Param("glassType") Integer glassType,
            @Param("waterproofMin") Integer waterproofMin,
            @Param("waterproofMax") Integer waterproofMax,
            @Param("inStock") Boolean inStock,
            @Param("priceMin") Float priceMin,
            @Param("priceMax") Float priceMax,
            Pageable pageable);

}