<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">
        <i class="fas fa-bookmark"></i> 我的收藏
      </h1>
      <div class="header-actions">
        <div class="sort-select">
          <select class="form-control" v-model="sortType" @change="handleSort">
            <option value="latest">最新收藏</option>
            <option value="oldest">最早收藏</option>
            <option value="popular">最受欢迎</option>
          </select>
        </div>
      </div>
    </div>

    <!-- 用户未登录提示 -->
    <div v-if="!isLoggedIn" class="empty-state">
      <div class="empty-icon">
        <i class="fas fa-user-lock"></i>
      </div>
      <h3>请先登录</h3>
      <p>登录后可以查看您的收藏动态</p>
      <router-link to="/login" class="btn btn-primary">
        <i class="fas fa-sign-in-alt"></i> 立即登录
      </router-link>
    </div>

    <!-- 加载状态 -->
    <div v-else-if="loading" class="loading-container">
      <i class="fas fa-spinner fa-spin"></i>
      <p>加载中...</p>
    </div>

    <!-- 收藏列表 -->
    <div v-else-if="collections.length > 0" class="collections-container">
      <DynamicCard
        v-for="(post, index) in collections"
        :key="post.id || index"
        :post="post"
        @card-click="openDetail"
        @like="handleLike"
        @collect="handleCollect"
        @delete="handleDelete"
        style="cursor:pointer;"
      >
        <template #extra v-if="post.product">
          <div class="product-detail" style="margin-top:8px; color:#888; font-size:0.98rem; background:#f8f8f8; border-radius:8px; padding:8px 12px;">
            {{ post.product.detail || '这是一款高性价比的优质商品，深受用户好评。' }}
          </div>
        </template>
      </DynamicCard>
    </div>

    <!-- 空状态 -->
    <div v-else class="empty-state">
      <i class="fas fa-bookmark"></i>
      <p>暂无收藏动态，快去收藏一些有趣的动态吧！</p>
      <router-link to="/post/square" class="btn btn-primary">
        <i class="fas fa-comments"></i> 浏览动态广场
      </router-link>
    </div>

    <!-- 分页 -->
    <div v-if="total > pageSize" class="pagination">
      <button 
        class="btn btn-outline" 
        @click="prevPage" 
        :disabled="currentPage <= 1"
      >
        <i class="fas fa-chevron-left"></i> 上一页
      </button>
      <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
      <button 
        class="btn btn-outline" 
        @click="nextPage" 
        :disabled="currentPage >= totalPages"
      >
        下一页 <i class="fas fa-chevron-right"></i>
      </button>
    </div>

    <!-- 动态详情弹窗 -->
    <div v-if="showDetail" class="detail-dialog-mask" @click.self="closeDetail">
      <div class="detail-dialog">
        <DynamicDetailContent
          :post="selectedPost"
          :comments="detailComments"
          :isLiked="selectedPost?.isLiked || false"
          :isCollected="selectedPost?.isCollected || false"
          :likes="selectedPost?.likes || 0"
          :currentUser="currentUser"
          :commentTotal="commentTotal"
          :commentPage="commentPage"
          :commentPageSize="commentPageSize"
          :commentSort="commentSort"
          :commentLoading="commentLoading"
          :commentSubmitting="commentSubmitting"
          @like="handleLike"
          @collect="handleCollect"
          @submit-comment="onSubmitComment"
          @delete-comment="onDeleteComment"
          @prev-page="prevCommentPage"
          @next-page="nextCommentPage"
          @sort-change="handleCommentSortChange"
        />
        <button class="close-btn" @click="closeDetail">关闭</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, inject, computed } from 'vue'
import { postsAPI } from '@/api/posts.js'
import DynamicCard from './components/DynamicCard.vue'
import DynamicDetailContent from './components/DynamicDetailContent.vue'

const store = inject('store')

// 响应式数据
const collections = ref([])
const loading = ref(false)
const isLoggedIn = ref(false)
const currentUser = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const sortType = ref('latest')
const showDetail = ref(false)
const selectedPost = ref(null)
const detailComments = ref([])

// 评论相关状态
const commentPage = ref(1)
const commentPageSize = ref(20)
const commentTotal = ref(0)
const commentSort = ref('latest')
const commentLoading = ref(false)
const commentSubmitting = ref(false)

// 计算属性
const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

// 检查登录状态
function checkLoginStatus() {
  const token = localStorage.getItem('token')
  const user = localStorage.getItem('user')
  isLoggedIn.value = !!(token && user)
  if (user) {
    currentUser.value = JSON.parse(user)
  }
}

// 获取收藏列表
async function fetchCollections() {
  if (!isLoggedIn.value || !currentUser.value?.id) return
  
  try {
    loading.value = true
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      sort: sortType.value
    }
    
    const response = await postsAPI.getUserCollections(currentUser.value.id, params.page, params.pageSize, params.sort)
    
    if (response.code === 0) {
      collections.value = response.data.list
      total.value = response.data.total
    } else {
      console.error('API返回错误:', response)
      if (store) store.showNotification('获取收藏列表失败', 'error')
    }
  } catch (error) {
    console.error('获取收藏列表失败:', error)
    if (store) store.showNotification('获取收藏列表失败', 'error')
  } finally {
    loading.value = false
  }
}

// 处理点赞
async function handleLike(postId) {
  try {
    const currentPost = collections.value.find(p => p.id === postId)
    const currentIsLiked = currentPost?.isLiked || false
    
    const response = await postsAPI.toggleLike(postId, !currentIsLiked)
    if (response.code === 0) {
      // 更新主列表
      const post = collections.value.find(p => p.id === postId)
      if (post) {
        post.likes = response.data.likes
        post.isLiked = response.data.isLiked
      }
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

// 处理收藏
async function handleCollect(postId) {
  try {
    const currentPost = collections.value.find(p => p.id === postId)
    const currentIsCollected = currentPost?.isCollected || false
    
    const response = await postsAPI.toggleCollect(postId, !currentIsCollected)
    if (response.code === 0) {
      // 更新主列表
      const post = collections.value.find(p => p.id === postId)
      if (post) {
        post.isCollected = response.data.isCollected
      }
      // 更新详情页
      if (selectedPost.value && selectedPost.value.id === postId) {
        selectedPost.value.isCollected = response.data.isCollected
        if (selectedPost.value !== post) {
          selectedPost.value = { ...selectedPost.value }
        }
      }
      
      // 如果取消收藏，从列表中移除
      if (!response.data.isCollected) {
        collections.value = collections.value.filter(p => p.id !== postId)
        total.value = Math.max(0, total.value - 1)
        if (selectedPost.value && selectedPost.value.id === postId) {
          closeDetail()
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

// 处理删除动态
async function handleDelete(postId) {
  try {
    const response = await postsAPI.deletePost(postId)
    if (response.code === 0) {
      // 从本地列表中移除
      const index = collections.value.findIndex(p => p.id === postId)
      if (index !== -1) {
        collections.value.splice(index, 1)
      }
      
      // 如果删除的是当前查看的详情，关闭详情
      if (selectedPost.value && selectedPost.value.id === postId) {
        closeDetail()
      }
      
      if (store) store.showNotification('动态删除成功', 'success')
    } else {
      if (store) store.showNotification('删除失败', 'error')
    }
  } catch (error) {
    console.error('删除动态失败:', error)
    if (store) store.showNotification('删除失败', 'error')
  }
}

// 排序处理
function handleSort() {
  currentPage.value = 1
  fetchCollections()
}

// 分页处理
function prevPage() {
  if (currentPage.value > 1) {
    currentPage.value--
    fetchCollections()
  }
}

function nextPage() {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
    fetchCollections()
  }
}

// 打开详情
async function openDetail(post) {
  selectedPost.value = post
  showDetail.value = true
  detailComments.value = []
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

function closeDetail() {
  showDetail.value = false
  selectedPost.value = null
  detailComments.value = []
}

// 处理评论提交
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
          const post = collections.value.find(p => p.id === selectedPost.value.id)
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
          const post = collections.value.find(p => p.id === selectedPost.value.id)
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

// 生命周期
onMounted(() => {
  checkLoginStatus()
  if (isLoggedIn.value) {
    fetchCollections()
  }
  
  // 监听登录状态变化
  window.addEventListener('loginStatusChanged', () => {
    checkLoginStatus()
    if (isLoggedIn.value) {
      fetchCollections()
    }
  })
  
  // 监听页面可见性变化，确保数据同步
  document.addEventListener('visibilitychange', () => {
    if (!document.hidden && showDetail.value && selectedPost.value) {
      fetchComments(selectedPost.value.id)
    }
  })
})
</script>

<style scoped>
.page-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

.page-title {
  font-size: 1.8rem;
  margin: 0;
  color: var(--primary);
  display: flex;
  align-items: center;
}

.page-title i {
  margin-right: 10px;
}

.header-actions {
  display: flex;
  gap: 15px;
  align-items: center;
}

.sort-select {
  min-width: 120px;
}

.loading-container {
  text-align: center;
  padding: 40px;
  color: #666;
}

.loading-container i {
  font-size: 2rem;
  margin-bottom: 10px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #999;
}

.empty-state i {
  font-size: 4rem;
  margin-bottom: 20px;
  display: block;
}

.empty-state h3 {
  margin-bottom: 10px;
  color: #666;
}

.empty-state p {
  margin-bottom: 20px;
  color: #999;
}

.collections-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-bottom: 30px;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-top: 30px;
}

.page-info {
  font-weight: 600;
  color: #666;
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

.btn-outline {
  background: none;
  border: 1px solid var(--primary);
  color: var(--primary);
}

.btn-outline:hover:not(:disabled) {
  background: var(--primary);
  color: white;
}

.btn-outline:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 详情弹窗样式 */
.detail-dialog-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.detail-dialog {
  background: white;
  border-radius: 12px;
  max-width: 90vw;
  max-height: 90vh;
  overflow-y: auto;
  position: relative;
  padding: 20px;
}

.close-btn {
  position: absolute;
  top: 10px;
  right: 10px;
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #999;
  padding: 5px;
  border-radius: 4px;
  transition: all 0.2s;
}

.close-btn:hover {
  background: #f0f0f0;
  color: #333;
}
</style> 