<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">
        <i class="fas fa-pen"></i> {{ isEditing ? '编辑动态' : '发布动态' }}
      </h1>
      <router-link to="/my-dynamic">
        <button class="btn btn-outline">
          <i class="fas fa-arrow-left"></i> 返回
        </button>
      </router-link>
    </div>
    <div class="card">
      <div class="card-title">分享购物体验</div>
      <textarea 
        v-model="content" 
        class="form-control" 
        placeholder="分享你的购物体验或发现..."
        rows="4"
      ></textarea>
      
      <div class="upload-area" @click="addImage">
        <i class="fas fa-cloud-upload-alt"></i>
        <p>点击上传图片 (最多4张)</p>
        <span class="upload-hint">已上传 {{ images.length }}/4 张图片</span>
      </div>
      
      <div v-if="images.length > 0" class="preview-grid">
        <div class="preview-item" v-for="(img, index) in images" :key="index">
          <img :src="img" class="preview-img" />
          <div class="remove-btn" @click.stop="removeImage(index)">
            <i class="fas fa-times"></i>
          </div>
        </div>
      </div>
      
      <div class="form-actions">
        <button 
          class="btn btn-primary" 
          @click="submitPost"
          :disabled="!content.trim()"
        >
          <i class="fas fa-paper-plane"></i> 
          {{ isEditing ? '更新动态' : '发布动态' }}
        </button>
        <button 
          v-if="isEditing" 
          class="btn btn-outline" 
          @click="cancelEdit"
        >
          <i class="fas fa-times"></i> 取消编辑
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

const content = ref('')
const images = ref([])
const isEditing = ref(false)
const editPostId = ref(null)

// 检查是否为编辑模式
function checkEditMode() {
  const editId = route.query.edit
  if (editId) {
    isEditing.value = true
    editPostId.value = parseInt(editId)
    loadPostForEdit(editPostId.value)
  }
}

// 加载要编辑的动态
function loadPostForEdit(postId) {
  const storedPosts = localStorage.getItem('userPosts')
  if (storedPosts) {
    const posts = JSON.parse(storedPosts)
    const post = posts.find(p => p.id === postId)
    if (post) {
      content.value = post.content
      images.value = post.images || []
    }
  }
}

function addImage() {
  if (images.value.length < 4) {
    // 模拟图片上传
    const mockImages = [
      'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9',
      'https://images.unsplash.com/photo-1572536147248-ac59a8abfa4b',
      'https://images.unsplash.com/photo-1505740420928-5e560c06d30e',
      'https://images.unsplash.com/photo-1523275335684-37898b6baf30'
    ]
    const randomImage = mockImages[Math.floor(Math.random() * mockImages.length)]
    images.value.push(randomImage)
  }
}

function removeImage(index) {
  images.value.splice(index, 1)
}

function submitPost() {
  if (!content.value.trim()) {
    alert('请输入动态内容')
    return
  }

  const postData = {
    content: content.value,
    images: images.value,
    time: new Date().toLocaleString('zh-CN', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    })
  }

  if (isEditing.value) {
    updatePost(postData)
  } else {
    createPost(postData)
  }
}

function createPost(postData) {
  const newPost = {
    id: Date.now(), // 使用时间戳作为ID
    username: '我',
    userAvatar: 'https://randomuser.me/api/portraits/men/32.jpg',
    time: postData.time,
    content: postData.content,
    images: postData.images,
    product: {
      name: '示例商品',
      price: '¥999',
      image: 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9',
      platform: '京东 | 天猫'
    },
    likes: 0,
    comments: 0,
    isLiked: false
  }

  // 保存到本地存储
  const storedPosts = localStorage.getItem('userPosts')
  const posts = storedPosts ? JSON.parse(storedPosts) : []
  posts.unshift(newPost) // 添加到开头
  localStorage.setItem('userPosts', JSON.stringify(posts))

  // 触发登录状态变化事件，更新其他页面的数据
  window.dispatchEvent(new Event('loginStatusChanged'))

  alert('动态发布成功！')
  router.push('/dynamic')
}

function updatePost(postData) {
  const storedPosts = localStorage.getItem('userPosts')
  if (storedPosts) {
    const posts = JSON.parse(storedPosts)
    const postIndex = posts.findIndex(p => p.id === editPostId.value)
    
    if (postIndex !== -1) {
      posts[postIndex].content = postData.content
      posts[postIndex].images = postData.images
      posts[postIndex].time = postData.time + ' (已编辑)'
      
      localStorage.setItem('userPosts', JSON.stringify(posts))
      
      // 触发登录状态变化事件
      window.dispatchEvent(new Event('loginStatusChanged'))
      
      alert('动态更新成功！')
      router.push('/dynamic')
    }
  }
}

function cancelEdit() {
  if (confirm('确定要取消编辑吗？未保存的内容将丢失。')) {
    router.push('/dynamic')
  }
}

onMounted(() => {
  checkEditMode()
})
</script>

<style scoped>
.page-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.page-title {
  font-size: 1.8rem;
  color: var(--primary);
  display: flex;
  align-items: center;
}

.page-title i {
  margin-right: 10px;
}

.card {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
}

.card-title {
  font-size: 1.3rem;
  font-weight: 600;
  margin-bottom: 20px;
  color: var(--dark);
}

.form-control {
  width: 100%;
  padding: 15px;
  border: 2px solid var(--light-gray);
  border-radius: 8px;
  font-size: 1rem;
  resize: vertical;
  transition: border-color 0.3s ease;
}

.form-control:focus {
  outline: none;
  border-color: var(--primary);
}

.upload-area {
  border: 2px dashed var(--light-gray);
  border-radius: 8px;
  padding: 40px 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  margin: 20px 0;
}

.upload-area:hover {
  border-color: var(--primary);
  background: rgba(67, 97, 238, 0.05);
}

.upload-area i {
  font-size: 3rem;
  color: var(--gray);
  margin-bottom: 15px;
}

.upload-area p {
  font-size: 1.1rem;
  color: var(--dark);
  margin-bottom: 10px;
}

.upload-hint {
  font-size: 0.9rem;
  color: var(--gray);
}

.preview-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 15px;
  margin: 20px 0;
}

.preview-item {
  position: relative;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.preview-img {
  width: 100%;
  height: 150px;
  object-fit: cover;
}

.remove-btn {
  position: absolute;
  top: 8px;
  right: 8px;
  background: rgba(0,0,0,0.7);
  color: white;
  border: none;
  border-radius: 50%;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background 0.3s ease;
}

.remove-btn:hover {
  background: rgba(231, 76, 60, 0.8);
}

.form-actions {
  display: flex;
  gap: 15px;
  margin-top: 30px;
}

.btn {
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  font-weight: 500;
  transition: all 0.3s ease;
  font-size: 1rem;
}

.btn-primary {
  background: var(--primary);
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background: var(--secondary);
  transform: translateY(-2px);
}

.btn-primary:disabled {
  background: var(--gray);
  cursor: not-allowed;
  transform: none;
}

.btn-outline {
  background: transparent;
  color: var(--primary);
  border: 2px solid var(--primary);
}

.btn-outline:hover {
  background: var(--primary);
  color: white;
}

.btn i {
  margin-right: 8px;
}

@media (max-width: 768px) {
  .page-container {
    padding: 15px;
  }
  
  .page-header {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }
  
  .form-actions {
    flex-direction: column;
  }
  
  .preview-grid {
    grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  }
  
  .preview-img {
    height: 120px;
  }
}
</style>
