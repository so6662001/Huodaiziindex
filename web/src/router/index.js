import { createRouter, createWebHistory } from 'vue-router'
import SiteAdLeadMinePage from '../pages/SiteAdLeadMinePage.vue'
import SiteAdLeadSubmitDemoPage from '../pages/SiteAdLeadSubmitDemoPage.vue'

const routes = [
  {
    path: '/',
    redirect: '/site/ad/mine'
  },
  {
    path: '/site/ad/mine',
    name: 'siteAdLeadMine',
    component: SiteAdLeadMinePage,
    meta: {
      title: '我的投放单_分站广告线索管理-货袋子'
    }
  },
  {
    path: '/site/ad/submit',
    name: 'siteAdLeadSubmit',
    component: SiteAdLeadSubmitDemoPage,
    meta: {
      title: '提交分站广告投放线索-货袋子'
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
