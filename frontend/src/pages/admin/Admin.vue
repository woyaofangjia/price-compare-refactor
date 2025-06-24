<template>
  <div class="admin-container">
    <AdminSidebar />
    <div class="admin-main">
      <AdminHeader />
      <div class="admin-content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script>
import AdminSidebar from './components/AdminSidebar.vue'
import AdminHeader from './components/AdminHeader.vue'

export default {
  name: 'Admin',
  components: {
    AdminSidebar,
    AdminHeader
  }
}
</script>

<style scoped>
.admin-container {
  display: flex;
  height: 100vh;
}

.admin-main {
  flex: 1;
  margin-left: 220px;
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
}

.admin-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

@media (max-width: 992px) {
  .admin-main {
    margin-left: 70px;
  }
}
</style>