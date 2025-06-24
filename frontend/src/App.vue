<template>
  <div class="app-layout">
    <AdminSidebar v-if="isAdminRoute && isAdmin" />
    <Sidebar v-else />
    <div class="main-content" :class="{ 'admin-content': isAdminRoute && isAdmin }">
      <SearchBox v-if="!hideSearchOn.includes(route.path)" />
      <router-view />
    </div>
  </div>
</template>

<script>
import Sidebar from './components/Sidebar.vue'
import AdminSidebar from './pages/admin/components/AdminSidebar.vue'
import SearchBox from './components/SearchBox.vue'
import { useRoute } from 'vue-router'
import { ref, computed, onMounted, onUnmounted } from 'vue'

export default {
  components: {
    Sidebar,
    AdminSidebar,
    SearchBox
  },
  setup() {
    const route = useRoute()
    const hideSearchOn = ['/profile', '/login', '/register', '/profile/edit', '/admin', '/admin/dashboard', '/admin/users', '/admin/products', '/admin/posts', '/admin/charts', '/admin/settings']
    const isAdmin = ref(false)

    const isAdminRoute = computed(() => {
      return route.path.startsWith('/admin')
    })

    const checkAdminStatus = () => {
      const user = JSON.parse(localStorage.getItem('user') || '{}')
      isAdmin.value = user.role === 'admin'
    }

    onMounted(() => {
      checkAdminStatus()
      window.addEventListener('storage', checkAdminStatus)
      window.addEventListener('loginStatusChanged', checkAdminStatus)
    })

    onUnmounted(() => {
      window.removeEventListener('storage', checkAdminStatus)
      window.removeEventListener('loginStatusChanged', checkAdminStatus)
    })

    return { 
      route, 
      hideSearchOn, 
      isAdmin,
      isAdminRoute
    }
  }
}
</script>

<style>
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

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

body {
  background-color: #f5f7fb;
}

.app-layout {
  display: flex;
  min-height: 100vh;
}

.main-content {
  flex: 1;
  margin-left: 80px; /* 侧边栏宽度 */
  min-height: 100vh;
  background: #f5f7fb;
  display: flex;
  flex-direction: column;
}

.main-content.admin-content {
  margin-left: 220px; /* 管理员侧边栏宽度 */
}

@media (max-width: 900px) {
  .main-content {
    margin-left: 60px;
  }
  
  .main-content.admin-content {
    margin-left: 70px;
  }
}
</style>