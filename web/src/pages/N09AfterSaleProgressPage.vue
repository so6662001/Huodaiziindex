<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const loading = ref(false)
const detailLoading = ref(false)
const errorMsg = ref('')

const records = ref([])
const selectedDisputeId = ref('')
const detail = ref(null)

const statusFilter = ref('')
const keyword = ref('')
const pageNo = ref(1)
const pageSize = ref(10)
const total = ref(0)

const token = computed(() => localStorage.getItem('N01_AUTH_TOKEN') || '')
const hasSession = computed(() => token.value.length > 0)

async function loadProgressList() {
  if (!hasSession.value || loading.value) {
    if (!hasSession.value) {
      errorMsg.value = '请先登录后查看售后处理进度'
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

    const resp = await fetch(`/api/v1/auth/after-sales/progress?${params.toString()}`, {
      headers: { 'X-Auth-Token': token.value }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `售后进度列表加载失败(${resp.status})`)
    }
    const data = json.data || {}
    records.value = data.records || []
    total.value = data.total || 0
    if (!selectedDisputeId.value && records.value.length > 0) {
      selectedDisputeId.value = records.value[0].disputeId
      await loadProgressDetail()
    } else if (selectedDisputeId.value) {
      const exists = records.value.some((item) => item.disputeId === selectedDisputeId.value)
      if (!exists && records.value.length > 0) {
        selectedDisputeId.value = records.value[0].disputeId
        await loadProgressDetail()
      }
    }
  } catch (error) {
    errorMsg.value = error.message || '售后进度列表加载失败'
  } finally {
    loading.value = false
  }
}

async function loadProgressDetail() {
  if (!hasSession.value || !selectedDisputeId.value || detailLoading.value) return
  detailLoading.value = true
  errorMsg.value = ''
  try {
    const resp = await fetch(`/api/v1/auth/after-sales/progress/${selectedDisputeId.value}`, {
      headers: { 'X-Auth-Token': token.value }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `售后进度详情加载失败(${resp.status})`)
    }
    detail.value = json.data || null
  } catch (error) {
    errorMsg.value = error.message || '售后进度详情加载失败'
  } finally {
    detailLoading.value = false
  }
}

function chooseDispute(disputeId) {
  selectedDisputeId.value = disputeId
  loadProgressDetail()
}

function goAfterSaleDispute() {
  router.push('/account/after-sale-dispute')
}

function goCashier() {
  router.push('/account/cashier')
}

function goTradeTerms() {
  router.push('/account/trade-terms-confirm')
}

function goHome() {
  router.push('/')
}

onMounted(() => {
  loadProgressList()
})
</script>

<template>
  <main class="n09-page">
    <section class="card hero">
      <h1>PC-N09 售后处理进度页</h1>
      <p>查看售后争议从提交、处理、方案达成到结案关闭的全链路处理进度。</p>
      <div class="hero-actions">
        <button class="btn" @click="goAfterSaleDispute">返回售后争议发起页</button>
        <button class="btn" @click="goCashier">前往收银台</button>
        <button class="btn" @click="goTradeTerms">返回交易条款确认页</button>
        <button class="btn" @click="goHome">返回首页</button>
      </div>
    </section>

    <section class="layout">
      <article class="card left">
        <div class="head">
          <h2>进度单列表</h2>
          <button class="btn" :disabled="loading" @click="loadProgressList">{{ loading ? '刷新中...' : '刷新' }}</button>
        </div>
        <div class="filters">
          <select v-model="statusFilter">
            <option value="">全部状态</option>
            <option value="SUBMITTED">已提交</option>
            <option value="PROCESSING">处理中</option>
            <option value="RESOLVED">已解决</option>
            <option value="CLOSED">已关闭</option>
          </select>
          <input v-model="keyword" placeholder="按争议号/订单号/商家搜索" />
          <button class="btn" :disabled="loading" @click="loadProgressList">筛选</button>
        </div>
        <p class="tip">共 {{ total }} 条进度单</p>
        <ul class="list">
          <li
            v-for="item in records"
            :key="item.disputeId"
            :class="{ active: item.disputeId === selectedDisputeId }"
            @click="chooseDispute(item.disputeId)"
          >
            <div class="line-1">
              <strong>{{ item.disputeId }}</strong>
              <span>{{ item.currentStatusText }}</span>
            </div>
            <p>订单：{{ item.orderNo }}</p>
            <p>阶段：{{ item.currentStage }} ｜ 问题：{{ item.issueTypeText }}</p>
            <p>摘要：{{ item.issueSummary }}</p>
            <p>备注：{{ item.latestRemark || '-' }}</p>
          </li>
        </ul>
      </article>

      <article class="card right">
        <div class="head">
          <h2>处理进度详情</h2>
          <button class="btn" :disabled="detailLoading || !selectedDisputeId" @click="loadProgressDetail">
            {{ detailLoading ? '加载中...' : '刷新详情' }}
          </button>
        </div>
        <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
        <div v-if="detail" class="detail">
          <div class="meta-grid">
            <p>争议单号：{{ detail.disputeId }}</p>
            <p>订单号：{{ detail.orderNo }}（{{ detail.orderId }}）</p>
            <p>询价号：{{ detail.inquiryNo }}</p>
            <p>买方：{{ detail.buyerCompany }}</p>
            <p>供应方：{{ detail.supplierName }}</p>
            <p>问题类型：{{ detail.issueTypeText }}</p>
            <p>当前状态：{{ detail.currentStatusText }}（{{ detail.currentStatus }}）</p>
            <p>当前阶段：{{ detail.currentStage }}</p>
            <p>进度：{{ detail.progressPercent }}%</p>
            <p>最新备注：{{ detail.latestRemark || '-' }}</p>
            <p>创建时间：{{ detail.createdAt || '-' }}</p>
            <p>更新时间：{{ detail.updatedAt || '-' }}</p>
          </div>
          <p class="desc">问题摘要：{{ detail.issueSummary }}</p>
          <p class="desc">问题描述：{{ detail.issueDescription }}</p>

          <section class="block">
            <h3>处理时间线</h3>
            <ul class="timeline">
              <li v-for="node in detail.nodes" :key="node.nodeCode">
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
        <p v-else class="tip">请选择左侧进度单查看处理详情</p>
      </article>
    </section>
  </main>
</template>

<style scoped>
.n09-page {
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
.desc {
  margin: 0;
}
.block {
  border: 1px solid #f3f4f6;
  border-radius: 10px;
  padding: 12px;
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
@media (max-width: 980px) {
  .layout {
    grid-template-columns: 1fr;
  }
  .meta-grid {
    grid-template-columns: 1fr;
  }
}
</style>
