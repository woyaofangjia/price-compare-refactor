<template>
   <div class="content-page active">  
    <div class="content-section">
      <div class="section-header">
        <h2 class="section-title">商品管理</h2>
        <div class="section-actions">
          <button class="btn btn-primary">添加商品</button>
        </div>
      </div>
      
      <table class="admin-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>商品名称</th>
            <th>当前价格</th>
            <th>平台</th>
            <th>监控次数</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="product in filteredProducts" :key="product.id">
            <td>{{ product.id }}</td>
            <td>{{ product.name }}</td>
            <td>{{ product.price }}</td>
            <td>
              <span 
                class="platform-badge" 
                v-for="platform in product.platforms" 
                :key="platform"
              >
                <i :class="platformIcons[platform]"></i> {{ platformNames[platform] }}
              </span>
            </td>
            <td>{{ product.watchCount }}</td>
            <td>
              <span :class="['status-badge', `status-${product.status}`]">
                {{ product.status === 'active' ? '监控中' : '已下架' }}
              </span>
            </td>
            <td class="user-actions">
              <button class="btn btn-sm btn-outline">编辑</button>
              <button 
                class="btn btn-sm" 
                :class="product.status === 'banned' ? 'btn-primary' : 'btn-danger'"
                @click="toggleProductStatus(product)"
              >
                {{ product.status === 'banned' ? '上架' : '删除' }}
              </button>
            </td>
          </tr>
        </tbody>
      </table>
      
      <div class="pagination">
        <div 
          class="page-item" 
          v-for="page in 3" 
          :key="page" 
          :class="{active: currentPage === page}"
          @click="currentPage = page"
        >
          {{ page }}
        </div>
        <div class="page-item">...</div>
        <div class="page-item">8</div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Products',
  data() {
    return {
      searchTerm: '',
      currentPage: 1,
      products: [
        { 
          id: '#P2001', 
          name: '华为手机 8GB+256GB', 
          price: '¥3,299', 
          platforms: ['jd', 'tmall'],
          watchCount: '1,245',
          status: 'active'
        },
        { 
          id: '#P2002', 
          name: '联想笔记本电脑 i7', 
          price: '¥6,499', 
          platforms: ['jd', 'pdd'],
          watchCount: '892',
          status: 'active'
        },
        { 
          id: '#P2003', 
          name: '小米智能手环', 
          price: '¥899', 
          platforms: ['tmall', 'suning'],
          watchCount: '578',
          status: 'active'
        },
        { 
          id: '#P2004', 
          name: '苹果智能手表', 
          price: '¥1,299', 
          platforms: ['jd', 'tmall'],
          watchCount: '1,023',
          status: 'banned'
        }
      ],
      platformIcons: {
        jd: 'fab fa-jd',
        tmall: 'fab fa-alipay',
        pdd: 'fas fa-shopping-basket',
        suning: 'fas fa-store'
      },
      platformNames: {
        jd: '京东',
        tmall: '天猫',
        pdd: '拼多多',
        suning: '苏宁'
      }
    }
  },
  computed: {
    filteredProducts() {
      if (!this.searchTerm) return this.products
      return this.products.filter(product => 
        product.name.toLowerCase().includes(this.searchTerm.toLowerCase())
      )
    }
  },
  methods: {
    toggleProductStatus(product) {
      product.status = product.status === 'active' ? 'banned' : 'active'
    }
  }
}
</script>

<style scoped>
:root {
  --primary: #4361ee;
  --secondary: #3f37c9;
  --success: #4cc9f0;
  --light: #f8f9fa;
  --dark: #212529;
  --warning: #f72585;
  --gray: #6c757d;
  --light-gray: #e9ecef;
}

.content-page {
  display: block; /* 修改为block确保显示 */
}

/* 修正所有缺少闭合括号的样式 */
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

/* 确保所有样式属性值都有正确的闭合括号 */
.btn-primary {
  background: var(--primary);
  color: white;
}

.content-page {
  display: none;
}

.content-page.active {
  display: block;
  animation: fadeIn 0.4s ease;
}

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

.search-box {
  display: flex;
  align-items: center;
  background: var(--light);
  border-radius: 6px;
  padding: 5px 10px;
  margin-right: 10px;
}

.search-box input {
  border: none;
  background: transparent;
  padding: 8px;
  outline: none;
  width: 200px;
}

.search-box button {
  background: transparent;
  border: none;
  cursor: pointer;
  color: var(--gray);
  padding: 5px;
}

.admin-table {
  width: 100%;
  border-collapse: collapse;
}

.admin-table th {
  background-color: var(--light);
  text-align: left;
  padding: 15px;
  font-weight: 600;
  color: var(--gray);
  border-bottom: 1px solid var(--light-gray);
}

.admin-table td {
  padding: 15px;
  border-bottom: 1px solid var(--light-gray);
}

.admin-table tr:last-child td {
  border-bottom: none;
}

.admin-table tr:hover {
  background-color: rgba(67, 97, 238, 0.03);
}

.status-badge {
  padding: 5px 10px;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 500;
}

.status-active {
  background-color: rgba(76, 201, 240, 0.1);
  color: #4cc9f0;
}

.status-banned {
  background-color: rgba(247, 37, 133, 0.1);
  color: #f72585;
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

.btn-sm {
  padding: 5px 10px;
  font-size: 0.85rem;
}

.btn-primary {
  background: var(--primary);
  color: white;
}

.btn-danger {
  background: var(--warning);
  color: white;
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

.user-actions {
  display: flex;
  gap: 8px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 25px;
  gap: 10px;
}

.page-item {
  width: 35px;
  height: 35px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  background: var(--light);
  cursor: pointer;
  transition: all 0.3s ease;
}

.page-item:hover, .page-item.active {
  background: var(--primary);
  color: white;
}

.platform-badge {
  display: inline-flex;
  align-items: center;
  padding: 4px 10px;
  border-radius: 20px;
  background: var(--light);
  font-size: 0.85rem;
  margin-right: 5px;
}

.platform-badge i {
  margin-right: 5px;
  color: var(--primary);
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
@media (max-width: 768px) {
  .search-box input {
    width: 150px;
  }
  
  .section-actions {
    flex-wrap: wrap;
  }
}
</style>