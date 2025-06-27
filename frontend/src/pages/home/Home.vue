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

      <h2 class="section-title">今日热门商品</h2>
      <div class="products-grid">
        <div class="product-card" v-for="item in hotProducts" :key="item.id" @click="goToProduct(item.id)" style="cursor:pointer;">
          <div class="product-image">
            <img :src="item.img" :alt="item.title" />
          </div>
          <div class="product-info">
            <div class="product-title">{{ item.title }}</div>
            <div class="product-price">{{ item.price }}</div>
            <div :class="['price-change', item.priceChange > 0 ? 'price-up' : 'price-down']">
              {{ item.priceChange > 0 ? '涨' : '降' }} {{ Math.abs(item.priceChange) }}%
            </div>
            <div class="platform-tags">
              <span class="platform-tag" v-for="tag in item.platforms" :key="tag">{{ tag }}</span>
            </div>
          </div>
        </div>
      </div>
      <h2 class="section-title" style="margin-top: 40px;">近期降价商品</h2>
      <div class="products-grid">
        <div class="product-card" v-for="item in dropProducts" :key="item.id">
          <div class="product-image">
            <img :src="item.img" :alt="item.title" />
          </div>
          <div class="product-info">
            <div class="product-title">{{ item.title }}</div>
            <div class="product-price">{{ item.price }} <span v-if="item.oldPrice" style="text-decoration: line-through; color: var(--gray); font-size: 0.9rem;">{{ item.oldPrice }}</span></div>
            <div class="price-change price-down">降 {{ item.priceChange }}%</div>
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

onMounted(async () => {
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
}
@media (max-width: 700px) {
  .quick-links {
    grid-template-columns: 1fr;
  }
}
</style>
