<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title"><i class="fas fa-pen"></i> 发布动态</h1>
      <RouterLink to="/post/square">
        <button class="btn btn-outline"><i class="fas fa-arrow-left"></i> 返回</button>
      </RouterLink>
    </div>
    <div class="card">
      <div class="card-title">分享购物体验</div>
      <textarea v-model="content" class="form-control" placeholder="分享你的购物体验或发现..."></textarea>
      <div class="upload-area" @click="addImage">点击上传图片</div>
      <div class="preview-grid">
        <div class="preview-item" v-for="(img, index) in images" :key="index">
          <img :src="img" class="preview-img" />
          <div class="remove-btn" @click.stop="removeImage(index)"><i class="fas fa-times"></i></div>
        </div>
      </div>
      <button class="btn btn-primary" @click="submitPost"><i class="fas fa-paper-plane"></i> 发布动态</button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
const content = ref('');
const images = ref([]);

function addImage() {
  if (images.value.length < 4) {
    images.value.push('https://images.unsplash.com/photo-1511707171634-5f897ff02aa9');
  }
}

function removeImage(index) {
  images.value.splice(index, 1);
}

function submitPost() {
  console.log('发布内容：', content.value);
}
</script>
