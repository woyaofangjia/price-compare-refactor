<template>
  <div class="square-page">
    <div class="header">
      <div class="logo">
        <el-icon :size="32" color="#409EFF"><ShoppingBag /></el-icon>
        <h1>比价宝</h1>
      </div>
      <div class="user-actions">
        <el-button type="primary" icon="Search">搜索商品</el-button>
        <el-button icon="User">个人中心</el-button>
      </div>
    </div>
    
    <div class="main-content">
      <h2 class="page-title">
        <el-icon :size="28" color="#409EFF"><ChatLineRound /></el-icon>
        动态广场
      </h2>
      
      <div class="filter-bar">
        <el-button :type="filter === 'all' ? 'primary' : ''" @click="filter = 'all'">全部动态</el-button>
        <el-button :type="filter === 'following' ? 'primary' : ''" @click="filter = 'following'">我关注的</el-button>
        <el-button :type="filter === 'popular' ? 'primary' : ''" @click="filter = 'popular'">热门动态</el-button>
        <el-select v-model="sortBy" placeholder="排序方式" style="width: 120px; margin-left: auto;">
          <el-option label="最新发布" value="newest"></el-option>
          <el-option label="最多点赞" value="likes"></el-option>
          <el-option label="最多评论" value="comments"></el-option>
        </el-select>
      </div>
      
      <div class="dynamic-list">
        <div v-if="filteredDynamics.length === 0" class="empty-state">
          <el-icon :size="80" color="#c0c4cc"><ChatLineSquare /></el-icon>
          <div class="empty-text">暂无动态，快来发布第一条吧！</div>
          <el-button type="primary" icon="Edit" @click="goToPost">发布动态</el-button>
        </div>
        
        <DynamicCard 
          v-for="dynamic in filteredDynamics" 
          :key="dynamic.id" 
          :dynamic="dynamic"
          @like="toggleLike"
          @collect="toggleCollect"
          @delete="deleteDynamic"
          @view-detail="goToDetail"
        />
        
        <div class="pagination">
          <el-pagination
            background
            layout="prev, pager, next"
            :total="50"
            :page-size="5"
            @current-change="handlePageChange"
          />
        </div>
      </div>
      
      <div class="sidebar">
        <TrendingList :topics="trendingTopics" />
        
        <el-button type="primary" icon="Edit" class="post-btn" @click="goToPost">发布动态</el-button>
        
        <h3 class="sidebar-title" style="margin-top: 30px;">
          <el-icon :size="18"><User /></el-icon>
          推荐关注
        </h3>
        
        <div class="trending-list">
          <UserCard 
            v-for="user in recommendedUsers" 
            :key="user.id" 
            :user="user"
            @follow="toggleFollow"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ShoppingBag, ChatLineRound, ChatLineSquare, Edit } from '@element-plus/icons-vue'
import DynamicCard from './components/DynamicCard.vue'
import TrendingList from './components/TrendingList.vue'
import UserCard from './components/UserCard.vue'
import { fetchDynamics } from './utils/api'

const router = useRouter()

// 状态管理
const filter = ref('all')
const sortBy = ref('newest')
const dynamics = ref([])
const currentUserId = ref(101) // 模拟当前用户ID
const currentPage = ref(1)

// 模拟数据
const trendingTopics = ref([
  { name: '双十一购物攻略', count: 1242 },
  { name: '家电选购指南', count: 876 },
  { name: '数码新品评测', count: 765 },
  { name: '省钱小技巧', count: 632 },
  { name: '黑五海淘', count: 521 }
])

const recommendedUsers = ref([
  { id: 201, name: '数码测评君', title: '数码产品评测专家', avatar: 'https://randomuser.me/api/portraits/men/32.jpg', isFollowing: false },
  { id: 202, name: '家电小百科', title: '家电选购顾问', avatar: 'https://randomuser.me/api/portraits/women/44.jpg', isFollowing: true },
  { id: 203, name: '时尚买手', title: '时尚潮流引领者', avatar: 'https://randomuser.me/api/portraits/women/68.jpg', isFollowing: false }
])

// 计算属性
const filteredDynamics = computed(() => {
  let result = [...dynamics.value]
  
  // 应用筛选
  if (filter.value === 'following') {
    const followingIds = [102, 104]
    result = result.filter(d => followingIds.includes(d.userId))
  } else if (filter.value === 'popular') {
    result = result.filter(d => d.likes > 50)
  }
  
  // 应用排序
  if (sortBy.value === 'likes') {
    result.sort((a, b) => b.likes - a.likes)
  } else if (sortBy.value === 'comments') {
    result.sort((a, b) => b.comments - a.comments)
  } else {
    // 默认按时间倒序
    result.sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp))
  }
  
  // 分页
  const pageSize = 5
  const start = (currentPage.value - 1) * pageSize
  const end = start + pageSize
  return result.slice(start, end)
})

// 初始化数据
onMounted(async () => {
  dynamics.value = await fetchDynamics()
})

// 方法
const toggleLike = (id) => {
  const dynamic = dynamics.value.find(d => d.id === id)
  if (dynamic) {
    dynamic.isLiked = !dynamic.isLiked
    dynamic.likes += dynamic.isLiked ? 1 : -1
  }
}

const toggleCollect = (id) => {
  const dynamic = dynamics.value.find(d => d.id === id)
  if (dynamic) {
    dynamic.isCollected = !dynamic.isCollected
  }
}

const toggleFollow = (userId) => {
  const user = recommendedUsers.value.find(u => u.id === userId)
  if (user) {
    user.isFollowing = !user.isFollowing
  }
}

const deleteDynamic = (id) => {
  dynamics.value = dynamics.value.filter(d => d.id !== id)
}

const goToDetail = (id) => {
  router.push({ name: 'dynamic-detail', params: { id } })
}

const goToPost = () => {
  router.push({ name: 'post-dynamic' })
}

const handlePageChange = (page) => {
  currentPage.value = page
}
</script>

<style scoped lang="scss">
@import './styles/square-page';
</style>