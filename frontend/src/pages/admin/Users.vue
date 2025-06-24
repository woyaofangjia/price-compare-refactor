<template>
  <div class="content-page">
    <div class="content-section">
      <div class="section-header">
        <h2 class="section-title">用户管理</h2>
        <div class="section-actions">
          <div class="search-box">
            <input type="text" v-model="searchTerm" placeholder="搜索用户...">
            <button><i class="fas fa-search"></i></button>
          </div>
          <button class="btn btn-primary">添加用户</button>
        </div>
      </div>
      
      <table class="admin-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>用户名</th>
            <th>邮箱</th>
            <th>注册时间</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in filteredUsers" :key="user.id">
            <td>{{ user.id }}</td>
            <td>{{ user.username }}</td>
            <td>{{ user.email }}</td>
            <td>{{ user.registerTime }}</td>
            <td>
              <span :class="['status-badge', `status-${user.status}`]">
                {{ statusText[user.status] }}
              </span>
            </td>
            <td class="user-actions">
              <button class="btn btn-sm btn-outline" @click="editUser(user)">编辑</button>
              <button 
                class="btn btn-sm" 
                :class="user.status === 'banned' ? 'btn-primary' : 'btn-danger'"
                @click="toggleUserStatus(user)"
              >
                {{ user.status === 'banned' ? '解封' : '封禁' }}
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
        <div class="page-item">10</div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Users',
  data() {
    return {
      searchTerm: '',
      currentPage: 1,
      users: [
        { id: '#1001', username: '@tech_lover', email: 'tech@example.com', registerTime: '2023-12-15', status: 'active' },
        { id: '#1002', username: '@gadget_guru', email: 'gadget@example.com', registerTime: '2024-01-10', status: 'active' },
        { id: '#1003', username: '@shopper123', email: 'shopper@example.com', registerTime: '2024-02-22', status: 'banned' },
        { id: '#1004', username: '@bargain_hunter', email: 'hunter@example.com', registerTime: '2024-03-05', status: 'active' },
        { id: '#1005', username: '@price_watcher', email: 'watcher@example.com', registerTime: '2024-04-18', status: 'pending' }
      ],
      statusText: {
        active: '正常',
        banned: '已封禁',
        pending: '待审核'
      }
    }
  },
  computed: {
    filteredUsers() {
      if (!this.searchTerm) return this.users
      return this.users.filter(user => 
        user.username.toLowerCase().includes(this.searchTerm.toLowerCase()) || 
        user.email.toLowerCase().includes(this.searchTerm.toLowerCase())
      )
    }
  },
  methods: {
    editUser(user) {
      console.log('编辑用户:', user)
    },
    toggleUserStatus(user) {
      if (user.status === 'banned') {
        user.status = 'active'
      } else if (user.status === 'active') {
        user.status = 'banned'
      } else if (user.status === 'pending') {
        user.status = 'active'
      }
    }
  }
}
</script>

<style scoped>
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

.status-pending {
  background-color: rgba(108, 117, 125, 0.1);
  color: var(--gray);
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
</style>