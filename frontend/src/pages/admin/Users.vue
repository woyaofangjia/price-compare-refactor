<template>
  <div class="content-page">
    <div class="content-section">
      <div class="section-header">
        <h2 class="section-title">用户管理</h2>
        <div class="header-actions">
          <button class="btn btn-primary" @click="refreshUsers" :disabled="loading">
            <span v-if="loading" class="loading-spinner"></span>
            刷新
          </button>
        </div>
      </div>
      
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container">
        <div class="loading-spinner large"></div>
        <p>正在加载用户数据...</p>
      </div>
      
      <!-- 用户表格 -->
      <div v-else-if="users.length > 0">
        <table class="admin-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>头像</th>
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
              <td>
                <div class="avatar-container">
                  <img 
                    v-if="user.avatar && user.avatar !== ''" 
                    :src="getAvatarUrl(user)"
                    :alt="user.username"
                    class="user-avatar"
                    @error="handleAvatarError"
                  />
                  <div v-else class="avatar-placeholder">
                    {{ user.username ? user.username.charAt(0).toUpperCase() : '?' }}
                  </div>
                </div>
              </td>
              <td>{{ user.username }}</td>
              <td>{{ user.email }}</td>
              <td>
                <select 
                  v-model="user.isadmin" 
                  @change="updateUserRole(user)"
                  :disabled="user.id === currentUserId"
                  class="role-select"
                >
                  <option :value="0">用户</option>
                  <option :value="1">管理员</option>
                </select>
              </td>
              <td>
                <select 
                  v-model="user.status" 
                  @change="updateUserStatus(user)"
                  :disabled="user.id === currentUserId"
                  class="status-select"
                >
                  <option value="active">正常</option>
                  <option value="banned">封禁</option>
                </select>
              </td>
              <td>
                <button 
                  class="btn btn-danger btn-sm" 
                  @click="confirmDeleteUser(user)"
                  :disabled="user.id === currentUserId || user.isadmin"
                >
                  删除
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      
      <!-- 空状态 -->
      <div v-else class="empty-state">
        <p>暂无用户数据</p>
      </div>
    </div>
    
    <!-- 确认删除对话框 -->
    <div v-if="showDeleteModal" class="modal-overlay" @click="closeDeleteModal">
      <div class="modal-content" @click.stop>
        <h3>确认删除</h3>
        <p>您确定要删除用户 "{{ userToDelete?.username }}" 吗？此操作不可撤销。</p>
        <div class="modal-actions">
          <button class="btn btn-secondary" @click="closeDeleteModal">取消</button>
          <button class="btn btn-danger" @click="deleteUser" :disabled="deleting">
            <span v-if="deleting" class="loading-spinner"></span>
            确认删除
          </button>
        </div>
      </div>
    </div>
    
    <!-- 消息提示 -->
    <div v-if="message" class="message-toast" :class="messageType">
      {{ message }}
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAllUsers, updateUser, deleteUser as deleteUserApi } from '../../api/user.js'

const users = ref([])
const loading = ref(false)
const deleting = ref(false)
const showDeleteModal = ref(false)
const userToDelete = ref(null)
const message = ref('')
const messageType = ref('success')
const currentUserId = ref(null)

// 头像基础URL
const AVATAR_BASE_URL = 'http://localhost:3000'
const DEFAULT_AVATAR = 'https://picsum.photos/seed/user/40/40'

// 获取头像显示URL
function getAvatarUrl(user) {
  if (user.avatar) {
    if (user.avatar.startsWith('http')) {
      return user.avatar
    }
    return AVATAR_BASE_URL + user.avatar
  }
  return DEFAULT_AVATAR
}

function handleAvatarError(event) {
  event.target.src = DEFAULT_AVATAR
}

// 获取当前用户ID（从localStorage或用户信息中获取）
onMounted(() => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  currentUserId.value = userInfo.id
  fetchUsers()
})

// 获取用户列表
async function fetchUsers() {
  loading.value = true
  try {
    const response = await getAllUsers()
    users.value = response.data || []
  } catch (error) {
    showMessage('获取用户列表失败', 'error')
    console.error('获取用户列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 刷新用户列表
function refreshUsers() {
  fetchUsers()
}

// 更新用户角色
async function updateUserRole(user) {
  try {
    await updateUser(user.id, { isadmin: user.isadmin })
    showMessage(`用户 ${user.username} 角色更新成功`, 'success')
  } catch (error) {
    showMessage(`更新用户角色失败: ${error.message}`, 'error')
    // 恢复原值
    await fetchUsers()
  }
}

// 更新用户状态
async function updateUserStatus(user) {
  try {
    await updateUser(user.id, { status: user.status })
    showMessage(`用户 ${user.username} 状态更新成功`, 'success')
  } catch (error) {
    showMessage(`更新用户状态失败: ${error.message}`, 'error')
    // 恢复原值
    await fetchUsers()
  }
}

// 确认删除用户
function confirmDeleteUser(user) {
  userToDelete.value = user
  showDeleteModal.value = true
}

// 关闭删除对话框
function closeDeleteModal() {
  showDeleteModal.value = false
  userToDelete.value = null
}

// 删除用户
async function deleteUser() {
  if (!userToDelete.value) return
  
  deleting.value = true
  try {
    await deleteUserApi(userToDelete.value.id)
    showMessage(`用户 ${userToDelete.value.username} 删除成功`, 'success')
    closeDeleteModal()
    await fetchUsers() // 重新获取用户列表
  } catch (error) {
    showMessage(`删除用户失败: ${error.message}`, 'error')
  } finally {
    deleting.value = false
  }
}

// 显示消息提示
function showMessage(text, type = 'success') {
  message.value = text
  messageType.value = type
  setTimeout(() => {
    message.value = ''
  }, 3000)
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

.header-actions {
  display: flex;
  gap: 10px;
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
  vertical-align: middle;
}

.admin-table tr:last-child td {
  border-bottom: none;
}

.admin-table tr:hover {
  background-color: rgba(67, 97, 238, 0.03);
}

.avatar-container {
  display: flex;
  justify-content: center;
  align-items: center;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #e9ecef;
}

.avatar-placeholder {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #4361ee, #f72585);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 16px;
}

.role-select,
.status-select {
  padding: 5px 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background: white;
  cursor: pointer;
}

.role-select:disabled,
.status-select:disabled {
  background: #f5f5f5;
  cursor: not-allowed;
  opacity: 0.6;
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

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none !important;
}

.btn-sm {
  padding: 5px 10px;
  font-size: 0.85rem;
}

.btn-primary {
  background: #4361ee;
  color: white;
}

.btn-secondary {
  background: #6c757d;
  color: white;
}

.btn-danger {
  background: #f72585;
  color: white;
}

.btn:hover:not(:disabled) {
  opacity: 0.9;
  transform: translateY(-2px);
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: #6c757d;
}

.loading-spinner {
  width: 20px;
  height: 20px;
  border: 2px solid #f3f3f3;
  border-top: 2px solid #4361ee;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.loading-spinner.large {
  width: 40px;
  height: 40px;
  border-width: 3px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.empty-state {
  text-align: center;
  padding: 40px;
  color: #6c757d;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 30px;
  border-radius: 10px;
  max-width: 400px;
  width: 90%;
}

.modal-content h3 {
  margin: 0 0 15px 0;
  color: #333;
}

.modal-content p {
  margin: 0 0 20px 0;
  color: #666;
}

.modal-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

.message-toast {
  position: fixed;
  top: 20px;
  right: 20px;
  padding: 15px 20px;
  border-radius: 8px;
  color: white;
  font-weight: 500;
  z-index: 1001;
  animation: slideIn 0.3s ease;
}

.message-toast.success {
  background: #28a745;
}

.message-toast.error {
  background: #dc3545;
}

@keyframes slideIn {
  from {
    transform: translateX(100%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

@media (max-width: 768px) {
  .admin-table {
    font-size: 0.9rem;
  }
  
  .admin-table th,
  .admin-table td {
    padding: 10px 5px;
  }
  
  .section-header {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }
  
  .user-avatar,
  .avatar-placeholder {
    width: 30px;
    height: 30px;
    font-size: 14px;
  }
}
</style>