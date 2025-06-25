<template>
  <div class="content-page">
    <div class="content-section">
      <div class="section-header">
        <h2 class="section-title">品牌管理</h2>
        <div class="section-actions">
          <button class="btn btn-primary" @click="showAddDialog = true">新增品牌</button>
        </div>
      </div>
      <table class="admin-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>品牌名</th>
            <th>Logo</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="brand in brands" :key="brand.id">
            <td>{{ brand.id }}</td>
            <td>{{ brand.name }}</td>
            <td>
              <img :src="brand.logo" alt="logo" style="height:32px;" />
            </td>
            <td>
              <button class="btn btn-primary btn-sm" @click="editBrand(brand)">编辑</button>
              <button class="btn btn-danger btn-sm" @click="deleteBrand(brand.id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
      <!-- 新增/编辑品牌弹窗 -->
      <div v-if="showAddDialog || editingBrand" class="dialog-mask" @click.self="closeDialog">
        <div class="dialog">
          <h3>{{ editingBrand ? '编辑品牌' : '新增品牌' }}</h3>
          <input v-model="brandForm.name" placeholder="品牌名" />
          <input v-model="brandForm.logo" placeholder="Logo URL" />
          <div>
            <button class="btn btn-primary" @click="saveBrand">保存</button>
            <button class="btn btn-outline" @click="closeDialog">取消</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const brands = ref([
  { id: 1, name: 'Apple', logo: 'https://logo.clearbit.com/apple.com' },
  { id: 2, name: 'Sony', logo: 'https://logo.clearbit.com/sony.com' }
])
const showAddDialog = ref(false)
const editingBrand = ref(null)
const brandForm = ref({ name: '', logo: '' })

function editBrand(brand) {
  editingBrand.value = brand
  brandForm.value = { ...brand }
}
function saveBrand() {
  if (editingBrand.value) {
    Object.assign(editingBrand.value, brandForm.value)
  } else {
    brands.value.push({
      id: Date.now(),
      ...brandForm.value
    })
  }
  closeDialog()
}
function deleteBrand(id) {
  brands.value = brands.value.filter(b => b.id !== id)
}
function closeDialog() {
  showAddDialog.value = false
  editingBrand.value = null
  brandForm.value = { name: '', logo: '' }
}
</script>

<style scoped>
.content-page {
  display: block;
  padding: 20px;
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
  border-bottom: 1px solid #e9ecef;
}
.section-title {
  font-size: 1.5rem;
  color: #4361ee;
  font-weight: 600;
}
.section-actions {
  display: flex;
  gap: 10px;
}
.admin-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 16px;
}
.admin-table th {
  background-color: #f8f9fa;
  text-align: left;
  padding: 15px;
  font-weight: 600;
  color: #6c757d;
  border-bottom: 1px solid #e9ecef;
}
.admin-table td {
  padding: 15px;
  border-bottom: 1px solid #e9ecef;
  text-align: center;
}
.admin-table tr:last-child td {
  border-bottom: none;
}
.admin-table tr:hover {
  background-color: rgba(67, 97, 238, 0.03);
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
  background: #4361ee;
  color: white;
}
.btn-danger {
  background: #f72585;
  color: white;
}
.btn-outline {
  background: transparent;
  border: 1px solid #4361ee;
  color: #4361ee;
}
.btn:hover {
  opacity: 0.9;
  transform: translateY(-2px);
}
.dialog-mask { position: fixed; left:0;top:0;right:0;bottom:0; background:rgba(0,0,0,0.1); display:flex;align-items:center;justify-content:center; }
.dialog { background:#fff; padding:24px; border-radius:8px; min-width:300px; }
</style>