package com.bijiabao.service;

import com.bijiabao.dto.BrandDto;
import com.bijiabao.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 品牌服务接口
 */
public interface BrandService {
    
    /**
     * 创建品牌
     */
    BrandDto createBrand(Brand brand);
    
    /**
     * 根据ID查找品牌
     */
    Optional<BrandDto> findById(Long id);
    
    /**
     * 根据名称查找品牌
     */
    Optional<BrandDto> findByName(String name);
    
    /**
     * 更新品牌
     */
    BrandDto updateBrand(Long id, Brand brand);
    
    /**
     * 删除品牌
     */
    void deleteBrand(Long id);
    
    /**
     * 分页查询品牌
     */
    Page<BrandDto> findAll(Pageable pageable);
    
    /**
     * 根据状态查询品牌
     */
    List<BrandDto> findByStatus(Integer status);
    
    /**
     * 搜索品牌
     */
    Page<BrandDto> searchBrands(String keyword, Pageable pageable);
    
    /**
     * 更新品牌状态
     */
    BrandDto updateStatus(Long id, Integer status);
    
    /**
     * 更新品牌排序权重
     */
    BrandDto updateSortWeight(Long id, Integer sortWeight);
    
    /**
     * 获取品牌统计信息
     */
    Map<String, Object> getBrandStats();
    
    /**
     * 检查品牌名称是否存在
     */
    boolean existsByName(String name);
} 