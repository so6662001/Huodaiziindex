<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const loading = ref(false)
const confirming = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

const records = ref([])
const selectedOrderId = ref('')
const detail = ref(null)

const statusFilter = ref('')
const keyword = ref('')
const pageNo = ref(1)
const pageSize = ref(10)
const total = ref(0)

const confirmRemark = ref('')
const token = computed(() => localStorage.getItem('N01_AUTH_TOKEN') || '')
const hasSession = computed(() => token.value.length > 0)
const canConfirm = computed(
  () => hasSession.value && selectedOrderId.value && detail.value && !detail.value.confirmed && !confirming.value
)

async function loadOrders() {
  if (!hasSession.value || loading.value) {
    if (!hasSession.value) {
      errorMsg.value = '请先登录后查看交易条款'
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
    const resp = await fetch(`/api/v1/auth/orders?${params.toString()}`, {
      headers: { 'X-Auth-Token': token.value }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `订单列表加载失败(${resp.status})`)
    }
    const data = json.data || {}
    records.value = data.records || []
    total.value = data.total || 0
    if (!selectedOrderId.value && records.value.length > 0) {
      selectedOrderId.value = records.value[0].orderId
      await loadTradeTerms()
    }
  } catch (error) {
    errorMsg.value = error.message || '订单列表加载失败'
  } finally {
    loading.value = false
  }
}

async function loadTradeTerms() {
  if (!hasSession.value || !selectedOrderId.value) return
  loading.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const resp = await fetch(`/api/v1/auth/orders/${selectedOrderId.value}/trade-terms`, {
      headers: { 'X-Auth-Token': token.value }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `交易条款加载失败(${resp.status})`)
    }
    detail.value = json.data || null
  } catch (error) {
    errorMsg.value = error.message || '交易条款加载失败'
  } finally {
    loading.value = false
  }
}

async function confirmTradeTerms() {
  if (!canConfirm.value) return
  confirming.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      action: 'CONFIRM',
      remark: confirmRemark.value.trim() || null,
      operator: 'pc-n07-ui'
    }
    const resp = await fetch(`/api/v1/auth/orders/${selectedOrderId.value}/trade-terms/confirm`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Auth-Token': token.value
      },
      body: JSON.stringify(payload)
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `交易条款确认失败(${resp.status})`)
    }
    detail.value = json.data || null
    successMsg.value = '交易条款已确认生效'
    confirmRemark.value = ''
  } catch (error) {
    errorMsg.value = error.message || '交易条款确认失败'
  } finally {
    confirming.value = false
  }
}

function chooseOrder(orderId) {
  selectedOrderId.value = orderId
  loadTradeTerms()
}

function goOrderDetail() {
  router.push('/account/order-detail')
}

function goNegotiation() {
  router.push('/account/negotiation-session')
}

function goAfterSale() {
  router.push('/account/after-sale-dispute')
}

function goHome() {
  router.push('/')
}

onMounted(() => {
  loadOrders()
})
</script>

<template>
  <main class="n07-page">
    <section class="card hero">
      <h1>PC-N07 交易条款确认页</h1>
      <p>核对成交订单的交付、结算、发票、质量、违约和争议条款后，完成条款确认生效。</p>
      <div class="hero-actions">
        <button class="btn" @click="goOrderDetail">返回订单详情</button>
        <button class="btn" @click="goNegotiation">返回议价会话</button>
        <button class="btn" @click="goAfterSale">售后/争议发起页</button>
        <button class="btn" @click="goHome">返回首页</button>
      </div>
    </section>

    <section class="layout">
      <article class="card left">
        <div class="head">
          <h2>订单列表</h2>
          <button class="btn" :disabled="loading" @click="loadOrders">{{ loading ? '刷新中...' : '刷新' }}</button>
        </div>
        <div class="filters">
          <select v-model="statusFilter">
            <option value="">全部状态</option>
            <option value="PENDING_SIGN">待签署</option>
            <option value="SIGNED">已签署</option>
            <option value="PICKUP_IN_PROGRESS">提货中</option>
            <option value="RECONCILING">对账中</option>
            <option value="COMPLETED">已完成</option>
            <option value="CANCELLED">已取消</option>
          </select>
          <input v-model="keyword" placeholder="按订单号/询价号/商家搜索" />
          <button class="btn" :disabled="loading" @click="loadOrders">筛选</button>
        </div>
        <p class="tip">共 {{ total }} 条订单</p>
        <ul class="order-list">
          <li
            v-for="item in records"
            :key="item.orderId"
            :class="{ active: item.orderId === selectedOrderId }"
            @click="chooseOrder(item.orderId)"
          >
            <div class="line-1">
              <strong>{{ item.orderNo }}</strong>
              <span>{{ item.orderStatusText }}</span>
            </div>
            <p>{{ item.goodsName }}</p>
            <p>{{ item.quantityText }} ｜ 商家：{{ item.supplierName }}</p>
          </li>
        </ul>
      </article>

      <article class="card right">
        <div class="head">
          <h2>交易条款详情</h2>
          <button class="btn" :disabled="loading || !selectedOrderId" @click="loadTradeTerms">
            {{ loading ? '加载中...' : '刷新详情' }}
          </button>
        </div>
        <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
        <p v-if="successMsg" class="ok">{{ successMsg }}</p>

        <div v-if="detail" class="detail">
          <div class="grid">
            <p>订单号：{{ detail.orderNo }}</p>
            <p>询价号：{{ detail.inquiryNo }}</p>
            <p>买方：{{ detail.buyerCompany }}</p>
            <p>供应方：{{ detail.supplierName }}</p>
            <p>货品：{{ detail.goodsName }} / {{ detail.specText }}</p>
            <p>数量：{{ detail.quantityTon }}</p>
            <p>单价：¥{{ detail.unitPrice }} ｜ 总价：¥{{ detail.totalAmount }}</p>
            <p>生效期：{{ detail.effectiveDate }} 至 {{ detail.expireDate }}</p>
            <p>
              条款状态：
              <strong>{{ detail.confirmed ? '已确认生效' : '待确认' }}</strong>
            </p>
            <p>确认人：{{ detail.confirmedBy || '-' }}</p>
            <p>确认时间：{{ detail.confirmedAt || '-' }}</p>
            <p>确认备注：{{ detail.confirmRemark || '-' }}</p>
          </div>

          <section class="block">
            <h3>核心条款</h3>
            <ul class="terms">
              <li><strong>交付条款：</strong>{{ detail.deliveryTerm }}</li>
              <li><strong>结算条款：</strong>{{ detail.paymentTerm }}</li>
              <li><strong>发票条款：</strong>{{ detail.invoiceTerm }}</li>
              <li><strong>结算方式：</strong>{{ detail.settlementMethod }}</li>
              <li><strong>质量标准：</strong>{{ detail.qualityStandard }}</li>
              <li><strong>溢短装范围：</strong>{{ detail.toleranceRange }}</li>
              <li><strong>违约责任：</strong>{{ detail.breachLiability }}</li>
              <li><strong>争议解决：</strong>{{ detail.disputeResolution }}</li>
              <li><strong>其他约定：</strong>{{ detail.otherClause }}</li>
            </ul>
          </section>

          <section class="block">
            <h3>条款清单</h3>
            <ul class="list">
              <li v-for="item in detail.clauses" :key="item.clauseCode">
                <div class="line-1">
                  <strong>{{ item.clauseName }}</strong>
                  <span>{{ item.required ? '必选' : '可选' }}</span>
                </div>
                <p>编码：{{ item.clauseCode }}</p>
                <p>{{ item.clauseContent }}</p>
              </li>
            </ul>
          </section>

          <section class="block">
            <h3>附件</h3>
            <ul class="list">
              <li v-for="file in detail.attachments" :key="file.fileName">
                <div class="line-1">
                  <strong>{{ file.fileName }}</strong>
                  <span>{{ file.fileType }}</span>
                </div>
                <p>{{ file.fileUrl }}</p>
              </li>
            </ul>
          </section>

          <section class="block">
            <h3>确认操作</h3>
            <div class="action-row">
              <input v-model="confirmRemark" placeholder="确认备注（可选）" />
              <button class="btn btn--primary" :disabled="!canConfirm" @click="confirmTradeTerms">
                {{ confirming ? '提交中...' : detail.confirmed ? '已确认' : '确认条款生效' }}
              </button>
            </div>
          </section>
        </div>
        <p v-else class="tip">请从左侧选择订单查看交易条款</p>
      </article>
    </section>
  </main>
</template>

<style scoped>
.n07-page {
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
.order-list {
  list-style: none;
  margin: 10px 0 0;
  padding: 0;
  display: grid;
  gap: 8px;
}
.order-list li {
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  padding: 10px;
  cursor: pointer;
}
.order-list li.active {
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
.grid {
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
.terms,
.list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: grid;
  gap: 8px;
}
.list li {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 8px;
}
.action-row {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 8px;
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
  .grid {
    grid-template-columns: 1fr;
  }
}
</style>
