<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const loading = ref(false)
const detailLoading = ref(false)
const submitting = ref(false)
const errorMsg = ref('')
const successMsg = ref('')
const selectedMessage = ref(null)

const filters = reactive({
  merchantId: '',
  messageType: '',
  readStatus: '',
  keyword: ''
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const state = reactive({
  items: [],
  unreadCount: 0,
  readCount: 0,
  systemCount: 0,
  transactionCount: 0,
  riskCount: 0
})

const typeTextMap = {
  SYSTEM: '系统通知',
  TRANSACTION: '交易通知',
  RISK: '风控通知'
}

const canQuery = computed(() => filters.merchantId.trim().length > 0)

function resetMessages() {
  selectedMessage.value = null
  successMsg.value = ''
}

async function loadMessages() {
  if (!canQuery.value || loading.value) return
  loading.value = true
  errorMsg.value = ''
  try {
    const params = new URLSearchParams()
    params.set('merchantId', filters.merchantId.trim())
    if (filters.messageType) params.set('messageType', filters.messageType)
    if (filters.readStatus) params.set('readStatus', filters.readStatus)
    if (filters.keyword.trim()) params.set('keyword', filters.keyword.trim())
    params.set('page', String(pagination.page))
    params.set('pageSize', String(pagination.pageSize))
    const resp = await fetch(`/api/v1/inquiries/merchant/messages?${params.toString()}`)
    const json = await resp.json()
    if (json.code !== '0') {
      throw new Error(json.message || '消息列表加载失败')
    }
    const data = json.data || {}
    state.items = Array.isArray(data.items) ? data.items : []
    pagination.total = Number(data.total || 0)
    state.unreadCount = Number(data.unreadCount || 0)
    state.readCount = Number(data.readCount || 0)
    state.systemCount = Number(data.systemCount || 0)
    state.transactionCount = Number(data.transactionCount || 0)
    state.riskCount = Number(data.riskCount || 0)
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    loading.value = false
  }
}

async function openDetail(row) {
  if (!row?.messageId || detailLoading.value) return
  detailLoading.value = true
  errorMsg.value = ''
  try {
    const params = new URLSearchParams()
    params.set('merchantId', filters.merchantId.trim())
    const resp = await fetch(`/api/v1/inquiries/merchant/messages/${row.messageId}?${params.toString()}`)
    const json = await resp.json()
    if (json.code !== '0') {
      throw new Error(json.message || '消息详情加载失败')
    }
    const data = json.data || {}
    selectedMessage.value = {
      ...row,
      content: data.content || row.content || '',
      relatedBizNo: data.relatedBizNo || '',
      relatedBizType: data.relatedBizType || '',
      extraInfo: data.extraInfo || ''
    }
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    detailLoading.value = false
  }
}

async function markRead(row) {
  if (!row?.messageId || submitting.value) return
  submitting.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const resp = await fetch(`/api/v1/inquiries/merchant/messages/${row.messageId}/read`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        merchantId: filters.merchantId.trim(),
        operator: '商家用户'
      })
    })
    const json = await resp.json()
    if (json.code !== '0') {
      throw new Error(json.message || '标记已读失败')
    }
    successMsg.value = json.data?.message || '已标记为已读'
    await loadMessages()
    if (selectedMessage.value?.messageId === row.messageId) {
      await openDetail(row)
    }
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    submitting.value = false
  }
}

async function markAllRead() {
  if (!canQuery.value || submitting.value) return
  submitting.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const resp = await fetch('/api/v1/inquiries/merchant/messages/read-all', {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        merchantId: filters.merchantId.trim(),
        channel: 'WEB'
      })
    })
    const json = await resp.json()
    if (json.code !== '0') {
      throw new Error(json.message || '全部已读失败')
    }
    successMsg.value = json.data?.message || '全部消息已标记为已读'
    await loadMessages()
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    submitting.value = false
  }
}

function goPage(page) {
  if (page < 1) return
  const maxPage = Math.max(1, Math.ceil(pagination.total / pagination.pageSize))
  if (page > maxPage) return
  pagination.page = page
  loadMessages()
}

function refresh() {
  pagination.page = 1
  resetMessages()
  loadMessages()
}

onMounted(() => {
  filters.merchantId = String(route.query.merchantId || 'S001').trim() || 'S001'
  loadMessages()
})
</script>

<template>
  <main class="message-center-page">
    <section class="card">
      <h1>P14 消息中心</h1>
      <p class="desc">统一查看系统通知、交易提醒与风控预警，支持单条/批量已读管理。</p>
      <div class="filters">
        <label>
          商家ID
          <input v-model="filters.merchantId" placeholder="如 S001" />
        </label>
        <label>
          消息类型
          <select v-model="filters.messageType">
            <option value="">全部</option>
            <option value="SYSTEM">系统通知</option>
            <option value="TRANSACTION">交易通知</option>
            <option value="RISK">风控通知</option>
          </select>
        </label>
        <label>
          阅读状态
          <select v-model="filters.readStatus">
            <option value="">全部</option>
            <option value="UNREAD">未读</option>
            <option value="READ">已读</option>
          </select>
        </label>
        <label class="keyword">
          关键词
          <input v-model="filters.keyword" placeholder="标题/内容关键词" />
        </label>
        <button class="btn primary" :disabled="!canQuery || loading" @click="refresh">
          {{ loading ? '加载中...' : '查询' }}
        </button>
        <button class="btn" :disabled="!canQuery || submitting" @click="markAllRead">
          {{ submitting ? '处理中...' : '全部标记已读' }}
        </button>
      </div>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
      <p v-if="successMsg" class="success">{{ successMsg }}</p>
    </section>

    <section class="stats-grid">
      <article class="stat-card">
        <span>未读</span>
        <strong>{{ state.unreadCount }}</strong>
      </article>
      <article class="stat-card">
        <span>已读</span>
        <strong>{{ state.readCount }}</strong>
      </article>
      <article class="stat-card">
        <span>系统通知</span>
        <strong>{{ state.systemCount }}</strong>
      </article>
      <article class="stat-card">
        <span>交易通知</span>
        <strong>{{ state.transactionCount }}</strong>
      </article>
      <article class="stat-card">
        <span>风控通知</span>
        <strong>{{ state.riskCount }}</strong>
      </article>
    </section>

    <section class="card">
      <div class="section-head">
        <h2>消息列表</h2>
        <span class="muted">共 {{ pagination.total }} 条</span>
      </div>
      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>状态</th>
              <th>类型</th>
              <th>标题</th>
              <th>时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="!state.items.length">
              <td colspan="5" class="empty">暂无消息</td>
            </tr>
            <tr v-for="row in state.items" :key="row.messageId" :class="{ unread: row.unread }">
              <td>
                <span class="status" :data-status="row.status">{{ row.statusText }}</span>
              </td>
              <td>{{ typeTextMap[row.messageType] || row.messageTypeText || row.messageType }}</td>
              <td>
                <strong>{{ row.title }}</strong>
                <p class="summary">{{ row.content }}</p>
              </td>
              <td>{{ row.createdAt }}</td>
              <td class="actions">
                <button class="btn tiny" @click="openDetail(row)">详情</button>
                <button class="btn tiny" :disabled="!row.unread || submitting" @click="markRead(row)">
                  标记已读
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div class="pager">
        <button class="btn tiny" :disabled="pagination.page <= 1" @click="goPage(pagination.page - 1)">上一页</button>
        <span>第 {{ pagination.page }} / {{ Math.max(1, Math.ceil(pagination.total / pagination.pageSize)) }} 页</span>
        <button
          class="btn tiny"
          :disabled="pagination.page >= Math.max(1, Math.ceil(pagination.total / pagination.pageSize))"
          @click="goPage(pagination.page + 1)"
        >
          下一页
        </button>
      </div>
    </section>

    <section class="card" v-if="selectedMessage">
      <div class="section-head">
        <h2>消息详情</h2>
        <span class="muted">{{ detailLoading ? '加载中...' : selectedMessage.createdAt }}</span>
      </div>
      <h3>{{ selectedMessage.title }}</h3>
      <p class="content">{{ selectedMessage.content }}</p>
      <p class="muted">关联业务：{{ selectedMessage.relatedBizType || '-' }} / {{ selectedMessage.relatedBizNo || '-' }}</p>
      <p class="muted">动作链接：{{ selectedMessage.actionUrl || '-' }}</p>
      <p class="muted">补充说明：{{ selectedMessage.extraInfo || '-' }}</p>
    </section>
  </main>
</template>

<style scoped>
.message-center-page {
  max-width: 1160px;
  margin: 0 auto;
  padding: 20px 16px 36px;
}
.card {
  background: #fff;
  border: 1px solid #ececec;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 14px;
}
.desc {
  color: #4b5563;
  margin: 0 0 12px;
}
.filters {
  display: flex;
  flex-wrap: wrap;
  align-items: end;
  gap: 10px;
}
label {
  display: grid;
  gap: 6px;
  color: #374151;
}
.keyword {
  min-width: 220px;
}
input,
select {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 9px 10px;
  font: inherit;
}
.btn {
  border: 1px solid #ddd;
  border-radius: 8px;
  background: #fff;
  padding: 9px 14px;
  cursor: pointer;
}
.btn.primary {
  border-color: #f57c00;
  background: #f57c00;
  color: #fff;
}
.btn.tiny {
  padding: 6px 10px;
  font-size: 13px;
}
.error {
  color: #b42318;
  margin-top: 10px;
}
.success {
  color: #0c7a43;
  margin-top: 10px;
}
.stats-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 10px;
  margin-bottom: 14px;
}
.stat-card {
  background: #fff;
  border: 1px solid #ececec;
  border-radius: 10px;
  padding: 10px 12px;
  display: grid;
  gap: 6px;
}
.stat-card span {
  color: #6b7280;
  font-size: 13px;
}
.stat-card strong {
  color: #111827;
  font-size: 24px;
}
.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}
.muted {
  color: #6b7280;
  margin: 0;
}
.table-wrap {
  overflow-x: auto;
}
table {
  width: 100%;
  border-collapse: collapse;
  min-width: 760px;
}
th,
td {
  border-bottom: 1px solid #f1f5f9;
  text-align: left;
  padding: 10px 8px;
  font-size: 14px;
}
th {
  color: #374151;
  background: #fafafa;
}
.empty {
  text-align: center;
  color: #6b7280;
}
.summary {
  margin: 6px 0 0;
  color: #6b7280;
  font-size: 12px;
}
.actions {
  display: flex;
  gap: 6px;
}
.status[data-status='UNREAD'] {
  color: #b45309;
}
.status[data-status='READ'] {
  color: #0c7a43;
}
tr.unread {
  background: #fffaf0;
}
.pager {
  margin-top: 10px;
  display: flex;
  align-items: center;
  gap: 8px;
}
h3 {
  margin: 0 0 8px;
}
.content {
  color: #374151;
  line-height: 1.7;
}
@media (max-width: 980px) {
  .stats-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
