<template>
  <div class="content-page">
    <div class="content-section">
      <div class="section-header">
        <h2 class="section-title">系统设置</h2>
        <div class="section-actions">
          <button class="btn btn-primary">保存设置</button>
        </div>
      </div>
      
      <div class="settings-container">
        <div class="settings-card">
          <div class="settings-header">
            <div class="settings-icon">
              <i class="fas fa-globe"></i>
            </div>
            <div class="settings-title">基本设置</div>
          </div>
          <div class="settings-form">
            <div class="form-row">
              <div class="form-group">
                <label>系统名称</label>
                <input type="text" class="form-control" v-model="settings.systemName">
              </div>
              <div class="form-group">
                <label>系统时区</label>
                <select class="form-control" v-model="settings.timezone">
                  <option value="UTC+08:00">UTC+08:00 北京时间</option>
                  <option value="UTC+00:00">UTC+00:00 格林威治时间</option>
                  <option value="UTC-05:00">UTC-05:00 纽约时间</option>
                </select>
              </div>
            </div>
            <div class="form-group">
              <label>系统公告</label>
              <textarea class="form-control" rows="3" v-model="settings.announcement"></textarea>
            </div>
          </div>
        </div>
        
        <div class="settings-card">
          <div class="settings-header">
            <div class="settings-icon">
              <i class="fas fa-bell"></i>
            </div>
            <div class="settings-title">通知设置</div>
          </div>
          <div class="settings-form">
            <div class="form-group">
              <div class="switch-label">
                <span>启用邮件通知</span>
                <label class="toggle-switch">
                  <input type="checkbox" v-model="settings.emailNotification">
                  <span class="toggle-slider"></span>
                </label>
              </div>
            </div>
            <div class="form-group">
              <div class="switch-label">
                <span>启用短信通知</span>
                <label class="toggle-switch">
                  <input type="checkbox" v-model="settings.smsNotification">
                  <span class="toggle-slider"></span>
                </label>
              </div>
            </div>
            <div class="form-group">
              <div class="switch-label">
                <span>价格变动通知</span>
                <label class="toggle-switch">
                  <input type="checkbox" v-model="settings.priceChangeNotification">
                  <span class="toggle-slider"></span>
                </label>
              </div>
            </div>
          </div>
        </div>
        
        <div class="settings-card">
          <div class="settings-header">
            <div class="settings-icon">
              <i class="fas fa-sync-alt"></i>
            </div>
            <div class="settings-title">监控设置</div>
          </div>
          <div class="settings-form">
            <div class="form-group">
              <label>价格监控频率</label>
              <select class="form-control" v-model="settings.monitorFrequency">
                <option value="30">每30分钟</option>
                <option value="60">每小时</option>
                <option value="120">每2小时</option>
                <option value="360">每6小时</option>
              </select>
            </div>
            <div class="form-group">
              <label>价格变动阈值</label>
              <input type="text" class="form-control" v-model="settings.priceThreshold">
            </div>
            <div class="form-group">
              <label>监控平台</label>
              <div>
                <span 
                  class="platform-badge" 
                  v-for="platform in platforms" 
                  :key="platform.id"
                  @click="togglePlatform(platform)"
                  :class="{active: platform.active}"
                >
                  <i :class="platform.icon"></i> {{ platform.name }}
                </span>
              </div>
            </div>
          </div>
        </div>
        
        <div class="settings-card">
          <div class="settings-header">
            <div class="settings-icon">
              <i class="fas fa-tags"></i>
            </div>
            <div class="settings-title">商品类别设置</div>
          </div>
          <div class="settings-form">
            <div class="form-group">
              <label>热门商品类别</label>
              <div class="tag-selector">
                <div 
                  class="tag-item" 
                  v-for="category in categories" 
                  :key="category"
                  :class="{active: selectedCategories.includes(category)}"
                  @click="toggleCategory(category)"
                >
                  {{ category }}
                </div>
              </div>
            </div>
            <div class="form-group">
              <label>添加新类别</label>
              <div class="form-row">
                <input 
                  type="text" 
                  class="form-control" 
                  v-model="newCategory" 
                  placeholder="输入新类别名称"
                  @keyup.enter="addCategory"
                >
                <button class="btn btn-outline" @click="addCategory">添加</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Settings',
  data() {
    return {
      settings: {
        systemName: '智能比价系统',
        timezone: 'UTC+08:00',
        announcement: '系统将于12月25日进行维护升级，预计停机2小时。',
        emailNotification: true,
        smsNotification: false,
        priceChangeNotification: true,
        monitorFrequency: '60',
        priceThreshold: '5%'
      },
      platforms: [
        { id: 1, name: '京东', icon: 'fab fa-jd', active: true },
        { id: 2, name: '天猫', icon: 'fab fa-alipay', active: true },
        { id: 3, name: '拼多多', icon: 'fas fa-shopping-basket', active: false },
        { id: 4, name: '苏宁', icon: 'fas fa-store', active: false }
      ],
      categories: ['手机', '电脑', '平板', '耳机', '手表', '相机', '家电', '服装'],
      selectedCategories: ['手机', '电脑', '平板'],
      newCategory: ''
    }
  },
  methods: {
    togglePlatform(platform) {
      platform.active = !platform.active
    },
    toggleCategory(category) {
      if (this.selectedCategories.includes(category)) {
        this.selectedCategories = this.selectedCategories.filter(c => c !== category)
      } else {
        this.selectedCategories.push(category)
      }
    },
    addCategory() {
      if (this.newCategory && !this.categories.includes(this.newCategory)) {
        this.categories.push(this.newCategory)
        this.newCategory = ''
      }
    }
  }
}
</script>

<style scoped>
.settings-card {
  background: #f9fafc;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
}

.settings-header {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.settings-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: rgba(67, 97, 238, 0.1);
  color: var(--primary);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-size: 1.2rem;
}

.settings-title {
  font-weight: 600;
  font-size: 1.1rem;
}

.settings-form {
  padding-left: 55px;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
}

.form-control {
  width: 100%;
  padding: 10px 15px;
  border: 1px solid var(--light-gray);
  border-radius: 8px;
  font-size: 1rem;
}

.form-control:focus {
  outline: none;
  border-color: var(--primary);
}

.toggle-switch {
  position: relative;
  display: inline-block;
  width: 50px;
  height: 24px;
}

.toggle-switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.toggle-slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  transition: .4s;
  border-radius: 24px;
}

.toggle-slider:before {
  position: absolute;
  content: "";
  height: 16px;
  width: 16px;
  left: 4px;
  bottom: 4px;
  background-color: white;
  transition: .4s;
  border-radius: 50%;
}

input:checked + .toggle-slider {
  background-color: var(--primary);
}

input:checked + .toggle-slider:before {
  transform: translateX(26px);
}

.switch-label {
  display: flex;
  align-items: center;
  gap: 10px;
}

.form-row {
  display: flex;
  gap: 20px;
  margin-bottom: 15px;
}

.form-row .form-group {
  flex: 1;
  margin-bottom: 0;
}

.platform-badge {
  display: inline-flex;
  align-items: center;
  padding: 4px 10px;
  border-radius: 20px;
  background: var(--light);
  font-size: 0.85rem;
  margin-right: 5px;
  cursor: pointer;
}

.platform-badge i {
  margin-right: 5px;
  color: var(--primary);
}

.platform-badge.active {
  background: rgba(67, 97, 238, 0.1);
}

.tag-selector {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 10px;
}

.tag-item {
  padding: 5px 15px;
  background: var(--light);
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.tag-item.active {
  background: var(--primary);
  color: white;
}

.tag-item:hover {
  background: var(--light-gray);
}
</style>