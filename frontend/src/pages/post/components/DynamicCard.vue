<template>
  <div class="dynamic-card" @click="handleCardClick">
    <slot name="extra"></slot>
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
        <div class="interaction-btn" :class="{ active: isLiked }" @click.stop="handleLike">
          <i class="fas fa-heart"></i> <span>{{ likes }}</span>
        </div>
        <div class="interaction-btn" @click.stop="handleComment">
          <i class="fas fa-comment"></i> <span>{{ post.comments }}</span>
        </div>
        <div class="interaction-btn" :class="{ active: isCollected }" @click.stop="handleCollect">
          <i class="fas fa-star"></i> 收藏
        </div>
      </div>
      <div class="interaction-btn"><i class="fas fa-share-alt"></i> 分享</div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const props = defineProps({ post: Object })
const emit = defineEmits(['card-click'])
const router = useRouter()

const isLiked = ref(false)
const isCollected = ref(false)
const likes = ref(props.post.likes || 0)

function handleCardClick() {
  emit('card-click', props.post)
}

function handleLike() {
  isLiked.value = !isLiked.value
  likes.value += isLiked.value ? 1 : -1
}

function handleCollect() {
  isCollected.value = !isCollected.value
}

function handleComment() {
  emit('card-click', props.post)
}
</script>

<style scoped>
@import '../styles/square-page.scss';
.interaction-btn.active i.fa-heart {
  color: #e74c3c;
}
.interaction-btn.active i.fa-star {
  color: #f7b731;
}
</style>
