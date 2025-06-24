import { createApp } from 'vue'
import './style.css'
import router from './router'
import App from './App.vue'
import '@fortawesome/fontawesome-free/css/all.min.css'
import 'echarts'
import 'chart.js'

//createApp(App).mount('#app')
const app = createApp(App)
app.use(router)
app.mount('#app')