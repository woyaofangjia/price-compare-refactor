<template>
  <section class="page-content">
    <div class="container">
      <div class="product-detail" v-if="product">
        <div class="detail-image">
          <img :src="product.img || product.image || defaultImg" :alt="product.title" @error="onImgError" style="object-fit:contain;width:100%;height:100%;" loading="lazy" />
        </div>
        <div class="detail-info">
          <h1>{{ product.title }}</h1>
          <div style="color: var(--gray); margin-bottom: 15px;">{{ product.desc }}</div>
          <div class="current-price">{{ product.price || '—' }}</div>
          <div class="price-change price-down">较上月 {{ product.priceChange }}%</div>
          <div class="price-comparison">
            <h3>平台比价</h3>
            <div class="platform-price" v-for="p in platformPrices" :key="p.platform">
              <span>{{ p.platform }}</span>
              <span :class="{'lowest-price': p.lowest}">{{ p.price }}</span>
            </div>
          </div>
          <div class="action-buttons">
            <button
              class="btn btn-outline"
              v-if="!isFavorite"
              @click="addToFavorites"
            ><i class="fas fa-heart"></i> 收藏商品</button>
            <button
              class="btn btn-outline"
              v-else
              @click="removeFromFavorites"
            ><i class="fas fa-heart-broken"></i> 取消收藏</button>
            <button class="btn btn-outline"><i class="fas fa-bell"></i> 设置提醒</button>
          </div>
        </div>
      </div>
      <div class="chart-container">
        <div class="chart-header">
          <h3>价格历史趋势</h3>
        </div>
        <canvas id="priceHistoryChart"></canvas>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const product = ref(null)
const platformPrices = ref([])
const priceHistory = ref([])
const isFavorite = ref(false)
const favoriteId = ref(null)
const user = JSON.parse(localStorage.getItem('user') || '{}')
const userId = user && user.id
const productId = route.params.id
const defaultImg = '/default-product.png'

console.log('userId:', userId, 'productId:', productId, 'user:', user)

function onImgError(e) {
  e.target.src = defaultImg
}

async function checkFavorite() {
  const id = route.params.id
  if (!userId || !id) {
    isFavorite.value = false
    favoriteId.value = null
    return
  }
  const res = await fetch(`/api/favorites/check?userId=${userId}&productId=${id}`)
  const data = await res.json()
  isFavorite.value = !!data.exists
  favoriteId.value = data.id || null
}

onMounted(async () => {
  const id = route.params.id
  if (!userId) {
    router.push('/login')
    return
  }
  // 商品详情
  const res = await fetch(`/api/products/${id}`)
  product.value = await res.json()

  // 平台比价
  const platRes = await fetch(`/api/products/${id}/platform-prices`)
  platformPrices.value = await platRes.json()

  // 价格历史
  const histRes = await fetch(`/api/products/${id}/price-history`)
  priceHistory.value = await histRes.json()

  // 查询是否已收藏
  await checkFavorite()

  // Chart.js 绘图部分，数据用 priceHistory.value
  if (window.Chart) {
    const ctx = document.getElementById('priceHistoryChart').getContext('2d');
    if (priceHistory.value.length > 0) {
      new window.Chart(ctx, {
        type: 'line',
        data: {
          labels: priceHistory.value.map(item => item.date),
          datasets: [{
            label: '价格走势 (元)',
            data: priceHistory.value.map(item => item.price),
            borderColor: '#4361ee',
            backgroundColor: 'rgba(67, 97, 238, 0.1)',
            borderWidth: 3,
            tension: 0.3,
            fill: true
          }]
        },
        options: {
          responsive: true,
          plugins: { legend: { display: false } },
          scales: {
            y: { beginAtZero: false, grid: { color: 'rgba(0, 0, 0, 0.05)' } },
            x: { grid: { display: false } }
          }
        }
      });
    }
  }
})

async function addToFavorites() {
  const id = route.params.id
  if (!userId || !id) {
    alert('请先登录或商品信息有误')
    return
  }
  const res = await fetch('/api/favorites', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ productId: id, userId: userId })
  })
  if (res.ok) {
    await checkFavorite()
    alert('收藏成功！')
  } else {
    const data = await res.json()
    alert(data.message || '收藏失败')
  }
}

async function removeFromFavorites() {
  if (!favoriteId.value) {
    alert('参数有误')
    return
  }
  const res = await fetch(`/api/favorites/${favoriteId.value}`, { method: 'DELETE' })
  if (res.ok) {
    await checkFavorite()
    alert('已取消收藏')
  } else {
    const data = await res.json()
    alert(data.message || '取消收藏失败')
  }
}
</script>

<style scoped>
.page-content {
  padding: 30px 0;
}
.product-detail {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
  background: white;
  border-radius: 15px;
  padding: 30px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.05);
}
.detail-image {
  background: var(--light);
  border-radius: 10px;
  height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.detail-image img {
  max-width: 90%;
  max-height: 90%;
}
.detail-info h1 {
  font-size: 1.8rem;
  margin-bottom: 15px;
}
.current-price {
  font-size: 2rem;
  color: var(--warning);
  font-weight: bold;
  margin: 15px 0;
}
.price-change {
  font-size: 1.2rem;
  color: var(--success);
  font-weight: bold;
}
.price-down {
  color: var(--success);
}
.price-comparison {
  background: var(--light);
  border-radius: 10px;
  padding: 15px;
  margin: 20px 0;
}
.price-comparison h3 {
  margin-bottom: 10px;
}
.platform-price {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid #dee2e6;
}
.platform-price:last-child {
  border-bottom: none;
}
.lowest-price {
  color: var(--success);
  font-weight: bold;
}
.action-buttons {
  display: flex;
  gap: 15px;
  margin-top: 20px;
}
.btn {
  padding: 12px 25px;
  border-radius: 8px;
  border: none;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}
.btn-primary {
  background: var(--primary);
  color: white;
  flex: 1;
}
.btn-outline {
  background: transparent;
  border: 2px solid var(--primary);
  color: var(--primary);
}
.btn:hover {
  opacity: 0.9;
  transform: translateY(-2px);
}
.chart-container {
  background: white;
  border-radius: 15px;
  padding: 25px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.05);
  margin-top: 30px;
}
.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
</style>