<template>
  <section class="page-content">
    <div class="container">
      <h2 class="section-title">商品价格趋势分析</h2>
      <div style="margin-bottom: 20px;">
        <label>选择商品：</label>
        <select v-model="selectedId">
          <option v-for="item in products" :key="item.id" :value="item.id">
            {{ item.title }}
          </option>
        </select>
      </div>
      <div class="chart-container">
        <div class="chart-header">
          <h3>多平台价格对比 - {{ selectedProductTitle }}</h3>
        </div>
        <canvas id="comparisonChart"></canvas>
      </div>
      <div class="chart-container" style="margin-top: 30px;">
        <div class="chart-header">
          <h3>历史价格波动统计</h3>
        </div>
        <canvas id="priceFluctuationChart"></canvas>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'

const products = ref([])
const selectedId = ref(null)
const comparisonData = ref({})
const monthlyData = ref([])
let comparisonChart = null
let fluctuationChart = null

const selectedProductTitle = computed(() => {
  const found = products.value.find(p => p.id === selectedId.value)
  return found ? found.title : ''
})

onMounted(async () => {
  // 获取商品列表（可根据实际接口调整）
  const res = await fetch('/api/products/hot')
  products.value = await res.json()
  if (products.value.length > 0) {
    selectedId.value = products.value[0].id
    await fetchChartData(selectedId.value)
  }
})

watch(selectedId, (id) => {
  if (id) fetchChartData(id)
})

async function fetchChartData(id) {
  if (!id) return
  const res = await fetch(`/api/products/${id}/chart-data`)
  const data = await res.json()
  console.log('chart-data:', data)
  if (!data.platformData || !data.monthlyData) {
    alert('暂无图表数据')
    return
  }
  comparisonData.value = data.platformData
  monthlyData.value = data.monthlyData
  renderCharts()
}

function renderCharts() {
  // 销毁旧图表
  if (comparisonChart) {
    comparisonChart.destroy()
  }
  if (fluctuationChart) {
    fluctuationChart.destroy()
  }
  // 多平台价格对比折线图
  if (window.Chart) {
    const comparisonCtx = document.getElementById('comparisonChart').getContext('2d');
    comparisonChart = new window.Chart(comparisonCtx, {
      type: 'line',
      data: {
        labels: (comparisonData.value['京东'] || []).map(item => item.date),
        datasets: Object.keys(comparisonData.value).map(platform => ({
          label: platform,
          data: comparisonData.value[platform].map(item => item.price),
          borderWidth: 3,
          tension: 0.3
        }))
      },
      options: {
        responsive: true,
        plugins: { legend: { position: 'top' } },
        scales: {
          y: { beginAtZero: false, grid: { color: 'rgba(0, 0, 0, 0.05)' } },
          x: { grid: { display: false } }
        }
      }
    })
    // 历史价格波动柱状图
    const fluctuationCtx = document.getElementById('priceFluctuationChart').getContext('2d');
    fluctuationChart = new window.Chart(fluctuationCtx, {
      type: 'bar',
      data: {
        labels: monthlyData.value.map(item => item.month),
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
        plugins: { legend: { display: false } },
        scales: {
          y: { beginAtZero: false, grid: { color: 'rgba(0, 0, 0, 0.05)' } },
          x: { grid: { display: false } }
        }
      }
    })
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