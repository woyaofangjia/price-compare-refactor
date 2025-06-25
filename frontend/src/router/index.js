import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/pages/home/Home.vue'
import Login from '@/pages/user/Login.vue'
import Register from '@/pages/user/Register.vue'
import Profile from '@/pages/user/Profile.vue'
import EditProfile from '@/pages/user/EditProfile.vue'
import Detail from '@/pages/home/Detail.vue'
import Favorites from '@/pages/home/Favorites.vue'
import Chart from '@/pages/home/Chart.vue'
import SquarePage from '@/pages/post/SquarePage.vue'
import Dynamic from '@/pages/Dynamic.vue'
import PostDynamicPage from '@/pages/post/PostDynamicPage.vue'
import Auth from '@/pages/Auth.vue'
import Admin from '@/pages/admin/Admin.vue'
import Products from '@/pages/admin/Products.vue'
import Posts from '@/pages/admin/Posts.vue'
import PostDetail from '@/pages/admin/PostDetail.vue'
import Charts from '@/pages/admin/Charts.vue'
import ProductDetailPage from '@/pages/ProductDetailPage.vue'

const routes = [
  { path: '/', name: 'Home', component: Home },
  { path: '/login', name: 'Login', component: Login },
  { path: '/register', name: 'Register', component: Register },
  { path: '/profile', name: 'Profile', component: Profile },
  { path: '/profile/edit', name: 'EditProfile', component: EditProfile },
  { path: '/detail', name: 'Detail', component: Detail },
  { path: '/favorites', name: 'Favorites', component: Favorites },
  { path: '/chart', name: 'Chart', component: Chart },
  { path: '/square', name: 'Square', component: SquarePage },
  { path: '/my-dynamic', name: 'MyDynamic', component: Dynamic },
  { path: '/post/create', name: 'PostCreate', component: PostDynamicPage },
  { path: '/auth', name: 'Auth', component: Auth },
  { path: '/product/:id', name: 'ProductDetail', component: ProductDetailPage },
  {
    path: '/admin',
    component: Admin,
    children: [
      { path: '', redirect: '/admin/products' },
      { path: 'products', component: Products },
      { path: 'brands', component: () => import('@/pages/admin/Brands.vue') },
      { path: 'posts', component: Posts },
      { path: 'users', component: () => import('@/pages/admin/Users.vue') },
      { path: 'posts/:id', component: PostDetail },
      { path: 'charts', component: Charts }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
