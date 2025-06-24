<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title"><i class="fas fa-file-alt"></i> 动态详情</h1>
      <RouterLink to="/dynamic">
        <button class="btn btn-outline"><i class="fas fa-arrow-left"></i> 返回</button>
      </RouterLink>
    </div>
    <div class="card">
      <DynamicCard v-if="postData" :post="postData" />
      <div v-else class="empty-state">未找到该动态</div>
      <div class="comments-section">
        <div class="card-header"><div class="card-title">评论 ({{ comments.length }})</div></div>
        <div class="comment-item" v-for="(c, index) in comments" :key="index">
          <img :src="c.avatar || 'https://randomuser.me/api/portraits/lego/1.jpg'" class="comment-avatar" />
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
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import DynamicCard from './components/DynamicCard.vue';

const route = useRoute()
const postId = Number(route.params.id)
const postData = ref(null)
const comments = ref([])
const newComment = ref('')

function loadPostAndComments() {
  // 从 localStorage 获取所有动态
  const storedPosts = localStorage.getItem('userPosts')
  if (storedPosts) {
    const posts = JSON.parse(storedPosts)
    postData.value = posts.find(p => p.id === postId)
  }
  // 从 localStorage 获取评论
  const storedComments = localStorage.getItem(`comments_${postId}`)
  if (storedComments) {
    comments.value = JSON.parse(storedComments)
  } else {
    comments.value = []
  }
}

function submitComment() {
  if (newComment.value.trim()) {
    comments.value.push({
      author: '你',
      avatar: '',
      time: '刚刚',
      text: newComment.value
    })
    localStorage.setItem(`comments_${postId}`, JSON.stringify(comments.value))
    newComment.value = ''
  }
}

onMounted(() => {
  loadPostAndComments()
})
</script>
