<template>
  <div class="dynamic-card">
    <div class="dynamic-header">
      <img :src="post.userAvatar" class="user-avatar" alt="用户头像" />
      <div class="user-info">
        <div class="username">{{ post.username }}</div>
        <div class="post-time">{{ post.time }}</div>
      </div>
    </div>
    <div class="dynamic-content">
      <div class="content-text">{{ post.content }}</div>
      <div class="images-grid" v-if="post.images && post.images.length">
        <img v-for="(img, index) in post.images" :key="index" :src="img" class="image-item" />
      </div>
      <div class="product-card" v-if="post.product">
        <div class="product-info">
          <img :src="post.product.image" class="product-image" />
          <div class="product-details">
            <div class="product-name">{{ post.product.name }}</div>
            <div class="product-price">{{ post.product.price }}</div>
            <div class="product-platform">{{ post.product.platform }}</div>
          </div>
        </div>
      </div>
    </div>
    <div class="dynamic-footer">
      <div class="interaction-bar">
        <div class="interaction-btn"><i class="fas fa-heart"></i> <span>{{ post.likes }}</span></div>
        <div class="interaction-btn"><i class="fas fa-comment"></i> <span>{{ post.comments }}</span></div>
        <div class="interaction-btn"><i class="fas fa-star"></i> 收藏</div>
      </div>
      <div class="interaction-btn"><i class="fas fa-share-alt"></i> 分享</div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  post: Object
});
</script>

<style scoped>
@import '../styles/square-page.scss';
</style>
