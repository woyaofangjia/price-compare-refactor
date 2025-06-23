<template>
  <div class="dynamic-card">
    <div class="dynamic-header">
      <img :src="dynamic.avatar" class="user-avatar" alt="用户头像">
      <div class="user-info">
        <div class="username">{{ dynamic.username }}</div>
        <div class="post-time">{{ dynamic.timestamp }}</div>
      </div>
      <el-button 
        v-if="dynamic.userId === currentUserId" 
        icon="Delete" 
        type="danger" 
        plain 
        size="small"
        @click="$emit('delete', dynamic.id)"
      >
        删除
      </el-button>
    </div>
    
    <div class="dynamic-content">
      <div class="content-text">{{ dynamic.content }}</div>
      
      <div v-if="dynamic.images && dynamic.images.length > 0" class="images-grid">
        <img 
          v-for="(img, idx) in dynamic.images" 
          :key="idx" 
          :src="img" 
          class="image-item" 
          alt="动态图片"
          @click="openImageGallery(idx)"
        >
      </div>
      
      <div v-if="dynamic.product" class="product-card">
        <div class="product-info">
          <img :src="dynamic.product.image" alt="商品图片" class="product-image">
          <div class="product-details">
            <div class="product-name">{{ dynamic.product.name }}</div>
            <div class="product-price">¥{{ dynamic.product.price }}</div>
            <div class="product-platform">{{ dynamic.product.platform }}</div>
          </div>
        </div>
      </div>
    </div>
    
    <div class="dynamic-footer">
      <div class="interaction-bar">
        <div 
          :class="['interaction-btn', { active: dynamic.isLiked }]" 
          @click="handleLike(dynamic.id)"
          style="position:relative;"
        >
          <el-icon :size="22" :color="dynamic.isLiked ? '#f56c6c' : '#bbb'" :class="{ 'like-animate': likeAnimating }"><StarFilled /></el-icon>
          <span :class="{ 'like-num-animate': likeNumberAnimating }" :style="{ color: dynamic.isLiked ? '#f56c6c' : '#606266' }">{{ dynamic.likes }}</span>
          <span v-for="p in heartParticles" :key="p.id" class="heart-particle" :style="getParticleStyle(p.angle)">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="#f56c6c"><path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41 0.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/></svg>
          </span>
        </div>
        <div 
          class="interaction-btn comment-btn" 
          @click="$emit('view-detail', dynamic.id)"
        >
          <el-icon :size="20" color="#409EFF"><ChatDotRound /></el-icon>
          <span class="comment-num">{{ dynamic.comments }}</span>
        </div>
        <div 
          :class="['interaction-btn', { active: dynamic.isCollected }]" 
          @click="$emit('collect', dynamic.id)"
        >
          <el-icon :size="18"><Star /></el-icon>
          <span>收藏</span>
        </div>
      </div>
      <div class="share-btn">
        <el-dropdown trigger="click">
          <span class="interaction-btn">
            <el-icon :size="18"><Share /></el-icon>
            <span>分享</span>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item><el-icon><Share /></el-icon> 微博</el-dropdown-item>
              <el-dropdown-item><el-icon><Share /></el-icon> 微信</el-dropdown-item>
              <!-- <el-dropdown-item><el-icon><QQ /></el-icon> QQ</el-dropdown-item> -->
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps, ref, nextTick } from 'vue'
import { 
  /* Thumb, */ ChatDotRound, Star, Share, StarFilled, 
  /* Weibo, Wechat, QQ, HeartFilled, */ Delete 
} from '@element-plus/icons-vue'

const props = defineProps({
  dynamic: {
    type: Object,
    required: true
  },
  currentUserId: {
    type: Number,
    default: 0
  }
})

const heartParticles = ref([])
const likeAnimating = ref(false)
const likeNumberAnimating = ref(false)

const handleLike = async (id) => {
  likeAnimating.value = true
  likeNumberAnimating.value = true
  createHeartParticles()
  setTimeout(() => {
    likeAnimating.value = false
  }, 350)
  setTimeout(() => {
    likeNumberAnimating.value = false
  }, 500)
  // 触发父组件逻辑
  emit('like', id)
}

function createHeartParticles() {
  heartParticles.value = []
  for (let i = 0; i < 8; i++) {
    heartParticles.value.push({
      id: Math.random(),
      angle: (i * 45),
    })
  }
  setTimeout(() => {
    heartParticles.value = []
  }, 600)
}

const openImageGallery = (index) => {
  // 实现图片画廊功能
  console.log(`打开图片画廊，当前索引: ${index}`)
}

function getParticleStyle(angle) {
  return {
    position: 'absolute',
    left: '10px',
    top: '-2px',
    transform: `rotate(${angle}deg) translate(0, -30px)`,
    animation: 'heart-explode 0.6s cubic-bezier(.36,1.56,.64,1)',
    pointerEvents: 'none',
    zIndex: 2
  }
}
</script>

<style scoped lang="scss">
.dynamic-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  transition: transform 0.3s, box-shadow 0.3s;
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  }
  
  .dynamic-header {
    display: flex;
    align-items: center;
    padding: 20px;
    border-bottom: 1px solid #f0f2f5;
  }
  
  .user-avatar {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    object-fit: cover;
    border: 2px solid #e6f7ff;
  }
  
  .user-info {
    margin-left: 15px;
    flex-grow: 1;
    
    .username {
      font-weight: bold;
      font-size: 16px;
      color: #303133;
    }
    
    .post-time {
      font-size: 13px;
      color: #909399;
      margin-top: 4px;
    }
  }
  
  .dynamic-content {
    padding: 20px;
    
    .content-text {
      font-size: 15px;
      line-height: 1.7;
      margin-bottom: 15px;
    }
  }
  
  .images-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 10px;
    margin-top: 15px;
    
    .image-item {
      width: 100%;
      height: 180px;
      border-radius: 8px;
      object-fit: cover;
      cursor: pointer;
      transition: transform 0.3s;
      
      &:hover {
        transform: scale(1.03);
      }
    }
  }
  
  .dynamic-footer {
    padding: 15px 20px;
    border-top: 1px solid #f0f2f5;
    display: flex;
    justify-content: space-between;
  }
  
  .interaction-bar {
    display: flex;
    gap: 25px;
  }
  
  .interaction-btn {
    display: flex;
    align-items: center;
    gap: 6px;
    cursor: pointer;
    color: #606266;
    transition: color 0.3s;
    
    &:hover {
      color: #409EFF;
    }
    
    &.active {
      color: #409EFF;
    }
  }
  
  .product-card {
    margin-top: 15px;
    border: 1px solid #ebeef5;
    border-radius: 8px;
    padding: 12px;
    background-color: #fafafa;
    transition: all 0.3s ease;
    
    &:hover {
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
      transform: translateY(-2px);
    }
    
    .product-info {
      display: flex;
      align-items: center;
    }
    
    .product-image {
      width: 70px;
      height: 70px;
      object-fit: cover;
      border-radius: 6px;
      margin-right: 15px;
    }
    
    .product-details {
      flex: 1;
      
      .product-name {
        font-weight: bold;
        font-size: 14px;
        margin-bottom: 5px;
        color: #303133;
      }
      
      .product-price {
        font-size: 16px;
        font-weight: bold;
        color: #f56c6c;
        margin-bottom: 3px;
      }
      
      .product-platform {
        font-size: 12px;
        color: #909399;
      }
    }
  }
  
  @media (max-width: 768px) {
    .images-grid {
      grid-template-columns: repeat(2, 1fr);
    }
  }
}

.like-animate {
  animation: like-bounce 0.35s cubic-bezier(.36,1.56,.64,1) forwards;
}
@keyframes like-bounce {
  0% { transform: scale(1); }
  50% { transform: scale(1.4); }
  100% { transform: scale(1); }
}
.like-num-animate {
  animation: like-num-jump 0.5s cubic-bezier(.36,1.56,.64,1);
}
@keyframes like-num-jump {
  0% { transform: translateY(0); }
  30% { transform: translateY(-10px); }
  60% { transform: translateY(2px); }
  100% { transform: translateY(0); }
}

.heart-particle {
  opacity: 0.8;
  pointer-events: none;
}
@keyframes heart-explode {
  0% { opacity: 1; transform: rotate(var(--angle, 0deg)) translate(0, 0); }
  80% { opacity: 1; }
  100% { opacity: 0; transform: rotate(var(--angle, 0deg)) translate(0, -30px) scale(1.2); }
}
.comment-btn {
  background: #f4f8ff;
  border-radius: 16px;
  padding: 2px 12px 2px 6px;
  transition: background 0.2s;
  margin-left: 10px;
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  &:hover {
    background: #e6f0ff;
  }
}
.comment-num {
  color: #409EFF;
  font-weight: 500;
  font-size: 15px;
}
</style>