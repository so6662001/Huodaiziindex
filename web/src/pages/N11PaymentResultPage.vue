<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const errorMsg = ref('')
const detail = ref(null)

const token = computed(() => localStorage.getItem('N01_AUTH_TOKEN') || '')
const hasSession = computed(() => token.value.length > 0)
const cashierOrderId = computed(() => String(route.query.cashierOrderId || '').trim())

async function loadPaymentResult() {
  if (!hasSession.value || !cashierOrderId.value || loading.value) {
    if (!hasSession.value) {
      errorMsg.value = '请先登录后查看支付结果'
    } else if (!cashierOrderId.value) {
      errorMsg.value = '缺少收银单号，无法查询支付结果'
    }
    return
  }
  loading.value = true
  errorMsg.value = ''
  try {
    const resp = await fetch(`/api/v1/auth/cashier/orders/${cashierOrderId.value}/result`, {
      headers: { 'X-Auth-Token': token.value }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `支付结果加载失败(${resp.status})`)
    }
    detail.value = json.data || null
  } catch (error) {
    errorMsg.value = error.message || '支付结果加载失败'
  } finally {
    loading.value = false
  }
}

function goCashier() {
  router.push('/account/cashier')
}

function goOrderDetail() {
  router.push('/account/order-detail')
}

function goHome() {
  router.push('/')
}

onMounted(() => {
  loadPaymentResult()
})
</script>

<template>
  <main class="n11-page">
    <section class="card hero">
      <h1>PC-N11 支付结果页</h1>
      <p>展示收银支付结果、支付流水与订单联动状态，支持一键返回收银台或订单详情。</p>
      <div class="hero-actions">
        <button class="btn" @click="goCashier">返回收银台</button>
        <button class="btn" @click="goOrderDetail">查看订单详情</button>
        <button class="btn" @click="goHome">返回首页</button>
      </div>
    </section>

    <section class="card body">
      <div class="head">
        <h2>支付结果</h2>
        <button class="btn" :disabled="loading" @click="loadPaymentResult">{{ loading ? '刷新中...' : '刷新' }}</button>
      </div>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>

      <div v-if="detail" class="detail">
        <div class="result-banner" :class="detail.resultStatus === 'SUCCESS' ? 'ok' : 'waiting'">
          <strong>{{ detail.resultStatusText }}</strong>
          <span>订单 {{ detail.orderNo }} ｜ 收银单 {{ detail.cashierOrderId }}</span>
        </div>

        <div class="meta-grid">
          <p>支付结果：{{ detail.resultStatusText }}（{{ detail.resultStatus }}）</p>
          <p>支付状态：{{ detail.payStatusText }}（{{ detail.payStatus }}）</p>
          <p>支付渠道：{{ detail.payChannelText || '-' }}</p>
          <p>支付时间：{{ detail.paidAt || '-' }}</p>
          <p>收银单号：{{ detail.cashierOrderId }}</p>
          <p>订单号：{{ detail.orderNo }}（{{ detail.orderId }}）</p>
          <p>询价号：{{ detail.inquiryNo }}</p>
          <p>货品：{{ detail.goodsName }}</p>
          <p>买方：{{ detail.buyerCompany }}</p>
          <p>供应方：{{ detail.supplierName }}</p>
          <p>应付金额：¥{{ detail.payableAmount }}</p>
          <p>实付金额：¥{{ detail.paidAmount }}</p>
          <p>订单联动状态：{{ detail.linkedOrderStatusText }}（{{ detail.linkedOrderStatus }}）</p>
          <p>联动备注：{{ detail.orderSyncRemark || '-' }}</p>
          <p>返回建议：{{ detail.nextActionHint }}</p>
          <p>更新时间：{{ detail.updatedAt || '-' }}</p>
        </div>

        <section class="block">
          <h3>支付事件时间线</h3>
          <ul class="timeline">
            <li v-for="node in detail.nodes" :key="`${node.nodeCode}-${node.happenedAt}`">
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
      <p v-else class="tip">暂无支付结果数据，请检查收银单号后重试</p>
    </section>
  </main>
</template>

<style scoped>
.n11-page {
  max-width: 1080px;
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
.head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.detail {
  margin-top: 10px;
  display: grid;
  gap: 12px;
}
.result-banner {
  border-radius: 10px;
  padding: 12px;
  display: grid;
  gap: 4px;
}
.result-banner.ok {
  background: #ecfdf3;
  border: 1px solid #86efac;
  color: #166534;
}
.result-banner.waiting {
  background: #fff7ed;
  border: 1px solid #fdba74;
  color: #9a3412;
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
.line-1 {
  display: flex;
  justify-content: space-between;
  align-items: center;
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
.tip {
  color: #6b7280;
}
.error {
  color: #b91c1c;
}
@media (max-width: 980px) {
  .meta-grid {
    grid-template-columns: 1fr;
  }
}
</style>
