<template>
  <div class="card">
    <div class="card-header">
      <div class="card-title">推荐动态</div>
    </div>
    <div v-if="loading" class="loading">
      <i class="fas fa-spinner fa-spin"></i>
    </div>
    <ul v-else style="text-align:left;">
      <li 
        v-for="(item, index) in trendingPosts" 
        :key="item.id || index" 
        class="trending-item"
        @click="handleClick(item)"
      >
        <div class="trending-content">
          <span class="trending-title">{{ item.content }}</span>
          <div class="trending-stats">
            <span class="likes"><i class="fas fa-heart"></i> {{ item.likes || 0 }}</span>
            <span class="comments"><i class="fas fa-comment"></i> {{ item.comments || 0 }}</span>
          </div>
        </div>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue'
import { postsAPI } from '@/api/posts.js'

const store = inject('store')
const trendingPosts = ref([])
const loading = ref(false)

// 获取推荐动态
async function fetchTrendingPosts() {
  try {
    loading.value = true
    const response = await postsAPI.getRecommendPosts()
    if (response.code === 0) {
      trendingPosts.value = response.data.slice(0, 5) // 只显示前5条
    } else {
      console.error('获取推荐动态失败:', response.message)
    }
  } catch (error) {
    console.error('获取推荐动态失败:', error)
  } finally {
    loading.value = false
  }
}

// 点击热门动态
function handleClick(post) {
  // 这里可以跳转到动态详情或触发事件
  console.log('点击热门动态:', post)
}

onMounted(() => {
  fetchTrendingPosts()
})
</script>

<style scoped>
.trending-item {
  color: rgba(60,60,60,0.5);
  font-size: 1.1rem;
  margin-bottom: 12px;
  font-weight: 500;
  cursor: pointer;
  padding: 8px;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.trending-item:hover {
  background: rgba(67, 97, 238, 0.05);
  color: rgba(60,60,60,0.8);
}

.trending-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.trending-title {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-right: 10px;
}

.trending-stats {
  display: flex;
  gap: 10px;
  font-size: 0.9rem;
  color: #999;
}

.trending-stats i {
  margin-right: 2px;
}

.loading {
  text-align: center;
  padding: 20px;
  color: #666;
}

.loading i {
  font-size: 1.5rem;
}
</style>
