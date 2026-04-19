<script setup>
import { computed, reactive, ref } from 'vue'

const loading = ref(false)
const detailLoading = ref(false)
const errorMsg = ref('')
const detailError = ref('')
const list = ref([])
const selected = ref(null)

const query = reactive({
  merchantId: 'S001',
  status: '',
  keyword: '',
  page: 1,
  pageSize: 10,
  total: 0
})

const quoteForm = reactive({
  unitPrice: '',
  taxMode: '含税到厂',
  paymentTerm: '月结15天',
  deliveryDays: '',
  canInvoice: 'YES',
  canFreight: 'YES',
  remark: ''
})

const statusForm = reactive({
  status: 'CONTACTED',
  remark: ''
})

const statusLabelMap = {
  NEW: '新线索',
  CONTACTED: '已联系',
  QUOTED: '已报价',
  WON: '已赢单',
  LOST: '已丢单',
  CLOSED: '已关闭'
}

const canQuote = computed(() => {
  return (
    selected.value &&
    Number(quoteForm.unitPrice) > 0 &&
    Number(quoteForm.deliveryDays) >= 0
  )
})

const canUpdateStatus = computed(() => {
  return selected.value && statusForm.status.trim().length > 0
})

async function queryLeads() {
  if (!query.merchantId.trim()) return
  loading.value = true
  errorMsg.value = ''
  try {
    const params = new URLSearchParams()
    params.set('merchantId', query.merchantId.trim())
    if (query.status) params.set('status', query.status)
    if (query.keyword.trim()) params.set('keyword', query.keyword.trim())
    params.set('page', String(query.page))
    params.set('pageSize', String(query.pageSize))
    const resp = await fetch(`/api/v1/inquiries/merchant/leads?${params.toString()}`)
    const json = await resp.json()
    if (json.code !== '0') {
      throw new Error(json.message || '查询线索失败')
    }
    list.value = json.data.items || []
    query.total = Number(json.data.total || 0)
    query.page = Number(json.data.page || query.page)
    query.pageSize = Number(json.data.pageSize || query.pageSize)
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    loading.value = false
  }
}

async function loadDetail(id) {
  detailLoading.value = true
  detailError.value = ''
  selected.value = null
  try {
    const params = new URLSearchParams()
    params.set('merchantId', query.merchantId.trim())
    const resp = await fetch(`/api/v1/inquiries/merchant/leads/${encodeURIComponent(id)}?${params.toString()}`)
    const json = await resp.json()
    if (json.code !== '0') {
      throw new Error(json.message || '加载线索详情失败')
    }
    selected.value = json.data.lead || null
    if (!selected.value) {
      throw new Error('线索详情数据结构异常')
    }
    quoteForm.unitPrice = ''
    quoteForm.deliveryDays = ''
    quoteForm.remark = ''
    statusForm.status = selected.value.status || 'CONTACTED'
    statusForm.remark = ''
  } catch (error) {
    detailError.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    detailLoading.value = false
  }
}

async function submitQuote() {
  if (!selected.value || !canQuote.value) return
  detailLoading.value = true
  detailError.value = ''
  try {
    const payload = {
      merchantId: query.merchantId.trim(),
      supplierName: selected.value.supplierName,
      unitPrice: String(Number(quoteForm.unitPrice)),
      totalAmount: String(Number(quoteForm.unitPrice) * Number(selected.value.demandQtyTon || 0)),
      deliveryDays: String(Number(quoteForm.deliveryDays)),
      paymentTerm: quoteForm.paymentTerm,
      canInvoice: quoteForm.canInvoice,
      quoteRemark: quoteForm.remark.trim() || null,
      operator: 'merchant-ui'
    }
    const resp = await fetch(
      `/api/v1/inquiries/merchant/leads/${encodeURIComponent(selected.value.leadId)}/quote`,
      {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      }
    )
    const json = await resp.json()
    if (json.code !== '0') {
      throw new Error(json.message || '提交报价失败')
    }
    selected.value = json.data
    await queryLeads()
  } catch (error) {
    detailError.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    detailLoading.value = false
  }
}

async function updateStatus() {
  if (!selected.value || !canUpdateStatus.value) return
  detailLoading.value = true
  detailError.value = ''
  try {
    const payload = {
      merchantId: query.merchantId.trim(),
      status: statusForm.status,
      remark: statusForm.remark.trim() || null,
      operator: 'merchant-ui'
    }
    const resp = await fetch(
      `/api/v1/inquiries/merchant/leads/${encodeURIComponent(selected.value.leadId)}/status`,
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
    selected.value = json.data
    await queryLeads()
  } catch (error) {
    detailError.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    detailLoading.value = false
  }
}

function prevPage() {
  if (query.page <= 1 || loading.value) return
  query.page -= 1
  queryLeads()
}

function nextPage() {
  if (query.page * query.pageSize >= query.total || loading.value) return
  query.page += 1
  queryLeads()
}
</script>

<template>
  <main class="lead-manage-page">
    <section class="card">
      <h1>P05 商家线索管理页</h1>
      <p class="desc">商家按询价线索进行跟进、报价和状态流转管理。</p>
      <div class="grid">
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
        <button class="btn primary" :disabled="loading" @click="queryLeads">查询线索</button>
      </div>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
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
          <p>询价单：{{ item.inquiryNo }} ｜ 城市：{{ item.deliveryCity }} ｜ 数量：{{ item.demandQtyTon }} 吨</p>
          <p>票据需求：{{ item.invoiceNeed || '-' }} ｜ 最新跟进：{{ item.latestFollow || '-' }}</p>
          <p>更新时间：{{ item.updatedAt }}</p>
          <button class="btn" @click="loadDetail(item.leadId)">查看详情</button>
        </li>
      </ul>
      <div v-if="query.total > 0" class="actions">
        <button class="btn" :disabled="query.page <= 1 || loading" @click="prevPage">上一页</button>
        <button class="btn" :disabled="query.page * query.pageSize >= query.total || loading" @click="nextPage">
          下一页
        </button>
        <span class="tip">第 {{ query.page }} 页 / 共 {{ Math.max(Math.ceil(query.total / query.pageSize), 1) }} 页</span>
      </div>
    </section>

    <section class="card" v-if="selected">
      <h2>线索详情与操作</h2>
      <p v-if="detailLoading">处理中...</p>
      <p v-if="detailError" class="error">{{ detailError }}</p>

      <div class="detail">
        <p>线索ID：{{ selected.leadId }}</p>
        <p>询价单：{{ selected.inquiryNo }}</p>
        <p>规格：{{ selected.specText }}</p>
        <p>收货地：{{ selected.deliveryCity }} ｜ 数量：{{ selected.demandQtyTon }} 吨</p>
        <p>状态：{{ statusLabelMap[selected.status] || selected.status }}</p>
      </div>

      <h3>提交报价</h3>
      <div class="grid">
        <label>
          单价（元/吨）
          <input v-model="quoteForm.unitPrice" type="number" min="0" />
        </label>
        <label>
          交期（天）
          <input v-model="quoteForm.deliveryDays" type="number" min="0" />
        </label>
        <label>
          税费模式
          <input v-model="quoteForm.taxMode" />
        </label>
        <label>
          付款条款
          <input v-model="quoteForm.paymentTerm" />
        </label>
        <label>
          可开票
          <select v-model="quoteForm.canInvoice">
            <option value="YES">可开票</option>
            <option value="NO">不可开票</option>
          </select>
        </label>
        <label>
          可含运费
          <select v-model="quoteForm.canFreight">
            <option value="YES">可含运费</option>
            <option value="NO">不可含运费</option>
          </select>
        </label>
      </div>
      <label>
        报价备注
        <textarea v-model="quoteForm.remark" rows="2" placeholder="如：48小时可发货"></textarea>
      </label>
      <div class="actions">
        <button class="btn primary" :disabled="!canQuote || detailLoading" @click="submitQuote">提交报价</button>
      </div>

      <h3>更新线索状态</h3>
      <div class="grid">
        <label>
          状态
          <select v-model="statusForm.status">
            <option value="CONTACTED">已联系</option>
            <option value="QUOTED">已报价</option>
            <option value="WON">已赢单</option>
            <option value="LOST">已丢单</option>
            <option value="CLOSED">已关闭</option>
          </select>
        </label>
      </div>
      <label>
        备注
        <textarea v-model="statusForm.remark" rows="2" placeholder="如：客户要求下周复询"></textarea>
      </label>
      <div class="actions">
        <button class="btn" :disabled="!canUpdateStatus || detailLoading" @click="updateStatus">更新状态</button>
      </div>
    </section>
  </main>
</template>

<style scoped>
.lead-manage-page {
  max-width: 1100px;
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
  margin: 0 0 12px;
  color: #4b5563;
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
.lead-list {
  list-style: none;
  margin: 0;
  padding: 0;
}
.lead-item {
  border-top: 1px dashed #ececec;
  padding: 12px 0;
}
.lead-item:first-child {
  border-top: 0;
}
.head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.status {
  font-size: 12px;
  color: #fff;
  background: #f57c00;
  padding: 2px 8px;
  border-radius: 999px;
}
.lead-item p,
.detail p {
  margin: 6px 0 0;
  color: #4b5563;
}
.tip {
  color: #4b5563;
}
.error {
  color: #b42318;
  margin-top: 10px;
}
@media (max-width: 980px) {
  .grid {
    grid-template-columns: 1fr;
  }
}
</style>
