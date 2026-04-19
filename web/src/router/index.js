import { createRouter, createWebHistory } from 'vue-router'
import StorageDemandPage from '../pages/StorageDemandPage.vue'
import StorageDemandListPage from '../pages/StorageDemandListPage.vue'

const routes = [
  {
    path: '/',
    redirect: '/logistics/demand/storage'
  },
  {
    path: '/logistics/demand/storage',
    name: 'storageDemandList',
    component: StorageDemandListPage,
    meta: {
      title: '仓储需求大厅_钢铁仓储需求检索-货袋子'
    }
  },
  {
    path: '/logistics/demand/storage/new',
    name: 'storageDemandCreate',
    component: StorageDemandPage,
    meta: {
      title: '发布仓储需求_钢铁仓储需求快速发布-货袋子'
    }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  if (to.meta?.title) {
    document.title = to.meta.title
  }
  next()
})

export default router
