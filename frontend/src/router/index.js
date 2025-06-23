import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/pages/home/Home.vue'
import Login from '@/pages/user/Login.vue'

const routes = [
  { path: '/', name: 'Home', component: Home },
  { path: '/login', name: 'Login', component: Login },
  // 后续添加更多路由
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
