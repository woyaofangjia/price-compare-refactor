import { createApp } from 'vue'
import './style.css'
import router from './router'
import App from './App.vue'

import 'chart.js'

//createApp(App).mount('#app')
const app = createApp(App)
app.use(router)
app.mount('#app')