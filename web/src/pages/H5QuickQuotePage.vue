<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const quoteLoading = ref(false)
const listLoading = ref(false)
const errorMsg = ref('')
const successMsg = ref('')
const leadOptions = ref([])

const query = reactive({
  merchantId: 'S001',
  leadId: ''
})

const detail = reactive({
  inquiryNo: '',
  specText: '',
  demandQtyTon: '',
  deliveryCity: '',
  invoiceNeed: '',
  currentStatus: '',
  tipText: ''
})

const quoteForm = reactive({
  unitPrice: '',
  deliveryDays: '1',
  paymentTerm: '月结15天',
  quoteRemark: ''
})

const canInit = computed(() => query.merchantId.trim() && query.leadId.trim())
const canSubmit = computed(
  () =>
    query.merchantId.trim() &&
    query.leadId.trim() &&
    Number(quoteForm.unitPrice) > 0 &&
    Number(quoteForm.deliveryDays) >= 0
)

async function loadLeadOptions() {
  if (listLoading.value || !query.merchantId.trim()) return
  listLoading.value = true
  errorMsg.value = ''
  try {
    const params = new URLSearchParams()
    params.set('merchantId', query.merchantId.trim())
    params.set('page', '1')
    params.set('pageSize', '20')
    const resp = await fetch(`/api/v1/inquiries/h5/merchant/leads?${params.toString()}`)
    const json = await resp.json()
    if (json.code !== '0') {
      throw new Error(json.message || '加载线索列表失败')
    }
    const items = Array.isArray(json.data?.items) ? json.data.items : []
    leadOptions.value = items
    if (!query.leadId && items.length > 0) {
      query.leadId = items[0].leadId
    }
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    listLoading.value = false
  }
}

async function initQuickQuote() {
  if (!canInit.value || loading.value) return
  loading.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const params = new URLSearchParams()
    params.set('merchantId', query.merchantId.trim())
    params.set('leadId', query.leadId.trim())
    const resp = await fetch(`/api/v1/inquiries/h5/quick-quote/init?${params.toString()}`)
    const json = await resp.json()
    if (json.code !== '0') {
      throw new Error(json.message || '初始化快捷报价失败')
    }
    const data = json.data || {}
    detail.inquiryNo = data.inquiryNo || ''
    detail.specText = data.specText || ''
    detail.demandQtyTon = data.demandQtyTon || ''
    detail.deliveryCity = data.deliveryCity || ''
    detail.invoiceNeed = data.invoiceNeed || ''
    detail.currentStatus = data.currentStatus || ''
    detail.tipText = data.tipText || ''
    quoteForm.unitPrice = data.suggestedUnitPrice || ''
    quoteForm.deliveryDays = data.suggestedDeliveryDays || '1'
    quoteForm.paymentTerm = data.defaultPaymentTerm || '月结15天'
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    loading.value = false
  }
}

async function submitQuickQuote() {
  if (!canSubmit.value || quoteLoading.value) return
  quoteLoading.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      merchantId: query.merchantId.trim(),
      unitPrice: String(Number(quoteForm.unitPrice)),
      deliveryDays: String(Number(quoteForm.deliveryDays)),
      paymentTerm: quoteForm.paymentTerm.trim() || '月结15天',
      quoteRemark: quoteForm.quoteRemark.trim() || null
    }
    const resp = await fetch(
      `/api/v1/inquiries/h5/merchant/leads/${encodeURIComponent(query.leadId.trim())}/quick-quote`,
      {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      }
    )
    const json = await resp.json()
    if (json.code !== '0') {
      throw new Error(json.message || '快捷报价提交失败')
    }
    successMsg.value = json.data?.message || '快捷报价已提交'
    detail.currentStatus = json.data?.status || 'QUOTED'
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    quoteLoading.value = false
  }
}

function goH5Leads() {
  router.push(`/h5/merchant/leads?merchantId=${encodeURIComponent(query.merchantId.trim() || 'S001')}`)
}

onMounted(async () => {
  const merchantId = String(route.query.merchantId || '').trim()
  const leadId = String(route.query.leadId || '').trim()
  if (merchantId) query.merchantId = merchantId
  if (leadId) query.leadId = leadId
  await loadLeadOptions()
  if (query.merchantId && query.leadId) {
    await initQuickQuote()
  }
})
</script>

<template>
  <main class="h5-quick-quote-page">
    <section class="card hero">
      <h1>H07 H5快捷报价</h1>
      <p>移动端针对线索一键初始化报价参数，快速提交，提升首报时效与转化率。</p>
    </section>

    <section class="card">
      <div class="filters">
        <label>
          商家ID
          <input v-model="query.merchantId" placeholder="如 S001" />
        </label>
        <label>
          线索ID
          <select v-model="query.leadId">
            <option value="">请选择线索</option>
            <option v-for="item in leadOptions" :key="item.leadId" :value="item.leadId">
              {{ item.leadId }}｜{{ item.specText }}｜{{ item.deliveryCity }}
            </option>
          </select>
        </label>
      </div>
      <div class="actions">
        <button class="btn" :disabled="listLoading" @click="loadLeadOptions">
          {{ listLoading ? '加载中...' : '刷新线索' }}
        </button>
        <button class="btn btn--primary" :disabled="!canInit || loading" @click="initQuickQuote">
          {{ loading ? '初始化中...' : '初始化快捷报价' }}
        </button>
        <button class="btn" @click="goH5Leads">去H06我的线索</button>
      </div>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
      <p v-if="successMsg" class="success">{{ successMsg }}</p>
    </section>

    <section class="card">
      <h2>线索摘要</h2>
      <p>询价单号：{{ detail.inquiryNo || '-' }}</p>
      <p>规格：{{ detail.specText || '-' }}</p>
      <p>数量：{{ detail.demandQtyTon || '-' }} 吨</p>
      <p>收货地：{{ detail.deliveryCity || '-' }}</p>
      <p>票据：{{ detail.invoiceNeed || '-' }}</p>
      <p>当前状态：{{ detail.currentStatus || '-' }}</p>
      <p class="tip">{{ detail.tipText || '建议优先处理高意向线索，快速报价后及时回访。' }}</p>
    </section>

    <section class="card">
      <h2>快捷报价提交</h2>
      <div class="quick-grid">
        <label>
          单价（元/吨）
          <input v-model="quoteForm.unitPrice" type="number" min="0" />
        </label>
        <label>
          交期（天）
          <input v-model="quoteForm.deliveryDays" type="number" min="0" />
        </label>
        <label>
          付款条款
          <input v-model="quoteForm.paymentTerm" />
        </label>
        <label>
          报价备注
          <input v-model="quoteForm.quoteRemark" placeholder="如 可当日排产、支持夜间装车" />
        </label>
      </div>
      <div class="actions">
        <button class="btn btn--primary" :disabled="!canSubmit || quoteLoading" @click="submitQuickQuote">
          {{ quoteLoading ? '提交中...' : '提交快捷报价' }}
        </button>
      </div>
    </section>
  </main>
</template>

<style scoped>
.h5-quick-quote-page {
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
