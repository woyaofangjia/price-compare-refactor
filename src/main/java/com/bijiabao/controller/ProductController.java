package com.bijiabao.controller;

import com.bijiabao.dto.ProductDto;
import com.bijiabao.entity.Product;
import com.bijiabao.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 商品控制器
 */
@Slf4j
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class ProductController {
    
    private final ProductService productService;
    
    /**
     * 创建商品
     */
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestBody Product product) {
        log.info("创建商品请求: {}", product.getTitle());
        
        try {
            ProductDto createdProduct = productService.createProduct(product);
            return ResponseEntity.ok(createdProduct);
        } catch (Exception e) {
            log.error("创建商品失败", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 获取商品列表
     */
    @GetMapping
    public ResponseEntity<Page<ProductDto>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer status) {
        
        log.info("获取商品列表 - 页码: {}, 大小: {}, 分类: {}, 状态: {}", page, size, category, status);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDto> products;
        
        if (category != null && status != null) {
            products = productService.findByCategoryAndStatus(category, status, pageable);
        } else if (category != null) {
            products = productService.findByCategory(category, pageable);
        } else if (status != null) {
            products = productService.findByStatus(status, pageable);
        } else {
            products = productService.findAll(pageable);
        }
        
        return ResponseEntity.ok(products);
    }
    
    /**
     * 根据ID获取商品
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        log.info("获取商品信息: {}", id);
        
        return productService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 获取商品详情
     */
    @GetMapping("/{id}/detail")
    public ResponseEntity<ProductDto> getProductDetail(
            @PathVariable Long id,
            @RequestParam(required = false) Long userId) {
        
        log.info("获取商品详情: {}, 用户ID: {}", id, userId);
        
        try {
            ProductDto productDetail = productService.getProductDetail(id, userId);
            return ResponseEntity.ok(productDetail);
        } catch (Exception e) {
            log.error("获取商品详情失败", e);
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 更新商品
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        log.info("更新商品请求: {}", id);
        
        try {
            ProductDto updatedProduct = productService.updateProduct(id, product);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            log.error("更新商品失败", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 删除商品
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        log.info("删除商品请求: {}", id);
        
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("删除商品失败", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 搜索商品
     */
    @GetMapping("/search")
    public ResponseEntity<Page<ProductDto>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("搜索商品: {}", keyword);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDto> products = productService.searchProducts(keyword, pageable);
        
        return ResponseEntity.ok(products);
    }
    
    /**
     * 获取热门商品
     */
    @GetMapping("/hot")
    public ResponseEntity<List<ProductDto>> getHotProducts() {
        log.info("获取热门商品");
        
        List<ProductDto> hotProducts = productService.getHotProducts();
        return ResponseEntity.ok(hotProducts);
    }
    
    /**
     * 获取降价商品
     */
    @GetMapping("/drop")
    public ResponseEntity<List<ProductDto>> getDropProducts() {
        log.info("获取降价商品");
        
        List<ProductDto> dropProducts = productService.getDropProducts();
        return ResponseEntity.ok(dropProducts);
    }
    
    /**
     * 根据品牌获取商品
     */
    @GetMapping("/brand/{brandId}")
    public ResponseEntity<Page<ProductDto>> getProductsByBrand(
            @PathVariable Long brandId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("获取品牌商品: {}", brandId);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDto> products = productService.findByBrandId(brandId, pageable);
        
        return ResponseEntity.ok(products);
    }
    
    /**
     * 更新商品状态
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<ProductDto> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        
        Integer status = request.get("status");
        log.info("更新商品状态: {} -> {}", id, status);
        
        try {
            ProductDto updatedProduct = productService.updateStatus(id, status);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            log.error("更新商品状态失败", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 更新商品热门状态
     */
    @PutMapping("/{id}/hot")
    public ResponseEntity<ProductDto> updateHotStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> request) {
        
        Boolean isHot = request.get("isHot");
        log.info("更新商品热门状态: {} -> {}", id, isHot);
        
        try {
            ProductDto updatedProduct = productService.updateHotStatus(id, isHot);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            log.error("更新商品热门状态失败", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 更新商品降价状态
     */
    @PutMapping("/{id}/drop")
    public ResponseEntity<ProductDto> updateDropStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> request) {
        
        Boolean isDrop = request.get("isDrop");
        log.info("更新商品降价状态: {} -> {}", id, isDrop);
        
        try {
            ProductDto updatedProduct = productService.updateDropStatus(id, isDrop);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            log.error("更新商品降价状态失败", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 获取商品统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getProductStats() {
        log.info("获取商品统计信息");
        
        Map<String, Object> stats = productService.getProductStats();
        return ResponseEntity.ok(stats);
    }
    
    /**
     * 获取分类统计
     */
    @GetMapping("/category-stats")
    public ResponseEntity<List<Map<String, Object>>> getCategoryStats() {
        log.info("获取分类统计");
        
        List<Map<String, Object>> categoryStats = productService.getCategoryStats();
        return ResponseEntity.ok(categoryStats);
    }
} 