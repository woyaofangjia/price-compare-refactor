package com.bijiabao.repository;

import com.bijiabao.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 品牌Repository接口
 */
@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    
    /**
     * 根据名称查找品牌
     */
    Brand findByName(String name);
    
    /**
     * 检查品牌名称是否存在
     */
    boolean existsByName(String name);
    
    /**
     * 根据状态查找品牌
     */
    List<Brand> findByStatusOrderBySortWeightDesc(Integer status);
    
    /**
     * 根据状态分页查找品牌
     */
    Page<Brand> findByStatus(Integer status, Pageable pageable);
    
    /**
     * 搜索品牌
     */
    @Query("SELECT b FROM Brand b WHERE b.name LIKE %:keyword% OR b.description LIKE %:keyword%")
    Page<Brand> searchBrands(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 获取品牌统计信息
     */
    @Query("SELECT COUNT(b) as total, COUNT(CASE WHEN b.status = 1 THEN 1 END) as active FROM Brand b")
    Object getBrandStats();
} 