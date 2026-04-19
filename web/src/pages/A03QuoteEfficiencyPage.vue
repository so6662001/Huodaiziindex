<script setup>
import { computed, onMounted, reactive, ref } from 'vue'

const loading = ref(false)
const opLoading = ref(false)
const errorMsg = ref('')
const opMsg = ref('')

const query = reactive({
  adminToken: 'test-admin-token',
  merchantId: 'S001',
  status: '',
  keyword: '',
  city: '',
  quoteTimeoutOnly: false,
  sortBy: 'AGE_DESC',
  page: 1,
  pageSize: 10
})

const state = reactive({
  overview: {
    totalLeads: 0,
    waitingQuoteCount: 0,
    timeoutCount: 0,
    quotedCount: 0,
    wonCount: 0,
    lostCount: 0,
    quoteRate: '0.0%',
    winRate: '0.0%',
    avgResponseMinutes: '0'
  },
  agingBuckets: [],
  items: [],
  total: 0,
  page: 1,
  pageSize: 10
})

const selectedLeadIds = ref([])
const selectedTask = ref(null)

const statusOptions = [
  { value: '', label: '全部状态' },
  { value: 'NEW', label: '待报价' },
  { value: 'CONTACTED', label: '已联系' },
  { value: 'FOLLOWING', label: '跟进中' },
  { value: 'QUOTED', label: '已报价' },
  { value: 'WON', label: '已赢单' },
  { value: 'LOST', label: '已丢单' },
  { value: 'CLOSED', label: '已关闭' }
]

const batchForm = reactive({
  status: 'CONTACTED',
  operator: 'a03-admin-ui',
  comment: ''
})

const quoteForm = reactive({
  merchantId: 'S001',
  supplierName: '',
  unitPrice: '',
  deliveryDays: '',
  paymentTerm: '月结30天',
  quoteRemark: '',
  operator: 'a03-admin-ui'
})

const canLoad = computed(() => query.adminToken.trim() && query.merchantId.trim())
const hasSelection = computed(() => selectedLeadIds.value.length > 0)
const canQuote = computed(() => {
  return (
    selectedTask.value &&
    query.merchantId.trim() &&
    quoteForm.supplierName.trim() &&
    Number(quoteForm.unitPrice) > 0 &&
    Number(quoteForm.deliveryDays) >= 0
  )
})

function sourceText(item) {
  return item.hasQuoted ? '已有报价' : '待报价'
}

async function loadData() {
  if (!canLoad.value || loading.value) return
  loading.value = true
  errorMsg.value = ''
  opMsg.value = ''
  try {
    const params = new URLSearchParams()
    params.set('merchantId', query.merchantId.trim())
    if (query.status) params.set('status', query.status)
    if (query.keyword.trim()) params.set('keyword', query.keyword.trim())
    if (query.city.trim()) params.set('city', query.city.trim())
    params.set('quoteTimeoutOnly', String(query.quoteTimeoutOnly))
    if (query.sortBy) params.set('sortBy', query.sortBy)
    params.set('page', String(query.page))
    params.set('pageSize', String(query.pageSize))
    const resp = await fetch(`/api/admin/quote-efficiency/tasks?${params.toString()}`, {
      headers: { 'X-Admin-Token': query.adminToken.trim() }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `加载失败(${resp.status})`)
    }
    state.overview = json.data.overview || state.overview
    state.agingBuckets = json.data.agingBuckets || []
    state.items = json.data.items || []
    state.total = Number(json.data.total || 0)
    state.page = Number(json.data.page || query.page)
    state.pageSize = Number(json.data.pageSize || query.pageSize)
    selectedLeadIds.value = []
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    loading.value = false
  }
}

function toggleSelect(leadId, checked) {
  if (checked) {
    if (!selectedLeadIds.value.includes(leadId)) {
      selectedLeadIds.value.push(leadId)
    }
    return
  }
  selectedLeadIds.value = selectedLeadIds.value.filter((id) => id !== leadId)
}

function toggleSelectAll(checked) {
  if (checked) {
    selectedLeadIds.value = state.items.map((item) => item.leadId)
    return
  }
  selectedLeadIds.value = []
}

function chooseTask(item) {
  selectedTask.value = item
  quoteForm.merchantId = query.merchantId
  quoteForm.supplierName = item.merchantName || ''
  quoteForm.unitPrice = ''
  quoteForm.deliveryDays = ''
  quoteForm.quoteRemark = ''
}

async function submitBatchStatus() {
  if (!hasSelection.value) return
  opLoading.value = true
  errorMsg.value = ''
  opMsg.value = ''
  try {
    const payload = {
      merchantId: query.merchantId.trim(),
      leadIds: selectedLeadIds.value,
      status: batchForm.status,
      operator: batchForm.operator,
      comment: batchForm.comment.trim() || null
    }
    const resp = await fetch('/api/admin/quote-efficiency/tasks/status', {
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
    opMsg.value = `批量更新完成，共 ${json.data.total} 条`
    await loadData()
  } catch (error) {
    errorMsg.value = error.message || '批量更新失败'
  } finally {
    opLoading.value = false
  }
}

async function submitQuickQuote() {
  if (!canQuote.value) return
  opLoading.value = true
  errorMsg.value = ''
  opMsg.value = ''
  try {
    const payload = {
      merchantId: quoteForm.merchantId.trim(),
      supplierName: quoteForm.supplierName.trim(),
      unitPrice: String(Number(quoteForm.unitPrice)),
      deliveryDays: String(Number(quoteForm.deliveryDays)),
      paymentTerm: quoteForm.paymentTerm.trim() || '月结30天',
      quoteRemark: quoteForm.quoteRemark.trim() || null,
      operator: quoteForm.operator
    }
    const resp = await fetch(`/api/admin/quote-efficiency/tasks/${selectedTask.value.leadId}/quick-quote`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Admin-Token': query.adminToken.trim()
      },
      body: JSON.stringify(payload)
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `快捷报价失败(${resp.status})`)
    }
    selectedTask.value = json.data
    opMsg.value = '快捷报价成功'
    await loadData()
  } catch (error) {
    errorMsg.value = error.message || '快捷报价失败'
  } finally {
    opLoading.value = false
  }
}

function prevPage() {
  if (query.page <= 1 || loading.value) return
  query.page -= 1
  loadData()
}

function nextPage() {
  if (query.page * query.pageSize >= state.total || loading.value) return
  query.page += 1
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<template>
  <main class="a03-page">
    <section class="card hero">
      <h1>A03 报价效率中心</h1>
      <p>聚焦报价时效、超时任务与批量处理，提升商家线索响应效率和赢单转化。</p>
    </section>

    <section class="card">
      <div class="filters">
        <label>
          Admin Token
          <input v-model="query.adminToken" placeholder="请输入X-Admin-Token" />
        </label>
        <label>
          商家ID
          <input v-model="query.merchantId" placeholder="如 S001" />
        </label>
        <label>
          状态
          <select v-model="query.status">
            <option v-for="op in statusOptions" :key="op.value" :value="op.value">{{ op.label }}</option>
          </select>
        </label>
        <label>
          交付城市
          <input v-model="query.city" placeholder="如 唐山/无锡" />
        </label>
        <label>
          关键词
          <input v-model="query.keyword" placeholder="询价单号/规格/采购方" />
        </label>
        <label>
          排序
          <select v-model="query.sortBy">
            <option value="AGE_DESC">时效降序</option>
            <option value="AGE_ASC">时效升序</option>
            <option value="UPDATED_DESC">更新时间降序</option>
          </select>
        </label>
      </div>
      <div class="actions">
        <label class="inline">
          <input v-model="query.quoteTimeoutOnly" type="checkbox" />
          仅看超时任务
        </label>
        <button class="btn btn--primary" :disabled="!canLoad || loading" @click="loadData">
          {{ loading ? '加载中...' : '查询任务' }}
        </button>
      </div>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
      <p v-if="opMsg" class="ok">{{ opMsg }}</p>
    </section>

    <section class="card">
      <h2>效率概览</h2>
      <div class="kpi-grid">
        <article class="kpi"><span>线索总量</span><strong>{{ state.overview.totalLeads }}</strong></article>
        <article class="kpi"><span>待报价</span><strong>{{ state.overview.waitingQuoteCount }}</strong></article>
        <article class="kpi"><span>超时任务</span><strong>{{ state.overview.timeoutCount }}</strong></article>
        <article class="kpi"><span>已报价</span><strong>{{ state.overview.quotedCount }}</strong></article>
        <article class="kpi"><span>已赢单</span><strong>{{ state.overview.wonCount }}</strong></article>
        <article class="kpi"><span>已丢单</span><strong>{{ state.overview.lostCount }}</strong></article>
        <article class="kpi"><span>报价率</span><strong>{{ state.overview.quoteRate }}</strong></article>
        <article class="kpi"><span>赢单率</span><strong>{{ state.overview.winRate }}</strong></article>
        <article class="kpi"><span>平均响应(分钟)</span><strong>{{ state.overview.avgResponseMinutes }}</strong></article>
      </div>
    </section>

    <section class="card">
      <h2>时效分层（分钟）</h2>
      <div class="aging-grid">
        <article v-for="(bucket, idx) in state.agingBuckets" :key="idx" class="aging-item">
          <p>10分钟内：{{ bucket.within10Minutes }}</p>
          <p>10-30分钟：{{ bucket.within30Minutes }}</p>
          <p>30-60分钟：{{ bucket.within60Minutes }}</p>
          <p>60分钟以上：{{ bucket.over60Minutes }}</p>
          <p class="warn">超时任务：{{ bucket.timeoutCount }}</p>
        </article>
      </div>
    </section>

    <section class="card">
      <div class="head">
        <h2>报价任务列表</h2>
        <label class="inline">
          <input
            type="checkbox"
            :checked="state.items.length > 0 && selectedLeadIds.length === state.items.length"
            @change="toggleSelectAll($event.target.checked)"
          />
          全选
        </label>
      </div>
      <p v-if="loading">任务加载中...</p>
      <p v-else-if="state.items.length === 0">暂无任务</p>
      <ul v-else class="task-list">
        <li v-for="item in state.items" :key="item.leadId" class="task-item">
          <div class="line">
            <label class="inline">
              <input
                type="checkbox"
                :checked="selectedLeadIds.includes(item.leadId)"
                @change="toggleSelect(item.leadId, $event.target.checked)"
              />
              <strong>{{ item.specText }}</strong>
            </label>
            <span class="status">{{ item.status }}</span>
          </div>
          <p>询价单：{{ item.inquiryNo }} ｜ 城市：{{ item.deliveryCity }} ｜ 数量：{{ item.demandQtyTon }} 吨</p>
          <p>时效：{{ item.quoteAgeMinutes }} 分钟（{{ item.efficiencyLevel }}） ｜ {{ sourceText(item) }}</p>
          <p>备注：{{ item.latestRemark || '-' }}</p>
          <div class="actions">
            <button class="btn" @click="chooseTask(item)">选择快捷报价</button>
          </div>
        </li>
      </ul>

      <div v-if="state.total > 0" class="actions">
        <button class="btn" :disabled="query.page <= 1 || loading" @click="prevPage">上一页</button>
        <button class="btn" :disabled="query.page * query.pageSize >= state.total || loading" @click="nextPage">
          下一页
        </button>
        <span class="tip">第 {{ query.page }} 页 / 共 {{ Math.max(Math.ceil(state.total / query.pageSize), 1) }} 页</span>
      </div>
    </section>

    <section class="card">
      <h2>批量状态处理</h2>
      <p>已选任务：{{ selectedLeadIds.length }}</p>
      <div class="filters">
        <label>
          目标状态
          <select v-model="batchForm.status">
            <option value="CONTACTED">标记已联系</option>
            <option value="FOLLOWING">标记跟进中</option>
            <option value="QUOTED">标记已报价</option>
            <option value="WON">标记已赢单</option>
            <option value="LOST">标记已丢单</option>
            <option value="CLOSED">标记已关闭</option>
          </select>
        </label>
        <label>
          备注
          <input v-model="batchForm.comment" placeholder="如：今日统一外呼跟进" />
        </label>
      </div>
      <div class="actions">
        <button class="btn btn--primary" :disabled="!hasSelection || opLoading" @click="submitBatchStatus">
          批量更新状态
        </button>
      </div>
    </section>

    <section class="card" v-if="selectedTask">
      <h2>快捷报价</h2>
      <p class="tip">当前线索：{{ selectedTask.leadNo }} ｜ {{ selectedTask.specText }} ｜ {{ selectedTask.demandQtyTon }} 吨</p>
      <div class="filters">
        <label>
          商家ID
          <input v-model="quoteForm.merchantId" />
        </label>
        <label>
          供应商名称
          <input v-model="quoteForm.supplierName" />
        </label>
        <label>
          单价(元/吨)
          <input v-model="quoteForm.unitPrice" type="number" min="0" />
        </label>
        <label>
          交期(天)
          <input v-model="quoteForm.deliveryDays" type="number" min="0" />
        </label>
        <label>
          账期
          <input v-model="quoteForm.paymentTerm" />
        </label>
        <label>
          报价备注
          <input v-model="quoteForm.quoteRemark" />
        </label>
      </div>
      <div class="actions">
        <button class="btn btn--primary" :disabled="opLoading || !canQuote" @click="submitQuickQuote">提交快捷报价</button>
      </div>
    </section>
  </main>
</template>

<style scoped>
.a03-page {
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
input,
select {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 9px 10px;
  font: inherit;
}
.actions {
  display: flex;
  gap: 10px;
  margin-top: 12px;
  flex-wrap: wrap;
  align-items: center;
}
.inline {
  display: inline-flex;
  gap: 6px;
  align-items: center;
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
.error {
  color: #b42318;
  margin-top: 8px;
}
.ok {
  color: #0c7a43;
  margin-top: 8px;
}
.tip {
  color: #6b7280;
}
.warn {
  color: #b42318;
}
.kpi-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
}
.kpi {
  border: 1px solid #ececec;
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
  font-size: 22px;
  color: #111827;
}
.aging-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
}
.aging-item {
  border: 1px solid #ececec;
  border-radius: 10px;
  padding: 10px;
}
.aging-item p {
  margin: 6px 0;
}
.head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
}
.task-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: grid;
  gap: 10px;
}
.task-item {
  border: 1px solid #ececec;
  border-radius: 10px;
  padding: 10px;
}
.task-item p {
  margin: 6px 0;
}
.line {
  display: flex;
  justify-content: space-between;
  gap: 8px;
  align-items: center;
}
.status {
  border-radius: 999px;
  background: #fff4e5;
  color: #9a4a00;
  font-size: 12px;
  padding: 2px 10px;
}
@media (max-width: 900px) {
  .filters,
  .kpi-grid,
  .aging-grid {
    grid-template-columns: 1fr;
  }
}
</style>
