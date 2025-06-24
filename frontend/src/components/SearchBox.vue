<template>
  <div class="search-box">
    <input type="text" placeholder="搜索商品..." />
    <button><i class="fas fa-search"></i></button>
  </div>
</template>

<script setup>
// 可根据需要添加props或emit
</script>

<style scoped>
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