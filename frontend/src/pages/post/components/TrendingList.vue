<template>
  <div class="card">
    <div class="card-header">
      <div class="card-title">热门动态</div>
    </div>
    <ul style="text-align:left;">
      <li v-for="(item, index) in items" :key="index" class="trending-item">{{ item }}</li>
    </ul>
  </div>
</template>

<script setup>
const items = ['无线耳机推荐', '手机性价比分析', '扫地机器人评测'];
</script>

<style scoped>
.trending-item {
  color: rgba(60,60,60,0.5);
  font-size: 1.1rem;
  margin-bottom: 6px;
  font-weight: 500;
}
</style>
