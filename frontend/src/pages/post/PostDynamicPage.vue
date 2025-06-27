<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">
        <i class="fas fa-pen"></i> {{ isEditing ? '编辑动态' : '发布动态' }}
      </h1>
      <router-link to="/square">
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
          :disabled="!content.trim() || loading"
        >
          <i class="fas fa-spinner fa-spin" v-if="loading"></i>
          <i class="fas fa-paper-plane" v-else></i> 
          {{ isEditing ? '更新动态' : '发布动态' }}
        </button>
        <button 
          v-if="isEditing" 
          class="btn btn-outline" 
          @click="cancelEdit"
          :disabled="loading"
        >
          <i class="fas fa-times"></i> 取消编辑
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { postsAPI, uploadAPI } from '@/api/posts.js'

const router = useRouter()
const route = useRoute()
const store = inject('store')

const content = ref('')
const images = ref([])
const isEditing = ref(false)
const editPostId = ref(null)
const loading = ref(false)

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
async function loadPostForEdit(postId) {
  try {
    loading.value = true
    const response = await postsAPI.getPostById(postId)
    if (response.code === 0) {
      const post = response.data
      content.value = post.content
      images.value = post.images || []
    } else {
      if (store) store.showNotification('加载动态失败', 'error')
    }
  } catch (error) {
    console.error('加载动态失败:', error)
    if (store) store.showNotification('加载动态失败', 'error')
  } finally {
    loading.value = false
  }
}

async function addImage() {
  if (images.value.length >= 4) {
    if (store) store.showNotification('最多只能上传4张图片', 'warning')
    return
  }

  // 创建文件输入元素
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = 'image/*'
  input.multiple = false

  input.onchange = async (event) => {
    const file = event.target.files[0]
    if (!file) return

    // 验证文件大小 (5MB)
    if (file.size > 5 * 1024 * 1024) {
      if (store) store.showNotification('图片大小不能超过5MB', 'error')
      return
    }

    // 验证文件类型
    if (!file.type.startsWith('image/')) {
      if (store) store.showNotification('请选择图片文件', 'error')
      return
    }

    try {
      loading.value = true
      const user = JSON.parse(localStorage.getItem('user') || '{}')
      const response = await uploadAPI.uploadImage(file, user.id || 1)
      
      if (response.data && response.data.code === 0) {
        images.value.push(response.data.data.url)
        if (store) store.showNotification('图片上传成功', 'success')
      } else {
        if (store) store.showNotification('图片上传失败', 'error')
      }
    } catch (error) {
      console.error('图片上传失败:', error)
      if (store) store.showNotification('图片上传失败', 'error')
    } finally {
      loading.value = false
    }
  }

  input.click()
}

function removeImage(index) {
  images.value.splice(index, 1)
}

async function submitPost() {
  if (!content.value.trim()) {
    if (store) store.showNotification('请输入动态内容', 'warning')
    return
  }

  try {
    loading.value = true
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    
    // 检查用户登录状态
    if (!user.id) {
      if (store) store.showNotification('请先登录', 'error')
      return
    }
    
    const postData = {
      content: content.value,
      images: images.value,
      userId: user.id,
      timestamp: new Date().toISOString()
    }

    // 调试信息
    console.log('发送的数据:', postData)
    console.log('用户信息:', user)

    let response
    if (isEditing.value) {
      response = await postsAPI.updatePost(editPostId.value, postData)
    } else {
      response = await postsAPI.createPost(postData)
    }

    if (response.code === 0) {
      if (store) {
        store.showNotification(
          isEditing.value ? '动态更新成功' : '动态发布成功', 
          'success'
        )
      }
      router.push('/square')
    } else {
      if (store) store.showNotification(response.message || '操作失败', 'error')
    }
  } catch (error) {
    console.error('提交动态失败:', error)
    if (store) store.showNotification('操作失败，请稍后重试', 'error')
  } finally {
    loading.value = false
  }
}

function cancelEdit() {
  if (confirm('确定要取消编辑吗？未保存的内容将丢失。')) {
    router.push('/square')
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
