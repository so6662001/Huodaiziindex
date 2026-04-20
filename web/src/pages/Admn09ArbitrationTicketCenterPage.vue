<script setup>
import { computed, onMounted, reactive, ref } from 'vue'

const loading = ref(false)
const assigning = ref(false)
const reviewing = ref(false)
const errorMsg = ref('')
const successMsg = ref('')
const selected = ref(null)

const query = reactive({
  adminToken: 'test-admin-token',
  arbitrationStatus: '',
  priorityLevel: '',
  city: '',
  assignedArbitrator: '',
  keyword: '',
  page: 1,
  pageSize: 10
})

const state = reactive({
  records: [],
  total: 0,
  page: 1,
  pageSize: 10,
  pendingCount: 0,
  processingCount: 0,
  resolvedCount: 0,
  closedCount: 0
})

const assignForm = reactive({
  action: 'ACCEPT',
  assignedArbitrator: '仲裁员-高阳',
  priorityLevel: 'MEDIUM',
  handleRemark: '',
  operator: 'admn09-dispatcher'
})

const reviewForm = reactive({
  action: 'MEDIATION',
  resolutionSummary: '',
  resolutionDetail: '',
  operator: 'admn09-judge'
})

const canLoad = computed(() => query.adminToken.trim().length > 0)
const canAssign = computed(() => selected.value && assignForm.action.trim() && !assigning.value)
const canReview = computed(() => selected.value && reviewForm.action.trim() && !reviewing.value)
const totalPages = computed(() => Math.max(Math.ceil(state.total / query.pageSize), 1))

const statusOptions = [
  { value: '', label: '全部仲裁状态' },
  { value: 'PENDING_ASSIGN', label: '待分派' },
  { value: 'PROCESSING', label: '仲裁处理中' },
  { value: 'RESOLVED', label: '已裁决' },
  { value: 'CLOSED', label: '已归档' }
]

const priorityOptions = [
  { value: '', label: '全部优先级' },
  { value: 'LOW', label: '低优先级' },
  { value: 'MEDIUM', label: '中优先级' },
  { value: 'HIGH', label: '高优先级' },
  { value: 'URGENT', label: '紧急' }
]

async function loadTickets() {
  if (!canLoad.value || loading.value) return
  loading.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const params = new URLSearchParams()
    if (query.arbitrationStatus) params.set('arbitrationStatus', query.arbitrationStatus)
    if (query.priorityLevel) params.set('priorityLevel', query.priorityLevel)
    if (query.city.trim()) params.set('city', query.city.trim())
    if (query.assignedArbitrator.trim()) params.set('assignedArbitrator', query.assignedArbitrator.trim())
    if (query.keyword.trim()) params.set('keyword', query.keyword.trim())
    params.set('page', String(query.page))
    params.set('pageSize', String(query.pageSize))
    const resp = await fetch(`/api/admin/arbitration-tickets?${params.toString()}`, {
      headers: { 'X-Admin-Token': query.adminToken.trim() }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `仲裁工单加载失败(${resp.status})`)
    }
    const data = json.data || {}
    state.records = Array.isArray(data.records) ? data.records : []
    state.total = Number(data.total || 0)
    state.page = Number(data.page || query.page)
    state.pageSize = Number(data.pageSize || query.pageSize)
    state.pendingCount = Number(data.pendingCount || 0)
    state.processingCount = Number(data.processingCount || 0)
    state.resolvedCount = Number(data.resolvedCount || 0)
    state.closedCount = Number(data.closedCount || 0)
    if (selected.value && !state.records.some((item) => item.ticketId === selected.value.ticketId)) {
      selected.value = null
    }
  } catch (error) {
    errorMsg.value = error.message || '仲裁工单加载失败'
  } finally {
    loading.value = false
  }
}

async function openDetail(row) {
  if (!row?.ticketId || loading.value) return
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const resp = await fetch(`/api/admin/arbitration-tickets/${row.ticketId}`, {
      headers: { 'X-Admin-Token': query.adminToken.trim() }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `仲裁工单详情加载失败(${resp.status})`)
    }
    selected.value = json.data || null
    assignForm.priorityLevel = selected.value?.priorityLevel || 'MEDIUM'
  } catch (error) {
    errorMsg.value = error.message || '仲裁工单详情加载失败'
  }
}

async function submitAssign() {
  if (!canAssign.value) return
  assigning.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      action: assignForm.action,
      assignedArbitrator: assignForm.assignedArbitrator.trim() || null,
      priorityLevel: assignForm.priorityLevel || null,
      handleRemark: assignForm.handleRemark.trim() || null,
      operator: assignForm.operator.trim() || 'admn09-dispatcher'
    }
    const resp = await fetch(`/api/admin/arbitration-tickets/${selected.value.ticketId}/assign`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'X-Admin-Token': query.adminToken.trim()
      },
      body: JSON.stringify(payload)
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `仲裁分派失败(${resp.status})`)
    }
    selected.value = json.data || null
    assignForm.handleRemark = ''
    successMsg.value = '仲裁工单分派成功'
    await loadTickets()
  } catch (error) {
    errorMsg.value = error.message || '仲裁分派失败'
  } finally {
    assigning.value = false
  }
}

async function submitReview() {
  if (!canReview.value) return
  reviewing.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      action: reviewForm.action,
      resolutionSummary: reviewForm.resolutionSummary.trim() || null,
      resolutionDetail: reviewForm.resolutionDetail.trim() || null,
      operator: reviewForm.operator.trim() || 'admn09-judge'
    }
    const resp = await fetch(`/api/admin/arbitration-tickets/${selected.value.ticketId}/review`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Admin-Token': query.adminToken.trim()
      },
      body: JSON.stringify(payload)
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `仲裁裁决失败(${resp.status})`)
    }
    selected.value = json.data || null
    reviewForm.resolutionSummary = ''
    reviewForm.resolutionDetail = ''
    successMsg.value = '仲裁裁决处理成功'
    await loadTickets()
  } catch (error) {
    errorMsg.value = error.message || '仲裁裁决失败'
  } finally {
    reviewing.value = false
  }
}

function prevPage() {
  if (query.page <= 1 || loading.value) return
  query.page -= 1
  loadTickets()
}

function nextPage() {
  if (query.page >= totalPages.value || loading.value) return
  query.page += 1
  loadTickets()
}

onMounted(() => {
  loadTickets()
})
</script>

<template>
  <main class="admn09-page">
    <section class="card hero">
      <h1>ADM-N09 仲裁工单中心</h1>
      <p>统一处理售后争议升级仲裁的分派、裁决与归档流程，形成工单闭环与裁决留痕。</p>
    </section>

    <section class="card">
      <div class="filters">
        <label>
          Admin Token
          <input v-model="query.adminToken" placeholder="请输入X-Admin-Token" />
        </label>
        <label>
          仲裁状态
          <select v-model="query.arbitrationStatus">
            <option v-for="op in statusOptions" :key="op.value" :value="op.value">{{ op.label }}</option>
          </select>
        </label>
        <label>
          优先级
          <select v-model="query.priorityLevel">
            <option v-for="op in priorityOptions" :key="op.value" :value="op.value">{{ op.label }}</option>
          </select>
        </label>
        <label>
          城市
          <input v-model="query.city" placeholder="支持城市模糊匹配" />
        </label>
        <label>
          仲裁员
          <input v-model="query.assignedArbitrator" placeholder="支持仲裁员模糊匹配" />
        </label>
        <label class="span-2">
          关键词
          <input v-model="query.keyword" placeholder="ticketId/disputeId/订单号/企业名/结论" />
        </label>
      </div>
      <div class="actions">
        <button class="btn btn--primary" :disabled="!canLoad || loading" @click="loadTickets">
          {{ loading ? '加载中...' : '查询仲裁工单' }}
        </button>
      </div>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
      <p v-if="successMsg" class="ok">{{ successMsg }}</p>
    </section>

    <section class="stats">
      <article class="stat"><span>工单总数</span><strong>{{ state.total }}</strong></article>
      <article class="stat"><span>待分派</span><strong>{{ state.pendingCount }}</strong></article>
      <article class="stat"><span>处理中</span><strong>{{ state.processingCount }}</strong></article>
      <article class="stat"><span>已裁决</span><strong>{{ state.resolvedCount }}</strong></article>
      <article class="stat"><span>已归档</span><strong>{{ state.closedCount }}</strong></article>
    </section>

    <section class="card">
      <h2>工单列表</h2>
      <p v-if="loading">加载中...</p>
      <p v-else-if="state.records.length === 0">暂无仲裁工单</p>
      <ul v-else class="list">
        <li v-for="row in state.records" :key="row.ticketId" class="item">
          <div>
            <h3>{{ row.ticketId }} / {{ row.issueTypeText }}</h3>
            <p>争议单：{{ row.disputeId }} ｜ 订单：{{ row.orderNo }} ｜ 询价：{{ row.inquiryNo }}</p>
            <p>城市：{{ row.city }} ｜ 买方：{{ row.buyerCompany }} ｜ 卖方：{{ row.supplierName }}</p>
            <p>状态：{{ row.arbitrationStatusText }} ｜ 优先级：{{ row.priorityLevelText }}</p>
            <p>仲裁员：{{ row.assignedArbitrator || '-' }} ｜ 开庭：{{ row.hearingAt || '-' }}</p>
            <p>结论：{{ row.latestConclusion || '-' }}</p>
            <p>更新时间：{{ row.updatedAt || '-' }}</p>
          </div>
          <button class="btn" @click="openDetail(row)">查看详情</button>
        </li>
      </ul>
      <div class="actions" v-if="state.total > 0">
        <button class="btn" :disabled="query.page <= 1 || loading" @click="prevPage">上一页</button>
        <button class="btn" :disabled="query.page >= totalPages || loading" @click="nextPage">下一页</button>
        <span>第 {{ query.page }} 页 / 共 {{ totalPages }} 页</span>
      </div>
    </section>

    <section class="card" v-if="selected">
      <h2>工单详情</h2>
      <p>工单号：{{ selected.ticketId }} ｜ 争议单号：{{ selected.disputeId }}</p>
      <p>订单：{{ selected.orderNo }}（{{ selected.orderId }}） ｜ 询价：{{ selected.inquiryNo }}</p>
      <p>买方：{{ selected.buyerCompany }} ｜ 卖方：{{ selected.supplierName }}</p>
      <p>问题类型：{{ selected.issueTypeText }} ｜ 状态：{{ selected.arbitrationStatusText }}</p>
      <p>争议原状态：{{ selected.sourceStatusText }} ｜ 当前阶段：{{ selected.currentStage }} ｜ 进度：{{ selected.progressPercent }}%</p>
      <p>优先级：{{ selected.priorityLevelText }} ｜ 仲裁员：{{ selected.assignedArbitrator || '-' }}</p>
      <p>问题摘要：{{ selected.issueSummary }}</p>
      <p>问题描述：{{ selected.issueDescription }}</p>
      <p>期望解决方案：{{ selected.expectedResolution || '-' }}</p>
      <p>联系人：{{ selected.contactName || '-' }} ｜ 手机：{{ selected.contactPhoneMasked || '-' }}</p>
      <p>证据文件：{{ selected.evidenceFiles || '-' }}</p>
      <p>最新备注：{{ selected.latestRemark || '-' }}</p>
      <p>可用动作：{{ (selected.availableActions || []).join(' / ') || '-' }}</p>
      <p>创建时间：{{ selected.createdAt || '-' }} ｜ 更新时间：{{ selected.updatedAt || '-' }}</p>
    </section>

    <section class="card" v-if="selected">
      <h2>分派处理</h2>
      <div class="form-grid">
        <label>
          分派动作
          <select v-model="assignForm.action">
            <option value="ACCEPT">ACCEPT 接单</option>
            <option value="TRANSFER">TRANSFER 转派</option>
            <option value="START_REVIEW">START_REVIEW 启动审理</option>
          </select>
        </label>
        <label>
          仲裁员
          <input v-model="assignForm.assignedArbitrator" placeholder="示例：仲裁员-高阳" />
        </label>
        <label>
          优先级
          <select v-model="assignForm.priorityLevel">
            <option value="LOW">LOW</option>
            <option value="MEDIUM">MEDIUM</option>
            <option value="HIGH">HIGH</option>
            <option value="URGENT">URGENT</option>
          </select>
        </label>
        <label>
          操作人
          <input v-model="assignForm.operator" placeholder="admn09-dispatcher" />
        </label>
        <label class="span-2">
          处理备注
          <textarea v-model="assignForm.handleRemark" rows="2" placeholder="请输入分派处理备注" />
        </label>
      </div>
      <div class="actions">
        <button class="btn btn--primary" :disabled="!canAssign" @click="submitAssign">
          {{ assigning ? '提交中...' : '提交分派处理' }}
        </button>
      </div>
    </section>

    <section class="card" v-if="selected">
      <h2>仲裁裁决</h2>
      <div class="form-grid">
        <label>
          裁决动作
          <select v-model="reviewForm.action">
            <option value="SUPPORT_BUYER">SUPPORT_BUYER 支持买方</option>
            <option value="SUPPORT_SUPPLIER">SUPPORT_SUPPLIER 支持卖方</option>
            <option value="MEDIATION">MEDIATION 调解</option>
            <option value="CLOSE_NO_FAULT">CLOSE_NO_FAULT 无责归档</option>
            <option value="REOPEN">REOPEN 重启仲裁</option>
          </select>
        </label>
        <label>
          操作人
          <input v-model="reviewForm.operator" placeholder="admn09-judge" />
        </label>
        <label class="span-2">
          裁决摘要
          <input v-model="reviewForm.resolutionSummary" placeholder="请输入裁决摘要" />
        </label>
        <label class="span-2">
          裁决详情
          <textarea v-model="reviewForm.resolutionDetail" rows="3" placeholder="请输入裁决详情" />
        </label>
      </div>
      <div class="actions">
        <button class="btn btn--primary" :disabled="!canReview" @click="submitReview">
          {{ reviewing ? '提交中...' : '提交仲裁裁决' }}
        </button>
      </div>
    </section>

    <section class="card" v-if="selected">
      <h2>仲裁时间线</h2>
      <ul class="timeline">
        <li v-for="node in selected.progressNodes || []" :key="node.nodeCode">
          <div class="line-1">
            <strong>{{ node.nodeName }}</strong>
            <span>{{ node.statusText }}</span>
          </div>
          <p>状态：{{ node.status }} ｜ 处理人：{{ node.handler || '-' }}</p>
          <p>时间：{{ node.happenedAt || '-' }}</p>
          <p>备注：{{ node.remark || '-' }}</p>
        </li>
      </ul>
    </section>
  </main>
</template>

<style scoped>
.admn09-page {
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
.filters,
.form-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
}
.span-2 {
  grid-column: span 2;
}
label {
  display: grid;
  gap: 6px;
}
input,
select,
textarea {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 9px 10px;
  font: inherit;
}
.actions {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 10px;
  flex-wrap: wrap;
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
.stats {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
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
  display: flex;
  justify-content: space-between;
  gap: 10px;
}
.item h3 {
  margin: 0 0 8px;
}
.item p {
  margin: 4px 0;
  color: #4b5563;
}
.timeline {
  list-style: none;
  margin: 0;
  padding: 0;
  display: grid;
  gap: 8px;
}
.timeline li {
  border: 1px solid #ececec;
  border-radius: 8px;
  padding: 8px;
}
.line-1 {
  display: flex;
  justify-content: space-between;
  gap: 8px;
}
@media (max-width: 960px) {
  .filters,
  .form-grid,
  .stats {
    grid-template-columns: 1fr;
  }
  .span-2 {
    grid-column: span 1;
  }
  .item {
    flex-direction: column;
  }
}
</style>
