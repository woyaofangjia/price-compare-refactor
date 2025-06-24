<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title"><i class="fas fa-comments"></i> 动态广场</h1>
      <router-link to="/post/create" class="btn btn-primary publish-btn">
        <i class="fas fa-plus"></i> 发布动态
      </router-link>
    </div>
    <div class="card">
      <DynamicCard
        v-for="(post, index) in posts"
        :key="post.id || index"
        :post="post"
      >
        <template #extra v-if="index === 0">
          <div class="card-extra-select">
            <select class="form-control" v-model="sortType">
              <option value="latest">最新发布</option>
              <option value="likes">最多点赞</option>
            </select>
          </div>
        </template>
      </DynamicCard>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import DynamicCard from './components/DynamicCard.vue';

const sortType = ref('latest')
const posts = [
  {
    id: 1,
    username: '科技达人',
    userAvatar: 'https://randomuser.me/api/portraits/men/32.jpg',
    time: '2小时前',
    content: '新耳机测评',
    images: ['https://images.unsplash.com/photo-1572536147248-ac59a8abfa4b'],
    product: {
      name: 'Sony WH-1000XM5',
      price: '¥2,499',
      image: 'https://images.unsplash.com/photo-1572536147248-ac59a8abfa4b',
      platform: '京东 | 天猫'
    },
    likes: 128,
    comments: 24
  },
  {
    id: 2,
    username: '数码玩家',
    userAvatar: 'https://randomuser.me/api/portraits/women/44.jpg',
    time: '1天前',
    content: '手机横评，欢迎讨论！',
    images: [],
    product: null,
    likes: 88,
    comments: 10
  }
]
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}
.publish-btn {
  margin-left: auto;
  float: right;
}
.card-extra-select {
  position: absolute;
  top: 20px;
  right: 20px;
  z-index: 2;
}
.card {
  position: relative;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 32px;
  background: none;
  box-shadow: none;
  padding: 0;
}

@media (max-width: 1000px) {
  .card {
    grid-template-columns: 1fr;
  }
}
</style>
