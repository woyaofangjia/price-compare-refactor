package com.bijiabao.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 品牌数据传输对象
 */
@Data
@Builder
public class BrandDto {
    
    private Long id;
    private String name;
    private String logo;
    private String description;
    private Integer status;
    private Integer sortWeight;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 扩展字段
    private Integer productCount;
} 