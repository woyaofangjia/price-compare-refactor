package com.bijiabao.repository;

import com.bijiabao.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商品Repository接口
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    /**
     * 根据分类查找商品
     */
    Page<Product> findByCategory(String category, Pageable pageable);
    
    /**
     * 根据状态查找商品
     */
    Page<Product> findByStatus(Integer status, Pageable pageable);
    
    /**
     * 根据品牌ID查找商品
     */
    Page<Product> findByBrandId(Long brandId, Pageable pageable);
    
    /**
     * 查找热门商品
     */
    List<Product> findByIsHotTrueAndStatusOrderByIdDesc(Integer status);
    
    /**
     * 查找降价商品
     */
    List<Product> findByIsDropTrueAndStatusOrderByIdDesc(Integer status);
    
    /**
     * 搜索商品
     */
    @Query("SELECT p FROM Product p WHERE p.title LIKE %:keyword% OR p.desc LIKE %:keyword%")
    Page<Product> searchProducts(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 根据分类和状态查找商品
     */
    @Query("SELECT p FROM Product p WHERE (:category IS NULL OR p.category = :category) AND (:status IS NULL OR p.status = :status)")
    Page<Product> findByCategoryAndStatus(@Param("category") String category, @Param("status") Integer status, Pageable pageable);
    
    /**
     * 获取商品统计信息
     */
    @Query("SELECT COUNT(p) as total, COUNT(CASE WHEN p.status = 1 THEN 1 END) as active, COUNT(CASE WHEN p.isHot = true THEN 1 END) as hot FROM Product p")
    Object getProductStats();
    
    /**
     * 获取分类统计
     */
    @Query("SELECT p.category, COUNT(p) FROM Product p WHERE p.status = 1 GROUP BY p.category")
    List<Object[]> getCategoryStats();
}