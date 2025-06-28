    <template>
      <div class="main-container">
        <h1>个人中心</h1>
        <div class="user-info">
          <div class="avatar-section">
            <div class="avatar">
              <img :src="avatarUrl" alt="用户头像" @error="handleAvatarError">
            </div>
            <div class="avatar-actions">
              <input 
                type="file" 
                ref="fileInput" 
                accept="image/*" 
                @change="handleFileChange" 
                style="display: none"
              >
              <button @click="$refs.fileInput.click()" class="change-avatar-btn">
                更换头像
              </button>
            </div>
          </div>
          <div class="info-items">
            <div class="info-item">
              <span class="label">用户名:</span>
              <span class="value">{{ user.username }}</span>
            </div>
            <div class="info-item">
              <span class="label">邮箱:</span>
              <span class="value">{{ user.email || '未设置' }}</span>
            </div>
          </div>
        </div>
        <div class="user-actions">
          <router-link to="/profile/edit" class="edit-btn">修改信息</router-link>
          <button @click="logout" class="logout-btn">退出登录</button>
        </div>
        <nav class="main-nav">
          <router-link to="/" class="nav-link">首页</router-link>
          <router-link to="/profile" class="nav-link">个人中心</router-link>
        </nav>
      </div>
    </template>

    <script setup>
    import { useRouter } from 'vue-router'
    import { ref, onMounted, computed } from 'vue'
    import axios from 'axios'

    const router = useRouter()
    const user = ref({})
    const fileInput = ref(null)

    // 计算头像URL
    const avatarUrl = computed(() => {
      if (user.value.avatar) {
        // 如果是完整URL，直接使用
        if (user.value.avatar.startsWith('http')) {
          return user.value.avatar
        }
        // 如果是相对路径，添加后端基础URL
        return `http://localhost:3000${user.value.avatar}`
      }
      // 默认头像
      return 'https://picsum.photos/seed/user/100/100'
    })

    function getUser() {
      const u = localStorage.getItem('user')
      user.value = u ? JSON.parse(u) : {}
      
      // 如果用户信息不完整，从服务器获取完整信息
      if (user.value.id && !user.value.email) {
        fetchUserProfile()
      }
    }

    async function fetchUserProfile() {
      try {
        const token = localStorage.getItem('token')
        if (!token) return
        
        const response = await axios.get('/api/auth/profile', {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        })
        
        if (response.data) {
          user.value = response.data
          localStorage.setItem('user', JSON.stringify(response.data))
        }
      } catch (error) {
        console.error('获取用户信息失败:', error)
      }
    }

    function handleAvatarError(event) {
      // 头像加载失败时使用默认头像
      event.target.src = 'https://picsum.photos/seed/user/100/100'
    }

    async function handleFileChange(event) {
      const file = event.target.files[0]
      if (!file) return

      console.log('选择的文件:', file.name, file.size, file.type)

      // 验证文件类型
      if (!file.type.startsWith('image/')) {
        alert('请选择图片文件')
        return
      }

      // 验证文件大小（5MB）
      if (file.size > 5 * 1024 * 1024) {
        alert('图片大小不能超过5MB')
        return
      }

      try {
        const formData = new FormData()
        formData.append('avatar', file)

        const token = localStorage.getItem('token')
        console.log('发送请求到:', '/api/upload/avatar')
        console.log('Token:', token ? '存在' : '不存在')
        
        const response = await axios.post('/api/upload/avatar', formData, {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'multipart/form-data'
          },
          timeout: 10000 // 10秒超时
        })

        console.log('上传响应:', response)

        if (response.data.avatarUrl) {
          // 更新本地用户信息
          user.value.avatar = response.data.avatarUrl
          localStorage.setItem('user', JSON.stringify(user.value))
          
          // 触发全局登录状态更新
          window.dispatchEvent(new Event('loginStatusChanged'))
          
          alert('头像上传成功！')
        }
      } catch (error) {
        console.error('头像上传失败:', error)
        console.error('错误详情:', {
          message: error.message,
          code: error.code,
          response: error.response?.data,
          status: error.response?.status
        })
        
        if (error.response && error.response.data && error.response.data.message) {
          alert(error.response.data.message)
        } else if (error.code === 'ERR_NETWORK') {
          alert('网络连接失败，请检查网络连接或稍后再试')
        } else {
          alert('头像上传失败，请稍后再试')
        }
      }

      // 清空文件输入
      event.target.value = ''
    }

    function logout() {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      window.dispatchEvent(new Event('loginStatusChanged'))
      router.push('/login')
    }

    onMounted(() => {
      getUser()
    })
    </script>

    <style scoped>
    .main-container {
      min-height: 100vh;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      padding: 20px 0;
    }

    h1 {
      color: #1e293b;
      font-size: 2rem;
      font-weight: 700;
      margin-bottom: 24px;
      text-align: center;
    }

    .user-info {
      background: #fff;
      border-radius: 16px;
      box-shadow: 0 4px 24px 0 rgba(34, 51, 84, 0.12);
      padding: 30px;
      width: 100%;
      max-width: 500px;
      margin-bottom: 24px;
      display: flex;
      align-items: flex-start;
      gap: 30px;
    }

    .avatar-section {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 16px;
    }

    .avatar img {
      width: 100px;
      height: 100px;
      border-radius: 50%;
      object-fit: cover;
      border: 4px solid #fff;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    }

    .change-avatar-btn {
      padding: 8px 16px;
      background: #6366f1;
      color: white;
      border: none;
      border-radius: 8px;
      font-size: 0.9rem;
      cursor: pointer;
      transition: all 0.3s ease;
    }

    .change-avatar-btn:hover {
      background: #4f46e5;
      transform: translateY(-1px);
    }

    .info-items {
      flex: 1;
    }

    .info-item {
      display: flex;
      margin-bottom: 16px;
      align-items: center;
    }

    .info-item:last-child {
      margin-bottom: 0;
    }

    .label {
      width: 100px;
      color: #64748b;
      font-size: 0.95rem;
    }

    .value {
      color: #1e293b;
      font-weight: 500;
    }

    .user-actions {
      display: flex;
      gap: 16px;
      margin-bottom: 24px;
      width: 100%;
      max-width: 500px;
    }

    .edit-btn, .logout-btn {
      flex: 1;
      padding: 12px;
      border-radius: 8px;
      font-size: 1rem;
      font-weight: 600;
      text-align: center;
      cursor: pointer;
      transition: all 0.3s ease;
    }

    .edit-btn {
      background-color: #2196F3;
      color: white;
    }

    .logout-btn {
      background-color: #f44336;
      color: white;
      border: none;
    }

    .main-nav {
      margin-top: 30px;
      display: flex;
      justify-content: center;
      gap: 20px;
    }

    .nav-link {
      text-decoration: none;
      color: #2c3e50;
      font-weight: bold;
      padding: 5px 10px;
      border-radius: 4px;
    }

    .nav-link:hover {
      background-color: #f0f0f0;
    }

    @media (prefers-color-scheme: dark) {
      body { color: white; }
    }
    </style>
  