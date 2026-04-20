<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const loading = ref(false)
const detailLoading = ref(false)
const sending = ref(false)
const statusSubmitting = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

const state = reactive({
  pageNo: 1,
  pageSize: 10,
  total: 0,
  activeSessionId: '',
  channel: 'H5',
  records: []
})

const selectedId = ref('')
const detail = ref(null)
const statusFilter = ref('')
const keyword = ref('')
const statusAction = ref('WAIT_CONFIRM')
const statusRemark = ref('')

const form = reactive({
  senderRole: 'BUYER',
  content: '',
  operator: 'h5-n04-quote-ui'
})

const token = computed(() => localStorage.getItem('H5_N01_AUTH_TOKEN') || localStorage.getItem('N01_AUTH_TOKEN') || '')
const hasSession = computed(() => token.value.length > 0)
const canSend = computed(() => hasSession.value && selectedId.value && form.content.trim().length > 0 && !sending.value)
const canUpdateStatus = computed(() => hasSession.value && selectedId.value && !statusSubmitting.value)
const messages = computed(() => (detail.value?.messages && Array.isArray(detail.value.messages) ? detail.value.messages : []))

async function loadSessions() {
  if (!hasSession.value || loading.value) {
    if (!hasSession.value) {
      errorMsg.value = '请先登录后查看报价会话'
    }
    return
  }
  loading.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const params = new URLSearchParams({
      pageNo: String(state.pageNo),
      pageSize: String(state.pageSize)
    })
    if (statusFilter.value) params.set('status', statusFilter.value)
    if (keyword.value.trim()) params.set('keyword', keyword.value.trim())
    const resp = await fetch(`/api/v1/auth/h5/quote-sessions?${params.toString()}`, {
      headers: { 'X-Auth-Token': token.value }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `会话列表加载失败(${resp.status})`)
    }
    const data = json.data || {}
    state.pageNo = data.pageNo || 1
    state.pageSize = data.pageSize || 10
    state.total = data.total || 0
    state.activeSessionId = data.activeSessionId || ''
    state.channel = data.channel || 'H5'
    state.records = Array.isArray(data.records) ? data.records : []

    if (!selectedId.value) {
      selectedId.value = state.activeSessionId || (state.records[0]?.sessionId || '')
      if (selectedId.value) await loadDetail()
      return
    }
    const exists = state.records.some((item) => item.sessionId === selectedId.value)
    if (!exists) {
      selectedId.value = state.activeSessionId || (state.records[0]?.sessionId || '')
      if (selectedId.value) {
        await loadDetail()
      } else {
        detail.value = null
      }
    }
  } catch (error) {
    errorMsg.value = error.message || '会话列表加载失败'
  } finally {
    loading.value = false
  }
}

async function loadDetail() {
  if (!hasSession.value || !selectedId.value || detailLoading.value) return
  detailLoading.value = true
  errorMsg.value = ''
  try {
    const resp = await fetch(`/api/v1/auth/h5/quote-sessions/${selectedId.value}`, {
      headers: { 'X-Auth-Token': token.value }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `会话详情加载失败(${resp.status})`)
    }
    detail.value = json.data || null
    const availableActions = Array.isArray(detail.value?.availableActions) ? detail.value.availableActions : []
    if (availableActions.length > 0 && !availableActions.includes(statusAction.value)) {
      statusAction.value = availableActions[0]
    }
  } catch (error) {
    errorMsg.value = error.message || '会话详情加载失败'
  } finally {
    detailLoading.value = false
  }
}

async function sendMessage() {
  if (!canSend.value) return
  sending.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      senderRole: form.senderRole,
      content: form.content.trim(),
      operator: form.operator.trim() || 'h5-n04-quote-ui'
    }
    const resp = await fetch(`/api/v1/auth/h5/quote-sessions/${selectedId.value}/messages`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Auth-Token': token.value
      },
      body: JSON.stringify(payload)
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `发送失败(${resp.status})`)
    }
    detail.value = json.data || null
    form.content = ''
    successMsg.value = '报价消息发送成功'
    await loadSessions()
  } catch (error) {
    errorMsg.value = error.message || '消息发送失败'
  } finally {
    sending.value = false
  }
}

async function updateStatus() {
  if (!canUpdateStatus.value) return
  statusSubmitting.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      action: statusAction.value,
      remark: statusRemark.value.trim() || null,
      operator: form.operator.trim() || 'h5-n04-quote-ui'
    }
    const resp = await fetch(`/api/v1/auth/h5/quote-sessions/${selectedId.value}/status`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Auth-Token': token.value
      },
      body: JSON.stringify(payload)
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `状态更新失败(${resp.status})`)
    }
    detail.value = json.data || null
    successMsg.value = '报价会话状态更新成功'
    await loadSessions()
  } catch (error) {
    errorMsg.value = error.message || '会话状态更新失败'
  } finally {
    statusSubmitting.value = false
  }
}

function chooseSession(sessionId) {
  selectedId.value = sessionId
  loadDetail()
}

function goH5Home() {
  router.push('/h5?city=唐山')
}

function goIdentitySwitch() {
  router.push('/h5/identity-switch')
}

function goEnterpriseCertification() {
  router.push('/h5/enterprise-certification')
}

function goOrderDetail() {
  router.push('/h5/order-detail')
}

function goPickupScan() {
  router.push('/h5/pickup-scan')
}

function goReconcileDetail() {
  router.push('/h5/reconcile-detail')
}

function goAfterSaleCreate() {
  router.push('/h5/after-sale-create')
}

onMounted(() => {
  loadSessions()
})
</script>

<template>
  <main class="h5-n04-page">
    <section class="card hero">
      <h1>H5-N04 报价会话页</h1>
      <p>移动端查看报价沟通进展，支持报价消息发送与状态流转。</p>
      <div class="hero-actions">
        <button class="btn" @click="goH5Home">返回H5首页</button>
        <button class="btn" @click="goIdentitySwitch">前往身份切换</button>
        <button class="btn" @click="goEnterpriseCertification">前往企业认证</button>
        <button class="btn" @click="goOrderDetail">前往H5-N05订单详情</button>
        <button class="btn" @click="goPickupScan">前往H5-N06扫码提货</button>
        <button class="btn" @click="goReconcileDetail">前往H5-N07对账详情</button>
        <button class="btn" @click="goAfterSaleCreate">前往H5-N08售后发起</button>
      </div>
    </section>

    <section class="card">
      <div class="head">
        <h2>报价会话列表</h2>
        <button class="btn" :disabled="loading" @click="loadSessions">{{ loading ? '刷新中...' : '刷新' }}</button>
      </div>
      <div class="filters">
        <select v-model="statusFilter">
          <option value="">全部状态</option>
          <option value="ONGOING">报价中</option>
          <option value="WAIT_CONFIRM">待确认</option>
          <option value="DEAL">已达成</option>
          <option value="CLOSED">已关闭</option>
        </select>
        <input v-model="keyword" placeholder="按询价号/品类/商家搜索" />
        <button class="btn" :disabled="loading" @click="loadSessions">筛选</button>
      </div>
      <p class="tip">渠道：{{ state.channel }} ｜ 共 {{ state.total }} 条</p>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
      <ul class="session-list">
        <li
          v-for="item in state.records"
          :key="item.sessionId"
          :class="{ active: item.sessionId === selectedId }"
          @click="chooseSession(item.sessionId)"
        >
          <div class="line-1">
            <strong>{{ item.goodsName }}</strong>
            <span>{{ item.statusText }}</span>
          </div>
          <p>{{ item.specText }} ｜ 对手方：{{ item.counterpartyName }}</p>
          <p>最新报价：{{ item.latestQuotedPrice || '-' }} ｜ {{ item.lastMessagePreview || '-' }}</p>
        </li>
      </ul>
    </section>

    <section class="card">
      <div class="head">
        <h2>会话详情</h2>
        <button class="btn" :disabled="detailLoading || !selectedId" @click="loadDetail">
          {{ detailLoading ? '加载中...' : '刷新详情' }}
        </button>
      </div>
      <p v-if="successMsg" class="ok">{{ successMsg }}</p>
      <div v-if="detail" class="detail">
        <p>会话ID：{{ detail.sessionId }}</p>
        <p>询价单号：{{ detail.inquiryNo }}</p>
        <p>品名规格：{{ detail.goodsName }} / {{ detail.specText }}</p>
        <p>采购方：{{ detail.buyerName }} ｜ 供应方：{{ detail.sellerName }}</p>
        <p>状态：{{ detail.statusText }}（{{ detail.status }}）</p>
        <p>轮次：{{ detail.currentRound }} ｜ 目标价：{{ detail.targetPrice || '-' }}</p>
        <p>最新报价：{{ detail.latestQuotedPrice || '-' }} ｜ 未读：{{ detail.unreadCount }}</p>

        <h3>消息流</h3>
        <ul class="message-list">
          <li v-for="msg in messages" :key="msg.messageId">
            <div class="line-1">
              <strong>{{ msg.senderName }}</strong>
              <span>{{ msg.sentAt || '-' }}</span>
            </div>
            <p>{{ msg.content }}</p>
            <p class="tip">类型：{{ msg.contentType }} ｜ 报价：{{ msg.quotePrice || '-' }}</p>
          </li>
        </ul>

        <div class="composer">
          <h3>发送报价消息</h3>
          <div class="row">
            <select v-model="form.senderRole">
              <option value="BUYER">采购方</option>
              <option value="SUPPLIER">供应方</option>
            </select>
            <input v-model="form.content" placeholder="请输入报价沟通内容" />
            <button class="btn btn--primary" :disabled="!canSend" @click="sendMessage">
              {{ sending ? '发送中...' : '发送' }}
            </button>
          </div>
        </div>

        <div class="status-flow">
          <h3>状态流转</h3>
          <div class="row">
            <select v-model="statusAction">
              <option value="ONGOING">恢复报价</option>
              <option value="WAIT_CONFIRM">转待确认</option>
              <option value="DEAL">标记达成</option>
              <option value="CLOSED">关闭会话</option>
            </select>
            <input v-model="statusRemark" placeholder="备注（可选）" />
            <button class="btn" :disabled="!canUpdateStatus" @click="updateStatus">
              {{ statusSubmitting ? '提交中...' : '更新状态' }}
            </button>
          </div>
        </div>
      </div>
      <p v-else class="tip">请选择会话查看详情</p>
    </section>
  </main>
</template>

<style scoped>
.h5-n04-page {
  max-width: 760px;
  margin: 0 auto;
  padding: 12px 12px 28px;
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
  margin: 0;
}
.hero p {
  margin: 8px 0 0;
  color: #7c2d12;
}
.hero-actions {
  margin-top: 10px;
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
.head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.filters {
  margin-top: 10px;
  display: grid;
  grid-template-columns: 120px 1fr auto;
  gap: 8px;
}
input,
select {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 8px 10px;
  font: inherit;
}
.session-list {
  list-style: none;
  margin: 10px 0 0;
  padding: 0;
  display: grid;
  gap: 8px;
}
.session-list li {
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  padding: 10px;
}
.session-list li.active {
  border-color: #f57c00;
  background: #fffaf3;
}
.line-1 {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}
.detail p {
  margin: 4px 0;
}
.message-list {
  list-style: none;
  margin: 8px 0 0;
  padding: 0;
  display: grid;
  gap: 8px;
}
.message-list li {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 8px;
}
.composer,
.status-flow {
  margin-top: 12px;
}
.row {
  display: grid;
  grid-template-columns: 110px 1fr auto;
  gap: 8px;
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
  color: #fff;
  background: #f57c00;
}
.error {
  color: #b42318;
}
.ok {
  color: #027a48;
}
.tip {
  color: #6b7280;
}
@media (max-width: 768px) {
  .filters,
  .row {
    grid-template-columns: 1fr;
  }
}
</style>
