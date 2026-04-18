import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '../pages/HomePage.vue'
import SpotPage from '../pages/SpotPage.vue'

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomePage
  },
  {
    path: '/spot',
    name: 'spot',
    component: SpotPage
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
