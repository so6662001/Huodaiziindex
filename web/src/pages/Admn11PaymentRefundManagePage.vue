<script setup>
import { computed, onMounted, reactive, ref } from 'vue'

const loading = ref(false)
const detailLoading = ref(false)
const submitting = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

const query = reactive({
  adminToken: 'test-admin-token',
  refundStatus: '',
  refundReasonCode: '',
  payChannel: '',
  keyword: '',
  page: 1,
  pageSize: 10
})

const state = reactive({
  total: 0,
  page: 1,
  pageSize: 10,
  pendingReviewCount: 0,
  approvedCount: 0,
  rejectedCount: 0,
  refundedCount: 0,
  records: []
})

const selectedRefundId = ref('')
const detail = ref(null)
const reviewForm = reactive({
  action: 'APPROVE',
  reviewRemark: '',
  operator: 'admn11-auditor-ui'
})

const canLoad = computed(() => query.adminToken.trim().length > 0)
const canSubmit = computed(() => {
  return (
    query.adminToken.trim() &&
    selectedRefundId.value &&
    reviewForm.action.trim() &&
    reviewForm.operator.trim() &&
    !submitting.value
  )
})
const totalPages = computed(() => Math.max(Math.ceil(state.total / query.pageSize), 1))

async function loadList() {
  if (!canLoad.value || loading.value) return
  loading.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const params = new URLSearchParams()
    if (query.refundStatus.trim()) params.set('refundStatus', query.refundStatus.trim())
    if (query.refundReasonCode.trim()) params.set('refundReasonCode', query.refundReasonCode.trim())
    if (query.payChannel.trim()) params.set('payChannel', query.payChannel.trim())
    if (query.keyword.trim()) params.set('keyword', query.keyword.trim())
    params.set('page', String(query.page))
    params.set('pageSize', String(query.pageSize))
    const resp = await fetch(`/api/admin/payment-refunds?${params.toString()}`, {
      headers: { 'X-Admin-Token': query.adminToken.trim() }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `加载失败(${resp.status})`)
    }
    const data = json.data || {}
    state.total = Number(data.total || 0)
    state.page = Number(data.page || query.page)
    state.pageSize = Number(data.pageSize || query.pageSize)
    state.pendingReviewCount = Number(data.pendingReviewCount || 0)
    state.approvedCount = Number(data.approvedCount || 0)
    state.rejectedCount = Number(data.rejectedCount || 0)
    state.refundedCount = Number(data.refundedCount || 0)
    state.records = Array.isArray(data.records) ? data.records : []
    if (state.records.length > 0) {
      const hit = state.records.find((item) => item.refundId === selectedRefundId.value)
      if (hit) {
        await openDetail(hit.refundId)
      } else if (!selectedRefundId.value) {
        await openDetail(state.records[0].refundId)
      }
    } else {
      selectedRefundId.value = ''
      detail.value = null
    }
  } catch (error) {
    errorMsg.value = error.message || '加载退款工单失败'
  } finally {
    loading.value = false
  }
}

async function openDetail(refundId) {
  if (!refundId || detailLoading.value) return
  detailLoading.value = true
  errorMsg.value = ''
  try {
    const resp = await fetch(`/api/admin/payment-refunds/${encodeURIComponent(refundId)}`, {
      headers: { 'X-Admin-Token': query.adminToken.trim() }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `详情加载失败(${resp.status})`)
    }
    selectedRefundId.value = refundId
    detail.value = json.data || null
  } catch (error) {
    errorMsg.value = error.message || '加载退款详情失败'
  } finally {
    detailLoading.value = false
  }
}

async function submitReview() {
  if (!canSubmit.value) return
  submitting.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      action: reviewForm.action.trim().toUpperCase(),
      reviewRemark: reviewForm.reviewRemark.trim() || null,
      operator: reviewForm.operator.trim()
    }
    const resp = await fetch(`/api/admin/payment-refunds/${encodeURIComponent(selectedRefundId.value)}/review`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'X-Admin-Token': query.adminToken.trim()
      },
      body: JSON.stringify(payload)
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `审核失败(${resp.status})`)
    }
    detail.value = json.data || null
    successMsg.value = `退款单 ${detail.value?.refundNo || selectedRefundId.value} 已完成 ${payload.action}`
    reviewForm.reviewRemark = ''
    await loadList()
  } catch (error) {
    errorMsg.value = error.message || '退款审核失败'
  } finally {
    submitting.value = false
  }
}

function prevPage() {
  if (query.page <= 1 || loading.value) return
  query.page -= 1
  loadList()
}

function nextPage() {
  if (query.page >= totalPages.value || loading.value) return
  query.page += 1
  loadList()
}

onMounted(() => {
  loadList()
})
</script>

<template>
  <main class="admn11-page">
    <section class="card hero">
      <h1>ADM-N11 支付与退款管理</h1>
      <p>管理支付异常退款、退款审核流程与订单状态联动，保证资金流程可追溯。</p>
    </section>

    <section class="card">
      <h2>筛选查询</h2>
      <div class="filters">
        <label>
          Admin Token
          <input v-model="query.adminToken" placeholder="请输入X-Admin-Token" />
        </label>
        <label>
          退款状态
          <select v-model="query.refundStatus">
            <option value="">全部</option>
            <option value="PENDING_REVIEW">PENDING_REVIEW</option>
            <option value="APPROVED">APPROVED</option>
            <option value="REJECTED">REJECTED</option>
            <option value="REFUNDED">REFUNDED</option>
          </select>
        </label>
        <label>
          退款原因
          <select v-model="query.refundReasonCode">
            <option value="">全部</option>
            <option value="DOUBLE_PAYMENT">DOUBLE_PAYMENT</option>
            <option value="ORDER_CANCELLED">ORDER_CANCELLED</option>
            <option value="QUALITY_DISPUTE">QUALITY_DISPUTE</option>
            <option value="OTHER">OTHER</option>
          </select>
        </label>
        <label>
          支付渠道
          <select v-model="query.payChannel">
            <option value="">全部</option>
            <option value="BANK_TRANSFER">BANK_TRANSFER</option>
            <option value="ALIPAY">ALIPAY</option>
            <option value="WECHAT">WECHAT</option>
            <option value="UNIONPAY">UNIONPAY</option>
          </select>
        </label>
        <label class="span-2">
          关键词
          <input v-model="query.keyword" placeholder="退款单号/收银单号/订单号/备注" />
        </label>
      </div>
      <div class="actions">
        <button class="btn btn--primary" :disabled="!canLoad || loading" @click="loadList">
          {{ loading ? '加载中...' : '查询退款单' }}
        </button>
      </div>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
      <p v-if="successMsg" class="ok">{{ successMsg }}</p>
    </section>

    <section class="card">
      <h2>退款统计</h2>
      <div class="kpi-grid">
        <article class="kpi"><span>总工单</span><strong>{{ state.total }}</strong></article>
        <article class="kpi"><span>待审核</span><strong>{{ state.pendingReviewCount }}</strong></article>
        <article class="kpi"><span>已通过</span><strong>{{ state.approvedCount }}</strong></article>
        <article class="kpi"><span>已驳回</span><strong>{{ state.rejectedCount }}</strong></article>
        <article class="kpi"><span>已退款</span><strong>{{ state.refundedCount }}</strong></article>
      </div>
    </section>

    <section class="card">
      <div class="head">
        <h2>退款工单列表</h2>
        <span class="tip">共 {{ state.total }} 条</span>
      </div>
      <p v-if="loading">列表加载中...</p>
      <p v-else-if="state.records.length === 0">暂无退款工单</p>
      <ul v-else class="refund-list">
        <li
          v-for="item in state.records"
          :key="item.refundId"
          :class="['refund-item', selectedRefundId === item.refundId ? 'active' : '']"
          @click="openDetail(item.refundId)"
        >
          <div class="line">
            <strong>{{ item.refundNo }}</strong>
            <span class="badge">{{ item.payChannelText }}</span>
            <span class="badge" :class="{ warn: item.refundStatus !== 'REFUNDED' }">
              {{ item.refundStatus }}
            </span>
          </div>
          <p class="muted">
            收银单 {{ item.cashierOrderId }} ｜ 订单 {{ item.orderNo }} ｜ 申请 ¥{{ item.refundAmountYuan }}
          </p>
          <p class="muted">原因：{{ item.refundReasonText }} ｜ 更新时间：{{ item.updatedAt }}</p>
        </li>
      </ul>
      <div class="pager">
        <button class="btn" :disabled="query.page <= 1 || loading" @click="prevPage">上一页</button>
        <span>第 {{ query.page }} / {{ totalPages }} 页</span>
        <button class="btn" :disabled="query.page >= totalPages || loading" @click="nextPage">下一页</button>
      </div>
    </section>

    <section class="card" v-if="detail">
      <h2>退款工单详情</h2>
      <div class="detail-grid">
        <p>退款单号：{{ detail.refundNo }}</p>
        <p>收银单号：{{ detail.cashierOrderId }}</p>
        <p>订单号：{{ detail.orderNo }}</p>
        <p>询价号：{{ detail.inquiryNo }}</p>
        <p>买方：{{ detail.buyerCompany }}</p>
        <p>供应方：{{ detail.supplierName }}</p>
        <p>支付渠道：{{ detail.payChannelText }}</p>
        <p>支付金额：¥{{ detail.paidAmount }}</p>
        <p>退款类型：{{ detail.refundTypeText }}</p>
        <p>申请金额：¥{{ detail.refundAmount }}</p>
        <p>退款状态：{{ detail.refundStatusText }}</p>
        <p>退款原因：{{ detail.refundReasonText }}</p>
        <p>创建时间：{{ detail.createdAt }}</p>
        <p>更新时间：{{ detail.updatedAt || '-' }}</p>
        <p>退款时间：{{ detail.refundedAt || '-' }}</p>
        <p>申请人：{{ detail.applicant || '-' }}</p>
        <p class="span-2">最新备注：{{ detail.latestRemark || '-' }}</p>
      </div>

      <section class="audit-block">
        <h3>审核操作</h3>
        <div class="audit-form">
          <select v-model="reviewForm.action">
            <option value="APPROVE">APPROVE（通过）</option>
            <option value="REJECT">REJECT（驳回）</option>
          </select>
          <input v-model="reviewForm.operator" placeholder="操作人" />
          <input v-model="reviewForm.reviewRemark" placeholder="审核备注（可选）" />
          <button class="btn btn--primary" :disabled="!canSubmit" @click="submitReview">
            {{ submitting ? '处理中...' : '提交审核' }}
          </button>
        </div>
        <p class="tip">可用动作：{{ detail.availableActions?.join(' / ') || '-' }}</p>
      </section>

      <section class="audit-block">
        <h3>进度时间线</h3>
        <ul class="timeline">
          <li v-for="node in detail.progressNodes || []" :key="`${node.nodeCode}-${node.happenedAt}`">
            <div class="line">
              <strong>{{ node.nodeName }}</strong>
              <span class="badge">{{ node.statusText }}</span>
            </div>
            <p class="muted">处理人：{{ node.handler || '-' }} ｜ 时间：{{ node.happenedAt || '-' }}</p>
            <p class="muted">备注：{{ node.remark || '-' }}</p>
          </li>
        </ul>
      </section>
    </section>
  </main>
</template>

<style scoped>
.admn11-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px 16px 40px;
}
.card {
  background: #fff;
  border: 1px solid #ececec;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 14px;
}
.hero h1 {
  margin: 0;
}
.hero p {
  margin: 8px 0 0;
  color: #4b5563;
}
.filters,
.detail-grid {
  display: grid;
  gap: 10px;
  grid-template-columns: repeat(4, minmax(0, 1fr));
}
.filters .span-2,
.detail-grid .span-2 {
  grid-column: span 2;
}
label {
  display: grid;
  gap: 6px;
  color: #374151;
}
input,
select {
  width: 100%;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 8px 10px;
  font: inherit;
  box-sizing: border-box;
}
.actions {
  margin-top: 12px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
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
.ok {
  color: #0c7a43;
}
.kpi-grid {
  display: grid;
  gap: 10px;
  grid-template-columns: repeat(5, minmax(0, 1fr));
}
.kpi {
  background: #f8fafc;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  padding: 10px;
  display: grid;
  gap: 6px;
}
.kpi span {
  font-size: 12px;
  color: #6b7280;
}
.kpi strong {
  font-size: 20px;
  color: #111827;
}
.tip,
.muted {
  color: #6b7280;
  margin: 6px 0 0;
}
.head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  margin-bottom: 10px;
}
.refund-list,
.timeline {
  list-style: none;
  margin: 0;
  padding: 0;
  display: grid;
  gap: 10px;
}
.refund-item,
.timeline li {
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  padding: 10px;
  cursor: pointer;
}
.refund-item.active {
  border-color: #f57c00;
  box-shadow: 0 0 0 1px #f57c0030 inset;
}
.line {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}
.badge {
  background: #eef2ff;
  color: #3730a3;
  border-radius: 999px;
  padding: 2px 8px;
  font-size: 12px;
}
.badge.warn {
  background: #fff5f5;
  color: #b42318;
}
.pager {
  margin-top: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
}
.audit-block {
  border: 1px solid #f1f5f9;
  border-radius: 10px;
  padding: 12px;
  margin-top: 10px;
}
.audit-block h3 {
  margin: 0 0 8px;
}
.audit-form {
  display: grid;
  gap: 8px;
  grid-template-columns: 220px 200px 1fr auto;
}
@media (max-width: 1024px) {
  .filters,
  .detail-grid,
  .kpi-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
  .audit-form {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
@media (max-width: 720px) {
  .filters,
  .detail-grid,
  .kpi-grid,
  .audit-form {
    grid-template-columns: 1fr;
  }
  .filters .span-2,
  .detail-grid .span-2 {
    grid-column: span 1;
  }
}
</style>
