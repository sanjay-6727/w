import { createApp } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'
import App from './App.vue'
import Home from './views/Home.vue'
import FirstItems from './views/FirstItems.vue'
import SecondItems from './views/SecondItems.vue'
import Reports from './views/Reports.vue'
import './style.css'

const routes = [
  { path: '/', component: Home },
  { path: '/first-items', component: FirstItems },
  { path: '/second-items', component: SecondItems },
  { path: '/reports', component: Reports }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

createApp(App).use(router).mount('#app')