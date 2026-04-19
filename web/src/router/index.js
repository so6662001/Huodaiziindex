import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '../pages/HomePage.vue'
import InquiryCreatePage from '../pages/InquiryCreatePage.vue'
import InquirySuccessPage from '../pages/InquirySuccessPage.vue'
import InquiryQuoteComparePage from '../pages/InquiryQuoteComparePage.vue'
import InquiryDealConfirmPage from '../pages/InquiryDealConfirmPage.vue'
import InquiryPickupPassPage from '../pages/InquiryPickupPassPage.vue'
import InquiryReconcilePassPage from '../pages/InquiryReconcilePassPage.vue'
import InquiryMerchantCreditScorePage from '../pages/InquiryMerchantCreditScorePage.vue'
import InquirySubscriptionPage from '../pages/InquirySubscriptionPage.vue'
import InquiryMerchantLeadManagePage from '../pages/InquiryMerchantLeadManagePage.vue'
import InquiryQuoteWorkbenchPage from '../pages/InquiryQuoteWorkbenchPage.vue'
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
    path: '/inquiry/success',
    name: 'inquirySuccess',
    component: InquirySuccessPage,
    meta: {
      title: '询价提交成功-货袋子'
    }
  },
  {
    path: '/inquiry/compare',
    name: 'inquiryCompare',
    component: InquiryQuoteComparePage,
    meta: {
      title: '报价对比_买家决策台-货袋子'
    }
  },
  {
    path: '/inquiry/deal/confirm',
    name: 'inquiryDealConfirm',
    component: InquiryDealConfirmPage,
    meta: {
      title: 'P07成交确认页_买家确认成交-货袋子'
    }
  },
  {
    path: '/inquiry/pickup/pass',
    name: 'inquiryPickupPass',
    component: InquiryPickupPassPage,
    meta: {
      title: 'P08提货通_提货单协同页-货袋子'
    }
  },
  {
    path: '/inquiry/reconcile/pass',
    name: 'inquiryReconcilePass',
    component: InquiryReconcilePassPage,
    meta: {
      title: 'P09对账通_往来对账协同页-货袋子'
    }
  },
  {
    path: '/merchant/credit/score',
    name: 'merchantCreditScore',
    component: InquiryMerchantCreditScorePage,
    meta: {
      title: 'P10信用评分页_商家信用评估-货袋子'
    }
  },
  {
    path: '/merchant/subscription',
    name: 'merchantSubscription',
    component: InquirySubscriptionPage,
    meta: {
      title: 'P11套餐与订阅页_商家SaaS订阅中心-货袋子'
    }
  },
  {
    path: '/merchant/lead/manage',
    name: 'merchantLeadManage',
    component: InquiryMerchantLeadManagePage,
    meta: {
      title: '商家线索管理_报价工作台-货袋子'
    }
  },
  {
    path: '/merchant/quote/workbench',
    name: 'merchantQuoteWorkbench',
    component: InquiryQuoteWorkbenchPage,
    meta: {
      title: 'P06报价工作台_任务视图-货袋子'
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
