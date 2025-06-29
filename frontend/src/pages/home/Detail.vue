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
            <button class="btn btn-outline" @click="showAlertModal = true">
              <i class="fas fa-bell"></i> 
              {{ alertPrice ? `提醒价: ${alertPrice}元` : '设置提醒' }}
            </button>
          </div>
        </div>
      </div>
      <div class="price-overview">
        <div class="overview-header">
          <h3><i class="fas fa-chart-line"></i> 价格概览</h3>
        </div>
        <div class="overview-content">
          <div class="price-stats">
            <div class="stat-card">
<<<<<<< Updated upstream
              <div class="stat-label">当前价格</div>
              <div class="stat-value current">{{ product?.price || '—' }}元</div>
            </div>
            <div class="stat-card">
=======
>>>>>>> Stashed changes
              <div class="stat-label">历史最低</div>
              <div class="stat-value low">{{ priceStats?.minPrice || '—' }}元</div>
            </div>
            <div class="stat-card">
              <div class="stat-label">历史最高</div>
              <div class="stat-value high">{{ priceStats?.maxPrice || '—' }}元</div>
            </div>
            <div class="stat-card">
              <div class="stat-label">平均价格</div>
              <div class="stat-value avg">{{ priceStats?.avgPrice || '—' }}元</div>
            </div>
          </div>
          <div class="recent-trend">
            <h4>最近7天价格走势</h4>
            <canvas id="recentPriceChart"></canvas>
          </div>
        </div>
      </div>
    </div>

    <!-- 价格提醒设置弹窗 -->
    <div v-if="showAlertModal" class="modal-overlay" @click="showAlertModal = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>设置价格提醒</h3>
          <button class="close-btn" @click="showAlertModal = false">&times;</button>
        </div>
        <div class="modal-body">
          <div class="alert-form">
            <label>当价格低于以下价格时通知我：</label>
            <div class="price-input-group">
              <input 
                type="number" 
                v-model="tempAlertPrice" 
                placeholder="输入提醒价格"
                step="0.01"
                min="0"
              />
              <span class="currency">元</span>
            </div>
            <div class="current-price-info">
              当前价格：<span class="current-price-text">{{ product?.price || '—' }}元</span>
            </div>
            <div class="alert-tips">
              <p><i class="fas fa-info-circle"></i> 提示：</p>
              <ul>
                <li>若商品未收藏，设置提醒后会自动收藏该商品</li>
                <li>当商品价格低于设定价格时，系统会发送通知</li>
                <li>可随时修改或取消提醒设置</li>
              </ul>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-outline" @click="showAlertModal = false">取消</button>
          <button class="btn btn-primary" @click="saveAlertPrice">保存提醒</button>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const product = ref(null)
const platformPrices = ref([])
const priceHistory = ref([])
const priceStats = ref(null)
const isFavorite = ref(false)
const favoriteId = ref(null)
const alertPrice = ref(null)
const showAlertModal = ref(false)
const tempAlertPrice = ref('')
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
  
  try {
    const controller = new AbortController()
    const timeoutId = setTimeout(() => controller.abort(), 10000) // 10秒超时
    
    const res = await fetch(`/api/favorites/check?userId=${userId}&productId=${id}`, {
      signal: controller.signal
    })
    
    clearTimeout(timeoutId)
    
    const data = await res.json()
    isFavorite.value = !!data.exists
    favoriteId.value = data.id || null
    // 获取提醒价格
    if (data.exists && data.alertPrice) {
      alertPrice.value = data.alertPrice
    }
  } catch (error) {
    console.error('检查收藏状态失败:', error)
    // 收藏状态检查失败不影响页面显示，静默处理
    isFavorite.value = false
    favoriteId.value = null
  }
}

// 获取价格统计数据
async function getPriceStats(productId) {
  try {
    console.log('开始获取价格统计数据，商品ID:', productId)
    
    // 设置超时时间
    const controller = new AbortController()
    const timeoutId = setTimeout(() => controller.abort(), 25000) // 25秒超时
    
    const res = await fetch(`/api/products/${productId}/chart-data?enhanced=true`, {
      signal: controller.signal,
      headers: {
        'Content-Type': 'application/json'
      }
    })
    
    clearTimeout(timeoutId)
    
    console.log('价格统计API响应状态:', res.status, res.statusText)
    
    if (!res.ok) {
      const errorData = await res.json().catch(() => ({}))
      console.error('价格统计API错误:', errorData)
      throw new Error(`获取价格统计失败: ${res.status} ${res.statusText}`)
    }
    
    const data = await res.json()
    console.log('价格统计数据获取成功:', data)
    
    if (data.fluctuationData && data.fluctuationData.length > 0) {
      // 计算总体统计
      const allPrices = data.fluctuationData.flatMap(item => [
        item.minPrice, item.maxPrice, item.avgPrice
      ]).filter(price => price > 0)
      
      console.log('计算价格统计，有效价格数量:', allPrices.length)
      
      priceStats.value = {
        minPrice: Math.min(...allPrices),
        maxPrice: Math.max(...allPrices),
        avgPrice: Number((allPrices.reduce((sum, price) => sum + price, 0) / allPrices.length).toFixed(2))
      }
      
      console.log('价格统计计算结果:', priceStats.value)
    } else {
      console.log('没有价格波动数据，跳过统计计算')
    }
    
    return data
  } catch (error) {
    console.error('获取价格统计失败:', error)
    console.error('价格统计错误详情:', {
      name: error.name,
      message: error.message,
      stack: error.stack
    })
    
    // 如果是超时或网络错误，不显示弹窗，让主函数处理
    if (error.name === 'AbortError' || error.message.includes('Failed to fetch')) {
      throw error // 重新抛出，让主函数处理
    }
    
    // 其他错误（如API错误）静默处理，不影响页面显示
    console.log('价格统计错误，但不影响页面显示')
    return null
  }
}

// 渲染简化图表
function renderRecentPriceChart(trendData) {
  if (!window.Chart || !trendData) return
  
  const ctx = document.getElementById('recentPriceChart')
  if (!ctx) return
  
  // 获取最近7天的数据
  const platforms = Object.keys(trendData)
  if (platforms.length === 0) return
  
  const firstPlatform = platforms[0]
  const recentData = trendData[firstPlatform].slice(0, 7).reverse() // 最近7天，按时间正序
  
    new window.Chart(ctx, {
      type: 'line',
      data: {
      labels: recentData.map(item => formatDate(item.date)),
        datasets: [{
          label: '价格走势 (元)',
        data: recentData.map(item => item.price),
          borderColor: '#4361ee',
          backgroundColor: 'rgba(67, 97, 238, 0.1)',
        borderWidth: 2,
          tension: 0.3,
        fill: true,
        pointBackgroundColor: '#4361ee',
        pointBorderColor: '#fff',
        pointBorderWidth: 2,
        pointRadius: 4
        }]
      },
      options: {
        responsive: true,
      maintainAspectRatio: false,
      plugins: { 
        legend: { display: false } 
      },
        scales: {
        y: { 
          beginAtZero: false, 
          grid: { color: 'rgba(0, 0, 0, 0.05)' },
          ticks: { color: '#333' }
        },
        x: { 
          grid: { display: false },
          ticks: {
            maxTicksLimit: 7,
            color: '#333'
          }
        }
      },
      interaction: {
        intersect: false,
        mode: 'index'
      }
    }
  })
}

// 在 onMounted 中添加错误处理
onMounted(async () => {
  const id = route.params.id
  if (!userId) {
    router.push('/login')
    return
  }
  
  console.log('开始加载商品详情，商品ID:', id, '用户ID:', userId)
  
  try {
    // 设置更长的超时时间
    const controller = new AbortController()
    const timeoutId = setTimeout(() => controller.abort(), 30000) // 30秒超时
    
    const fetchOptions = {
      signal: controller.signal,
      headers: {
        'Content-Type': 'application/json'
      }
    }
    
    // 商品详情
    console.log('正在获取商品详情...')
    const res = await fetch(`/api/products/${id}`, fetchOptions)
    console.log('商品详情响应状态:', res.status, res.statusText)
    
    if (!res.ok) {
      const errorData = await res.json().catch(() => ({}))
      console.error('商品详情API错误:', errorData)
      throw new Error(`获取商品详情失败: ${res.status} ${res.statusText}`)
    }
    
    product.value = await res.json()
    console.log('商品详情获取成功:', product.value)

    // 平台比价
    console.log('正在获取平台价格...')
    const platRes = await fetch(`/api/products/${id}/platform-prices`, fetchOptions)
    if (!platRes.ok) {
      console.error('平台价格API错误:', platRes.status, platRes.statusText)
      throw new Error('获取平台价格失败')
    }
    platformPrices.value = await platRes.json()
    console.log('平台价格获取成功:', platformPrices.value)

    // 获取价格统计和趋势数据
    console.log('正在获取价格统计数据...')
    const chartData = await getPriceStats(id)
    if (chartData && chartData.trendData) {
      // 渲染简化图表
      await nextTick()
      renderRecentPriceChart(chartData.trendData)
      console.log('图表渲染完成')
    }

    // 查询是否已收藏
    console.log('正在检查收藏状态...')
    await checkFavorite()
    
    clearTimeout(timeoutId)
    console.log('所有数据加载完成')
  } catch (error) {
    console.error('加载数据失败:', error)
    console.error('错误详情:', {
      name: error.name,
      message: error.message,
      stack: error.stack
    })
    
    // 根据错误类型显示不同的提示
    if (error.name === 'AbortError') {
      alert('加载超时，请检查网络连接后重试')
    } else if (error.message.includes('Failed to fetch')) {
      alert('网络连接失败，请检查网络后重试')
    } else if (error.message.includes('商品不存在')) {
      alert('商品不存在或已被删除，请返回首页重新选择商品')
    } else {
      alert(`加载数据失败: ${error.message}`)
    }
  }
})

// 日期格式化函数
function formatDate(dateStr) {
  if (!dateStr) return '';
  const d = new Date(dateStr);
  return String(d.getMonth() + 1).padStart(2, '0') + '-' + String(d.getDate()).padStart(2, '0');
  }

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

async function saveAlertPrice() {
  const id = route.params.id
  if (!userId || !id) {
    alert('请先登录或商品信息有误')
    return
  }

  if (!tempAlertPrice.value || parseFloat(tempAlertPrice.value) <= 0) {
    alert('请输入有效的提醒价格')
    return
  }

  try {
    // 如果商品未收藏，先收藏
    if (!isFavorite.value) {
      const favRes = await fetch('/api/favorites', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ productId: id, userId: userId })
      })
      if (!favRes.ok) {
        throw new Error('收藏商品失败')
      }
      await checkFavorite()
    }

    // 保存提醒价格
    const alertRes = await fetch(`/api/favorites/${favoriteId.value}/alert`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ alertPrice: parseFloat(tempAlertPrice.value) })
    })

    if (alertRes.ok) {
      alertPrice.value = parseFloat(tempAlertPrice.value)
      showAlertModal.value = false
      alert('价格提醒设置成功！')
    } else {
      const data = await alertRes.json()
      throw new Error(data.message || '设置提醒失败')
    }
  } catch (error) {
    console.error('设置提醒失败:', error)
    alert(error.message || '设置提醒失败')
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
  margin-bottom: 10px;
  color: var(--dark);
}
.current-price {
  font-size: 2rem;
  font-weight: bold;
  color: var(--warning);
  margin: 15px 0;
}
.price-change {
  display: inline-block;
  padding: 5px 12px;
  border-radius: 20px;
  font-size: 0.9rem;
  margin-bottom: 20px;
}
.price-down {
  background-color: rgba(0, 200, 83, 0.1);
  color: #00c853;
}
.price-up {
  background-color: rgba(255, 0, 0, 0.1);
  color: #e74c3c;
}
.price-comparison {
  margin: 20px 0;
}
.price-comparison h3 {
  margin-bottom: 10px;
  color: var(--dark);
}
.platform-price {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid var(--light-gray);
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
.chart-container {
  margin-top: 30px;
  background: white;
  border-radius: 15px;
  padding: 25px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.05);
}
.chart-header h3 {
  margin-bottom: 20px;
  color: var(--dark);
}

/* 弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 15px;
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 25px;
  border-bottom: 1px solid var(--light-gray);
}

.modal-header h3 {
  margin: 0;
  color: var(--dark);
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: var(--gray);
}

.modal-body {
  padding: 25px;
}

.alert-form label {
  display: block;
  margin-bottom: 10px;
  color: var(--dark);
  font-weight: 500;
}

.price-input-group {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.price-input-group input {
  flex: 1;
  padding: 12px;
  border: 2px solid var(--light-gray);
  border-radius: 8px;
  font-size: 1rem;
  margin-right: 10px;
}

.price-input-group input:focus {
  outline: none;
  border-color: var(--primary);
}

.currency {
  color: var(--gray);
  font-weight: 500;
}

.current-price-info {
  background: var(--light);
  padding: 12px;
  border-radius: 8px;
  margin-bottom: 20px;
  color: var(--dark);
}

.current-price-text {
  color: var(--warning);
  font-weight: bold;
}

.alert-tips {
  background: rgba(67, 97, 238, 0.05);
  padding: 15px;
  border-radius: 8px;
  border-left: 4px solid var(--primary);
}

.alert-tips p {
  margin: 0 0 10px 0;
  color: var(--primary);
  font-weight: 500;
}

.alert-tips ul {
  margin: 0;
  padding-left: 20px;
  color: var(--gray);
}

.alert-tips li {
  margin-bottom: 5px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
  padding: 20px 25px;
  border-top: 1px solid var(--light-gray);
}

@media (max-width: 768px) {
  .product-detail {
    grid-template-columns: 1fr;
    gap: 20px;
}
  
  .action-buttons {
    flex-direction: column;
  }
  
  .modal-content {
    width: 95%;
    margin: 20px;
  }
}

/* 价格概览样式 */
.price-overview {
  background: white;
  border-radius: 15px;
  padding: 25px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.05);
  margin-top: 30px;
}

.overview-header {
  margin-bottom: 20px;
}

.overview-header h3 {
  font-size: 1.3rem;
  color: var(--dark);
  display: flex;
  align-items: center;
  margin: 0;
}

.overview-header h3 i {
  margin-right: 10px;
  color: var(--primary);
}

.overview-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 25px;
}

.price-stats {
  display: grid;
<<<<<<< Updated upstream
  grid-template-columns: 1fr 1fr;
=======
  grid-template-columns: 1fr;
>>>>>>> Stashed changes
  gap: 15px;
}

.stat-card {
  background: var(--light);
  border-radius: 10px;
  padding: 15px;
  text-align: center;
  transition: transform 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
}

.stat-label {
  font-size: 0.9rem;
  color: var(--gray);
  margin-bottom: 8px;
}

.stat-value {
  font-size: 1.2rem;
  font-weight: bold;
  color: var(--dark);
}

.stat-value.current {
  color: var(--warning);
}

.stat-value.low {
  color: #00c853;
}

.stat-value.high {
  color: #e74c3c;
}

.stat-value.avg {
  color: var(--primary);
}

.recent-trend {
  background: var(--light);
  border-radius: 10px;
  padding: 20px;
}

.recent-trend h4 {
  font-size: 1.1rem;
  color: var(--dark);
  margin: 0 0 15px 0;
  text-align: center;
}

.recent-trend canvas {
  height: 200px !important;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .product-detail {
    grid-template-columns: 1fr;
  }
  
  .overview-content {
    grid-template-columns: 1fr;
  }
  
  .price-stats {
<<<<<<< Updated upstream
    grid-template-columns: 1fr 1fr;
=======
    grid-template-columns: 1fr;
>>>>>>> Stashed changes
  }
}

@media (max-width: 480px) {
  .price-stats {
    grid-template-columns: 1fr;
  }
}
</style>