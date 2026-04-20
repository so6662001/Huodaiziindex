<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const loading = ref(false)
const detailLoading = ref(false)
const paying = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

const state = reactive({
  pageNo: 1,
  pageSize: 10,
  total: 0,
  channel: 'H5',
  contactMobileMasked: '',
  activeCashierOrderId: '',
  records: []
})

const selectedCashierOrderId = ref('')
const detail = ref(null)
const statusFilter = ref('')
const keyword = ref('')

const form = reactive({
  payMethod: 'WECHAT',
  payerName: '移动端采购财务',
  remark: '',
  operator: 'h5-n09-lite-pay-ui'
})

const token = computed(() => localStorage.getItem('H5_N01_AUTH_TOKEN') || localStorage.getItem('N01_AUTH_TOKEN') || '')
const hasSession = computed(() => token.value.length > 0)
const canPay = computed(() => {
  return hasSession.value && selectedCashierOrderId.value && detail.value?.payStatus === 'UNPAID' && !paying.value
})
const availablePayMethods = computed(() =>
  Array.isArray(detail.value?.availablePayMethods) ? detail.value.availablePayMethods : []
)

const methodNameMap = {
  BANK_TRANSFER: '对公转账',
  ALIPAY: '支付宝',
  WECHAT: '微信支付',
  UNIONPAY: '银联'
}

function methodText(code) {
  return methodNameMap[code] || code
}

function syncPayMethod() {
  if (availablePayMethods.value.length === 0) {
    return
  }
  if (!availablePayMethods.value.includes(form.payMethod)) {
    form.payMethod = availablePayMethods.value[0]
  }
}

async function loadOrders() {
  if (!hasSession.value || loading.value) {
    if (!hasSession.value) {
      errorMsg.value = '请先登录后查看轻支付页'
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
    const resp = await fetch(`/api/v1/auth/h5/lite-pay/orders?${params.toString()}`, {
      headers: { 'X-Auth-Token': token.value }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `轻支付列表加载失败(${resp.status})`)
    }
    const data = json.data || {}
    state.pageNo = Number(data.pageNo || 1)
    state.pageSize = Number(data.pageSize || 10)
    state.total = Number(data.total || 0)
    state.channel = data.channel || 'H5'
    state.contactMobileMasked = data.contactMobileMasked || ''
    state.activeCashierOrderId = data.activeCashierOrderId || ''
    state.records = Array.isArray(data.records) ? data.records : []

    if (!selectedCashierOrderId.value) {
      selectedCashierOrderId.value = state.activeCashierOrderId || state.records[0]?.cashierOrderId || ''
      if (selectedCashierOrderId.value) await loadDetail()
      return
    }
    const exists = state.records.some((item) => item.cashierOrderId === selectedCashierOrderId.value)
    if (!exists) {
      selectedCashierOrderId.value = state.activeCashierOrderId || state.records[0]?.cashierOrderId || ''
      if (selectedCashierOrderId.value) {
        await loadDetail()
      } else {
        detail.value = null
      }
    }
  } catch (error) {
    errorMsg.value = error.message || '轻支付列表加载失败'
  } finally {
    loading.value = false
  }
}

async function loadDetail() {
  if (!hasSession.value || !selectedCashierOrderId.value || detailLoading.value) return
  detailLoading.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const resp = await fetch(`/api/v1/auth/h5/lite-pay/orders/${selectedCashierOrderId.value}`, {
      headers: { 'X-Auth-Token': token.value }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `轻支付详情加载失败(${resp.status})`)
    }
    detail.value = json.data || null
    syncPayMethod()
  } catch (error) {
    errorMsg.value = error.message || '轻支付详情加载失败'
  } finally {
    detailLoading.value = false
  }
}

async function submitPay() {
  if (!canPay.value) return
  paying.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      payMethod: form.payMethod,
      payerName: form.payerName.trim() || null,
      remark: form.remark.trim() || null,
      operator: form.operator.trim() || 'h5-n09-lite-pay-ui'
    }
    const resp = await fetch(`/api/v1/auth/h5/lite-pay/orders/${selectedCashierOrderId.value}/submit`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Auth-Token': token.value
      },
      body: JSON.stringify(payload)
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `轻支付提交失败(${resp.status})`)
    }
    detail.value = json.data || null
    successMsg.value = detail.value?.quickResultText || '支付已完成'
    form.remark = ''
    await loadOrders()
  } catch (error) {
    errorMsg.value = error.message || '轻支付提交失败'
  } finally {
    paying.value = false
  }
}

function chooseOrder(cashierOrderId) {
  selectedCashierOrderId.value = cashierOrderId
  loadDetail()
}

function goH5Home() {
  router.push('/h5?city=唐山')
}

function goAfterSaleCreate() {
  router.push('/h5/after-sale-create')
}

function goCreditBrief() {
  router.push('/h5/credit-brief')
}

function goMessageSettings() {
  router.push('/h5/message-settings')
}

function goQuickLogin() {
  router.push('/h5/login-quick')
}

onMounted(() => {
  loadOrders()
})
</script>

<template>
  <main class="h5-n09-page">
    <section class="card hero">
      <h1>H5-N09 轻支付页</h1>
      <p>移动端快速完成收银单支付，实时同步订单履约状态。</p>
      <div class="hero-actions">
        <button class="btn" @click="goH5Home">返回H5首页</button>
        <button class="btn" @click="goAfterSaleCreate">前往H5-N08售后发起</button>
        <button class="btn" @click="goCreditBrief">前往H5-N10信用分简报</button>
        <button class="btn" @click="goMessageSettings">前往H5-N11消息设置</button>
        <button class="btn" @click="goQuickLogin">返回H5-N01快捷登录</button>
      </div>
    </section>

    <section class="card">
      <div class="head">
        <h2>轻支付收银单列表</h2>
        <button class="btn" :disabled="loading" @click="loadOrders">{{ loading ? '刷新中...' : '刷新' }}</button>
      </div>
      <div class="filters">
        <select v-model="statusFilter">
          <option value="">全部状态</option>
          <option value="UNPAID">待支付</option>
          <option value="PAID">已支付</option>
        </select>
        <input v-model="keyword" placeholder="按收银单号/订单号/商家搜索" />
        <button class="btn" :disabled="loading" @click="loadOrders">筛选</button>
      </div>
      <p class="tip">渠道：{{ state.channel }} ｜ 登录手机号：{{ state.contactMobileMasked || '-' }} ｜ 共 {{ state.total }} 条</p>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
      <ul class="list">
        <li
          v-for="item in state.records"
          :key="item.cashierOrderId"
          :class="{ active: item.cashierOrderId === selectedCashierOrderId }"
          @click="chooseOrder(item.cashierOrderId)"
        >
          <div class="line-1">
            <strong>{{ item.cashierOrderId }}</strong>
            <span>{{ item.payStatusText }}</span>
          </div>
          <p>订单：{{ item.orderNo }} ｜ 询价：{{ item.inquiryNo }}</p>
          <p>商家：{{ item.supplierName }}</p>
          <p>货品：{{ item.goodsName }}</p>
          <p>应付：¥{{ item.payableAmount }} ｜ 已付：¥{{ item.paidAmount }} ｜ 未付：¥{{ item.outstandingAmount }}</p>
          <p>快捷动作：{{ item.quickActionText }}</p>
        </li>
      </ul>
    </section>

    <section class="card" v-if="detail">
      <div class="head">
        <h2>轻支付详情</h2>
        <button class="btn" :disabled="detailLoading || !selectedCashierOrderId" @click="loadDetail">
          {{ detailLoading ? '加载中...' : '刷新详情' }}
        </button>
      </div>
      <p v-if="successMsg" class="ok">{{ successMsg }}</p>
      <div class="meta-grid">
        <p>收银单号：{{ detail.cashierOrderId }}</p>
        <p>订单号：{{ detail.orderNo }}（{{ detail.orderId }}）</p>
        <p>询价号：{{ detail.inquiryNo }}</p>
        <p>供应方：{{ detail.supplierName }}</p>
        <p>货品：{{ detail.goodsName }}</p>
        <p>支付状态：{{ detail.payStatusText }}（{{ detail.payStatus }}）</p>
        <p>支付渠道：{{ detail.payChannelText || '-' }}（{{ detail.payChannel || '-' }}）</p>
        <p>轻支付结果：{{ detail.quickResultText }}（{{ detail.quickResultStatus }}）</p>
        <p>应付：¥{{ detail.payableAmount }}</p>
        <p>已付：¥{{ detail.paidAmount }}</p>
        <p>未付：¥{{ detail.outstandingAmount }}</p>
        <p>支付截止：{{ detail.dueAt || '-' }}</p>
        <p>支付时间：{{ detail.paidAt || '-' }}</p>
        <p>最新备注：{{ detail.latestRemark || '-' }}</p>
      </div>
      <p class="tip">{{ detail.tipText || '-' }}</p>

      <section class="block">
        <h3>发起轻支付</h3>
        <div class="pay-row">
          <select v-model="form.payMethod">
            <option v-for="method in availablePayMethods" :key="method" :value="method">
              {{ methodText(method) }}
            </option>
          </select>
          <input v-model="form.payerName" placeholder="付款人（可选）" />
          <input v-model="form.remark" placeholder="支付备注（可选）" />
          <button class="btn btn--primary" :disabled="!canPay" @click="submitPay">
            {{ paying ? '支付中...' : detail.payStatus === 'PAID' ? '已支付' : '确认支付' }}
          </button>
        </div>
      </section>

      <section class="block">
        <h3>支付时间线</h3>
        <ul class="timeline">
          <li v-for="node in detail.timeline" :key="`${node.nodeCode}-${node.happenedAt}`">
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
    </section>
  </main>
</template>

<style scoped>
.h5-n09-page {
  max-width: 760px;
  margin: 0 auto;
  padding: 12px 12px 28px;
  display: grid;
  gap: 12px;
}
.card {
  background: #fff;
  border: 1px solid #ececec;
  border-radius: 12px;
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
  grid-template-columns: 130px 1fr auto;
  gap: 8px;
}
.list {
  list-style: none;
  margin: 10px 0 0;
  padding: 0;
  display: grid;
  gap: 8px;
}
.list li {
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  padding: 10px;
}
.list li.active {
  border-color: #f57c00;
  background: #fffaf3;
}
.line-1 {
  display: flex;
  justify-content: space-between;
  gap: 8px;
}
.meta-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 6px 10px;
}
.block {
  margin-top: 12px;
}
.pay-row {
  display: grid;
  grid-template-columns: 160px 1fr 1fr auto;
  gap: 8px;
}
.timeline {
  list-style: none;
  margin: 0;
  padding: 0;
  display: grid;
  gap: 8px;
}
.timeline li {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 8px;
}
.btn {
  height: 34px;
  border-radius: 8px;
  border: 1px solid #d1d5db;
  background: #fff;
  padding: 0 12px;
  cursor: pointer;
}
.btn--primary {
  border-color: #f57c00;
  background: #f57c00;
  color: #fff;
}
input,
select {
  border: 1px solid #d1d5db;
  border-radius: 8px;
  padding: 8px 10px;
}
input,
select {
  height: 34px;
}
.tip {
  color: #6b7280;
}
.error {
  color: #b91c1c;
}
.ok {
  color: #166534;
}
@media (max-width: 768px) {
  .filters,
  .meta-grid,
  .pay-row {
    grid-template-columns: 1fr;
  }
}
</style>
