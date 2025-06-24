<template>
  <div class="user-card">
    <img :src="avatar" class="user-avatar" alt="用户头像" />
    <div class="user-info">
      <div class="username">{{ username }}</div>
      <div class="post-time">{{ time }}</div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  avatar: String,
  username: String,
  time: String
});
</script>
