<template>
  <div class="dynamic-detail-page">
    <el-page-header @back="goBack" content="动态详情" />
    <DynamicCard
      v-if="dynamic"
      :dynamic="dynamic"
      :currentUserId="currentUserId"
      @like="toggleLike"
      @collect="toggleCollect"
      @delete="deleteDynamic"
    />
    <el-divider>评论区</el-divider>
    <div v-if="dynamic && dynamic.commentsList && dynamic.commentsList.length">
      <el-comment
        v-for="comment in dynamic.commentsList"
        :key="comment.id"
        :author="comment.username"
        :avatar="comment.avatar"
        :content="comment.content"
        :datetime="comment.timestamp"
      />
    </div>
    <div v-else class="empty-comments">暂无评论</div>
    <el-form :model="newComment" class="comment-form" @submit.prevent="submitComment">
      <el-form-item>
        <el-input v-model="newComment.content" placeholder="写下你的评论..." />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitComment">发表评论</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import DynamicCard from './components/DynamicCard.vue'
import { fetchDynamics } from './utils/api'

const route = useRoute()
const router = useRouter()
const currentUserId = ref(101)
const dynamic = ref(null)
const newComment = ref({ content: '' })

const goBack = () => {
  router.back()
}

const toggleLike = (id) => {
  if (dynamic.value && dynamic.value.id === id) {
    dynamic.value.isLiked = !dynamic.value.isLiked
    dynamic.value.likes += dynamic.value.isLiked ? 1 : -1
  }
}

const toggleCollect = (id) => {
  if (dynamic.value && dynamic.value.id === id) {
    dynamic.value.isCollected = !dynamic.value.isCollected
  }
}

const deleteDynamic = (id) => {
  router.push('/')
}

const submitComment = () => {
  if (!newComment.value.content.trim()) return
  if (!dynamic.value.commentsList) dynamic.value.commentsList = []
  dynamic.value.commentsList.push({
    id: Date.now(),
    username: '当前用户',
    avatar: 'https://randomuser.me/api/portraits/men/32.jpg',
    content: newComment.value.content,
    timestamp: '刚刚'
  })
  newComment.value.content = ''
}

onMounted(async () => {
  const id = Number(route.params.id)
  const allDynamics = await fetchDynamics()
  dynamic.value = allDynamics.find(d => d.id === id) || allDynamics[0]
  // mock comments
  dynamic.value.commentsList = [
    { id: 1, username: '用户A', avatar: 'https://randomuser.me/api/portraits/women/44.jpg', content: '很棒的分享！', timestamp: '1小时前' },
    { id: 2, username: '用户B', avatar: 'https://randomuser.me/api/portraits/men/45.jpg', content: '有用的信息，谢谢！', timestamp: '2小时前' }
  ]
})
</script>

<style scoped>
.dynamic-detail-page {
  max-width: 700px;
  margin: 30px auto;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 6px 16px rgba(0,0,0,0.08);
  padding: 30px;
}
.empty-comments {
  color: #909399;
  text-align: center;
  margin: 20px 0;
}
.comment-form {
  margin-top: 30px;
}
</style> 