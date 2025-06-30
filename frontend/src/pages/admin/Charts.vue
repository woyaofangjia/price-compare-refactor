<template>
  <div class="content-page">
    <div class="content-section">
      <div class="section-header">
        <h2 class="section-title">平台数据分析</h2>
        <div class="section-actions">
          <button class="btn btn-outline">导出数据</button>
        </div>
      </div>
      
      <div class="charts-container">
        <div class="chart-box">
          <div class="chart-title">用户活跃度分布</div>
          <div class="chart-content" ref="userActivityChart"></div>
        </div>
        
        <div class="chart-box">
          <div class="chart-title">商品类别分布</div>
          <div class="chart-content" ref="productCategoryChart"></div>
        </div>
        
        <div class="chart-box">
          <div class="chart-title">价格趋势监控</div>
          <div class="chart-content" ref="priceTrendChart"></div>
        </div>
        
        <div class="chart-box">
          <div class="chart-title">平台商品数量对比</div>
          <div class="chart-content" ref="platformComparisonChart"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import axios from 'axios'

export default {
  name: 'Charts',
  mounted() {
    this.initCharts()
    window.addEventListener('resize', this.handleResize)
  },
  beforeUnmount() {
    window.removeEventListener('resize', this.handleResize)
    this.charts.forEach(chart => chart.dispose())
  },
  data() {
    return {
      charts: []
    }
  },
  methods: {
    async initCharts() {
      // 用户活跃度分布图
      const userActivityChart = echarts.init(this.$refs.userActivityChart)
      // 动态获取数据
      let activityData = [
        { value: 0, name: '高活跃用户' },
        { value: 0, name: '中等活跃用户' },
        { value: 0, name: '低活跃用户' },
        { value: 0, name: '新用户' }
      ]
      try {
        const res = await axios.get('/api/users/activity-distribution')
        if (res.data && res.data.data) {
          activityData = res.data.data
        }
      } catch (e) { /* ignore */ }
      userActivityChart.setOption({
        tooltip: { trigger: 'item' },
        legend: {
          orient: 'vertical',
          right: 10,
          top: 'center'
        },
        series: [{
          name: '用户活跃度',
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 10,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: { show: false, position: 'center' },
          emphasis: {
            label: {
              show: true,
              fontSize: '18',
              fontWeight: 'bold'
            }
          },
          labelLine: { show: false },
          data: activityData,
          color: ['#4361ee', '#4cc9f0', '#f72585', '#6c757d']
        }]
      })

      // 商品类别分布图
      const productCategoryChart = echarts.init(this.$refs.productCategoryChart)
      productCategoryChart.setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: ['手机', '电脑', '平板', '耳机', '手表', '相机', '家电'],
          axisTick: { alignWithLabel: true }
        },
        yAxis: { type: 'value', name: '商品数量' },
        series: [{
          name: '商品数量',
          type: 'bar',
          barWidth: '60%',
          data: [1250, 890, 670, 540, 480, 320, 210],
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#4361ee' },
              { offset: 1, color: '#4cc9f0' }
            ])
          }
        }]
      })

      // 价格趋势监控图
      const priceTrendChart = echarts.init(this.$refs.priceTrendChart)
      priceTrendChart.setOption({
        tooltip: { trigger: 'axis' },
        legend: {
          data: ['手机平均价格', '电脑平均价格', '平板平均价格']
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月']
        },
        yAxis: { type: 'value', name: '价格 (元)' },
        series: [
          {
            name: '手机平均价格',
            type: 'line',
            smooth: true,
            data: [4200, 3900, 3800, 3750, 3600, 3500, 3400],
            lineStyle: { width: 3 }
          },
          {
            name: '电脑平均价格',
            type: 'line',
            smooth: true,
            data: [7800, 7600, 7500, 7400, 7300, 7200, 7100],
            lineStyle: { width: 3 }
          },
          {
            name: '平板平均价格',
            type: 'line',
            smooth: true,
            data: [3200, 3100, 3000, 2900, 2850, 2800, 2750],
            lineStyle: { width: 3 }
          }
        ],
        color: ['#4361ee', '#f72585', '#4cc9f0']
      })

      // 平台商品数量对比图
      const platformComparisonChart = echarts.init(this.$refs.platformComparisonChart)
      platformComparisonChart.setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' }
        },
        legend: {
          data: ['京东', '天猫', '拼多多', '苏宁']
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: [{
          type: 'category',
          data: ['手机', '电脑', '平板', '耳机', '手表']
        }],
        yAxis: [{ type: 'value' }],
        series: [
          { name: '京东', type: 'bar', stack: 'Ad', data: [320, 302, 301, 334, 190] },
          { name: '天猫', type: 'bar', stack: 'Ad', data: [220, 182, 191, 234, 290] },
          { name: '拼多多', type: 'bar', stack: 'Ad', data: [150, 212, 201, 154, 190] },
          { name: '苏宁', type: 'bar', stack: 'Ad', data: [98, 77, 101, 99, 60] }
        ],
        color: ['#e74c3c', '#f39c12', '#2ecc71', '#9b59b6']
      })

      this.charts = [userActivityChart, productCategoryChart, priceTrendChart, platformComparisonChart]
    },
    handleResize() {
      this.charts.forEach(chart => chart.resize())
    }
  }
}
</script>

<style scoped>
.charts-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(500px, 1fr));
  gap: 25px;
  margin-top: 20px;
}

.chart-box {
  background: white;
  border-radius: 15px;
  padding: 20px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.05);
  height: 350px;
}

.chart-title {
  font-size: 1.2rem;
  margin-bottom: 15px;
  color: var(--primary);
  text-align: center;
}

.chart-content {
  height: calc(100% - 30px);
}

@media (max-width: 768px) {
  .charts-container {
    grid-template-columns: 1fr;
  }
}
</style>