import { createApp } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'
import App from './App.vue'
import Home from './views/Home.vue'
import Movies from './views/Movies.vue'
import Bookings from './views/Bookings.vue'
import Reports from './views/Reports.vue'
import './style.css'

const routes = [
  { path: '/', component: Home },
  { path: '/movies', component: Movies },
  { path: '/bookings', component: Bookings },
  { path: '/reports', component: Reports }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

createApp(App).use(router).mount('#app')