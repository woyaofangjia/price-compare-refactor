    <template>
      <div class="main-container">
        <h1>个人中心</h1>
        <div class="user-info">
          <div class="avatar">
            <img src="https://picsum.photos/seed/user/100/100" alt="用户头像">
          </div>
          <div class="info-items">
            <div class="info-item">
              <span class="label">用户名:</span>
              <span class="value">{{ user.username }}</span>
            </div>
            <div class="info-item">
              <span class="label">邮箱:</span>
              <span class="value">{{ user.email }}</span>
            </div>
            <div class="info-item">
              <span class="label">注册日期:</span>
              <span class="value">{{ user.registerDate }}</span>
            </div>
          </div>
        </div>
        <div class="user-actions">
          <router-link to="/profile/edit" class="edit-btn">修改信息</router-link>
          <button @click="logout" class="logout-btn">退出登录</button>
        </div>
        <nav class="main-nav">
          <router-link to="/" class="nav-link">首页</router-link>
          <router-link to="/profile" class="nav-link">个人中心</router-link>
          <button @click="logout" class="logout-btn">退出</button>
        </nav>
      </div>
    </template>

    <script>
    export default {
      name: 'Profile',
      data() {
        return {
          user: {
            username: 'testuser',
            email: 'test@example.com',
            registerDate: '2023-01-01'
          }
        }
      },
      methods: {
        logout() {
          localStorage.removeItem('token')
          localStorage.removeItem('user')
          // 触发自定义事件通知导航栏更新状态
          window.dispatchEvent(new Event('loginStatusChanged'))
          this.$router.push({ name: 'Login' })
        }
      }
    }
    </script>

    <style scoped>
    .main-container {
      min-height: 100vh;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      padding: 20px 0;
    }

    h1 {
      color: #1e293b;
      font-size: 2rem;
      font-weight: 700;
      margin-bottom: 24px;
      text-align: center;
    }

    .user-info {
      background: #fff;
      border-radius: 16px;
      box-shadow: 0 4px 24px 0 rgba(34, 51, 84, 0.12);
      padding: 30px;
      width: 100%;
      max-width: 500px;
      margin-bottom: 24px;
      display: flex;
      align-items: center;
      gap: 30px;
    }

    .avatar img {
      width: 80px;
      height: 80px;
      border-radius: 50%;
      object-fit: cover;
      border: 4px solid #fff;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    }

    .info-items {
      flex: 1;
    }

    .info-item {
      display: flex;
      margin-bottom: 16px;
      align-items: center;
    }

    .info-item:last-child {
      margin-bottom: 0;
    }

    .label {
      width: 100px;
      color: #64748b;
      font-size: 0.95rem;
    }

    .value {
      color: #1e293b;
      font-weight: 500;
    }

    .user-actions {
      display: flex;
      gap: 16px;
      margin-bottom: 24px;
      width: 100%;
      max-width: 500px;
    }

    .edit-btn, .logout-btn {
      flex: 1;
      padding: 12px;
      border-radius: 8px;
      font-size: 1rem;
      font-weight: 600;
      text-align: center;
      cursor: pointer;
      transition: all 0.3s ease;
    }

    .edit-btn {
      background-color: #2196F3;
      color: white;
    }

    .logout-btn {
      background-color: #f44336;
      color: white;
      border: none;
    }

    .main-nav {
      margin-top: 30px;
      display: flex;
      justify-content: center;
      gap: 20px;
    }

    .nav-link {
      text-decoration: none;
      color: #2c3e50;
      font-weight: bold;
      padding: 5px 10px;
      border-radius: 4px;
    }

    .nav-link:hover {
      background-color: #f0f0f0;
    }

    @media (prefers-color-scheme: dark) {
      body { color: white; }
    }
    </style>
  