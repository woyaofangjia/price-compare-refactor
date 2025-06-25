<template>
  <div class="content-page">
    <div class="content-section">
      <div class="section-header">
        <div class="header-left">
          <button class="btn btn-outline" @click="$router.go(-1)">
            <i class="fas fa-arrow-left"></i> 返回
          </button>
          <h2 class="section-title">动态详情</h2>
        </div>
        <div class="header-actions">
          <button class="btn btn-danger" @click="deletePost">
            <i class="fas fa-trash"></i> 删除动态
          </button>
        </div>
      </div>
      
      <div class="post-detail-container">
        <!-- 动态内容 -->
        <div class="post-detail-card">
          <div class="post-header">
            <div class="post-avatar">{{ post.user.charAt(0) }}</div>
            <div class="post-info">
              <div class="post-user">{{ post.user }}</div>
              <div class="post-time">{{ post.time }}</div>
            </div>
          </div>
          
          <div class="post-content">
            <p>{{ post.content }}</p>
          </div>
          
          <img v-if="post.image" :src="post.image" alt="动态图片" class="post-image">
          
          <div class="post-stats">
            <div class="stat-item">
              <i class="fas fa-heart"></i>
              <span>{{ post.likes }}</span>
            </div>
            <div class="stat-item">
              <i class="fas fa-comment"></i>
              <span>{{ post.comments }}</span>
            </div>
            <div class="stat-item">
              <i class="fas fa-share"></i>
              <span>{{ post.shares }}</span>
            </div>
          </div>
        </div>
        
        <!-- 评论列表 -->
        <div class="comments-section">
          <div class="comments-header">
            <h3>评论列表 ({{ comments.length }})</h3>
          </div>
          
          <div class="comment-list">
            <div class="comment-item" v-for="(comment, index) in comments" :key="index">
              <div class="comment-avatar">{{ comment.user.charAt(0) }}</div>
              <div class="comment-content">
                <div class="comment-header">
                  <div class="comment-user">{{ comment.user }}</div>
                  <div class="comment-time">{{ comment.time }}</div>
                </div>
                <div class="comment-text">{{ comment.text }}</div>
              </div>
              <div class="comment-actions">
                <button class="btn btn-danger btn-sm" @click="deleteComment(index)">
                  <i class="fas fa-trash"></i> 删除
                </button>
              </div>
            </div>
          </div>
          
          <div v-if="comments.length === 0" class="no-comments">
            <i class="fas fa-comment-slash"></i>
            <p>暂无评论</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'PostDetail',
  data() {
    return {
      post: {
        id: 1,
        user: '@tech_lover',
        time: '2023-12-15 14:30',
        content: '今天在京东发现华为P60 Pro降价了，只要¥5499，比上个月便宜了500块，果断入手！',
        image: 'https://images.unsplash.com/photo-1595941069915-4ebc5197c14a?q=80&w=500&auto=format&fit=crop',
        likes: 128,
        comments: 24,
        shares: 9
      },
      comments: [
        {
          id: 1,
          user: '@gadget_guru',
          time: '2023-12-15 15:00',
          text: '这个价格确实很划算，我也在考虑入手'
        },
        {
          id: 2,
          user: '@shopper123',
          time: '2023-12-15 15:30',
          text: '华为P60 Pro的拍照功能真的很强大，值得推荐'
        },
        {
          id: 3,
          user: '@tech_reviewer',
          time: '2023-12-15 16:00',
          text: '性价比很高的一款手机，支持国产'
        }
      ]
    }
  },
  mounted() {
    // 从路由参数获取动态ID
    const postId = this.$route.params.id
    this.loadPostDetail(postId)
  },
  methods: {
    loadPostDetail(postId) {
      // 这里应该调用API获取动态详情
      // 暂时使用模拟数据
      console.log('加载动态详情:', postId)
    },
    
    deletePost() {
      if (confirm('确定要删除这条动态吗？此操作不可恢复。')) {
        // 这里应该调用API删除动态
        alert('动态删除成功')
        this.$router.go(-1)
      }
    },
    
    deleteComment(index) {
      if (confirm('确定要删除这条评论吗？')) {
        // 这里应该调用API删除评论
        this.comments.splice(index, 1)
        this.post.comments--
        alert('评论删除成功')
      }
    }
  }
}
</script>

<style scoped>
.content-page {
  padding: 20px;
}

.content-section {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
  overflow: hidden;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 25px;
  border-bottom: 1px solid var(--light-gray);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.section-title {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 600;
  color: var(--primary);
}

.btn {
  padding: 8px 16px;
  border-radius: 6px;
  border: none;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.btn-outline {
  background: transparent;
  border: 1px solid var(--light-gray);
  color: var(--gray);
}

.btn-outline:hover {
  background: var(--light);
  border-color: var(--primary);
  color: var(--primary);
}

.btn-danger {
  background: #dc3545;
  color: white;
}

.btn-danger:hover {
  background: #c82333;
}

.btn-sm {
  padding: 4px 8px;
  font-size: 0.8rem;
}

.post-detail-container {
  padding: 25px;
}

.post-detail-card {
  background: #f9fafc;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 25px;
}

.post-header {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.post-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: var(--light-gray);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  color: var(--primary);
  font-weight: bold;
  font-size: 1.2rem;
}

.post-user {
  font-weight: 600;
  font-size: 1.1rem;
  margin-bottom: 4px;
}

.post-time {
  font-size: 0.9rem;
  color: var(--gray);
}

.post-content {
  margin-bottom: 15px;
  font-size: 1rem;
  line-height: 1.6;
}

.post-image {
  width: 100%;
  border-radius: 10px;
  margin-bottom: 15px;
  max-height: 400px;
  object-fit: cover;
}

.post-stats {
  display: flex;
  gap: 20px;
  border-top: 1px solid var(--light-gray);
  padding-top: 15px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--gray);
  font-size: 0.9rem;
}

.comments-section {
  margin-top: 30px;
}

.comments-header {
  margin-bottom: 20px;
}

.comments-header h3 {
  margin: 0;
  font-size: 1.3rem;
  color: var(--primary);
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.comment-item {
  display: flex;
  align-items: flex-start;
  gap: 15px;
  padding: 15px;
  background: #f9fafc;
  border-radius: 8px;
  border: 1px solid var(--light-gray);
}

.comment-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: var(--light-gray);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--primary);
  font-weight: bold;
  flex-shrink: 0;
}

.comment-content {
  flex: 1;
}

.comment-user {
  font-weight: 600;
  margin-bottom: 4px;
}

.comment-time {
  font-size: 0.8rem;
  color: var(--gray);
  margin-bottom: 8px;
}

.comment-text {
  font-size: 0.95rem;
  line-height: 1.5;
}

.comment-actions {
  flex-shrink: 0;
}

.no-comments {
  text-align: center;
  padding: 40px 20px;
  color: var(--gray);
}

.no-comments i {
  font-size: 3rem;
  margin-bottom: 15px;
  opacity: 0.5;
}

.no-comments p {
  margin: 0;
  font-size: 1rem;
}
</style> 