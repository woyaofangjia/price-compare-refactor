package com.bijiabao.service;

import com.bijiabao.dto.ProductDto;
import com.bijiabao.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 商品服务接口
 */
public interface ProductService {
    
    /**
     * 创建商品
     */
    ProductDto createProduct(Product product);
    
    /**
     * 根据ID查找商品
     */
    Optional<ProductDto> findById(Long id);
    
    /**
     * 更新商品
     */
    ProductDto updateProduct(Long id, Product product);
    
    /**
     * 删除商品
     */
    void deleteProduct(Long id);
    
    /**
     * 分页查询商品
     */
    Page<ProductDto> findAll(Pageable pageable);
    
    /**
     * 根据分类查询商品
     */
    Page<ProductDto> findByCategory(String category, Pageable pageable);
    
    /**
     * 根据状态查询商品
     */
    Page<ProductDto> findByStatus(Integer status, Pageable pageable);
    
    /**
     * 根据品牌查询商品
     */
    Page<ProductDto> findByBrandId(Long brandId, Pageable pageable);
    
    /**
     * 搜索商品
     */
    Page<ProductDto> searchProducts(String keyword, Pageable pageable);
    
    /**
     * 获取热门商品
     */
    List<ProductDto> getHotProducts();
    
    /**
     * 获取降价商品
     */
    List<ProductDto> getDropProducts();
    
    /**
     * 更新商品状态
     */
    ProductDto updateStatus(Long id, Integer status);
    
    /**
     * 更新商品热门状态
     */
    ProductDto updateHotStatus(Long id, Boolean isHot);
    
    /**
     * 更新商品降价状态
     */
    ProductDto updateDropStatus(Long id, Boolean isDrop);
    
    /**
     * 获取商品统计信息
     */
    Map<String, Object> getProductStats();
    
    /**
     * 获取分类统计
     */
    List<Map<String, Object>> getCategoryStats();
    
    /**
     * 获取商品详情（包含价格信息）
     */
    ProductDto getProductDetail(Long id, Long userId);

    Page<ProductDto> findByCategoryAndStatus(String category, Integer status, Pageable pageable);
} 