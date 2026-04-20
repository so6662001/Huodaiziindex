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
import InquiryBillingPage from '../pages/InquiryBillingPage.vue'
import InquiryDispatchScoreRulePage from '../pages/InquiryDispatchScoreRulePage.vue'
import InquiryMessageCenterPage from '../pages/InquiryMessageCenterPage.vue'
import H5HomePage from '../pages/H5HomePage.vue'
import H5InquiryStep1Page from '../pages/H5InquiryStep1Page.vue'
import H5InquiryStep2Page from '../pages/H5InquiryStep2Page.vue'
import H5InquiryStep3Page from '../pages/H5InquiryStep3Page.vue'
import H5QuoteComparePage from '../pages/H5QuoteComparePage.vue'
import H5MerchantLeadPage from '../pages/H5MerchantLeadPage.vue'
import H5QuickQuotePage from '../pages/H5QuickQuotePage.vue'
import H5PickupOrderPage from '../pages/H5PickupOrderPage.vue'
import H5ReconcileOrderPage from '../pages/H5ReconcileOrderPage.vue'
import H5MemberPage from '../pages/H5MemberPage.vue'
import A01DashboardPage from '../pages/A01DashboardPage.vue'
import A02LeadOpsPage from '../pages/A02LeadOpsPage.vue'
import A03QuoteEfficiencyPage from '../pages/A03QuoteEfficiencyPage.vue'
import A04PickupMonitorPage from '../pages/A04PickupMonitorPage.vue'
import A05ReconcileMonitorPage from '../pages/A05ReconcileMonitorPage.vue'
import A06DispatchStrategyConfigPage from '../pages/A06DispatchStrategyConfigPage.vue'
import A07PlanPricingManagePage from '../pages/A07PlanPricingManagePage.vue'
import A08RiskAlertCenterPage from '../pages/A08RiskAlertCenterPage.vue'
import N01LoginRegisterPage from '../pages/N01LoginRegisterPage.vue'
import N02IdentitySelectPage from '../pages/N02IdentitySelectPage.vue'
import N03EnterpriseCertificationPage from '../pages/N03EnterpriseCertificationPage.vue'
import N04OnboardingProgressPage from '../pages/N04OnboardingProgressPage.vue'
import N05NegotiationSessionPage from '../pages/N05NegotiationSessionPage.vue'
import N06OrderDetailPage from '../pages/N06OrderDetailPage.vue'
import N07TradeTermsConfirmPage from '../pages/N07TradeTermsConfirmPage.vue'
import N08AfterSaleDisputePage from '../pages/N08AfterSaleDisputePage.vue'
import N09AfterSaleProgressPage from '../pages/N09AfterSaleProgressPage.vue'
import N10CashierPage from '../pages/N10CashierPage.vue'
import N11PaymentResultPage from '../pages/N11PaymentResultPage.vue'
import N12InvoiceManagePage from '../pages/N12InvoiceManagePage.vue'
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
    path: '/h5',
    name: 'h5Home',
    component: H5HomePage,
    meta: {
      title: 'H01 H5首页_钢铁交易移动端首页-货袋子'
    }
  },
  {
    path: '/h5/inquiry/step1',
    name: 'h5InquiryStep1',
    component: H5InquiryStep1Page,
    meta: {
      title: 'H02 H5询价Step1_移动端询价第一步-货袋子'
    }
  },
  {
    path: '/h5/inquiry/step2',
    name: 'h5InquiryStep2',
    component: H5InquiryStep2Page,
    meta: {
      title: 'H03 H5询价Step2_移动端询价第二步-货袋子'
    }
  },
  {
    path: '/h5/inquiry/step3',
    name: 'h5InquiryStep3',
    component: H5InquiryStep3Page,
    meta: {
      title: 'H04 H5询价Step3_移动端询价第三步-货袋子'
    }
  },
  {
    path: '/h5/quote-compare',
    name: 'h5QuoteCompare',
    component: H5QuoteComparePage,
    meta: {
      title: 'H05 H5报价对比_移动端商家报价对比-货袋子'
    }
  },
  {
    path: '/h5/merchant/leads',
    name: 'h5MerchantLeads',
    component: H5MerchantLeadPage,
    meta: {
      title: 'H06 H5我的线索_商家移动端线索管理-货袋子'
    }
  },
  {
    path: '/h5/quick-quote',
    name: 'h5QuickQuote',
    component: H5QuickQuotePage,
    meta: {
      title: 'H07 H5快捷报价_商家移动端快速报价-货袋子'
    }
  },
  {
    path: '/h5/pickup-orders',
    name: 'h5PickupOrders',
    component: H5PickupOrderPage,
    meta: {
      title: 'H08 H5提货单_移动端提货协同-货袋子'
    }
  },
  {
    path: '/h5/reconcile-orders',
    name: 'h5ReconcileOrders',
    component: H5ReconcileOrderPage,
    meta: {
      title: 'H09 H5对账单_移动端对账回款协同-货袋子'
    }
  },
  {
    path: '/h5/member',
    name: 'h5Member',
    component: H5MemberPage,
    meta: {
      title: 'H10 H5我的会员_移动端会员中心-货袋子'
    }
  },
  {
    path: '/admin/dashboard/a01',
    name: 'a01Dashboard',
    component: A01DashboardPage,
    meta: {
      title: 'A01经营总看板_管理端经营总览-货袋子'
    }
  },
  {
    path: '/admin/lead-ops/a02',
    name: 'a02LeadOps',
    component: A02LeadOpsPage,
    meta: {
      title: 'A02线索运营中心_管理端线索运营协同-货袋子'
    }
  },
  {
    path: '/admin/quote-efficiency/a03',
    name: 'a03QuoteEfficiency',
    component: A03QuoteEfficiencyPage,
    meta: {
      title: 'A03报价效率中心_管理端报价效率协同-货袋子'
    }
  },
  {
    path: '/admin/pickup-monitor/a04',
    name: 'a04PickupMonitor',
    component: A04PickupMonitorPage,
    meta: {
      title: 'A04提货监控中心_管理端提货履约监控-货袋子'
    }
  },
  {
    path: '/admin/reconcile-monitor/a05',
    name: 'a05ReconcileMonitor',
    component: A05ReconcileMonitorPage,
    meta: {
      title: 'A05对账监控中心_管理端回款与对账监控-货袋子'
    }
  },
  {
    path: '/admin/dispatch-strategy/a06',
    name: 'a06DispatchStrategyConfig',
    component: A06DispatchStrategyConfigPage,
    meta: {
      title: 'A06分发策略配置页_管理端分发规则配置-货袋子'
    }
  },
  {
    path: '/admin/plan-pricing/a07',
    name: 'a07PlanPricingManage',
    component: A07PlanPricingManagePage,
    meta: {
      title: 'A07套餐与定价管理_管理端套餐定价配置-货袋子'
    }
  },
  {
    path: '/admin/risk-alert/a08',
    name: 'a08RiskAlertCenter',
    component: A08RiskAlertCenterPage,
    meta: {
      title: 'A08风险预警中心_管理端全链路风险处置-货袋子'
    }
  },
  {
    path: '/account/login-register',
    name: 'n01LoginRegister',
    component: N01LoginRegisterPage,
    meta: {
      title: 'PC-N01登录注册_货袋子账号中心'
    }
  },
  {
    path: '/account/identity-select',
    name: 'n02IdentitySelect',
    component: N02IdentitySelectPage,
    meta: {
      title: 'PC-N02身份选择_货袋子账号中心'
    }
  },
  {
    path: '/account/enterprise-certification',
    name: 'n03EnterpriseCertification',
    component: N03EnterpriseCertificationPage,
    meta: {
      title: 'PC-N03企业认证提交_货袋子账号中心'
    }
  },
  {
    path: '/account/onboarding-progress',
    name: 'n04OnboardingProgress',
    component: N04OnboardingProgressPage,
    meta: {
      title: 'PC-N04入驻审核进度_货袋子账号中心'
    }
  },
  {
    path: '/account/negotiation-session',
    name: 'n05NegotiationSession',
    component: N05NegotiationSessionPage,
    meta: {
      title: 'PC-N05议价会话页_货袋子账号中心'
    }
  },
  {
    path: '/account/order-detail',
    name: 'n06OrderDetail',
    component: N06OrderDetailPage,
    meta: {
      title: 'PC-N06订单详情页_货袋子账号中心'
    }
  },
  {
    path: '/account/trade-terms-confirm',
    name: 'n07TradeTermsConfirm',
    component: N07TradeTermsConfirmPage,
    meta: {
      title: 'PC-N07交易条款确认页_货袋子账号中心'
    }
  },
  {
    path: '/account/after-sale-dispute',
    name: 'n08AfterSaleDispute',
    component: N08AfterSaleDisputePage,
    meta: {
      title: 'PC-N08售后争议发起页_货袋子账号中心'
    }
  },
  {
    path: '/account/after-sale-progress',
    name: 'n09AfterSaleProgress',
    component: N09AfterSaleProgressPage,
    meta: {
      title: 'PC-N09售后处理进度页_货袋子账号中心'
    }
  },
  {
    path: '/account/cashier',
    name: 'n10Cashier',
    component: N10CashierPage,
    meta: {
      title: 'PC-N10收银台_货袋子账号中心'
    }
  },
  {
    path: '/account/payment-result',
    name: 'n11PaymentResult',
    component: N11PaymentResultPage,
    meta: {
      title: 'PC-N11支付结果页_货袋子账号中心'
    }
  },
  {
    path: '/account/invoice-manage',
    name: 'n12InvoiceManage',
    component: N12InvoiceManagePage,
    meta: {
      title: 'PC-N12发票与抬头管理_货袋子账号中心'
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
    path: '/merchant/billing',
    name: 'merchantBilling',
    component: InquiryBillingPage,
    meta: {
      title: 'P12计费与账单页_账单管理与回款登记-货袋子'
    }
  },
  {
    path: '/dispatch/score-rules',
    name: 'dispatchScoreRules',
    component: InquiryDispatchScoreRulePage,
    meta: {
      title: 'P13分发评分规则公开页_公开透明规则说明-货袋子'
    }
  },
  {
    path: '/merchant/message-center',
    name: 'merchantMessageCenter',
    component: InquiryMessageCenterPage,
    meta: {
      title: 'P14消息中心_通知与待办聚合-货袋子'
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
