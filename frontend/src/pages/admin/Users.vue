<template>
  <div class="content-page">
    <div class="content-section">
      <div class="section-header">
        <h2 class="section-title">用户管理</h2>
      </div>
      <table class="admin-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>用户名</th>
            <th>邮箱</th>
            <th>角色</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in users" :key="user.id">
            <td>{{ user.id }}</td>
            <td>{{ user.username }}</td>
            <td>{{ user.email }}</td>
            <td>
              <select v-model="user.role" @change="updateUser(user)">
                <option value="user">用户</option>
                <option value="admin">管理员</option>
              </select>
            </td>
            <td>
              <select v-model="user.status" @change="updateUser(user)">
                <option value="active">正常</option>
                <option value="disabled">禁用</option>
              </select>
            </td>
            <td>
              <button class="btn btn-danger btn-sm" @click="deleteUser(user.id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const users = ref([
  { id: 1, username: 'admin', email: 'admin@test.com', role: 'admin', status: 'active' },
  { id: 2, username: 'user1', email: 'user1@test.com', role: 'user', status: 'active' }
])

function updateUser(user) {
  // 这里应调用API更新用户信息
  // 示例：await api.updateUser(user.id, { role: user.role, status: user.status })
}
function deleteUser(id) {
  users.value = users.value.filter(u => u.id !== id)
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
.admin-table {
  width: 100%;
  border-collapse: collapse;
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
.btn-danger {
  background: #f72585;
  color: white;
}
.btn:hover {
  opacity: 0.9;
  transform: translateY(-2px);
}
</style>