<template>
  <div class="admin-container">
    <div class="admin-main">
      <AdminHeader />
      <div class="main-container">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script>
import AdminHeader from './components/AdminHeader.vue'

export default {
  name: 'Admin',
  components: {
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
  /* margin-left: 220px; */
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
}

.main-container {
  flex: 1;
  padding: 20px 0;
  overflow-y: auto;
}
</style>