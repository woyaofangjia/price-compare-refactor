    <template>
      <div class="page-container">
        <div class="card">
          <div class="card-header">
            <h2>用户登录</h2>
            <p>欢迎回来！请登录您的账号</p>
          </div>
          <div class="form-group">
            <label for="username">用户名</label>
            <div class="input-with-icon">
              <i class="fas fa-user"></i>
              <input type="text" id="username" v-model="form.username" placeholder="请输入用户名">
            </div>
            <div class="error-message" v-if="errors.username">{{ errors.username }}</div>
          </div>
          <div class="form-group">
            <label for="password">密码</label>
            <div class="input-with-icon">
              <i class="fas fa-lock"></i>
              <input type="password" id="password" v-model="form.password" placeholder="请输入密码">
            </div>
            <div class="error-message" v-if="errors.password">{{ errors.password }}</div>
          </div>
          <button class="btn btn-primary" @click="login">登录</button>
          <div class="form-footer">
            <router-link to="/" class="back-link">
              <i class="fas fa-arrow-left"></i>
              返回首页
            </router-link>
            <router-link to="/register" class="register-link">
              还没有账号？立即注册
              <i class="fas fa-arrow-right"></i>
            </router-link>
          </div>
        </div>
      </div>
    </template>

    <script setup>
    import { reactive, inject } from 'vue'
    import { useRouter } from 'vue-router'
    const form = reactive({ username: '', password: '' })
    const errors = reactive({ username: '', password: '' })
    const router = useRouter()
    const store = inject('store')

    function validateForm() {
      let isValid = true
      errors.username = ''
      errors.password = ''
      if (!form.username.trim()) {
        errors.username = '请输入用户名'
        isValid = false
      }
      if (!form.password) {
        errors.password = '请输入密码'
        isValid = false
      } else if (form.password.length < 6) {
        errors.password = '密码长度至少为6位'
        isValid = false
      }
      return isValid
    }
    function login() {
      if (validateForm()) {
        setTimeout(() => {
          // 设置登录状态到localStorage
          const userData = {
            id: 1,
            username: form.username,
            email: form.username + '@example.com',
            phone: '13800138000',
            joinDate: '2023-01-15'
          }
          localStorage.setItem('token', 'mock-token-' + Date.now())
          localStorage.setItem('user', JSON.stringify(userData))
          
          // 触发自定义事件通知导航栏更新状态
          window.dispatchEvent(new Event('loginStatusChanged'))
          
          // 如果store存在，也更新store
          if (store) {
            store.login(userData)
            store.showNotification('登录成功！', 'success')
          }
          
          router.push('/profile')
        }, 500)
      }
    }
    </script>

    <style scoped>
    .page-container {
      min-height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 20px;
    }

    .card {
      background: #fff;
      border-radius: 16px;
      box-shadow: 0 4px 24px 0 rgba(34, 51, 84, 0.12);
      padding: 40px;
      width: 100%;
      max-width: 360px;
    }

    .card-header {
      text-align: center;
      margin-bottom: 32px;
    }

    .card-header h2 {
      color: #1e293b;
      font-size: 2rem;
      font-weight: 700;
      margin-bottom: 8px;
    }

    .card-header p {
      color: #64748b;
      font-size: 1rem;
    }

    .form-group {
      margin-bottom: 24px;
    }

    .form-group label {
      display: block;
      margin-bottom: 8px;
      color: #475569;
      font-size: 0.95rem;
      font-weight: 500;
    }

    .input-with-icon {
      position: relative;
    }

    .input-with-icon i {
      position: absolute;
      left: 12px;
      top: 50%;
      transform: translateY(-50%);
      color: #94a3b8;
      font-size: 1.1rem;
    }

    .input-with-icon input {
      width: 80%;
      padding: 12px 12px 12px 40px;
      border: 1px solid #e2e8f0;
      border-radius: 8px;
      font-size: 1rem;
      color: #1e293b;
      transition: all 0.3s ease;
      background: #f8fafc;
    }

    .input-with-icon input:focus {
      outline: none;
      border-color: #6366f1;
      background: #fff;
      box-shadow: 0 0 0 4px rgba(99, 102, 241, 0.1);
    }

    .error-message {
      color: #ef4444;
      font-size: 0.875rem;
      margin-top: 6px;
    }

    .btn-primary {
      width: 100%;
      padding: 12px;
      background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%);
      color: #fff;
      border: none;
      border-radius: 8px;
      font-size: 1rem;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.3s ease;
    }

    .btn-primary:hover {
      background: linear-gradient(135deg, #4f46e5 0%, #6366f1 100%);
      transform: translateY(-1px);
      box-shadow: 0 4px 12px rgba(79, 70, 229, 0.3);
    }

    .form-footer {
      margin-top: 24px;
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-size: 0.95rem;
      gap: 20px;
    }

    .back-link, .register-link {
      color: #6366f1;
      text-decoration: none;
      display: flex;
      align-items: center;
      gap: 6px;
      transition: color 0.3s ease;
      white-space: nowrap;
    }

    .back-link:hover, .register-link:hover {
      color: #4f46e5;
    }

    @media (max-width: 480px) {
      .card {
        padding: 24px;
      }
      
      .form-footer {
        flex-direction: column;
        gap: 16px;
        text-align: center;
        align-items: center;
      }
      
      .back-link, .register-link {
        justify-content: center;
        width: auto;
      }
    }
    </style>
  