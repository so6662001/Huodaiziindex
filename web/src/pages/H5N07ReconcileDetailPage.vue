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
  channel: 'H5',
  contactMobileMasked: '',
  activeReconcileId: '',
  records: []
})

const selectedReconcileId = ref('')
const detail = ref(null)
const statusFilter = ref('')
const keyword = ref('')
const statusAction = ref('')
const paidAmount = ref('')
const statusRemark = ref('')

const token = computed(() => localStorage.getItem('H5_N01_AUTH_TOKEN') || localStorage.getItem('N01_AUTH_TOKEN') || '')
const hasSession = computed(() => token.value.length > 0)
const availableActions = computed(() => (Array.isArray(detail.value?.availableActions) ? detail.value.availableActions : []))
const canUpdateStatus = computed(() => hasSession.value && selectedReconcileId.value && statusAction.value && !statusSubmitting.value)

const actionNameMap = {
  CONFIRMED: '标记已确认',
  PARTIAL_PAID: '登记部分回款',
  PAID: '标记已回款',
  CLOSED: '关闭对账单',
  DISPUTED: '标记争议中'
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

async function loadReconciles() {
  if (!hasSession.value || loading.value) {
    if (!hasSession.value) {
      errorMsg.value = '请先登录后查看对账详情'
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
    const resp = await fetch(`/api/v1/auth/h5/reconciles?${params.toString()}`, {
      headers: { 'X-Auth-Token': token.value }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `对账列表加载失败(${resp.status})`)
    }
    const data = json.data || {}
    state.pageNo = Number(data.pageNo || 1)
    state.pageSize = Number(data.pageSize || 10)
    state.total = Number(data.total || 0)
    state.channel = data.channel || 'H5'
    state.contactMobileMasked = data.contactMobileMasked || ''
    state.activeReconcileId = data.activeReconcileId || ''
    state.records = Array.isArray(data.records) ? data.records : []

    if (!selectedReconcileId.value) {
      selectedReconcileId.value = state.activeReconcileId || state.records[0]?.reconcileId || ''
      if (selectedReconcileId.value) await loadDetail()
      return
    }
    const exists = state.records.some((item) => item.reconcileId === selectedReconcileId.value)
    if (!exists) {
      selectedReconcileId.value = state.activeReconcileId || state.records[0]?.reconcileId || ''
      if (selectedReconcileId.value) {
        await loadDetail()
      } else {
        detail.value = null
        statusAction.value = ''
      }
    }
  } catch (error) {
    errorMsg.value = error.message || '对账列表加载失败'
  } finally {
    loading.value = false
  }
}

async function loadDetail() {
  if (!hasSession.value || !selectedReconcileId.value || detailLoading.value) return
  detailLoading.value = true
  errorMsg.value = ''
  try {
    const resp = await fetch(`/api/v1/auth/h5/reconciles/${selectedReconcileId.value}`, {
      headers: { 'X-Auth-Token': token.value }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `对账详情加载失败(${resp.status})`)
    }
    detail.value = json.data || null
    syncActionFromDetail()
  } catch (error) {
    errorMsg.value = error.message || '对账详情加载失败'
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
      status: statusAction.value,
      paidAmount: paidAmount.value.trim() || null,
      operator: 'h5-n07-reconcile-ui',
      remark: statusRemark.value.trim() || null
    }
    const resp = await fetch(`/api/v1/auth/h5/reconciles/${selectedReconcileId.value}/status`, {
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
    paidAmount.value = ''
    statusRemark.value = ''
    syncActionFromDetail()
    successMsg.value = '对账状态更新成功'
    await loadReconciles()
  } catch (error) {
    errorMsg.value = error.message || '对账状态更新失败'
  } finally {
    statusSubmitting.value = false
  }
}

function chooseReconcile(reconcileId) {
  selectedReconcileId.value = reconcileId
  loadDetail()
}

function goH5Home() {
  router.push('/h5?city=唐山')
}

function goPickupScan() {
  router.push('/h5/pickup-scan')
}

function goOrderDetail() {
  router.push('/h5/order-detail')
}

function goQuickLogin() {
  router.push('/h5/login-quick')
}

onMounted(() => {
  loadReconciles()
})
</script>

<template>
  <main class="h5-n07-page">
    <section class="card hero">
      <h1>H5-N07 对账详情页</h1>
      <p>移动端查看对账单台账与回款进度，支持状态流转与金额登记。</p>
      <div class="hero-actions">
        <button class="btn" @click="goH5Home">返回H5首页</button>
        <button class="btn" @click="goOrderDetail">前往H5-N05订单详情</button>
        <button class="btn" @click="goPickupScan">前往H5-N06扫码提货</button>
        <button class="btn" @click="goQuickLogin">返回H5-N01快捷登录</button>
      </div>
    </section>

    <section class="card">
      <div class="head">
        <h2>对账单列表</h2>
        <button class="btn" :disabled="loading" @click="loadReconciles">{{ loading ? '刷新中...' : '刷新' }}</button>
      </div>
      <div class="filters">
        <select v-model="statusFilter">
          <option value="">全部状态</option>
          <option value="CREATED">已创建</option>
          <option value="INVOICE_PENDING">待开票</option>
          <option value="INVOICED">已开票</option>
          <option value="CONFIRMED">已确认</option>
          <option value="PARTIAL_PAID">部分回款</option>
          <option value="PAID">已回款</option>
          <option value="CLOSED">已关闭</option>
          <option value="DISPUTED">争议中</option>
        </select>
        <input v-model="keyword" placeholder="按对账单号/提货单号/供应商搜索" />
        <button class="btn" :disabled="loading" @click="loadReconciles">筛选</button>
      </div>
      <p class="tip">渠道：{{ state.channel }} ｜ 登录手机号：{{ state.contactMobileMasked || '-' }} ｜ 共 {{ state.total }} 条</p>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
      <ul class="reconcile-list">
        <li
          v-for="item in state.records"
          :key="item.reconcileId"
          :class="{ active: item.reconcileId === selectedReconcileId }"
          @click="chooseReconcile(item.reconcileId)"
        >
          <div class="line-1">
            <strong>{{ item.reconcileNo }}</strong>
            <span>{{ item.statusText }}</span>
          </div>
          <p>提货单：{{ item.pickupOrderNo }} ｜ 询价单：{{ item.inquiryNo }}</p>
          <p>商家：{{ item.supplierName }}</p>
          <p>货物：{{ item.goodsSummary }}</p>
          <p>应收：{{ item.totalAmount }} ｜ 已收：{{ item.paidAmount }} ｜ 未收：{{ item.unpaidAmount }}</p>
          <p>快捷动作：{{ item.quickActionText }}</p>
        </li>
      </ul>
    </section>

    <section class="card">
      <div class="head">
        <h2>对账详情</h2>
        <button class="btn" :disabled="detailLoading || !selectedReconcileId" @click="loadDetail">
          {{ detailLoading ? '加载中...' : '刷新详情' }}
        </button>
      </div>
      <p v-if="successMsg" class="ok">{{ successMsg }}</p>
      <div v-if="detail" class="detail">
        <p>对账单号：{{ detail.reconcileNo }}</p>
        <p>提货单号：{{ detail.pickupOrderNo }} ｜ 询价单号：{{ detail.inquiryNo }}</p>
        <p>商家：{{ detail.supplierName }} ｜ 买方：{{ detail.buyerCompany }}</p>
        <p>货物：{{ detail.goodsSummary }}</p>
        <p>账期：{{ detail.statementMonth }} ｜ 到期日：{{ detail.dueDate }}</p>
        <p>对账状态：{{ detail.statusText }}（{{ detail.status }}）</p>
        <p>票据状态：{{ detail.invoiceStatusText }}（{{ detail.invoiceStatus }}）</p>
        <p>应收：{{ detail.receivableAmount }} ｜ 已收：{{ detail.paidAmount }} ｜ 未收：{{ detail.outstandingAmount }}</p>
        <p>最新备注：{{ detail.latestRemark || '-' }}</p>
        <p class="tip">{{ detail.tipText || '-' }}</p>

        <section class="block">
          <h3>状态流转</h3>
          <div class="row">
            <select v-model="statusAction">
              <option v-for="action in availableActions" :key="action" :value="action">
                {{ actionText(action) }}
              </option>
            </select>
            <input v-model="paidAmount" placeholder="回款金额（可选）" />
            <input v-model="statusRemark" placeholder="备注（可选）" />
            <button class="btn btn--primary" :disabled="!canUpdateStatus" @click="updateStatus">
              {{ statusSubmitting ? '提交中...' : '更新状态' }}
            </button>
          </div>
          <p class="tip" v-if="!availableActions.length">当前状态无可用动作</p>
        </section>
      </div>
      <p v-else class="tip">请选择对账单查看详情</p>
    </section>
  </main>
</template>

<style scoped>
.h5-n07-page {
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
input,
select {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 8px 10px;
  font: inherit;
}
.reconcile-list {
  list-style: none;
  margin: 10px 0 0;
  padding: 0;
  display: grid;
  gap: 8px;
}
.reconcile-list li {
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  padding: 10px;
}
.reconcile-list li.active {
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
.reconcile-list p,
.detail p {
  margin: 4px 0;
  color: #4b5563;
}
.block {
  margin-top: 12px;
}
.row {
  display: grid;
  grid-template-columns: 160px 160px 1fr auto;
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
