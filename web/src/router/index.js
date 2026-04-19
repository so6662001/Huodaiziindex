import { createRouter, createWebHistory } from 'vue-router'
import SiteAdPage from '../pages/SiteAdPage.vue'
import SiteAdDetailPage from '../pages/SiteAdDetailPage.vue'

const routes = [
  {
    path: '/',
    redirect: '/site/ad'
  },
  {
    path: '/site/ad',
    name: 'siteAd',
    component: SiteAdPage,
    meta: {
      title: '分站广告投放_城市站点流量曝光方案-货袋子'
    }
  },
  {
    path: '/site/ad/:id',
    name: 'siteAdDetail',
    component: SiteAdDetailPage,
    meta: {
      title: '分站广告位详情_广告位资源与投放方案-货袋子'
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
