<template>
  <div>
    <div class="detail-header">
      <img :src="postAvatarUrl" class="user-avatar" @error="handleAvatarError" />
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
      <button class="btn btn-outline" :class="{active: isLiked}" @click="submitLike"><i class="fas fa-heart"></i> {{ likes }}</button>
      <button class="btn btn-outline" :class="{active: isCollected}" @click="$emit('collect')"><i class="fas fa-star"></i> {{ isCollected ? '已收藏' : '收藏' }}</button>
    </div>
    
    <!-- 评论区 -->
    <div class="comments-section">
      <div class="comments-header">
        <div class="comments-title">
          <i class="fas fa-comments"></i>
          评论 ({{ commentTotal }})
        </div>
        <div class="comments-controls">
          <select v-model="localCommentSort" @change="handleSortChange" class="sort-select">
            <option value="latest">最新</option>
            <option value="oldest">最早</option>
          </select>
        </div>
      </div>
      
      <!-- 评论列表 -->
      <div class="comments-list" v-if="!commentLoading">
        <div v-if="comments.length === 0" class="empty-comments">
          <i class="fas fa-comment-slash"></i>
          <p>暂无评论，快来发表第一条评论吧！</p>
        </div>
        <div v-else class="comment-item" v-for="comment in comments" :key="comment.id">
          <img :src="getCommentAvatarUrl(comment.userAvatar)" class="comment-avatar" @error="handleCommentAvatarError" />
          <div class="comment-content">
            <div class="comment-header">
              <div class="comment-author">{{ comment.username }}</div>
              <div class="comment-time">{{ formatTime(comment.createdAt) }}</div>
            </div>
            <div class="comment-text">{{ comment.content }}</div>
          </div>
          <button 
            v-if="canDeleteComment(comment)" 
            class="delete-comment-btn" 
            @click="$emit('delete-comment', comment.id)"
            title="删除评论"
          >
            <i class="fas fa-trash"></i>
          </button>
        </div>
      </div>
      
      <!-- 加载状态 -->
      <div v-if="commentLoading" class="loading-comments">
        <i class="fas fa-spinner fa-spin"></i>
        <span>加载评论中...</span>
      </div>
      
      <!-- 分页控制 -->
      <div v-if="commentTotal > commentPageSize" class="comments-pagination">
        <button 
          class="pagination-btn" 
          :disabled="commentPage <= 1"
          @click="$emit('prev-page')"
        >
          <i class="fas fa-chevron-left"></i>
          上一页
        </button>
        <span class="page-info">
          {{ commentPage }} / {{ Math.ceil(commentTotal / commentPageSize) }}
        </span>
        <button 
          class="pagination-btn" 
          :disabled="commentPage >= Math.ceil(commentTotal / commentPageSize)"
          @click="$emit('next-page')"
        >
          下一页
          <i class="fas fa-chevron-right"></i>
        </button>
      </div>
      
      <!-- 评论表单 -->
      <div class="comment-form">
        <div class="form-header">
          <img :src="getCurrentUserAvatarUrl()" class="current-user-avatar" @error="handleCurrentUserAvatarError" />
          <span class="current-username">{{ currentUser?.username || '游客' }}</span>
        </div>
        <div class="form-content">
          <textarea 
            v-model="newComment" 
            class="form-control comment-textarea" 
            placeholder="写下你的评论..."
            :maxlength="500"
            rows="3"
          ></textarea>
          <div class="form-footer">
            <span class="char-count">{{ newComment.length }}/500</span>
            <button 
              class="btn btn-primary submit-btn" 
              :disabled="!newComment.trim() || commentSubmitting"
              @click="submitComment"
            >
              <i v-if="commentSubmitting" class="fas fa-spinner fa-spin"></i>
              <i v-else class="fas fa-paper-plane"></i>
              {{ commentSubmitting ? '发表中...' : '发表评论' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import { getAvatarUrl, handleAvatarError } from '@/utils/avatar.js'

const props = defineProps({
  post: Object,
  comments: Array,
  isLiked: Boolean,
  isCollected: Boolean,
  likes: Number,
  currentUser: Object,
  commentTotal: Number,
  commentPage: Number,
  commentPageSize: Number,
  commentSort: String,
  commentLoading: Boolean,
  commentSubmitting: Boolean
})

const emit = defineEmits(['like', 'collect', 'submit-comment', 'delete-comment', 'prev-page', 'next-page', 'sort-change'])
const newComment = ref('')

// 本地状态管理
const localCommentSort = ref(props.commentSort || 'latest')

// 监听props变化
watch(() => props.commentSort, (newSort) => {
  localCommentSort.value = newSort
})

// 计算动态作者头像URL
const postAvatarUrl = computed(() => {
  return getAvatarUrl(props.post?.userAvatar)
})

function submitComment() {
  if (newComment.value.trim() && !props.commentSubmitting) {
    emit('submit-comment', newComment.value)
    newComment.value = ''
  }
}

function submitLike() {
  emit('like', props.post.id)
}

function handleSortChange() {
  emit('sort-change', localCommentSort.value)
}

// 判断是否可以删除评论
function canDeleteComment(comment) {
  if (!props.currentUser) return false
  
  // 评论作者可以删除自己的评论
  if (comment.username === props.currentUser.username) return true
  
  // 动态作者可以删除自己动态下的所有评论
  if (props.post.username === props.currentUser.username) return true
  
  return false
}

// 格式化时间
function formatTime(timeStr) {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  if (diff < 2592000000) return `${Math.floor(diff / 86400000)}天前`
  
  return date.toLocaleDateString()
}

// 获取评论头像URL
function getCommentAvatarUrl(avatar) {
  return getAvatarUrl(avatar)
}

// 获取当前用户头像URL
function getCurrentUserAvatarUrl() {
  return getAvatarUrl(props.currentUser?.avatar)
}

// 处理评论头像加载错误
function handleCommentAvatarError(event) {
  handleAvatarError(event)
}

// 处理当前用户头像加载错误
function handleCurrentUserAvatarError(event) {
  handleAvatarError(event)
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

/* 评论区样式 */
.comments-section {
  margin-top: 24px;
  border-top: 1px solid #eee;
  padding-top: 20px;
}

.comments-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.comments-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: #333;
}

.comments-title i {
  margin-right: 8px;
  color: #007bff;
}

.sort-select {
  padding: 6px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  background: white;
  font-size: 0.9rem;
  cursor: pointer;
}

.comments-list {
  max-height: 400px;
  overflow-y: auto;
  margin-bottom: 20px;
}

.empty-comments {
  text-align: center;
  padding: 40px 20px;
  color: #999;
}

.empty-comments i {
  font-size: 3rem;
  margin-bottom: 16px;
  display: block;
}

.loading-comments {
  text-align: center;
  padding: 20px;
  color: #666;
}

.loading-comments i {
  margin-right: 8px;
}

.comment-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 20px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 12px;
  transition: background 0.2s;
}

.comment-item:hover {
  background: #e9ecef;
}

.comment-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
}

.comment-content {
  flex: 1;
  min-width: 0;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.comment-author {
  font-weight: 600;
  color: #333;
  font-size: 0.95rem;
}

.comment-time {
  font-size: 0.85rem;
  color: #999;
}

.comment-text {
  color: #555;
  line-height: 1.5;
  word-break: break-word;
}

.delete-comment-btn {
  background: none;
  border: none;
  color: #dc3545;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: background 0.2s;
  flex-shrink: 0;
}

.delete-comment-btn:hover {
  background: #dc3545;
  color: white;
}

/* 分页控制 */
.comments-pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  margin: 20px 0;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.pagination-btn {
  padding: 8px 16px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 6px;
}

.pagination-btn:hover:not(:disabled) {
  background: #007bff;
  color: white;
  border-color: #007bff;
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  font-weight: 600;
  color: #666;
}

/* 评论表单 */
.comment-form {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 20px;
  margin-top: 20px;
}

.form-header {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}

.current-user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  margin-right: 12px;
}

.current-username {
  font-weight: 600;
  color: #333;
}

.form-content {
  position: relative;
}

.comment-textarea {
  width: 100%;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 12px;
  font-size: 0.95rem;
  resize: vertical;
  min-height: 80px;
  transition: border-color 0.2s;
}

.comment-textarea:focus {
  outline: none;
  border-color: #007bff;
  box-shadow: 0 0 0 2px rgba(0, 123, 255, 0.1);
}

.form-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
}

.char-count {
  font-size: 0.85rem;
  color: #999;
}

.submit-btn {
  padding: 8px 20px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style> 