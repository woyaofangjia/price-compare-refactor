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
        <div v-for="(post, index) in userPosts" :key="index" class="dynamic-card">
          <div class="dynamic-header">
            <div class="user-info">
              <img :src="post.userAvatar" :alt="post.username" class="user-avatar" />
              <div class="user-details">
                <div class="username">{{ post.username }}</div>
                <div class="post-time">{{ post.time }}</div>
              </div>
            </div>
            <div class="post-actions">
              <button class="action-btn" @click="editPost(post.id)">
                <i class="fas fa-edit"></i>
              </button>
              <button class="action-btn delete" @click="deletePost(post.id)">
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
                :src="img" 
                :alt="`图片${imgIndex + 1}`"
                class="content-image"
                @click="previewImage(img)"
              />
            </div>
            <div v-if="post.product" class="product-card">
              <img :src="post.product.image" :alt="post.product.name" class="product-image" />
              <div class="product-info">
                <div class="product-name">{{ post.product.name }}</div>
                <div class="product-price">{{ post.product.price }}</div>
                <div class="product-platform">{{ post.product.platform }}</div>
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
              <button class="action-btn" @click="likePost(post.id)">
                <i :class="post.isLiked ? 'fas fa-heart liked' : 'far fa-heart'"></i>
              </button>
              <button class="action-btn" @click="commentPost(post.id)">
                <i class="fas fa-comment"></i>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const isLoggedIn = ref(false)
const userPosts = ref([])

// 检查登录状态
function checkLoginStatus() {
  const token = localStorage.getItem('token')
  const user = localStorage.getItem('user')
  isLoggedIn.value = !!(token && user)
}

// 获取用户动态
function getUserPosts() {
  if (!isLoggedIn.value) return
  
  // 模拟从本地存储获取用户动态
  const storedPosts = localStorage.getItem('userPosts')
  if (storedPosts) {
    userPosts.value = JSON.parse(storedPosts)
  } else {
    // 模拟数据 - 添加更多测试数据
    userPosts.value = [
      {
        id: 1,
        username: '我',
        userAvatar: 'https://randomuser.me/api/portraits/men/32.jpg',
        time: '2小时前',
        content: '刚入手的新耳机，音质真的很棒！推荐给大家。',
        images: ['https://images.unsplash.com/photo-1572536147248-ac59a8abfa4b'],
        product: {
          name: 'Sony WH-1000XM5',
          price: '¥2,499',
          image: 'https://images.unsplash.com/photo-1572536147248-ac59a8abfa4b',
          platform: '京东 | 天猫'
        },
        likes: 12,
        comments: 3,
        isLiked: false
      },
      {
        id: 2,
        username: '我',
        userAvatar: 'https://randomuser.me/api/portraits/men/32.jpg',
        time: '1天前',
        content: '今天发现了一个很不错的购物网站，价格比实体店便宜很多！',
        images: ['https://images.unsplash.com/photo-1511707171634-5f897ff02aa9'],
        product: {
          name: '示例商品',
          price: '¥999',
          image: 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9',
          platform: '淘宝 | 拼多多'
        },
        likes: 8,
        comments: 2,
        isLiked: true
      },
      {
        id: 3,
        username: '我',
        userAvatar: 'https://randomuser.me/api/portraits/men/32.jpg',
        time: '3天前',
        content: '分享一个省钱小技巧：多平台比价真的很重要！',
        images: [],
        product: null,
        likes: 15,
        comments: 5,
        isLiked: false
      }
    ]
    localStorage.setItem('userPosts', JSON.stringify(userPosts.value))
  }
}

// 点赞动态
function likePost(postId) {
  const post = userPosts.value.find(p => p.id === postId)
  if (post) {
    post.isLiked = !post.isLiked
    post.likes += post.isLiked ? 1 : -1
    localStorage.setItem('userPosts', JSON.stringify(userPosts.value))
  }
}

// 评论动态
function commentPost(postId) {
  router.push(`/dynamic-detail/${postId}`)
}

// 编辑动态
function editPost(postId) {
  router.push(`/post/create?edit=${postId}`)
}

// 删除动态
function deletePost(postId) {
  console.log('尝试删除动态，ID:', postId)
  console.log('当前动态列表:', userPosts.value)
  
  // 更友好的确认对话框
  if (confirm('确定要删除这条动态吗？\n删除后将无法恢复。')) {
    try {
      // 从数组中移除动态
      const originalLength = userPosts.value.length
      userPosts.value = userPosts.value.filter(p => p.id !== postId)
      
      console.log('删除后动态列表:', userPosts.value)
      console.log('删除前长度:', originalLength, '删除后长度:', userPosts.value.length)
      
      // 检查是否真的删除了
      if (userPosts.value.length < originalLength) {
        // 更新本地存储
        localStorage.setItem('userPosts', JSON.stringify(userPosts.value))
        
        // 显示成功消息
        alert('动态删除成功！')
        
        // 触发页面更新事件
        window.dispatchEvent(new Event('loginStatusChanged'))
        
        console.log('动态删除成功，已更新本地存储')
      } else {
        console.error('删除失败，未找到要删除的动态，ID:', postId)
        alert('删除失败，未找到要删除的动态')
      }
    } catch (error) {
      console.error('删除动态时出错:', error)
      alert('删除动态时出现错误，请重试')
    }
  } else {
    console.log('用户取消了删除操作')
  }
}

// 预览图片
function previewImage(imgSrc) {
  // 这里可以实现图片预览功能
  console.log('预览图片:', imgSrc)
}

// 清空所有动态
function clearAllPosts() {
  if (confirm('确定要清空所有动态吗？\n清空后将无法恢复。')) {
    try {
      userPosts.value = []
      localStorage.removeItem('userPosts')
      alert('所有动态已清空！')
      window.dispatchEvent(new Event('loginStatusChanged'))
    } catch (error) {
      console.error('清空所有动态时出错:', error)
      alert('清空所有动态时出现错误，请重试')
    }
  }
}

onMounted(() => {
  checkLoginStatus()
  getUserPosts()
  
  // 监听登录状态变化
  window.addEventListener('loginStatusChanged', () => {
    checkLoginStatus()
    getUserPosts()
  })
})
</script>

<style scoped>
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
  display: flex;
  flex-direction: column;
  gap: 20px;
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

@media (max-width: 768px) {
  .dynamic-header {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }
  
  .btn {
    justify-content: center;
  }
  
  .image-grid {
    grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  }
  
  .content-image {
    height: 120px;
  }
}
</style> 