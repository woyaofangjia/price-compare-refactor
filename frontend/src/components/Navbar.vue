<template>
  <nav class="navbar">
    <div class="container nav-container">
      <button class="back-btn" @click="goBack">
        <i class="fas fa-arrow-left"></i>
      </button>
      <div class="logo">
        <i class="fas fa-balance-scale"></i>
        <span>智能比价</span>
      </div>
      <ul class="nav-links">
        <li><router-link to="/" class="nav-link" active-class="active" exact><i class="fas fa-home"></i> 首页</router-link></li>
        <li><router-link to="/search" class="nav-link" active-class="active"><i class="fas fa-search"></i> 搜索商品</router-link></li>
        <li><router-link to="/favorites" class="nav-link" active-class="active"><i class="fas fa-heart"></i> 收藏夹</router-link></li>
        <li><router-link to="/chart" class="nav-link" active-class="active"><i class="fas fa-chart-line"></i> 价格图表</router-link></li>
      </ul>
      <div class="search-box">
        <input type="text" placeholder="搜索商品...">
        <button><i class="fas fa-search"></i></button>
      </div>
      <div class="user-actions">
        <router-link v-if="!isLoggedIn" to="/login" class="auth-btn login-btn">
          <i class="fas fa-sign-in-alt"></i> 登录
        </router-link>
        <router-link v-if="!isLoggedIn" to="/register" class="auth-btn register-btn">
          <i class="fas fa-user-plus"></i> 注册
        </router-link>
        <router-link v-if="isLoggedIn" to="/profile" class="auth-btn profile-btn">
          <i class="fas fa-user"></i> 个人中心
        </router-link>
        <button v-if="isLoggedIn" @click="logout" class="auth-btn logout-btn">
          <i class="fas fa-sign-out-alt"></i> 退出
        </button>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const isLoggedIn = ref(false)

function goBack() {
  router.back()
}

function logout() {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  isLoggedIn.value = false
  router.push('/')
}

function checkLoginStatus() {
  const token = localStorage.getItem('token')
  const user = localStorage.getItem('user')
  isLoggedIn.value = !!(token && user)
}

function handleStorageChange(e) {
  if (e.key === 'token' || e.key === 'user') {
    checkLoginStatus()
  }
}

onMounted(() => {
  checkLoginStatus()
  // 监听localStorage变化
  window.addEventListener('storage', handleStorageChange)
  // 监听自定义事件（用于同一页面内的状态变化）
  window.addEventListener('loginStatusChanged', checkLoginStatus)
})

onUnmounted(() => {
  window.removeEventListener('storage', handleStorageChange)
  window.removeEventListener('loginStatusChanged', checkLoginStatus)
})
</script>

<style scoped>
:root {
  --primary: #4361ee;
  --secondary: #3f37c9;
  --success: #4cc9f0;
  --light: #f8f9fa;
  --dark: #212529;
  --warning: #f72585;
  --gray: #6c757d;
  --light-gray: #e9ecef;
}
.navbar {
  background: linear-gradient(135deg, var(--primary), var(--secondary));
  color: white;
  padding: 15px 0;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}
.nav-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 15px;
}
.logo {
  font-size: 1.8rem;
  font-weight: bold;
  display: flex;
  align-items: center;
}
.logo i {
  margin-right: 10px;
}
.nav-links {
  display: flex;
  list-style: none;
}
.nav-links li {
  margin-left: 15px;
}
.nav-link {
  color: white;
  text-decoration: none;
  font-weight: 500;
  padding: 8px 15px;
  border-radius: 20px;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
}
.nav-link i {
  margin-right: 8px;
  display: inline-block;
  width: 20px;
  text-align: center;
}
.nav-link:hover, .nav-link.active {
  background-color: rgba(255,255,255,0.2);
}
.search-box {
  display: flex;
  background: white;
  border-radius: 30px;
  overflow: hidden;
  width: 300px;
}
.search-box input {
  flex: 1;
  border: none;
  padding: 10px 15px;
  outline: none;
}
.search-box button {
  background: var(--success);
  border: none;
  color: white;
  padding: 10px 20px;
  cursor: pointer;
}
.back-btn {
  background: transparent;
  border: none;
  color: white;
  font-size: 1.3rem;
  margin-right: 18px;
  cursor: pointer;
  transition: color 0.2s;
}
.back-btn:hover {
  color: var(--success);
}
.user-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}
.auth-btn {
  color: white;
  text-decoration: none;
  font-weight: 500;
  padding: 8px 15px;
  border-radius: 20px;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  border: none;
  cursor: pointer;
  font-size: 0.9rem;
}
.auth-btn i {
  margin-right: 6px;
  display: inline-block;
  width: 16px;
  text-align: center;
}
.login-btn {
  background-color: rgba(255,255,255,0.2);
}
.login-btn:hover {
  background-color: rgba(255,255,255,0.3);
}
.register-btn {
  background-color: var(--success);
}
.register-btn:hover {
  background-color: #3bb8d9;
}
.profile-btn {
  background-color: rgba(255,255,255,0.2);
}
.profile-btn:hover {
  background-color: rgba(255,255,255,0.3);
}
.logout-btn {
  background-color: #e74c3c;
}
.logout-btn:hover {
  background-color: #c0392b;
}
@media (max-width: 768px) {
  .nav-container {
    flex-direction: column;
    gap: 15px;
  }
  .nav-links {
    margin-top: 10px;
    flex-wrap: wrap;
    justify-content: center;
  }
  .nav-links li {
    margin: 5px;
  }
  .search-box {
    width: 100%;
    margin-top: 10px;
  }
  .user-actions {
    margin-top: 10px;
    flex-wrap: wrap;
    justify-content: center;
  }
}
</style>