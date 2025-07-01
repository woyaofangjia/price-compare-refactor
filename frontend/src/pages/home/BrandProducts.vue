<template>
  <section class="page-content">
    <div class="main-container">
      <!-- 品牌信息头部 -->
      <div class="brand-header">
        <div class="brand-info">
          <h1 class="brand-title">{{ brandName }}</h1>
          <p class="brand-stats">共 {{ total }} 件商品</p>
        </div>
        <div class="brand-actions">
          <button class="btn btn-outline" @click="$router.go(-1)">
            <i class="fas fa-arrow-left"></i> 返回
          </button>
        </div>
      </div>

      <!-- 商品列表 -->
      <div class="products-grid" v-if="products.length > 0">
        <div 
          class="product-card" 
          v-for="item in products" 
          :key="item.id" 
          @click="goToProduct(item.id)"
        >
          <div class="product-image">
            <img 
              :src="item.img || item.image || defaultImg" 
              :alt="item.title" 
              @error="onImgError" 
            />
          </div>
          <div class="product-info">
            <div class="product-title">{{ item.title }}</div>
            <div class="product-price">{{ item.current_price || item.price || '暂无价格' }}</div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div class="empty-state" v-else-if="!loading">
        <i class="fas fa-box-open"></i>
        <h3>暂无商品</h3>
        <p>该品牌暂无商品信息</p>
      </div>

      <!-- 加载状态 -->
      <div class="loading-state" v-if="loading">
        <i class="fas fa-spinner fa-spin"></i>
        <p>加载中...</p>
      </div>

      <!-- 分页 -->
      <div class="pagination" v-if="totalPages > 1">
        <button 
          class="page-btn" 
          :disabled="currentPage === 1"
          @click="changePage(currentPage - 1)"
        >
          <i class="fas fa-chevron-left"></i>
        </button>
        
        <span class="page-info">
          第 {{ currentPage }} 页，共 {{ totalPages }} 页
        </span>
        
        <button 
          class="page-btn" 
          :disabled="currentPage === totalPages"
          @click="changePage(currentPage + 1)"
        >
          <i class="fas fa-chevron-right"></i>
        </button>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

const products = ref([])
const loading = ref(true)
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)
const defaultImg = '/default-product.png'

const brandName = computed(() => route.params.brandName)

const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

onMounted(async () => {
  await loadProducts()
})

async function loadProducts() {
  loading.value = true
  try {
    const response = await fetch(
      `/api/products/brands/${encodeURIComponent(brandName.value)}?page=${currentPage.value}&pageSize=${pageSize.value}`
    )
    const result = await response.json()
    
    if (result.code === 0) {
      // 处理商品数据，添加平台信息
      products.value = result.data.list.map(product => ({
        ...product,
        platforms: product.platform ? [product.platform] : []
      }))
      total.value = result.data.total
    } else {
      console.error('获取品牌商品失败:', result.message)
    }
  } catch (error) {
    console.error('获取品牌商品失败:', error)
  } finally {
    loading.value = false
  }
}

function goToProduct(id) {
  router.push(`/product/${id}`)
}

function onImgError(e) {
  e.target.src = defaultImg
}

function changePage(page) {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
    loadProducts()
  }
}
</script>

<style scoped>
.page-content {
  padding: 30px 0;
  min-height: calc(100vh - 150px);
}

.brand-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
}

.brand-title {
  font-size: 2rem;
  font-weight: 600;
  color: var(--primary);
  margin: 0 0 5px 0;
}

.brand-stats {
  color: var(--gray);
  margin: 0;
}

.brand-actions {
  display: flex;
  gap: 10px;
}

.btn {
  padding: 10px 20px;
  border-radius: 8px;
  border: none;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.btn-outline {
  background: transparent;
  border: 1px solid var(--primary);
  color: var(--primary);
}

.btn-outline:hover {
  background: var(--primary);
  color: white;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 25px;
  margin-bottom: 30px;
}

.product-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 5px 15px rgba(0,0,0,0.05);
  transition: transform 0.3s ease;
  cursor: pointer;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0,0,0,0.1);
}

.product-image {
  height: 180px;
  background-color: #f0f3f8;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.product-image img {
  max-width: 90%;
  max-height: 90%;
  object-fit: contain;
}

.product-info {
  padding: 15px;
}

.product-title {
  font-weight: 600;
  margin-bottom: 8px;
  height: 40px;
  overflow: hidden;
}

.product-price {
  color: var(--warning);
  font-size: 1.2rem;
  font-weight: bold;
  margin-bottom: 8px;
}



.platform-tags {
  display: flex;
  margin-top: 10px;
}

.platform-tag {
  background: var(--light-gray);
  padding: 3px 8px;
  border-radius: 5px;
  font-size: 0.8rem;
  margin-right: 5px;
}

.empty-state, .loading-state {
  text-align: center;
  padding: 60px 20px;
  color: var(--gray);
}

.empty-state i, .loading-state i {
  font-size: 3rem;
  margin-bottom: 20px;
  color: var(--light-gray);
}

.empty-state h3, .loading-state h3 {
  margin: 0 0 10px 0;
  color: var(--dark);
}

.empty-state p, .loading-state p {
  margin: 0;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-top: 30px;
}

.page-btn {
  padding: 10px 15px;
  border: 1px solid var(--light-gray);
  background: white;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.page-btn:hover:not(:disabled) {
  background: var(--primary);
  color: white;
  border-color: var(--primary);
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  color: var(--gray);
  font-size: 0.9rem;
}

@media (max-width: 768px) {
  .brand-header {
    flex-direction: column;
    gap: 15px;
    text-align: center;
  }
  
  .brand-title {
    font-size: 1.5rem;
  }
  
  .products-grid {
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 15px;
  }
}
</style> 