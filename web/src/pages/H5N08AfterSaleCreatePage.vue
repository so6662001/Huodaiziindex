<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const loading = ref(false)
const detailLoading = ref(false)
const creating = ref(false)
const statusSubmitting = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

const state = reactive({
  pageNo: 1,
  pageSize: 10,
  total: 0,
  channel: 'H5',
  activeDisputeId: '',
  records: []
})

const selectedDisputeId = ref('')
const detail = ref(null)
const statusFilter = ref('')
const keyword = ref('')
const statusAction = ref('')
const statusRemark = ref('')

const form = reactive({
  orderId: '',
  issueType: 'QUALITY',
  issueSummary: '',
  issueDescription: '',
  expectedResolution: '',
  contactName: '',
  contactPhone: '',
  evidenceFiles: '',
  operator: 'h5-n08-after-sale-ui'
})

const token = computed(() => localStorage.getItem('H5_N01_AUTH_TOKEN') || localStorage.getItem('N01_AUTH_TOKEN') || '')
const hasSession = computed(() => token.value.length > 0)
const availableActions = computed(() => (Array.isArray(detail.value?.availableActions) ? detail.value.availableActions : []))
const canCreate = computed(() => {
  return (
    hasSession.value &&
    form.orderId.trim() &&
    form.issueType.trim() &&
    form.issueSummary.trim() &&
    form.issueDescription.trim() &&
    form.contactName.trim() &&
    /^1\d{10}$/.test(form.contactPhone.trim()) &&
    !creating.value
  )
})
const canUpdateStatus = computed(() => hasSession.value && selectedDisputeId.value && statusAction.value && !statusSubmitting.value)

const actionNameMap = {
  MARK_PROCESSING: '标记处理中',
  MARK_RESOLVED: '标记已解决',
  MARK_CLOSED: '关闭争议',
  REOPEN: '重新打开'
}

function actionText(code) {
  return actionNameMap[code] || code
}

function defaultActionByStatus(status) {
  if (status === 'SUBMITTED') return 'MARK_PROCESSING'
  if (status === 'PROCESSING') return 'MARK_RESOLVED'
  if (status === 'RESOLVED') return 'MARK_CLOSED'
  return 'REOPEN'
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

async function loadAfterSales() {
  if (!hasSession.value || loading.value) {
    if (!hasSession.value) {
      errorMsg.value = '请先登录后查看售后发起页'
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
    const resp = await fetch(`/api/v1/auth/h5/after-sales?${params.toString()}`, {
      headers: { 'X-Auth-Token': token.value }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `售后列表加载失败(${resp.status})`)
    }
    const data = json.data || {}
    state.pageNo = Number(data.pageNo || 1)
    state.pageSize = Number(data.pageSize || 10)
    state.total = Number(data.total || 0)
    state.channel = data.channel || 'H5'
    state.activeDisputeId = data.activeDisputeId || ''
    state.records = Array.isArray(data.records) ? data.records : []

    if (!selectedDisputeId.value) {
      selectedDisputeId.value = state.activeDisputeId || state.records[0]?.disputeId || ''
      if (selectedDisputeId.value) await loadDetail()
      return
    }
    const exists = state.records.some((item) => item.disputeId === selectedDisputeId.value)
    if (!exists) {
      selectedDisputeId.value = state.activeDisputeId || state.records[0]?.disputeId || ''
      if (selectedDisputeId.value) {
        await loadDetail()
      } else {
        detail.value = null
        statusAction.value = ''
      }
    }
  } catch (error) {
    errorMsg.value = error.message || '售后列表加载失败'
  } finally {
    loading.value = false
  }
}

async function loadDetail() {
  if (!hasSession.value || !selectedDisputeId.value || detailLoading.value) return
  detailLoading.value = true
  errorMsg.value = ''
  try {
    const resp = await fetch(`/api/v1/auth/h5/after-sales/${selectedDisputeId.value}`, {
      headers: { 'X-Auth-Token': token.value }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `售后详情加载失败(${resp.status})`)
    }
    detail.value = json.data || null
    statusAction.value = defaultActionByStatus(detail.value?.status)
    syncActionFromDetail()
  } catch (error) {
    errorMsg.value = error.message || '售后详情加载失败'
  } finally {
    detailLoading.value = false
  }
}

async function createAfterSale() {
  if (!canCreate.value) return
  creating.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      orderId: form.orderId.trim(),
      issueType: form.issueType.trim(),
      issueSummary: form.issueSummary.trim(),
      issueDescription: form.issueDescription.trim(),
      expectedResolution: form.expectedResolution.trim() || null,
      contactName: form.contactName.trim(),
      contactPhone: form.contactPhone.trim(),
      evidenceFiles: form.evidenceFiles.trim() || null,
      operator: form.operator.trim() || 'h5-n08-after-sale-ui'
    }
    const resp = await fetch('/api/v1/auth/h5/after-sales', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Auth-Token': token.value
      },
      body: JSON.stringify(payload)
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `售后发起失败(${resp.status})`)
    }
    detail.value = json.data || null
    selectedDisputeId.value = detail.value?.disputeId || selectedDisputeId.value
    statusAction.value = defaultActionByStatus(detail.value?.status)
    syncActionFromDetail()
    successMsg.value = '售后争议已发起'
    form.issueSummary = ''
    form.issueDescription = ''
    form.expectedResolution = ''
    form.evidenceFiles = ''
    await loadAfterSales()
  } catch (error) {
    errorMsg.value = error.message || '售后发起失败'
  } finally {
    creating.value = false
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
      operator: 'h5-n08-after-sale-ui'
    }
    const resp = await fetch(`/api/v1/auth/h5/after-sales/${selectedDisputeId.value}/status`, {
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
    statusAction.value = defaultActionByStatus(detail.value?.status)
    syncActionFromDetail()
    successMsg.value = '售后状态更新成功'
    await loadAfterSales()
  } catch (error) {
    errorMsg.value = error.message || '售后状态更新失败'
  } finally {
    statusSubmitting.value = false
  }
}

function chooseDispute(disputeId) {
  selectedDisputeId.value = disputeId
  loadDetail()
}

function goH5Home() {
  router.push('/h5?city=唐山')
}

function goReconcileDetail() {
  router.push('/h5/reconcile-detail')
}

function goQuickLogin() {
  router.push('/h5/login-quick')
}

function goLitePay() {
  router.push('/h5/lite-pay')
}

function goCreditBrief() {
  router.push('/h5/credit-brief')
}

function goMessageSettings() {
  router.push('/h5/message-settings')
}

onMounted(() => {
  loadAfterSales()
})
</script>

<template>
  <main class="h5-n08-page">
    <section class="card hero">
      <h1>H5-N08 售后发起页</h1>
      <p>移动端发起售后争议、查看处理状态并推动售后闭环。</p>
      <div class="hero-actions">
        <button class="btn" @click="goH5Home">返回H5首页</button>
        <button class="btn" @click="goReconcileDetail">前往H5-N07对账详情</button>
        <button class="btn" @click="goLitePay">前往H5-N09轻支付</button>
        <button class="btn" @click="goCreditBrief">前往H5-N10信用分简报</button>
        <button class="btn" @click="goMessageSettings">前往H5-N11消息设置</button>
        <button class="btn" @click="goQuickLogin">返回H5-N01快捷登录</button>
      </div>
    </section>

    <section class="card">
      <div class="head">
        <h2>售后争议列表</h2>
        <button class="btn" :disabled="loading" @click="loadAfterSales">{{ loading ? '刷新中...' : '刷新' }}</button>
      </div>
      <div class="filters">
        <select v-model="statusFilter">
          <option value="">全部状态</option>
          <option value="SUBMITTED">已提交</option>
          <option value="PROCESSING">处理中</option>
          <option value="RESOLVED">已解决</option>
          <option value="CLOSED">已关闭</option>
        </select>
        <input v-model="keyword" placeholder="按争议号/订单号/商家搜索" />
        <button class="btn" :disabled="loading" @click="loadAfterSales">筛选</button>
      </div>
      <p class="tip">渠道：{{ state.channel }} ｜ 共 {{ state.total }} 条</p>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
      <ul class="list">
        <li
          v-for="item in state.records"
          :key="item.disputeId"
          :class="{ active: item.disputeId === selectedDisputeId }"
          @click="chooseDispute(item.disputeId)"
        >
          <div class="line-1">
            <strong>{{ item.disputeId }}</strong>
            <span>{{ item.statusText }}</span>
          </div>
          <p>订单：{{ item.orderNo }}</p>
          <p>问题：{{ item.issueTypeText }}｜{{ item.issueSummary }}</p>
          <p>商家：{{ item.supplierName }}</p>
          <p>快捷动作：{{ item.quickActionText }}</p>
        </li>
      </ul>
    </section>

    <section class="card">
      <h2>发起售后争议</h2>
      <div class="form-grid">
        <input v-model="form.orderId" placeholder="订单ID（如 OD0001）" />
        <select v-model="form.issueType">
          <option value="QUALITY">质量异议</option>
          <option value="DELIVERY_DELAY">交付延迟</option>
          <option value="INVOICE">发票问题</option>
          <option value="PAYMENT">结算问题</option>
          <option value="OTHER">其他问题</option>
        </select>
        <input v-model="form.issueSummary" placeholder="问题摘要（必填）" />
        <input v-model="form.contactName" placeholder="联系人（必填）" />
        <input v-model="form.contactPhone" placeholder="联系人手机号（11位）" />
        <input v-model="form.expectedResolution" placeholder="期望解决方案（可选）" />
        <input v-model="form.evidenceFiles" placeholder="证据URL（可选，多条可逗号分隔）" class="span-2" />
        <textarea
          v-model="form.issueDescription"
          class="span-2"
          rows="3"
          placeholder="问题描述（必填）"
        ></textarea>
      </div>
      <div class="actions">
        <button class="btn btn--primary" :disabled="!canCreate" @click="createAfterSale">
          {{ creating ? '提交中...' : '发起争议' }}
        </button>
      </div>
      <p v-if="successMsg" class="ok">{{ successMsg }}</p>
    </section>

    <section class="card" v-if="detail">
      <div class="head">
        <h2>争议详情</h2>
        <button class="btn" :disabled="detailLoading || !selectedDisputeId" @click="loadDetail">
          {{ detailLoading ? '加载中...' : '刷新详情' }}
        </button>
      </div>
      <div class="meta-grid">
        <p>争议单号：{{ detail.disputeId }}</p>
        <p>订单号：{{ detail.orderNo }}（{{ detail.orderId }}）</p>
        <p>询价号：{{ detail.inquiryNo }}</p>
        <p>供应方：{{ detail.supplierName }}</p>
        <p>问题类型：{{ detail.issueTypeText }}（{{ detail.issueType }}）</p>
        <p>当前状态：{{ detail.statusText }}（{{ detail.status }}）</p>
        <p>联系人：{{ detail.contactName }} / {{ detail.contactPhoneMasked }}</p>
        <p>期望方案：{{ detail.expectedResolution || '-' }}</p>
        <p>证据文件：{{ detail.evidenceFiles || '-' }}</p>
        <p>最新备注：{{ detail.latestRemark || '-' }}</p>
      </div>
      <p class="desc">问题描述：{{ detail.issueDescription }}</p>
      <p class="tip">{{ detail.tipText || '-' }}</p>

      <div class="actions status-actions">
        <select v-model="statusAction">
          <option v-for="action in availableActions" :key="action" :value="action">
            {{ actionText(action) }}
          </option>
        </select>
        <input v-model="statusRemark" placeholder="状态备注（可选）" />
        <button class="btn" :disabled="!canUpdateStatus" @click="updateStatus">
          {{ statusSubmitting ? '提交中...' : '更新状态' }}
        </button>
      </div>
    </section>
  </main>
</template>

<style scoped>
.h5-n08-page {
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
.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 8px;
}
.span-2 {
  grid-column: span 2;
}
.meta-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 6px 10px;
}
.desc {
  margin-top: 10px;
}
.actions {
  margin-top: 10px;
}
.status-actions {
  display: grid;
  grid-template-columns: 160px 1fr auto;
  gap: 8px;
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
select,
textarea {
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
  .form-grid,
  .meta-grid,
  .status-actions {
    grid-template-columns: 1fr;
  }
  .span-2 {
    grid-column: span 1;
  }
}
</style>
