<script setup>
import { computed, onMounted, reactive, ref } from 'vue'

const loading = ref(false)
const updating = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

const query = reactive({
  adminToken: 'test-admin-token',
  contactMobile: '',
  source: '',
  level: '',
  status: '',
  owner: '',
  keyword: '',
  highOnly: false,
  page: 1,
  pageSize: 12
})

const state = reactive({
  overview: {
    totalCount: 0,
    highCount: 0,
    mediumCount: 0,
    lowCount: 0,
    openCount: 0,
    processingCount: 0,
    resolvedCount: 0,
    overdueCount: 0,
    highRate: '0%',
    resolvedRate: '0%',
    avgAgingHours: '0'
  },
  buckets: [],
  items: [],
  total: 0,
  page: 1,
  pageSize: 12
})

const selectedIds = ref([])
const batchForm = reactive({
  status: 'PROCESSING',
  operator: 'a08-admin-ui',
  remark: ''
})

const sourceOptions = [
  { value: '', label: '全部来源' },
  { value: 'LEAD', label: '线索' },
  { value: 'PICKUP', label: '提货' },
  { value: 'RECONCILE', label: '对账' },
  { value: 'BILLING', label: '账单' }
]

const levelOptions = [
  { value: '', label: '全部级别' },
  { value: 'HIGH', label: '高' },
  { value: 'MEDIUM', label: '中' },
  { value: 'LOW', label: '低' }
]

const statusOptions = [
  { value: '', label: '全部状态' },
  { value: 'OPEN', label: '待处理' },
  { value: 'PROCESSING', label: '处理中' },
  { value: 'RESOLVED', label: '已处理' }
]

const canLoad = computed(() => query.adminToken.trim().length > 0)
const canBatch = computed(() => selectedIds.value.length > 0 && batchForm.operator.trim().length > 0)
const totalPages = computed(() => Math.max(Math.ceil(state.total / query.pageSize), 1))

async function loadData() {
  if (!canLoad.value || loading.value) return
  loading.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const params = new URLSearchParams()
    if (query.contactMobile.trim()) params.set('contactMobile', query.contactMobile.trim())
    if (query.source) params.set('source', query.source)
    if (query.level) params.set('level', query.level)
    if (query.status) params.set('status', query.status)
    if (query.owner.trim()) params.set('owner', query.owner.trim())
    if (query.keyword.trim()) params.set('keyword', query.keyword.trim())
    params.set('highOnly', String(query.highOnly))
    params.set('page', String(query.page))
    params.set('pageSize', String(query.pageSize))

    const resp = await fetch(`/api/admin/risk-alert/tasks?${params.toString()}`, {
      headers: { 'X-Admin-Token': query.adminToken.trim() }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `加载失败(${resp.status})`)
    }
    const data = json.data || {}
    state.overview = data.overview || state.overview
    state.buckets = Array.isArray(data.buckets) ? data.buckets : []
    state.items = Array.isArray(data.items) ? data.items : []
    state.total = Number(data.total || 0)
    state.page = Number(data.page || query.page)
    state.pageSize = Number(data.pageSize || query.pageSize)
    selectedIds.value = []
  } catch (error) {
    errorMsg.value = error.message || '加载风险预警失败'
  } finally {
    loading.value = false
  }
}

function toggleSelect(alertId, checked) {
  if (checked) {
    if (!selectedIds.value.includes(alertId)) selectedIds.value.push(alertId)
    return
  }
  selectedIds.value = selectedIds.value.filter((id) => id !== alertId)
}

function toggleAll(checked) {
  if (checked) {
    selectedIds.value = state.items.map((item) => item.alertId)
    return
  }
  selectedIds.value = []
}

async function submitBatch() {
  if (!canBatch.value || updating.value) return
  updating.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      alertIds: selectedIds.value,
      status: batchForm.status,
      operator: batchForm.operator.trim(),
      remark: batchForm.remark.trim() || null
    }
    const resp = await fetch('/api/admin/risk-alert/tasks/status', {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'X-Admin-Token': query.adminToken.trim()
      },
      body: JSON.stringify(payload)
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `批量更新失败(${resp.status})`)
    }
    successMsg.value = `批量更新成功，共处理 ${selectedIds.value.length} 条`
    await loadData()
  } catch (error) {
    errorMsg.value = error.message || '批量处理失败'
  } finally {
    updating.value = false
  }
}

function severityTag(level) {
  if (level === 'HIGH') return '高风险'
  if (level === 'MEDIUM') return '中风险'
  return '低风险'
}

function prevPage() {
  if (query.page <= 1 || loading.value) return
  query.page -= 1
  loadData()
}

function nextPage() {
  if (query.page >= totalPages.value || loading.value) return
  query.page += 1
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<template>
  <main class="a08-page">
    <section class="card hero">
      <h1>A08 风险预警中心</h1>
      <p>聚合线索、提货、对账、账单风险信号，支持统一筛选、分层监控与批量处置。</p>
    </section>

    <section class="card">
      <h2>筛选条件</h2>
      <div class="filters">
        <label>
          Admin Token
          <input v-model="query.adminToken" placeholder="请输入X-Admin-Token" />
        </label>
        <label>
          联系手机号
          <input v-model="query.contactMobile" placeholder="支持11位手机号筛选" />
        </label>
        <label>
          业务来源
          <select v-model="query.source">
            <option v-for="op in sourceOptions" :key="op.value" :value="op.value">{{ op.label }}</option>
          </select>
        </label>
        <label>
          风险级别
          <select v-model="query.level">
            <option v-for="op in levelOptions" :key="op.value" :value="op.value">{{ op.label }}</option>
          </select>
        </label>
        <label>
          处理状态
          <select v-model="query.status">
            <option v-for="op in statusOptions" :key="op.value" :value="op.value">{{ op.label }}</option>
          </select>
        </label>
        <label>
          责任人
          <input v-model="query.owner" placeholder="责任人/商家名" />
        </label>
        <label class="span-2">
          关键词
          <input v-model="query.keyword" placeholder="业务单号/风险编码/标题" />
        </label>
      </div>
      <div class="actions">
        <label class="inline">
          <input v-model="query.highOnly" type="checkbox" />
          仅看高风险
        </label>
        <button class="btn btn--primary" :disabled="!canLoad || loading" @click="loadData">
          {{ loading ? '加载中...' : '查询预警' }}
        </button>
      </div>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
      <p v-if="successMsg" class="ok">{{ successMsg }}</p>
    </section>

    <section class="card">
      <h2>风险总览</h2>
      <div class="kpi-grid">
        <article class="kpi"><span>总预警数</span><strong>{{ state.overview.totalCount }}</strong></article>
        <article class="kpi"><span>高风险</span><strong>{{ state.overview.highCount }}</strong></article>
        <article class="kpi"><span>中风险</span><strong>{{ state.overview.mediumCount }}</strong></article>
        <article class="kpi"><span>低风险</span><strong>{{ state.overview.lowCount }}</strong></article>
        <article class="kpi"><span>待处理</span><strong>{{ state.overview.openCount }}</strong></article>
        <article class="kpi"><span>处理中</span><strong>{{ state.overview.processingCount }}</strong></article>
        <article class="kpi"><span>已处理</span><strong>{{ state.overview.resolvedCount }}</strong></article>
        <article class="kpi"><span>逾期类风险</span><strong>{{ state.overview.overdueCount }}</strong></article>
        <article class="kpi"><span>高风险占比</span><strong>{{ state.overview.highRate }}</strong></article>
        <article class="kpi"><span>处理闭环率</span><strong>{{ state.overview.resolvedRate }}</strong></article>
        <article class="kpi"><span>平均账龄(小时)</span><strong>{{ state.overview.avgAgingHours }}</strong></article>
      </div>
    </section>

    <section class="card">
      <h2>风险分桶</h2>
      <div class="bucket-grid">
        <article v-for="(bucket, idx) in state.buckets" :key="idx" class="bucket-item">
          <p>线索超时：{{ bucket.leadTimeoutCount }}</p>
          <p>提货逾期：{{ bucket.pickupOverdueCount }}</p>
          <p>对账逾期：{{ bucket.reconcileOverdueCount }}</p>
          <p>账单逾期：{{ bucket.billingOverdueCount }}</p>
          <p>高风险：{{ bucket.highSeverityCount }}</p>
          <p>中风险：{{ bucket.mediumSeverityCount }}</p>
          <p class="warn">未闭环：{{ bucket.unhandledCount }}</p>
        </article>
      </div>
    </section>

    <section class="card">
      <div class="head">
        <h2>预警任务列表</h2>
        <label class="inline">
          <input
            type="checkbox"
            :checked="state.items.length > 0 && selectedIds.length === state.items.length"
            @change="toggleAll($event.target.checked)"
          />
          全选
        </label>
      </div>
      <p v-if="loading">列表加载中...</p>
      <p v-else-if="state.items.length === 0">暂无风险任务</p>
      <ul v-else class="task-list">
        <li v-for="item in state.items" :key="item.alertId" class="task-item">
          <div class="line">
            <label class="inline">
              <input
                type="checkbox"
                :checked="selectedIds.includes(item.alertId)"
                @change="toggleSelect(item.alertId, $event.target.checked)"
              />
              {{ item.bizNo }}
            </label>
            <span class="badge">{{ item.sourceText }}</span>
            <span class="badge risk">{{ severityTag(item.severity) }}</span>
            <span class="badge">{{ item.statusText }}</span>
          </div>
          <p class="muted">
            {{ item.riskCode }} ｜ {{ item.riskTitle }} ｜ 责任人：{{ item.owner }} ｜ 账龄：{{ item.agingHours }}h
          </p>
          <p class="muted">{{ item.riskDetail }}</p>
          <p class="tip">建议动作：{{ item.suggestedAction }}</p>
        </li>
      </ul>
      <div class="pager">
        <button class="btn" :disabled="query.page <= 1 || loading" @click="prevPage">上一页</button>
        <span>第 {{ query.page }} / {{ totalPages }} 页</span>
        <button class="btn" :disabled="query.page >= totalPages || loading" @click="nextPage">下一页</button>
      </div>
    </section>

    <section class="card">
      <h2>批量处置</h2>
      <div class="batch-grid">
        <label>
          目标状态
          <select v-model="batchForm.status">
            <option value="OPEN">待处理</option>
            <option value="PROCESSING">处理中</option>
            <option value="RESOLVED">已处理</option>
          </select>
        </label>
        <label>
          操作人
          <input v-model="batchForm.operator" placeholder="如 a08-admin-ui" />
        </label>
      </div>
      <label>
        处置备注
        <textarea v-model="batchForm.remark" rows="2" placeholder="可选，记录处置动作"></textarea>
      </label>
      <div class="actions">
        <button class="btn btn--primary" :disabled="!canBatch || updating" @click="submitBatch">
          {{ updating ? '处理中...' : `批量更新(${selectedIds.length})` }}
        </button>
      </div>
    </section>
  </main>
</template>

<style scoped>
.a08-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px 16px 34px;
  display: grid;
  gap: 14px;
}
.card {
  border: 1px solid #fde7cc;
  border-radius: 12px;
  background: #fff;
  padding: 14px;
}
.hero {
  background: linear-gradient(135deg, #fff7ed, #ffedd5);
}
.hero h1 {
  margin: 0;
}
.hero p {
  margin: 8px 0 0;
  color: #7c2d12;
}
.filters {
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
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 9px 10px;
  font: inherit;
}
.actions {
  margin-top: 10px;
  display: flex;
  gap: 10px;
  align-items: center;
}
.inline {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}
.btn {
  border: 1px solid #ddd;
  border-radius: 8px;
  background: #fff;
  padding: 8px 13px;
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
  color: #067647;
}
.kpi-grid {
  display: grid;
  gap: 10px;
  grid-template-columns: repeat(6, minmax(0, 1fr));
}
.kpi {
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 10px;
}
.kpi span {
  display: block;
  color: #6b7280;
  font-size: 12px;
}
.kpi strong {
  font-size: 18px;
}
.bucket-grid {
  display: grid;
  gap: 10px;
  grid-template-columns: repeat(2, minmax(0, 1fr));
}
.bucket-item {
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  padding: 10px;
}
.warn {
  color: #b54708;
  font-weight: 600;
}
.head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}
.task-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: grid;
  gap: 10px;
}
.task-item {
  border: 1px solid #eee;
  border-radius: 10px;
  padding: 10px;
}
.line {
  display: flex;
  gap: 8px;
  align-items: center;
  margin-bottom: 6px;
}
.badge {
  font-size: 12px;
  border-radius: 999px;
  background: #f3f4f6;
  color: #111827;
  padding: 2px 8px;
}
.badge.risk {
  background: #ffedd5;
  color: #9a3412;
}
.muted {
  margin: 0;
  color: #4b5563;
}
.tip {
  margin: 6px 0 0;
  color: #6b7280;
}
.pager {
  margin-top: 10px;
  display: flex;
  justify-content: center;
  gap: 10px;
  align-items: center;
}
.batch-grid {
  display: grid;
  gap: 10px;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  margin-bottom: 8px;
}
@media (max-width: 960px) {
  .filters,
  .kpi-grid,
  .batch-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
  .bucket-grid {
    grid-template-columns: 1fr;
  }
}
</style>
