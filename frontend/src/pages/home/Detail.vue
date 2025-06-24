<template>
  <section class="page-content">
    <div class="container">
      <div class="product-detail">
        <div class="detail-image">
          <img :src="product.img" :alt="product.title" />
        </div>
        <div class="detail-info">
          <h1>{{ product.title }}</h1>
          <div style="color: var(--gray); margin-bottom: 15px;">{{ product.desc }}</div>
          <div class="current-price">{{ product.price }}</div>
          <div class="price-change price-down">较上月 {{ product.priceChange }}%</div>
          <div class="price-comparison">
            <h3>平台比价</h3>
            <div class="platform-price" v-for="p in product.platformPrices" :key="p.platform">
              <span>{{ p.platform }}</span>
              <span :class="{'lowest-price': p.lowest}">{{ p.price }}</span>
            </div>
          </div>
          <div class="action-buttons">
            <button class="btn btn-primary"><i class="fas fa-shopping-cart"></i> 去购买</button>
            <button class="btn btn-outline" @click="addToFavorites"><i class="fas fa-heart"></i> 收藏商品</button>
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
import { onMounted } from 'vue'
let product = {
  img: 'https://via.placeholder.com/400x400?text=商品大图',
  title: '某品牌旗舰手机 8GB+256GB 全网通',
  desc: '型号：SM-X9000 | 颜色：曜夜黑',
  price: '￥3,299',
  priceChange: -5.2,
  platformPrices: [
    { platform: '京东自营', price: '￥3,299', lowest: true },
    { platform: '天猫官方旗舰', price: '￥3,399' },
    { platform: '拼多多百亿补贴', price: '￥3,199' },
    { platform: '苏宁易购', price: '￥3,349' }
  ]
}
function addToFavorites() {
  alert('商品已加入收藏夹！')
}
onMounted(() => {
  // Chart.js 绘制价格历史折线图
  if (window.Chart) {
    const ctx = document.getElementById('priceHistoryChart').getContext('2d');
    new window.Chart(ctx, {
      type: 'line',
      data: {
        labels: ['1日', '5日', '10日', '15日', '20日', '25日', '30日'],
        datasets: [{
          label: '价格走势 (元)',
          data: [3599, 3499, 3399, 3299, 3399, 3299, 3299],
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
})
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