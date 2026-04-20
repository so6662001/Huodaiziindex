<script setup>
import { computed, onMounted, reactive, ref } from 'vue'

const loading = ref(false)
const batchLoading = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

const query = reactive({
  adminToken: 'test-admin-token',
  contactMobile: '13800138000',
  status: '',
  supplierName: '',
  keyword: '',
  riskOnly: false,
  page: 1,
  pageSize: 10
})

const state = reactive({
  overview: {
    totalOrders: 0,
    createdCount: 0,
    confirmedCount: 0,
    inTransitCount: 0,
    signedCount: 0,
    completedCount: 0,
    cancelledCount: 0,
    riskCount: 0,
    completionRate: '0.0%',
    inTransitRate: '0.0%',
    cancelledRate: '0.0%',
    avgAgingHours: '0'
  },
  riskBuckets: [],
  items: [],
  total: 0,
  page: 1,
  pageSize: 10
})

const selectedIds = ref([])

const statusOptions = [
  { value: '', label: '全部状态' },
  { value: 'CREATED', label: '待确认' },
  { value: 'CONFIRMED', label: '已确认' },
  { value: 'IN_TRANSIT', label: '运输中' },
  { value: 'SIGNED', label: '已签收' },
  { value: 'COMPLETED', label: '已完成' },
  { value: 'CANCELLED', label: '已取消' }
]

const batchForm = reactive({
  status: 'CONFIRMED',
  operator: 'a04-admin-ui',
  remark: ''
})

const canLoad = computed(() => query.adminToken.trim())
const canBatch = computed(() => query.contactMobile.trim() && selectedIds.value.length > 0)
const totalPages = computed(() => Math.max(Math.ceil(state.total / query.pageSize), 1))

async function loadData() {
  if (!canLoad.value || loading.value) return
  loading.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const params = new URLSearchParams()
    if (query.contactMobile.trim()) params.set('contactMobile', query.contactMobile.trim())
    if (query.status) params.set('status', query.status)
    if (query.supplierName.trim()) params.set('supplierName', query.supplierName.trim())
    if (query.keyword.trim()) params.set('keyword', query.keyword.trim())
    params.set('riskOnly', String(query.riskOnly))
    params.set('page', String(query.page))
    params.set('pageSize', String(query.pageSize))

    const resp = await fetch(`/api/admin/pickup-monitor/tasks?${params.toString()}`, {
      headers: { 'X-Admin-Token': query.adminToken.trim() }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `加载失败(${resp.status})`)
    }
    state.overview = json.data.overview || state.overview
    state.riskBuckets = json.data.riskBuckets || []
    state.items = json.data.items || []
    state.total = Number(json.data.total || 0)
    state.page = Number(json.data.page || query.page)
    state.pageSize = Number(json.data.pageSize || query.pageSize)
    selectedIds.value = []
  } catch (error) {
    errorMsg.value = error.message || '加载提货监控失败'
  } finally {
    loading.value = false
  }
}

function toggleSelect(id, checked) {
  if (checked) {
    if (!selectedIds.value.includes(id)) selectedIds.value.push(id)
    return
  }
  selectedIds.value = selectedIds.value.filter((x) => x !== id)
}

function toggleSelectAll(checked) {
  if (checked) {
    selectedIds.value = state.items.map((item) => item.pickupId)
    return
  }
  selectedIds.value = []
}

async function submitBatchStatus() {
  if (!canBatch.value || batchLoading.value) return
  batchLoading.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      contactMobile: query.contactMobile.trim(),
      pickupIds: selectedIds.value,
      status: batchForm.status,
      operator: batchForm.operator.trim() || null,
      remark: batchForm.remark.trim() || null
    }
    const resp = await fetch('/api/admin/pickup-monitor/tasks/status', {
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
    successMsg.value = `批量更新成功，共 ${json.data.total} 条`
    await loadData()
  } catch (error) {
    errorMsg.value = error.message || '批量状态更新失败'
  } finally {
    batchLoading.value = false
  }
}

function riskTag(level) {
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
  <main class="a04-page">
    <section class="card hero">
      <h1>A04 提货监控中心</h1>
      <p>监控提货单全流程状态、识别风险订单、支持运营批量处置，保障履约进度可视可控。</p>
    </section>

    <section class="card">
      <h2>查询筛选</h2>
      <div class="filters">
        <label>
          Admin Token
          <input v-model="query.adminToken" placeholder="请输入X-Admin-Token" />
        </label>
        <label>
          联系手机号
          <input v-model="query.contactMobile" placeholder="可选，11位手机号" />
        </label>
        <label>
          提货状态
          <select v-model="query.status">
            <option v-for="op in statusOptions" :key="op.value" :value="op.value">{{ op.label }}</option>
          </select>
        </label>
        <label>
          供应商
          <input v-model="query.supplierName" placeholder="供应商名称" />
        </label>
        <label>
          关键词
          <input v-model="query.keyword" placeholder="提货单号/询价单号/规格" />
        </label>
      </div>
      <div class="actions">
        <label class="inline">
          <input v-model="query.riskOnly" type="checkbox" />
          仅看风险单
        </label>
        <button class="btn btn--primary" :disabled="!canLoad || loading" @click="loadData">
          {{ loading ? '加载中...' : '查询监控' }}
        </button>
      </div>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
      <p v-if="successMsg" class="ok">{{ successMsg }}</p>
    </section>

    <section class="card">
      <h2>监控概览</h2>
      <div class="kpi-grid">
        <article class="kpi"><span>提货单总量</span><strong>{{ state.overview.totalOrders }}</strong></article>
        <article class="kpi"><span>待确认</span><strong>{{ state.overview.createdCount }}</strong></article>
        <article class="kpi"><span>运输中</span><strong>{{ state.overview.inTransitCount }}</strong></article>
        <article class="kpi"><span>已完成</span><strong>{{ state.overview.completedCount }}</strong></article>
        <article class="kpi"><span>风险单</span><strong>{{ state.overview.riskCount }}</strong></article>
        <article class="kpi"><span>完成率</span><strong>{{ state.overview.completionRate }}</strong></article>
        <article class="kpi"><span>在途占比</span><strong>{{ state.overview.inTransitRate }}</strong></article>
        <article class="kpi"><span>取消率</span><strong>{{ state.overview.cancelledRate }}</strong></article>
        <article class="kpi"><span>平均时长(小时)</span><strong>{{ state.overview.avgAgingHours }}</strong></article>
      </div>
    </section>

    <section class="card">
      <h2>风险分层</h2>
      <div class="risk-grid">
        <article v-for="(bucket, idx) in state.riskBuckets" :key="idx" class="risk-item">
          <p>当日待确认超12h：{{ bucket.todayPendingConfirmCount }}</p>
          <p>待确认超1天：{{ bucket.over1DayUnconfirmedCount }}</p>
          <p>在途超3天：{{ bucket.over3DayInTransitCount }}</p>
          <p class="warn">已取消：{{ bucket.cancelledCount }}</p>
        </article>
      </div>
    </section>

    <section class="card">
      <div class="head">
        <h2>提货单任务列表</h2>
        <label class="inline">
          <input
            type="checkbox"
            :checked="state.items.length > 0 && selectedIds.length === state.items.length"
            @change="toggleSelectAll($event.target.checked)"
          />
          全选
        </label>
      </div>

      <p v-if="loading">列表加载中...</p>
      <p v-else-if="state.items.length === 0">暂无提货任务</p>
      <ul v-else class="task-list">
        <li v-for="item in state.items" :key="item.pickupId" class="task-item">
          <div class="line">
            <label class="inline">
              <input
                type="checkbox"
                :checked="selectedIds.includes(item.pickupId)"
                @change="toggleSelect(item.pickupId, $event.target.checked)"
              />
              {{ item.pickupNo }}
            </label>
            <span class="badge">{{ item.statusText }}</span>
            <span class="badge risk">{{ riskTag(item.riskLevel) }}</span>
          </div>
          <p class="muted">
            询价单 {{ item.inquiryNo }} ｜ 供应商 {{ item.supplierName }} ｜ {{ item.goodsSummary }}
          </p>
          <p class="muted">
            提货日 {{ item.pickupDate }} ｜ 车牌 {{ item.truckNo }} ｜ 司机 {{ item.driverName }} ｜ 时长
            {{ item.agingHours }}h
          </p>
        </li>
      </ul>

      <div class="pager">
        <button class="btn" :disabled="query.page <= 1 || loading" @click="prevPage">上一页</button>
        <span>第 {{ query.page }} / {{ totalPages }} 页</span>
        <button class="btn" :disabled="query.page >= totalPages || loading" @click="nextPage">下一页</button>
      </div>
    </section>

    <section class="card">
      <h2>批量状态操作</h2>
      <div class="batch-grid">
        <label>
          目标状态
          <select v-model="batchForm.status">
            <option value="CONFIRMED">已确认</option>
            <option value="IN_TRANSIT">运输中</option>
            <option value="SIGNED">已签收</option>
            <option value="COMPLETED">已完成</option>
            <option value="CANCELLED">已取消</option>
          </select>
        </label>
        <label>
          操作人
          <input v-model="batchForm.operator" placeholder="如 a04-admin-ui" />
        </label>
      </div>
      <label>
        备注
        <textarea v-model="batchForm.remark" rows="2" placeholder="批量处理说明（可选）"></textarea>
      </label>
      <div class="actions">
        <button class="btn btn--primary" :disabled="!canBatch || batchLoading" @click="submitBatchStatus">
          {{ batchLoading ? '处理中...' : `批量更新(${selectedIds.length})` }}
        </button>
      </div>
    </section>
  </main>
</template>

<style scoped>
.a04-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px 16px 32px;
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
  margin: 0 0 6px;
  color: #b45309;
}

.hero p {
  margin: 0;
  color: #9a3412;
}

.filters,
.batch-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 10px;
}

label {
  display: grid;
  gap: 6px;
  font-size: 13px;
  color: #6b7280;
}

input,
select,
textarea {
  border: 1px solid #fdba74;
  border-radius: 8px;
  padding: 8px 10px;
  font-size: 14px;
  outline: none;
}

input:focus,
select:focus,
textarea:focus {
  border-color: #f97316;
}

.actions {
  margin-top: 10px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.inline {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.btn {
  border: 1px solid #fb923c;
  background: #fff;
  color: #9a3412;
  border-radius: 8px;
  padding: 8px 12px;
  cursor: pointer;
}

.btn--primary {
  background: #f97316;
  color: #fff;
}

.btn:disabled {
  cursor: not-allowed;
  opacity: 0.6;
}

.error {
  margin-top: 8px;
  color: #dc2626;
}

.ok {
  margin-top: 8px;
  color: #059669;
}

.kpi-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  gap: 10px;
}

.kpi {
  border: 1px solid #fed7aa;
  border-radius: 10px;
  padding: 10px;
  background: #fff7ed;
  display: grid;
  gap: 6px;
}

.kpi span {
  font-size: 12px;
  color: #9a3412;
}

.kpi strong {
  font-size: 20px;
  color: #ea580c;
}

.risk-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 10px;
}

.risk-item {
  border: 1px solid #ffedd5;
  border-radius: 10px;
  background: #fffaf5;
  padding: 10px;
}

.risk-item p {
  margin: 0 0 6px;
  color: #7c2d12;
}

.risk-item p.warn {
  color: #b91c1c;
}

.head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.head h2 {
  margin: 0;
}

.task-list {
  margin: 10px 0 0;
  padding: 0;
  list-style: none;
  display: grid;
  gap: 10px;
}

.task-item {
  border: 1px solid #fed7aa;
  border-radius: 10px;
  padding: 10px;
  background: #fff;
}

.line {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.badge {
  border: 1px solid #f97316;
  color: #c2410c;
  border-radius: 999px;
  padding: 2px 8px;
  font-size: 12px;
}

.badge.risk {
  border-color: #dc2626;
  color: #dc2626;
}

.muted {
  margin: 6px 0 0;
  color: #6b7280;
  font-size: 13px;
}

.pager {
  margin-top: 10px;
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
