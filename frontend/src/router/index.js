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
import Admin from '@/pages/admin/Admin.vue'
import Dashboard from '@/pages/admin/Dashboard.vue'
import Users from '@/pages/admin/Users.vue'
import Products from '@/pages/admin/Products.vue'
import Posts from '@/pages/admin/Posts.vue'
import Charts from '@/pages/admin/Charts.vue'
import Settings from '@/pages/admin/Settings.vue'

const routes = [
  { path: '/', name: 'Home', component: Home },
  { path: '/login', name: 'Login', component: Login },
  { path: '/register', name: 'Register', component: Register },
  { path: '/profile', name: 'Profile', component: Profile },
  { path: '/profile/edit', name: 'EditProfile', component: EditProfile },
  { path: '/detail', name: 'Detail', component: Detail },
  { path: '/favorites', name: 'Favorites', component: Favorites },
  { path: '/chart', name: 'Chart', component: Chart },
  { path: '/dynamic', name: 'Dynamic', component: Dynamic },
  { path: '/auth', name: 'Auth', component: Auth },
    {
    path: '/admin',
    component: Admin,
    children: [
      { path: '', redirect: '/admin/dashboard' },
      { path: 'dashboard', component: Dashboard },
      { path: 'users', component: Users },
      { path: 'products', component: Products },
      { path: 'posts', component: Posts },
      { path: 'charts', component: Charts },
      { path: 'settings', component: Settings }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
