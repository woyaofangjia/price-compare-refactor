<template>
  <section class="page-content">
    <div class="container">
      <h2 class="section-title">商品价格趋势分析</h2>
      
      <!-- 商品选择区域 -->
      <div v-if="!selectedId" class="product-selection">
        <div class="selection-step">
          <h3>第一步：选择商品类型</h3>
          <div class="type-buttons">
            <button 
              class="type-btn" 
              :class="{ active: productType === 'hot' }"
              @click="selectProductType('hot')"
            >
              <i class="fas fa-fire"></i>
              热门商品
            </button>
            <button 
              class="type-btn" 
              :class="{ active: productType === 'favorites' }"
              @click="selectProductType('favorites')"
            >
              <i class="fas fa-heart"></i>
              已收藏商品
            </button>
          </div>
        </div>

        <div class="selection-step" v-if="productType">
          <h3>第二步：选择具体商品</h3>
          <div class="product-list">
            <div 
              v-for="product in availableProducts" 
              :key="product.id"
              class="product-item"
              :class="{ selected: selectedId === product.id }"
              @click="selectProduct(product.id)"
            >
              <div class="product-image">
                <img :src="getSafeImageUrl(product)" :alt="product.title" @error="onImgError" />
              </div>
              <div class="product-info">
                <h4>{{ product.title }}</h4>
                <div class="product-price-display">
                  <span class="price-amount">{{ product.price || product.currentPrice || '暂无价格' }}</span>
                  <span class="price-currency">元</span>
                </div>
                <div class="product-trend">
                  <i :class="['trend-arrow', (product.priceChange || 0) > 0 ? 'fas fa-arrow-up trend-up' : 'fas fa-arrow-down trend-down']"></i>
                  <span :class="['trend-text', (product.priceChange || 0) > 0 ? 'trend-up' : 'trend-down']">
                    {{ Math.abs(product.priceChange || 0) }}%
                  </span>
                </div>
              </div>
              <div class="select-indicator">
                <i class="fas fa-check"></i>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 图表展示区域 -->
      <div v-if="selectedId && selectedProduct" class="chart-section">
        <!-- 商品信息和操作栏 -->
        <div class="product-header">
          <div class="product-info-card">
            <div class="product-image-large">
              <img :src="getSafeImageUrl(selectedProduct)" :alt="selectedProduct.title" @error="onImgError" />
            </div>
            <div class="product-details">
              <h3>{{ selectedProduct.title }}</h3>
              <div class="product-desc">{{ selectedProduct.desc || selectedProduct.description || '暂无描述' }}</div>
            </div>
          </div>
          <div class="action-buttons">
            <button class="btn btn-outline" @click="refreshChart">
              <i class="fas fa-sync-alt"></i> 刷新数据
            </button>
            <button class="btn btn-outline" @click="backToSelection">
              <i class="fas fa-arrow-left"></i> 重新选择
            </button>
          </div>
        </div>

        <!-- 横向排版的图表 -->
        <div class="charts-grid">
          <div class="chart-container">
            <div class="chart-header">
              <h3><i class="fas fa-chart-line"></i> 多平台价格对比</h3>
            </div>
            <canvas id="comparisonChart"></canvas>
          </div>

          <div class="chart-container">
            <div class="chart-header">
              <h3><i class="fas fa-chart-bar"></i> 历史价格波动</h3>
            </div>
            <canvas id="priceFluctuationChart"></canvas>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-else-if="productType && availableProducts.length === 0" class="empty-state">
        <i class="fas fa-chart-line"></i>
        <h3>{{ productType === 'hot' ? '暂无热门商品' : '暂无收藏商品' }}</h3>
        <p>{{ productType === 'hot' ? '热门商品列表为空，请稍后再试' : '您还没有收藏任何商品，先去收藏一些商品吧！' }}</p>
        <router-link v-if="productType === 'favorites'" to="/favorites" class="btn btn-primary">
          <i class="fas fa-heart"></i> 去收藏商品
        </router-link>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted, watch, computed, nextTick } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const productType = ref('') // 'hot' 或 'favorites'
const availableProducts = ref([])
const selectedId = ref(null)
const comparisonData = ref({})
const monthlyData = ref([])
const defaultImg = '/default-product.png'
let comparisonChart = null
let fluctuationChart = null

const selectedProduct = computed(() => {
  return availableProducts.value.find(p => p.id === selectedId.value)
})

onMounted(async () => {
  // 等待 Chart.js 加载
  await waitForChart()
  
  // 默认选择热门商品
  selectProductType('hot')
})

// 等待 Chart.js 加载
function waitForChart() {
  return new Promise((resolve) => {
    if (window.Chart) {
      resolve()
    } else {
      const checkChart = () => {
        if (window.Chart) {
          resolve()
        } else {
          setTimeout(checkChart, 100)
        }
      }
      checkChart()
    }
  })
}

// 选择商品类型
async function selectProductType(type) {
  productType.value = type
  selectedId.value = null
  
  console.log('选择商品类型:', type)
  
  try {
    // 设置超时时间
    const controller = new AbortController()
    const timeoutId = setTimeout(() => controller.abort(), 20000) // 20秒超时
    
    const fetchOptions = {
      signal: controller.signal,
      headers: {
        'Content-Type': 'application/json'
      }
    }
    
    if (type === 'hot') {
      // 获取热门商品
      console.log('正在获取热门商品...')
      const res = await fetch('/api/products/hot', fetchOptions)
      console.log('热门商品API响应状态:', res.status, res.statusText)
      
      if (!res.ok) {
        const errorData = await res.json().catch(() => ({}))
        console.error('热门商品API错误:', errorData)
        throw new Error(`获取热门商品失败: ${res.status} ${res.statusText}`)
      }
      
      const hotProductsData = await res.json()
      console.log('热门商品获取成功，原始数量:', hotProductsData.length)
      
      // 验证热门商品的有效性
      const validHotProducts = []
      for (const product of hotProductsData) {
        if (!product || !product.id) {
          console.warn('发现无效热门商品记录:', product)
          continue
        }
        
        // 验证商品是否存在
        try {
          const productCheckRes = await fetch(`/api/products/${product.id}`, {
            signal: controller.signal,
            headers: { 'Content-Type': 'application/json' }
          })
          
          if (productCheckRes.ok) {
            const productData = await productCheckRes.json()
            validHotProducts.push({
              ...product,
              ...productData // 合并商品信息
            })
          } else {
            console.warn('热门商品不存在，跳过:', product.id)
          }
        } catch (checkError) {
          console.warn('验证热门商品时出错，跳过:', product.id, checkError)
          // 如果验证失败，仍然保留该商品
          validHotProducts.push(product)
        }
      }
      
      availableProducts.value = validHotProducts
      console.log('热门商品验证完成，有效数量:', availableProducts.value.length)
    } else if (type === 'favorites') {
      // 获取收藏商品
      const user = JSON.parse(localStorage.getItem('user') || '{}')
      if (!user.id) {
        router.push('/login')
        return
      }
      
      console.log('正在获取收藏商品，用户ID:', user.id)
      const res = await fetch(`/api/favorites?userId=${user.id}`, fetchOptions)
      console.log('收藏商品API响应状态:', res.status, res.statusText)
      
      if (!res.ok) {
        const errorData = await res.json().catch(() => ({}))
        console.error('收藏商品API错误:', errorData)
        throw new Error(`获取收藏商品失败: ${res.status} ${res.statusText}`)
      }
      
      const favoritesData = await res.json()
      console.log('收藏商品获取成功，原始数量:', favoritesData.length)
      
      // 验证收藏商品的有效性
      const validFavorites = []
      for (const favorite of favoritesData) {
        if (!favorite || !favorite.productId) {
          console.warn('发现无效收藏记录:', favorite)
          continue
        }
        
        // 验证商品是否存在
        try {
          const productCheckRes = await fetch(`/api/products/${favorite.productId}`, {
            signal: controller.signal,
            headers: { 'Content-Type': 'application/json' }
          })
          
          if (productCheckRes.ok) {
            const productData = await productCheckRes.json()
            validFavorites.push({
              ...favorite,
              ...productData // 合并商品信息
            })
        } else {
            console.warn('收藏的商品不存在，跳过:', favorite.productId)
          }
        } catch (checkError) {
          console.warn('验证收藏商品时出错，跳过:', favorite.productId, checkError)
          // 如果验证失败，仍然保留该商品
          validFavorites.push(favorite)
        }
      }
      
      availableProducts.value = validFavorites
      console.log('收藏商品验证完成，有效数量:', availableProducts.value.length)
    }
    
    clearTimeout(timeoutId)
    
    // 如果有商品，显示商品列表供用户选择
    if (availableProducts.value.length > 0) {
      console.log('商品列表加载完成，等待用户选择，有效商品数量:', availableProducts.value.length)
    } else {
      console.log('没有找到有效商品')
      alert('暂无可用商品，请稍后重试')
    }
  } catch (error) {
    console.error('获取商品列表失败:', error)
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
    } else if (error.message.includes('商品不存在') || error.message.includes('已被删除')) {
      // 商品不存在的情况已经在上面处理了，这里不需要重复提示
      console.log('商品不存在错误已处理')
    } else if (error.message.includes('500')) {
      alert('服务器内部错误，请稍后重试')
    } else {
      alert(`获取数据失败: ${error.message}`)
    }
  }
}

// 选择具体商品
function selectProduct(id) {
  console.log('用户手动选择商品，ID:', id)
  
  // 检查商品是否在可用列表中
  const product = availableProducts.value.find(p => p.id === id)
  if (!product) {
    console.error('商品不在可用列表中，ID:', id)
    alert('商品不存在或已被删除，请重新选择')
    return
  }
  
  selectedId.value = id
  console.log('选中的商品数据:', product)
  console.log('商品价格字段:', {
    price: product?.price,
    currentPrice: product?.currentPrice,
    priceChange: product?.priceChange
  })
  
  // 用户手动选择商品后，获取图表数据
  console.log('开始获取选中商品的图表数据...')
  
  // 使用商品ID获取图表数据
  fetchChartData(id)
}

// 图片加载错误处理
function onImgError(e) {
  console.log('图片加载失败，使用默认图片:', e.target.src)
  e.target.src = defaultImg
}

// 获取安全的图片URL
function getSafeImageUrl(product) {
  const imgUrl = product.img || product.image || product.avatar || ''
  
  // 如果是有效的URL，直接返回
  if (imgUrl && (imgUrl.startsWith('http') || imgUrl.startsWith('/'))) {
    return imgUrl
  }
  
  // 如果是无效的URL，返回默认图片
  return defaultImg
}

// 刷新图表数据
async function refreshChart() {
  if (selectedId.value) {
    console.log('用户点击刷新图表，商品ID:', selectedId.value)
    try {
      await fetchChartData(selectedId.value)
    } catch (error) {
      console.error('刷新图表失败:', error)
      // 错误已经在 fetchChartData 中处理，这里不需要重复处理
    }
  } else {
    console.warn('没有选中的商品，无法刷新图表')
  }
}

// 返回商品选择
function backToSelection() {
  console.log('用户点击重新选择，清除当前选择')
  selectedId.value = null
  // 清除图表数据
  comparisonData.value = {}
  monthlyData.value = []
  // 销毁图表
  if (comparisonChart) {
    comparisonChart.destroy()
    comparisonChart = null
  }
  if (fluctuationChart) {
    fluctuationChart.destroy()
    fluctuationChart = null
  }
  console.log('已清除选择，返回商品选择界面')
}

async function fetchChartData(id) {
  if (!id) return
  try {
    console.log('开始获取图表数据，商品ID:', id)
    console.log('当前可用商品列表:', availableProducts.value.map(p => ({ id: p.id, title: p.title })))
    
    // 设置超时时间
    const controller = new AbortController()
    const timeoutId = setTimeout(() => controller.abort(), 30000) // 30秒超时
    
    const fetchOptions = {
      signal: controller.signal,
      headers: {
        'Content-Type': 'application/json'
      }
    }
    
    // 同时获取图表数据和最新商品信息
    console.log('正在获取图表数据和商品信息...')
    console.log('请求URL:', `/api/products/${id}/chart-data`, `/api/products/${id}`)
    
    const [chartRes, productRes] = await Promise.all([
      fetch(`/api/products/${id}/chart-data`, fetchOptions),
      fetch(`/api/products/${id}`, fetchOptions)
    ])
    
    clearTimeout(timeoutId)
    
    console.log('API响应状态 - 图表数据:', chartRes.status, chartRes.statusText)
    console.log('API响应状态 - 商品信息:', productRes.status, productRes.statusText)
    
    if (!chartRes.ok) {
      const chartError = await chartRes.json().catch(() => ({}))
      console.error('图表数据API错误:', chartError)
      throw new Error(`获取图表数据失败: ${chartRes.status} ${chartRes.statusText}`)
    }
    
    if (!productRes.ok) {
      const productError = await productRes.json().catch(() => ({}))
      console.error('商品信息API错误:', productError)
      
      // 从可用商品列表中移除不存在的商品
      const productIndex = availableProducts.value.findIndex(p => p.id === id)
      if (productIndex !== -1) {
        const removedProduct = availableProducts.value.splice(productIndex, 1)[0]
        console.log('已移除不存在的商品:', removedProduct)
        
        // 如果当前选中的商品被移除，清除选择
        if (selectedId.value === id) {
          selectedId.value = null
          comparisonData.value = {}
          monthlyData.value = []
          
          // 销毁图表
          if (comparisonChart) {
            comparisonChart.destroy()
            comparisonChart = null
          }
          if (fluctuationChart) {
            fluctuationChart.destroy()
            fluctuationChart = null
          }
          
          // 提示用户重新选择，不自动选择下一个
          console.log('当前商品已被删除，请用户重新选择')
          alert('该商品已被删除，请重新选择其他商品')
        }
        
        throw new Error('商品不存在或已被删除')
      }
      
      throw new Error(`获取商品信息失败: ${productRes.status} ${productRes.statusText}`)
    }
    
    const chartData = await chartRes.json()
    const productData = await productRes.json()
    
    console.log('原始 chart-data:', chartData)
    console.log('最新商品数据:', productData)
    console.log('价格字段详情:', {
      currentPrice: productData.currentPrice,
      price: productData.price,
      latestPrice: productData.latestPrice,
      actualPrice: productData.actualPrice
    })
    
    if (!chartData.platformData || !chartData.monthlyData) {
      console.warn('图表数据不完整，platformData或monthlyData为空')
      alert('暂无图表数据')
      return
    }
    
    // 更新商品列表中的商品信息（包括最新价格）
    const productIndex = availableProducts.value.findIndex(p => p.id === id)
    if (productIndex !== -1) {
      availableProducts.value[productIndex] = {
        ...availableProducts.value[productIndex],
        ...productData
      }
    }
    
    // 数据去重和格式化
    comparisonData.value = processPlatformData(chartData.platformData)
    monthlyData.value = processMonthlyData(chartData.monthlyData)
    
    console.log('处理后的数据:', {
      platformData: comparisonData.value,
      monthlyData: monthlyData.value
    })
    
    // 延迟渲染确保 DOM 已更新
    await nextTick()
    renderCharts()
    console.log('图表渲染完成')
  } catch (error) {
    console.error('获取数据失败:', error)
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
    } else if (error.message.includes('商品不存在') || error.message.includes('已被删除')) {
      // 商品不存在的情况已经在上面处理了，这里不需要重复提示
      console.log('商品不存在错误已处理')
    } else if (error.message.includes('500')) {
      alert('服务器内部错误，请稍后重试')
    } else {
      alert(`获取数据失败: ${error.message}`)
    }
  }
}

function processPlatformData(platformData) {
  const processed = {}
  
  Object.keys(platformData).forEach(platform => {
    const platformPrices = platformData[platform]
    
    // 按日期去重，保留最新价格
    const uniqueByDate = {}
    platformPrices.forEach(item => {
      const date = item.date
      if (!uniqueByDate[date] || uniqueByDate[date].price < item.price) {
        uniqueByDate[date] = item
      }
    })
    
    // 转换为数组并排序
    processed[platform] = Object.values(uniqueByDate)
      .sort((a, b) => new Date(a.date) - new Date(b.date))
      .map(item => ({
        date: item.date,
        price: parseFloat(item.price) || 0
      }))
  })
  
  return processed
}

function processMonthlyData(monthlyData) {
  // 按月份去重，保留最新数据
  const uniqueByMonth = {}
  monthlyData.forEach(item => {
    const month = item.month
    if (!uniqueByMonth[month] || uniqueByMonth[month].avgPrice < item.avgPrice) {
      uniqueByMonth[month] = item
    }
  })
  
  return Object.values(uniqueByMonth)
    .sort((a, b) => a.month.localeCompare(b.month))
    .map(item => ({
      month: item.month,
      avgPrice: parseFloat(item.avgPrice) || 0
    }))
}

function renderCharts() {
  // 检查 Canvas 元素是否存在
  const comparisonCanvas = document.getElementById('comparisonChart')
  const fluctuationCanvas = document.getElementById('priceFluctuationChart')
  
  if (!comparisonCanvas || !fluctuationCanvas) {
    console.error('Canvas 元素不存在')
    return
  }
  
  // 检查 Chart.js 是否可用
  if (!window.Chart) {
    console.error('Chart.js 未加载')
    return
  }
  
  // 销毁旧图表
  if (comparisonChart) {
    comparisonChart.destroy()
  }
  if (fluctuationChart) {
    fluctuationChart.destroy()
  }
  
  // 检查数据是否为空
  if (Object.keys(comparisonData.value).length === 0) {
    console.error('平台数据为空')
    return
  }
  
  if (monthlyData.value.length === 0) {
    console.error('月度数据为空')
    return
  }
  
  // 定义平台颜色
  const platformColors = {
    '京东': '#e74c3c',
    '天猫': '#3498db',
    '苏宁': '#f39c12',
    '拼多多': '#9b59b6',
    '淘宝': '#1abc9c',
    '当当': '#34495e'
  }
  
  // 多平台价格对比折线图
  const comparisonCtx = comparisonCanvas.getContext('2d')
  comparisonChart = new window.Chart(comparisonCtx, {
    type: 'line',
    data: {
      labels: (comparisonData.value['京东'] || []).map(item => formatDateLabel(item.date)),
      datasets: Object.keys(comparisonData.value).map(platform => ({
        label: platform,
        data: comparisonData.value[platform].map(item => item.price),
        borderColor: platformColors[platform] || '#4361ee',
        backgroundColor: platformColors[platform] ? `${platformColors[platform]}20` : 'rgba(67, 97, 238, 0.1)',
        borderWidth: 3,
        tension: 0.3,
        fill: false
      }))
    },
    options: {
      responsive: true,
      plugins: { 
        legend: { 
          position: 'top',
          labels: { 
            color: '#333',
            padding: 15,
            usePointStyle: true
          }
        } 
      },
      scales: {
        y: { 
          beginAtZero: false, 
          grid: { color: 'rgba(0, 0, 0, 0.05)' },
          ticks: { 
            color: '#333',
            padding: 8
          }
        },
        x: { 
          grid: { display: false },
          ticks: { 
            color: '#333',
            padding: 8,
            maxTicksLimit: 8,
            callback: function(value, index, values) {
              const label = this.getLabelForValue(value)
              return formatDateLabel(label)
            }
          }
        }
      }
    }
  })
  
  // 历史价格波动柱状图
  const fluctuationCtx = fluctuationCanvas.getContext('2d')
  fluctuationChart = new window.Chart(fluctuationCtx, {
    type: 'bar',
    data: {
      labels: monthlyData.value.map(item => formatMonthLabel(item.month)),
      datasets: [{
        label: '平均价格 (元)',
        data: monthlyData.value.map(item => item.avgPrice),
        backgroundColor: 'rgba(67, 97, 238, 0.7)',
        borderColor: '#4361ee',
        borderWidth: 1
      }]
    },
    options: {
      responsive: true,
      plugins: { 
        legend: { 
          display: false 
        } 
      },
      scales: {
        y: { 
          beginAtZero: false, 
          grid: { color: 'rgba(0, 0, 0, 0.05)' },
          ticks: { 
            color: '#333',
            padding: 8
          }
        },
        x: { 
          grid: { display: false },
          ticks: { 
            color: '#333',
            padding: 8,
            callback: function(value, index, values) {
              const label = this.getLabelForValue(value)
              return formatMonthLabel(label)
            }
          }
        }
      }
    }
  })
}

function formatDateLabel(dateStr) {
  if (!dateStr) return ''
  try {
    const date = new Date(dateStr)
    if (isNaN(date.getTime())) {
      // 如果日期无效，尝试其他格式
      const parts = dateStr.split('-')
      if (parts.length >= 3) {
        return `${parts[0]}-${parts[1]}-${parts[2]}`
      }
      return dateStr
    }
    return date.toLocaleDateString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit'
    })
  } catch (error) {
    console.warn('日期格式化失败:', dateStr, error)
    return dateStr
  }
}

function formatMonthLabel(monthStr) {
  if (!monthStr) return ''
  try {
    const date = new Date(monthStr)
    if (isNaN(date.getTime())) {
      // 如果日期无效，尝试其他格式
      const parts = monthStr.split('-')
      if (parts.length >= 2) {
        return `${parts[0]}-${parts[1]}`
      }
      return monthStr
    }
    return date.toLocaleDateString('zh-CN', { 
      year: 'numeric',
      month: '2-digit'
    })
  } catch (error) {
    console.warn('月份格式化失败:', monthStr, error)
    return monthStr
  }
}
</script>

<style scoped>
.page-content {
  padding: 30px 0;
}

.section-title {
  font-size: 1.8rem;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid var(--light-gray);
  color: var(--primary);
}

/* 商品选择区域 */
.product-selection {
  background: white;
  border-radius: 15px;
  padding: 25px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.05);
  margin-bottom: 30px;
}

.selection-step {
  margin-bottom: 30px;
}

.selection-step:last-child {
  margin-bottom: 0;
}

.selection-step h3 {
  font-size: 1.2rem;
  margin-bottom: 15px;
  color: var(--dark);
  display: flex;
  align-items: center;
}

.selection-step h3::before {
  content: '';
  width: 4px;
  height: 20px;
  background: var(--primary);
  margin-right: 10px;
  border-radius: 2px;
}

/* 商品类型选择按钮 */
.type-buttons {
  display: flex;
  gap: 15px;
}

.type-btn {
  flex: 1;
  padding: 15px 20px;
  border: 2px solid var(--light-gray);
  border-radius: 10px;
  background: white;
  color: var(--gray);
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.type-btn:hover {
  border-color: var(--primary);
  color: var(--primary);
}

.type-btn.active {
  border-color: var(--primary);
  background: var(--primary);
  color: white;
}

.type-btn i {
  font-size: 1.1rem;
}

/* 商品列表 */
.product-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 15px;
}

.product-item {
  display: flex;
  align-items: center;
  padding: 15px;
  border: 2px solid var(--light-gray);
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
}

.product-item:hover {
  border-color: var(--primary);
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(67, 97, 238, 0.1);
}

.product-item.selected {
  border-color: var(--primary);
  background: rgba(67, 97, 238, 0.05);
}

.product-image {
  width: 60px;
  height: 60px;
  background: var(--light);
  border-radius: 8px;
  margin-right: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.product-image img {
  max-width: 90%;
  max-height: 90%;
  object-fit: contain;
}

.product-info {
  flex: 1;
  min-width: 0;
}

.product-info h4 {
  margin: 0 0 5px 0;
  font-size: 1rem;
  color: var(--dark);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.product-price-display {
  display: flex;
  align-items: baseline;
  gap: 3px;
  margin-bottom: 5px;
}

.price-amount {
  color: var(--warning);
  font-weight: bold;
  font-size: 1.2rem;
}

.price-currency {
  color: var(--gray);
  font-size: 0.8rem;
}

.product-trend {
  display: flex;
  align-items: center;
  gap: 4px;
}

.trend-arrow {
  font-size: 0.7rem;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--light);
}

.trend-text {
  font-size: 0.8rem;
  font-weight: 500;
}

.trend-value {
  font-weight: bold;
  font-size: 1rem;
}

.trend-up {
  color: #00c853;
}

.trend-down {
  color: #e74c3c;
}

.select-indicator {
  width: 24px;
  height: 24px;
  border: 2px solid var(--light-gray);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-left: 10px;
  transition: all 0.3s ease;
}

.product-item.selected .select-indicator {
  border-color: var(--primary);
  background: var(--primary);
  color: white;
}

.select-indicator i {
  font-size: 0.8rem;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.product-item.selected .select-indicator i {
  opacity: 1;
}

/* 图表区域 */
.chart-section {
  margin-top: 30px;
}

.product-header {
  background: white;
  border-radius: 15px;
  padding: 20px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.05);
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.product-info-card {
  display: flex;
  align-items: center;
  gap: 20px;
}

.product-image-large {
  width: 120px;
  height: 120px;
  background: var(--light);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.product-image-large img {
  max-width: 90%;
  max-height: 90%;
  object-fit: contain;
}

.product-details {
  flex: 1;
  min-width: 0;
}

.product-details h3 {
  margin: 0 0 10px 0;
  color: var(--dark);
}

.product-desc {
  color: var(--gray);
  margin-top: 10px;
}

.action-buttons {
  display: flex;
  gap: 10px;
}

.btn {
  padding: 8px 16px;
  border-radius: 6px;
  border: none;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 5px;
  text-decoration: none;
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

.btn-primary {
  background: var(--primary);
  color: white;
}

.btn-primary:hover {
  opacity: 0.9;
}

.btn-sm {
  padding: 6px 12px;
  font-size: 0.9rem;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 15px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.05);
}

.empty-state i {
  font-size: 4rem;
  color: var(--gray);
  margin-bottom: 20px;
}

.empty-state h3 {
  margin: 0 0 10px 0;
  color: var(--dark);
}

.empty-state p {
  color: var(--gray);
  margin-bottom: 20px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .charts-grid {
    grid-template-columns: 1fr;
    gap: 15px;
  }
}

@media (max-width: 768px) {
  .type-buttons {
    flex-direction: column;
  }
  
  .product-list {
    grid-template-columns: 1fr;
  }
  
  .product-item {
    flex-direction: column;
    text-align: center;
  }
  
  .product-image {
    margin-right: 0;
    margin-bottom: 10px;
  }
  
  .select-indicator {
    margin-left: 0;
    margin-top: 10px;
  }
  
  .product-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .product-info-card {
    flex-direction: column;
    text-align: center;
    gap: 15px;
  }
  
  .product-image-large {
    width: 100px;
    height: 100px;
  }
  
  .product-summary {
    flex-direction: column;
    gap: 15px;
    text-align: center;
  }
  
  .price-trend {
    justify-content: center;
  }
  
  .action-buttons {
    width: 100%;
    justify-content: center;
  }
  
  .charts-grid {
    grid-template-columns: 1fr;
    gap: 15px;
  }
  
  .chart-container {
    padding: 20px;
    height: 350px;
  }
  
  .chart-container canvas {
    height: 280px !important;
  }
}

/* 横向排版的图表 */
.charts-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.chart-container {
  background: white;
  border-radius: 15px;
  padding: 25px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.05);
  height: 400px;
}

.chart-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.chart-header h3 {
  margin: 0;
  color: var(--dark);
  display: flex;
  align-items: center;
  gap: 8px;
}

.chart-header h3 i {
  color: var(--primary);
}

.chart-container canvas {
  height: 320px !important;
}
</style>