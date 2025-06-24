<template>
  <div class="card">
    <div class="card-header">
      <div class="card-title">热门动态</div>
    </div>
    <ul>
      <li v-for="(item, index) in items" :key="index">{{ item }}</li>
    </ul>
  </div>
</template>

<script setup>
const items = ['无线耳机推荐', '手机性价比分析', '扫地机器人评测'];
</script>
