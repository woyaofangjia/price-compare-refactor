<template>
  <div class="sidebar">
    <div class="sidebar-top-links">
      <router-link to="/dynamic" class="sidebar-top-link">
        <i class="fas fa-comments"></i>
        <span>动态互动</span>
      </router-link>
      <router-link to="/auth" class="sidebar-top-link">
        <i class="fas fa-user-check"></i>
        <span>用户认证</span>
      </router-link>
      <router-link to="/admin" class="sidebar-top-link">
        <i class="fas fa-user-cog"></i>
        <span>管理员后台</span>
      </router-link>
    </div>
    <div class="sidebar-logo">
      <i class="fas fa-balance-scale"></i>
    </div>
    <ul class="sidebar-links">
      <li>
        <router-link to="/" class="sidebar-link" active-class="active" exact>
          <i class="fas fa-home"></i>
          <span>首页</span>
        </router-link>
      </li>
      <li>
        <router-link to="/search" class="sidebar-link" active-class="active">
          <i class="fas fa-search"></i>
          <span>搜索</span>
        </router-link>
      </li>
      <li>
        <router-link to="/favorites" class="sidebar-link" active-class="active">
          <i class="fas fa-heart"></i>
          <span>收藏夹</span>
        </router-link>
      </li>
      <li>
        <router-link to="/chart" class="sidebar-link" active-class="active">
          <i class="fas fa-chart-line"></i>
          <span>图表</span>
        </router-link>
      </li>
    </ul>
  </div>
</template>

<style scoped>
:root {
  --primary: #4361ee;
  --secondary: #3f37c9;
  --light: #f8f9fa;
  --dark: #212529;
  --gray: #6c757d;
  --light-gray: #e9ecef;
}
.sidebar {
  width: 80px;
  background: linear-gradient(135deg, var(--primary), var(--secondary));
  color: white;
  height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 0;
  position: fixed;
  left: 0;
  top: 0;
  z-index: 200;
  box-shadow: 2px 0 10px rgba(0,0,0,0.05);
}
.sidebar-logo {
  font-size: 2rem;
  margin-bottom: 30px;
}
.sidebar-links {
  list-style: none;
  padding: 0;
  width: 100%;
}
.sidebar-link {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: white;
  text-decoration: none;
  padding: 18px 0;
  transition: background 0.2s, color 0.2s;
  font-size: 1.2rem;
  border-radius: 10px 0 0 10px;
}
.sidebar-link i {
  font-size: 1.4rem;
  margin-bottom: 5px;
}
.sidebar-link span {
  font-size: 0.85rem;
}
.sidebar-link.active, .sidebar-link:hover {
  background: rgba(255,255,255,0.15);
  color: #f8f9fa;
}
@media (max-width: 900px) {
  .sidebar {
    width: 60px;
  }
  .sidebar-link span {
    display: none;
  }
}
.sidebar-top-links {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 20px;
  width: 100%;
  align-items: center;
}
.sidebar-top-link {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: white;
  text-decoration: none;
  font-size: 1.1rem;
  padding: 8px 0 0 0;
  transition: color 0.2s;
}
.sidebar-top-link i {
  font-size: 1.3rem;
  margin-bottom: 2px;
}
.sidebar-top-link span {
  font-size: 0.8rem;
}
.sidebar-top-link:hover {
  color: var(--success);
}
</style>