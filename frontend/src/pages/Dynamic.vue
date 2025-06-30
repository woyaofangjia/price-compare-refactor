<template>
  <section class="page-content">
    <div class="container">
      <div class="dynamic-header">
        <h2 class="section-title">
          <i class="fas fa-bullhorn"></i> 我的动态
        </h2>
        <div class="header-actions">
          <button v-if="userPosts.length > 0" @click="clearAllPosts" class="btn btn-outline" style="margin-right: 10px;">
            <i class="fas fa-trash-alt"></i> 清空所有动态
          </button>
          <router-link to="/post/create" class="btn btn-primary">
            <i class="fas fa-plus"></i> 发布动态
          </router-link>
        </div>
      </div>
      
      <!-- 用户未登录提示 -->
      <div v-if="!isLoggedIn" class="empty-state">
        <div class="empty-icon">
          <i class="fas fa-user-lock"></i>
        </div>
        <h3>请先登录</h3>
        <p>登录后可以查看和发布您的动态</p>
        <router-link to="/login" class="btn btn-primary">
          <i class="fas fa-sign-in-alt"></i> 立即登录
        </router-link>
      </div>
      
      <!-- 用户已登录但无动态 -->
      <div v-else-if="userPosts.length === 0" class="empty-state">
        <div class="empty-icon">
          <i class="fas fa-comment-slash"></i>
        </div>
        <h3>暂无动态</h3>
        <p>快来发布第一条动态吧！</p>
        <router-link to="/post/create" class="btn btn-primary">
          <i class="fas fa-plus"></i> 发布动态
        </router-link>
      </div>
      
      <!-- 用户动态列表 -->
      <div v-else class="dynamic-list">
        <div v-for="(post, index) in userPosts" :key="index" class="dynamic-card" @click="openDetail(post)" style="cursor:pointer;">
          <div class="dynamic-header">
            <div class="user-info">
              <img 
                :src="getAvatarUrl(post.userAvatar)" 
                :alt="post.username" 
                class="user-avatar"
                @error="handleAvatarError"
              />
              <div class="user-details">
                <div class="username">{{ post.username }}</div>
                <div class="post-time">{{ post.time }}</div>
              </div>
            </div>
            <div class="post-actions">
              <button class="action-btn" @click.stop="editPost(post.id)">
                <i class="fas fa-edit"></i>
              </button>
              <button class="action-btn delete" @click.stop="deletePost(post.id)">
                <i class="fas fa-trash"></i>
              </button>
            </div>
          </div>
          
          <div class="dynamic-content">
            <p class="content-text">{{ post.content }}</p>
            <div v-if="post.images && post.images.length > 0" class="image-grid">
              <img 
                v-for="(img, imgIndex) in post.images" 
                :key="imgIndex" 
                :src="getFullImageUrl(img)" 
                :alt="`图片${imgIndex + 1}`"
                class="content-image"
                @click.stop="previewImage(img)"
              />
            </div>
            <div v-if="post.product" class="product-card">
              <img :src="post.product.image" :alt="post.product.name" class="product-image" />
              <div class="product-info">
                <div class="product-name">{{ post.product.name }}</div>
                <div class="product-price">{{ post.product.price }}</div>
                <div class="product-platform">{{ post.product.platform }}</div>
                <div class="product-detail" style="margin-top:8px; color:#888; font-size:0.98rem; background:#f8f8f8; border-radius:8px; padding:8px 12px;">
                  {{ post.product.detail || '这是一款高性价比的优质商品，深受用户好评。' }}
                </div>
              </div>
            </div>
          </div>
          
          <div class="dynamic-footer">
            <div class="interaction-stats">
              <span class="stat-item">
                <i class="fas fa-heart"></i> {{ post.likes }}
              </span>
              <span class="stat-item">
                <i class="fas fa-comment"></i> {{ post.comments }}
              </span>
            </div>
            <div class="post-actions">
              <button class="action-btn" @click.stop="likePost(post.id)">
                <i :class="post.isLiked ? 'fas fa-heart liked' : 'far fa-heart'"></i>
              </button>
              <button class="action-btn" @click.stop="commentPost(post.id)">
                <i class="fas fa-comment"></i>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
  <div v-if="showDetail" class="detail-dialog-mask" @click.self="closeDetail">
    <div class="detail-dialog">
      <DynamicDetailContent
        :post="selectedPost"
        :comments="detailComments"
        :isLiked="false"
        :isCollected="false"
        :likes="selectedPost?.likes || 0"
        :currentUser="currentUser"
        :commentTotal="commentTotal"
        :commentPage="commentPage"
        :commentPageSize="commentPageSize"
        :commentSort="commentSort"
        :commentLoading="commentLoading"
        :commentSubmitting="commentSubmitting"
        @like="() => {}"
        @collect="() => {}"
        @submit-comment="onSubmitComment"
        @delete-comment="onDeleteComment"
        @prev-page="prevCommentPage"
        @next-page="nextCommentPage"
        @sort-change="handleCommentSortChange"
      />
      <button class="close-btn" @click="closeDetail">关闭</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue'
import { useRouter } from 'vue-router'
import DynamicDetailContent from './post/components/DynamicDetailContent.vue'
import { postsAPI } from '@/api/posts.js'
import { getAvatarUrl, handleAvatarError } from '@/utils/avatar.js'
import axios from 'axios'

const router = useRouter()
const store = inject('store')
const isLoggedIn = ref(false)
const userPosts = ref([])
const showDetail = ref(false)
const selectedPost = ref(null)
const detailComments = ref([])
const currentUser = ref(null)
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const sort = ref('latest')
const commentPage = ref(1)
const commentPageSize = ref(20)
const commentTotal = ref(0)
const commentSort = ref('latest')
const commentLoading = ref(false)
const commentSubmitting = ref(false)

// 检查登录状态
function checkLoginStatus() {
  const token = localStorage.getItem('token')
  const user = localStorage.getItem('user')
  isLoggedIn.value = !!(token && user)
  if (user) {
    currentUser.value = JSON.parse(user)
  }
}

// 获取用户动态（从后端获取，支持分页/排序）
async function getUserPosts() {
  if (!isLoggedIn.value) return
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  if (!user.id) return
  try {
    const response = await postsAPI.getUserPosts(user.id, page.value, pageSize.value, sort.value)
    if (response.code === 0) {
      userPosts.value = response.data.list || []
      total.value = response.data.total || 0
      page.value = response.data.page || 1
      pageSize.value = response.data.pageSize || 10
    } else {
      userPosts.value = []
      total.value = 0
    }
  } catch (e) {
    userPosts.value = []
    total.value = 0
  }
}

// 分页切换
function prevPage() {
  if (page.value > 1) {
    page.value--
    getUserPosts()
  }
}
function nextPage() {
  if (page.value < Math.ceil(total.value / pageSize.value)) {
    page.value++
    getUserPosts()
  }
}
function handleSortChange(newSort) {
  sort.value = newSort
  page.value = 1
  getUserPosts()
}

// 点赞动态
async function likePost(postId) {
  try {
    const post = userPosts.value.find(p => p.id === postId)
    if (!post) return
    
    const currentIsLiked = post.isLiked || false
    const response = await postsAPI.toggleLike(postId, !currentIsLiked)
    
    if (response.code === 0) {
      // 更新主列表
      post.likes = response.data.likes
      post.isLiked = response.data.isLiked
      
      // 更新详情页
      if (selectedPost.value && selectedPost.value.id === postId) {
        selectedPost.value.likes = response.data.likes
        selectedPost.value.isLiked = response.data.isLiked
        if (selectedPost.value !== post) {
          selectedPost.value = { ...selectedPost.value }
        }
      }
      
      if (store) store.showNotification(
        response.data.isLiked ? '点赞成功' : '取消点赞', 
        'success'
      )
    } else {
      if (store) store.showNotification('操作失败', 'error')
    }
  } catch (error) {
    console.error('点赞操作失败:', error)
    if (store) store.showNotification('操作失败', 'error')
  }
}

// 评论动态
function commentPost(postId) {
  const post = userPosts.value.find(p => p.id === postId)
  if (post) {
    openDetail(post)
  }
}

// 编辑动态
function editPost(postId) {
  router.push(`/post/create?edit=${postId}`)
}

// 删除动态
function deletePost(postId) {
  if (confirm('确定要删除这条动态吗？\n删除后将无法恢复。')) {
    try {
      userPosts.value = userPosts.value.filter(p => p.id !== postId)
      alert('动态删除成功！')
      window.dispatchEvent(new Event('loginStatusChanged'))
    } catch (error) {
      alert('删除动态时出现错误，请重试')
    }
  }
}

// 清空所有动态（调用后端接口）
async function clearAllPosts() {
  if (!currentUser.value?.id) return
  if (confirm('确定要清空所有动态吗？\n清空后将无法恢复。')) {
    try {
      const token = localStorage.getItem('token')
      const response = await axios.delete(`/api/posts/user/${currentUser.value.id}/all`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      })
      if (response.data.code === 0) {
        alert(`成功删除 ${response.data.data.deletedCount} 条动态！`)
        getUserPosts()
      } else {
        alert(response.data.message || '清空失败')
      }
    } catch (error) {
      alert(error.response?.data?.message || '清空所有动态时出现错误，请重试')
    }
  }
}

function previewImage(imgSrc) {
  // 这里可以实现图片预览功能
}

// 打开详情并加载评论（支持分页/排序）
async function openDetail(post) {
  selectedPost.value = post
  detailComments.value = []
  showDetail.value = true
  commentPage.value = 1
  await fetchComments(post.id)
}

// 获取评论列表
async function fetchComments(postId) {
  commentLoading.value = true
  try {
    const response = await postsAPI.getComments(postId, commentPage.value, commentPageSize.value, commentSort.value)
    if (response.code === 0 && response.data) {
      detailComments.value = response.data.list || []
      commentTotal.value = response.data.total || 0
      commentPage.value = response.data.page || 1
      commentPageSize.value = response.data.pageSize || 20
    } else {
      detailComments.value = []
      commentTotal.value = 0
    }
  } catch (e) {
    detailComments.value = []
    commentTotal.value = 0
  } finally {
    commentLoading.value = false
  }
}

// 评论分页切换
function prevCommentPage() {
  if (commentPage.value > 1) {
    commentPage.value--
    fetchComments(selectedPost.value.id)
  }
}
function nextCommentPage() {
  if (commentPage.value < Math.ceil(commentTotal.value / commentPageSize.value)) {
    commentPage.value++
    fetchComments(selectedPost.value.id)
  }
}
function handleCommentSortChange(newSort) {
  commentSort.value = newSort
  commentPage.value = 1
  fetchComments(selectedPost.value.id)
}

// 处理评论提交（成功后刷新评论列表）
async function onSubmitComment(comment) {
  commentSubmitting.value = true
  try {
    const response = await postsAPI.addComment(selectedPost.value.id, { content: comment })
    if (response.code === 0) {
      // 重新加载评论第一页
      commentPage.value = 1
      await fetchComments(selectedPost.value.id)
      
      // 从后端获取最新的动态详情来更新评论数
      try {
        const postResponse = await postsAPI.getPostById(selectedPost.value.id)
        if (postResponse.code === 0 && postResponse.data) {
          const updatedPost = postResponse.data
          // 更新主列表评论数
          const post = userPosts.value.find(p => p.id === selectedPost.value.id)
          if (post) {
            post.comments = updatedPost.comments
          }
          // 更新详情页评论数
          if (selectedPost.value) {
            selectedPost.value.comments = updatedPost.comments
            if (selectedPost.value !== post) {
              selectedPost.value = { ...selectedPost.value }
            }
          }
        }
      } catch (error) {
        console.error('获取更新后的动态详情失败:', error)
      }
      
      if (store) store.showNotification('评论成功', 'success')
    } else {
      if (store) store.showNotification('评论失败', 'error')
    }
  } catch (error) {
    console.error('评论操作失败:', error)
    if (store) store.showNotification('评论失败', 'error')
  } finally {
    commentSubmitting.value = false
  }
}

// 处理评论删除
async function onDeleteComment(commentId) {
  try {
    const response = await postsAPI.deleteComment(selectedPost.value.id, commentId)
    if (response.code === 0) {
      // 重新加载评论列表
      await fetchComments(selectedPost.value.id)
      
      // 从后端获取最新的动态详情来更新评论数
      try {
        const postResponse = await postsAPI.getPostById(selectedPost.value.id)
        if (postResponse.code === 0 && postResponse.data) {
          const updatedPost = postResponse.data
          // 更新主列表评论数
          const post = userPosts.value.find(p => p.id === selectedPost.value.id)
          if (post) {
            post.comments = updatedPost.comments
          }
          // 更新详情页评论数
          if (selectedPost.value) {
            selectedPost.value.comments = updatedPost.comments
            if (selectedPost.value !== post) {
              selectedPost.value = { ...selectedPost.value }
            }
          }
        }
      } catch (error) {
        console.error('获取更新后的动态详情失败:', error)
      }
      
      if (store) store.showNotification('评论删除成功', 'success')
    } else {
      if (store) store.showNotification('删除失败', 'error')
    }
  } catch (error) {
    console.error('删除评论失败:', error)
    if (store) store.showNotification('删除失败', 'error')
  }
}

// 收藏动态
async function collectPost(postId) {
  try {
    const post = userPosts.value.find(p => p.id === postId)
    if (!post) return
    
    const currentIsCollected = post.isCollected || false
    const response = await postsAPI.toggleCollect(postId, !currentIsCollected)
    
    if (response.code === 0) {
      // 更新主列表
      post.isCollected = response.data.isCollected
      
      // 更新详情页
      if (selectedPost.value && selectedPost.value.id === postId) {
        selectedPost.value.isCollected = response.data.isCollected
        if (selectedPost.value !== post) {
          selectedPost.value = { ...selectedPost.value }
        }
      }
      
      if (store) store.showNotification(
        response.data.isCollected ? '收藏成功' : '取消收藏', 
        'success'
      )
    } else {
      if (store) store.showNotification('操作失败', 'error')
    }
  } catch (error) {
    console.error('收藏操作失败:', error)
    if (store) store.showNotification('操作失败', 'error')
  }
}

function closeDetail() {
  showDetail.value = false
}

function getFullImageUrl(url) {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return 'http://localhost:3000' + url
}

onMounted(() => {
  checkLoginStatus()
  getUserPosts()
  // 监听登录状态变化
  window.addEventListener('loginStatusChanged', () => {
    checkLoginStatus()
    getUserPosts()
  })
  
  // 监听页面可见性变化，确保数据同步
  document.addEventListener('visibilitychange', () => {
    if (!document.hidden && showDetail.value && selectedPost.value) {
      // 页面重新可见时，刷新评论数据
      fetchComments(selectedPost.value.id)
    }
  })
})
</script>

<style scoped>
.page-content {
  padding: 30px 0;
  min-height: calc(100vh - 150px);
  max-width: 1200px;
  margin: 0 auto;
}

.container {
  padding: 0 20px;
}

.dynamic-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.section-title {
  font-size: 1.8rem;
  margin: 0;
  color: var(--primary);
  display: flex;
  align-items: center;
}

.section-title i {
  margin-right: 10px;
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  font-weight: 500;
  transition: all 0.3s ease;
}

.btn-primary {
  background: var(--primary);
  color: white;
}

.btn-primary:hover {
  background: var(--secondary);
  transform: translateY(-2px);
}

.btn-primary:active {
  transform: scale(0.95);
}

.btn-outline {
  background: none;
  border: 1px solid var(--primary);
  color: var(--primary);
}

.btn-outline:hover {
  background: var(--primary);
  color: white;
}

.btn-outline:active {
  transform: scale(0.95);
}

.btn i {
  margin-right: 8px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
}

.empty-icon {
  font-size: 4rem;
  color: var(--gray);
  margin-bottom: 20px;
}

.empty-state h3 {
  font-size: 1.5rem;
  margin-bottom: 10px;
  color: var(--dark);
}

.empty-state p {
  color: var(--gray);
  margin-bottom: 30px;
}

.dynamic-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 32px;
}

.dynamic-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
  transition: transform 0.3s ease;
}

.dynamic-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
}

.dynamic-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.user-info {
  display: flex;
  align-items: center;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 12px;
}

.user-details {
  display: flex;
  flex-direction: column;
}

.username {
  font-weight: 600;
  color: var(--dark);
}

.post-time {
  font-size: 0.9rem;
  color: var(--gray);
}

.post-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  background: none;
  border: none;
  padding: 8px;
  border-radius: 6px;
  cursor: pointer;
  color: var(--gray);
  transition: all 0.3s ease;
}

.action-btn:hover {
  background: var(--light-gray);
  color: var(--dark);
}

.action-btn.delete {
  color: #e74c3c;
}

.action-btn.delete:hover {
  background: #ffebee;
  color: #c0392b;
  transform: scale(1.1);
}

.action-btn.delete:active {
  transform: scale(0.95);
}

.dynamic-content {
  margin-bottom: 15px;
}

.content-text {
  margin-bottom: 15px;
  line-height: 1.6;
  color: var(--dark);
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

.product-card {
  display: flex;
  background: var(--light);
  border-radius: 8px;
  padding: 12px;
  margin-top: 15px;
}

.product-image {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 6px;
  margin-right: 12px;
}

.product-info {
  flex: 1;
}

.product-name {
  font-weight: 600;
  margin-bottom: 4px;
  color: var(--dark);
}

.product-price {
  color: var(--warning);
  font-weight: 600;
  margin-bottom: 4px;
}

.product-platform {
  font-size: 0.9rem;
  color: var(--gray);
}

.product-detail {
  margin-top: 8px;
  color: #888;
  font-size: 0.98rem;
  background: #f8f8f8;
  border-radius: 8px;
  padding: 8px 12px;
}

.dynamic-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid var(--light-gray);
}

.interaction-stats {
  display: flex;
  gap: 20px;
}

.stat-item {
  display: flex;
  align-items: center;
  color: var(--gray);
  font-size: 0.9rem;
}

.stat-item i {
  margin-right: 6px;
}

.liked {
  color: #e74c3c;
}

@media (max-width: 1000px) {
  .dynamic-list {
    grid-template-columns: 1fr;
  }
}

.detail-dialog-mask {
  position: fixed;
  left: 0; top: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.18);
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
}
.detail-dialog {
  background: #fff;
  border-radius: 12px;
  padding: 32px 24px 24px 24px;
  min-width: 340px;
  max-width: 420px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.18);
  position: relative;
}
.close-btn {
  position: absolute;
  right: 18px;
  top: 12px;
  background: #eee;
  border: none;
  border-radius: 6px;
  padding: 4px 12px;
  cursor: pointer;
}
</style> 