<template>
  <div class="page-container">
    <TrendingList />
    <div class="page-header">
      <h1 class="page-title"><i class="fas fa-comments"></i> 动态广场</h1>
      <router-link to="/post/create" class="btn btn-primary publish-btn">
        <i class="fas fa-plus"></i> 发布动态
      </router-link>
    </div>
    
    <!-- 筛选和搜索 -->
    <div class="filter-section">
      <div class="search-box">
        <input 
          v-model="searchKeyword" 
          type="text" 
          placeholder="搜索动态内容..."
          @input="handleSearch"
          class="search-input"
        />
        <i class="fas fa-search search-icon"></i>
      </div>
      <div class="sort-select">
        <select class="form-control" v-model="sortType" @change="handleSort">
          <option value="latest">最新发布</option>
          <option value="likes">最多点赞</option>
          <option value="comments">最多评论</option>
        </select>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <i class="fas fa-spinner fa-spin"></i>
      <p>加载中...</p>
    </div>

    <!-- 动态列表 -->
    <div v-else-if="posts.length > 0" class="posts-container">
      <DynamicCard
        v-for="(post, index) in posts"
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
      <i class="fas fa-comments"></i>
      <p>暂无动态，快来发布第一条动态吧！</p>
      <router-link to="/post/create" class="btn btn-primary">
        <i class="fas fa-plus"></i> 发布动态
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
import DynamicCard from './components/DynamicCard.vue';
import TrendingList from './components/TrendingList.vue';
import DynamicDetailContent from './components/DynamicDetailContent.vue';

const store = inject('store')

// 响应式数据
const posts = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchKeyword = ref('')
const sortType = ref('latest')
const showDetail = ref(false)
const selectedPost = ref(null)
const detailComments = ref([])
const currentUser = ref(null)
// 评论相关状态
const commentPage = ref(1)
const commentPageSize = ref(20)
const commentTotal = ref(0)
const commentSort = ref('latest')
const commentLoading = ref(false)
const commentSubmitting = ref(false)

// 计算属性
const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

// 获取动态列表
async function fetchPosts() {
  try {
    loading.value = true
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchKeyword.value,
      sort: sortType.value
    }
    
    console.log('请求参数:', params)
    const response = await postsAPI.getPosts(params)
    console.log('API响应:', response)
    
    if (response.code === 0) {
      posts.value = response.data.list
      total.value = response.data.total
      
      // 调试信息：检查每个动态的图片数据
      posts.value.forEach((post, index) => {
        console.log(`动态 ${index + 1}:`, {
          id: post.id,
          content: post.content,
          images: post.images,
          imagesLength: post.images ? post.images.length : 0
        })
      })
    } else {
      console.error('API返回错误:', response)
      if (store) store.showNotification('获取动态列表失败', 'error')
    }
  } catch (error) {
    console.error('获取动态列表失败:', error)
    if (store) store.showNotification('获取动态列表失败', 'error')
  } finally {
    loading.value = false
  }
}

// 处理点赞
async function handleLike(postId) {
  if (!postId) return
  const token = localStorage.getItem('token')
  if (!token) {
    if (store) store.showNotification('请先登录', 'error')
    return
  }
  try {
    const currentPost = posts.value.find(p => p.id === postId)
    const currentIsLiked = currentPost?.isLiked || false
    const response = await postsAPI.toggleLike(postId, !currentIsLiked)
    if (response.code === 0) {
      // 点赞后强制刷新该动态的最新数据
      const postDetail = await postsAPI.getPostById(postId)
      if (postDetail.code === 0 && postDetail.data) {
        // 更新主列表
        const post = posts.value.find(p => p.id === postId)
        if (post) {
          post.likes = postDetail.data.likes
          post.isLiked = postDetail.data.isLiked
        }
        // 更新详情页
        if (selectedPost.value && selectedPost.value.id === postId) {
          selectedPost.value.likes = postDetail.data.likes
          selectedPost.value.isLiked = postDetail.data.isLiked
          if (selectedPost.value !== post) {
            selectedPost.value = { ...selectedPost.value }
          }
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
    if (store) store.showNotification('操作失败', 'error')
  }
}

// 处理收藏
async function handleCollect(postId) {
  try {
    // 找到当前动态，获取其收藏状态
    const currentPost = posts.value.find(p => p.id === postId)
    const currentIsCollected = currentPost?.isCollected || false
    
    const response = await postsAPI.toggleCollect(postId, !currentIsCollected)
    if (response.code === 0) {
      // 更新主列表
      const post = posts.value.find(p => p.id === postId)
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
      const index = posts.value.findIndex(p => p.id === postId)
      if (index !== -1) {
        posts.value.splice(index, 1)
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

// 搜索处理
function handleSearch() {
  currentPage.value = 1
  fetchPosts()
}

// 排序处理
function handleSort() {
  currentPage.value = 1
  fetchPosts()
}

// 分页处理
function prevPage() {
  if (currentPage.value > 1) {
    currentPage.value--
    fetchPosts()
  }
}

function nextPage() {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
    fetchPosts()
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
          const post = posts.value.find(p => p.id === selectedPost.value.id)
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
          const post = posts.value.find(p => p.id === selectedPost.value.id)
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

// 获取当前用户信息
function getCurrentUser() {
  const user = localStorage.getItem('user')
  if (user) {
    currentUser.value = JSON.parse(user)
  }
}

// 生命周期
onMounted(() => {
  getCurrentUser()
  fetchPosts()
  
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
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

.publish-btn {
  margin-left: auto;
  float: right;
}

.filter-section {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
  align-items: center;
}

.search-box {
  position: relative;
  flex: 1;
}

.search-input {
  width: 100%;
  padding: 10px 40px 10px 15px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
}

.search-icon {
  position: absolute;
  right: 15px;
  top: 50%;
  transform: translateY(-50%);
  color: #999;
}

.sort-select {
  min-width: 120px;
}

.form-control {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
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
  color: #666;
}

.empty-state i {
  font-size: 3rem;
  margin-bottom: 15px;
  color: #ddd;
}

.empty-state p {
  margin-bottom: 20px;
  font-size: 1.1rem;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
  margin-top: 30px;
}

.page-info {
  font-size: 14px;
  color: #666;
}

.posts-container {
  position: relative;
  display: flex;
  flex-direction: column;
  gap: 20px;
  background: none;
  box-shadow: none;
  padding: 0;
}

@media (max-width: 1000px) {
  .posts-container {
    gap: 15px;
  }
  
  .filter-section {
    flex-direction: column;
    align-items: stretch;
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
