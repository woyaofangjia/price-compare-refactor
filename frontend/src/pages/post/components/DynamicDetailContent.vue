<template>
  <div>
    <div class="detail-header">
      <img :src="post.userAvatar" class="user-avatar" />
      <div class="user-info">
        <div class="username">{{ post.username }}</div>
        <div class="post-time">{{ post.time }}</div>
      </div>
    </div>
    <div class="detail-content">
      <div class="content-text">{{ post.content }}</div>
      <div v-if="post.images && post.images.length" class="images-grid">
        <img v-for="(img, idx) in post.images" :key="idx" :src="img" class="detail-image" />
      </div>
      <div v-if="post.product" class="product-card">
        <img :src="post.product.image" class="product-image-large" />
        <div class="product-info">
          <div class="product-name">{{ post.product.name }}</div>
          <div class="product-price">{{ post.product.price }}</div>
          <div class="product-platform">{{ post.product.platform }}</div>
          <div class="product-detail" v-if="post.product.detail">{{ post.product.detail }}</div>
        </div>
      </div>
    </div>
    <div class="detail-footer">
      <button class="btn btn-outline" :class="{active: isLiked}" @click="$emit('like')"><i class="fas fa-heart"></i> {{ likes }}</button>
      <button class="btn btn-outline" :class="{active: isCollected}" @click="$emit('collect')"><i class="fas fa-star"></i> {{ isCollected ? '已收藏' : '收藏' }}</button>
    </div>
    <div class="comments-section">
      <div class="card-header"><div class="card-title">评论 ({{ comments.length }})</div></div>
      <div class="comment-item" v-for="(c, index) in comments" :key="index">
        <img :src="c.avatar || 'https://randomuser.me/api/portraits/lego/1.jpg'" class="comment-avatar" />
        <div class="comment-content">
          <div class="comment-header">
            <div class="comment-author">{{ c.author }}</div>
            <div class="comment-time">{{ c.time }}</div>
          </div>
          <div class="comment-text">{{ c.text }}</div>
        </div>
        <button 
          v-if="canDeleteComment(c)" 
          class="delete-comment-btn" 
          @click="$emit('delete-comment', index)"
        >
          删除
        </button>
      </div>
      <div class="comment-form">
        <textarea v-model="newComment" class="form-control" placeholder="写下你的评论..."></textarea>
        <button class="btn btn-primary" @click="submitComment"><i class="fas fa-paper-plane"></i> 发表评论</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
const props = defineProps({
  post: Object,
  comments: Array,
  isLiked: Boolean,
  isCollected: Boolean,
  likes: Number,
  currentUser: Object // 当前登录用户信息
})
const emit = defineEmits(['like', 'collect', 'submit-comment', 'delete-comment'])
const newComment = ref('')

function submitComment() {
  if (newComment.value.trim()) {
    emit('submit-comment', newComment.value)
    newComment.value = ''
  }
}

// 判断是否可以删除评论
function canDeleteComment(comment) {
  if (!props.currentUser) return false
  
  // 评论作者可以删除自己的评论
  if (comment.author === props.currentUser.username) return true
  
  // 动态作者可以删除自己动态下的所有评论
  if (props.post.username === props.currentUser.username) return true
  
  return false
}
</script>

<style scoped>
.detail-header {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}
.user-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  margin-right: 16px;
}
.detail-content {
  margin-bottom: 16px;
}
.detail-image {
  width: 100%;
  max-width: 160px;
  display: block;
  margin: 12px auto;
  border-radius: 12px;
  box-shadow: 0 4px 24px rgba(0,0,0,0.10);
}
.product-image-large {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 8px;
  margin-right: 16px;
}
.product-detail {
  margin-top: 10px;
  color: #666;
  font-size: 1rem;
  line-height: 1.7;
  background: #f8f8f8;
  border-radius: 8px;
  padding: 12px 16px;
}
.detail-footer {
  margin-bottom: 16px;
}
.btn.active i.fa-heart {
  color: #e74c3c;
}
.btn.active i.fa-star {
  color: #f7b731;
}
.comments-section {
  max-height: 400px;
  overflow-y: auto;
  margin-top: 24px;
  padding-right: 8px;
}
.comment-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  margin-bottom: 10px;
}
.delete-comment-btn {
  margin-left: 20px;
  background: #ffebee;
  color: #e74c3c;
  border: none;
  border-radius: 6px;
  padding: 6px 14px;
  cursor: pointer;
  font-size: 0.95rem;
  transition: background 0.2s, color 0.2s;
}
.delete-comment-btn:hover {
  background: #e74c3c;
  color: #fff;
}
.comment-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 18px;
}
</style> 