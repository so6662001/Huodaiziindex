<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const loading = ref(false)
const detailLoading = ref(false)
const scanSubmitting = ref(false)
const statusSubmitting = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

const state = reactive({
  pageNo: 1,
  pageSize: 10,
  total: 0,
  channel: 'H5',
  contactMobileMasked: '',
  activePickupId: '',
  records: []
})

const selectedPickupId = ref('')
const detail = ref(null)
const statusFilter = ref('')
const keyword = ref('')
const scanCode = ref('')
const scanRemark = ref('')
const statusAction = ref('')
const statusRemark = ref('')

const token = computed(() => localStorage.getItem('H5_N01_AUTH_TOKEN') || localStorage.getItem('N01_AUTH_TOKEN') || '')
const hasSession = computed(() => token.value.length > 0)
const canScan = computed(() => hasSession.value && scanCode.value.trim().length > 0 && !scanSubmitting.value)
const canUpdateStatus = computed(() => hasSession.value && selectedPickupId.value && statusAction.value && !statusSubmitting.value)
const availableActions = computed(() => (Array.isArray(detail.value?.availableActions) ? detail.value.availableActions : []))

const actionNameMap = {
  CONFIRMED: '确认提货',
  IN_TRANSIT: '标记运输中',
  SIGNED: '标记已签收',
  COMPLETED: '标记已完成',
  CANCELLED: '取消提货'
}

function actionText(code) {
  return actionNameMap[code] || code
}

function syncActionFromDetail() {
  if (availableActions.value.length > 0) {
    if (!availableActions.value.includes(statusAction.value)) {
      statusAction.value = availableActions.value[0]
    }
    return
  }
  statusAction.value = ''
}

async function loadPickups() {
  if (!hasSession.value || loading.value) {
    if (!hasSession.value) {
      errorMsg.value = '请先登录后查看扫码提货页'
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
    const resp = await fetch(`/api/v1/auth/h5/pickups?${params.toString()}`, {
      headers: { 'X-Auth-Token': token.value }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `提货单列表加载失败(${resp.status})`)
    }
    const data = json.data || {}
    state.pageNo = Number(data.pageNo || 1)
    state.pageSize = Number(data.pageSize || 10)
    state.total = Number(data.total || 0)
    state.channel = data.channel || 'H5'
    state.contactMobileMasked = data.contactMobileMasked || ''
    state.activePickupId = data.activePickupId || ''
    state.records = Array.isArray(data.records) ? data.records : []

    if (!selectedPickupId.value) {
      selectedPickupId.value = state.activePickupId || state.records[0]?.pickupId || ''
      if (selectedPickupId.value) await loadDetail()
      return
    }
    const exists = state.records.some((item) => item.pickupId === selectedPickupId.value)
    if (!exists) {
      selectedPickupId.value = state.activePickupId || state.records[0]?.pickupId || ''
      if (selectedPickupId.value) {
        await loadDetail()
      } else {
        detail.value = null
        statusAction.value = ''
      }
    }
  } catch (error) {
    errorMsg.value = error.message || '提货单列表加载失败'
  } finally {
    loading.value = false
  }
}

async function loadDetail() {
  if (!hasSession.value || !selectedPickupId.value || detailLoading.value) return
  detailLoading.value = true
  errorMsg.value = ''
  try {
    const resp = await fetch(`/api/v1/auth/h5/pickups/${selectedPickupId.value}`, {
      headers: { 'X-Auth-Token': token.value }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `提货单详情加载失败(${resp.status})`)
    }
    detail.value = json.data || null
    syncActionFromDetail()
  } catch (error) {
    errorMsg.value = error.message || '提货单详情加载失败'
  } finally {
    detailLoading.value = false
  }
}

async function submitScan() {
  if (!canScan.value) return
  scanSubmitting.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      scanCode: scanCode.value.trim(),
      operator: 'h5-n06-pickup-ui',
      remark: scanRemark.value.trim() || null
    }
    const resp = await fetch('/api/v1/auth/h5/pickups/scan', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Auth-Token': token.value
      },
      body: JSON.stringify(payload)
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `扫码失败(${resp.status})`)
    }
    detail.value = json.data || null
    selectedPickupId.value = detail.value?.pickupId || selectedPickupId.value
    scanCode.value = ''
    scanRemark.value = ''
    syncActionFromDetail()
    successMsg.value = '扫码核验成功'
    await loadPickups()
  } catch (error) {
    errorMsg.value = error.message || '扫码失败'
  } finally {
    scanSubmitting.value = false
  }
}

async function updateStatus() {
  if (!canUpdateStatus.value) return
  statusSubmitting.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      status: statusAction.value,
      operator: 'h5-n06-pickup-ui',
      remark: statusRemark.value.trim() || null
    }
    const resp = await fetch(`/api/v1/auth/h5/pickups/${selectedPickupId.value}/status`, {
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
    statusRemark.value = ''
    syncActionFromDetail()
    successMsg.value = '提货状态更新成功'
    await loadPickups()
  } catch (error) {
    errorMsg.value = error.message || '提货状态更新失败'
  } finally {
    statusSubmitting.value = false
  }
}

function choosePickup(pickupId) {
  selectedPickupId.value = pickupId
  loadDetail()
}

function goH5Home() {
  router.push('/h5?city=唐山')
}

function goOrderDetail() {
  router.push('/h5/order-detail')
}

function goReconcileDetail() {
  router.push('/h5/reconcile-detail')
}

function goAfterSaleCreate() {
  router.push('/h5/after-sale-create')
}

function goQuoteSession() {
  router.push('/h5/quote-session')
}

function goQuickLogin() {
  router.push('/h5/login-quick')
}

onMounted(() => {
  loadPickups()
})
</script>

<template>
  <main class="h5-n06-page">
    <section class="card hero">
      <h1>H5-N06 扫码提货页</h1>
      <p>移动端扫码核验提货单，支持状态流转与履约节点跟踪。</p>
      <div class="hero-actions">
        <button class="btn" @click="goH5Home">返回H5首页</button>
        <button class="btn" @click="goQuickLogin">前往H5-N01快捷登录</button>
        <button class="btn" @click="goQuoteSession">前往H5-N04报价会话</button>
        <button class="btn" @click="goOrderDetail">前往H5-N05订单详情</button>
        <button class="btn" @click="goReconcileDetail">前往H5-N07对账详情</button>
        <button class="btn" @click="goAfterSaleCreate">前往H5-N08售后发起</button>
      </div>
    </section>

    <section class="card">
      <h2>扫码核验</h2>
      <div class="scan-row">
        <input v-model="scanCode" placeholder="请输入扫码内容（示例：PU-20260420-PU20260418001 或 PU20260418001）" />
        <button class="btn btn--primary" :disabled="!canScan" @click="submitScan">
          {{ scanSubmitting ? '核验中...' : '扫码核验' }}
        </button>
      </div>
      <input v-model="scanRemark" placeholder="扫码备注（可选）" />
      <p class="tip">当前渠道：{{ state.channel }} ｜ 登录手机号：{{ state.contactMobileMasked || '-' }}</p>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
      <p v-if="successMsg" class="ok">{{ successMsg }}</p>
    </section>

    <section class="card">
      <div class="head">
        <h2>提货单列表</h2>
        <button class="btn" :disabled="loading" @click="loadPickups">{{ loading ? '刷新中...' : '刷新' }}</button>
      </div>
      <div class="filters">
        <select v-model="statusFilter">
          <option value="">全部状态</option>
          <option value="CREATED">待确认</option>
          <option value="CONFIRMED">已确认</option>
          <option value="IN_TRANSIT">运输中</option>
          <option value="SIGNED">已签收</option>
          <option value="COMPLETED">已完成</option>
          <option value="CANCELLED">已取消</option>
        </select>
        <input v-model="keyword" placeholder="按提货单号/询价号/供应商搜索" />
        <button class="btn" :disabled="loading" @click="loadPickups">筛选</button>
      </div>
      <p class="tip">共 {{ state.total }} 条</p>
      <ul class="pickup-list">
        <li
          v-for="item in state.records"
          :key="item.pickupId"
          :class="{ active: item.pickupId === selectedPickupId }"
          @click="choosePickup(item.pickupId)"
        >
          <div class="line-1">
            <strong>{{ item.pickupNo }}</strong>
            <span>{{ item.statusText }}</span>
          </div>
          <p>{{ item.goodsSummary }}</p>
          <p>供应商：{{ item.supplierName }} ｜ 车牌：{{ item.truckNo }}</p>
          <p>提货点：{{ item.pickupAddress }} ｜ 日期：{{ item.pickupDate }}</p>
          <p>快捷动作：{{ item.quickActionText }}</p>
        </li>
      </ul>
    </section>

    <section class="card">
      <div class="head">
        <h2>提货单详情</h2>
        <button class="btn" :disabled="detailLoading || !selectedPickupId" @click="loadDetail">
          {{ detailLoading ? '加载中...' : '刷新详情' }}
        </button>
      </div>

      <div v-if="detail" class="detail">
        <p>提货单号：{{ detail.pickupNo }}</p>
        <p>询价号：{{ detail.inquiryNo }} ｜ 报价号：{{ detail.quoteId }}</p>
        <p>货物：{{ detail.goodsSummary }}</p>
        <p>供应商：{{ detail.supplierName }} ｜ 买方：{{ detail.buyerCompany }}</p>
        <p>提货状态：{{ detail.statusText }}（{{ detail.status }}）</p>
        <p>提货日期：{{ detail.pickupDate }} ｜ 提货点：{{ detail.pickupAddress }}</p>
        <p>司机：{{ detail.driverName }}（{{ detail.driverPhoneMasked }}）</p>
        <p>车牌：{{ detail.truckNo }}</p>
        <p>扫码结果：{{ detail.scanResult || '-' }}</p>
        <p>最新备注：{{ detail.latestRemark || '-' }}</p>

        <section class="block">
          <h3>状态流转</h3>
          <div class="row">
            <select v-model="statusAction">
              <option v-for="action in availableActions" :key="action" :value="action">
                {{ actionText(action) }}
              </option>
            </select>
            <input v-model="statusRemark" placeholder="状态备注（可选）" />
            <button class="btn btn--primary" :disabled="!canUpdateStatus" @click="updateStatus">
              {{ statusSubmitting ? '提交中...' : '更新状态' }}
            </button>
          </div>
          <p class="tip" v-if="!availableActions.length">当前状态无可用动作</p>
        </section>
      </div>
      <p v-else class="tip">请选择提货单查看详情</p>
    </section>
  </main>
</template>

<style scoped>
.h5-n06-page {
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
  grid-template-columns: 140px 1fr auto;
  gap: 8px;
}
.scan-row {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 8px;
  margin-bottom: 8px;
}
input,
select {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 8px 10px;
  font: inherit;
}
.pickup-list {
  list-style: none;
  margin: 10px 0 0;
  padding: 0;
  display: grid;
  gap: 8px;
}
.pickup-list li {
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  padding: 10px;
}
.pickup-list li.active {
  border-color: #f57c00;
  background: #fffaf3;
}
.line-1 {
  display: flex;
  justify-content: space-between;
  gap: 8px;
}
.line-1 + p {
  margin-top: 6px;
}
.pickup-list p,
.detail p {
  margin: 4px 0;
  color: #4b5563;
}
.block {
  margin-top: 12px;
}
.row {
  display: grid;
  grid-template-columns: 160px 1fr auto;
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
  .scan-row,
  .filters,
  .row {
    grid-template-columns: 1fr;
  }
}
</style>
