<template>
  <div class="app-layout">
    <Sidebar />
    <div class="main-content">
      <Navbar v-if="!hideNavbarOn.includes(route.path)" />
      <router-view />
    </div>
  </div>
</template>

<script>
import Sidebar from './components/Sidebar.vue'
import Navbar from './components/Navbar.vue'
import { useRoute } from 'vue-router'

export default {
  components: {
    Sidebar,
    Navbar
  },
  setup() {
    const route = useRoute()
    const hideNavbarOn = ['/profile', '/login', '/register', '/profile/edit']
    return { route, hideNavbarOn }
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

@media (max-width: 900px) {
  .main-content {
    margin-left: 60px;
  }
}
</style>