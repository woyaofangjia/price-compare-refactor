<template>
  <section class="page-content">
    <div class="container">
      <h2 class="section-title">我的收藏夹</h2>
      <div class="favorites-container">
        <div class="favorites-grid">
          <div class="favorite-item" v-for="item in favorites" :key="item.id" @click="goToProduct(item.id)" style="cursor:pointer;">
            <div class="favorite-image">
              <img :src="item.img" :alt="item.title" />
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

onMounted(async () => {
  const res = await fetch('/api/favorites')
  favorites.value = await res.json()
})

function goToProduct(id) {
  router.push(`/product/${id}`)
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
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 32px;
  margin-top: 20px;
}
.favorite-item {
  display: flex;
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
}
.favorite-info {
  flex: 1;
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

@media (max-width: 1200px) {
  .favorites-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
@media (max-width: 700px) {
  .favorites-grid {
    grid-template-columns: 1fr;
  }
}
</style>