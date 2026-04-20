<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const loading = ref(false)
const detailLoading = ref(false)
const statusSubmitting = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

const state = reactive({
  pageNo: 1,
  pageSize: 10,
  total: 0,
  activeOrderId: '',
  channel: 'H5',
  records: []
})

const selectedOrderId = ref('')
const detail = ref(null)
const statusFilter = ref('')
const keyword = ref('')
const actionCode = ref('')
const actionRemark = ref('')

const token = computed(() => localStorage.getItem('H5_N01_AUTH_TOKEN') || localStorage.getItem('N01_AUTH_TOKEN') || '')
const hasSession = computed(() => token.value.length > 0)
const canUpdateStatus = computed(
  () => hasSession.value && selectedOrderId.value && actionCode.value && !statusSubmitting.value
)
const detailActions = computed(() =>
  Array.isArray(detail.value?.actions) ? detail.value.actions.filter((item) => item.enabled) : []
)

function syncActionFromDetail() {
  const availableActions = Array.isArray(detail.value?.availableActions) ? detail.value.availableActions : []
  if (availableActions.length > 0) {
    actionCode.value = availableActions[0]
    return
  }
  actionCode.value = detailActions.value[0]?.actionCode || ''
}

async function loadOrders() {
  if (!hasSession.value || loading.value) {
    if (!hasSession.value) {
      errorMsg.value = '请先登录后查看订单详情'
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

    const resp = await fetch(`/api/v1/auth/h5/orders?${params.toString()}`, {
      headers: { 'X-Auth-Token': token.value }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `订单列表加载失败(${resp.status})`)
    }
    const data = json.data || {}
    state.pageNo = data.pageNo || 1
    state.pageSize = data.pageSize || 10
    state.total = data.total || 0
    state.activeOrderId = data.activeOrderId || ''
    state.channel = data.channel || 'H5'
    state.records = Array.isArray(data.records) ? data.records : []

    if (!selectedOrderId.value) {
      selectedOrderId.value = state.activeOrderId || state.records[0]?.orderId || ''
      if (selectedOrderId.value) await loadDetail()
      return
    }
    const exists = state.records.some((item) => item.orderId === selectedOrderId.value)
    if (!exists) {
      selectedOrderId.value = state.activeOrderId || state.records[0]?.orderId || ''
      if (selectedOrderId.value) {
        await loadDetail()
      } else {
        detail.value = null
        actionCode.value = ''
      }
    }
  } catch (error) {
    errorMsg.value = error.message || '订单列表加载失败'
  } finally {
    loading.value = false
  }
}

async function loadDetail() {
  if (!hasSession.value || !selectedOrderId.value || detailLoading.value) return
  detailLoading.value = true
  errorMsg.value = ''
  try {
    const resp = await fetch(`/api/v1/auth/h5/orders/${selectedOrderId.value}`, {
      headers: { 'X-Auth-Token': token.value }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `订单详情加载失败(${resp.status})`)
    }
    detail.value = json.data || null
    syncActionFromDetail()
  } catch (error) {
    errorMsg.value = error.message || '订单详情加载失败'
  } finally {
    detailLoading.value = false
  }
}

async function updateStatus() {
  if (!canUpdateStatus.value) return
  statusSubmitting.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      action: actionCode.value,
      remark: actionRemark.value.trim() || null,
      operator: 'h5-n05-order-ui'
    }
    const resp = await fetch(`/api/v1/auth/h5/orders/${selectedOrderId.value}/status`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Auth-Token': token.value
      },
      body: JSON.stringify(payload)
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `订单状态更新失败(${resp.status})`)
    }
    detail.value = json.data || null
    actionRemark.value = ''
    syncActionFromDetail()
    successMsg.value = '订单状态更新成功'
    await loadOrders()
  } catch (error) {
    errorMsg.value = error.message || '订单状态更新失败'
  } finally {
    statusSubmitting.value = false
  }
}

function chooseOrder(orderId) {
  selectedOrderId.value = orderId
  loadDetail()
}

function goH5Home() {
  router.push('/h5?city=唐山')
}

function goQuoteSession() {
  router.push('/h5/quote-session')
}

function goEnterpriseCertification() {
  router.push('/h5/enterprise-certification')
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

function goLitePay() {
  router.push('/h5/lite-pay')
}

onMounted(() => {
  loadOrders()
})
</script>

<template>
  <main class="h5-n05-page">
    <section class="card hero">
      <h1>H5-N05 订单详情页</h1>
      <p>移动端查看订单履约进度、回款状态与关键时间线，支持一键状态流转。</p>
      <div class="hero-actions">
        <button class="btn" @click="goH5Home">返回H5首页</button>
        <button class="btn" @click="goQuoteSession">前往H5-N04报价会话</button>
        <button class="btn" @click="goEnterpriseCertification">前往H5-N03企业认证</button>
        <button class="btn" @click="goPickupScan">前往H5-N06扫码提货</button>
        <button class="btn" @click="goReconcileDetail">前往H5-N07对账详情</button>
        <button class="btn" @click="goAfterSaleCreate">前往H5-N08售后发起</button>
        <button class="btn" @click="goLitePay">前往H5-N09轻支付</button>
      </div>
    </section>

    <section class="card">
      <div class="head">
        <h2>订单列表</h2>
        <button class="btn" :disabled="loading" @click="loadOrders">{{ loading ? '刷新中...' : '刷新' }}</button>
      </div>
      <div class="filters">
        <select v-model="statusFilter">
          <option value="">全部状态</option>
          <option value="PENDING_SIGN">待签署</option>
          <option value="SIGNED">已签署</option>
          <option value="PICKUP_IN_PROGRESS">提货中</option>
          <option value="RECONCILING">对账中</option>
          <option value="COMPLETED">已完成</option>
          <option value="CANCELLED">已取消</option>
        </select>
        <input v-model="keyword" placeholder="按订单号/询价号/供应商搜索" />
        <button class="btn" :disabled="loading" @click="loadOrders">筛选</button>
      </div>
      <p class="tip">渠道：{{ state.channel }} ｜ 共 {{ state.total }} 条</p>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
      <ul class="order-list">
        <li
          v-for="item in state.records"
          :key="item.orderId"
          :class="{ active: item.orderId === selectedOrderId }"
          @click="chooseOrder(item.orderId)"
        >
          <div class="line-1">
            <strong>{{ item.orderNo }}</strong>
            <span>{{ item.orderStatusText }}</span>
          </div>
          <p>{{ item.goodsName }}</p>
          <p>{{ item.quantityText }} ｜ 供应商：{{ item.supplierName }}</p>
          <p>成交额：¥{{ item.dealAmount }} ｜ 快捷动作：{{ item.quickActionText }}</p>
        </li>
      </ul>
    </section>

    <section class="card">
      <div class="head">
        <h2>订单详情</h2>
        <button class="btn" :disabled="detailLoading || !selectedOrderId" @click="loadDetail">
          {{ detailLoading ? '加载中...' : '刷新详情' }}
        </button>
      </div>
      <p v-if="successMsg" class="ok">{{ successMsg }}</p>
      <div v-if="detail" class="detail">
        <p>订单号：{{ detail.orderNo }}</p>
        <p>询价号：{{ detail.inquiryNo }}</p>
        <p>货品：{{ detail.goodsName }} / {{ detail.specText }}</p>
        <p>数量：{{ detail.quantityTon }}</p>
        <p>供应商：{{ detail.supplierName }} ｜ 买方：{{ detail.buyerCompany }}</p>
        <p>订单状态：{{ detail.orderStatusText }}（{{ detail.orderStatus }}）</p>
        <p>合同状态：{{ detail.contractStatusText }}（{{ detail.contractStatus }}）</p>
        <p>回款状态：{{ detail.paymentStatusText }}（{{ detail.paymentStatus }}）</p>
        <p>应收：¥{{ detail.receivableAmount }} ｜ 已收：¥{{ detail.paidAmount }} ｜ 待收：¥{{ detail.outstandingAmount }}</p>
        <p>预计交付：{{ detail.expectedDeliveryAt || '-' }}</p>
        <p>最新备注：{{ detail.latestRemark || '-' }}</p>

        <section class="block">
          <h3>履约时间线</h3>
          <ul class="timeline">
            <li v-for="node in detail.timeline" :key="node.nodeCode">
              <div class="line-1">
                <strong>{{ node.nodeName }}</strong>
                <span>{{ node.statusText }}</span>
              </div>
              <p>状态：{{ node.status }} ｜ 责任方：{{ node.owner }}</p>
              <p>时间：{{ node.happenedAt || '-' }}</p>
              <p>备注：{{ node.remark || '-' }}</p>
            </li>
          </ul>
        </section>

        <section class="block">
          <h3>状态流转</h3>
          <div class="row">
            <select v-model="actionCode">
              <option v-for="action in detailActions" :key="action.actionCode" :value="action.actionCode">
                {{ action.actionName }}
              </option>
            </select>
            <input v-model="actionRemark" placeholder="操作备注（可选）" />
            <button class="btn btn--primary" :disabled="!canUpdateStatus" @click="updateStatus">
              {{ statusSubmitting ? '提交中...' : '更新状态' }}
            </button>
          </div>
        </section>
      </div>
      <p v-else class="tip">请选择订单查看详情</p>
    </section>
  </main>
</template>

<style scoped>
.h5-n05-page {
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
.order-list {
  list-style: none;
  margin: 10px 0 0;
  padding: 0;
  display: grid;
  gap: 8px;
}
.order-list li {
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  padding: 10px;
}
.order-list li.active {
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
.block {
  margin-top: 12px;
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
.row {
  display: grid;
  grid-template-columns: 150px 1fr auto;
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
