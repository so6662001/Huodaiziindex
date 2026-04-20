<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const listLoading = ref(false)
const creating = ref(false)
const statusLoading = ref(false)
const errorMsg = ref('')
const successMsg = ref('')
const list = ref([])
const selected = ref(null)

const query = reactive({
  contactMobile: '',
  status: '',
  keyword: '',
  page: 1,
  pageSize: 10,
  total: 0
})

const createForm = reactive({
  pickupOrderId: '',
  contactMobile: '',
  statementMonth: '',
  dueDate: '',
  settleType: 'MONTHLY',
  invoiceTitle: '',
  remark: ''
})

const quickStatus = reactive({
  reconcileId: '',
  status: 'CONFIRMED',
  paidAmount: '',
  operator: '',
  remark: ''
})

const stats = reactive({
  createdCount: 0,
  invoicePendingCount: 0,
  invoicedCount: 0,
  confirmedCount: 0,
  partialPaidCount: 0,
  paidCount: 0,
  closedCount: 0,
  disputedCount: 0,
  tipText: ''
})

const canQuery = computed(() => /^1\d{10}$/.test(query.contactMobile.trim()))
const canCreate = computed(
  () =>
    createForm.pickupOrderId.trim() &&
    /^1\d{10}$/.test(createForm.contactMobile.trim()) &&
    /^\d{4}-\d{2}$/.test(createForm.statementMonth.trim()) &&
    createForm.settleType.trim() &&
    createForm.dueDate.trim()
)
const canQuickStatus = computed(
  () => quickStatus.reconcileId.trim() && quickStatus.status.trim() && /^1\d{10}$/.test(query.contactMobile.trim())
)
const totalPages = computed(() => Math.max(Math.ceil(query.total / query.pageSize), 1))

async function loadList() {
  if (!canQuery.value || listLoading.value) return
  listLoading.value = true
  errorMsg.value = ''
  try {
    const params = new URLSearchParams()
    params.set('contactMobile', query.contactMobile.trim())
    if (query.status) params.set('status', query.status)
    if (query.keyword.trim()) params.set('keyword', query.keyword.trim())
    params.set('page', String(query.page))
    params.set('pageSize', String(query.pageSize))
    const resp = await fetch(`/api/v1/inquiries/h5/reconcile-orders?${params.toString()}`)
    const json = await resp.json()
    if (json.code !== '0') throw new Error(json.message || '加载对账单失败')
    const data = json.data || {}
    list.value = Array.isArray(data.items) ? data.items : []
    query.total = Number(data.total || 0)
    query.page = Number(data.page || query.page)
    query.pageSize = Number(data.pageSize || query.pageSize)
    stats.createdCount = Number(data.createdCount || 0)
    stats.invoicePendingCount = Number(data.invoicePendingCount || 0)
    stats.invoicedCount = Number(data.invoicedCount || 0)
    stats.confirmedCount = Number(data.confirmedCount || 0)
    stats.partialPaidCount = Number(data.partialPaidCount || 0)
    stats.paidCount = Number(data.paidCount || 0)
    stats.closedCount = Number(data.closedCount || 0)
    stats.disputedCount = Number(data.disputedCount || 0)
    stats.tipText = data.tipText || ''
    if (!quickStatus.reconcileId && list.value.length > 0) {
      quickStatus.reconcileId = list.value[0].reconcileId
    }
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    listLoading.value = false
  }
}

async function createReconcileOrder() {
  if (!canCreate.value || creating.value) return
  creating.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      pickupOrderId: createForm.pickupOrderId.trim(),
      contactMobile: createForm.contactMobile.trim(),
      statementMonth: createForm.statementMonth.trim(),
      settleType: createForm.settleType.trim(),
      dueDate: createForm.dueDate.trim(),
      invoiceTitle: createForm.invoiceTitle.trim() || null,
      remark: createForm.remark.trim() || null
    }
    const resp = await fetch('/api/v1/inquiries/h5/reconcile-orders', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    })
    const json = await resp.json()
    if (json.code !== '0') throw new Error(json.message || '创建对账单失败')
    successMsg.value = json.data?.message || '对账单创建成功'
    query.contactMobile = createForm.contactMobile.trim()
    quickStatus.reconcileId = json.data?.reconcileId || quickStatus.reconcileId
    query.page = 1
    await loadList()
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    creating.value = false
  }
}

async function viewDetail(reconcileId) {
  if (!reconcileId || !canQuery.value) return
  errorMsg.value = ''
  try {
    const params = new URLSearchParams()
    params.set('contactMobile', query.contactMobile.trim())
    const resp = await fetch(`/api/v1/inquiries/h5/reconcile-orders/${encodeURIComponent(reconcileId)}?${params.toString()}`)
    const json = await resp.json()
    if (json.code !== '0') throw new Error(json.message || '加载对账详情失败')
    selected.value = json.data || null
    quickStatus.reconcileId = reconcileId
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  }
}

async function submitQuickStatus() {
  if (!canQuickStatus.value || statusLoading.value) return
  statusLoading.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      contactMobile: query.contactMobile.trim(),
      status: quickStatus.status,
      paidAmount: quickStatus.paidAmount.trim() || null,
      operator: quickStatus.operator.trim() || null,
      remark: quickStatus.remark.trim() || null
    }
    const resp = await fetch(
      `/api/v1/inquiries/h5/reconcile-orders/${encodeURIComponent(quickStatus.reconcileId.trim())}/quick-status`,
      {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      }
    )
    const json = await resp.json()
    if (json.code !== '0') throw new Error(json.message || '更新状态失败')
    successMsg.value = json.data?.message || '状态已更新'
    if (selected.value?.reconcileId === quickStatus.reconcileId.trim()) {
      selected.value.currentStatus = json.data?.status || selected.value.currentStatus
      selected.value.currentStatusText = json.data?.statusText || selected.value.currentStatusText
      selected.value.latestRemark = quickStatus.remark.trim() || selected.value.latestRemark
    }
    await loadList()
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    statusLoading.value = false
  }
}

function useReconcile(item) {
  if (!item?.reconcileId) return
  quickStatus.reconcileId = item.reconcileId
}

function prevPage() {
  if (query.page <= 1 || listLoading.value) return
  query.page -= 1
  loadList()
}

function nextPage() {
  if (query.page >= totalPages.value || listLoading.value) return
  query.page += 1
  loadList()
}

function goH5PickupOrders() {
  router.push(`/h5/pickup-orders?contactMobile=${encodeURIComponent(query.contactMobile.trim() || '13800138000')}`)
}

onMounted(() => {
  const contactMobile = String(route.query.contactMobile || '').trim()
  const pickupOrderId = String(route.query.pickupOrderId || '').trim()
  if (contactMobile) {
    query.contactMobile = contactMobile
    createForm.contactMobile = contactMobile
  }
  if (pickupOrderId) createForm.pickupOrderId = pickupOrderId
  if (canQuery.value) loadList()
})
</script>

<template>
  <main class="h5-reconcile-page">
    <section class="card hero">
      <h1>H09 H5对账单</h1>
      <p>移动端完成对账单创建、台账查询、状态流转与回款登记，提升资金闭环效率。</p>
    </section>

    <section class="card">
      <h2>创建对账单</h2>
      <div class="grid">
        <label>提货单ID <input v-model="createForm.pickupOrderId" placeholder="PU..." /></label>
        <label>手机号 <input v-model="createForm.contactMobile" placeholder="11位手机号" /></label>
        <label>账期月份 <input v-model="createForm.statementMonth" placeholder="2026-04" /></label>
        <label>结算类型 <input v-model="createForm.settleType" placeholder="MONTHLY" /></label>
        <label>到期日 <input v-model="createForm.dueDate" type="date" /></label>
        <label>发票抬头 <input v-model="createForm.invoiceTitle" placeholder="可选" /></label>
      </div>
      <label>
        备注
        <input v-model="createForm.remark" placeholder="如 按合同月结" />
      </label>
      <div class="actions">
        <button class="btn btn--primary" :disabled="!canCreate || creating" @click="createReconcileOrder">
          {{ creating ? '创建中...' : '创建对账单' }}
        </button>
      </div>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
      <p v-if="successMsg" class="success">{{ successMsg }}</p>
    </section>

    <section class="card">
      <h2>对账单列表</h2>
      <div class="grid">
        <label>手机号 <input v-model="query.contactMobile" placeholder="11位手机号" /></label>
        <label>
          状态
          <select v-model="query.status">
            <option value="">全部</option>
            <option value="CREATED">已创建</option>
            <option value="INVOICE_PENDING">待开票</option>
            <option value="INVOICED">已开票</option>
            <option value="CONFIRMED">已确认</option>
            <option value="PARTIAL_PAID">部分回款</option>
            <option value="PAID">已回款</option>
            <option value="CLOSED">已关闭</option>
            <option value="DISPUTED">争议中</option>
          </select>
        </label>
        <label>关键词 <input v-model="query.keyword" placeholder="对账单号/提货单号/商家" /></label>
      </div>
      <div class="actions">
        <button class="btn" :disabled="!canQuery || listLoading" @click="loadList">
          {{ listLoading ? '加载中...' : '查询对账单' }}
        </button>
        <button class="btn" @click="goH5PickupOrders">回H08提货单</button>
      </div>
      <p class="tip">{{ stats.tipText || '—' }}</p>
      <div class="stat-grid">
        <span>已创建 {{ stats.createdCount }}</span>
        <span>待开票 {{ stats.invoicePendingCount }}</span>
        <span>已开票 {{ stats.invoicedCount }}</span>
        <span>已确认 {{ stats.confirmedCount }}</span>
        <span>部分回款 {{ stats.partialPaidCount }}</span>
        <span>已回款 {{ stats.paidCount }}</span>
        <span>已关闭 {{ stats.closedCount }}</span>
        <span>争议中 {{ stats.disputedCount }}</span>
      </div>
      <p v-if="listLoading">加载中...</p>
      <ul v-else-if="list.length" class="order-list">
        <li v-for="item in list" :key="item.reconcileId" class="order-item">
          <div class="head">
            <strong>{{ item.reconcileNo }}</strong>
            <span class="status">{{ item.statusText }}</span>
          </div>
          <p>提货单：{{ item.pickupOrderNo }} ｜ 商家：{{ item.supplierName }}</p>
          <p>货物：{{ item.goodsSummary }}</p>
          <p>应收：{{ item.totalAmount }} ｜ 已收：{{ item.paidAmount }} ｜ 未收：{{ item.unpaidAmount }}</p>
          <div class="actions">
            <button class="btn" @click="useReconcile(item)">设为当前单</button>
            <button class="btn" @click="viewDetail(item.reconcileId)">查看详情</button>
          </div>
        </li>
      </ul>
      <p v-else>暂无对账单</p>

      <div class="actions" v-if="query.total > 0">
        <button class="btn" :disabled="query.page <= 1 || listLoading" @click="prevPage">上一页</button>
        <button class="btn" :disabled="query.page >= totalPages || listLoading" @click="nextPage">下一页</button>
        <span class="tip">第 {{ query.page }} 页 / 共 {{ totalPages }} 页（{{ query.total }} 条）</span>
      </div>
    </section>

    <section class="card">
      <h2>快捷状态更新</h2>
      <div class="grid">
        <label>对账单ID <input v-model="quickStatus.reconcileId" placeholder="请选择或输入" /></label>
        <label>
          新状态
          <select v-model="quickStatus.status">
            <option value="CONFIRMED">已确认</option>
            <option value="PARTIAL_PAID">部分回款</option>
            <option value="PAID">已回款</option>
            <option value="CLOSED">已关闭</option>
            <option value="DISPUTED">争议中</option>
          </select>
        </label>
        <label>本次回款金额 <input v-model="quickStatus.paidAmount" placeholder="可选，如 50000" /></label>
        <label>操作人 <input v-model="quickStatus.operator" placeholder="可选" /></label>
      </div>
      <label>
        备注
        <input v-model="quickStatus.remark" placeholder="如 已到账，待核销" />
      </label>
      <div class="actions">
        <button class="btn btn--primary" :disabled="!canQuickStatus || statusLoading" @click="submitQuickStatus">
          {{ statusLoading ? '提交中...' : '更新状态' }}
        </button>
      </div>
    </section>

    <section class="card" v-if="selected">
      <h2>对账单详情</h2>
      <p>对账单号：{{ selected.reconcileNo }}</p>
      <p>提货单号：{{ selected.pickupOrderNo }}</p>
      <p>商家：{{ selected.supplierName }}</p>
      <p>货物：{{ selected.goodsSummary }}</p>
      <p>应收：{{ selected.totalAmount }} ｜ 已收：{{ selected.paidAmount }} ｜ 未收：{{ selected.unpaidAmount }}</p>
      <p>票据状态：{{ selected.voucherStatus || '-' }}</p>
      <p>当前状态：{{ selected.currentStatusText }}</p>
      <p class="tip">最新备注：{{ selected.latestRemark || '-' }}</p>
    </section>
  </main>
</template>

<style scoped>
.h5-reconcile-page {
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
.hero h1 {
  margin: 0 0 8px;
}
.hero p {
  margin: 0;
  color: #4b5563;
}
h2 {
  margin: 0 0 10px;
}
.grid {
  display: grid;
  gap: 10px;
}
label {
  display: grid;
  gap: 6px;
  color: #374151;
}
input,
select {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 9px 10px;
  font: inherit;
}
.actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 12px;
  align-items: center;
}
.btn {
  border: 1px solid #ddd;
  border-radius: 8px;
  background: #fff;
  padding: 9px 14px;
  cursor: pointer;
}
.btn--primary {
  border-color: #f57c00;
  background: #f57c00;
  color: #fff;
}
.tip {
  color: #4b5563;
}
.stat-grid {
  margin-top: 10px;
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 8px;
  color: #4b5563;
}
.order-list {
  list-style: none;
  margin: 10px 0 0;
  padding: 0;
  display: grid;
  gap: 10px;
}
.order-item {
  border: 1px solid #f2f4f7;
  border-radius: 10px;
  padding: 10px;
}
.head {
  display: flex;
  justify-content: space-between;
  gap: 10px;
}
.status {
  font-size: 12px;
  color: #92400e;
  background: #fff7ed;
  border-radius: 999px;
  padding: 2px 8px;
}
.order-item p {
  margin: 6px 0 0;
  color: #4b5563;
}
.error {
  color: #b42318;
  margin: 10px 0 0;
}
.success {
  color: #1f7a43;
  margin: 10px 0 0;
}
</style>
