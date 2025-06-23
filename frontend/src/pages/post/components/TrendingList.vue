<template>
  <div class="trending-list">
    <h3 class="sidebar-title">
      <el-icon :size="18"><DataLine /></el-icon>
      热门话题
    </h3>
    
    <div class="trending-items">
      <div v-for="(topic, index) in topics" :key="index" class="trending-item">
        <div class="trending-rank">{{ index + 1 }}</div>
        <div class="trending-text">#{{ topic.name }}</div>
        <div class="trending-count">{{ topic.count }}条</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps } from 'vue'
import { DataLine } from '@element-plus/icons-vue'

const props = defineProps({
  topics: {
    type: Array,
    required: true,
    default: () => []
  }
})
</script>

<style scoped lang="scss">
.trending-list {
  margin-bottom: 25px;
  
  .sidebar-title {
    font-size: 18px;
    margin-bottom: 15px;
    color: #303133;
    display: flex;
    align-items: center;
    gap: 8px;
  }
  
  .trending-items {
    display: flex;
    flex-direction: column;
    gap: 15px;
  }
  
  .trending-item {
    display: flex;
    gap: 12px;
    padding: 8px 0;
    border-bottom: 1px dashed #eee;
    align-items: center;
  }
  
  .trending-rank {
    width: 26px;
    height: 26px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #f5f7fa;
    border-radius: 6px;
    font-weight: bold;
    color: #409EFF;
    font-size: 14px;
  }
  
  .trending-text {
    flex-grow: 1;
    font-size: 14px;
    font-weight: 500;
  }
  
  .trending-count {
    font-size: 13px;
    color: #909399;
  }
}
</style>