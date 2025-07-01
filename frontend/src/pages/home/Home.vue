<template>
  <section class="page-content">
    <div class="main-container">
      <!-- 快速入口区域 -->
      <div class="quick-access">
        <h2 class="section-title">
          <i class="fas fa-rocket"></i> 快速入口
        </h2>
        <div class="quick-links">
          <router-link to="/square" class="quick-link">
            <div class="quick-link-icon">
              <i class="fas fa-bullhorn"></i>
            </div>
            <div class="quick-link-text">
              <h3>我的动态</h3>
              <p>查看和管理您的动态</p>
            </div>
          </router-link>
          <router-link to="/square" class="quick-link">
            <div class="quick-link-icon">
              <i class="fas fa-comments"></i>
            </div>
            <div class="quick-link-text">
              <h3>动态广场</h3>
              <p>浏览所有用户动态</p>
            </div>
          </router-link>
          <router-link to="/favorites" class="quick-link">
            <div class="quick-link-icon">
              <i class="fas fa-heart"></i>
            </div>
            <div class="quick-link-text">
              <h3>收藏夹</h3>
              <p>查看收藏的商品</p>
            </div>
          </router-link>
          <router-link to="/chart" class="quick-link">
            <div class="quick-link-icon">
              <i class="fas fa-chart-line"></i>
            </div>
            <div class="quick-link-text">
              <h3>价格图表</h3>
              <p>查看价格趋势</p>
            </div>
          </router-link>
        </div>
      </div>

      <!-- 品牌专栏 -->
      <h2 class="section-title">
        <i class="fas fa-tags"></i> 品牌专栏
      </h2>
      <div class="brands-grid" v-if="brands.length > 0">
        <div 
          class="brand-card" 
          v-for="brand in brands" 
          :key="brand.id" 
          @click="goToBrand(brand.id)"
        >
          <div class="brand-info">
            <h3 class="brand-name">{{ brand.name }}</h3>
            <p class="brand-count">{{ brand.product_count }} 件商品</p>
          </div>
          <div class="brand-arrow">
            <i class="fas fa-chevron-right"></i>
          </div>
        </div>
      </div>
      <div class="brands-loading" v-else-if="brandsLoading">
        <i class="fas fa-spinner fa-spin"></i>
        <p>加载品牌中...</p>
      </div>

      <h2 class="section-title">今日热门商品</h2>
      <div class="products-grid">
        <div class="product-card" v-for="item in hotProducts" :key="item.id" @click="goToProduct(item.id)" style="cursor:pointer;">
          <div class="product-image">
            <img :src="item.img || item.image || defaultImg" :alt="item.title" @error="onImgError" />
          </div>
          <div class="product-info">
            <div class="product-title">{{ item.title }}</div>
            <div class="product-price">{{ item.price }}</div>
            <div class="platform-tags">
              <span class="platform-tag" v-for="tag in item.platforms" :key="tag">{{ tag }}</span>
            </div>
          </div>
        </div>
      </div>
      <h2 class="section-title" style="margin-top: 40px;">近期降价商品</h2>
      <div class="products-grid">
        <div class="product-card" v-for="item in dropProducts" :key="item.id" @click="goToProduct(item.id)" style="cursor:pointer;">
          <div class="product-image">
            <img :src="item.img || item.image || defaultImg" :alt="item.title" @error="onImgError" />
          </div>
          <div class="product-info">
            <div class="product-title">{{ item.title }}</div>
            <div class="product-price">{{ item.price }} <span v-if="item.oldPrice" style="text-decoration: line-through; color: var(--gray); font-size: 0.9rem;">{{ item.oldPrice }}</span></div>
            <div class="platform-tags">
              <span class="platform-tag" v-for="tag in item.platforms" :key="tag">{{ tag }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const hotProducts = ref([])
const dropProducts = ref([])
const brands = ref([])
const brandsLoading = ref(true)
const defaultImg = '/default-product.png'

onMounted(async () => {
  // 品牌列表
  try {
    const brandsRes = await fetch('/api/products/brands')
    const brandsResult = await brandsRes.json()
    if (brandsResult.code === 0) {
      brands.value = brandsResult.data
    }
  } catch (error) {
    console.error('获取品牌列表失败:', error)
  } finally {
    brandsLoading.value = false
  }

  // 热门商品
  const hotRes = await fetch('/api/products/hot')
  hotProducts.value = await hotRes.json()

  // 降价商品
  const dropRes = await fetch('/api/products/drops')
  dropProducts.value = await dropRes.json()
})

function goToProduct(id) {
  router.push(`/product/${id}`)
}

function goToBrand(brandId) {
  router.push(`/brand/${brandId}`)
}

function onImgError(e) {
  e.target.src = defaultImg
}
</script>

<style scoped>
/* 首页特有样式 */
.page-content {
  padding: 30px 0;
  min-height: calc(100vh - 150px);
}

/* 快速入口样式 */
.quick-access {
  margin-bottom: 40px;
}

.quick-links {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 32px;
  margin-top: 20px;
}

.quick-link {
  display: flex;
  align-items: center;
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
  text-decoration: none;
  color: inherit;
  transition: all 0.3s ease;
}

.quick-link:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.1);
  color: var(--primary);
}

.quick-link-icon {
  width: 50px;
  height: 50px;
  background: linear-gradient(135deg, var(--primary), var(--secondary));
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  color: white;
  font-size: 1.5rem;
}

.quick-link-text h3 {
  font-size: 1.1rem;
  font-weight: 600;
  margin-bottom: 5px;
  color: var(--dark);
}

.quick-link-text p {
  font-size: 0.9rem;
  color: var(--gray);
  margin: 0;
}

/* 其他首页样式 */
.section-title {
  font-size: 1.8rem;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid var(--light-gray);
  color: var(--primary);
  display: flex;
  align-items: center;
}

.section-title i {
  margin-right: 10px;
}

/* 品牌专栏样式 */
.brands-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 40px;
}

.brand-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.brand-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.1);
  color: var(--primary);
}

.brand-info {
  flex: 1;
}

.brand-name {
  font-size: 1.1rem;
  font-weight: 600;
  margin: 0 0 5px 0;
  color: var(--dark);
}

.brand-count {
  font-size: 0.9rem;
  color: var(--gray);
  margin: 0;
}

.brand-arrow {
  color: var(--gray);
  transition: all 0.3s ease;
}

.brand-card:hover .brand-arrow {
  color: var(--primary);
  transform: translateX(3px);
}

.brands-loading {
  text-align: center;
  padding: 40px 20px;
  color: var(--gray);
}

.brands-loading i {
  font-size: 2rem;
  margin-bottom: 10px;
  color: var(--light-gray);
}

.brands-loading p {
  margin: 0;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 25px;
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
.price-change {
  font-size: 0.9rem;
  padding: 3px 8px;
  border-radius: 10px;
  display: inline-block;
}
.price-up {
  background-color: rgba(255, 0, 0, 0.1);
  color: #e74c3c;
}
.price-down {
  background-color: rgba(0, 200, 83, 0.1);
  color: #00c853;
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

@media (max-width: 1200px) {
  .quick-links {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .brands-grid {
    grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  }
}
@media (max-width: 700px) {
  .quick-links {
    grid-template-columns: 1fr;
  }
  
  .brands-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: 15px;
  }
  
  .brand-card {
    padding: 15px;
  }
  
  .brand-name {
    font-size: 1rem;
  }
}
</style>
