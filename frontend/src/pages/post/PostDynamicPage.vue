<template>
  <div class="post-dynamic-page">
    <el-page-header @back="goBack" content="发布动态" />
    <el-form :model="form" label-width="80px" class="post-form" @submit.prevent>
      <el-form-item label="内容">
        <el-input v-model="form.content" type="textarea" rows="4" placeholder="分享你的购物体验或发现..." />
      </el-form-item>
      <el-form-item label="图片">
        <el-upload
          action="#"
          list-type="picture-card"
          :auto-upload="false"
          :on-change="handleImageChange"
          :file-list="form.images"
        >
          <el-icon><Plus /></el-icon>
        </el-upload>
      </el-form-item>
      <el-form-item label="商品信息">
        <el-input v-model="form.product.name" placeholder="商品名称（可选）" />
        <el-input v-model="form.product.price" placeholder="价格（可选）" style="margin-top: 8px;" />
        <el-input v-model="form.product.platform" placeholder="平台（可选）" style="margin-top: 8px;" />
        <el-input v-model="form.product.image" placeholder="商品图片链接（可选）" style="margin-top: 8px;" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitPost">发布</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const router = useRouter()
const form = ref({
  content: '',
  images: [],
  product: {
    name: '',
    price: '',
    platform: '',
    image: ''
  }
})

const goBack = () => {
  router.back()
}

const handleImageChange = (file, fileList) => {
  form.value.images = fileList
}

const submitPost = () => {
  if (!form.value.content.trim()) {
    ElMessage.error('内容不能为空')
    return
  }
  // 模拟发布成功
  ElMessage.success('动态发布成功！')
  setTimeout(() => {
    router.push('/')
  }, 1000)
}
</script>

<style scoped>
.post-dynamic-page {
  max-width: 600px;
  margin: 30px auto;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 6px 16px rgba(0,0,0,0.08);
  padding: 30px;
}
.post-form {
  margin-top: 30px;
}
</style> 