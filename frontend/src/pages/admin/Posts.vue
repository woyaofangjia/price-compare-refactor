<template>
  <div class="content-page">
    <div class="content-section">
      <div class="section-header">
        <h2 class="section-title">用户动态管理</h2>
        <div class="section-actions">
          <div class="search-box">
            <input type="text" placeholder="搜索动态...">
            <button><i class="fas fa-search"></i></button>
          </div>
          <button class="btn btn-outline">筛选</button>
        </div>
      </div>
      
      <div class="posts-container">
        <div class="post-card" v-for="post in posts" :key="post.id">
          <div class="post-header">
            <div class="post-avatar">{{ post.user.charAt(0) }}</div>
            <div>
              <div class="post-user">{{ post.user }}</div>
              <div class="post-time">{{ post.time }}</div>
            </div>
          </div>
          <div class="post-content">
            <p>{{ post.content }}</p>
          </div>
          <img v-if="post.image" :src="post.image" alt="商品图片" class="post-image">
          <div class="post-actions">
            <div class="post-action">
              <i class="fas fa-heart"></i>
              <span>{{ post.likes }}</span>
            </div>
            <div class="post-action">
              <i class="fas fa-comment"></i>
              <span>{{ post.comments }}</span>
            </div>
            <div class="post-action">
              <i class="fas fa-share"></i>
              <span>{{ post.shares }}</span>
            </div>
          </div>
        </div>
      </div>
      
      <div class="pagination">
        <div 
          class="page-item" 
          v-for="page in 3" 
          :key="page" 
          :class="{active: currentPage === page}"
          @click="currentPage = page"
        >
          {{ page }}
        </div>
        <div class="page-item">...</div>
        <div class="page-item">5</div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Posts',
  data() {
    return {
      currentPage: 1,
      posts: [
        {
          id: 1,
          user: '@tech_lover',
          time: '2023-12-15 14:30',
          content: '今天在京东发现华为P60 Pro降价了，只要¥5499，比上个月便宜了500块，果断入手！',
          image: 'https://images.unsplash.com/photo-1595941069915-4ebc5197c14a?q=80&w=500&auto=format&fit=crop',
          likes: 128,
          comments: 24,
          shares: 9
        },
        {
          id: 2,
          user: '@gadget_guru',
          time: '2023-12-10 09:45',
          content: '分享一个省钱小技巧：使用比价系统监控商品价格变化，在最低价时收到提醒，最近用这个方法省了¥800！',
          image: null,
          likes: 245,
          comments: 56,
          shares: 42
        },
        {
          id: 3,
          user: '@shopper123',
          time: '2023-12-05 18:20',
          content: '双12购物清单分享：iPhone 15 Pro、索尼降噪耳机、戴森吹风机。哪个平台最便宜？',
          image: 'https://images.unsplash.com/photo-1606813907291-d86efa9b94db?q=80&w=500&auto=format&fit=crop',
          likes: 89,
          comments: 32,
          shares: 15
        }
      ]
    }
  }
}
</script>

<style scoped>
.post-card {
  background: #f9fafc;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 15px;
  box-shadow: 0 3px 10px rgba(0,0,0,0.05);
  transition: transform 0.3s ease;
}

.post-card:hover {
  transform: translateY(-3px);
}

.post-header {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.post-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: var(--light-gray);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 10px;
  color: var(--primary);
  font-weight: bold;
}

.post-user {
  font-weight: 600;
}

.post-time {
  font-size: 0.85rem;
  color: var(--gray);
}

.post-content {
  margin-bottom: 15px;
}

.post-image {
  width: 100%;
  border-radius: 10px;
  margin-bottom: 15px;
  max-height: 300px;
  object-fit: cover;
}

.post-actions {
  display: flex;
  gap: 15px;
  border-top: 1px solid var(--light-gray);
  padding-top: 15px;
}

.post-action {
  display: flex;
  align-items: center;
  color: var(--gray);
  cursor: pointer;
  transition: color 0.3s;
}

.post-action:hover {
  color: var(--primary);
}

.post-action i {
  margin-right: 5px;
}
</style>