<template>
  <div class="content-page">
    <div class="content-section">
      <div class="section-header">
        <h2 class="section-title">系统概览</h2>
      </div>
      
      <div class="stats-container">
        <div class="stats-grid">
          <div class="stat-card">
            <div class="stat-icon" style="background: rgba(67, 97, 238, 0.1); color: var(--primary);">
              <i class="fas fa-users"></i>
            </div>
            <div class="stat-info">
              <div class="stat-value">15,328</div>
              <div class="stat-label">总用户数</div>
            </div>
          </div>
          
          <div class="stat-card">
            <div class="stat-icon" style="background: rgba(76, 201, 240, 0.1); color: var(--success);">
              <i class="fas fa-shopping-bag"></i>
            </div>
            <div class="stat-info">
              <div class="stat-value">2,451</div>
              <div class="stat-label">监控商品</div>
            </div>
          </div>
          
          <div class="stat-card">
            <div class="stat-icon" style="background: rgba(247, 37, 133, 0.1); color: var(--warning);">
              <i class="fas fa-comment-alt"></i>
            </div>
            <div class="stat-info">
              <div class="stat-value">8,742</div>
              <div class="stat-label">用户动态</div>
            </div>
          </div>
          
          <div class="stat-card">
            <div class="stat-icon" style="background: rgba(108, 117, 125, 0.1); color: var(--gray);">
              <i class="fas fa-heart"></i>
            </div>
            <div class="stat-info">
              <div class="stat-value">42,156</div>
              <div class="stat-label">收藏商品</div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <div class="content-section">
      <div class="section-header">
        <h2 class="section-title">近期活动</h2>
        <div class="section-actions">
          <button class="btn btn-outline">查看更多</button>
        </div>
      </div>
      
      <div class="activity-list">
        <div class="activity-item">
          <div class="activity-icon">
            <i class="fas fa-user-plus"></i>
          </div>
          <div class="activity-content">
            <div class="activity-text">新用户 <strong>@user_2024</strong> 注册了账号</div>
            <div class="activity-time">2分钟前</div>
          </div>
        </div>
        
        <div class="activity-item">
          <div class="activity-icon">
            <i class="fas fa-shopping-cart"></i>
          </div>
          <div class="activity-content">
            <div class="activity-text">用户 <strong>@tech_lover</strong> 添加了新的商品监控</div>
            <div class="activity-time">30分钟前</div>
          </div>
        </div>
        
        <div class="activity-item">
          <div class="activity-icon">
            <i class="fas fa-comment"></i>
          </div>
          <div class="activity-content">
            <div class="activity-text">用户 <strong>@gadget_guru</strong> 发布了新的动态</div>
            <div class="activity-time">1小时前</div>
          </div>
        </div>
        
        <div class="activity-item">
          <div class="activity-icon">
            <i class="fas fa-heart"></i>
          </div>
          <div class="activity-content">
            <div class="activity-text">用户 <strong>@shopper123</strong> 收藏了iPhone 15 Pro</div>
            <div class="activity-time">2小时前</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Dashboard'
}
</script>

<style scoped>
.content-section {
  background: white;
  border-radius: 15px;
  padding: 25px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.05);
  margin-bottom: 25px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid var(--light-gray);
}

.section-title {
  font-size: 1.5rem;
  color: var(--primary);
  font-weight: 600;
}

.section-actions {
  display: flex;
  gap: 10px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 20px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 15px rgba(0,0,0,0.1);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin-right: 15px;
}

.stat-value {
  font-size: 1.8rem;
  font-weight: 700;
  margin-bottom: 5px;
  transition: all 0.5s ease;
}

.stat-label {
  color: var(--gray);
  font-size: 0.95rem;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.activity-item {
  display: flex;
  align-items: flex-start;
  padding: 15px;
  border-radius: 10px;
  background: #f9fafc;
  transition: background 0.3s ease;
}

.activity-item:hover {
  background: #f0f4ff;
}

.activity-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(67, 97, 238, 0.1);
  color: var(--primary);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
}

.activity-content {
  flex: 1;
}

.activity-text {
  margin-bottom: 5px;
}

.activity-time {
  font-size: 0.85rem;
  color: var(--gray);
}

.btn {
  padding: 8px 15px;
  border-radius: 6px;
  border: none;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
}

.btn-outline {
  background: transparent;
  border: 1px solid var(--primary);
  color: var(--primary);
}

.btn:hover {
  opacity: 0.9;
  transform: translateY(-2px);
}
</style>