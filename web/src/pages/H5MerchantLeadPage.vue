<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const loading = ref(false)
const actionLoading = ref(false)
const errorMsg = ref('')
const successMsg = ref('')
const list = ref([])

const query = reactive({
  merchantId: 'S001',
  status: '',
  keyword: '',
  page: 1,
  pageSize: 10,
  total: 0
})

const stats = reactive({
  pendingQuoteCount: 0,
  quotedCount: 0,
  wonCount: 0,
  lostCount: 0,
  closedCount: 0,
  tipText: ''
})

const quickQuote = reactive({
  leadId: '',
  unitPrice: '',
  deliveryDays: '1',
  paymentTerm: '月结15天',
  quoteRemark: ''
})

const quickStatus = reactive({
  leadId: '',
  status: 'CONTACTED',
  comment: ''
})

const statusLabelMap = {
  NEW: '新线索',
  FOLLOWING: '跟进中',
  CONTACTED: '已联系',
  QUOTED: '已报价',
  WON: '已赢单',
  LOST: '已丢单',
  CLOSED: '已关闭'
}

const totalPages = computed(() => Math.max(Math.ceil(query.total / query.pageSize), 1))
const canQuickQuote = computed(
  () => quickQuote.leadId.trim() && Number(quickQuote.unitPrice) > 0 && Number(quickQuote.deliveryDays) >= 0
)
const canQuickStatus = computed(() => quickStatus.leadId.trim() && quickStatus.status.trim())

function useLead(leadId) {
  if (!leadId) return
  quickQuote.leadId = leadId
  quickStatus.leadId = leadId
}

async function loadLeads() {
  if (!query.merchantId.trim() || loading.value) return
  loading.value = true
  errorMsg.value = ''
  try {
    const params = new URLSearchParams()
    params.set('merchantId', query.merchantId.trim())
    if (query.status) params.set('status', query.status)
    if (query.keyword.trim()) params.set('keyword', query.keyword.trim())
    params.set('page', String(query.page))
    params.set('pageSize', String(query.pageSize))
    const resp = await fetch(`/api/v1/inquiries/h5/merchant/leads?${params.toString()}`)
    const json = await resp.json()
    if (json.code !== '0') {
      throw new Error(json.message || '加载我的线索失败')
    }
    const data = json.data || {}
    list.value = Array.isArray(data.items) ? data.items : []
    query.total = Number(data.total || 0)
    query.page = Number(data.page || query.page)
    query.pageSize = Number(data.pageSize || query.pageSize)
    stats.pendingQuoteCount = Number(data.pendingQuoteCount || 0)
    stats.quotedCount = Number(data.quotedCount || 0)
    stats.wonCount = Number(data.wonCount || 0)
    stats.lostCount = Number(data.lostCount || 0)
    stats.closedCount = Number(data.closedCount || 0)
    stats.tipText = data.tipText || ''
    if (list.value.length > 0 && !quickQuote.leadId) {
      useLead(list.value[0].leadId)
    }
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    loading.value = false
  }
}

async function submitQuickQuote() {
  if (!canQuickQuote.value || actionLoading.value) return
  actionLoading.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      merchantId: query.merchantId.trim(),
      unitPrice: String(Number(quickQuote.unitPrice)),
      deliveryDays: String(Number(quickQuote.deliveryDays)),
      paymentTerm: quickQuote.paymentTerm.trim() || '月结15天',
      quoteRemark: quickQuote.quoteRemark.trim() || null
    }
    const resp = await fetch(
      `/api/v1/inquiries/h5/merchant/leads/${encodeURIComponent(quickQuote.leadId.trim())}/quick-quote`,
      {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      }
    )
    const json = await resp.json()
    if (json.code !== '0') {
      throw new Error(json.message || '快捷报价失败')
    }
    successMsg.value = json.data?.message || '快捷报价成功'
    await loadLeads()
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    actionLoading.value = false
  }
}

async function submitQuickStatus() {
  if (!canQuickStatus.value || actionLoading.value) return
  actionLoading.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      merchantId: query.merchantId.trim(),
      status: quickStatus.status,
      comment: quickStatus.comment.trim() || null
    }
    const resp = await fetch(
      `/api/v1/inquiries/h5/merchant/leads/${encodeURIComponent(quickStatus.leadId.trim())}/quick-status`,
      {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      }
    )
    const json = await resp.json()
    if (json.code !== '0') {
      throw new Error(json.message || '快捷更新状态失败')
    }
    successMsg.value = json.data?.message || '状态更新成功'
    await loadLeads()
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    actionLoading.value = false
  }
}

function prevPage() {
  if (query.page <= 1 || loading.value) return
  query.page -= 1
  loadLeads()
}

function nextPage() {
  if (query.page >= totalPages.value || loading.value) return
  query.page += 1
  loadLeads()
}

onMounted(() => {
  const merchantId = String(route.query.merchantId || '').trim()
  if (merchantId) query.merchantId = merchantId
  loadLeads()
})
</script>

<template>
  <main class="h5-lead-page">
    <section class="card hero">
      <h1>H06 H5我的线索（商家）</h1>
      <p>移动端查看线索池、执行快捷报价与状态流转，优先处理新线索提升成交率。</p>
    </section>

    <section class="card">
      <div class="filters">
        <label>
          商家ID
          <input v-model="query.merchantId" placeholder="如 S001" />
        </label>
        <label>
          线索状态
          <select v-model="query.status">
            <option value="">全部</option>
            <option value="NEW">新线索</option>
            <option value="CONTACTED">已联系</option>
            <option value="QUOTED">已报价</option>
            <option value="WON">已赢单</option>
            <option value="LOST">已丢单</option>
            <option value="CLOSED">已关闭</option>
          </select>
        </label>
        <label>
          关键词
          <input v-model="query.keyword" placeholder="询价单号/规格/城市" />
        </label>
      </div>
      <div class="actions">
        <button class="btn btn--primary" :disabled="loading" @click="loadLeads">刷新线索</button>
      </div>
      <p class="tip">{{ stats.tipText || '—' }}</p>
      <div class="stat-grid">
        <span>待报价 {{ stats.pendingQuoteCount }}</span>
        <span>已报价 {{ stats.quotedCount }}</span>
        <span>已赢单 {{ stats.wonCount }}</span>
        <span>已丢单 {{ stats.lostCount }}</span>
        <span>已关闭 {{ stats.closedCount }}</span>
      </div>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
      <p v-if="successMsg" class="success">{{ successMsg }}</p>
    </section>

    <section class="card">
      <h2>线索列表</h2>
      <p v-if="loading">加载中...</p>
      <p v-else-if="list.length === 0">暂无线索</p>
      <ul v-else class="lead-list">
        <li v-for="item in list" :key="item.leadId" class="lead-item">
          <div class="head">
            <strong>{{ item.specText }}</strong>
            <span class="status">{{ statusLabelMap[item.status] || item.status }}</span>
          </div>
          <p>询价单：{{ item.inquiryNo }}</p>
          <p>收货地：{{ item.deliveryCity }} ｜ 数量：{{ item.demandQtyTon }} 吨</p>
          <p>票据：{{ item.invoiceNeed }} ｜ 联系人：{{ item.buyerContactMasked }}</p>
          <p>更新时间：{{ item.updatedAt }}</p>
          <button class="btn" @click="useLead(item.leadId)">设为当前操作线索</button>
        </li>
      </ul>
      <div v-if="query.total > 0" class="actions">
        <button class="btn" :disabled="query.page <= 1 || loading" @click="prevPage">上一页</button>
        <button class="btn" :disabled="query.page >= totalPages || loading" @click="nextPage">下一页</button>
        <span class="tip">第 {{ query.page }} 页 / 共 {{ totalPages }} 页（{{ query.total }} 条）</span>
      </div>
    </section>

    <section class="card">
      <h2>快捷操作</h2>
      <div class="quick-grid">
        <label>
          当前线索ID（报价）
          <input v-model="quickQuote.leadId" placeholder="请选择或输入线索ID" />
        </label>
        <label>
          单价（元/吨）
          <input v-model="quickQuote.unitPrice" type="number" min="0" />
        </label>
        <label>
          交期（天）
          <input v-model="quickQuote.deliveryDays" type="number" min="0" />
        </label>
        <label>
          付款条款
          <input v-model="quickQuote.paymentTerm" />
        </label>
        <label>
          报价备注
          <input v-model="quickQuote.quoteRemark" placeholder="如 可当日排产" />
        </label>
      </div>
      <div class="actions">
        <button class="btn btn--primary" :disabled="!canQuickQuote || actionLoading" @click="submitQuickQuote">
          {{ actionLoading ? '处理中...' : '快捷报价' }}
        </button>
      </div>

      <div class="quick-grid second">
        <label>
          当前线索ID（状态）
          <input v-model="quickStatus.leadId" placeholder="请选择或输入线索ID" />
        </label>
        <label>
          新状态
          <select v-model="quickStatus.status">
            <option value="CONTACTED">已联系</option>
            <option value="QUOTED">已报价</option>
            <option value="WON">已赢单</option>
            <option value="LOST">已丢单</option>
            <option value="CLOSED">已关闭</option>
          </select>
        </label>
        <label>
          备注
          <input v-model="quickStatus.comment" placeholder="如 已电话跟进" />
        </label>
      </div>
      <div class="actions">
        <button class="btn" :disabled="!canQuickStatus || actionLoading" @click="submitQuickStatus">快捷更新状态</button>
      </div>
    </section>
  </main>
</template>

<style scoped>
.h5-lead-page {
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
.filters,
.quick-grid {
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
  margin: 10px 0 0;
}
.stat-grid {
  margin-top: 10px;
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 8px;
  color: #4b5563;
}
.lead-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: grid;
  gap: 10px;
}
.lead-item {
  border: 1px solid #f2f4f7;
  border-radius: 10px;
  padding: 10px;
}
.head {
  display: flex;
  justify-content: space-between;
  gap: 10px;
}
.head strong {
  font-size: 15px;
}
.status {
  font-size: 12px;
  color: #92400e;
  background: #fff7ed;
  border-radius: 999px;
  padding: 2px 8px;
}
.lead-item p {
  margin: 6px 0 0;
  color: #4b5563;
}
.second {
  margin-top: 14px;
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
