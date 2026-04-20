<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

const query = reactive({
  draftId: '',
  sortBy: 'TOTAL_PRICE',
  deliveryCycle: '',
  invoiceType: '',
  page: 1,
  pageSize: 10,
  total: 0
})

const summary = reactive({
  inquiryId: '',
  inquiryNo: '',
  inquiryStatus: '',
  specText: '',
  demandQtyTon: '',
  deliveryCity: '',
  contactMobileMasked: '',
  quoteCount: 0,
  nextActionTip: '',
  dealConfirmBaseUrl: ''
})

const quotes = ref([])

const totalPages = computed(() => Math.max(Math.ceil(query.total / query.pageSize), 1))
const canQuery = computed(() => query.draftId.trim().length > 0)

async function loadCompare() {
  if (!canQuery.value || loading.value) return
  loading.value = true
  errorMsg.value = ''
  try {
    const params = new URLSearchParams()
    params.set('draftId', query.draftId.trim())
    params.set('sortBy', query.sortBy)
    if (query.deliveryCycle) {
      params.set('deliveryCycle', query.deliveryCycle)
    }
    if (query.invoiceType) {
      params.set('invoiceType', query.invoiceType)
    }
    params.set('page', String(query.page))
    params.set('pageSize', String(query.pageSize))

    const resp = await fetch(`/api/v1/inquiries/h5/quote-compare?${params.toString()}`)
    const json = await resp.json()
    if (json.code !== '0') {
      throw new Error(json.message || '加载报价对比失败')
    }
    const data = json.data || {}
    summary.inquiryId = data.inquiryId || ''
    summary.inquiryNo = data.inquiryNo || ''
    summary.inquiryStatus = data.inquiryStatus || ''
    summary.specText = data.specText || ''
    summary.demandQtyTon = data.demandQtyTon || ''
    summary.deliveryCity = data.deliveryCity || ''
    summary.contactMobileMasked = data.contactMobileMasked || ''
    summary.quoteCount = Number(data.quoteCount || 0)
    summary.nextActionTip = data.nextActionTip || ''
    summary.dealConfirmBaseUrl = data.dealConfirmBaseUrl || '/inquiry/deal/confirm'

    quotes.value = Array.isArray(data.quotes) ? data.quotes : []
    query.total = Number(data.total || 0)
    query.page = Number(data.page || query.page)
    query.pageSize = Number(data.pageSize || query.pageSize)
    successMsg.value = `已加载 ${quotes.value.length} 条报价`
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    loading.value = false
  }
}

function goDealConfirm(item) {
  if (!item?.quoteId || !summary.inquiryId) return
  const params = new URLSearchParams()
  params.set('inquiryId', summary.inquiryId)
  params.set('quoteId', item.quoteId)
  const mobile = summary.contactMobileMasked.replace(/\D/g, '')
  if (mobile.length === 11) {
    params.set('contactMobile', mobile)
  }
  router.push(`${summary.dealConfirmBaseUrl}?${params.toString()}`)
}

function prevPage() {
  if (query.page <= 1 || loading.value) return
  query.page -= 1
  loadCompare()
}

function nextPage() {
  if (query.page >= totalPages.value || loading.value) return
  query.page += 1
  loadCompare()
}

function backStep3() {
  if (!query.draftId) return
  router.push(`/h5/inquiry/step3?draftId=${encodeURIComponent(query.draftId)}`)
}

onMounted(() => {
  const draftId = String(route.query.draftId || '').trim()
  if (draftId) {
    query.draftId = draftId
    loadCompare()
  } else {
    errorMsg.value = '缺少draftId，请先完成Step3'
  }
})
</script>

<template>
  <main class="h5-quote-compare-page">
    <section class="card hero">
      <h1>H05 H5报价对比</h1>
      <p>对比总价、交付天数、响应时效与履约评分，帮助你快速选择成交商家。</p>
    </section>

    <section class="card filters">
      <label>
        草稿ID
        <input v-model="query.draftId" placeholder="请输入draftId" />
      </label>
      <label>
        排序
        <select v-model="query.sortBy">
          <option value="TOTAL_PRICE">总价优先</option>
          <option value="UNIT_PRICE">单价优先</option>
          <option value="DELIVERY_HOURS">交付天数优先</option>
          <option value="RESPONSE_MINUTES">响应时效优先</option>
          <option value="SUPPLIER_SCORE">履约评分优先</option>
        </select>
      </label>
      <label>
        交付周期
        <input v-model="query.deliveryCycle" placeholder="如 3日到货（可选）" />
      </label>
      <label>
        票据
        <select v-model="query.invoiceType">
          <option value="">不限</option>
          <option value="YES">需要开票</option>
        </select>
      </label>
      <div class="actions">
        <button class="btn btn--primary" :disabled="!canQuery || loading" @click="loadCompare">
          {{ loading ? '加载中...' : '查询报价' }}
        </button>
        <button class="btn" :disabled="!query.draftId" @click="backStep3">返回Step3</button>
      </div>
      <p v-if="successMsg" class="success">{{ successMsg }}</p>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
    </section>

    <section v-if="summary.inquiryNo" class="card">
      <h2>询价摘要</h2>
      <p>询价单号：{{ summary.inquiryNo }} ｜ 状态：{{ summary.inquiryStatus }}</p>
      <p>规格：{{ summary.specText }} ｜ 数量：{{ summary.demandQtyTon }} 吨</p>
      <p>收货地：{{ summary.deliveryCity }} ｜ 联系手机：{{ summary.contactMobileMasked }}</p>
      <p>报价数：{{ summary.quoteCount }} ｜ {{ summary.nextActionTip }}</p>
    </section>

    <section class="card">
      <h2>报价列表</h2>
      <p v-if="loading">加载中...</p>
      <p v-else-if="quotes.length === 0">暂无报价，请稍后刷新。</p>
      <ul v-else class="quote-list">
        <li v-for="item in quotes" :key="item.quoteId" class="quote-item">
          <div class="row">
            <strong>{{ item.supplierName }}</strong>
            <span>{{ item.supplierLevel }} ｜ 评分 {{ item.serviceScore }}</span>
          </div>
          <p>总价：¥{{ item.totalAmount }} ｜ 单价：{{ item.unitPrice }} / 吨</p>
          <p>交付：{{ item.deliveryDays }}天 ｜ 响应：{{ item.responseMinutes }} 分钟</p>
          <p>票据：{{ item.canInvoice }} ｜ 运费：{{ item.canFreight }} ｜ 账期：{{ item.paymentTerm }}</p>
          <p>履约率：{{ item.fulfillmentRate }} ｜ 备注：{{ item.quoteRemark }}</p>
          <button class="btn btn--primary small" @click="goDealConfirm(item)">去成交确认</button>
        </li>
      </ul>
      <div v-if="query.total > 0" class="actions">
        <button class="btn" :disabled="query.page <= 1 || loading" @click="prevPage">上一页</button>
        <button class="btn" :disabled="query.page >= totalPages || loading" @click="nextPage">下一页</button>
        <span class="tip">第 {{ query.page }} 页 / 共 {{ totalPages }} 页（{{ query.total }} 条）</span>
      </div>
    </section>
  </main>
</template>

<style scoped>
.h5-quote-compare-page {
  max-width: 860px;
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
.filters {
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
.btn.small {
  padding: 6px 10px;
  font-size: 12px;
}
.quote-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: grid;
  gap: 10px;
}
.quote-item {
  border: 1px solid #f1f3f5;
  border-radius: 10px;
  padding: 10px;
}
.row {
  display: flex;
  justify-content: space-between;
  gap: 8px;
}
.quote-item p {
  margin: 6px 0 0;
  color: #4b5563;
}
.tip {
  color: #4b5563;
}
.success {
  color: #1f7a43;
  margin: 0;
}
.error {
  color: #b42318;
  margin: 0;
}
</style>
