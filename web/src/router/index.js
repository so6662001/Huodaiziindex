import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '../pages/HomePage.vue'
import InquiryCreatePage from '../pages/InquiryCreatePage.vue'
import SiteAdLeadMinePage from '../pages/SiteAdLeadMinePage.vue'
import SiteAdLeadSubmitDemoPage from '../pages/SiteAdLeadSubmitDemoPage.vue'

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomePage,
    meta: {
      title: '货袋子钢铁交易平台_找货更快卖货更稳'
    }
  },
  {
    path: '/inquiry/create',
    name: 'inquiryCreate',
    component: InquiryCreatePage,
    meta: {
      title: 'AI询价_3步快速找货-货袋子'
    }
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
