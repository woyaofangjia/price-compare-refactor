package com.bijiabao.service.impl;

import com.bijiabao.dto.BrandDto;
import com.bijiabao.entity.Brand;
import com.bijiabao.repository.BrandRepository;
import com.bijiabao.service.BrandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 品牌服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BrandServiceImpl implements BrandService {
    
    private final BrandRepository brandRepository;
    
    @Override
    public BrandDto createBrand(Brand brand) {
        log.info("创建品牌: {}", brand.getName());
        
        Brand savedBrand = brandRepository.save(brand);
        return convertToDto(savedBrand);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<BrandDto> findById(Long id) {
        return brandRepository.findById(id).map(this::convertToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<BrandDto> findByName(String name) {
        Brand brand = brandRepository.findByName(name);
        return Optional.ofNullable(brand).map(this::convertToDto);
    }
    
    @Override
    public BrandDto updateBrand(Long id, Brand brandDetails) {
        log.info("更新品牌: {}", id);
        
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("品牌不存在"));
        
        // 更新字段
        if (brandDetails.getName() != null) {
            brand.setName(brandDetails.getName());
        }
        if (brandDetails.getLogo() != null) {
            brand.setLogo(brandDetails.getLogo());
        }
        if (brandDetails.getDescription() != null) {
            brand.setDescription(brandDetails.getDescription());
        }
        if (brandDetails.getSortWeight() != null) {
            brand.setSortWeight(brandDetails.getSortWeight());
        }
        
        Brand updatedBrand = brandRepository.save(brand);
        return convertToDto(updatedBrand);
    }
    
    @Override
    public void deleteBrand(Long id) {
        log.info("删除品牌: {}", id);
        brandRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<BrandDto> findAll(Pageable pageable) {
        return brandRepository.findAll(pageable).map(this::convertToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BrandDto> findByStatus(Integer status) {
        List<Brand> brands = brandRepository.findByStatusOrderBySortWeightDesc(status);
        return brands.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<BrandDto> searchBrands(String keyword, Pageable pageable) {
        return brandRepository.searchBrands(keyword, pageable).map(this::convertToDto);
    }
    
    @Override
    public BrandDto updateStatus(Long id, Integer status) {
        log.info("更新品牌状态: {} -> {}", id, status);
        
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("品牌不存在"));
        
        brand.setStatus(status);
        Brand updatedBrand = brandRepository.save(brand);
        return convertToDto(updatedBrand);
    }
    
    @Override
    public BrandDto updateSortWeight(Long id, Integer sortWeight) {
        log.info("更新品牌排序权重: {} -> {}", id, sortWeight);
        
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("品牌不存在"));
        
        brand.setSortWeight(sortWeight);
        Brand updatedBrand = brandRepository.save(brand);
        return convertToDto(updatedBrand);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getBrandStats() {
        Object[] stats = (Object[]) brandRepository.getBrandStats();
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", stats[0]);
        result.put("active", stats[1]);
        
        return result;
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return brandRepository.existsByName(name);
    }
    
    /**
     * 转换为DTO
     */
    private BrandDto convertToDto(Brand brand) {
        return BrandDto.builder()
                .id(brand.getId().longValue())
                .name(brand.getName())
                .logo(brand.getLogo())
                .description(brand.getDescription())
                .status(brand.getStatus())
                .sortWeight(brand.getSortWeight())
                .createdAt(brand.getCreatedAt())
                .updatedAt(brand.getUpdatedAt())
                .build();
    }
} 