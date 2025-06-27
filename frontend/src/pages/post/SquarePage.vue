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
    <div v-else-if="posts.length > 0" class="card">
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
          @like="handleLike"
          @collect="handleCollect"
          @submit-comment="onSubmitComment"
          @delete-comment="onDeleteComment"
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

// 计算属性
const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

// 获取动态列表
async function fetchPosts() {
  try {
    loading.value = true
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    const userId = user.id
    if (!userId) {
      if (store) store.showNotification('请先登录后再查看动态广场', 'warning')
      loading.value = false
      return
    }
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchKeyword.value,
      sort: sortType.value,
      userId
    }
    
    const response = await postsAPI.getPosts(params)
    if (response.code === 0) {
      posts.value = response.data.list
      total.value = response.data.total
    } else {
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
  try {
    const response = await postsAPI.toggleLike(postId)
    if (response.code === 0) {
      // 更新本地数据
      const post = posts.value.find(p => p.id === postId)
      if (post) {
        post.likes = response.data.likes
        post.isLiked = response.data.isLiked
      }
      
      // 更新详情页数据
      if (selectedPost.value && selectedPost.value.id === postId) {
        selectedPost.value.likes = response.data.likes
        selectedPost.value.isLiked = response.data.isLiked
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
    const response = await postsAPI.toggleCollect(postId)
    if (response.code === 0) {
      // 更新本地数据
      const post = posts.value.find(p => p.id === postId)
      if (post) {
        post.isCollected = response.data.isCollected
      }
      
      // 更新详情页数据
      if (selectedPost.value && selectedPost.value.id === postId) {
        selectedPost.value.isCollected = response.data.isCollected
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
  
  // 这里可以加载评论数据
  // const response = await postsAPI.getComments(post.id)
  // detailComments.value = response.data || []
}

function closeDetail() {
  showDetail.value = false
  selectedPost.value = null
}

function onSubmitComment(comment) {
  // 这里可以调用评论API
  detailComments.value.push({ 
    author: currentUser.value?.username || '你', 
    text: comment, 
    time: '刚刚' 
  })
}

function onDeleteComment(index) {
  detailComments.value.splice(index, 1)
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

.card {
  position: relative;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 32px;
  background: none;
  box-shadow: none;
  padding: 0;
}

@media (max-width: 1000px) {
  .card {
    grid-template-columns: 1fr;
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
