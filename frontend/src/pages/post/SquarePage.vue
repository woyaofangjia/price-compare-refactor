<template>
  <div class="page-container">
    <TrendingList />
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
        @click="openDetail(post)"
        style="cursor:pointer;"
      >
        <template #extra v-if="post.product">
          <div class="product-detail" style="margin-top:8px; color:#888; font-size:0.98rem; background:#f8f8f8; border-radius:8px; padding:8px 12px;">
            {{ post.product.detail || '这是一款高性价比的优质商品，深受用户好评。' }}
          </div>
        </template>
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
    <div v-if="showDetail" class="detail-dialog-mask" @click.self="closeDetail">
      <div class="detail-dialog">
        <DynamicDetailContent
          :post="selectedPost"
          :comments="detailComments"
          :isLiked="false"
          :isCollected="false"
          :likes="selectedPost?.likes || 0"
          @like="() => {}"
          @collect="() => {}"
          @submit-comment="onSubmitComment"
          @delete-comment="onDeleteComment"
        />
        <button class="close-btn" @click="closeDetail">关闭</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import DynamicCard from './components/DynamicCard.vue';
import TrendingList from './components/TrendingList.vue';
import DynamicDetailContent from './components/DynamicDetailContent.vue';

const sortType = ref('latest')
const posts = ref([
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
      platform: '京东 | 天猫',
      detail: '索尼旗舰降噪耳机，音质出色，佩戴舒适，适合通勤和办公。'
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
])

const showDetail = ref(false)
const selectedPost = ref(null)
const detailComments = ref([])

function openDetail(post) {
  selectedPost.value = post
  // 模拟评论
  detailComments.value = [
    { author: '用户A', text: '很棒的测评！', time: '1小时前' },
    { author: '用户B', text: '支持一下！', time: '30分钟前' }
  ]
  showDetail.value = true
}
function closeDetail() {
  showDetail.value = false
}
function onSubmitComment(comment) {
  detailComments.value.push({ author: '你', text: comment, time: '刚刚' })
}
function onDeleteComment(index) {
  detailComments.value.splice(index, 1)
}
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

.detail-dialog-mask {
  position: fixed;
  left: 0; top: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.18);
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
}
.detail-dialog {
  background: #fff;
  border-radius: 12px;
  padding: 32px 24px 24px 24px;
  min-width: 340px;
  max-width: 420px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.18);
  position: relative;
}
.close-btn {
  position: absolute;
  right: 18px;
  top: 12px;
  background: #eee;
  border: none;
  border-radius: 6px;
  padding: 4px 12px;
  cursor: pointer;
}
</style>
