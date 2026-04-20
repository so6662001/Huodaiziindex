<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const loading = ref(false)
const creating = ref(false)
const updating = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

const list = ref([])
const selected = ref(null)

const pager = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const filters = reactive({
  contactMobile: '',
  status: '',
  keyword: ''
})

const createForm = reactive({
  pickupOrderId: '',
  contactMobile: '',
  statementMonth: '',
  dueDate: '',
  invoiceAmount: '',
  deductionAmount: '',
  remark: ''
})

const statusForm = reactive({
  contactMobile: '',
  status: 'CONFIRMED',
  operator: '',
  remark: ''
})

const totalPages = computed(() => Math.max(Math.ceil(pager.total / pager.pageSize), 1))
const canCreate = computed(() => {
  return (
    createForm.pickupOrderId.trim() &&
    /^1\d{10}$/.test(createForm.contactMobile.trim()) &&
    /^\d{4}-\d{2}$/.test(createForm.statementMonth.trim()) &&
    createForm.invoiceAmount.trim()
  )
})
const canQuery = computed(() => /^1\d{10}$/.test(filters.contactMobile.trim()))

function fillFromQuery() {
  const pickupOrderId = String(route.query.pickupOrderId || '').trim()
  const contactMobile = String(route.query.contactMobile || '').trim()
  if (pickupOrderId) createForm.pickupOrderId = pickupOrderId
  if (contactMobile) {
    createForm.contactMobile = contactMobile
    filters.contactMobile = contactMobile
    statusForm.contactMobile = contactMobile
  }
}

async function loadList() {
  if (!canQuery.value || loading.value) return
  loading.value = true
  errorMsg.value = ''
  try {
    const params = new URLSearchParams()
    params.set('contactMobile', filters.contactMobile.trim())
    if (filters.status) params.set('status', filters.status)
    if (filters.keyword.trim()) params.set('keyword', filters.keyword.trim())
    params.set('page', String(pager.page))
    params.set('pageSize', String(pager.pageSize))
    const resp = await fetch(`/api/v1/inquiries/reconcile-orders?${params.toString()}`)
    const json = await resp.json()
    if (json.code !== '0') {
      throw new Error(json.message || '加载对账单失败')
    }
    const data = json.data || {}
    list.value = data.items || []
    pager.total = Number(data.total || 0)
    pager.page = Number(data.page || pager.page)
    pager.pageSize = Number(data.pageSize || pager.pageSize)
    if (selected.value) {
      const hit = list.value.find((item) => item.reconcileId === selected.value.order.reconcileId)
      if (!hit) selected.value = null
    }
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    loading.value = false
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
      dueDate: createForm.dueDate.trim() || null,
      invoiceAmount: createForm.invoiceAmount.trim(),
      deductionAmount: createForm.deductionAmount.trim() || null,
      remark: createForm.remark.trim() || null
    }
    const resp = await fetch('/api/v1/inquiries/reconcile-orders', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    })
    const json = await resp.json()
    if (json.code !== '0') {
      throw new Error(json.message || '创建对账单失败')
    }
    successMsg.value = json.data.message || '对账单创建成功'
    filters.contactMobile = createForm.contactMobile.trim()
    statusForm.contactMobile = createForm.contactMobile.trim()
    pager.page = 1
    await loadList()
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    creating.value = false
  }
}

async function viewDetail(item) {
  if (!item?.reconcileId || !filters.contactMobile.trim()) return
  errorMsg.value = ''
  try {
    const params = new URLSearchParams()
    params.set('contactMobile', filters.contactMobile.trim())
    const resp = await fetch(
      `/api/v1/inquiries/reconcile-orders/${encodeURIComponent(item.reconcileId)}?${params.toString()}`
    )
    const json = await resp.json()
    if (json.code !== '0') {
      throw new Error(json.message || '加载对账单详情失败')
    }
    selected.value = json.data || null
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  }
}

async function updateStatus() {
  if (!selected.value?.order?.reconcileId || !/^1\d{10}$/.test(statusForm.contactMobile.trim()) || updating.value) {
    return
  }
  updating.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      contactMobile: statusForm.contactMobile.trim(),
      status: statusForm.status,
      operator: statusForm.operator.trim() || null,
      remark: statusForm.remark.trim() || null
    }
    const resp = await fetch(
      `/api/v1/inquiries/reconcile-orders/${encodeURIComponent(selected.value.order.reconcileId)}/status`,
      {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      }
    )
    const json = await resp.json()
    if (json.code !== '0') {
      throw new Error(json.message || '更新对账状态失败')
    }
    selected.value = json.data || null
    successMsg.value = '对账状态更新成功'
    await loadList()
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    updating.value = false
  }
}

function prevPage() {
  if (pager.page <= 1 || loading.value) return
  pager.page -= 1
  loadList()
}

function nextPage() {
  if (pager.page >= totalPages.value || loading.value) return
  pager.page += 1
  loadList()
}

onMounted(() => {
  fillFromQuery()
  if (canQuery.value) {
    loadList()
  }
})
</script>

<template>
  <main class="reconcile-page">
    <section class="card">
      <h1>P09 对账通页面</h1>
      <p class="desc">围绕提货单沉淀应收、回款和对账争议处理，形成交易闭环。</p>
    </section>

    <section class="card">
      <h2>创建对账单</h2>
      <div class="grid">
        <label>提货单ID<input v-model="createForm.pickupOrderId" placeholder="PU..." /></label>
        <label>手机号<input v-model="createForm.contactMobile" placeholder="11位手机号" /></label>
        <label>账期月份<input v-model="createForm.statementMonth" placeholder="2026-04" /></label>
        <label>到期日期<input v-model="createForm.dueDate" type="date" /></label>
        <label>开票金额<input v-model="createForm.invoiceAmount" placeholder="如 421500" /></label>
        <label>扣减金额<input v-model="createForm.deductionAmount" placeholder="可选，如 1500" /></label>
      </div>
      <label>
        备注
        <textarea v-model="createForm.remark" rows="2" placeholder="可选"></textarea>
      </label>
      <div class="actions">
        <button class="btn primary" :disabled="!canCreate || creating" @click="createReconcileOrder">
          {{ creating ? '创建中...' : '创建对账单' }}
        </button>
      </div>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
      <p v-if="successMsg" class="success">{{ successMsg }}</p>
    </section>

    <section class="card">
      <h2>对账单列表</h2>
      <div class="grid">
        <label>手机号<input v-model="filters.contactMobile" placeholder="11位手机号" /></label>
        <label>
          状态
          <select v-model="filters.status">
            <option value="">全部</option>
            <option value="CREATED">已创建</option>
            <option value="CONFIRMED">已确认</option>
            <option value="PARTIAL_PAID">部分回款</option>
            <option value="PAID">已回款</option>
            <option value="CLOSED">已结清</option>
            <option value="DISPUTED">争议中</option>
          </select>
        </label>
        <label>关键词<input v-model="filters.keyword" placeholder="对账单号/提货单号/商家" /></label>
      </div>
      <div class="actions">
        <button class="btn" :disabled="!canQuery || loading" @click="loadList">查询</button>
      </div>
      <p v-if="loading">加载中...</p>
      <div v-else class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>对账单号</th>
              <th>提货单号</th>
              <th>商家</th>
              <th>货物</th>
              <th>应收总额</th>
              <th>应收</th>
              <th>已收</th>
              <th>未收</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in list" :key="item.reconcileId">
              <td>{{ item.reconcileNo }}</td>
              <td>{{ item.pickupNo }}</td>
              <td>{{ item.supplierName }}</td>
              <td>{{ item.goodsSummary }}</td>
              <td>{{ item.totalAmount }}</td>
              <td>{{ item.receivableAmount }}</td>
              <td>{{ item.paidAmount }}</td>
              <td>{{ item.outstandingAmount }}</td>
              <td>{{ item.statusText }}</td>
              <td><button class="btn small" @click="viewDetail(item)">详情</button></td>
            </tr>
          </tbody>
        </table>
      </div>
      <div class="actions" v-if="pager.total > 0">
        <button class="btn" :disabled="pager.page <= 1 || loading" @click="prevPage">上一页</button>
        <button class="btn" :disabled="pager.page >= totalPages || loading" @click="nextPage">下一页</button>
        <span class="tip">第 {{ pager.page }} 页 / 共 {{ totalPages }} 页（{{ pager.total }} 条）</span>
      </div>
    </section>

    <section class="card" v-if="selected?.order">
      <h2>对账单详情</h2>
      <p><strong>对账单号：</strong>{{ selected.order.reconcileNo }}</p>
      <p><strong>提货单号：</strong>{{ selected.order.pickupNo }}</p>
      <p><strong>商家：</strong>{{ selected.order.supplierName }}</p>
      <p><strong>货物：</strong>{{ selected.order.goodsSummary }}</p>
      <p>
        <strong>金额：</strong>应收 {{ selected.order.receivableAmount }}，已收 {{ selected.order.paidAmount }}，未收
        {{ selected.order.outstandingAmount }}
      </p>
      <p><strong>当前状态：</strong>{{ selected.order.statusText }}</p>

      <h3>状态流转</h3>
      <div class="grid">
        <label>
          状态
          <select v-model="statusForm.status">
            <option value="CONFIRMED">已确认</option>
            <option value="PARTIAL_PAID">部分回款</option>
            <option value="PAID">已回款</option>
            <option value="CLOSED">已结清</option>
            <option value="DISPUTED">争议中</option>
          </select>
        </label>
        <label>手机号<input v-model="statusForm.contactMobile" placeholder="11位手机号" /></label>
        <label>操作人<input v-model="statusForm.operator" placeholder="可选" /></label>
      </div>
      <label>
        备注
        <textarea v-model="statusForm.remark" rows="2" placeholder="可选"></textarea>
      </label>
      <div class="actions">
        <button class="btn primary" :disabled="updating" @click="updateStatus">
          {{ updating ? '提交中...' : '更新对账状态' }}
        </button>
      </div>
      <ul class="logs">
        <li v-for="line in selected.operationLogs || []" :key="line">{{ line }}</li>
      </ul>
    </section>
  </main>
</template>

<style scoped>
.reconcile-page {
  max-width: 1120px;
  margin: 0 auto;
  padding: 20px 16px 36px;
}
.card {
  background: #fff;
  border: 1px solid #ececec;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 14px;
}
.desc {
  color: #4b5563;
  margin: 0;
}
.grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
}
label {
  display: grid;
  gap: 6px;
  color: #374151;
}
input,
select,
textarea {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 9px 10px;
  font: inherit;
}
.actions {
  display: flex;
  gap: 10px;
  margin-top: 12px;
  align-items: center;
  flex-wrap: wrap;
}
.btn {
  border: 1px solid #ddd;
  border-radius: 8px;
  background: #fff;
  padding: 9px 14px;
  cursor: pointer;
}
.btn.primary {
  border-color: #f57c00;
  background: #f57c00;
  color: #fff;
}
.btn.small {
  padding: 6px 10px;
  font-size: 12px;
}
.error {
  color: #b42318;
}
.success {
  color: #0c7a43;
}
.tip {
  color: #4b5563;
}
.table-wrap {
  width: 100%;
  overflow-x: auto;
}
table {
  width: 100%;
  border-collapse: collapse;
}
th,
td {
  border-top: 1px solid #f3f4f6;
  text-align: left;
  padding: 10px 8px;
  white-space: nowrap;
}
th {
  color: #6b7280;
  font-weight: 600;
}
.logs {
  margin: 10px 0 0;
  padding-left: 18px;
  color: #4b5563;
}
@media (max-width: 980px) {
  .grid {
    grid-template-columns: 1fr;
  }
}
</style>
