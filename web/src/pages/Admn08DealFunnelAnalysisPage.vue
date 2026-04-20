<script setup>
import { computed, onMounted, reactive, ref } from 'vue'

const loading = ref(false)
const errorMsg = ref('')

const query = reactive({
  adminToken: 'test-admin-token',
  days: 30,
  city: ''
})

const state = reactive({
  generatedAt: '',
  windowDays: 30,
  city: '全国',
  inquiryCount: 0,
  quotedCount: 0,
  wonCount: 0,
  dealCount: 0,
  pickupCompletedCount: 0,
  paidCount: 0,
  inquiryToQuoteRate: '0.00%',
  quoteToWinRate: '0.00%',
  wonToDealRate: '0.00%',
  dealToPickupRate: '0.00%',
  pickupToPaidRate: '0.00%',
  paidAmountYuan: '0.00',
  avgDealDays: '0.0',
  avgPickupDays: '0.0',
  nodes: [],
  trends: [],
  insights: []
})

const canLoad = computed(() => query.adminToken.trim() && Number(query.days) >= 1 && Number(query.days) <= 365)
const trendMax = computed(() => {
  const values = state.trends.flatMap((item) => [
    Number(item.leadCreatedCount || 0),
    Number(item.dealCount || 0),
    Number(item.paidCount || 0)
  ])
  return Math.max(...values, 1)
})

function barHeight(value) {
  return `${Math.max((Number(value || 0) / trendMax.value) * 100, 2)}%`
}

async function loadFunnel() {
  if (!canLoad.value || loading.value) return
  loading.value = true
  errorMsg.value = ''
  try {
    const params = new URLSearchParams()
    params.set('days', String(Number(query.days)))
    if (query.city.trim()) params.set('city', query.city.trim())
    const resp = await fetch(`/api/admin/deal-funnel?${params.toString()}`, {
      headers: { 'X-Admin-Token': query.adminToken.trim() }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `成交漏斗加载失败(${resp.status})`)
    }
    const data = json.data || {}
    state.generatedAt = data.generatedAt || ''
    state.windowDays = Number(data.windowDays || query.days)
    state.city = data.city || query.city || '全国'
    state.inquiryCount = Number(data.inquiryCount || 0)
    state.quotedCount = Number(data.quotedCount || 0)
    state.wonCount = Number(data.wonCount || 0)
    state.dealCount = Number(data.dealCount || 0)
    state.pickupCompletedCount = Number(data.pickupCompletedCount || 0)
    state.paidCount = Number(data.paidCount || 0)
    state.inquiryToQuoteRate = data.inquiryToQuoteRate || '0.00%'
    state.quoteToWinRate = data.quoteToWinRate || '0.00%'
    state.wonToDealRate = data.wonToDealRate || '0.00%'
    state.dealToPickupRate = data.dealToPickupRate || '0.00%'
    state.pickupToPaidRate = data.pickupToPaidRate || '0.00%'
    state.paidAmountYuan = data.paidAmountYuan || '0.00'
    state.avgDealDays = data.avgDealDays || '0.0'
    state.avgPickupDays = data.avgPickupDays || '0.0'
    state.nodes = Array.isArray(data.nodes) ? data.nodes : []
    state.trends = Array.isArray(data.trends) ? data.trends : []
    state.insights = Array.isArray(data.insights) ? data.insights : []
  } catch (error) {
    errorMsg.value = error.message || '成交漏斗加载失败'
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadFunnel()
})
</script>

<template>
  <main class="admn08-page">
    <section class="card hero">
      <h1>ADM-N08 成交漏斗分析</h1>
      <p>聚合询价、报价、赢单、成交、提货与回款链路，识别关键转化损耗并辅助运营提效。</p>
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
        <button class="btn btn--primary" :disabled="!canLoad || loading" @click="loadFunnel">
          {{ loading ? '加载中...' : '查询漏斗' }}
        </button>
      </div>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
      <p class="tip">
        统计范围：{{ state.windowDays }}天 ｜ 城市：{{ state.city }} ｜ 生成时间：{{ state.generatedAt || '-' }}
      </p>
    </section>

    <section class="stats">
      <article class="stat"><span>询价</span><strong>{{ state.inquiryCount }}</strong></article>
      <article class="stat"><span>已报价</span><strong>{{ state.quotedCount }}</strong></article>
      <article class="stat"><span>已赢单</span><strong>{{ state.wonCount }}</strong></article>
      <article class="stat"><span>已成交</span><strong>{{ state.dealCount }}</strong></article>
      <article class="stat"><span>已提货</span><strong>{{ state.pickupCompletedCount }}</strong></article>
      <article class="stat"><span>已回款</span><strong>{{ state.paidCount }}</strong></article>
    </section>

    <section class="card">
      <h2>阶段转化</h2>
      <div class="funnel-grid">
        <span>询价→报价：{{ state.inquiryToQuoteRate }}</span>
        <span>报价→赢单：{{ state.quoteToWinRate }}</span>
        <span>赢单→成交：{{ state.wonToDealRate }}</span>
        <span>成交→提货：{{ state.dealToPickupRate }}</span>
        <span>提货→回款：{{ state.pickupToPaidRate }}</span>
        <span>累计回款金额(元)：{{ state.paidAmountYuan }}</span>
        <span>平均成交周期(天)：{{ state.avgDealDays }}</span>
        <span>平均提货周期(天)：{{ state.avgPickupDays }}</span>
      </div>
    </section>

    <section class="card">
      <h2>漏斗节点明细</h2>
      <p v-if="state.nodes.length === 0">暂无节点数据</p>
      <ul v-else class="list">
        <li v-for="node in state.nodes" :key="node.stageCode" class="item">
          <div>
            <h3>{{ node.stageName }}</h3>
            <p>阶段数量：{{ node.stageCount }}</p>
            <p>阶段金额(元)：{{ node.amountYuan }}</p>
            <p>阶段转化率：{{ node.conversionRate }}</p>
            <p>流失率：{{ node.dropRate }}</p>
          </div>
        </li>
      </ul>
    </section>

    <section class="card">
      <h2>趋势（线索创建 / 成交 / 回款）</h2>
      <div class="trend" v-if="state.trends.length">
        <article v-for="item in state.trends" :key="item.date" class="trend-item">
          <div class="bars">
            <i class="bar inquiry" :style="{ height: barHeight(item.leadCreatedCount) }"></i>
            <i class="bar deal" :style="{ height: barHeight(item.dealCount) }"></i>
            <i class="bar paid" :style="{ height: barHeight(item.paidCount) }"></i>
          </div>
          <p>{{ item.date.slice(5) }}</p>
        </article>
      </div>
      <p v-else class="tip">暂无趋势数据</p>
    </section>

    <section class="card">
      <h2>分析洞察</h2>
      <ul class="insight-list">
        <li v-for="(text, idx) in state.insights" :key="`${idx}-${text}`">{{ text }}</li>
      </ul>
    </section>
  </main>
</template>

<style scoped>
.admn08-page {
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
.hero {
  background: linear-gradient(135deg, #fff7ed, #ffedd5);
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
  margin-top: 10px;
}
.btn {
  border: 1px solid #ddd;
  border-radius: 8px;
  background: #fff;
  padding: 8px 12px;
  cursor: pointer;
}
.btn--primary {
  border-color: #f57c00;
  background: #f57c00;
  color: #fff;
}
.error {
  color: #b42318;
}
.tip {
  margin-top: 10px;
  color: #6b7280;
}
.stats {
  display: grid;
  grid-template-columns: repeat(6, minmax(0, 1fr));
  gap: 10px;
  margin-bottom: 12px;
}
.stat {
  background: #fff;
  border: 1px solid #ececec;
  border-radius: 10px;
  padding: 10px;
  display: grid;
  gap: 6px;
}
.stat span {
  color: #6b7280;
}
.stat strong {
  font-size: 24px;
}
.funnel-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 8px;
}
.list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: grid;
  gap: 10px;
}
.item {
  border: 1px solid #ececec;
  border-radius: 10px;
  padding: 10px;
}
.item h3 {
  margin: 0 0 6px;
}
.item p {
  margin: 4px 0;
  color: #4b5563;
}
.trend {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(48px, 1fr));
  gap: 8px;
  align-items: end;
}
.trend-item {
  text-align: center;
}
.bars {
  height: 120px;
  border: 1px solid #ececec;
  border-radius: 8px;
  padding: 6px;
  display: flex;
  align-items: end;
  justify-content: center;
  gap: 4px;
  background: #fafafa;
}
.bar {
  width: 10px;
  border-radius: 4px 4px 0 0;
}
.bar.inquiry {
  background: #fb923c;
}
.bar.deal {
  background: #22c55e;
}
.bar.paid {
  background: #0ea5e9;
}
.insight-list {
  margin: 0;
  padding-left: 18px;
  color: #374151;
  display: grid;
  gap: 6px;
}
@media (max-width: 900px) {
  .filters {
    grid-template-columns: 1fr;
  }
  .stats {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
  .funnel-grid {
    grid-template-columns: 1fr;
  }
}
</style>
