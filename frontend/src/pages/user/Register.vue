<template>
  <div class="page-container">
    <div class="card">
      <div class="card-header">
        <h2>用户注册</h2>
        <p>创建您的个人账户</p>
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
        <label for="email">邮箱</label>
        <div class="input-with-icon">
          <i class="fas fa-envelope"></i>
          <input type="email" id="email" v-model="form.email" placeholder="请输入邮箱">
        </div>
        <div class="error-message" v-if="errors.email">{{ errors.email }}</div>
      </div>
      <div class="form-group">
        <label for="code">验证码</label>
        <div class="input-with-icon" style="display: flex; align-items: center;">
          <i class="fas fa-key"></i>
          <input type="text" id="code" v-model="form.code" placeholder="请输入验证码" style="flex:1;">
          <button class="btn btn-outline" style="margin-left:10px;" @click="sendCode" :disabled="countdown > 0">
            {{ countdown > 0 ? countdown + 's后重试' : '获取验证码' }}
          </button>
        </div>
        <div class="error-message" v-if="errors.code">{{ errors.code }}</div>
      </div>
      <div class="form-group">
        <label for="password">密码</label>
        <div class="input-with-icon">
          <i class="fas fa-lock"></i>
          <input type="password" id="password" v-model="form.password" placeholder="请输入密码">
        </div>
        <div class="error-message" v-if="errors.password">{{ errors.password }}</div>
      </div>
      <div class="form-group">
        <label for="confirmPassword">确认密码</label>
        <div class="input-with-icon">
          <i class="fas fa-lock"></i>
          <input type="password" id="confirmPassword" v-model="form.confirmPassword" placeholder="请再次输入密码">
        </div>
        <div class="error-message" v-if="errors.confirmPassword">{{ errors.confirmPassword }}</div>
      </div>
      <button class="btn btn-primary" @click="register">注册</button>
      <div class="form-footer">
        <router-link to="/" class="back-link">
          <i class="fas fa-arrow-left"></i>
          返回首页
        </router-link>
        <router-link to="/login" class="login-link">
          已有账号？立即登录
          <i class="fas fa-arrow-right"></i>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const form = reactive({ username: '', email: '', code: '', password: '', confirmPassword: '' })
const errors = reactive({ username: '', email: '', code: '', password: '', confirmPassword: '' })
const router = useRouter()
const countdown = ref(0)
let timer = null

function validateForm() {
  let isValid = true
  errors.username = ''
  errors.email = ''
  errors.code = ''
  errors.password = ''
  errors.confirmPassword = ''
  if (!form.username.trim()) {
    errors.username = '请输入用户名'
    isValid = false
  }
  if (!form.email.trim()) {
    errors.email = '请输入邮箱'
    isValid = false
  } else if (!/^\S+@\S+\.\S+$/.test(form.email)) {
    errors.email = '请输入有效的邮箱地址'
    isValid = false
  }
  if (!form.code.trim()) {
    errors.code = '请输入验证码'
    isValid = false
  }
  if (!form.password) {
    errors.password = '请输入密码'
    isValid = false
  } else if (form.password.length < 6) {
    errors.password = '密码长度至少为6位'
    isValid = false
  }
  if (form.password !== form.confirmPassword) {
    errors.confirmPassword = '两次输入的密码不一致'
    isValid = false
  }
  return isValid
}

function sendCode() {
  if (!/^\S+@\S+\.\S+$/.test(form.email)) {
    errors.email = '请输入有效的邮箱地址'
    return
  }
  if (countdown.value > 0) return // 倒计时中禁止重复点击
  // 这里调用后端发送验证码API
  // axios.post('/api/send-code', { email: form.email }) ...
  countdown.value = 60
  timer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      clearInterval(timer)
      timer = null
    }
  }, 1000)
}

onUnmounted(() => {
  if (timer) clearInterval(timer)
})

function register() {
  axios.post('/api/auth/register', {
    username: form.username,
    password: form.password,
    email: form.email
  })
    .then(res => {
      router.push('/login')
    })
    .catch(err => {
      console.error('注册失败', err)
    })
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

.back-link, .login-link {
  color: #6366f1;
  text-decoration: none;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: color 0.3s ease;
  white-space: nowrap;
}

.back-link:hover, .login-link:hover {
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
  
  .back-link, .login-link {
    justify-content: center;
    width: auto;
  }
}
</style>
  