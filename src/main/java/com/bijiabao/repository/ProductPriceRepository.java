package com.bijiabao.repository;

import com.bijiabao.entity.ProductPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品价格Repository接口
 */
@Repository
public interface ProductPriceRepository extends JpaRepository<ProductPrice, Long> {
    
    /**
     * 根据商品ID查找价格记录
     */
    List<ProductPrice> findByProductIdOrderByPriceDateDesc(Long productId);
    
    /**
     * 根据商品ID和日期范围查找价格记录
     */
    List<ProductPrice> findByProductIdAndPriceDateAfterOrderByPriceDateDesc(Long productId, LocalDateTime startDate);
    
    /**
     * 根据商品ID和平台查找最新价格
     */
    @Query("SELECT pp FROM ProductPrice pp WHERE pp.productId = :productId AND pp.platform = :platform ORDER BY pp.priceDate DESC")
    List<ProductPrice> findLatestByProductIdAndPlatform(@Param("productId") Long productId, @Param("platform") String platform);
    
    /**
     * 获取商品在各平台的最新价格
     */
    @Query("SELECT pp FROM ProductPrice pp WHERE pp.productId = :productId AND pp.priceDate = (SELECT MAX(pp2.priceDate) FROM ProductPrice pp2 WHERE pp2.productId = :productId AND pp2.platform = pp.platform)")
    List<ProductPrice> findLatestPricesByProductId(@Param("productId") Long productId);
    
    /**
     * 获取商品价格统计信息
     */
    @Query("SELECT MIN(pp.price) as minPrice, MAX(pp.price) as maxPrice, AVG(pp.price) as avgPrice FROM ProductPrice pp WHERE pp.productId = :productId")
    Object findPriceStatsByProductId(@Param("productId") Long productId);
} 