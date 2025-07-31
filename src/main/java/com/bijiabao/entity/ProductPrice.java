package com.bijiabao.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 商品价格实体类
 */
@Entity
@Table(name = "product_prices")
@EntityListeners(AuditingEntityListener.class)
public class ProductPrice {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    /**
     * 商品ID
     */
    @Column(name = "product_id", nullable = false, columnDefinition = "BIGINT")
    private Long productId;
    
    /**
     * 平台
     */
    @Column(nullable = false, length = 50)
    private String platform;
    
    /**
     * 价格
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    /**
     * 价格日期
     */
    @Column(nullable = false)
    private LocalDate date;
    
    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * 商品实体
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;

    /**
     * 价格日期
     */
    private LocalDateTime priceDate;
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Long getProductId() {
        return productId;
    }
    
    public void setProductId(Long productId2) {
        this.productId = productId2;
    }
    
    public String getPlatform() {
        return platform;
    }
    
    public void setPlatform(String platform) {
        this.platform = platform;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }

    public LocalDateTime getPriceDate() {
        return priceDate;
    }

    public void setPriceDate(LocalDateTime priceDate) {
        this.priceDate = priceDate;
    }
}