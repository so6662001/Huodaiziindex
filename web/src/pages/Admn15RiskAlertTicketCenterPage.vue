<script setup>
import { computed, onMounted, reactive, ref } from 'vue'

const loading = ref(false)
const detailLoading = ref(false)
const submitting = ref(false)
const errorMsg = ref('')
const successMsg = ref('')
const selectedTicketId = ref('')
const detail = ref(null)

const query = reactive({
  adminToken: 'test-admin-token',
  ticketStatus: '',
  riskLevel: '',
  sourceType: '',
  owner: '',
  keyword: '',
  page: 1,
  pageSize: 10
})

const state = reactive({
  total: 0,
  page: 1,
  pageSize: 10,
  pendingCount: 0,
  processingCount: 0,
  escalatedCount: 0,
  resolvedCount: 0,
  closedCount: 0,
  records: []
})

const handleForm = reactive({
  action: 'START_PROCESS',
  targetStatus: 'PROCESSING',
  owner: '风控值班组',
  followUpPlan: '',
  solution: '',
  remark: '',
  operator: 'admn15-operator-ui'
})

const canLoad = computed(() => query.adminToken.trim().length > 0)
const canSubmit = computed(() => {
  return (
    query.adminToken.trim() &&
    selectedTicketId.value &&
    handleForm.action.trim() &&
    handleForm.operator.trim() &&
    !submitting.value
  )
})
const totalPages = computed(() => Math.max(Math.ceil(state.total / query.pageSize), 1))

const statusOptions = [
  { value: '', label: '全部状态' },
  { value: 'OPEN', label: '待处理' },
  { value: 'PROCESSING', label: '处理中' },
  { value: 'ESCALATED', label: '升级处理中' },
  { value: 'RESOLVED', label: '已处理' },
  { value: 'CLOSED', label: '已关闭' }
]

const levelOptions = [
  { value: '', label: '全部风险等级' },
  { value: 'HIGH', label: '高风险' },
  { value: 'MEDIUM', label: '中风险' },
  { value: 'LOW', label: '低风险' }
]

const sourceOptions = [
  { value: '', label: '全部来源' },
  { value: 'LEAD', label: '线索' },
  { value: 'PICKUP', label: '提货' },
  { value: 'RECONCILE', label: '对账' },
  { value: 'BILLING', label: '账单' }
]

async function loadList() {
  if (!canLoad.value || loading.value) return
  loading.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const params = new URLSearchParams()
    if (query.ticketStatus.trim()) params.set('ticketStatus', query.ticketStatus.trim())
    if (query.riskLevel.trim()) params.set('riskLevel', query.riskLevel.trim())
    if (query.sourceType.trim()) params.set('sourceType', query.sourceType.trim())
    if (query.owner.trim()) params.set('owner', query.owner.trim())
    if (query.keyword.trim()) params.set('keyword', query.keyword.trim())
    params.set('page', String(query.page))
    params.set('pageSize', String(query.pageSize))
    const resp = await fetch(`/api/admin/risk-alert-tickets?${params.toString()}`, {
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
    state.pendingCount = Number(data.pendingCount || 0)
    state.processingCount = Number(data.processingCount || 0)
    state.escalatedCount = Number(data.escalatedCount || 0)
    state.resolvedCount = Number(data.resolvedCount || 0)
    state.closedCount = Number(data.closedCount || 0)
    state.records = Array.isArray(data.records) ? data.records : []
    if (state.records.length > 0) {
      const hit = state.records.find((item) => item.ticketId === selectedTicketId.value)
      if (hit) {
        await openDetail(hit.ticketId)
      } else if (!selectedTicketId.value) {
        await openDetail(state.records[0].ticketId)
      }
    } else {
      selectedTicketId.value = ''
      detail.value = null
    }
  } catch (error) {
    errorMsg.value = error.message || '加载风险预警工单失败'
  } finally {
    loading.value = false
  }
}

async function openDetail(ticketId) {
  if (!ticketId || detailLoading.value) return
  detailLoading.value = true
  errorMsg.value = ''
  try {
    const resp = await fetch(`/api/admin/risk-alert-tickets/${encodeURIComponent(ticketId)}`, {
      headers: { 'X-Admin-Token': query.adminToken.trim() }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `详情加载失败(${resp.status})`)
    }
    selectedTicketId.value = ticketId
    detail.value = json.data || null
    handleForm.owner = detail.value?.owner || handleForm.owner
    if (detail.value?.ticketStatus === 'OPEN') {
      handleForm.action = 'START_PROCESS'
      handleForm.targetStatus = 'PROCESSING'
    }
  } catch (error) {
    errorMsg.value = error.message || '加载工单详情失败'
  } finally {
    detailLoading.value = false
  }
}

async function submitHandle() {
  if (!canSubmit.value) return
  submitting.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      action: handleForm.action.trim().toUpperCase(),
      targetStatus: handleForm.targetStatus.trim().toUpperCase() || null,
      owner: handleForm.owner.trim() || null,
      followUpPlan: handleForm.followUpPlan.trim() || null,
      solution: handleForm.solution.trim() || null,
      remark: handleForm.remark.trim() || null,
      operator: handleForm.operator.trim()
    }
    const resp = await fetch(
      `/api/admin/risk-alert-tickets/${encodeURIComponent(selectedTicketId.value)}/handle`,
      {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'X-Admin-Token': query.adminToken.trim()
        },
        body: JSON.stringify(payload)
      }
    )
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `处理失败(${resp.status})`)
    }
    detail.value = json.data || null
    successMsg.value = `工单 ${detail.value?.ticketCode || selectedTicketId.value} 已处理为 ${detail.value?.ticketStatusText || payload.targetStatus}`
    handleForm.followUpPlan = detail.value?.followUpPlan || ''
    handleForm.solution = ''
    handleForm.remark = ''
    await loadList()
  } catch (error) {
    errorMsg.value = error.message || '工单处理失败'
  } finally {
    submitting.value = false
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
  loadList()
})
</script>

<template>
  <main class="admn15-page">
    <section class="card hero">
      <h1>ADM-N15 风险预警工单中心</h1>
      <p>聚合全链路风险预警，支持工单接单、升级处置、闭环追踪与审计留痕。</p>
    </section>

    <section class="card">
      <h2>筛选查询</h2>
      <div class="filters">
        <label>
          Admin Token
          <input v-model="query.adminToken" placeholder="请输入X-Admin-Token" />
        </label>
        <label>
          工单状态
          <select v-model="query.ticketStatus">
            <option v-for="op in statusOptions" :key="op.value" :value="op.value">{{ op.label }}</option>
          </select>
        </label>
        <label>
          风险等级
          <select v-model="query.riskLevel">
            <option v-for="op in levelOptions" :key="op.value" :value="op.value">{{ op.label }}</option>
          </select>
        </label>
        <label>
          业务来源
          <select v-model="query.sourceType">
            <option v-for="op in sourceOptions" :key="op.value" :value="op.value">{{ op.label }}</option>
          </select>
        </label>
        <label>
          责任人
          <input v-model="query.owner" placeholder="按责任人模糊查询" />
        </label>
        <label class="span-2">
          关键词
          <input v-model="query.keyword" placeholder="工单号/风险码/商家/备注" />
        </label>
      </div>
      <div class="actions">
        <button class="btn btn--primary" :disabled="!canLoad || loading" @click="loadList">
          {{ loading ? '加载中...' : '查询工单' }}
        </button>
      </div>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
      <p v-if="successMsg" class="ok">{{ successMsg }}</p>
    </section>

    <section class="card">
      <h2>工单统计</h2>
      <div class="kpi-grid">
        <article class="kpi"><span>总工单</span><strong>{{ state.total }}</strong></article>
        <article class="kpi"><span>待处理</span><strong>{{ state.pendingCount }}</strong></article>
        <article class="kpi"><span>处理中</span><strong>{{ state.processingCount }}</strong></article>
        <article class="kpi"><span>升级中</span><strong>{{ state.escalatedCount }}</strong></article>
        <article class="kpi"><span>已处理</span><strong>{{ state.resolvedCount }}</strong></article>
        <article class="kpi"><span>已关闭</span><strong>{{ state.closedCount }}</strong></article>
      </div>
    </section>

    <section class="card">
      <div class="head">
        <h2>工单列表</h2>
        <span class="tip">共 {{ state.total }} 条</span>
      </div>
      <p v-if="loading">列表加载中...</p>
      <p v-else-if="state.records.length === 0">暂无风险工单</p>
      <ul v-else class="ticket-list">
        <li
          v-for="item in state.records"
          :key="item.ticketId"
          :class="['ticket-item', selectedTicketId === item.ticketId ? 'active' : '']"
          @click="openDetail(item.ticketId)"
        >
          <div class="line">
            <strong>{{ item.ticketNo }}</strong>
            <span class="badge">{{ item.sourceText }}</span>
            <span class="badge warn">{{ item.riskLevelText }}</span>
            <span class="badge">{{ item.ticketStatusText }}</span>
          </div>
          <p class="muted">{{ item.riskCode }} ｜ {{ item.riskTitle }}</p>
          <p class="muted">
            责任人：{{ item.owner || '-' }} ｜ 风险分：{{ item.riskScore || '-' }} ｜ 账龄：{{ item.agingHours }}h
          </p>
          <p class="muted">建议动作：{{ item.suggestedAction || '-' }}</p>
        </li>
      </ul>
      <div class="pager">
        <button class="btn" :disabled="query.page <= 1 || loading" @click="prevPage">上一页</button>
        <span>第 {{ query.page }} / {{ totalPages }} 页</span>
        <button class="btn" :disabled="query.page >= totalPages || loading" @click="nextPage">下一页</button>
      </div>
    </section>

    <section class="card" v-if="detail">
      <h2>工单详情</h2>
      <div class="detail-grid">
        <p>工单号：{{ detail.ticketCode }}</p>
        <p>来源：{{ detail.sourceTypeText }}</p>
        <p>业务单号：{{ detail.bizNo || '-' }}</p>
        <p>风险等级：{{ detail.riskLevelText }}</p>
        <p>工单状态：{{ detail.ticketStatusText }}</p>
        <p>责任人：{{ detail.owner || '-' }}</p>
        <p>处理人：{{ detail.resolver || '-' }}</p>
        <p>账龄：{{ detail.agingHours }}h</p>
        <p>风险码：{{ detail.riskCode }}</p>
        <p>风险标题：{{ detail.riskTitle }}</p>
        <p class="span-2">风险详情：{{ detail.riskDetail }}</p>
        <p class="span-2">建议动作：{{ detail.suggestedAction || '-' }}</p>
        <p class="span-2">跟进计划：{{ detail.followUpPlan || '-' }}</p>
        <p class="span-2">最新备注：{{ detail.latestRemark || '-' }}</p>
      </div>

      <section class="handle-block">
        <h3>处置动作</h3>
        <div class="handle-form">
          <select v-model="handleForm.action">
            <option value="START_PROCESS">START_PROCESS 启动处理</option>
            <option value="FOLLOW_UP">FOLLOW_UP 持续跟进</option>
            <option value="ESCALATE">ESCALATE 升级处置</option>
            <option value="RESOLVE">RESOLVE 处理完成</option>
            <option value="REOPEN">REOPEN 重新打开</option>
          </select>
          <select v-model="handleForm.targetStatus">
            <option value="OPEN">OPEN</option>
            <option value="PROCESSING">PROCESSING</option>
            <option value="ESCALATED">ESCALATED</option>
            <option value="RESOLVED">RESOLVED</option>
            <option value="CLOSED">CLOSED</option>
          </select>
          <input v-model="handleForm.owner" placeholder="责任人" />
          <input v-model="handleForm.operator" placeholder="操作人" />
          <input v-model="handleForm.followUpPlan" placeholder="跟进计划（可选）" />
          <input v-model="handleForm.solution" placeholder="处理方案（可选）" />
          <input v-model="handleForm.remark" placeholder="处理备注（可选）" />
          <button class="btn btn--primary" :disabled="!canSubmit" @click="submitHandle">
            {{ submitting ? '提交中...' : '提交处置' }}
          </button>
        </div>
      </section>

      <section class="handle-block">
        <h3>进度时间线</h3>
        <ul class="timeline">
          <li v-for="node in detail.progressNodes || []" :key="`${node.nodeCode}-${node.happenedAt}`">
            <div class="line">
              <strong>{{ node.nodeName }}</strong>
              <span class="badge">{{ node.statusText }}</span>
            </div>
            <p class="muted">处理人：{{ node.handler || '-' }} ｜ 时间：{{ node.happenedAt || '-' }}</p>
            <p class="muted">备注：{{ node.remark || '-' }}</p>
          </li>
        </ul>
      </section>
    </section>
  </main>
</template>

<style scoped>
.admn15-page {
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
select {
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
  grid-template-columns: repeat(6, minmax(0, 1fr));
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
.ticket-list,
.timeline {
  list-style: none;
  margin: 0;
  padding: 0;
  display: grid;
  gap: 10px;
}
.ticket-item,
.timeline li {
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  padding: 10px;
  cursor: pointer;
}
.ticket-item.active {
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
.handle-block {
  border: 1px solid #f1f5f9;
  border-radius: 10px;
  padding: 12px;
  margin-top: 10px;
}
.handle-block h3 {
  margin: 0 0 8px;
}
.handle-form {
  display: grid;
  gap: 8px;
  grid-template-columns: repeat(4, minmax(0, 1fr));
}
@media (max-width: 1024px) {
  .filters,
  .detail-grid,
  .kpi-grid,
  .handle-form {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
@media (max-width: 720px) {
  .filters,
  .detail-grid,
  .kpi-grid,
  .handle-form {
    grid-template-columns: 1fr;
  }
  .span-2 {
    grid-column: span 1;
  }
}
</style>
