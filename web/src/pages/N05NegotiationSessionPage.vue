<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const loading = ref(false)
const detailLoading = ref(false)
const submitting = ref(false)
const statusSubmitting = ref(false)
const errorMsg = ref('')
const successMsg = ref('')
const records = ref([])
const selectedId = ref('')
const detail = ref(null)
const statusFilter = ref('')
const keyword = ref('')
const pageNo = ref(1)
const pageSize = ref(10)
const total = ref(0)

const form = ref({
  senderRole: 'BUYER',
  messageType: 'TEXT',
  content: '',
  operator: 'pc-n05-ui'
})

const statusAction = ref('WAIT_CONFIRM')
const statusRemark = ref('')

const token = computed(() => localStorage.getItem('N01_AUTH_TOKEN') || '')
const hasSession = computed(() => token.value.length > 0)
const canSend = computed(
  () =>
    hasSession.value &&
    selectedId.value &&
    form.value.content.trim().length > 0 &&
    !submitting.value
)
const canUpdateStatus = computed(() => hasSession.value && selectedId.value && !statusSubmitting.value)

async function loadSessions() {
  if (!hasSession.value || loading.value) {
    if (!hasSession.value) {
      errorMsg.value = '请先登录后查看议价会话'
    }
    return
  }
  loading.value = true
  errorMsg.value = ''
  try {
    const params = new URLSearchParams({
      pageNo: String(pageNo.value),
      pageSize: String(pageSize.value)
    })
    if (statusFilter.value) params.set('status', statusFilter.value)
    if (keyword.value.trim()) params.set('keyword', keyword.value.trim())
    const resp = await fetch(`/api/v1/auth/negotiations?${params.toString()}`, {
      headers: { 'X-Auth-Token': token.value }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `会话列表加载失败(${resp.status})`)
    }
    const data = json.data || {}
    records.value = data.records || []
    total.value = data.total || 0
    if (!selectedId.value && records.value.length > 0) {
      selectedId.value = records.value[0].sessionId
      await loadDetail()
    } else if (selectedId.value) {
      const exists = records.value.some((item) => item.sessionId === selectedId.value)
      if (!exists && records.value.length > 0) {
        selectedId.value = records.value[0].sessionId
        await loadDetail()
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
    const resp = await fetch(`/api/v1/auth/negotiations/${selectedId.value}`, {
      headers: { 'X-Auth-Token': token.value }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `会话详情加载失败(${resp.status})`)
    }
    detail.value = json.data || null
  } catch (error) {
    errorMsg.value = error.message || '会话详情加载失败'
  } finally {
    detailLoading.value = false
  }
}

async function sendMessage() {
  if (!canSend.value) return
  submitting.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      senderRole: form.value.senderRole,
      messageType: form.value.messageType,
      content: form.value.content.trim(),
      operator: form.value.operator
    }
    const resp = await fetch(`/api/v1/auth/negotiations/${selectedId.value}/messages`, {
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
    form.value.content = ''
    successMsg.value = '消息发送成功'
    await loadSessions()
  } catch (error) {
    errorMsg.value = error.message || '消息发送失败'
  } finally {
    submitting.value = false
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
      remark: statusRemark.value.trim() || null
    }
    const resp = await fetch(`/api/v1/auth/negotiations/${selectedId.value}/status`, {
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
    successMsg.value = '会话状态更新成功'
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

function goOnboarding() {
  router.push('/account/onboarding-progress')
}

function goOrderDetail() {
  router.push('/account/order-detail')
}

function goIdentity() {
  router.push('/account/identity-select')
}

function goHome() {
  router.push('/')
}

onMounted(() => {
  loadSessions()
})
</script>

<template>
  <main class="n05-page">
    <section class="card hero">
      <h1>PC-N05 议价会话页</h1>
      <p>查看采购与供应双方议价消息流，支持发言与会话状态流转。</p>
      <div class="hero-actions">
        <button class="btn" @click="goOnboarding">返回入驻进度</button>
        <button class="btn" @click="goOrderDetail">订单详情页</button>
        <button class="btn" @click="goIdentity">返回身份选择</button>
        <button class="btn" @click="goHome">返回首页</button>
      </div>
    </section>

    <section class="grid">
      <article class="card">
        <div class="head">
          <h2>会话列表</h2>
          <button class="btn" :disabled="loading" @click="loadSessions">{{ loading ? '刷新中...' : '刷新' }}</button>
        </div>
        <div class="filters">
          <select v-model="statusFilter">
            <option value="">全部状态</option>
            <option value="ONGOING">议价中</option>
            <option value="WAIT_CONFIRM">待确认</option>
            <option value="DEAL">已达成</option>
            <option value="CLOSED">已关闭</option>
          </select>
          <input v-model="keyword" placeholder="按会话号/询价号/商家搜索" />
          <button class="btn" :disabled="loading" @click="loadSessions">筛选</button>
        </div>
        <p class="tip">共 {{ total }} 条</p>
        <ul class="session-list">
          <li
            v-for="item in records"
            :key="item.sessionId"
            :class="{ active: item.sessionId === selectedId }"
            @click="chooseSession(item.sessionId)"
          >
            <div class="line-1">
              <strong>{{ item.productName }}</strong>
              <span>{{ item.statusText }}</span>
            </div>
            <p>{{ item.specification }} · {{ item.quantityText }} · {{ item.city }}</p>
            <p>对手方：{{ item.counterpartyName }} · 最新价：{{ item.latestOfferPrice || '-' }}</p>
          </li>
        </ul>
      </article>

      <article class="card">
        <div class="head">
          <h2>会话详情</h2>
          <button class="btn" :disabled="detailLoading || !selectedId" @click="loadDetail">
            {{ detailLoading ? '加载中...' : '刷新详情' }}
          </button>
        </div>
        <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
        <p v-if="successMsg" class="ok">{{ successMsg }}</p>
        <div v-if="detail" class="detail">
          <p>会话ID：{{ detail.sessionId }}</p>
          <p>询价单号：{{ detail.inquiryNo }}</p>
          <p>品名规格：{{ detail.goodsName }} / {{ detail.specText }}</p>
          <p>采购方：{{ detail.buyerName }} ｜ 供应方：{{ detail.sellerName }}</p>
          <p>状态：{{ detail.statusText }}（{{ detail.status }}）</p>
          <p>当前轮次：{{ detail.currentRound }} ｜ 目标价：{{ detail.targetPrice || '-' }}</p>
          <p>最新报价：{{ detail.latestQuotedPrice || '-' }} ｜ 未读：{{ detail.unreadCount }}</p>
          <p>最后消息时间：{{ detail.lastMessageAt || '-' }}</p>

          <h3>消息流</h3>
          <ul class="message-list">
            <li v-for="msg in detail.messages" :key="msg.messageId">
              <div class="line-1">
                <strong>{{ msg.senderName }}</strong>
                <span>{{ msg.sentAt || '-' }}</span>
              </div>
              <p>{{ msg.content }}</p>
              <p class="tip">类型：{{ msg.contentType }} ｜ 报价：{{ msg.quotePrice || '-' }}</p>
            </li>
          </ul>

          <div class="composer">
            <h3>发送消息</h3>
            <div class="row">
              <select v-model="form.senderRole">
                <option value="BUYER">采购方</option>
                <option value="SUPPLIER">供应方</option>
              </select>
              <input v-model="form.content" placeholder="请输入沟通内容" />
              <button class="btn btn--primary" :disabled="!canSend" @click="sendMessage">
                {{ submitting ? '发送中...' : '发送' }}
              </button>
            </div>
          </div>

          <div class="status">
            <h3>状态流转</h3>
            <div class="row">
              <select v-model="statusAction">
                <option value="ONGOING">恢复议价</option>
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
        <p v-else class="tip">请从左侧选择会话</p>
      </article>
    </section>
  </main>
</template>

<style scoped>
.n05-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px 16px 36px;
  display: grid;
  gap: 14px;
}
.card {
  background: #fff;
  border: 1px solid #ececec;
  border-radius: 12px;
  padding: 16px;
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
  margin-top: 12px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}
.grid {
  display: grid;
  grid-template-columns: 380px 1fr;
  gap: 14px;
}
.head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.filters {
  margin-top: 10px;
  display: grid;
  grid-template-columns: 130px 1fr auto;
  gap: 8px;
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
  cursor: pointer;
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
.line-1 + p {
  margin-top: 5px;
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
.status {
  margin-top: 12px;
}
.row {
  display: grid;
  grid-template-columns: 140px 1fr auto;
  gap: 8px;
}
input,
select {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 8px 10px;
  font: inherit;
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
@media (max-width: 960px) {
  .grid {
    grid-template-columns: 1fr;
  }
}
</style>
