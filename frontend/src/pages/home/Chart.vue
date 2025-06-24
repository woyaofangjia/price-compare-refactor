<template>
  <section class="page-content">
    <div class="container">
      <h2 class="section-title">商品价格趋势分析</h2>
      <div class="chart-container">
        <div class="chart-header">
          <h3>多平台价格对比 - 某品牌旗舰手机</h3>
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
import { onMounted } from 'vue'
onMounted(() => {
  if (window.Chart) {
    // 多平台价格对比折线图
    const comparisonCtx = document.getElementById('comparisonChart').getContext('2d');
    new window.Chart(comparisonCtx, {
      type: 'line',
      data: {
        labels: ['1日', '5日', '10日', '15日', '20日', '25日', '30日'],
        datasets: [
          {
            label: '京东',
            data: [3499, 3399, 3349, 3299, 3399, 3299, 3299],
            borderColor: '#e74c3c',
            borderWidth: 3,
            tension: 0.3
          },
          {
            label: '天猫',
            data: [3599, 3499, 3399, 3399, 3499, 3399, 3399],
            borderColor: '#f39c12',
            borderWidth: 3,
            tension: 0.3
          },
          {
            label: '拼多多',
            data: [3299, 3199, 3099, 3199, 3099, 3199, 3199],
            borderColor: '#2ecc71',
            borderWidth: 3,
            tension: 0.3
          },
          {
            label: '苏宁',
            data: [3499, 3449, 3399, 3349, 3399, 3349, 3349],
            borderColor: '#9b59b6',
            borderWidth: 3,
            tension: 0.3
          }
        ]
      },
      options: {
        responsive: true,
        plugins: { legend: { position: 'top' } },
        scales: {
          y: { beginAtZero: false, grid: { color: 'rgba(0, 0, 0, 0.05)' } },
          x: { grid: { display: false } }
        }
      }
    });
    // 历史价格波动柱状图
    const fluctuationCtx = document.getElementById('priceFluctuationChart').getContext('2d');
    new window.Chart(fluctuationCtx, {
      type: 'bar',
      data: {
        labels: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'],
        datasets: [{
          label: '平均价格 (元)',
          data: [3899, 3799, 3699, 3599, 3499, 3399, 3499, 3399, 3299, 3199, 3299, 3299],
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
    });
  }
})
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