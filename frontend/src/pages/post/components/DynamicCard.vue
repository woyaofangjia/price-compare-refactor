<template>
  <div class="dynamic-card" @click="handleCardClick">
    <slot name="extra"></slot>
    <div class="dynamic-header">
      <img 
        :src="avatarUrl" 
        class="user-avatar" 
        alt="用户头像"
        @error="handleAvatarError"
      />
      <div class="user-info">
        <div class="username">{{ post.username }}</div>
        <div class="post-time">{{ post.time }}</div>
      </div>
      <!-- 编辑按钮 -->
      <div v-if="canEdit" class="edit-actions">
        <button class="edit-btn" @click.stop="handleEdit">
          <i class="fas fa-edit"></i>
        </button>
        <button class="delete-btn" @click.stop="handleDelete">
          <i class="fas fa-trash"></i>
        </button>
      </div>
    </div>
    <div class="dynamic-content">
      <div class="content-text">{{ post.content }}</div>
      <div class="image-grid" v-if="post.images && post.images.length > 0">
        <img 
          v-for="(img, index) in post.images" 
          :key="index" 
          :src="getFullImageUrl(img)" 
          class="content-image" 
          :alt="`图片${index + 1}`"
        />
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
        <div class="interaction-btn" :class="{ active: post.isLiked }" @click.stop="handleLike">
          <i class="fas fa-heart"></i> <span>{{ post.likes || 0 }}</span>
        </div>
        <div class="interaction-btn" @click.stop="handleComment">
          <i class="fas fa-comment"></i> <span>{{ post.comments || 0 }}</span>
        </div>
        <div class="interaction-btn" :class="{ active: post.isCollected }" @click.stop="handleCollect">
          <i class="fas fa-star"></i> 收藏
        </div>
      </div>
      <div class="interaction-btn"><i class="fas fa-share-alt"></i> 分享</div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { getAvatarUrl, handleAvatarError } from '@/utils/avatar.js'

const props = defineProps({ 
  post: Object 
})

const emit = defineEmits(['card-click', 'like', 'collect', 'edit', 'delete'])
const router = useRouter()

const canEdit = computed(() => {
  const currentUser = JSON.parse(localStorage.getItem('user') || '{}')
  return currentUser.id === props.post.userId
})

// 计算头像URL
const avatarUrl = computed(() => {
  return getAvatarUrl(props.post.userAvatar)
})

function handleCardClick() {
  emit('card-click', props.post)
}

function handleLike() {
  emit('like', props.post.id)
}

function handleCollect() {
  emit('collect', props.post.id)
}

function handleComment() {
  emit('card-click', props.post)
}

function handleEdit() {
  router.push(`/post/create?edit=${props.post.id}`)
}

function handleDelete() {
  if (confirm('确定要删除这条动态吗？')) {
    emit('delete', props.post.id)
  }
}

function getFullImageUrl(url) {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return 'http://localhost:3000' + url
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

.edit-actions {
  display: flex;
  gap: 8px;
  margin-left: auto;
}

.edit-btn, .delete-btn {
  background: none;
  border: none;
  padding: 4px 8px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.edit-btn {
  color: #3498db;
}

.edit-btn:hover {
  background: rgba(52, 152, 219, 0.1);
}

.delete-btn {
  color: #e74c3c;
}

.delete-btn:hover {
  background: rgba(231, 76, 60, 0.1);
}

.dynamic-header {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.image-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 10px;
  margin-bottom: 15px;
}

.content-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
  border-radius: 8px;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.content-image:hover {
  transform: scale(1.05);
}
</style>
