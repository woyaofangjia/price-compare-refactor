package com.bijiabao.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 收藏实体类
 */
@Entity
@Table(name = "favorites")
@EntityListeners(AuditingEntityListener.class)
public class Favorite {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false, columnDefinition = "BIGINT")
    private Long userId;

    /**
     * 商品ID
     */
    @Column(name = "product_id", nullable = false, columnDefinition = "BIGINT")
    private Long productId;
    
    /**
     * 提醒价格
     */
    @Column(name = "alert_price", precision = 10, scale = 2)
    private BigDecimal alertPrice = BigDecimal.ZERO;
    
    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * 用户实体
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
    
    /**
     * 商品实体
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId2) {
        this.userId = userId2;
    }
    
    public Long getProductId() {
        return productId;
    }
    
    public void setProductId(Long productId2) {
        this.productId = productId2;
    }
    
    public BigDecimal getAlertPrice() {
        return alertPrice;
    }
    
    public void setAlertPrice(BigDecimal alertPrice) {
        this.alertPrice = alertPrice;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }
}