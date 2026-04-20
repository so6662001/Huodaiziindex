<script setup>
import { computed, onMounted, reactive, ref } from 'vue'

const loading = ref(false)
const errorMsg = ref('')
const selected = ref(null)

const query = reactive({
  adminToken: 'test-admin-token',
  moduleCode: '',
  actionCode: '',
  resultStatus: '',
  operator: '',
  keyword: '',
  page: 1,
  pageSize: 10
})

const state = reactive({
  records: [],
  total: 0,
  page: 1,
  pageSize: 10,
  successCount: 0,
  failedCount: 0,
  moduleCode: '',
  actionCode: '',
  resultStatus: '',
  keyword: ''
})

const canLoad = computed(() => query.adminToken.trim().length > 0)
const totalPages = computed(() => Math.max(Math.ceil(state.total / query.pageSize), 1))

const moduleOptions = [
  { value: '', label: '全部模块' },
  { value: 'ADMN01', label: 'ADMN01 商家认证审核' },
  { value: 'ADMN02', label: 'ADMN02 买家黑名单' },
  { value: 'ADMN03', label: 'ADMN03 角色权限管理' },
  { value: 'ADMN04', label: 'ADMN04 审计日志查询' }
]

const actionOptions = [
  { value: '', label: '全部动作' },
  { value: 'CERT_REVIEW', label: '认证审核' },
  { value: 'BUYER_BLACKLIST', label: '黑名单操作' },
  { value: 'ROLE_UPSERT', label: '角色新增/编辑' },
  { value: 'ROLE_PERMISSION_UPDATE', label: '角色权限更新' },
  { value: 'AUDIT_QUERY', label: '日志查询' }
]

const resultOptions = [
  { value: '', label: '全部结果' },
  { value: 'SUCCESS', label: '成功' },
  { value: 'FAILED', label: '失败' }
]

async function loadLogs() {
  if (!canLoad.value || loading.value) return
  loading.value = true
  errorMsg.value = ''
  try {
    const params = new URLSearchParams()
    if (query.moduleCode) params.set('moduleCode', query.moduleCode)
    if (query.actionCode) params.set('actionCode', query.actionCode)
    if (query.resultStatus) params.set('resultStatus', query.resultStatus)
    if (query.operator.trim()) params.set('operator', query.operator.trim())
    if (query.keyword.trim()) params.set('keyword', query.keyword.trim())
    params.set('page', String(query.page))
    params.set('pageSize', String(query.pageSize))
    const resp = await fetch(`/api/admin/audit-logs?${params.toString()}`, {
      headers: { 'X-Admin-Token': query.adminToken.trim() }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `审计日志加载失败(${resp.status})`)
    }
    const data = json.data || {}
    state.records = Array.isArray(data.records) ? data.records : []
    state.total = Number(data.total || 0)
    state.page = Number(data.page || query.page)
    state.pageSize = Number(data.pageSize || query.pageSize)
    state.successCount = Number(data.successCount || 0)
    state.failedCount = Number(data.failedCount || 0)
    state.moduleCode = data.moduleCode || query.moduleCode
    state.actionCode = data.actionCode || query.actionCode
    state.resultStatus = data.resultStatus || query.resultStatus
    state.keyword = data.keyword || query.keyword
    if (selected.value && !state.records.some((item) => item.logId === selected.value.auditId)) {
      selected.value = null
    }
  } catch (error) {
    errorMsg.value = error.message || '审计日志加载失败'
  } finally {
    loading.value = false
  }
}

async function openDetail(row) {
  if (!row?.logId || loading.value) return
  errorMsg.value = ''
  try {
    const resp = await fetch(`/api/admin/audit-logs/${row.logId}`, {
      headers: { 'X-Admin-Token': query.adminToken.trim() }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `审计日志详情加载失败(${resp.status})`)
    }
    selected.value = json.data || null
  } catch (error) {
    errorMsg.value = error.message || '审计日志详情加载失败'
  }
}

function prevPage() {
  if (query.page <= 1 || loading.value) return
  query.page -= 1
  loadLogs()
}

function nextPage() {
  if (query.page >= totalPages.value || loading.value) return
  query.page += 1
  loadLogs()
}

onMounted(() => {
  loadLogs()
})
</script>

<template>
  <main class="admn04-page">
    <section class="card hero">
      <h1>ADM-N04 操作审计日志</h1>
      <p>统一记录管理端关键操作留痕，支持按模块、动作、结果、操作人快速检索与回溯。</p>
    </section>

    <section class="card">
      <div class="filters">
        <label>
          Admin Token
          <input v-model="query.adminToken" placeholder="请输入X-Admin-Token" />
        </label>
        <label>
          模块
          <select v-model="query.moduleCode">
            <option v-for="op in moduleOptions" :key="op.value" :value="op.value">{{ op.label }}</option>
          </select>
        </label>
        <label>
          动作
          <select v-model="query.actionCode">
            <option v-for="op in actionOptions" :key="op.value" :value="op.value">{{ op.label }}</option>
          </select>
        </label>
        <label>
          结果
          <select v-model="query.resultStatus">
            <option v-for="op in resultOptions" :key="op.value" :value="op.value">{{ op.label }}</option>
          </select>
        </label>
        <label>
          操作人
          <input v-model="query.operator" placeholder="operator 模糊匹配" />
        </label>
        <label class="span-2">
          关键词
          <input v-model="query.keyword" placeholder="logId/targetId/requestId/摘要" />
        </label>
      </div>
      <div class="actions">
        <button class="btn btn--primary" :disabled="!canLoad || loading" @click="loadLogs">
          {{ loading ? '加载中...' : '查询审计日志' }}
        </button>
      </div>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
    </section>

    <section class="stats">
      <article class="stat"><span>日志总数</span><strong>{{ state.total }}</strong></article>
      <article class="stat"><span>成功</span><strong>{{ state.successCount }}</strong></article>
      <article class="stat"><span>失败</span><strong>{{ state.failedCount }}</strong></article>
      <article class="stat"><span>当前页</span><strong>{{ query.page }}</strong></article>
    </section>

    <section class="card">
      <h2>日志列表</h2>
      <p v-if="loading">加载中...</p>
      <p v-else-if="state.records.length === 0">暂无审计日志</p>
      <ul v-else class="list">
        <li v-for="row in state.records" :key="row.logId" class="item">
          <div>
            <h3>{{ row.moduleName }} / {{ row.actionName }}</h3>
            <p>日志ID：{{ row.logId }}</p>
            <p>目标：{{ row.targetType }} ｜ {{ row.targetId }}</p>
            <p>操作人：{{ row.operator }} ｜ IP：{{ row.operatorIp || '-' }}</p>
            <p>结果：{{ row.resultText }} ｜ 时间：{{ row.happenedAt || '-' }}</p>
            <p>摘要：{{ row.detailSummary || '-' }}</p>
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
      <h2>日志详情</h2>
      <p>审计ID：{{ selected.auditId }}</p>
      <p>模块：{{ selected.moduleName }}（{{ selected.moduleCode }}）</p>
      <p>动作：{{ selected.actionName }}（{{ selected.actionCode }}）</p>
      <p>目标对象：{{ selected.targetType }} ｜ {{ selected.targetId }}</p>
      <p>操作人：{{ selected.operator }}（{{ selected.operatorRole }}）</p>
      <p>请求：{{ selected.requestMethod }} {{ selected.requestPath }}</p>
      <p>来源IP：{{ selected.requestIp || '-' }} ｜ TraceID：{{ selected.requestTraceId || '-' }}</p>
      <p>结果：{{ selected.resultText }}（{{ selected.resultCode }}）</p>
      <p>变更摘要：{{ selected.changedFieldsText || '-' }}</p>
      <p>标签：{{ (selected.tags || []).join(' / ') || '-' }}</p>
      <p>创建时间：{{ selected.createdAt || '-' }}</p>
      <div class="detail-grid">
        <article class="detail-box">
          <h3>变更前</h3>
          <pre>{{ selected.beforeJson || '-' }}</pre>
        </article>
        <article class="detail-box">
          <h3>变更后</h3>
          <pre>{{ selected.afterJson || '-' }}</pre>
        </article>
      </div>
    </section>
  </main>
</template>

<style scoped>
.admn04-page {
  max-width: 1140px;
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
.span-2 {
  grid-column: span 2;
}
label {
  display: grid;
  gap: 6px;
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
.stats {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
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
  margin: 5px 0;
}
.detail-grid {
  margin-top: 10px;
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
}
.detail-box {
  border: 1px solid #ececec;
  border-radius: 8px;
  padding: 10px;
}
.detail-box h3 {
  margin: 0 0 8px;
}
pre {
  margin: 0;
  white-space: pre-wrap;
  word-break: break-all;
  color: #374151;
  font-size: 12px;
  line-height: 1.5;
}
@media (max-width: 960px) {
  .filters,
  .stats,
  .detail-grid {
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
