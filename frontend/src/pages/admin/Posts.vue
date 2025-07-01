<template>
  <div class="content-page">
    <div class="content-section">
      <div class="section-header">
        <h2>动态管理</h2>
        <div>
          <input v-model="searchKeyword" placeholder="搜索内容..." @keyup.enter="fetchPosts" class="search-input" />
          <button class="btn btn-primary" @click="fetchPosts">搜索</button>
        </div>
      </div>
      <div v-if="posts.length === 0" class="empty-state">暂无动态</div>
      <div class="card-list">
        <div class="post-card" v-for="post in posts" :key="post.id">
          <div class="card-header">
            <div class="author-info">
              <div class="avatar-container">
                <img 
                  v-if="post.userAvatar && post.userAvatar !== ''" 
                  :src="getAvatarUrl(post)"
                  :alt="post.username"
                  class="avatar"
                  @error="handleAvatarError($event, post)"
                />
                <div v-else class="avatar-placeholder">
                  {{ post.username ? post.username.charAt(0).toUpperCase() : '?' }}
                </div>
              </div>
              <span class="username">{{ post.username }}</span>
              <span class="created-at">{{ formatTime(post.createdAt) }}</span>
            </div>
            <button class="btn btn-danger btn-sm" @click="confirmDelete(post.id)">删除动态</button>
          </div>
          <div class="card-content">
            <div class="content-text">{{ post.content }}</div>
            <div v-if="post.images && post.images.length > 0" class="image-list">
              <img v-for="(img, idx) in parseImages(post.images)" :key="idx" :src="getFullImageUrl(img)" class="post-image" @error="console.log('图片加载失败:', img)" />
            </div>
          </div>
          <div class="card-footer">
            <span>评论数：{{ post.comments }}</span>
            <span>点赞数：{{ post.likes }}</span>
          </div>
          <div class="comments-section">
            <div class="comments-header">评论列表</div>
            <div v-if="post.commentList && post.commentList.length > 0">
              <div class="comment-item" v-for="comment in post.commentList" :key="comment.id">
                <span class="comment-user">{{ comment.username }}</span>
                <span class="comment-content">{{ comment.content }}</span>
                <span class="comment-time">{{ formatTime(comment.createdAt) }}</span>
                <button class="btn btn-danger btn-xs" @click="deleteComment(post.id, comment.id)">删除评论</button>
              </div>
            </div>
            <div v-else class="no-comments">暂无评论</div>
          </div>
        </div>
      </div>
      <div class="pagination" v-if="total > pageSize">
        <button class="btn" :disabled="page <= 1" @click="changePage(page-1)">上一页</button>
        <span>{{ page }} / {{ Math.ceil(total / pageSize) }}</span>
        <button class="btn" :disabled="page >= Math.ceil(total / pageSize)" @click="changePage(page+1)">下一页</button>
      </div>
    </div>
    <!-- 删除动态确认弹窗 -->
    <div v-if="showDeleteModal" class="modal-overlay" @click="closeDeleteModal">
      <div class="modal-content" @click.stop>
        <h3>确认删除</h3>
        <p>确定要删除该动态吗？此操作不可撤销。</p>
        <div class="modal-actions">
          <button class="btn" @click="closeDeleteModal">取消</button>
          <button class="btn btn-danger" @click="deletePost">
            <span v-if="deletingId === deleteId" class="loading-spinner"></span>
            确认删除
          </button>
        </div>
      </div>
    </div>
    <!-- 消息提示 -->
    <div v-if="message" class="message-toast" :class="messageType">{{ message }}</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { postsAPI } from '../../api/posts.js'

const posts = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchKeyword = ref('')
const loading = ref(false)
const showDeleteModal = ref(false)
const deleteId = ref(null)
const deletingId = ref(null)
const message = ref('')
const messageType = ref('success')
const AVATAR_BASE_URL = 'http://localhost:3000'
const DEFAULT_AVATAR = 'https://picsum.photos/seed/user/40/40'

function fetchPosts() {
  loading.value = true
  postsAPI.getAdminPosts({ page: page.value, pageSize: pageSize.value, keyword: searchKeyword.value })
    .then(async res => {
      if (res.code === 0 && res.data) {
        // 对每个动态获取评论列表
        const list = await Promise.all(res.data.list.map(async post => {
          let commentList = []
          try {
            const commentRes = await postsAPI.getComments(post.id, 1, 10, 'latest')
            if (commentRes.code === 0 && commentRes.data) {
              commentList = commentRes.data.list
            }
          } catch {}
          console.log('post.id:', post.id, 'post.images:', post.images)
          return { ...post, commentList }
        }))
        posts.value = list
        total.value = res.data.total
      }
    })
    .catch(() => showMessage('获取动态失败', 'error'))
    .finally(() => loading.value = false)
}

function changePage(p) {
  page.value = p
  fetchPosts()
}

function confirmDelete(id) {
  showDeleteModal.value = true
  deleteId.value = id
}

function closeDeleteModal() {
  showDeleteModal.value = false
  deleteId.value = null
}

function deletePost() {
  if (!deleteId.value) return
  deletingId.value = deleteId.value
  postsAPI.deleteAdminPost(deleteId.value)
    .then(res => {
      console.log('删除动态接口响应:', res);
      if (res.code === 0) {
        showMessage('删除成功', 'success')
        fetchPosts()
        closeDeleteModal()
      } else {
        showMessage('删除失败', 'error')
      }
    })
    .catch(err => {
      console.error('删除动态接口异常:', err);
      showMessage('删除失败', 'error')
    })
    .finally(() => deletingId.value = null)
}

function deleteComment(postId, commentId) {
  postsAPI.deleteAdminComment(postId, commentId)
    .then(res => {
      if (res.code === 0) {
        showMessage('评论删除成功', 'success')
        fetchPosts()
      } else {
        showMessage('评论删除失败', 'error')
      }
    })
    .catch(() => showMessage('评论删除失败', 'error'))
}

function showMessage(text, type = 'success') {
  message.value = text
  messageType.value = type
  setTimeout(() => { message.value = '' }, 2000)
}

function formatTime(time) {
  if (!time) return ''
  return new Date(time).toLocaleString()
}

function parseImages(images) {
  if (!images) return []
  if (typeof images === 'string') {
    try {
      const arr = JSON.parse(images)
      if (Array.isArray(arr)) return arr
      if (typeof arr === 'string') return [arr]
    } catch {
      // 兼容直接是图片路径字符串
      return [images]
    }
  }
  if (Array.isArray(images)) return images
  return []
}

function getFullImageUrl(img) {
  if (!img) return ''
  if (img.startsWith('http://') || img.startsWith('https://')) return img
  return `http://localhost:3000${img}`
}

function getAvatarUrl(post) {
  if (post.userAvatar) {
    if (post.userAvatar.startsWith('http')) {
      return post.userAvatar
    }
    return AVATAR_BASE_URL + post.userAvatar
  }
  return DEFAULT_AVATAR
}

function handleAvatarError(event, post) {
  event.target.src = DEFAULT_AVATAR
}

onMounted(fetchPosts)
</script>

<style scoped>
.content-page {
  padding: 20px;
}
.content-section {
  background: #fff;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.search-input {
  padding: 6px 12px;
  border-radius: 6px;
  border: 1px solid #ccc;
  margin-right: 10px;
}
.card-list {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}
.post-card {
  background: #fafbfc;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  padding: 18px 20px 12px 20px;
  width: 420px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  position: relative;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.author-info {
  display: flex;
  align-items: center;
  gap: 8px;
}
.avatar-container {
  display: flex;
  justify-content: center;
  align-items: center;
}
.avatar-placeholder {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: linear-gradient(135deg, #4361ee, #f72585);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 16px;
}
.avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
}
.username {
  font-weight: 600;
  color: #333;
}
.created-at {
  color: #888;
  font-size: 13px;
}
.card-content {
  margin: 8px 0;
}
.content-text {
  font-size: 15px;
  color: #222;
  margin-bottom: 6px;
}
.image-list {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
.post-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 6px;
  border: 1px solid #eee;
}
.card-footer {
  display: flex;
  gap: 18px;
  color: #888;
  font-size: 14px;
}
.comments-section {
  background: #f6f7f9;
  border-radius: 8px;
  padding: 10px 12px;
  margin-top: 8px;
}
.comments-header {
  font-weight: 600;
  margin-bottom: 6px;
  color: #555;
}
.comment-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  margin-bottom: 4px;
}
.comment-user {
  color: #409eff;
  font-weight: 500;
}
.comment-content {
  color: #222;
}
.comment-time {
  color: #aaa;
  font-size: 12px;
}
.btn-xs {
  padding: 2px 8px;
  font-size: 12px;
  border-radius: 4px;
}
.btn {
  padding: 5px 12px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  margin-right: 6px;
}
.btn-primary { background: #409eff; color: #fff; }
.btn-danger { background: #f56c6c; color: #fff; }
.btn-sm { font-size: 13px; padding: 3px 8px; }
.loading-spinner {
  display: inline-block;
  width: 16px;
  height: 16px;
  border: 2px solid #fff;
  border-top: 2px solid #409eff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-right: 4px;
}
@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
.pagination {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 10px;
}
.empty-state {
  text-align: center;
  color: #aaa;
  padding: 40px 0;
  font-size: 18px;
}
.message-toast {
  position: fixed;
  bottom: 30px;
  left: 50%;
  transform: translateX(-50%);
  background: #333;
  color: #fff;
  padding: 10px 30px;
  border-radius: 6px;
  z-index: 2000;
  font-size: 15px;
}
.message-toast.error { background: #f56c6c; }
.message-toast.success { background: #67c23a; }
</style>