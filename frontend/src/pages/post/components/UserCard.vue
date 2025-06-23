<template>
  <div class="user-card">
    <img :src="user.avatar" class="user-avatar" alt="用户头像">
    <div class="user-info">
      <div class="username">{{ user.name }}</div>
      <div class="user-title">{{ user.title }}</div>
    </div>
    <el-button 
      size="small" 
      :type="user.isFollowing ? '' : 'primary'" 
      plain
      @click="$emit('follow', user.id)"
    >
      {{ user.isFollowing ? '已关注' : '关注' }}
    </el-button>
  </div>
</template>

<script setup>
import { defineProps } from 'vue'

const props = defineProps({
  user: {
    type: Object,
    required: true
  }
})
</script>

<style scoped lang="scss">
.user-card {
  display: flex;
  gap: 12px;
  padding: 10px 0;
  align-items: center;
  border-bottom: 1px dashed #eee;
  
  .user-avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    object-fit: cover;
  }
  
  .user-info {
    flex-grow: 1;
    
    .username {
      font-weight: bold;
      font-size: 14px;
    }
    
    .user-title {
      font-size: 12px;
      color: #909399;
      margin-top: 3px;
    }
  }
}
</style>