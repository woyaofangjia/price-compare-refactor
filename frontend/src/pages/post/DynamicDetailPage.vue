<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title"><i class="fas fa-file-alt"></i> 动态详情</h1>
      <RouterLink to="/post/square">
        <button class="btn btn-outline"><i class="fas fa-arrow-left"></i> 返回</button>
      </RouterLink>
    </div>
    <div class="card">
      <DynamicCard :post="postData" />
      <div class="comments-section">
        <div class="card-header"><div class="card-title">评论 ({{ comments.length }})</div></div>
        <div class="comment-item" v-for="(c, index) in comments" :key="index">
          <img :src="c.avatar" class="comment-avatar" />
          <div class="comment-content">
            <div class="comment-header">
              <div class="comment-author">{{ c.author }}</div>
              <div class="comment-time">{{ c.time }}</div>
            </div>
            <div class="comment-text">{{ c.text }}</div>
          </div>
        </div>
        <div class="comment-form">
          <textarea v-model="newComment" class="form-control" placeholder="写下你的评论..."></textarea>
          <button class="btn btn-primary" @click="submitComment"><i class="fas fa-paper-plane"></i> 发表评论</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import DynamicCard from './components/DynamicCard.vue';

const postData = {
  username: '数码达人',
  userAvatar: 'https://randomuser.me/api/portraits/women/44.jpg',
  time: '2023年10月15日 14:30',
  content: '这是一条动态内容',
  images: [],
  product: null,
  likes: 256,
  comments: 42
};

const comments = [
  { author: '数码爱好者', avatar: 'https://randomuser.me/api/portraits/men/32.jpg', time: '1小时前', text: '评论内容' }
];

let newComment = '';

function submitComment() {
  if (newComment) {
    comments.push({ author: '你', avatar: '', time: '刚刚', text: newComment });
    newComment = '';
  }
}
</script>
