<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const loading = ref(false)
const detailLoading = ref(false)
const paySubmitting = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

const records = ref([])
const selectedCashierOrderId = ref('')
const detail = ref(null)

const statusFilter = ref('')
const keyword = ref('')
const pageNo = ref(1)
const pageSize = ref(10)
const total = ref(0)

const payForm = reactive({
  payMethod: 'BANK_TRANSFER',
  payerName: '采购财务',
  remark: '',
  operator: 'pc-n10-ui'
})

const token = computed(() => localStorage.getItem('N01_AUTH_TOKEN') || '')
const hasSession = computed(() => token.value.length > 0)
const canPay = computed(
  () =>
    hasSession.value &&
    selectedCashierOrderId.value &&
    detail.value &&
    detail.value.status === 'UNPAID' &&
    payForm.payMethod &&
    !paySubmitting.value
)

async function loadCashierOrders() {
  if (!hasSession.value || loading.value) {
    if (!hasSession.value) {
      errorMsg.value = '请先登录后查看收银台'
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

    const resp = await fetch(`/api/v1/auth/cashier/orders?${params.toString()}`, {
      headers: { 'X-Auth-Token': token.value }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `收银单列表加载失败(${resp.status})`)
    }
    const data = json.data || {}
    records.value = data.records || []
    total.value = data.total || 0

    if (!selectedCashierOrderId.value && records.value.length > 0) {
      selectedCashierOrderId.value = records.value[0].cashierOrderId
      await loadCashierDetail()
    } else if (selectedCashierOrderId.value) {
      const exists = records.value.some((item) => item.cashierOrderId === selectedCashierOrderId.value)
      if (!exists && records.value.length > 0) {
        selectedCashierOrderId.value = records.value[0].cashierOrderId
        await loadCashierDetail()
      }
    }
  } catch (error) {
    errorMsg.value = error.message || '收银单列表加载失败'
  } finally {
    loading.value = false
  }
}

async function loadCashierDetail() {
  if (!hasSession.value || !selectedCashierOrderId.value || detailLoading.value) return
  detailLoading.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const resp = await fetch(`/api/v1/auth/cashier/orders/${selectedCashierOrderId.value}`, {
      headers: { 'X-Auth-Token': token.value }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `收银单详情加载失败(${resp.status})`)
    }
    detail.value = json.data || null
  } catch (error) {
    errorMsg.value = error.message || '收银单详情加载失败'
  } finally {
    detailLoading.value = false
  }
}

async function submitPay() {
  if (!canPay.value) return
  paySubmitting.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      payMethod: payForm.payMethod,
      payerName: payForm.payerName.trim() || null,
      remark: payForm.remark.trim() || null,
      operator: payForm.operator
    }
    const resp = await fetch(`/api/v1/auth/cashier/orders/${selectedCashierOrderId.value}/pay`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Auth-Token': token.value
      },
      body: JSON.stringify(payload)
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `支付提交失败(${resp.status})`)
    }
    detail.value = json.data || null
    payForm.remark = ''
    successMsg.value = '支付成功，订单状态已联动更新'
    await loadCashierOrders()
  } catch (error) {
    errorMsg.value = error.message || '支付提交失败'
  } finally {
    paySubmitting.value = false
  }
}

function chooseCashierOrder(cashierOrderId) {
  selectedCashierOrderId.value = cashierOrderId
  loadCashierDetail()
}

function goOrderDetail() {
  router.push('/account/order-detail')
}

function goAfterSaleDispute() {
  router.push('/account/after-sale-dispute')
}

function goAfterSaleProgress() {
  router.push('/account/after-sale-progress')
}

function goHome() {
  router.push('/')
}

onMounted(() => {
  loadCashierOrders()
})
</script>

<template>
  <main class="n10-page">
    <section class="card hero">
      <h1>PC-N10 收银台</h1>
      <p>集中处理订单付款，记录支付流水，并与订单履约状态联动更新。</p>
      <div class="hero-actions">
        <button class="btn" @click="goOrderDetail">返回订单详情页</button>
        <button class="btn" @click="goAfterSaleDispute">售后/争议发起页</button>
        <button class="btn" @click="goAfterSaleProgress">售后处理进度页</button>
        <button class="btn" @click="goHome">返回首页</button>
      </div>
    </section>

    <section class="layout">
      <article class="card left">
        <div class="head">
          <h2>收银单列表</h2>
          <button class="btn" :disabled="loading" @click="loadCashierOrders">{{ loading ? '刷新中...' : '刷新' }}</button>
        </div>
        <div class="filters">
          <select v-model="statusFilter">
            <option value="">全部状态</option>
            <option value="UNPAID">待支付</option>
            <option value="PAID">已支付</option>
          </select>
          <input v-model="keyword" placeholder="按收银单号/订单号/商家搜索" />
          <button class="btn" :disabled="loading" @click="loadCashierOrders">筛选</button>
        </div>
        <p class="tip">共 {{ total }} 条收银单</p>
        <ul class="list">
          <li
            v-for="item in records"
            :key="item.cashierOrderId"
            :class="{ active: item.cashierOrderId === selectedCashierOrderId }"
            @click="chooseCashierOrder(item.cashierOrderId)"
          >
            <div class="line-1">
              <strong>{{ item.cashierOrderId }}</strong>
              <span>{{ item.payStatusText }}</span>
            </div>
            <p>订单号：{{ item.orderNo }}</p>
            <p>供应方：{{ item.supplierName }}</p>
            <p>应付：¥{{ item.payableAmount }} ｜ 已付：¥{{ item.paidAmount }}</p>
            <p>待付：¥{{ item.outstandingAmount }}</p>
          </li>
        </ul>
      </article>

      <article class="card right">
        <div class="head">
          <h2>收银单详情</h2>
          <button class="btn" :disabled="detailLoading || !selectedCashierOrderId" @click="loadCashierDetail">
            {{ detailLoading ? '加载中...' : '刷新详情' }}
          </button>
        </div>
        <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
        <p v-if="successMsg" class="ok">{{ successMsg }}</p>

        <div v-if="detail" class="detail">
          <div class="meta-grid">
            <p>收银单号：{{ detail.cashierOrderId }}</p>
            <p>订单号：{{ detail.orderNo }}（{{ detail.orderId }}）</p>
            <p>询价号：{{ detail.inquiryNo }}</p>
            <p>买方公司：{{ detail.buyerCompany }}</p>
            <p>供应方：{{ detail.supplierName }}</p>
            <p>货品：{{ detail.goodsName }}</p>
            <p>支付类型：{{ detail.payTypeText }}</p>
            <p>支付状态：{{ detail.statusText }}（{{ detail.status }}）</p>
            <p>应付金额：¥{{ detail.payableAmount }}</p>
            <p>已付金额：¥{{ detail.paidAmount }}</p>
            <p>优惠金额：¥{{ detail.discountAmount }}</p>
            <p>服务费：¥{{ detail.serviceFeeAmount }}</p>
            <p>本次支付金额：¥{{ detail.finalPayAmount }}</p>
            <p>支付截止：{{ detail.dueAt || '-' }}</p>
            <p>支付时间：{{ detail.paidAt || '-' }}</p>
            <p>最新备注：{{ detail.latestRemark || '-' }}</p>
          </div>

          <section class="block">
            <h3>支付操作</h3>
            <div class="pay-form">
              <select v-model="payForm.payMethod">
                <option value="BANK_TRANSFER">对公转账</option>
                <option value="ALIPAY">支付宝</option>
                <option value="WECHAT">微信支付</option>
                <option value="UNIONPAY">银联</option>
              </select>
              <input v-model="payForm.payerName" placeholder="付款人（可选）" />
              <input v-model="payForm.remark" placeholder="支付备注（可选）" />
              <button class="btn btn--primary" :disabled="!canPay" @click="submitPay">
                {{ paySubmitting ? '提交中...' : detail.status === 'PAID' ? '已支付' : '确认支付' }}
              </button>
            </div>
          </section>

          <section class="block">
            <h3>支付时间线</h3>
            <ul class="timeline">
              <li v-for="node in detail.timeline" :key="`${node.nodeCode}-${node.happenedAt}`">
                <div class="line-1">
                  <strong>{{ node.nodeName }}</strong>
                  <span>{{ node.statusText }}</span>
                </div>
                <p>状态：{{ node.status }} ｜ 处理人：{{ node.handler || '-' }}</p>
                <p>时间：{{ node.happenedAt || '-' }}</p>
                <p>备注：{{ node.remark || '-' }}</p>
              </li>
            </ul>
          </section>
        </div>
        <p v-else class="tip">请选择左侧收银单查看详情</p>
      </article>
    </section>
  </main>
</template>

<style scoped>
.n10-page {
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
.detail {
  display: grid;
  gap: 12px;
}
.meta-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 6px 10px;
}
.block {
  border: 1px solid #f3f4f6;
  border-radius: 10px;
  padding: 12px;
}
.block h3 {
  margin: 0 0 8px;
}
.pay-form {
  display: grid;
  grid-template-columns: 180px 1fr 1fr auto;
  gap: 8px;
}
.timeline {
  list-style: none;
  margin: 0;
  padding: 0;
  display: grid;
  gap: 8px;
}
.timeline li {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 8px;
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
select {
  height: 34px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  padding: 0 10px;
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
  .meta-grid,
  .pay-form {
    grid-template-columns: 1fr;
  }
}
</style>
