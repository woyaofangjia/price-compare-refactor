<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title"><i class="fas fa-comments"></i> 动态广场</h1>
      <RouterLink to="/post/create">
        <button class="btn btn-primary"><i class="fas fa-pen"></i> 发布动态</button>
      </RouterLink>
    </div>
    <div class="card">
      <div class="card-header">
        <div class="card-title">最新动态</div>
        <select class="form-control"><option>最新发布</option><option>最多点赞</option></select>
      </div>
      <DynamicCard v-for="(post, index) in posts" :key="index" :post="post" />
    </div>
  </div>
</template>

<script setup>
import DynamicCard from './components/DynamicCard.vue';

const posts = [
  {
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
  }
];
</script>
