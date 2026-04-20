<script setup>
import { computed, onMounted, reactive, ref } from 'vue'

const loading = ref(false)
const detailLoading = ref(false)
const saving = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

const query = reactive({
  adminToken: 'test-admin-token',
  subscriptionStatus: '',
  apiProductCode: '',
  billingCycle: '',
  owner: '',
  keyword: '',
  page: 1,
  pageSize: 10
})

const state = reactive({
  total: 0,
  page: 1,
  pageSize: 10,
  activeCount: 0,
  trialingCount: 0,
  suspendedCount: 0,
  expiredCount: 0,
  records: []
})

const selectedSubscriptionId = ref('')
const detail = ref(null)
const form = reactive({
  subscriptionCode: '',
  subscriptionName: '',
  merchantId: '',
  merchantName: '',
  apiPackageCode: 'CREDIT_DATA',
  subscriptionStatus: 'TRIALING',
  billingCycle: 'MONTHLY',
  startDate: '',
  endDate: '',
  autoRenewFlag: 'Y',
  throttlePolicy: 'AK_SK',
  qpsLimit: '30',
  dailyQuota: '100000',
  monthlyQuota: '3000000',
  owner: '数据产品组',
  metricsText:
    'SUCCESS_RATE:调用成功率:99210:100000:99.2%:+0.12%\nP95_LATENCY:P95延迟:190ms:240ms:79.2%:-8ms',
  remark: '',
  operator: 'admn16-admin-ui'
})

const canLoad = computed(() => query.adminToken.trim().length > 0)
const canSave = computed(() => {
  return (
    query.adminToken.trim() &&
    form.subscriptionCode.trim() &&
    form.subscriptionName.trim() &&
    form.merchantId.trim() &&
    form.merchantName.trim() &&
    form.apiPackageCode.trim() &&
    form.subscriptionStatus.trim() &&
    form.billingCycle.trim() &&
    form.startDate.trim() &&
    form.operator.trim()
  )
})
const totalPages = computed(() => Math.max(Math.ceil(state.total / query.pageSize), 1))

function parseMetricsText() {
  return form.metricsText
    .split('\n')
    .map((line) => line.trim())
    .filter(Boolean)
    .map((line) => {
      const [metricCode, metricName, usedValue, quotaValue, usageRate, trend] = line.split(':')
      return {
        metricCode: (metricCode || '').trim(),
        metricName: (metricName || '').trim(),
        usedValue: (usedValue || '').trim(),
        quotaValue: (quotaValue || '').trim(),
        usageRate: (usageRate || '').trim(),
        trend: (trend || '').trim()
      }
    })
    .filter((item) => item.metricCode && item.metricName)
}

function formatMetricsText(metrics) {
  if (!Array.isArray(metrics) || metrics.length === 0) return ''
  return metrics
    .map(
      (item) =>
        `${item.metricCode || ''}:${item.metricName || ''}:${item.usedValue || ''}:${item.quotaValue || ''}:${item.usageRate || ''}:${item.trend || ''}`
    )
    .join('\n')
}

function applyDetailToForm(data) {
  form.subscriptionCode = data?.subscriptionCode || ''
  form.subscriptionName = data?.scenarioText || ''
  form.merchantId = data?.merchantId || ''
  form.merchantName = data?.merchantName || ''
  form.apiPackageCode = data?.apiPackageCode || 'CREDIT_DATA'
  form.subscriptionStatus = data?.subscriptionStatus || 'TRIALING'
  form.billingCycle = inferBillingCycleFromDetail(data)
  form.startDate = (data?.createdAt || '').slice(0, 10)
  form.endDate = data?.expireAt || ''
  form.autoRenewFlag = 'Y'
  form.throttlePolicy = data?.authMode || 'AK_SK'
  form.qpsLimit = data?.qpsLimit || ''
  form.dailyQuota = data?.dailyQuota || ''
  form.monthlyQuota = data?.monthlyQuota || ''
  form.owner = data?.owner || '数据产品组'
  form.metricsText = formatMetricsText(data?.quotaMetrics)
  form.remark = data?.latestRemark || ''
}

function inferBillingCycleFromDetail(data) {
  const text = (data?.scenarioText || '').toUpperCase()
  if (text.includes('年')) return 'YEARLY'
  if (text.includes('季')) return 'QUARTERLY'
  if (text.includes('月')) return 'MONTHLY'
  return form.billingCycle || 'MONTHLY'
}

function resetFormForCreate() {
  selectedSubscriptionId.value = ''
  detail.value = null
  form.subscriptionCode = ''
  form.subscriptionName = ''
  form.merchantId = ''
  form.merchantName = ''
  form.apiPackageCode = 'CREDIT_DATA'
  form.subscriptionStatus = 'TRIALING'
  form.billingCycle = 'MONTHLY'
  form.startDate = new Date().toISOString().slice(0, 10)
  form.endDate = ''
  form.autoRenewFlag = 'Y'
  form.throttlePolicy = 'AK_SK'
  form.qpsLimit = '30'
  form.dailyQuota = '100000'
  form.monthlyQuota = '3000000'
  form.owner = '数据产品组'
  form.metricsText =
    'SUCCESS_RATE:调用成功率:99210:100000:99.2%:+0.12%\nP95_LATENCY:P95延迟:190ms:240ms:79.2%:-8ms'
  form.remark = ''
}

async function loadList() {
  if (!canLoad.value || loading.value) return
  loading.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const params = new URLSearchParams()
    if (query.subscriptionStatus.trim()) params.set('subscriptionStatus', query.subscriptionStatus.trim())
    if (query.apiProductCode.trim()) params.set('apiProductCode', query.apiProductCode.trim())
    if (query.billingCycle.trim()) params.set('billingCycle', query.billingCycle.trim())
    if (query.owner.trim()) params.set('owner', query.owner.trim())
    if (query.keyword.trim()) params.set('keyword', query.keyword.trim())
    params.set('page', String(query.page))
    params.set('pageSize', String(query.pageSize))
    const resp = await fetch(`/api/admin/data-api-subscriptions?${params.toString()}`, {
      headers: { 'X-Admin-Token': query.adminToken.trim() }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `加载失败(${resp.status})`)
    }
    const data = json.data || {}
    state.total = Number(data.total || 0)
    state.page = Number(data.page || query.page)
    state.pageSize = Number(data.pageSize || query.pageSize)
    state.activeCount = Number(data.activeCount || 0)
    state.trialingCount = Number(data.trialingCount || 0)
    state.suspendedCount = Number(data.suspendedCount || 0)
    state.expiredCount = Number(data.expiredCount || 0)
    state.records = Array.isArray(data.records) ? data.records : []
    if (state.records.length > 0 && !selectedSubscriptionId.value) {
      await openDetail(state.records[0].subscriptionId)
    }
  } catch (error) {
    errorMsg.value = error.message || '加载数据API订阅列表失败'
  } finally {
    loading.value = false
  }
}

async function openDetail(subscriptionId) {
  if (!subscriptionId || detailLoading.value) return
  detailLoading.value = true
  errorMsg.value = ''
  try {
    const resp = await fetch(`/api/admin/data-api-subscriptions/${encodeURIComponent(subscriptionId)}`, {
      headers: { 'X-Admin-Token': query.adminToken.trim() }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `详情加载失败(${resp.status})`)
    }
    const data = json.data || {}
    selectedSubscriptionId.value = data.subscriptionId || subscriptionId
    detail.value = data
    applyDetailToForm(data)
  } catch (error) {
    errorMsg.value = error.message || '加载数据API订阅详情失败'
  } finally {
    detailLoading.value = false
  }
}

async function saveSubscription() {
  if (!canSave.value || saving.value) return
  saving.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      subscriptionCode: form.subscriptionCode.trim().toUpperCase(),
      subscriptionName: form.subscriptionName.trim(),
      merchantId: form.merchantId.trim().toUpperCase(),
      merchantName: form.merchantName.trim(),
      apiPackageCode: form.apiPackageCode.trim().toUpperCase(),
      subscriptionStatus: form.subscriptionStatus.trim().toUpperCase(),
      billingCycle: form.billingCycle.trim().toUpperCase(),
      startDate: form.startDate.trim(),
      endDate: form.endDate.trim() || null,
      autoRenewFlag: form.autoRenewFlag.trim() || null,
      throttlePolicy: form.throttlePolicy.trim().toUpperCase() || null,
      qpsLimit: form.qpsLimit.trim() || null,
      dailyQuota: form.dailyQuota.trim() || null,
      monthlyQuota: form.monthlyQuota.trim() || null,
      owner: form.owner.trim() || null,
      metrics: parseMetricsText(),
      remark: form.remark.trim() || null,
      operator: form.operator.trim()
    }
    const resp = await fetch('/api/admin/data-api-subscriptions', {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'X-Admin-Token': query.adminToken.trim()
      },
      body: JSON.stringify(payload)
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `保存失败(${resp.status})`)
    }
    const data = json.data || {}
    successMsg.value = `订阅 ${data.subscriptionCode || form.subscriptionCode} 保存成功`
    selectedSubscriptionId.value = data.subscriptionId || ''
    detail.value = data
    applyDetailToForm(data)
    await loadList()
  } catch (error) {
    errorMsg.value = error.message || '保存数据API订阅失败'
  } finally {
    saving.value = false
  }
}

function prevPage() {
  if (query.page <= 1 || loading.value) return
  query.page -= 1
  loadList()
}

function nextPage() {
  if (query.page >= totalPages.value || loading.value) return
  query.page += 1
  loadList()
}

onMounted(() => {
  resetFormForCreate()
  loadList()
})
</script>

<template>
  <main class="admn16-page">
    <section class="card hero">
      <h1>ADM-N16 数据API订阅管理</h1>
      <p>统一管理商家数据 API 订阅、配额用量、授权策略与生命周期状态。</p>
    </section>

    <section class="card">
      <h2>筛选查询</h2>
      <div class="filters">
        <label>
          Admin Token
          <input v-model="query.adminToken" placeholder="请输入X-Admin-Token" />
        </label>
        <label>
          订阅状态
          <select v-model="query.subscriptionStatus">
            <option value="">全部</option>
            <option value="ACTIVE">ACTIVE</option>
            <option value="TRIALING">TRIALING</option>
            <option value="SUSPENDED">SUSPENDED</option>
            <option value="EXPIRED">EXPIRED</option>
          </select>
        </label>
        <label>
          API产品
          <select v-model="query.apiProductCode">
            <option value="">全部</option>
            <option value="CREDIT_DATA">CREDIT_DATA</option>
            <option value="PRICE_DATA">PRICE_DATA</option>
            <option value="RISK_TAG">RISK_TAG</option>
            <option value="CAPACITY_FORECAST">CAPACITY_FORECAST</option>
          </select>
        </label>
        <label>
          计费周期
          <select v-model="query.billingCycle">
            <option value="">全部</option>
            <option value="MONTHLY">MONTHLY</option>
            <option value="QUARTERLY">QUARTERLY</option>
            <option value="YEARLY">YEARLY</option>
          </select>
        </label>
        <label>
          负责人
          <input v-model="query.owner" placeholder="按负责人筛选" />
        </label>
        <label class="span-2">
          关键词
          <input v-model="query.keyword" placeholder="订阅编码/商家/API/备注" />
        </label>
      </div>
      <div class="actions">
        <button class="btn btn--primary" :disabled="!canLoad || loading" @click="loadList">
          {{ loading ? '加载中...' : '查询订阅' }}
        </button>
        <button class="btn" @click="resetFormForCreate">新建订阅</button>
      </div>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
      <p v-if="successMsg" class="ok">{{ successMsg }}</p>
    </section>

    <section class="card">
      <h2>订阅统计</h2>
      <div class="kpi-grid">
        <article class="kpi"><span>订阅总数</span><strong>{{ state.total }}</strong></article>
        <article class="kpi"><span>生效中</span><strong>{{ state.activeCount }}</strong></article>
        <article class="kpi"><span>试用中</span><strong>{{ state.trialingCount }}</strong></article>
        <article class="kpi"><span>已暂停</span><strong>{{ state.suspendedCount }}</strong></article>
        <article class="kpi"><span>已到期</span><strong>{{ state.expiredCount }}</strong></article>
      </div>
    </section>

    <section class="card">
      <div class="head">
        <h2>订阅列表</h2>
        <span class="tip">共 {{ state.total }} 条</span>
      </div>
      <p v-if="loading">列表加载中...</p>
      <p v-else-if="state.records.length === 0">暂无订阅数据</p>
      <ul v-else class="sub-list">
        <li
          v-for="item in state.records"
          :key="item.subscriptionId"
          :class="['sub-item', selectedSubscriptionId === item.subscriptionId ? 'active' : '']"
          @click="openDetail(item.subscriptionId)"
        >
          <div class="line">
            <strong>{{ item.subscriptionCode }}</strong>
            <span class="badge">{{ item.apiProductCode }}</span>
            <span class="badge">{{ item.billingCycleText || item.billingCycle }}</span>
            <span class="badge" :class="{ warn: item.subscriptionStatus !== 'ACTIVE' }">{{
              item.subscriptionStatusText || item.subscriptionStatus
            }}</span>
          </div>
          <p class="muted">商家：{{ item.merchantName }}（{{ item.merchantId }}）</p>
          <p class="muted">
            配额：{{ item.quotaUsed || '0' }} / {{ item.quotaTotal || '-' }} ｜ 使用率：{{ item.usageRate || '-' }}
          </p>
          <p class="muted">到期：{{ item.expireAt || '-' }} ｜ 更新时间：{{ item.updatedAt || '-' }}</p>
        </li>
      </ul>
      <div class="pager">
        <button class="btn" :disabled="query.page <= 1 || loading" @click="prevPage">上一页</button>
        <span>第 {{ query.page }} / {{ totalPages }} 页</span>
        <button class="btn" :disabled="query.page >= totalPages || loading" @click="nextPage">下一页</button>
      </div>
    </section>

    <section class="card">
      <h2>订阅编辑</h2>
      <div class="form-grid">
        <label>订阅编码<input v-model="form.subscriptionCode" placeholder="如 DAPI_SUB_CREDIT_PRO_2026Q2" /></label>
        <label>订阅名称<input v-model="form.subscriptionName" placeholder="如 信用数据API订阅增强版" /></label>
        <label>商家ID<input v-model="form.merchantId" placeholder="如 M000009" /></label>
        <label>商家名称<input v-model="form.merchantName" placeholder="如 成都锦川钢贸" /></label>
        <label>
          API产品
          <select v-model="form.apiPackageCode">
            <option value="CREDIT_DATA">CREDIT_DATA</option>
            <option value="PRICE_DATA">PRICE_DATA</option>
            <option value="RISK_TAG">RISK_TAG</option>
            <option value="CAPACITY_FORECAST">CAPACITY_FORECAST</option>
          </select>
        </label>
        <label>
          订阅状态
          <select v-model="form.subscriptionStatus">
            <option value="ACTIVE">ACTIVE</option>
            <option value="TRIALING">TRIALING</option>
            <option value="SUSPENDED">SUSPENDED</option>
            <option value="EXPIRED">EXPIRED</option>
          </select>
        </label>
        <label>
          计费周期
          <select v-model="form.billingCycle">
            <option value="MONTHLY">MONTHLY</option>
            <option value="QUARTERLY">QUARTERLY</option>
            <option value="YEARLY">YEARLY</option>
          </select>
        </label>
        <label>
          鉴权策略
          <select v-model="form.throttlePolicy">
            <option value="AK_SK">AK_SK</option>
            <option value="OAUTH2">OAUTH2</option>
            <option value="IP_WHITELIST">IP_WHITELIST</option>
          </select>
        </label>
        <label>QPS上限<input v-model="form.qpsLimit" placeholder="如 80" /></label>
        <label>日配额<input v-model="form.dailyQuota" placeholder="如 250000" /></label>
        <label>月配额<input v-model="form.monthlyQuota" placeholder="如 7500000" /></label>
        <label>开始日期<input v-model="form.startDate" placeholder="YYYY-MM-DD" /></label>
        <label>结束日期<input v-model="form.endDate" placeholder="YYYY-MM-DD，可空" /></label>
        <label>负责人<input v-model="form.owner" placeholder="如 数据产品组" /></label>
        <label>操作人<input v-model="form.operator" /></label>
        <label class="span-2">
          指标快照（每行：编码:名称:已用值:额度值:使用率:趋势）
          <textarea
            v-model="form.metricsText"
            rows="5"
            placeholder="SUCCESS_RATE:调用成功率:113000:250000:45.2%:+0.18%"
          />
        </label>
        <label class="span-2">
          备注
          <input v-model="form.remark" placeholder="可空" />
        </label>
      </div>
      <div class="actions">
        <button class="btn btn--primary" :disabled="!canSave || saving" @click="saveSubscription">
          {{ saving ? '保存中...' : '保存订阅' }}
        </button>
      </div>
    </section>

    <section class="card" v-if="detail">
      <h2>订阅详情</h2>
      <div class="detail-grid">
        <p>订阅ID：{{ detail.subscriptionId }}</p>
        <p>订阅编码：{{ detail.subscriptionCode }}</p>
        <p>商家：{{ detail.merchantName }}（{{ detail.merchantId }}）</p>
        <p>API产品：{{ detail.apiPackageName }}（{{ detail.apiPackageCode }}）</p>
        <p>订阅状态：{{ detail.subscriptionStatusText || detail.subscriptionStatus }}</p>
        <p>鉴权策略：{{ detail.authModeText || detail.authMode }}</p>
        <p>QPS上限：{{ detail.qpsLimit || '-' }}</p>
        <p>日配额：{{ detail.dailyQuota || '-' }}</p>
        <p>月配额：{{ detail.monthlyQuota || '-' }}</p>
        <p>今日用量：{{ detail.usedToday || '-' }}</p>
        <p>本月用量：{{ detail.usedThisMonth || '-' }}</p>
        <p>使用率：{{ detail.usageRate || '-' }}</p>
        <p>到期时间：{{ detail.expireAt || '-' }}</p>
        <p>负责人：{{ detail.owner || '-' }}</p>
        <p class="span-2">最新备注：{{ detail.latestRemark || '-' }}</p>
      </div>
      <p class="muted">可执行动作：{{ detail.availableActions?.join(' / ') || '-' }}</p>
    </section>
  </main>
</template>

<style scoped>
.admn16-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px 16px 40px;
}
.card {
  background: #fff;
  border: 1px solid #ececec;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 14px;
}
.hero h1 {
  margin: 0;
}
.hero p {
  margin: 8px 0 0;
  color: #4b5563;
}
.filters,
.form-grid,
.detail-grid {
  display: grid;
  gap: 10px;
  grid-template-columns: repeat(4, minmax(0, 1fr));
}
.span-2 {
  grid-column: span 2;
}
label {
  display: grid;
  gap: 6px;
  color: #374151;
}
input,
select,
textarea {
  width: 100%;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 8px 10px;
  font: inherit;
  box-sizing: border-box;
}
.actions {
  margin-top: 12px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
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
.ok {
  color: #0c7a43;
}
.kpi-grid {
  display: grid;
  gap: 10px;
  grid-template-columns: repeat(5, minmax(0, 1fr));
}
.kpi {
  background: #f8fafc;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  padding: 10px;
  display: grid;
  gap: 6px;
}
.kpi span {
  font-size: 12px;
  color: #6b7280;
}
.kpi strong {
  font-size: 20px;
  color: #111827;
}
.tip,
.muted {
  color: #6b7280;
  margin: 6px 0 0;
}
.head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  margin-bottom: 10px;
}
.sub-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: grid;
  gap: 10px;
}
.sub-item {
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  padding: 10px;
  cursor: pointer;
}
.sub-item.active {
  border-color: #f57c00;
  box-shadow: 0 0 0 1px #f57c0030 inset;
}
.line {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}
.badge {
  background: #eef2ff;
  color: #3730a3;
  border-radius: 999px;
  padding: 2px 8px;
  font-size: 12px;
}
.badge.warn {
  background: #fff5f5;
  color: #b42318;
}
.pager {
  margin-top: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
}
@media (max-width: 1024px) {
  .filters,
  .form-grid,
  .detail-grid,
  .kpi-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
@media (max-width: 720px) {
  .filters,
  .form-grid,
  .detail-grid,
  .kpi-grid {
    grid-template-columns: 1fr;
  }
  .span-2 {
    grid-column: span 1;
  }
}
</style>
