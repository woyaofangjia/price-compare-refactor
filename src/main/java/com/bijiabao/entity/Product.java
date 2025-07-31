package com.bijiabao.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品实体类
 */
@Entity
@Table(name = "products")
@EntityListeners(AuditingEntityListener.class)
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long brandId;
    
    /**
     * 商品标题
     */
    @Column(nullable = false, length = 255)
    private String title;
    
    /**
     * 商品描述
     */
    @Column(name = "`desc`", columnDefinition = "TEXT")
    private String desc;
    
    /**
     * 商品图片
     */
    @Column(name = "img", length = 255)
    private String img;
    
    /**
     * 商品分类
     */
    @Column(length = 100)
    private String category;
    
    /**
     * 是否热门
     */
    @Column(name = "is_hot")
    private Boolean isHot = false;
    
    /**
     * 是否降价
     */
    @Column(name = "is_drop")
    private Boolean isDrop = false;
    
    /**
     * 商品状态 (1: 上架, 0: 下架)
     */
    @Column(nullable = false)
    private Integer status = 1;
    
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
     * 价格历史
     */
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductPrice> prices;
    
    /**
     * 收藏列表
     */
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Favorite> favorites;
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDesc() {
        return desc;
    }
    
    public void setDesc(String desc) {
        this.desc = desc;
    }
    
    public Object getImg() {
        return img;
    }
    
    public void setImg(String img) {
        this.img = img;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public Boolean getIsHot() {
        return isHot;
    }
    
    public void setIsHot(Boolean isHot) {
        this.isHot = isHot;
    }
    
    public Boolean getIsDrop() {
        return isDrop;
    }
    
    public void setIsDrop(Boolean isDrop) {
        this.isDrop = isDrop;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
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
    
    public List<ProductPrice> getPrices() {
        return prices;
    }
    
    public void setPrices(List<ProductPrice> prices) {
        this.prices = prices;
    }
    
    public List<Favorite> getFavorites() {
        return favorites;
    }
    
    public void setFavorites(List<Favorite> favorites) {
        this.favorites = favorites;
    }

    public String getDescription() {
        return desc;
    }

    public void setDescription(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return img;
    }

    public void setImage(String img) {
        this.img = img;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    private BigDecimal currentPrice;
    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }
    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }
}