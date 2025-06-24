import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/pages/home/Home.vue'
import Login from '@/pages/user/Login.vue'
import Register from '@/pages/user/Register.vue'
import Profile from '@/pages/user/Profile.vue'
import EditProfile from '@/pages/user/EditProfile.vue'
import Detail from '@/pages/home/Detail.vue'
import Favorites from '@/pages/home/Favorites.vue'
import Chart from '@/pages/home/Chart.vue'
import Dynamic from '@/pages/Dynamic.vue'
import Auth from '@/pages/Auth.vue'
import Admin from '@/pages/Admin.vue'
import SquarePage from '@/pages/post/SquarePage.vue'
import PostDynamicPage from '@/pages/post/PostDynamicPage.vue'
import DynamicDetailPage from '@/pages/post/DynamicDetailPage.vue'

const routes = [
  { path: '/', name: 'Home', component: Home },
  { path: '/login', name: 'Login', component: Login },
  { path: '/register', name: 'Register', component: Register },
  { path: '/profile', name: 'Profile', component: Profile },
  { path: '/edit-profile', name: 'EditProfile', component: EditProfile },
  { path: '/detail', name: 'Detail', component: Detail },
  { path: '/favorites', name: 'Favorites', component: Favorites },
  { path: '/chart', name: 'Chart', component: Chart },
  { path: '/dynamic', name: 'Dynamic', component: Dynamic },
  { path: '/auth', name: 'Auth', component: Auth },
  { path: '/admin', name: 'Admin', component: Admin },
  { path: '/square', name: 'Square', component: SquarePage },
  { path: '/post-dynamic', name: 'PostDynamic', component: PostDynamicPage },
  { path: '/dynamic-detail/:id', name: 'DynamicDetail', component: DynamicDetailPage },
  // 后续添加更多路由
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
