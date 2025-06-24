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
        <li><router-link to="/square" class="nav-link" active-class="active"><i class="fas fa-comments"></i> 动态广场</router-link></li>
        <li><router-link to="/favorites" class="nav-link" active-class="active"><i class="fas fa-heart"></i> 收藏夹</router-link></li>
        <li><router-link to="/chart" class="nav-link" active-class="active"><i class="fas fa-chart-line"></i> 价格图表</router-link></li>
        <li><router-link to="/profile" class="nav-link" active-class="active"><i class="fas fa-user"></i> 个人中心</router-link></li>
      </ul>
      <div class="search-box">
        <input type="text" placeholder="搜索商品...">
        <button><i class="fas fa-search"></i></button>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()
function goBack() {
  router.back()
}
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
}
</style>