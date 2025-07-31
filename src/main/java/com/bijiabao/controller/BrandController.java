package com.bijiabao.controller;

import com.bijiabao.dto.BrandDto;
import com.bijiabao.entity.Brand;
import com.bijiabao.service.BrandService;
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
 * 品牌控制器
 */
@Slf4j
@RestController
@RequestMapping("/brands")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class BrandController {
    
    private final BrandService brandService;
    
    /**
     * 创建品牌
     */
    @PostMapping
    public ResponseEntity<BrandDto> createBrand(@RequestBody Brand brand) {
        log.info("创建品牌请求: {}", brand.getName());
        
        try {
            BrandDto createdBrand = brandService.createBrand(brand);
            return ResponseEntity.ok(createdBrand);
        } catch (Exception e) {
            log.error("创建品牌失败", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 获取品牌列表
     */
    @GetMapping
    public ResponseEntity<Page<BrandDto>> getBrands(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("获取品牌列表 - 页码: {}, 大小: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<BrandDto> brands = brandService.findAll(pageable);
        
        return ResponseEntity.ok(brands);
    }
    
    /**
     * 根据ID获取品牌
     */
    @GetMapping("/{id}")
    public ResponseEntity<BrandDto> getBrandById(@PathVariable Long id) {
        log.info("获取品牌信息: {}", id);
        
        return brandService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 根据名称获取品牌
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<BrandDto> getBrandByName(@PathVariable String name) {
        log.info("根据名称获取品牌: {}", name);
        
        return brandService.findByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 更新品牌
     */
    @PutMapping("/{id}")
    public ResponseEntity<BrandDto> updateBrand(@PathVariable Long id, @RequestBody Brand brand) {
        log.info("更新品牌请求: {}", id);
        
        try {
            BrandDto updatedBrand = brandService.updateBrand(id, brand);
            return ResponseEntity.ok(updatedBrand);
        } catch (Exception e) {
            log.error("更新品牌失败", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 删除品牌
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBrand(@PathVariable Long id) {
        log.info("删除品牌请求: {}", id);
        
        try {
            brandService.deleteBrand(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("删除品牌失败", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 搜索品牌
     */
    @GetMapping("/search")
    public ResponseEntity<Page<BrandDto>> searchBrands(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("搜索品牌: {}", keyword);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<BrandDto> brands = brandService.searchBrands(keyword, pageable);
        
        return ResponseEntity.ok(brands);
    }
    
    /**
     * 获取活跃品牌列表
     */
    @GetMapping("/active")
    public ResponseEntity<List<BrandDto>> getActiveBrands() {
        log.info("获取活跃品牌列表");
        
        List<BrandDto> brands = brandService.findByStatus(1);
        return ResponseEntity.ok(brands);
    }
    
    /**
     * 更新品牌状态
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<BrandDto> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        
        Integer status = request.get("status");
        log.info("更新品牌状态: {} -> {}", id, status);
        
        try {
            BrandDto updatedBrand = brandService.updateStatus(id, status);
            return ResponseEntity.ok(updatedBrand);
        } catch (Exception e) {
            log.error("更新品牌状态失败", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 更新品牌排序权重
     */
    @PutMapping("/{id}/sort-weight")
    public ResponseEntity<BrandDto> updateSortWeight(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        
        Integer sortWeight = request.get("sortWeight");
        log.info("更新品牌排序权重: {} -> {}", id, sortWeight);
        
        try {
            BrandDto updatedBrand = brandService.updateSortWeight(id, sortWeight);
            return ResponseEntity.ok(updatedBrand);
        } catch (Exception e) {
            log.error("更新品牌排序权重失败", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 获取品牌统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getBrandStats() {
        log.info("获取品牌统计信息");
        
        Map<String, Object> stats = brandService.getBrandStats();
        return ResponseEntity.ok(stats);
    }
    
    /**
     * 检查品牌名称是否存在
     */
    @GetMapping("/check-name")
    public ResponseEntity<Map<String, Boolean>> checkBrandName(@RequestParam String name) {
        boolean exists = brandService.existsByName(name);
        return ResponseEntity.ok(Map.of("exists", exists));
    }
} 