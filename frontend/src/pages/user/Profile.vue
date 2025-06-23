    <template>
      <div class="profile-container">
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
        <div class="my-favorites">
          <h2>我的收藏</h2>
          <div v-if="favorites.length === 0" class="empty-favorites">暂无收藏商品</div>
          <div v-else class="favorites-list">
            <div v-for="product in favorites" :key="product.id" class="favorite-item">
              <img :src="product.image" :alt="product.name">
              <div class="product-info">
                <h3>{{ product.name }}</h3>
                <div class="price">{{ product.lowestPrice }} 元</div>
              </div>
              <button class="remove-btn" @click="removeFavorite(product.id)">移除</button>
            </div>
          </div>
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
          },
          favorites: [
            {
              id: 1,
              name: 'iPhone 13',
              image: 'https://picsum.photos/seed/iphone13/100/100',
              lowestPrice: 5799
            },
            {
              id: 2,
              name: '华为 Mate 50',
              image: 'https://picsum.photos/seed/mate50/100/100',
              lowestPrice: 4799
            }
          ]
        }
      },
      methods: {
        logout() {
          localStorage.removeItem('token')
          this.$router.push({ name: 'Login' })
        },
        removeFavorite(productId) {
          // 从收藏中移除商品
          this.favorites = this.favorites.filter(product => product.id !== productId)
        }
      }
    }
    </script>

    <style scoped>
    .profile-container {
      min-height: 100vh;
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 20px;
      background: #f8fafc;
      max-width: 600px;
      width: 80%;
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
      width: 80%;
      max-width: 600px;
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
      max-width: 600px;
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

    .my-favorites {
      margin-top: 30px;
    }

    .favorites-list {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
      gap: 20px;
      margin-top: 20px;
    }

    .favorite-item {
      display: flex;
      border: 1px solid #ddd;
      border-radius: 8px;
      padding: 10px;
    }

    .favorite-item img {
      width: 80px;
      height: 80px;
      object-fit: contain;
      margin-right: 10px;
    }

    .product-info {
      flex: 1;
      text-align: left;
    }

    .product-info h3 {
      margin: 0;
      font-size: 16px;
    }

    .price {
      color: #f44336;
      font-weight: bold;
    }

    .remove-btn {
      padding: 5px 10px;
      background-color: #f44336;
      color: white;
      border: none;
      border-radius: 4px;
      cursor: pointer;
    }

    .empty-favorites {
      padding: 20px;
      text-align: center;
      color: #666;
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
    </style>
  