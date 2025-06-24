import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/pages/home/Home.vue'
// import Login from '@/pages/user/Login.vue' // 暂无此页面
import Detail from '@/pages/home/Detail.vue'
import Favorites from '@/pages/home/Favorites.vue'
import Chart from '@/pages/home/Chart.vue'
import Dynamic from '@/pages/Dynamic.vue'
import Auth from '@/pages/Auth.vue'
import Admin from '@/pages/Admin.vue'

const routes = [
  { path: '/', name: 'Home', component: Home },
  // { path: '/login', name: 'Login', component: Login },
  { path: '/detail', name: 'Detail', component: Detail },
  { path: '/favorites', name: 'Favorites', component: Favorites },
  { path: '/chart', name: 'Chart', component: Chart },
  { path: '/dynamic', name: 'Dynamic', component: Dynamic },
  { path: '/auth', name: 'Auth', component: Auth },
  { path: '/admin', name: 'Admin', component: Admin },
  // 后续添加更多路由
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
