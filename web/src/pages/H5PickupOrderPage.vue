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

const quickStatus = reactive({
  pickupId: '',
  status: 'CONFIRMED',
  operator: '',
  remark: ''
})

const stats = reactive({
  createdCount: 0,
  confirmedCount: 0,
  inTransitCount: 0,
  signedCount: 0,
  completedCount: 0,
  cancelledCount: 0,
  tipText: ''
})

const canQuery = computed(() => /^1\d{10}$/.test(query.contactMobile.trim()))
const canCreate = computed(
  () =>
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
const canQuickStatus = computed(
  () =>
    quickStatus.pickupId.trim() &&
    quickStatus.status.trim() &&
    /^1\d{10}$/.test(query.contactMobile.trim())
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
    const resp = await fetch(`/api/v1/inquiries/h5/pickup-orders?${params.toString()}`)
    const json = await resp.json()
    if (json.code !== '0') throw new Error(json.message || '加载提货单失败')
    const data = json.data || {}
    list.value = Array.isArray(data.items) ? data.items : []
    query.total = Number(data.total || 0)
    query.page = Number(data.page || query.page)
    query.pageSize = Number(data.pageSize || query.pageSize)
    stats.createdCount = Number(data.createdCount || 0)
    stats.confirmedCount = Number(data.confirmedCount || 0)
    stats.inTransitCount = Number(data.inTransitCount || 0)
    stats.signedCount = Number(data.signedCount || 0)
    stats.completedCount = Number(data.completedCount || 0)
    stats.cancelledCount = Number(data.cancelledCount || 0)
    stats.tipText = data.tipText || ''
    if (!quickStatus.pickupId && list.value.length > 0) {
      quickStatus.pickupId = list.value[0].pickupId
    }
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    listLoading.value = false
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
    const resp = await fetch('/api/v1/inquiries/h5/pickup-orders', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    })
    const json = await resp.json()
    if (json.code !== '0') throw new Error(json.message || '创建提货单失败')
    successMsg.value = json.data?.message || '提货单创建成功'
    query.contactMobile = createForm.contactMobile.trim()
    quickStatus.pickupId = json.data?.pickupId || quickStatus.pickupId
    query.page = 1
    await loadList()
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    creating.value = false
  }
}

async function viewDetail(pickupId) {
  if (!pickupId || !canQuery.value) return
  errorMsg.value = ''
  try {
    const params = new URLSearchParams()
    params.set('contactMobile', query.contactMobile.trim())
    const resp = await fetch(`/api/v1/inquiries/h5/pickup-orders/${encodeURIComponent(pickupId)}?${params.toString()}`)
    const json = await resp.json()
    if (json.code !== '0') throw new Error(json.message || '加载详情失败')
    selected.value = json.data || null
    quickStatus.pickupId = pickupId
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
      operator: quickStatus.operator.trim() || null,
      remark: quickStatus.remark.trim() || null
    }
    const resp = await fetch(
      `/api/v1/inquiries/h5/pickup-orders/${encodeURIComponent(quickStatus.pickupId.trim())}/quick-status`,
      {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      }
    )
    const json = await resp.json()
    if (json.code !== '0') throw new Error(json.message || '更新提货状态失败')
    successMsg.value = json.data?.message || '状态已更新'
    if (selected.value?.pickupId === quickStatus.pickupId.trim()) {
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

function usePickup(item) {
  if (!item?.pickupId) return
  quickStatus.pickupId = item.pickupId
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

function goH5Reconcile() {
  if (!selected.value?.pickupId) return
  const params = new URLSearchParams()
  params.set('pickupOrderId', selected.value.pickupId)
  params.set('contactMobile', query.contactMobile.trim())
  router.push(`/inquiry/reconcile/pass?${params.toString()}`)
}

onMounted(() => {
  const contactMobile = String(route.query.contactMobile || '').trim()
  const inquiryId = String(route.query.inquiryId || '').trim()
  const quoteId = String(route.query.quoteId || '').trim()
  if (contactMobile) {
    query.contactMobile = contactMobile
    createForm.contactMobile = contactMobile
  }
  if (inquiryId) createForm.inquiryId = inquiryId
  if (quoteId) createForm.quoteId = quoteId
  if (canQuery.value) loadList()
})
</script>

<template>
  <main class="h5-pickup-page">
    <section class="card hero">
      <h1>H08 H5提货单</h1>
      <p>移动端完成提货单创建、列表追踪与状态更新，打通履约协同闭环。</p>
    </section>

    <section class="card">
      <h2>创建提货单</h2>
      <div class="grid">
        <label>询价单ID <input v-model="createForm.inquiryId" placeholder="IQ..." /></label>
        <label>报价ID <input v-model="createForm.quoteId" placeholder="IQ...-Q1" /></label>
        <label>买方手机号 <input v-model="createForm.contactMobile" placeholder="11位手机号" /></label>
        <label>买方公司 <input v-model="createForm.buyerCompany" placeholder="可选" /></label>
        <label>买方联系人 <input v-model="createForm.buyerContact" placeholder="可选" /></label>
        <label>提货点 <input v-model="createForm.pickupSite" placeholder="仓库地址" /></label>
        <label>提货日期 <input v-model="createForm.pickupDate" type="date" /></label>
        <label>车牌号 <input v-model="createForm.pickupVehicleNo" placeholder="冀A12345" /></label>
        <label>司机姓名 <input v-model="createForm.pickupDriverName" placeholder="张师傅" /></label>
        <label>司机电话 <input v-model="createForm.pickupDriverPhone" placeholder="11位手机号" /></label>
      </div>
      <label class="checkbox">
        <input v-model="createForm.agreedProtocol" type="checkbox" />
        已同意提货服务协议
      </label>
      <label>
        备注
        <input v-model="createForm.remark" placeholder="如 需提前备货" />
      </label>
      <div class="actions">
        <button class="btn btn--primary" :disabled="!canCreate || creating" @click="createPickupOrder">
          {{ creating ? '创建中...' : '创建提货单' }}
        </button>
      </div>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
      <p v-if="successMsg" class="success">{{ successMsg }}</p>
    </section>

    <section class="card">
      <h2>提货单列表</h2>
      <div class="grid">
        <label>手机号 <input v-model="query.contactMobile" placeholder="11位手机号" /></label>
        <label>
          状态
          <select v-model="query.status">
            <option value="">全部</option>
            <option value="CREATED">待确认</option>
            <option value="CONFIRMED">已确认</option>
            <option value="IN_TRANSIT">运输中</option>
            <option value="SIGNED">已签收</option>
            <option value="COMPLETED">已完成</option>
            <option value="CANCELLED">已取消</option>
          </select>
        </label>
        <label>关键词 <input v-model="query.keyword" placeholder="提货单号/商家/规格" /></label>
      </div>
      <div class="actions">
        <button class="btn" :disabled="!canQuery || listLoading" @click="loadList">
          {{ listLoading ? '加载中...' : '查询提货单' }}
        </button>
      </div>
      <p class="tip">{{ stats.tipText || '—' }}</p>
      <div class="stat-grid">
        <span>待确认 {{ stats.createdCount }}</span>
        <span>已确认 {{ stats.confirmedCount }}</span>
        <span>运输中 {{ stats.inTransitCount }}</span>
        <span>已签收 {{ stats.signedCount }}</span>
        <span>已完成 {{ stats.completedCount }}</span>
        <span>已取消 {{ stats.cancelledCount }}</span>
      </div>
      <p v-if="listLoading">加载中...</p>
      <ul v-else-if="list.length" class="order-list">
        <li v-for="item in list" :key="item.pickupId" class="order-item">
          <div class="head">
            <strong>{{ item.pickupNo }}</strong>
            <span class="status">{{ item.statusText }}</span>
          </div>
          <p>商家：{{ item.supplierName }} ｜ 货物：{{ item.goodsSummary }}</p>
          <p>提货点：{{ item.pickupAddress }} ｜ 车牌：{{ item.truckNo }}</p>
          <p>提货日：{{ item.pickupDate }}</p>
          <div class="actions">
            <button class="btn" @click="usePickup(item)">设为当前单</button>
            <button class="btn" @click="viewDetail(item.pickupId)">查看详情</button>
          </div>
        </li>
      </ul>
      <p v-else>暂无提货单</p>

      <div class="actions" v-if="query.total > 0">
        <button class="btn" :disabled="query.page <= 1 || listLoading" @click="prevPage">上一页</button>
        <button class="btn" :disabled="query.page >= totalPages || listLoading" @click="nextPage">下一页</button>
        <span class="tip">第 {{ query.page }} 页 / 共 {{ totalPages }} 页（{{ query.total }} 条）</span>
      </div>
    </section>

    <section class="card">
      <h2>快捷状态更新</h2>
      <div class="grid">
        <label>提货单ID <input v-model="quickStatus.pickupId" placeholder="请选择或输入" /></label>
        <label>
          新状态
          <select v-model="quickStatus.status">
            <option value="CONFIRMED">已确认</option>
            <option value="IN_TRANSIT">运输中</option>
            <option value="SIGNED">已签收</option>
            <option value="COMPLETED">已完成</option>
            <option value="CANCELLED">已取消</option>
          </select>
        </label>
        <label>操作人 <input v-model="quickStatus.operator" placeholder="可选" /></label>
      </div>
      <label>
        备注
        <input v-model="quickStatus.remark" placeholder="如 车辆已出发" />
      </label>
      <div class="actions">
        <button class="btn btn--primary" :disabled="!canQuickStatus || statusLoading" @click="submitQuickStatus">
          {{ statusLoading ? '提交中...' : '更新状态' }}
        </button>
      </div>
    </section>

    <section class="card" v-if="selected">
      <h2>提货单详情</h2>
      <p>提货单号：{{ selected.pickupNo }}</p>
      <p>商家：{{ selected.supplierName }}</p>
      <p>货物：{{ selected.goodsSummary }}</p>
      <p>提货点：{{ selected.pickupAddress }}</p>
      <p>司机：{{ selected.driverName }}（{{ selected.driverPhoneMasked }}）</p>
      <p>当前状态：{{ selected.currentStatusText }}</p>
      <p class="tip">最新备注：{{ selected.latestRemark || '-' }}</p>
      <div class="actions">
        <button class="btn" @click="goH5Reconcile">去P09对账通</button>
      </div>
    </section>
  </main>
</template>

<style scoped>
.h5-pickup-page {
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
.checkbox {
  margin-top: 10px;
  display: inline-flex;
  align-items: center;
  gap: 8px;
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
