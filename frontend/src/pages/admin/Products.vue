<template>
   <div class="content-page active">  
    <div class="content-section">
      <div class="section-header">
        <h2 class="section-title">商品管理</h2>
        <div class="section-actions">
          <button class="btn btn-primary" @click="showAddDialog = true">添加商品</button>
        </div>
      </div>
      
      <!-- 添加商品弹窗 -->
      <div v-if="showAddDialog" class="dialog-mask" @click.self="showAddDialog = false">
        <div class="dialog">
          <h3>添加商品</h3>
          <form @submit.prevent="submitAddProduct">
            <div class="form-row">
              <label>商品名称</label>
              <input v-model="addForm.title" required maxlength="100" />
            </div>
            <div class="form-row">
              <label>描述</label>
              <textarea v-model="addForm.desc" maxlength="500" />
            </div>
            <div class="form-row">
              <label>图片URL</label>
              <input v-model="addForm.img" />
            </div>
            <div class="form-actions">
              <button class="btn btn-primary" type="submit">提交</button>
              <button class="btn btn-outline" type="button" @click="showAddDialog = false">取消</button>
            </div>
          </form>
        </div>
      </div>
      
      <!-- 编辑商品弹窗 -->
      <div v-if="showEditDialog" class="dialog-mask" @click.self="showEditDialog = false">
        <div class="dialog">
          <h3>编辑商品</h3>
          <form @submit.prevent="submitEditProduct">
            <div class="form-row">
              <label>商品名称</label>
              <input v-model="editForm.title" required maxlength="100" />
            </div>
            <div class="form-row">
              <label>描述</label>
              <textarea v-model="editForm.desc" maxlength="500" />
            </div>
            <div class="form-row">
              <label>图片URL</label>
              <input v-model="editForm.img" />
            </div>
            <div class="form-row form-row-inline">
              <div class="form-group">
                <label>分类</label>
                <input v-model="editForm.category" />
              </div>
              <div class="form-group">
                <label>品牌</label>
                <input v-model="editForm.brand" />
              </div>
            </div>
            <div class="form-row form-row-inline">
              <div class="form-group">
                <label>是否热门</label>
                <select v-model.number="editForm.is_hot">
                  <option :value="1">是</option>
                  <option :value="0">否</option>
                </select>
              </div>
              <div class="form-group">
                <label>是否降价</label>
                <select v-model.number="editForm.is_drop">
                  <option :value="1">是</option>
                  <option :value="0">否</option>
                </select>
              </div>
            </div>
            <div class="form-row form-row-inline">
              <div class="form-group">
                <label>平台</label>
                <input v-model="editForm.platform" placeholder="如 京东/天猫/拼多多/苏宁" />
              </div>
              <div class="form-group">
                <label>价格</label>
                <input v-model.number="editForm.price" type="number" min="0" step="0.01" />
              </div>
            </div>
            <div class="form-actions">
              <button class="btn btn-primary" type="submit">保存</button>
              <button class="btn btn-outline" type="button" @click="showEditDialog = false">取消</button>
            </div>
          </form>
        </div>
      </div>
      
      <table class="admin-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>商品名称</th>
            <th>当前价格</th>
            <th>平台</th>
            <th>收藏次数</th>
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
                :style="{ background: platformColors[platform] || '#eee', color: '#fff' }"
              >
                {{ platformNames[platform] || platform }}
              </span>
            </td>
            <td>{{ product.favoriteCount }}</td>
            <td>
              <span :class="['status-badge', product.status == 1 ? 'status-active' : 'status-banned']">
                {{ product.status == 1 ? '已上架' : '已下架' }}
              </span>
            </td>
            <td class="user-actions">
              <button class="btn btn-sm btn-outline" @click="editProduct(product)">编辑</button>
              <button 
                v-if="product.status == 1"
                class="btn btn-sm btn-danger"
                @click="toggleProductStatus(product)">下架</button>
              <button 
                v-else
                class="btn btn-sm btn-primary"
                @click="toggleProductStatus(product)">上架</button>
              <button class="btn btn-sm btn-danger" @click="deleteProduct(product)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
      
      <div class="pagination">
        <div 
          class="page-item" 
          v-for="page in totalPages" 
          :key="page" 
          :class="{active: currentPage === page}"
          @click="handlePageChange(page)"
        >
          {{ page }}
        </div>
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
      pageSize: 10,
      total: 0,
      products: [],
      loading: false,
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
      },
      platformColors: {
        jd: '#e74c3c', // 京东
        tmall: '#3498db', // 天猫
        suning: '#f39c12', // 苏宁
        pdd: '#9b59b6', // 拼多多
        taobao: '#1abc9c',
        dangdang: '#34495e'
      },
      showAddDialog: false,
      addForm: {
        title: '', desc: '', img: ''
      },
      showEditDialog: false,
      editForm: {
        id: '', title: '', desc: '', img: '', category: '', brand: '', is_hot: 0, is_drop: 0, platform: '', price: ''
      },
    }
  },
  computed: {
    filteredProducts() {
      if (!this.searchTerm) return this.products
      return this.products.filter(product => 
        product.name && product.name.toLowerCase().includes(this.searchTerm.toLowerCase())
      )
    },
    totalPages() {
      return Math.ceil(this.total / this.pageSize) || 1
    }
  },
  async mounted() {
    await this.loadProducts(this.currentPage)
  },
  methods: {
    async loadProducts(page = 1) {
      this.loading = true
      try {
        const response = await fetch(`/api/products?page=${page}&pageSize=${this.pageSize}`, {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          }
        })
        const result = await response.json()
        if (result.code === 0) {
          this.products = result.data.list.map(product => ({
            id: product.id,
            name: product.title,
            price: product.price ? `¥${product.price}` : '暂无',
            platforms: product.platform ? [this.mapPlatform(product.platform)] : [],
            favoriteCount: product.favorite_count || 0,
            status: product.status || 'active'
          }))
          this.total = result.data.total
          this.currentPage = result.data.page
        } else {
          console.error('获取商品列表失败:', result.message)
        }
      } catch (error) {
        console.error('获取商品列表失败:', error)
      } finally {
        this.loading = false
      }
    },
    mapPlatform(platformStr) {
      if (!platformStr) return '-'
      if (platformStr.includes('京东')) return 'jd'
      if (platformStr.includes('天猫')) return 'tmall'
      if (platformStr.includes('拼多多')) return 'pdd'
      if (platformStr.includes('苏宁')) return 'suning'
      return platformStr // fallback
    },
    async toggleProductStatus(product) {
      const newStatus = product.status == 1 ? 0 : 1
      try {
        const response = await fetch(`/api/products/${product.id}/status`, {
          method: 'PATCH',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          },
          body: JSON.stringify({ status: newStatus })
        })
        const result = await response.json()
        if (result.code === 0) {
          this.loadProducts(this.currentPage)
        } else {
          alert('操作失败: ' + result.message)
        }
      } catch (error) {
        alert('操作失败: ' + error)
      }
    },
    async deleteProduct(product) {
      if (!confirm('确定要删除该商品吗？此操作不可恢复！')) return
      try {
        const response = await fetch(`/api/products/${product.id}`, {
          method: 'DELETE',
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          }
        })
        const result = await response.json()
        if (result.code === 0) {
          this.loadProducts(this.currentPage)
        } else {
          alert('删除失败: ' + result.message)
        }
      } catch (error) {
        alert('删除失败: ' + error)
      }
    },
    async editProduct(product) {
      try {
        // 获取商品的详细信息
        const response = await fetch(`/api/products/${product.id}`, {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          }
        })
        const result = await response.json()
        
        if (result.code === 0) {
          // 使用标准格式的响应数据
          const productDetail = result.data
          console.log('获取到的商品详情:', productDetail)
          
          this.editForm = {
            id: productDetail.id,
            title: productDetail.title || productDetail.name || '',
            desc: productDetail.desc || '',
            img: productDetail.img || productDetail.image || '',
            category: productDetail.category || '',
            brand: productDetail.brand || '',
            is_hot: productDetail.is_hot || 0,
            is_drop: productDetail.is_drop || 0,
            platform: '',
            price: ''
          }
          
          // 如果有当前价格，也显示在价格字段中
          if (productDetail.current_price || productDetail.price) {
            this.editForm.price = productDetail.current_price || productDetail.price
          }
          
          this.showEditDialog = true
        } else {
          console.error('API返回错误:', result)
          alert('获取商品详情失败: ' + (result.message || '未知错误'))
        }
      } catch (error) {
        console.error('获取商品详情失败:', error)
        alert('获取商品详情失败: ' + error.message)
      }
    },
    handlePageChange(page) {
      if (page !== this.currentPage && page > 0 && page <= this.totalPages) {
        this.loadProducts(page)
      }
    },
    async submitAddProduct() {
      try {
        const response = await fetch('/api/products', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          },
          body: JSON.stringify(this.addForm)
        })
        const result = await response.json()
        if (result.code === 0) {
          this.showAddDialog = false
          this.addForm = { title: '', desc: '', img: '' }
          this.loadProducts(1)
        } else {
          alert('添加失败: ' + result.message)
        }
      } catch (error) {
        alert('添加失败: ' + error)
      }
    },
    async submitEditProduct() {
      try {
        const { id, platform, price, ...data } = this.editForm
        const response = await fetch(`/api/products/${id}`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          },
          body: JSON.stringify(data)
        })
        const result = await response.json()
        if (result.code === 0) {
          if (platform && price) {
            await fetch('/api/products/product-prices', {
              method: 'POST',
              headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('token')}`
              },
              body: JSON.stringify({ product_id: id, platform, price })
            })
          }
          this.showEditDialog = false
          this.loadProducts(this.currentPage)
        } else {
          alert('编辑失败: ' + result.message)
        }
      } catch (error) {
        alert('编辑失败: ' + error)
      }
    },
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
  font-size: 0.85rem;
  margin-right: 5px;
  font-weight: 600;
  border: none;
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
  
  .form-row-inline {
    flex-direction: column;
    gap: 8px;
  }
  
  .dialog {
    margin: 20px;
    max-width: calc(100vw - 40px);
  }
}

.dialog-mask {
  position: fixed;
  left: 0; top: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.25);
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
}
.dialog {
  background: #fff;
  border-radius: 12px;
  padding: 32px 28px 20px 28px;
  min-width: 340px;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 8px 32px rgba(0,0,0,0.12);
  position: relative;
}
.dialog h3 {
  margin-top: 0;
  margin-bottom: 18px;
  font-size: 1.2rem;
  font-weight: 600;
  color: var(--primary);
}
.form-row {
  margin-bottom: 14px;
  display: flex;
  flex-direction: column;
}
.form-row-inline {
  flex-direction: row;
  gap: 12px;
}
.form-group {
  flex: 1;
  display: flex;
  flex-direction: column;
}
.form-row label {
  font-size: 0.98rem;
  margin-bottom: 4px;
  color: #333;
}
.form-row input, .form-row textarea, .form-row select {
  padding: 7px 10px;
  border: 1px solid #e0e0e0;
  border-radius: 5px;
  font-size: 1rem;
  outline: none;
}
.form-row textarea {
  min-height: 60px;
  resize: vertical;
}
.form-actions {
  display: flex;
  gap: 12px;
  margin-top: 10px;
  justify-content: flex-end;
}
</style>