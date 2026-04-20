<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const city = ref('唐山')
const keyword = ref('')

const hotSpots = [
  { id: 'SP001', name: '螺纹钢 HRB400E', spec: 'Φ20*12m', city: '唐山', price: '3520', stock: '860吨' },
  { id: 'SP002', name: '热轧卷板 Q235B', spec: '3.0*1500*C', city: '无锡', price: '3680', stock: '420吨' },
  { id: 'SP003', name: '中厚板 Q355B', spec: '10*2000*8000', city: '郑州', price: '3890', stock: '220吨' }
]

const featuredMerchants = [
  { name: '唐山弘达钢贸', score: 92, response: '7分钟', fulfill: '98.1%', tags: ['可开票', '当日排货'] },
  { name: '无锡铭泰供应链', score: 89, response: '11分钟', fulfill: '97.3%', tags: ['可回单', '支持月结'] },
  { name: '佛山联胜金属', score: 87, response: '14分钟', fulfill: '96.8%', tags: ['南下专线', '夜间装车'] }
]

const rankResponse = [
  { name: '唐山弘达钢贸', value: '平均7分钟' },
  { name: '无锡铭泰供应链', value: '平均11分钟' },
  { name: '佛山联胜金属', value: '平均14分钟' }
]

const rankFulfillment = [
  { name: '唐山弘达钢贸', value: '98.1%' },
  { name: '郑州鑫诚贸易', value: '97.9%' },
  { name: '无锡铭泰供应链', value: '97.3%' }
]

const indicators = [
  { label: '询价发起率', value: '38.6%', trend: '+4.2%' },
  { label: '询价→报价率', value: '72.4%', trend: '+3.8%' },
  { label: '报价→成交率', value: '29.1%', trend: '+2.5%' },
  { label: 'SaaS订阅渗透率', value: '41.7%', trend: '+5.6%' }
]

const valueChains = {
  seller: [
    '入驻黄页/现货上架',
    '接收线索',
    '报价成交',
    '提货履约',
    '对账回款',
    '信用提升',
    '获得更多流量'
  ],
  buyer: ['AI询价/求购', '报价对比', '下单提货', '对账售后', '稳定复购'],
  platform: ['撮合流量', '交易协同', '交付留痕', '风控评分', '智能分发', '收费增长']
}

const revenueModels = [
  {
    title: '流量收入',
    items: ['入驻年费', '广告位', '会员展示']
  },
  {
    title: '线索收入',
    items: ['有效线索包', '竞价推荐位', '超额线索计费']
  },
  {
    title: 'SaaS收入',
    items: ['提货通', '对账通', '库准/AI销售']
  },
  {
    title: '数据收入',
    items: ['信用评分', '风险预警', '经营分析/API']
  }
]

const kpiViews = [
  {
    title: '买家侧',
    values: ['询价发起率', '询价→报价率', '报价→成交率']
  },
  {
    title: '商家侧',
    values: ['报价响应时效', '成交率', '复购率', '履约评分']
  },
  {
    title: '平台侧',
    values: ['有效线索率', '线索收入占比', 'SaaS渗透率', '净留存NRR']
  },
  {
    title: '风控侧',
    values: ['逾期率', '争议率', '坏账率趋势']
  }
]

function goInquiry() {
  router.push('/inquiry/create')
}

function goWorkbench() {
  router.push('/merchant/quote/workbench')
}

function goLeadCenter() {
  router.push('/site/ad/mine')
}

function goCreditScore() {
  router.push('/merchant/credit/score?merchantId=S001')
}

function goSubscriptionPage() {
  router.push('/merchant/subscription?merchantId=S001')
}

function goBillingPage() {
  router.push('/merchant/billing?merchantId=S001')
}

function goDispatchRulePage() {
  router.push('/dispatch/score-rules?merchantId=S001')
}

function goMessageCenterPage() {
  router.push('/merchant/messages?merchantId=S001')
}

function goH5HomePage() {
  router.push('/h5/home?city=唐山')
}

function goH5InquiryStep1Page() {
  router.push('/h5/inquiry/step1?city=唐山')
}

function goH5QuickQuotePage() {
  router.push('/h5/quick-quote?merchantId=S001')
}

function goH5PickupOrderPage() {
  router.push('/h5/pickup-orders?contactMobile=13800138000')
}

function goH5ReconcileOrderPage() {
  router.push('/h5/reconcile-orders?contactMobile=13800138000')
}

function goH5MemberPage() {
  router.push('/h5/member?merchantId=S001')
}

function goH5N01QuickLoginPage() {
  router.push('/h5/login-quick')
}

function goH5N02IdentitySwitchPage() {
  router.push('/h5/identity-switch')
}

function goH5N03EnterpriseCertificationPage() {
  router.push('/h5/enterprise-certification')
}

function goH5N04NegotiationSessionPage() {
  router.push('/h5/quote-session')
}

function goH5N05OrderDetailPage() {
  router.push('/h5/order-detail')
}

function goH5N06PickupScanPage() {
  router.push('/h5/pickup-scan')
}

function goH5N07ReconcileDetailPage() {
  router.push('/h5/reconcile-detail')
}

function goH5N08AfterSaleCreatePage() {
  router.push('/h5/after-sale-create')
}

function goH5N09LitePayPage() {
  router.push('/h5/lite-pay')
}

function goH5N10CreditBriefPage() {
  router.push('/h5/credit-brief')
}

function goH5N11MessageSettingsPage() {
  router.push('/h5/message-settings')
}

function goA01DashboardPage() {
  router.push('/admin/dashboard/a01')
}

function goA02LeadOpsPage() {
  router.push('/admin/lead-ops/a02')
}

function goA03QuoteEfficiencyPage() {
  router.push('/admin/quote-efficiency/a03?merchantId=S001')
}

function goA04PickupMonitorPage() {
  router.push('/admin/pickup-monitor/a04')
}

function goA05ReconcileMonitorPage() {
  router.push('/admin/reconcile-monitor/a05')
}

function goA06DispatchStrategyPage() {
  router.push('/admin/dispatch-strategy/a06')
}

function goA07PlanPricingPage() {
  router.push('/admin/plan-pricing/a07')
}

function goA08RiskAlertPage() {
  router.push('/admin/risk-alert/a08')
}

function goAdmn01MerchantCertificationReviewPage() {
  router.push('/admin/merchant-cert-review/admn01')
}

function goAdmn02BuyerBlacklistPage() {
  router.push('/admin/buyer-blacklist/admn02')
}

function goAdmn03RbacPage() {
  router.push('/admin/rbac-role-permission/admn03')
}

function goAdmn04AuditLogPage() {
  router.push('/admin/audit-log/admn04')
}

function goAdmn05CategorySpecDictPage() {
  router.push('/admin/category-spec-dict/admn05')
}

function goAdmn06LeadQualityCenterPage() {
  router.push('/admin/lead-quality/admn06')
}

function goAdmn08DealFunnelAnalysisPage() {
  router.push('/admin/deal-funnel-analysis/admn08')
}

function goAdmn09ArbitrationTicketCenterPage() {
  router.push('/admin/arbitration-ticket-center/admn09')
}

function goAdmn10BillingRuleConfigPage() {
  router.push('/admin/billing-rule-config/admn10')
}

function goAdmn11PaymentRefundManagePage() {
  router.push('/admin/payment-refund-manage/admn11')
}

function goAdmn12AdSlotScheduleCenterPage() {
  router.push('/admin/ad-slot-schedule-center/admn12')
}

function goN01LoginRegisterPage() {
  router.push('/account/login-register')
}

function goN02IdentitySelectPage() {
  router.push('/account/identity-select')
}

function goN03EnterpriseCertificationPage() {
  router.push('/account/enterprise-certification')
}

function goN04OnboardingProgressPage() {
  router.push('/account/onboarding-progress')
}

function goN05NegotiationSessionPage() {
  router.push('/account/negotiation-session')
}

function goN06OrderDetailPage() {
  router.push('/account/order-detail')
}

function goN07TradeTermsPage() {
  router.push('/account/trade-terms-confirm')
}

function goN08AfterSaleDisputePage() {
  router.push('/account/after-sale-dispute')
}

function goN09AfterSaleProgressPage() {
  router.push('/account/after-sale-progress')
}

function goN10CashierPage() {
  router.push('/account/cashier')
}

function goN11PaymentResultPage() {
  router.push('/account/payment-result')
}

function goN12InvoiceManagePage() {
  router.push('/account/invoice-manage')
}

function goN13CreditScoreDetailPage() {
  router.push('/account/credit-score-detail')
}

function goN14DispatchAppealPage() {
  router.push('/account/dispatch-appeal')
}

function onSearch() {
  if (!keyword.value.trim()) {
    return
  }
  router.push(`/site/ad/mine?keyword=${encodeURIComponent(keyword.value.trim())}`)
}
</script>

<template>
  <main class="home-page">
    <header class="topbar">
      <div class="logo">货袋子</div>
      <nav class="topnav">
        <a href="#closure">三闭环方案</a>
        <a href="#spot">现货大厅</a>
        <a href="#merchant">企业黄页</a>
        <a href="#rules">分发规则</a>
      </nav>
      <div class="search-area">
        <select v-model="city" aria-label="城市">
          <option>唐山</option>
          <option>无锡</option>
          <option>佛山</option>
          <option>郑州</option>
        </select>
        <input v-model="keyword" placeholder="搜索现货/企业/规格" />
        <button class="btn primary" @click="onSearch">搜索</button>
      </div>
    </header>

    <section class="hero">
      <div class="hero-main">
        <p class="eyebrow">钢铁交易平台 · 线索-成交-风控一体化</p>
        <h1>P01 首页（新版）</h1>
        <p class="desc">
          聚焦“线索→报价→成交”最快变现，联动提货通、对账通与信用分发，帮助商家提升成交效率与风控能力。
        </p>
        <div class="hero-actions">
          <button class="btn primary" @click="goInquiry">我要买货（AI询价）</button>
          <button class="btn" @click="goWorkbench">我要卖货（报价工作台）</button>
        </div>
      </div>
      <div class="hero-panel">
        <h3>经营核心指标</h3>
        <ul>
          <li v-for="item in indicators" :key="item.label">
            <span>{{ item.label }}</span>
            <strong>{{ item.value }}</strong>
            <em>{{ item.trend }}</em>
          </li>
        </ul>
      </div>
    </section>

    <section class="panel">
      <h2>平台商业模式图（价值链）</h2>
      <div class="value-chain-grid">
        <article class="value-card">
          <h3>供给侧（商家）</h3>
          <ol>
            <li v-for="item in valueChains.seller" :key="item">{{ item }}</li>
          </ol>
        </article>
        <article class="value-card">
          <h3>需求侧（买家）</h3>
          <ol>
            <li v-for="item in valueChains.buyer" :key="item">{{ item }}</li>
          </ol>
        </article>
        <article class="value-card">
          <h3>平台侧</h3>
          <ol>
            <li v-for="item in valueChains.platform" :key="item">{{ item }}</li>
          </ol>
        </article>
      </div>
    </section>

    <section id="closure" class="panel">
      <h2>三大短闭环</h2>
      <div class="closure-grid">
        <article class="closure-card">
          <h3>闭环A：线索→报价→成交</h3>
          <p>现货大厅 + AI询价 + 报价工作台，先验证平台带订单能力。</p>
          <p class="meta">收费：有效线索费 / 会员展示费 / 报价工具订阅</p>
        </article>
        <article class="closure-card">
          <h3>闭环B：成交→提货→对账</h3>
          <p>提货通 + 对账通，沉淀证据链与回款管理，提升客单与续费。</p>
          <p class="meta">收费：按账号/按单量订阅</p>
        </article>
        <article class="closure-card">
          <h3>闭环C：履约→评分→分发</h3>
          <p>信用评分 + 智能分发，服务越好流量越多，形成正向飞轮。</p>
          <p class="meta">收费：高级流量包 + 信用数据服务</p>
        </article>
      </div>
    </section>

    <section id="spot" class="panel">
      <div class="panel-head">
        <h2>现货大厅（线上资源）</h2>
        <button class="btn" @click="goInquiry">发起AI询价</button>
      </div>
      <div class="spot-list">
        <article v-for="item in hotSpots" :key="item.id" class="spot-card">
          <h3>{{ item.name }}</h3>
          <p>规格：{{ item.spec }}</p>
          <p>城市：{{ item.city }} ｜ 库存：{{ item.stock }}</p>
          <div class="spot-bottom">
            <strong>¥{{ item.price }}/吨</strong>
            <button class="btn primary" @click="goInquiry">立即询价</button>
          </div>
        </article>
      </div>
    </section>

    <section class="panel">
      <h2>四层收入模型</h2>
      <div class="model-grid">
        <article v-for="item in revenueModels" :key="item.title" class="model-card">
          <h3>{{ item.title }}</h3>
          <ul>
            <li v-for="line in item.items" :key="line">{{ line }}</li>
          </ul>
        </article>
      </div>
    </section>

    <section id="merchant" class="panel">
      <h2>企业黄页（优质商家）</h2>
      <div class="merchant-grid">
        <article v-for="m in featuredMerchants" :key="m.name" class="merchant-card">
          <h3>{{ m.name }}</h3>
          <p>信用分：<strong>{{ m.score }}</strong> ｜ 响应：{{ m.response }} ｜ 履约：{{ m.fulfill }}</p>
          <div class="tags">
            <span v-for="tag in m.tags" :key="tag">{{ tag }}</span>
          </div>
          <button class="btn" @click="goLeadCenter">查看可报价线索</button>
        </article>
      </div>
    </section>

    <section class="panel rank-panel">
      <h2>本周榜单</h2>
      <div class="rank-grid">
        <article class="rank-card">
          <h3>响应之星</h3>
          <ol>
            <li v-for="item in rankResponse" :key="item.name">
              <span>{{ item.name }}</span>
              <strong>{{ item.value }}</strong>
            </li>
          </ol>
        </article>
        <article class="rank-card">
          <h3>履约之星</h3>
          <ol>
            <li v-for="item in rankFulfillment" :key="item.name">
              <span>{{ item.name }}</span>
              <strong>{{ item.value }}</strong>
            </li>
          </ol>
        </article>
      </div>
    </section>

    <section id="rules" class="panel rules-panel">
      <h2>分发评分框架（公开）</h2>
      <p>平台按履约稳定性、响应速度、报价竞争力、纠纷率、数据完整性分发流量，付费因素仅小权重加成。</p>
      <ul>
        <li>履约稳定性（权重高）</li>
        <li>响应速度（权重高）</li>
        <li>报价竞争力（权重中）</li>
        <li>纠纷率/投诉率（负向）</li>
        <li>ERP/提货/对账接入完整度</li>
      </ul>
      <div class="rules-actions">
        <button class="btn" @click="goLeadCenter">查看我的投放单</button>
        <button class="btn" @click="goCreditScore">查看信用评分</button>
        <button class="btn" @click="goDispatchRulePage">分发评分规则</button>
        <button class="btn" @click="goMessageCenterPage">消息中心</button>
        <button class="btn" @click="goH5HomePage">H5首页</button>
        <button class="btn" @click="goH5InquiryStep1Page">H5询价Step1</button>
        <button class="btn" @click="goH5QuickQuotePage">H5快捷报价</button>
        <button class="btn" @click="goH5PickupOrderPage">H5提货单</button>
        <button class="btn" @click="goH5ReconcileOrderPage">H5对账单</button>
        <button class="btn" @click="goH5MemberPage">H5我的会员</button>
        <button class="btn" @click="goH5N01QuickLoginPage">H5-N01快捷登录</button>
        <button class="btn" @click="goH5N02IdentitySwitchPage">H5-N02身份切换</button>
        <button class="btn" @click="goH5N03EnterpriseCertificationPage">H5-N03企业认证</button>
        <button class="btn" @click="goH5N04NegotiationSessionPage">H5-N04报价会话</button>
        <button class="btn" @click="goH5N05OrderDetailPage">H5-N05订单详情</button>
        <button class="btn" @click="goH5N06PickupScanPage">H5-N06扫码提货</button>
        <button class="btn" @click="goH5N07ReconcileDetailPage">H5-N07对账详情</button>
        <button class="btn" @click="goH5N08AfterSaleCreatePage">H5-N08售后发起</button>
        <button class="btn" @click="goH5N09LitePayPage">H5-N09轻支付</button>
        <button class="btn" @click="goH5N10CreditBriefPage">H5-N10信用分简报</button>
        <button class="btn" @click="goH5N11MessageSettingsPage">H5-N11消息设置</button>
        <button class="btn" @click="goA01DashboardPage">A01经营总看板</button>
        <button class="btn" @click="goA02LeadOpsPage">A02线索运营中心</button>
        <button class="btn" @click="goA03QuoteEfficiencyPage">A03报价效率中心</button>
        <button class="btn" @click="goA04PickupMonitorPage">A04提货监控中心</button>
        <button class="btn" @click="goA05ReconcileMonitorPage">A05对账监控中心</button>
        <button class="btn" @click="goA06DispatchStrategyPage">A06分发策略配置</button>
        <button class="btn" @click="goA07PlanPricingPage">A07套餐与定价管理</button>
        <button class="btn" @click="goA08RiskAlertPage">A08风险预警中心</button>
        <button class="btn" @click="goAdmn01MerchantCertificationReviewPage">ADM-N01商家认证审核台</button>
        <button class="btn" @click="goAdmn02BuyerBlacklistPage">ADM-N02买家与黑名单管理</button>
        <button class="btn" @click="goAdmn03RbacPage">ADM-N03角色权限管理（RBAC）</button>
        <button class="btn" @click="goAdmn04AuditLogPage">ADM-N04操作审计日志</button>
        <button class="btn" @click="goAdmn05CategorySpecDictPage">ADM-N05类目/规格词库管理</button>
        <button class="btn" @click="goAdmn06LeadQualityCenterPage">ADM-N06线索质检中心</button>
        <button class="btn" @click="goAdmn08DealFunnelAnalysisPage">ADM-N08成交漏斗分析</button>
        <button class="btn" @click="goAdmn09ArbitrationTicketCenterPage">ADM-N09仲裁工单中心</button>
        <button class="btn" @click="goAdmn10BillingRuleConfigPage">ADM-N10计费规则配置</button>
        <button class="btn" @click="goAdmn11PaymentRefundManagePage">ADM-N11支付与退款管理</button>
        <button class="btn" @click="goAdmn12AdSlotScheduleCenterPage">ADM-N12广告位排期中心</button>
        <button class="btn" @click="goN01LoginRegisterPage">PC-N01登录/注册</button>
        <button class="btn" @click="goN02IdentitySelectPage">PC-N02身份选择</button>
        <button class="btn" @click="goN03EnterpriseCertificationPage">PC-N03企业认证提交</button>
        <button class="btn" @click="goN04OnboardingProgressPage">PC-N04入驻审核进度</button>
        <button class="btn" @click="goN05NegotiationSessionPage">PC-N05议价会话页</button>
        <button class="btn" @click="goN06OrderDetailPage">PC-N06订单详情页</button>
        <button class="btn" @click="goN07TradeTermsPage">PC-N07交易条款确认页</button>
        <button class="btn" @click="goN08AfterSaleDisputePage">PC-N08售后/争议发起页</button>
        <button class="btn" @click="goN09AfterSaleProgressPage">PC-N09售后处理进度页</button>
        <button class="btn" @click="goN10CashierPage">PC-N10收银台</button>
        <button class="btn" @click="goN11PaymentResultPage">PC-N11支付结果页</button>
        <button class="btn" @click="goN12InvoiceManagePage">PC-N12发票与抬头管理</button>
        <button class="btn" @click="goN13CreditScoreDetailPage">PC-N13信用评分明细页</button>
        <button class="btn" @click="goN14DispatchAppealPage">PC-N14分发异议申诉页</button>
        <button class="btn" @click="goSubscriptionPage">套餐与订阅</button>
        <button class="btn" @click="goBillingPage">计费与账单</button>
        <button class="btn primary" @click="goInquiry">立即体验AI询价</button>
      </div>
    </section>

    <section class="panel">
      <h2>经营看板核心指标（四视角）</h2>
      <div class="kpi-grid">
        <article v-for="view in kpiViews" :key="view.title" class="kpi-card">
          <h3>{{ view.title }}</h3>
          <ul>
            <li v-for="metric in view.values" :key="metric">{{ metric }}</li>
          </ul>
        </article>
      </div>
    </section>

    <button class="mobile-cta" @click="goInquiry">立即询价</button>
  </main>
</template>

<style scoped>
.home-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 16px 16px 44px;
}

.topbar {
  display: grid;
  grid-template-columns: 120px 1fr auto;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.logo {
  font-size: 22px;
  font-weight: 700;
  color: #f57c00;
}

.topnav {
  display: flex;
  gap: 14px;
  color: #374151;
}

.topnav a {
  font-size: 14px;
  color: #374151;
}

.search-area {
  display: flex;
  gap: 8px;
}

.search-area select,
.search-area input {
  height: 36px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 0 10px;
  background: #fff;
}

.search-area input {
  min-width: 220px;
}

.hero {
  display: grid;
  grid-template-columns: 1fr 360px;
  gap: 14px;
  margin-bottom: 14px;
}

.hero-main,
.hero-panel,
.panel {
  background: #fff;
  border: 1px solid #ececec;
  border-radius: 12px;
  padding: 16px;
}

.eyebrow {
  margin: 0 0 8px;
  color: #f57c00;
  font-size: 13px;
  font-weight: 600;
}

h1 {
  margin: 0;
  font-size: 30px;
}

.desc {
  margin: 10px 0 14px;
  color: #4b5563;
  line-height: 1.6;
}

.hero-actions {
  display: flex;
  gap: 10px;
}

.hero-panel h3 {
  margin-top: 0;
}

.hero-panel ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.hero-panel li {
  display: grid;
  grid-template-columns: 1fr auto auto;
  gap: 8px;
  align-items: baseline;
  border-top: 1px dashed #ececec;
  padding: 8px 0;
}

.hero-panel li:first-child {
  border-top: 0;
}

.hero-panel li span {
  color: #6b7280;
}

.hero-panel li strong {
  color: #111827;
}

.hero-panel li em {
  color: #16a34a;
  font-style: normal;
  font-size: 12px;
}

.panel {
  margin-bottom: 14px;
}

.panel h2 {
  margin: 0 0 12px;
}

.closure-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.value-chain-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.value-card {
  border: 1px solid #f3f4f6;
  border-radius: 10px;
  padding: 12px;
}

.value-card h3 {
  margin: 0 0 8px;
}

.value-card ol {
  margin: 0;
  padding-left: 18px;
  color: #374151;
  line-height: 1.7;
}

.closure-card {
  border: 1px solid #f3f4f6;
  border-radius: 10px;
  padding: 12px;
  background: #fffaf4;
}

.closure-card h3 {
  margin: 0 0 6px;
  font-size: 17px;
}

.closure-card p {
  margin: 0 0 8px;
  color: #4b5563;
}

.closure-card .meta {
  margin: 0;
  color: #92400e;
  font-size: 13px;
}

.panel-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.spot-list {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.model-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}

.model-card {
  border: 1px solid #f3f4f6;
  border-radius: 10px;
  padding: 12px;
  background: #fffbf5;
}

.model-card h3 {
  margin: 0 0 8px;
  font-size: 16px;
}

.model-card ul {
  margin: 0;
  padding-left: 18px;
  color: #4b5563;
  line-height: 1.7;
}

.spot-card {
  border: 1px solid #f3f4f6;
  border-radius: 10px;
  padding: 12px;
}

.spot-card h3 {
  margin: 0 0 8px;
}

.spot-card p {
  margin: 0 0 6px;
  color: #4b5563;
}

.spot-bottom {
  margin-top: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.spot-bottom strong {
  color: #ea580c;
}

.merchant-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.merchant-card {
  border: 1px solid #f3f4f6;
  border-radius: 10px;
  padding: 12px;
}

.merchant-card h3 {
  margin: 0 0 8px;
}

.merchant-card p {
  margin: 0 0 8px;
  color: #4b5563;
}

.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 10px;
}

.tags span {
  font-size: 12px;
  color: #92400e;
  background: #fff7ed;
  border-radius: 999px;
  padding: 2px 8px;
}

.rank-panel .rank-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.rank-card {
  border: 1px solid #f3f4f6;
  border-radius: 10px;
  padding: 12px;
}

.rank-card h3 {
  margin-top: 0;
}

.rank-card ol {
  margin: 0;
  padding-left: 18px;
}

.rank-card li {
  display: flex;
  justify-content: space-between;
  gap: 10px;
  padding: 6px 0;
}

.rank-card strong {
  color: #ea580c;
}

.rules-panel p {
  color: #4b5563;
  margin-top: 0;
}

.rules-panel ul {
  margin-top: 6px;
  color: #374151;
  line-height: 1.8;
}

.rules-actions {
  display: flex;
  gap: 10px;
  margin-top: 12px;
}

.kpi-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}

.kpi-card {
  border: 1px solid #f3f4f6;
  border-radius: 10px;
  padding: 12px;
}

.kpi-card h3 {
  margin: 0 0 8px;
}

.kpi-card ul {
  margin: 0;
  padding-left: 18px;
  color: #374151;
  line-height: 1.7;
}

.btn {
  height: 36px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  background: #fff;
  color: #374151;
  padding: 0 12px;
  cursor: pointer;
}

.btn.primary {
  border-color: #f57c00;
  background: #f57c00;
  color: #fff;
}

.mobile-cta {
  display: none;
}

@media (max-width: 980px) {
  .topbar {
    grid-template-columns: 1fr;
    gap: 10px;
  }

  .topnav {
    overflow-x: auto;
    white-space: nowrap;
    padding-bottom: 4px;
  }

  .search-area {
    display: grid;
    grid-template-columns: 1fr 1fr;
  }

  .search-area input {
    min-width: 0;
    grid-column: span 2;
  }

  .hero {
    grid-template-columns: 1fr;
  }

  .closure-grid,
  .value-chain-grid,
  .spot-list,
  .merchant-grid,
  .model-grid,
  .kpi-grid,
  .rank-panel .rank-grid {
    grid-template-columns: 1fr;
  }

  .hero-actions,
  .rules-actions {
    display: grid;
    grid-template-columns: 1fr;
  }

  .mobile-cta {
    position: fixed;
    left: 16px;
    right: 16px;
    bottom: 14px;
    height: 44px;
    border: 0;
    border-radius: 999px;
    background: #f57c00;
    color: #fff;
    font-size: 15px;
    font-weight: 600;
    display: block;
    box-shadow: 0 6px 20px rgba(245, 124, 0, 0.35);
  }
}
</style>
