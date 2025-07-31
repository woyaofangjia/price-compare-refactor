package com.bijiabao.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品数据传输对象
 */
@Data
@Builder
public class ProductDto {
    
    private Long id;
    private String title;
    private String description;
    private Object image;
    private String category;
    private Long brandId;
    private Boolean isHot;
    private Boolean isDrop;
    private Integer status;
    private BigDecimal currentPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 扩展字段
    private List<String> platforms;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private BigDecimal avgPrice;
    private Integer favoriteCount;
    private Boolean isFavorited;
} 