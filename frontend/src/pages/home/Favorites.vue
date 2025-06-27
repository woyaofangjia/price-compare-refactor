<template>
  <section class="page-content">
    <div class="container">
      <h2 class="section-title">我的收藏夹</h2>
      <div class="favorites-container">
        <div class="favorites-grid">
          <div class="favorite-item" v-for="item in favorites" :key="item.id">
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
                <input type="number" v-model="item.alertPrice" style="width: 100px;" @click.stop />
                <span>元时通知我</span>
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
  console.log('favorites:', favorites.value)
  if (!userId) {
    router.push('/login')
    return
  }
  const res = await fetch(`/api/favorites?userId=${userId}`)
  favorites.value = await res.json()
}

onMounted(fetchFavorites)

function goToProduct(id) {
  router.push(`/product/${id}`)
}

async function removeFromFavorites(favoriteId) {
  if (!favoriteId) {
    alert('参数有误')
    return
  }
  const res = await fetch(`/api/favorites/${favoriteId}`, { method: 'DELETE' })
  if (res.ok) {
    await fetchFavorites()
    alert('已取消收藏')
  } else {
    const data = await res.json()
    alert(data.message || '取消收藏失败')
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
}
.alert-setting input {
  width: 100px;
  padding: 5px 10px;
  border: 1px solid var(--light-gray);
  border-radius: 5px;
  margin: 0 10px;
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