<script setup>
import { computed, onMounted, reactive, ref } from 'vue'

const loading = ref(false)
const errorMsg = ref('')

const query = reactive({
  adminToken: 'test-admin-token',
  days: 30,
  city: ''
})

const data = reactive({
  generatedAt: '',
  windowDays: 30,
  selectedCity: '全国',
  overview: {
    inquiryTotal: 0,
    inquiryOpenCount: 0,
    inquiryQuotingCount: 0,
    inquiryDealDoneCount: 0,
    inquiryClosedCount: 0,
    merchantLeadTotal: 0,
    merchantLeadNewCount: 0,
    merchantLeadQuotedCount: 0,
    merchantLeadWonCount: 0,
    pickupOrderTotal: 0,
    pickupInTransitCount: 0,
    pickupCompletedCount: 0,
    reconcileOrderTotal: 0,
    reconcilePartialPaidCount: 0,
    reconcilePaidCount: 0,
    subscriptionActiveCount: 0,
    subscriptionExpiringSoonCount: 0,
    billingOutstandingCount: 0,
    billingOutstandingAmountYuan: '0.00',
    adLeadTotal: 0,
    adLeadConvertedCount: 0,
    leadToDealRate: '0.00%',
    dealToFulfillmentRate: '0.00%',
    paidRate: '0.00%',
    adLeadConvertedRate: '0.00%'
  },
  trends: [],
  funnel: {
    inquiryCount: 0,
    quotedLeadCount: 0,
    wonLeadCount: 0,
    dealDoneInquiryCount: 0,
    pickupCompletedCount: 0,
    reconcilePaidCount: 0,
    inquiryToQuoteRate: '0.00%',
    quoteToWinRate: '0.00%',
    dealConversionRate: '0.00%',
    dealToFulfillmentRate: '0.00%',
    paidRate: '0.00%'
  },
  operations: {
    adLeadSubmittedCount: 0,
    adLeadAssignedCount: 0,
    memberExpiringSoonCount: 0,
    memberExpiredCount: 0,
    reconcileDisputedCount: 0,
    pickupInTransitCount: 0,
    pickupCancelledCount: 0,
    tipText: ''
  }
})

const trendMax = computed(() => {
  const all = data.trends.flatMap((item) => [
    Number(item.inquiryCount || 0),
    Number(item.dealCount || 0),
    Number(item.paidCount || 0)
  ])
  return Math.max(...all, 1)
})

const canLoad = computed(() => query.adminToken.trim() && Number(query.days) >= 1 && Number(query.days) <= 365)

async function loadDashboard() {
  if (!canLoad.value || loading.value) return
  loading.value = true
  errorMsg.value = ''
  try {
    const params = new URLSearchParams()
    params.set('days', String(Number(query.days)))
    if (query.city.trim()) {
      params.set('city', query.city.trim())
    }
    const resp = await fetch(`/api/admin/dashboard/a01?${params.toString()}`, {
      headers: { 'X-Admin-Token': query.adminToken.trim() }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `加载失败(${resp.status})`)
    }
    const payload = json.data || {}
    data.generatedAt = payload.generatedAt || ''
    data.windowDays = Number(payload.windowDays || query.days)
    data.selectedCity = payload.selectedCity || query.city || '全国'
    data.overview = payload.overview || data.overview
    data.trends = Array.isArray(payload.trends) ? payload.trends : []
    data.funnel = payload.funnel || data.funnel
    data.operations = payload.operations || data.operations
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    loading.value = false
  }
}

function barHeight(value) {
  return `${Math.max((Number(value || 0) / trendMax.value) * 100, 2)}%`
}

onMounted(() => {
  loadDashboard()
})
</script>

<template>
  <main class="a01-page">
    <section class="card hero">
      <h1>A01 经营总看板</h1>
      <p>管理端聚合线索、成交、履约、回款、会员与广告投放全链路指标。</p>
    </section>

    <section class="card">
      <div class="filters">
        <label>
          Admin Token
          <input v-model="query.adminToken" placeholder="请输入X-Admin-Token" />
        </label>
        <label>
          统计窗口(天)
          <input v-model="query.days" type="number" min="1" max="365" />
        </label>
        <label>
          城市
          <input v-model="query.city" placeholder="默认全国，可填唐山/无锡等" />
        </label>
      </div>
      <div class="actions">
        <button class="btn btn--primary" :disabled="!canLoad || loading" @click="loadDashboard">
          {{ loading ? '加载中...' : '刷新看板' }}
        </button>
      </div>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
      <p class="tip">
        统计范围：{{ data.windowDays }}天 ｜ 城市：{{ data.selectedCity }} ｜ 生成时间：{{ data.generatedAt || '-' }}
      </p>
    </section>

    <section class="card">
      <h2>核心经营指标</h2>
      <div class="kpi-grid">
        <article class="kpi"><span>询价总量</span><strong>{{ data.overview.inquiryTotal }}</strong></article>
        <article class="kpi"><span>待报价询价</span><strong>{{ data.overview.inquiryOpenCount }}</strong></article>
        <article class="kpi"><span>报价中询价</span><strong>{{ data.overview.inquiryQuotingCount }}</strong></article>
        <article class="kpi"><span>已成交询价</span><strong>{{ data.overview.inquiryDealDoneCount }}</strong></article>
        <article class="kpi"><span>商家线索总量</span><strong>{{ data.overview.merchantLeadTotal }}</strong></article>
        <article class="kpi"><span>待报价线索</span><strong>{{ data.overview.merchantLeadNewCount }}</strong></article>
        <article class="kpi"><span>提货单总量</span><strong>{{ data.overview.pickupOrderTotal }}</strong></article>
        <article class="kpi"><span>运输中提货单</span><strong>{{ data.overview.pickupInTransitCount }}</strong></article>
        <article class="kpi"><span>账单未收金额(元)</span><strong>{{ data.overview.billingOutstandingAmountYuan }}</strong></article>
        <article class="kpi"><span>广告转化率</span><strong>{{ data.overview.adLeadConvertedRate }}</strong></article>
      </div>
    </section>

    <section class="card">
      <h2>经营趋势（询价/成交/回款）</h2>
      <div class="trend" v-if="data.trends.length">
        <article v-for="item in data.trends" :key="item.date" class="trend-item">
          <div class="bars">
            <i class="bar inquiry" :style="{ height: barHeight(item.inquiryCount) }"></i>
            <i class="bar deal" :style="{ height: barHeight(item.dealCount) }"></i>
            <i class="bar paid" :style="{ height: barHeight(item.paidCount) }"></i>
          </div>
          <p>{{ item.date.slice(5) }}</p>
        </article>
      </div>
      <p v-else class="tip">暂无趋势数据</p>
    </section>

    <section class="card">
      <h2>漏斗转化</h2>
      <div class="funnel-grid">
        <span>询价→报价：{{ data.funnel.inquiryToQuoteRate }}</span>
        <span>报价→赢单：{{ data.funnel.quoteToWinRate }}</span>
        <span>询价→成交：{{ data.funnel.dealConversionRate }}</span>
        <span>成交→履约：{{ data.funnel.dealToFulfillmentRate }}</span>
        <span>履约→回款：{{ data.funnel.paidRate }}</span>
      </div>
    </section>

    <section class="card">
      <h2>运营待办</h2>
      <div class="ops-grid">
        <span>广告待提交处理：{{ data.operations.adLeadSubmittedCount }}</span>
        <span>广告已分配跟进：{{ data.operations.adLeadAssignedCount }}</span>
        <span>会员临期：{{ data.operations.memberExpiringSoonCount }}</span>
        <span>会员已过期：{{ data.operations.memberExpiredCount }}</span>
        <span>争议对账单：{{ data.operations.reconcileDisputedCount }}</span>
        <span>提货运输中：{{ data.operations.pickupInTransitCount }}</span>
        <span>提货已取消：{{ data.operations.pickupCancelledCount }}</span>
      </div>
      <p class="tip">{{ data.operations.tipText || '关注待办数量波动，优先处理高风险积压。' }}</p>
    </section>
  </main>
</template>

<style scoped>
.a01-page {
  max-width: 1180px;
  margin: 0 auto;
  padding: 14px;
}
.card {
  background: #fff;
  border: 1px solid #ececec;
  border-radius: 12px;
  padding: 14px;
  margin-bottom: 12px;
}
.hero h1 {
  margin: 0 0 8px;
}
.hero p {
  margin: 0;
  color: #4b5563;
}
.filters {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
}
label {
  display: grid;
  gap: 6px;
  color: #374151;
}
input {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 9px 10px;
  font: inherit;
}
.actions {
  display: flex;
  gap: 10px;
  margin-top: 12px;
}
.btn {
  border: 1px solid #ddd;
  border-radius: 8px;
  background: #fff;
  padding: 9px 14px;
  cursor: pointer;
}
.btn--primary {
  border-color: #f57c00;
  background: #f57c00;
  color: #fff;
}
.kpi-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 10px;
}
.kpi {
  border: 1px solid #f2f4f7;
  border-radius: 10px;
  padding: 10px;
  display: grid;
  gap: 6px;
}
.kpi span {
  color: #6b7280;
  font-size: 13px;
}
.kpi strong {
  font-size: 20px;
}
.trend {
  display: grid;
  grid-template-columns: repeat(14, minmax(0, 1fr));
  gap: 8px;
  align-items: end;
  min-height: 160px;
}
.trend-item {
  display: grid;
  gap: 6px;
  justify-items: center;
}
.bars {
  height: 120px;
  width: 100%;
  display: flex;
  align-items: end;
  justify-content: center;
  gap: 2px;
}
.bar {
  width: 6px;
  border-radius: 4px 4px 0 0;
}
.bar.inquiry {
  background: #9ca3af;
}
.bar.quote {
  background: #f59e0b;
}
.bar.deal {
  background: #ef4444;
}
.bar.paid {
  background: #22c55e;
}
.trend-item p {
  margin: 0;
  color: #6b7280;
  font-size: 12px;
}
.funnel-grid,
.ops-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
  color: #374151;
}
.tip {
  margin: 10px 0 0;
  color: #4b5563;
}
.error {
  color: #b42318;
}
@media (max-width: 980px) {
  .filters,
  .kpi-grid,
  .funnel-grid,
  .ops-grid {
    grid-template-columns: 1fr;
  }
  .trend {
    grid-template-columns: repeat(7, minmax(0, 1fr));
  }
}
</style>
