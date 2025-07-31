package com.bijiabao.repository;

import com.bijiabao.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 收藏Repository接口
 */
@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    
    /**
     * 根据用户ID和商品ID查找收藏
     */
    Optional<Favorite> findByUserIdAndProductId(Long userId, Long productId);
    
    /**
     * 检查用户是否收藏了商品
     */
    boolean existsByUserIdAndProductId(Long userId, Long productId);
    
    /**
     * 根据用户ID查找收藏列表
     */
    Page<Favorite> findByUserId(Long userId, Pageable pageable);
    
    /**
     * 根据商品ID查找收藏列表
     */
    List<Favorite> findByProductId(Long productId);
    
    /**
     * 根据用户ID获取收藏数量
     */
    long countByUserId(Long userId);
    
    /**
     * 根据商品ID获取收藏数量
     */
    long countByProductId(Long productId);
    
    /**
     * 删除用户的收藏
     */
    void deleteByUserIdAndProductId(Long userId, Long productId);
    
    /**
     * 获取用户收藏的商品ID列表
     */
    @Query("SELECT f.productId FROM Favorite f WHERE f.userId = :userId")
    List<Long> findProductIdsByUserId(@Param("userId") Long userId);
} 