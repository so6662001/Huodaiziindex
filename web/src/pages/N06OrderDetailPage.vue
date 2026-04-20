<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const loading = ref(false)
const detailLoading = ref(false)
const actionSubmitting = ref(false)
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

const actionCode = ref('')
const actionRemark = ref('')

const token = computed(() => localStorage.getItem('N01_AUTH_TOKEN') || '')
const hasSession = computed(() => token.value.length > 0)
const canSubmitAction = computed(() => hasSession.value && selectedOrderId.value && actionCode.value && !actionSubmitting.value)

async function loadOrders() {
  if (!hasSession.value || loading.value) {
    if (!hasSession.value) {
      errorMsg.value = '请先登录后查看订单详情'
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
      await loadOrderDetail()
    } else if (selectedOrderId.value) {
      const exists = records.value.some((item) => item.orderId === selectedOrderId.value)
      if (!exists && records.value.length > 0) {
        selectedOrderId.value = records.value[0].orderId
        await loadOrderDetail()
      }
    }
  } catch (error) {
    errorMsg.value = error.message || '订单列表加载失败'
  } finally {
    loading.value = false
  }
}

async function loadOrderDetail() {
  if (!hasSession.value || !selectedOrderId.value || detailLoading.value) return
  detailLoading.value = true
  errorMsg.value = ''
  try {
    const resp = await fetch(`/api/v1/auth/orders/${selectedOrderId.value}`, {
      headers: { 'X-Auth-Token': token.value }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `订单详情加载失败(${resp.status})`)
    }
    detail.value = json.data || null
    if (detail.value?.actions?.length) {
      actionCode.value = detail.value.actions[0].actionCode || ''
    } else {
      actionCode.value = ''
    }
  } catch (error) {
    errorMsg.value = error.message || '订单详情加载失败'
  } finally {
    detailLoading.value = false
  }
}

async function submitAction() {
  if (!canSubmitAction.value) return
  actionSubmitting.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      action: actionCode.value,
      remark: actionRemark.value.trim() || null,
      operator: 'pc-n06-ui'
    }
    const resp = await fetch(`/api/v1/auth/orders/${selectedOrderId.value}/action`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Auth-Token': token.value
      },
      body: JSON.stringify(payload)
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `订单操作失败(${resp.status})`)
    }
    detail.value = json.data || null
    successMsg.value = '订单状态更新成功'
    actionRemark.value = ''
    if (detail.value?.actions?.length) {
      actionCode.value = detail.value.actions[0].actionCode || ''
    } else {
      actionCode.value = ''
    }
    await loadOrders()
  } catch (error) {
    errorMsg.value = error.message || '订单操作失败'
  } finally {
    actionSubmitting.value = false
  }
}

function chooseOrder(orderId) {
  selectedOrderId.value = orderId
  loadOrderDetail()
}

function goNegotiation() {
  router.push('/account/negotiation-session')
}

function goOnboarding() {
  router.push('/account/onboarding-progress')
}

function goHome() {
  router.push('/')
}

onMounted(() => {
  loadOrders()
})
</script>

<template>
  <main class="n06-page">
    <section class="card hero">
      <h1>PC-N06 订单详情页</h1>
      <p>查看成交订单履约状态、对账回款信息与关键时间线，支持订单状态推进。</p>
      <div class="hero-actions">
        <button class="btn" @click="goNegotiation">返回议价会话</button>
        <button class="btn" @click="goOnboarding">返回入驻进度</button>
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
            <p>成交额：¥{{ item.dealAmount }}</p>
          </li>
        </ul>
      </article>

      <article class="card right">
        <div class="head">
          <h2>订单详情</h2>
          <button class="btn" :disabled="detailLoading || !selectedOrderId" @click="loadOrderDetail">
            {{ detailLoading ? '加载中...' : '刷新详情' }}
          </button>
        </div>
        <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
        <p v-if="successMsg" class="ok">{{ successMsg }}</p>

        <div v-if="detail" class="detail">
          <div class="grid">
            <p>订单号：{{ detail.orderNo }}</p>
            <p>询价单号：{{ detail.inquiryNo }}</p>
            <p>状态：{{ detail.orderStatusText }}（{{ detail.orderStatus }}）</p>
            <p>供应商：{{ detail.supplierName }}</p>
            <p>买方公司：{{ detail.buyerCompany }}</p>
            <p>货品：{{ detail.goodsName }} / {{ detail.specText }}</p>
            <p>数量：{{ detail.quantityTon }}</p>
            <p>单价：¥{{ detail.unitPrice }}</p>
            <p>总价：¥{{ detail.totalAmount }}</p>
            <p>回款状态：{{ detail.reconcileStatusText }}</p>
            <p>已回款：¥{{ detail.paidAmount }} ｜ 待回款：¥{{ detail.outstandingAmount }}</p>
            <p>最新备注：{{ detail.latestRemark || '-' }}</p>
          </div>

          <section class="block">
            <h3>履约时间线</h3>
            <ul class="timeline">
              <li v-for="node in detail.timeline" :key="node.nodeCode">
                <div class="line-1">
                  <strong>{{ node.nodeName }}</strong>
                  <span>{{ node.statusText }}</span>
                </div>
                <p>状态：{{ node.status }} ｜ 责任方：{{ node.owner }}</p>
                <p>时间：{{ node.happenedAt || '-' }}</p>
                <p>备注：{{ node.remark || '-' }}</p>
              </li>
            </ul>
          </section>

          <section class="block">
            <h3>订单操作</h3>
            <div class="action-row">
              <select v-model="actionCode">
                <option v-for="action in detail.actions" :key="action.actionCode" :value="action.actionCode">
                  {{ action.actionName }}
                </option>
              </select>
              <input v-model="actionRemark" placeholder="操作备注（可选）" />
              <button class="btn btn--primary" :disabled="!canSubmitAction" @click="submitAction">
                {{ actionSubmitting ? '提交中...' : '提交操作' }}
              </button>
            </div>
          </section>
        </div>
        <p v-else class="tip">请选择左侧订单查看详情</p>
      </article>
    </section>
  </main>
</template>

<style scoped>
.n06-page {
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
.grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 8px 14px;
}
.grid p {
  margin: 0;
}
.block {
  margin-top: 12px;
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
  border-radius: 10px;
  padding: 10px;
}
.timeline li p {
  margin: 4px 0 0;
}
.action-row {
  display: grid;
  grid-template-columns: 180px 1fr auto;
  gap: 8px;
}
.btn {
  border: 1px solid #ddd;
  border-radius: 8px;
  background: #fff;
  padding: 8px 12px;
  cursor: pointer;
}
.btn:disabled {
  cursor: not-allowed;
  opacity: 0.6;
}
.btn--primary {
  border-color: #f57c00;
  background: #f57c00;
  color: #fff;
}
select,
input {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 8px 10px;
  font: inherit;
}
.error {
  color: #b42318;
}
.ok {
  color: #0f766e;
}
.tip {
  color: #6b7280;
}
</style>
