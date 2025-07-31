package com.bijiabao.service.impl;

import com.bijiabao.dto.ProductDto;
import com.bijiabao.entity.Product;
import com.bijiabao.repository.ProductRepository;
import com.bijiabao.service.ProductService;
import com.bijiabao.service.PriceHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 商品服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {
    
    private final ProductRepository productRepository;
    private final PriceHistoryService priceHistoryService;
    
    @Override
    public ProductDto createProduct(Product product) {
        log.info("创建商品: {}", product.getTitle());
        
        Product savedProduct = productRepository.save(product);
        return convertToDto(savedProduct);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ProductDto> findById(Long id) {
        return productRepository.findById(id).map(this::convertToDto);
    }
    
    @Override
    public ProductDto updateProduct(Long id, Product productDetails) {
        log.info("更新商品: {}", id);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在"));
        
        // 更新字段
        if (productDetails.getTitle() != null) {
            product.setTitle(productDetails.getTitle());
        }
        if (productDetails.getDescription() != null) {
            product.setDescription(productDetails.getDescription());
        }
        if (productDetails.getImage() != null) {
            product.setImage(productDetails.getImage());
        }
        if (productDetails.getCategory() != null) {
            product.setCategory(productDetails.getCategory());
        }
        if (productDetails.getBrandId() != null) {
            product.setBrandId(productDetails.getBrandId());
        }
        if (productDetails.getCurrentPrice() != null) {
            product.setCurrentPrice(productDetails.getCurrentPrice());
        }
        
        Product updatedProduct = productRepository.save(product);
        return convertToDto(updatedProduct);
    }
    
    @Override
    public void deleteProduct(Long id) {
        log.info("删除商品: {}", id);
        productRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> findAll(Pageable pageable) {
        return productRepository.findAll(pageable).map(this::convertToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> findByCategory(String category, Pageable pageable) {
        return productRepository.findByCategory(category, pageable).map(this::convertToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> findByStatus(Integer status, Pageable pageable) {
        return productRepository.findByStatus(status, pageable).map(this::convertToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> findByBrandId(Long brandId, Pageable pageable) {
        return productRepository.findByBrandId(brandId, pageable).map(this::convertToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> searchProducts(String keyword, Pageable pageable) {
        return productRepository.searchProducts(keyword, pageable).map(this::convertToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getHotProducts() {
        List<Product> hotProducts = productRepository.findByIsHotTrueAndStatusOrderByIdDesc(1);
        return hotProducts.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getDropProducts() {
        List<Product> dropProducts = productRepository.findByIsDropTrueAndStatusOrderByIdDesc(1);
        return dropProducts.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    @Override
    public ProductDto updateStatus(Long id, Integer status) {
        log.info("更新商品状态: {} -> {}", id, status);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在"));
        
        product.setStatus(status);
        Product updatedProduct = productRepository.save(product);
        return convertToDto(updatedProduct);
    }
    
    @Override
    public ProductDto updateHotStatus(Long id, Boolean isHot) {
        log.info("更新商品热门状态: {} -> {}", id, isHot);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在"));
        
        product.setIsHot(isHot);
        Product updatedProduct = productRepository.save(product);
        return convertToDto(updatedProduct);
    }
    
    @Override
    public ProductDto updateDropStatus(Long id, Boolean isDrop) {
        log.info("更新商品降价状态: {} -> {}", id, isDrop);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在"));
        
        product.setIsDrop(isDrop);
        Product updatedProduct = productRepository.save(product);
        return convertToDto(updatedProduct);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getProductStats() {
        Object[] stats = (Object[]) productRepository.getProductStats();
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", stats[0]);
        result.put("active", stats[1]);
        result.put("hot", stats[2]);
        
        return result;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getCategoryStats() {
        List<Object[]> categoryStats = productRepository.getCategoryStats();
        
        return categoryStats.stream().map(stat -> {
            Map<String, Object> category = new HashMap<>();
            category.put("category", stat[0]);
            category.put("count", stat[1]);
            return category;
        }).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public ProductDto getProductDetail(Long id, Long userId) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在"));
        
        ProductDto productDto = convertToDto(product);
        
        // 添加价格信息
        try {
            List<Double> prices = priceHistoryService.getPriceHistory(id);
            if (!prices.isEmpty()) {
                productDto.setMinPrice(BigDecimal.valueOf(prices.stream().mapToDouble(Double::doubleValue).min().orElse(0.0)));
                productDto.setMaxPrice(BigDecimal.valueOf(prices.stream().mapToDouble(Double::doubleValue).max().orElse(0.0)));
                productDto.setAvgPrice(BigDecimal.valueOf(prices.stream().mapToDouble(Double::doubleValue).average().orElse(0.0)));
            }
        } catch (Exception e) {
            log.warn("获取商品价格信息失败: {}", e.getMessage());
        }
        
        return productDto;
    }
    
    /**
     * 转换为DTO
     */
    private ProductDto convertToDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .title(product.getTitle())
                .description(product.getDesc())
                .image(product.getImage())
                .category(product.getCategory())
                .brandId(product.getBrandId())
                .isHot(product.getIsHot())
                .isDrop(product.getIsDrop())
                .status(product.getStatus())
                .currentPrice(product.getCurrentPrice())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    @Override
    public Page<ProductDto> findByCategoryAndStatus(String category, Integer status, Pageable pageable) {
        log.info("根据分类和状态查询商品: 分类={}, 状态={}", category, status);
    return productRepository.findByCategoryAndStatus(category, status, pageable).map(this::convertToDto);
    }
}