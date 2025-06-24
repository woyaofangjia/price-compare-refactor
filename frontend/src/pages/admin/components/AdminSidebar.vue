<template>
  <div class="admin-sidebar">
    <div class="sidebar-header">
      <h2>
        <i class="fas fa-cogs"></i>
        <span>管理员后台</span>
      </h2>
    </div>
    
    <ul class="admin-menu">
      <li class="menu-item" :class="{active: $route.path === '/admin/dashboard'}" @click="$router.push('/admin/dashboard')">
        <i class="fas fa-tachometer-alt"></i>
        <span class="menu-text">控制面板</span>
      </li>
      <li class="menu-item" :class="{active: $route.path === '/admin/users'}" @click="$router.push('/admin/users')">
        <i class="fas fa-users"></i>
        <span class="menu-text">用户管理</span>
      </li>
      <li class="menu-item" :class="{active: $route.path === '/admin/products'}" @click="$router.push('/admin/products')">
        <i class="fas fa-shopping-bag"></i>
        <span class="menu-text">商品管理</span>
      </li>
      <li class="menu-item" :class="{active: $route.path === '/admin/posts'}" @click="$router.push('/admin/posts')">
        <i class="fas fa-comments"></i>
        <span class="menu-text">动态管理</span>
      </li>
      <li class="menu-item" :class="{active: $route.path === '/admin/charts'}" @click="$router.push('/admin/charts')">
        <i class="fas fa-chart-pie"></i>
        <span class="menu-text">图表分析</span>
      </li>
      <li class="menu-item" :class="{active: $route.path === '/admin/settings'}" @click="$router.push('/admin/settings')">
        <i class="fas fa-cog"></i>
        <span class="menu-text">系统设置</span>
      </li>
    </ul>
  </div>
</template>

<script>
export default {
  name: 'AdminSidebar'
}
</script>

<style scoped>
.admin-sidebar {
  width: 220px;
  background: linear-gradient(135deg, var(--primary), var(--secondary));
  color: white;
  height: 100%;
  position: fixed;
  z-index: 100;
  box-shadow: 2px 0 10px rgba(0,0,0,0.1);
  transition: all 0.3s ease;
}

.sidebar-header {
  padding: 20px 15px;
  text-align: center;
  border-bottom: 1px solid rgba(255,255,255,0.1);
}

.sidebar-header h2 {
  font-size: 1.2rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

.sidebar-header i {
  margin-right: 10px;
  font-size: 1.5rem;
}

.admin-menu {
  list-style: none;
  padding: 15px 0;
}

.menu-item {
  padding: 12px 20px;
  margin: 5px 10px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
}

.menu-item i {
  margin-right: 10px;
  width: 24px;
  text-align: center;
}

.menu-item:hover {
  background-color: rgba(255,255,255,0.15);
}

.menu-item.active {
  background-color: rgba(255,255,255,0.25);
}

@media (max-width: 992px) {
  .admin-sidebar {
    width: 70px;
  }
  
  .menu-text {
    display: none;
  }
  
  .sidebar-header h2 span {
    display: none;
  }
  
  .sidebar-header h2 i {
    margin-right: 0;
    font-size: 1.8rem;
  }
}
</style>