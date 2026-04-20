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

const records = ref([])
const selectedDisputeId = ref('')
const detail = ref(null)

const statusFilter = ref('')
const keyword = ref('')
const pageNo = ref(1)
const pageSize = ref(10)
const total = ref(0)

const createForm = reactive({
  orderId: '',
  issueType: 'QUALITY',
  issueSummary: '',
  issueDescription: '',
  expectedResolution: '',
  contactName: '',
  contactPhone: '',
  evidenceFiles: '',
  operator: 'pc-n08-ui'
})

const statusAction = ref('MARK_PROCESSING')
const statusRemark = ref('')

const token = computed(() => localStorage.getItem('N01_AUTH_TOKEN') || '')
const hasSession = computed(() => token.value.length > 0)
const canCreate = computed(() => {
  return (
    hasSession.value &&
    createForm.orderId.trim() &&
    createForm.issueType.trim() &&
    createForm.issueSummary.trim() &&
    createForm.issueDescription.trim() &&
    createForm.contactName.trim() &&
    /^1\d{10}$/.test(createForm.contactPhone.trim()) &&
    !creating.value
  )
})
const canSubmitStatus = computed(
  () => hasSession.value && selectedDisputeId.value && statusAction.value && !statusSubmitting.value
)

async function loadDisputes() {
  if (!hasSession.value || loading.value) {
    if (!hasSession.value) {
      errorMsg.value = '请先登录后查看售后争议'
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

    const resp = await fetch(`/api/v1/auth/after-sales/disputes?${params.toString()}`, {
      headers: { 'X-Auth-Token': token.value }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `售后争议列表加载失败(${resp.status})`)
    }
    const data = json.data || {}
    records.value = data.records || []
    total.value = data.total || 0
    if (!selectedDisputeId.value && records.value.length > 0) {
      selectedDisputeId.value = records.value[0].disputeId
      await loadDisputeDetail()
    } else if (selectedDisputeId.value) {
      const exists = records.value.some((item) => item.disputeId === selectedDisputeId.value)
      if (!exists && records.value.length > 0) {
        selectedDisputeId.value = records.value[0].disputeId
        await loadDisputeDetail()
      }
    }
  } catch (error) {
    errorMsg.value = error.message || '售后争议列表加载失败'
  } finally {
    loading.value = false
  }
}

async function loadDisputeDetail() {
  if (!hasSession.value || !selectedDisputeId.value || detailLoading.value) return
  detailLoading.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const resp = await fetch(`/api/v1/auth/after-sales/disputes/${selectedDisputeId.value}`, {
      headers: { 'X-Auth-Token': token.value }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `售后争议详情加载失败(${resp.status})`)
    }
    detail.value = json.data || null
    statusAction.value = defaultActionByStatus(detail.value?.status)
  } catch (error) {
    errorMsg.value = error.message || '售后争议详情加载失败'
  } finally {
    detailLoading.value = false
  }
}

async function createDispute() {
  if (!canCreate.value) return
  creating.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      orderId: createForm.orderId.trim(),
      issueType: createForm.issueType.trim(),
      issueSummary: createForm.issueSummary.trim(),
      issueDescription: createForm.issueDescription.trim(),
      expectedResolution: createForm.expectedResolution.trim() || null,
      contactName: createForm.contactName.trim(),
      contactPhone: createForm.contactPhone.trim(),
      evidenceFiles: createForm.evidenceFiles.trim() || null,
      operator: createForm.operator
    }
    const resp = await fetch('/api/v1/auth/after-sales/disputes', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Auth-Token': token.value
      },
      body: JSON.stringify(payload)
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `售后争议发起失败(${resp.status})`)
    }
    detail.value = json.data || null
    selectedDisputeId.value = detail.value?.disputeId || ''
    successMsg.value = '售后争议已发起'
    createForm.issueSummary = ''
    createForm.issueDescription = ''
    createForm.expectedResolution = ''
    createForm.evidenceFiles = ''
    await loadDisputes()
  } catch (error) {
    errorMsg.value = error.message || '售后争议发起失败'
  } finally {
    creating.value = false
  }
}

async function submitStatus() {
  if (!canSubmitStatus.value) return
  statusSubmitting.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      action: statusAction.value,
      remark: statusRemark.value.trim() || null,
      operator: 'pc-n08-ui'
    }
    const resp = await fetch(`/api/v1/auth/after-sales/disputes/${selectedDisputeId.value}/status`, {
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
    successMsg.value = '售后争议状态更新成功'
    statusRemark.value = ''
    statusAction.value = defaultActionByStatus(detail.value?.status)
    await loadDisputes()
  } catch (error) {
    errorMsg.value = error.message || '状态更新失败'
  } finally {
    statusSubmitting.value = false
  }
}

function defaultActionByStatus(status) {
  if (status === 'SUBMITTED') return 'MARK_PROCESSING'
  if (status === 'PROCESSING') return 'MARK_RESOLVED'
  if (status === 'RESOLVED') return 'MARK_CLOSED'
  return 'REOPEN'
}

function chooseDispute(disputeId) {
  selectedDisputeId.value = disputeId
  loadDisputeDetail()
}

function goTradeTerms() {
  router.push('/account/trade-terms-confirm')
}

function goAfterSaleProgress() {
  router.push('/account/after-sale-progress')
}

function goOrderDetail() {
  router.push('/account/order-detail')
}

function goHome() {
  router.push('/')
}

onMounted(() => {
  loadDisputes()
})
</script>

<template>
  <main class="n08-page">
    <section class="card hero">
      <h1>PC-N08 售后/争议发起页</h1>
      <p>针对质量、交付、发票、结算等异常发起售后争议，并跟踪处理进度与结案状态。</p>
      <div class="hero-actions">
        <button class="btn" @click="goTradeTerms">返回交易条款确认页</button>
        <button class="btn" @click="goAfterSaleProgress">售后处理进度页</button>
        <button class="btn" @click="goOrderDetail">返回订单详情页</button>
        <button class="btn" @click="goHome">返回首页</button>
      </div>
    </section>

    <section class="layout">
      <article class="card left">
        <div class="head">
          <h2>争议单列表</h2>
          <button class="btn" :disabled="loading" @click="loadDisputes">{{ loading ? '刷新中...' : '刷新' }}</button>
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
          <button class="btn" :disabled="loading" @click="loadDisputes">筛选</button>
        </div>
        <p class="tip">共 {{ total }} 条争议单</p>
        <ul class="list">
          <li
            v-for="item in records"
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
          </li>
        </ul>
      </article>

      <article class="card right">
        <div class="head">
          <h2>争议详情 / 发起</h2>
          <button class="btn" :disabled="detailLoading || !selectedDisputeId" @click="loadDisputeDetail">
            {{ detailLoading ? '加载中...' : '刷新详情' }}
          </button>
        </div>
        <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
        <p v-if="successMsg" class="ok">{{ successMsg }}</p>

        <section class="block">
          <h3>发起售后争议</h3>
          <div class="form-grid">
            <input v-model="createForm.orderId" placeholder="订单ID（如 OD0001）" />
            <select v-model="createForm.issueType">
              <option value="QUALITY">质量异议</option>
              <option value="DELIVERY_DELAY">交付延迟</option>
              <option value="INVOICE">发票问题</option>
              <option value="PAYMENT">结算问题</option>
              <option value="OTHER">其他问题</option>
            </select>
            <input v-model="createForm.issueSummary" placeholder="问题摘要（必填）" />
            <input v-model="createForm.contactName" placeholder="联系人（必填）" />
            <input v-model="createForm.contactPhone" placeholder="联系人手机号（11位）" />
            <input v-model="createForm.expectedResolution" placeholder="期望解决方案（可选）" />
            <input v-model="createForm.evidenceFiles" placeholder="证据URL（可选，多条可逗号分隔）" class="span-2" />
            <textarea
              v-model="createForm.issueDescription"
              class="span-2"
              rows="3"
              placeholder="问题描述（必填）"
            ></textarea>
          </div>
          <div class="actions">
            <button class="btn btn--primary" :disabled="!canCreate" @click="createDispute">
              {{ creating ? '提交中...' : '发起争议' }}
            </button>
          </div>
        </section>

        <section v-if="detail" class="block">
          <h3>争议详情</h3>
          <div class="meta-grid">
            <p>争议单号：{{ detail.disputeId }}</p>
            <p>订单号：{{ detail.orderNo }}（{{ detail.orderId }}）</p>
            <p>询价号：{{ detail.inquiryNo }}</p>
            <p>买方：{{ detail.buyerCompany }}</p>
            <p>供应方：{{ detail.supplierName }}</p>
            <p>问题类型：{{ detail.issueTypeText }}（{{ detail.issueType }}）</p>
            <p>问题摘要：{{ detail.issueSummary }}</p>
            <p>当前状态：{{ detail.statusText }}（{{ detail.status }}）</p>
            <p>联系人：{{ detail.contactName }} / {{ detail.contactPhoneMasked }}</p>
            <p>期望方案：{{ detail.expectedResolution || '-' }}</p>
            <p>证据文件：{{ detail.evidenceFiles || '-' }}</p>
            <p>最新备注：{{ detail.latestRemark || '-' }}</p>
          </div>
          <p class="desc">问题描述：{{ detail.issueDescription }}</p>
          <div class="actions status-actions">
            <select v-model="statusAction">
              <option value="MARK_PROCESSING">标记处理中</option>
              <option value="MARK_RESOLVED">标记已解决</option>
              <option value="MARK_CLOSED">关闭争议</option>
              <option value="REOPEN">重新打开</option>
            </select>
            <input v-model="statusRemark" placeholder="状态备注（可选）" />
            <button class="btn" :disabled="!canSubmitStatus" @click="submitStatus">
              {{ statusSubmitting ? '提交中...' : '更新状态' }}
            </button>
          </div>
        </section>
        <p v-else class="tip">请选择左侧争议单，或在上方直接发起新的售后争议</p>
      </article>
    </section>
  </main>
</template>

<style scoped>
.n08-page {
  max-width: 1220px;
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
.layout {
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
  cursor: pointer;
}
.list li.active {
  border-color: #f57c00;
  background: #fffaf3;
}
.line-1 {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}
.block {
  border: 1px solid #f3f4f6;
  border-radius: 10px;
  padding: 12px;
  margin-top: 12px;
}
.block h3 {
  margin: 0 0 8px;
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
  margin: 10px 0 0;
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
@media (max-width: 980px) {
  .layout {
    grid-template-columns: 1fr;
  }
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
