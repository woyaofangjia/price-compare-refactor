<template>
  <section class="page-content">
    <div class="container">
      <h2 class="section-title">我的收藏夹</h2>
      <div class="favorites-container">
        <div class="favorites-grid">
          <div class="favorite-item" v-for="item in favorites" :key="item.id" @click="goToProduct(item.productId)" style="cursor:pointer;">
            <div class="favorite-image">
              <img :src="item.img || item.image || defaultImg" :alt="item.title" @error="onImgError" />
            </div>
            <div class="favorite-info">
              <h3>{{ item.title }}</h3>
              <div class="favorite-price">{{ item.price }}</div>
              <div :class="['price-change', item.priceChange > 0 ? 'price-up' : 'price-down']">
                {{ item.priceChange > 0 ? '涨' : '降' }} {{ Math.abs(item.priceChange) }}
              </div>
              <div class="alert-setting">
                <span>提醒价：</span>
                <input 
                  type="number" 
                  v-model="item.alertPrice" 
                  style="width: 100px;" 
                  @click.stop 
                  @blur="saveAlertPrice(item.id, item.alertPrice)"
                  @keyup.enter="saveAlertPrice(item.id, item.alertPrice)"
                  step="0.01"
                  min="0"
                  placeholder="设置提醒价"
                />
                <span>元时通知我</span>
                <div class="save-status" v-if="item.saveStatus">
                  <i :class="item.saveStatus === 'saving' ? 'fas fa-spinner fa-spin' : 
                           item.saveStatus === 'success' ? 'fas fa-check' : 'fas fa-times'"></i>
                  {{ item.saveStatus === 'saving' ? '保存中...' : 
                     item.saveStatus === 'success' ? '已保存' : '保存失败' }}
                </div>
              </div>
              <button class="btn btn-outline" @click.stop="removeFromFavorites(item.id)">取消收藏</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const favorites = ref([])
const router = useRouter()
const user = JSON.parse(localStorage.getItem('user') || '{}')
const userId = user && user.id
const defaultImg = '/default-product.png'
function onImgError(e) {
  e.target.src = defaultImg
}

async function fetchFavorites() {
  try {
    if (!userId) {
      router.push('/login')
      return
    }
    const res = await fetch(`/api/favorites?userId=${userId}`)
    if (res.ok) {
      const response = await res.json()
      console.log('收藏数据原始响应:', response)
      
      // 处理不同的响应格式
      let data
      if (response.data) {
        // 标准格式：{code: 0, data: [...]}
        data = response.data
      } else if (Array.isArray(response)) {
        // 直接数组格式
        data = response
      } else {
        console.error('未知的响应格式:', response)
        return
      }
      
      console.log('收藏数据原始结构:', data)
      favorites.value = data.map(item => {
        console.log('单个收藏项:', item)
        return {
          ...item,
          saveStatus: null
        }
      })
      console.log('处理后的收藏数据:', favorites.value)
    } else {
      console.error('获取收藏失败')
    }
  } catch (error) {
    console.error('获取收藏失败:', error)
  }
}

onMounted(fetchFavorites)

function goToProduct(productId) {
  console.log('跳转商品id:', productId)
  if (!productId) return
  router.push(`/product/${productId}`)
}

async function removeFromFavorites(favoriteId) {
  if (!favoriteId) {
    alert('参数有误')
    return
  }
  const res = await fetch(`/api/favorites/${favoriteId}?userId=${userId}`, { 
    method: 'DELETE'
  })
  if (res.ok) {
    await fetchFavorites()
    alert('已取消收藏')
  } else {
    const data = await res.json().catch(() => ({}))
    alert(data.message || '取消收藏失败')
  }
}

async function saveAlertPrice(favoriteId, alertPrice) {
  console.log('=== saveAlertPrice 调试信息 ===')
  console.log('传入的favoriteId:', favoriteId)
  console.log('传入的alertPrice:', alertPrice)
  
  if (!favoriteId) {
    console.error('收藏ID不存在')
    return
  }

  // 找到对应的收藏项
  const favorite = favorites.value.find(item => item.id === favoriteId)
  console.log('找到的收藏项:', favorite)
  if (!favorite) {
    console.error('找不到对应的收藏项')
    return
  }

  // 设置保存状态为保存中
  favorite.saveStatus = 'saving'

  try {
    // 验证价格
    if (alertPrice && parseFloat(alertPrice) <= 0) {
      throw new Error('提醒价格必须大于0')
    }
    
    console.log('请求URL:', `/api/favorites/${favoriteId}/alert?userId=${userId}`)
    console.log('请求体:', { alertPrice: alertPrice ? parseFloat(alertPrice) : null })
    
    const res = await fetch(`/api/favorites/${favoriteId}/alert?userId=${userId}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ alertPrice: alertPrice ? parseFloat(alertPrice) : null })
    })
    
    console.log('提醒价格API响应状态:', res.status, res.statusText)
    
    if (res.ok) {
      const successData = await res.json()
      console.log('提醒价格设置成功，响应:', successData)
      favorite.saveStatus = 'success'
      setTimeout(() => {
        favorite.saveStatus = null
      }, 3000)
    } else {
      const data = await res.json().catch(() => ({}))
      console.error('设置提醒失败，响应:', data)
      throw new Error(data.message || '保存失败')
    }
  } catch (error) {
    console.error('保存提醒价格失败:', error)
    favorite.saveStatus = 'error'
    setTimeout(() => {
      favorite.saveStatus = null
    }, 3000)
  }
}
</script>

<style scoped>
.page-content {
  padding: 30px 0;
}
.section-title {
  font-size: 1.8rem;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid var(--light-gray);
  color: var(--primary);
}
.favorites-container {
  background: white;
  border-radius: 15px;
  padding: 25px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.05);
}
.favorites-grid {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-top: 20px;
}
.favorite-item {
  display: flex;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid var(--light-gray);
  transition: all 0.3s ease;
  border-radius: 8px;
  margin-bottom: 10px;
}
.favorite-item:hover {
  background-color: var(--light);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}
.favorite-image {
  width: 100px;
  height: 100px;
  background: var(--light);
  border-radius: 8px;
  margin-right: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.favorite-image img {
  max-width: 90%;
  max-height: 90%;
  object-fit: contain;
}
.favorite-info {
  flex: 1;
  min-width: 0;
}
.favorite-price {
  color: var(--warning);
  font-weight: bold;
  font-size: 1.2rem;
  margin: 5px 0;
}
.price-change {
  font-size: 0.9rem;
  padding: 3px 8px;
  border-radius: 10px;
  display: inline-block;
}
.price-up {
  background-color: rgba(255, 0, 0, 0.1);
  color: #e74c3c;
}
.price-down {
  background-color: rgba(0, 200, 83, 0.1);
  color: #00c853;
}
.alert-setting {
  display: flex;
  align-items: center;
  margin-top: 10px;
  flex-wrap: wrap;
  gap: 5px;
}
.alert-setting input {
  width: 100px;
  padding: 5px 10px;
  border: 1px solid var(--light-gray);
  border-radius: 5px;
  margin: 0 10px;
}
.alert-setting input:focus {
  outline: none;
  border-color: var(--primary);
}
.save-status {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 0.8rem;
  margin-left: 10px;
}
.save-status i {
  font-size: 0.7rem;
}
.save-status:has(.fa-spinner) {
  color: var(--primary);
}
.save-status:has(.fa-check) {
  color: var(--success);
}
.save-status:has(.fa-times) {
  color: var(--danger);
}
.btn {
  margin-top: 10px;
}
@media (max-width: 1200px) {
  .favorites-grid {
    gap: 10px;
  }
}
@media (max-width: 700px) {
  .favorites-grid {
    gap: 5px;
  }
  .favorite-item {
    flex-direction: column;
    align-items: flex-start;
  }
  .favorite-image {
    margin-bottom: 10px;
    margin-right: 0;
  }
}
</style>