<template>
  <nav class="navbar">
    <div class="container nav-container">
      <div
        v-if="!hideSearchOn.includes(route.path)"
        class="search-box"
      >
        <input type="text" placeholder="搜索商品..." />
        <button><i class="fas fa-search"></i></button>
      </div>
      <!-- 管理员后台入口，仅管理员可见 -->
      <router-link
        v-if="isadmin"
        to="/admin"
        class="admin-link"
        style="margin-left: 30px; color: #fff; font-weight: bold;"
      >
        <i class="fas fa-cogs"></i> 管理员后台
      </router-link>
    </div>
  </nav>
</template>

<script setup>
import { useRoute } from 'vue-router'
import { ref, onMounted } from 'vue'
const route = useRoute()
const hideSearchOn = ['/profile', '/login', '/register', '/profile/edit']

const isadmin = ref(false)

function checkAdmin() {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  isadmin.value = !!user && Number(user.isadmin) === 1
  console.log('isadmin:', user.isadmin)
}

onMounted(() => {
  checkAdmin()
  window.addEventListener('storage', checkAdmin)
  window.addEventListener('loginStatusChanged', checkAdmin)
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
  justify-content: center;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 15px;
}
.search-box {
  display: flex;
  background: #f5f7fb;
  border-radius: 30px;
  overflow: hidden;
  width: 600px;
  align-items: center;
  box-shadow: 0 2px 10px rgba(0,0,0,0.04);
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

@media (max-width: 900px) {
  .search-box {
    width: 100%;
    min-width: 0;
  }
}
</style>