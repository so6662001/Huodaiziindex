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
  inquiryId: '',
  quoteId: '',
  contactMobile: '',
  buyerCompany: '',
  buyerContact: '',
  pickupSite: '',
  pickupDate: '',
  pickupVehicleNo: '',
  pickupDriverName: '',
  pickupDriverPhone: '',
  agreedProtocol: true,
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
    createForm.inquiryId.trim() &&
    createForm.quoteId.trim() &&
    /^1\d{10}$/.test(createForm.contactMobile.trim()) &&
    createForm.pickupSite.trim() &&
    createForm.pickupDate.trim() &&
    createForm.pickupVehicleNo.trim() &&
    createForm.pickupDriverName.trim() &&
    /^1\d{10}$/.test(createForm.pickupDriverPhone.trim()) &&
    createForm.agreedProtocol
  )
})
const canQuery = computed(() => /^1\d{10}$/.test(filters.contactMobile.trim()))

function fillFromQuery() {
  const inquiryId = String(route.query.inquiryId || '').trim()
  const quoteId = String(route.query.quoteId || '').trim()
  const contactMobile = String(route.query.contactMobile || '').trim()
  if (inquiryId) createForm.inquiryId = inquiryId
  if (quoteId) createForm.quoteId = quoteId
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
    const resp = await fetch(`/api/v1/inquiries/pickup-orders?${params.toString()}`)
    const json = await resp.json()
    if (json.code !== '0') {
      throw new Error(json.message || '加载提货单失败')
    }
    const data = json.data || {}
    list.value = data.items || []
    pager.total = Number(data.total || 0)
    pager.page = Number(data.page || pager.page)
    pager.pageSize = Number(data.pageSize || pager.pageSize)
    if (selected.value) {
      const hit = list.value.find((item) => item.pickupId === selected.value.pickupId)
      if (!hit) selected.value = null
    }
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    loading.value = false
  }
}

async function createPickupOrder() {
  if (!canCreate.value || creating.value) return
  creating.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      inquiryId: createForm.inquiryId.trim(),
      quoteId: createForm.quoteId.trim(),
      contactMobile: createForm.contactMobile.trim(),
      buyerCompany: createForm.buyerCompany.trim() || null,
      buyerContact: createForm.buyerContact.trim() || null,
      pickupSite: createForm.pickupSite.trim(),
      pickupDate: createForm.pickupDate.trim(),
      pickupVehicleNo: createForm.pickupVehicleNo.trim(),
      pickupDriverName: createForm.pickupDriverName.trim(),
      pickupDriverPhone: createForm.pickupDriverPhone.trim(),
      agreedProtocol: createForm.agreedProtocol,
      remark: createForm.remark.trim() || null
    }
    const resp = await fetch('/api/v1/inquiries/pickup-orders', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    })
    const json = await resp.json()
    if (json.code !== '0') {
      throw new Error(json.message || '创建提货单失败')
    }
    successMsg.value = json.data.message || '提货单创建成功'
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
  if (!item?.pickupId || !filters.contactMobile.trim()) return
  errorMsg.value = ''
  try {
    const params = new URLSearchParams()
    params.set('contactMobile', filters.contactMobile.trim())
    const resp = await fetch(`/api/v1/inquiries/pickup-orders/${encodeURIComponent(item.pickupId)}?${params.toString()}`)
    const json = await resp.json()
    if (json.code !== '0') {
      throw new Error(json.message || '加载详情失败')
    }
    selected.value = json.data || null
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  }
}

async function updateStatus() {
  if (!selected.value?.order?.pickupId || !/^1\d{10}$/.test(statusForm.contactMobile.trim()) || updating.value) return
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
      `/api/v1/inquiries/pickup-orders/${encodeURIComponent(selected.value.order.pickupId)}/status`,
      {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      }
    )
    const json = await resp.json()
    if (json.code !== '0') {
      throw new Error(json.message || '更新状态失败')
    }
    selected.value = json.data || null
    successMsg.value = '状态更新成功'
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
  <main class="pickup-page">
    <section class="card">
      <h1>P08 提货通页面</h1>
      <p class="desc">成交后创建提货单，跟踪提货状态，沉淀履约证据链。</p>
    </section>

    <section class="card">
      <h2>创建提货单</h2>
      <div class="grid">
        <label>询价单ID<input v-model="createForm.inquiryId" placeholder="IQ..." /></label>
        <label>报价ID<input v-model="createForm.quoteId" placeholder="IQ...-Q1" /></label>
        <label>买方手机号<input v-model="createForm.contactMobile" placeholder="11位手机号" /></label>
        <label>买方公司<input v-model="createForm.buyerCompany" placeholder="可选" /></label>
        <label>买方联系人<input v-model="createForm.buyerContact" placeholder="可选" /></label>
        <label>提货点<input v-model="createForm.pickupSite" placeholder="仓库地址" /></label>
        <label>提货日期<input v-model="createForm.pickupDate" type="date" /></label>
        <label>车牌号<input v-model="createForm.pickupVehicleNo" placeholder="冀A12345" /></label>
        <label>司机姓名<input v-model="createForm.pickupDriverName" placeholder="张师傅" /></label>
        <label>司机电话<input v-model="createForm.pickupDriverPhone" placeholder="11位手机号" /></label>
      </div>
      <label class="checkbox">
        <input v-model="createForm.agreedProtocol" type="checkbox" />
        已同意提货服务协议
      </label>
      <label>
        备注
        <textarea v-model="createForm.remark" rows="2" placeholder="选填"></textarea>
      </label>
      <div class="actions">
        <button class="btn primary" :disabled="!canCreate || creating" @click="createPickupOrder">
          {{ creating ? '创建中...' : '创建提货单' }}
        </button>
      </div>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
      <p v-if="successMsg" class="success">{{ successMsg }}</p>
    </section>

    <section class="card">
      <h2>提货单列表</h2>
      <div class="grid">
        <label>手机号<input v-model="filters.contactMobile" placeholder="11位手机号" /></label>
        <label>
          状态
          <select v-model="filters.status">
            <option value="">全部</option>
            <option value="CREATED">待确认</option>
            <option value="CONFIRMED">已确认</option>
            <option value="IN_TRANSIT">运输中</option>
            <option value="SIGNED">已签收</option>
            <option value="COMPLETED">已完成</option>
            <option value="CANCELLED">已取消</option>
          </select>
        </label>
        <label>关键词<input v-model="filters.keyword" placeholder="提货单号/商家/规格" /></label>
      </div>
      <div class="actions">
        <button class="btn" :disabled="!canQuery || loading" @click="loadList">查询</button>
      </div>
      <p v-if="loading">加载中...</p>
      <div v-else class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>提货单号</th>
              <th>商家</th>
              <th>货物</th>
              <th>提货点</th>
              <th>日期</th>
              <th>车牌</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in list" :key="item.pickupId">
              <td>{{ item.pickupNo }}</td>
              <td>{{ item.supplierName }}</td>
              <td>{{ item.goodsSummary }}</td>
              <td>{{ item.pickupAddress }}</td>
              <td>{{ item.pickupDate }}</td>
              <td>{{ item.truckNo }}</td>
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
      <h2>提货单详情</h2>
      <p><strong>提货单号：</strong>{{ selected.order.pickupNo }}</p>
      <p><strong>商家：</strong>{{ selected.order.supplierName }}</p>
      <p><strong>货物：</strong>{{ selected.order.goodsSummary }}</p>
      <p><strong>提货点：</strong>{{ selected.order.pickupAddress }}</p>
      <p><strong>司机：</strong>{{ selected.order.driverName }}（{{ selected.order.driverPhoneMasked }}）</p>
      <p><strong>当前状态：</strong>{{ selected.order.statusText }}</p>

      <h3>状态流转</h3>
      <div class="grid">
        <label>
          状态
          <select v-model="statusForm.status">
            <option value="CONFIRMED">已确认</option>
            <option value="IN_TRANSIT">运输中</option>
            <option value="SIGNED">已签收</option>
            <option value="COMPLETED">已完成</option>
            <option value="CANCELLED">已取消</option>
          </select>
        </label>
        <label>手机号<input v-model="statusForm.contactMobile" placeholder="11位手机号" /></label>
        <label>操作人<input v-model="statusForm.operator" placeholder="选填" /></label>
      </div>
      <label>
        备注
        <textarea v-model="statusForm.remark" rows="2" placeholder="选填"></textarea>
      </label>
      <div class="actions">
        <button class="btn primary" :disabled="updating" @click="updateStatus">
          {{ updating ? '提交中...' : '更新状态' }}
        </button>
      </div>
      <ul class="logs">
        <li v-for="line in selected.operationLogs || []" :key="line">{{ line }}</li>
      </ul>
    </section>
  </main>
</template>

<style scoped>
.pickup-page {
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
.checkbox {
  margin-top: 10px;
  display: inline-flex;
  align-items: center;
  gap: 8px;
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
