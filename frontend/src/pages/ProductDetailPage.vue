<template>
  <div>
    <ProductDetailContent v-if="product" :product="product" />
    <div v-else style="text-align:center;margin-top:40px;">未找到该商品</div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import ProductDetailContent from '@/components/ProductDetailContent.vue'

const route = useRoute()
const product = ref(null)

// 假设全站商品数据可通过某种方式获取，这里用模拟数据
const allProducts = [
  {
    id: 1,
    name: 'Sony WH-1000XM5',
    image: 'https://images.unsplash.com/photo-1572536147248-ac59a8abfa4b',
    price: '¥2,499',
    tags: ['耳机', '降噪'],
    detail: '索尼旗舰降噪耳机，音质出色，佩戴舒适，适合通勤和办公。'
  },
  {
    id: 2,
    name: 'MacBook Pro 14',
    image: 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8',
    price: '¥14,999',
    tags: ['电脑', '苹果'],
    detail: '苹果高性能笔记本，适合创意设计和开发。'
  }
]

onMounted(() => {
  const id = Number(route.params.id)
  product.value = allProducts.find(p => p.id === id)
})
</script> 