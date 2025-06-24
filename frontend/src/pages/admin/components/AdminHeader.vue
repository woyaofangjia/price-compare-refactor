<template>
  <div class="admin-header">
    <div class="admin-title">{{ title }}</div>
    <div class="admin-actions">
      <div class="notification-badge">
        <i class="fas fa-bell"></i>
        <span class="notification-count">3</span>
      </div>
      <div class="admin-user" @click="toggleDropdown">
        <div class="admin-avatar">A</div>
        <span>admin</span>
        <i class="fas fa-chevron-down" style="margin-left: 10px;"></i>
      </div>
    </div>
    
    <div class="dropdown-menu" v-show="showDropdown">
      <div class="dropdown-item">
        <i class="fas fa-user"></i>
        <span>个人资料</span>
      </div>
      <div class="dropdown-item">
        <i class="fas fa-cog"></i>
        <span>账户设置</span>
      </div>
      <div class="dropdown-divider"></div>
      <div class="dropdown-item" @click="logout">
        <i class="fas fa-sign-out-alt"></i>
        <span>退出登录</span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AdminHeader',
  data() {
    return {
      showDropdown: false,
      title: '管理员控制台'
    }
  },
  watch: {
    $route() {
      this.updateTitle()
    }
  },
  mounted() {
    this.updateTitle()
    document.addEventListener('click', this.handleClickOutside)
  },
  beforeUnmount() {
    document.removeEventListener('click', this.handleClickOutside)
  },
  methods: {
    toggleDropdown() {
      this.showDropdown = !this.showDropdown
    },
    handleClickOutside(event) {
      if (!this.$el.contains(event.target)) {
        this.showDropdown = false
      }
    },
    logout() {
      // 退出登录逻辑
      this.$router.push('/')
    },
    updateTitle() {
      const routeMap = {
        '/admin/dashboard': '控制面板',
        '/admin/users': '用户管理',
        '/admin/products': '商品管理',
        '/admin/posts': '动态管理',
        '/admin/charts': '图表分析',
        '/admin/settings': '系统设置'
      }
      this.title = routeMap[this.$route.path] || '管理员控制台'
    }
  }
}
</script>

<style scoped>
.admin-header {
  height: 60px;
  background: white;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 25px;
  z-index: 99;
}

.admin-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: var(--primary);
}

.admin-actions {
  display: flex;
  align-items: center;
}

.admin-user {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.admin-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: var(--light-gray);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 10px;
  color: var(--primary);
  font-weight: bold;
}

.notification-badge {
  position: relative;
  margin-right: 20px;
  font-size: 1.2rem;
  color: var(--gray);
  cursor: pointer;
}

.notification-count {
  position: absolute;
  top: -5px;
  right: -8px;
  background: var(--warning);
  color: white;
  border-radius: 50%;
  width: 18px;
  height: 18px;
  font-size: 0.7rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

.dropdown-menu {
  position: absolute;
  top: 60px;
  right: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
  width: 200px;
  overflow: hidden;
  z-index: 1000;
}

.dropdown-item {
  padding: 12px 15px;
  cursor: pointer;
  display: flex;
  align-items: center;
  transition: background 0.3s;
}

.dropdown-item:hover {
  background: var(--light);
}

.dropdown-item i {
  margin-right: 10px;
  width: 20px;
  text-align: center;
}

.dropdown-divider {
  height: 1px;
  background: var(--light-gray);
  margin: 5px 0;
}

.fade-in {
  animation: fadeIn 0.5s ease forwards;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

@media (max-width: 768px) {
  .admin-header {
    padding: 0 15px;
  }
  
  .admin-title {
    font-size: 1.2rem;
  }
  
  .admin-user span {
    display: none;
  }
}
</style>