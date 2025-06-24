<template>
    <section class="page-content">
      <div class="container">
        <h2 class="section-title">今日热门商品</h2>
        <div class="products-grid">
          <div class="product-card" v-for="item in hotProducts" :key="item.title">
            <div class="product-image">
              <img :src="item.img" :alt="item.title" />
            </div>
            <div class="product-info">
              <div class="product-title">{{ item.title }}</div>
              <div class="product-price">{{ item.price }}</div>
              <div :class="['price-change', item.priceChange > 0 ? 'price-up' : 'price-down']">
                {{ item.priceChange > 0 ? '涨' : '降' }} {{ Math.abs(item.priceChange) }}%
              </div>
              <div class="platform-tags">
                <span class="platform-tag" v-for="tag in item.platforms" :key="tag">{{ tag }}</span>
              </div>
            </div>
          </div>
        </div>
        <h2 class="section-title" style="margin-top: 40px;">近期降价商品</h2>
        <div class="products-grid">
          <div class="product-card" v-for="item in dropProducts" :key="item.title">
            <div class="product-image">
              <img :src="item.img" :alt="item.title" />
            </div>
            <div class="product-info">
              <div class="product-title">{{ item.title }}</div>
              <div class="product-price">{{ item.price }} <span v-if="item.oldPrice" style="text-decoration: line-through; color: var(--gray); font-size: 0.9rem;">{{ item.oldPrice }}</span></div>
              <div class="price-change price-down">降 {{ item.priceChange }}%</div>
              <div class="platform-tags">
                <span class="platform-tag" v-for="tag in item.platforms" :key="tag">{{ tag }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  </template>
  
  <script setup>
  const hotProducts = [
    {
      img: 'https://via.placeholder.com/200x200?text=旗舰手机',
      title: '某品牌旗舰手机 8GB+256GB 全网通',
      price: '￥3,299',
      priceChange: -5.2,
      platforms: ['京东', '天猫', '拼多多']
    },
    {
      img: 'https://via.placeholder.com/200x200?text=轻薄笔记本',
      title: '某品牌轻薄笔记本 i7高配 16GB内存',
      price: '￥6,499',
      priceChange: 2.1,
      platforms: ['京东', '苏宁']
    },
    {
      img: 'https://via.placeholder.com/200x200?text=智能手表',
      title: '智能健康手表 多功能 续航强',
      price: '￥899',
      priceChange: -8.7,
      platforms: ['天猫', '拼多多']
    },
    {
      img: 'https://via.placeholder.com/200x200?text=运动手环',
      title: '运动智能手环 血氧检测 50米防水',
      price: '￥1,299',
      priceChange: -3.4,
      platforms: ['京东', '天猫', '官方商城']
    }
  ];
  const dropProducts = [
    {
      img: 'https://via.placeholder.com/200x200?text=游戏主机',
      title: '新一代游戏主机 4K高清 1TB存储',
      price: '￥3,199',
      oldPrice: '￥3,899',
      priceChange: 18.0,
      platforms: ['京东', '苏宁']
    },
    {
      img: 'https://via.placeholder.com/200x200?text=平板电脑',
      title: '高性能平板电脑 10.5英寸 128GB',
      price: '￥2,399',
      oldPrice: '￥2,999',
      priceChange: 20.0,
      platforms: ['天猫', '拼多多']
    }
  ];
  </script>
  
  <style scoped>
  /* 首页特有样式 */
  .page-content {
    padding: 30px 0;
    min-height: calc(100vh - 150px);
  }
  /* 其他首页样式 */
  .section-title {
    font-size: 1.8rem;
    margin-bottom: 20px;
    padding-bottom: 10px;
    border-bottom: 2px solid var(--light-gray);
    color: var(--primary);
  }
  .products-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 25px;
  }
  .product-card {
    background: white;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 5px 15px rgba(0,0,0,0.05);
    transition: transform 0.3s ease;
    cursor: pointer;
  }
  .product-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 20px rgba(0,0,0,0.1);
  }
  .product-image {
    height: 180px;
    background-color: #f0f3f8;
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;
  }
  .product-image img {
    max-width: 90%;
    max-height: 90%;
    object-fit: contain;
  }
  .product-info {
    padding: 15px;
  }
  .product-title {
    font-weight: 600;
    margin-bottom: 8px;
    height: 40px;
    overflow: hidden;
  }
  .product-price {
    color: var(--warning);
    font-size: 1.2rem;
    font-weight: bold;
    margin-bottom: 8px;
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
  .platform-tags {
    display: flex;
    margin-top: 10px;
  }
  .platform-tag {
    background: var(--light-gray);
    padding: 3px 8px;
    border-radius: 5px;
    font-size: 0.8rem;
    margin-right: 5px;
  }
  </style>