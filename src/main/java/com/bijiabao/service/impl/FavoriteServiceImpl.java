package com.bijiabao.service.impl;

import com.bijiabao.dto.ProductDto;
import com.bijiabao.entity.Favorite;
import com.bijiabao.entity.Product;
import com.bijiabao.repository.FavoriteRepository;
import com.bijiabao.repository.ProductRepository;
import com.bijiabao.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 收藏服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final ProductRepository productRepository;

    @Override
    public boolean addFavorite(Long userId, Long productId) {
        log.info("添加收藏 - 用户ID: {}, 商品ID: {}", userId, productId);

        try {
            // 检查是否已收藏
            if (favoriteRepository.existsByUserIdAndProductId(userId, productId)) {
                log.warn("用户已收藏该商品");
                return false;
            }

            // 检查商品是否存在
            if (!productRepository.existsById(productId)) {
                log.warn("商品不存在: {}", productId);
                return false;
            }

            // 创建收藏记录
            Favorite favorite = new Favorite();
            favorite.setUserId(userId);
            favorite.setProductId(productId);

            favoriteRepository.save(favorite);
            log.info("收藏添加成功");
            return true;

        } catch (Exception e) {
            log.error("添加收藏失败", e);
            return false;
        }
    }

    @Override
    public boolean removeFavorite(Long userId, Long productId) {
        log.info("取消收藏 - 用户ID: {}, 商品ID: {}", userId, productId);

        try {
            favoriteRepository.deleteByUserIdAndProductId(userId, productId);
            log.info("收藏取消成功");
            return true;
        } catch (Exception e) {
            log.error("取消收藏失败", e);
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFavorited(Long userId, Long productId) {
        return favoriteRepository.existsByUserIdAndProductId(userId, productId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> getUserFavorites(Long userId, Pageable pageable) {
        log.info("获取用户收藏列表 - 用户ID: {}", userId);

        Page<Favorite> favorites = favoriteRepository.findByUserId(userId, pageable);

        List<ProductDto> productDtos = favorites.stream()
                .map(favorite -> {
                    Optional<Product> productOpt = productRepository.findById(favorite.getProductId());
                    return productOpt.map(this::convertToDto).orElse(null);
                })
                .filter(productDto -> productDto != null)
                .collect(Collectors.toList());

        return new PageImpl<>(productDtos, pageable, favorites.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public long getFavoriteCount(Long productId) {
        return favoriteRepository.countByProductId(productId);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUserFavoriteCount(Long userId) {
        return favoriteRepository.countByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getUserFavoriteProductIds(Long userId) {
        return favoriteRepository.findProductIdsByUserId(userId);
    }

    /**
     * 转换为DTO
     */
    private ProductDto convertToDto(Product product) {
        return ProductDto.builder()
                .id(product.getId().longValue())
                .title(product.getTitle())
                .description(product.getDescription())
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
}