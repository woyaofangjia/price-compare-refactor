<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title"><i class="fas fa-file-alt"></i> 动态详情</h1>
      <RouterLink to="/square">
        <button class="btn btn-outline"><i class="fas fa-arrow-left"></i> 返回</button>
      </RouterLink>
      <RouterLink to="/post/create">
        <button class="btn btn-primary"><i class="fas fa-plus"></i> 发布动态</button>
      </RouterLink>
    </div>
    <div class="card">
      <div v-if="postData">
        <div class="detail-header">
          <img :src="postData.userAvatar" class="user-avatar" />
          <div class="user-info">
            <div class="username">{{ postData.username }}</div>
            <div class="post-time">{{ postData.time }}</div>
          </div>
        </div>
        <div class="detail-content">
          <div class="content-text">{{ postData.content }}</div>
          <div v-if="postData.images && postData.images.length" class="images-grid">
            <img v-for="(img, idx) in postData.images" :key="idx" :src="img" class="detail-image-large" />
          </div>
          <div v-if="postData.product" class="product-card">
            <img :src="postData.product.image" class="product-image-large" />
            <div class="product-info">
              <div class="product-name">{{ postData.product.name }}</div>
              <div class="product-price">{{ postData.product.price }}</div>
              <div class="product-platform">{{ postData.product.platform }}</div>
              <div class="product-detail" v-if="postData.product.detail">{{ postData.product.detail }}</div>
            </div>
          </div>
        </div>
        <div class="detail-footer">
          <button class="btn btn-outline" :class="{active: isLiked}" @click="toggleLike"><i class="fas fa-heart"></i> {{ likes }}</button>
          <button class="btn btn-outline" :class="{active: isCollected}" @click="toggleCollect"><i class="fas fa-star"></i> {{ isCollected ? '已收藏' : '收藏' }}</button>
        </div>
      </div>
      <div v-else class="empty-state">未找到该动态</div>
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
          <button class="delete-comment-btn" @click="deleteComment(index)">删除</button>
        </div>
        <div class="comment-form">
          <textarea v-model="newComment" class="form-control" placeholder="写下你的评论..."></textarea>
          <button class="btn btn-primary" @click="submitComment"><i class="fas fa-paper-plane"></i> 发表评论</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const postId = Number(route.params.id)
const postData = ref(null)
const comments = ref([])
const newComment = ref('')
const isLiked = ref(false)
const isCollected = ref(false)
const likes = ref(0)

function loadPostAndComments() {
  // 从 localStorage 获取所有动态
  const storedPosts = localStorage.getItem('userPosts')
  if (storedPosts) {
    const posts = JSON.parse(storedPosts)
    postData.value = posts.find(p => p.id === postId)
    likes.value = postData.value ? postData.value.likes : 0
  }
  // 从 localStorage 获取评论
  const storedComments = localStorage.getItem(`comments_${postId}`)
  if (storedComments) {
    comments.value = JSON.parse(storedComments)
  } else {
    comments.value = []
  }
  // 收藏和点赞状态
  isLiked.value = localStorage.getItem(`like_${postId}`) === '1'
  isCollected.value = localStorage.getItem(`collect_${postId}`) === '1'
}

function submitComment() {
  if (newComment.value.trim()) {
    comments.value.push({
      author: '你',
      avatar: '',
      time: '刚刚',
      text: newComment.value
    })
    localStorage.setItem(`comments_${postId}`, JSON.stringify(comments.value))
    newComment.value = ''
  }
}

function deleteComment(index) {
  if (confirm('确定要删除这条评论吗？')) {
    comments.value.splice(index, 1)
    localStorage.setItem(`comments_${postId}`, JSON.stringify(comments.value))
  }
}

function toggleLike() {
  isLiked.value = !isLiked.value
  localStorage.setItem(`like_${postId}`, isLiked.value ? '1' : '0')
  likes.value += isLiked.value ? 1 : -1
}

function toggleCollect() {
  isCollected.value = !isCollected.value
  localStorage.setItem(`collect_${postId}`, isCollected.value ? '1' : '0')
}

onMounted(() => {
  loadPostAndComments()
})
</script>

<style scoped>
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
.detail-image-large {
  width: 100%;
  max-width: 160px;
  display: block;
  margin: 20px auto 20px auto;
  border-radius: 12px;
  box-shadow: 0 4px 24px rgba(0,0,0,0.10);
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
.comments-section {
  max-height: 400px;
  overflow-y: auto;
  margin-top: 24px;
  padding-right: 8px;
}
</style>
