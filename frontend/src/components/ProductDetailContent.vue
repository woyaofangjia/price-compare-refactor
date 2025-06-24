<template>
  <div class="product-detail">
    <img :src="product.image" class="product-image" />
    <div class="product-info">
      <div class="product-title">{{ product.name }}</div>
      <div class="product-tags">
        <span v-for="tag in product.tags" :key="tag" class="tag">{{ tag }}</span>
      </div>
      <div class="product-price">{{ product.price }}</div>
      <div class="product-desc">{{ product.detail }}</div>
    </div>
  </div>
</template>

<script setup>
defineProps({ product: Object })
</script>

<style scoped>
.product-detail {
  display: flex;
  align-items: flex-start;
  gap: 24px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.06);
  padding: 24px;
  margin: 0 auto;
  max-width: 600px;
}
.product-image {
  width: 160px;
  height: 160px;
  object-fit: cover;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}
.product-info {
  flex: 1;
}
.product-title {
  font-size: 1.5rem;
  font-weight: bold;
  margin-bottom: 8px;
}
.product-tags {
  margin-bottom: 10px;
}
.tag {
  display: inline-block;
  background: #e0e7ff;
  color: #3f37c9;
  border-radius: 6px;
  padding: 2px 10px;
  font-size: 0.95rem;
  margin-right: 8px;
}
.product-price {
  color: #e74c3c;
  font-size: 1.2rem;
  margin-bottom: 12px;
}
.product-desc {
  color: #555;
  font-size: 1rem;
  line-height: 1.7;
  background: #f8f8f8;
  border-radius: 8px;
  padding: 12px 16px;
}
</style> 