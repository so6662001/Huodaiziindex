<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const creating = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

const query = reactive({
  merchantId: 'S001'
})

const overview = reactive({
  merchantId: '',
  currentPlanName: '',
  currentPlanCode: '',
  currentStatus: '',
  currentStatusText: '',
  startAt: '',
  expireAt: '',
  autoRenew: '',
  amountYuan: '',
  activeCount: 0,
  expiringSoonCount: 0,
  expiredCount: 0,
  tipText: ''
})

const plans = reactive({
  items: [],
  recommendPlanCode: ''
})

const mine = reactive({
  items: []
})

const createForm = reactive({
  planCode: '',
  billingCycle: 'MONTHLY',
  operator: ''
})

const canLoad = computed(() => query.merchantId.trim())
const canCreate = computed(
  () => query.merchantId.trim() && createForm.planCode.trim() && createForm.billingCycle.trim()
)

const cycleTextMap = {
  MONTHLY: '月付',
  YEARLY: '年付'
}

const statusTextMap = {
  ACTIVE: '生效中',
  EXPIRING_SOON: '临近到期',
  EXPIRED: '已到期'
}

async function loadData() {
  if (!canLoad.value || loading.value) return
  loading.value = true
  errorMsg.value = ''
  try {
    const params = new URLSearchParams()
    params.set('merchantId', query.merchantId.trim())
    const [overviewResp, plansResp, mineResp] = await Promise.all([
      fetch(`/api/v1/inquiries/h5/member/overview?${params.toString()}`),
      fetch(`/api/v1/inquiries/h5/member/plans?${params.toString()}`),
      fetch(`/api/v1/inquiries/h5/member/mine?${params.toString()}`)
    ])
    const overviewJson = await overviewResp.json()
    const plansJson = await plansResp.json()
    const mineJson = await mineResp.json()
    if (overviewJson.code !== '0') throw new Error(overviewJson.message || '加载会员概览失败')
    if (plansJson.code !== '0') throw new Error(plansJson.message || '加载会员套餐失败')
    if (mineJson.code !== '0') throw new Error(mineJson.message || '加载会员列表失败')

    const o = overviewJson.data || {}
    overview.merchantId = o.merchantId || query.merchantId.trim()
    overview.currentPlanName = o.currentPlanName || ''
    overview.currentPlanCode = o.currentPlanCode || ''
    overview.currentStatus = o.currentStatus || ''
    overview.currentStatusText = o.currentStatusText || ''
    overview.startAt = o.startAt || ''
    overview.expireAt = o.expireAt || ''
    overview.autoRenew = o.autoRenew || ''
    overview.amountYuan = o.amountYuan || ''
    overview.activeCount = Number(o.activeCount || 0)
    overview.expiringSoonCount = Number(o.expiringSoonCount || 0)
    overview.expiredCount = Number(o.expiredCount || 0)
    overview.tipText = o.tipText || ''

    const p = plansJson.data || {}
    plans.items = Array.isArray(p.plans) ? p.plans : []
    plans.recommendPlanCode = p.recommendPlanCode || ''

    const m = mineJson.data || {}
    mine.items = Array.isArray(m.items) ? m.items : []

    if (!createForm.planCode) {
      createForm.planCode = plans.recommendPlanCode || plans.items[0]?.planCode || ''
      createForm.billingCycle = plans.items[0]?.billingCycle || 'MONTHLY'
    }
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    loading.value = false
  }
}

async function openMember() {
  if (!canCreate.value || creating.value) return
  creating.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      merchantId: query.merchantId.trim(),
      planCode: createForm.planCode.trim(),
      billingCycle: createForm.billingCycle.trim(),
      operator: createForm.operator.trim() || 'H5运营'
    }
    const resp = await fetch('/api/v1/inquiries/h5/member/open', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    })
    const json = await resp.json()
    if (json.code !== '0') throw new Error(json.message || '开通会员失败')
    successMsg.value = json.data?.message || '会员开通成功'
    await loadData()
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    creating.value = false
  }
}

function choosePlan(item) {
  if (!item) return
  createForm.planCode = item.planCode || ''
  createForm.billingCycle = item.billingCycle || 'MONTHLY'
}

function goBilling() {
  router.push(`/merchant/billing?merchantId=${encodeURIComponent(query.merchantId.trim() || 'S001')}`)
}

onMounted(() => {
  const merchantId = String(route.query.merchantId || '').trim()
  if (merchantId) query.merchantId = merchantId
  loadData()
})
</script>

<template>
  <main class="h5-member-page">
    <section class="card hero">
      <h1>H10 H5我的会员</h1>
      <p>移动端查看当前会员权益、开通新套餐并追踪到期状态。</p>
    </section>

    <section class="card">
      <div class="actions">
        <label>
          商家ID
          <input v-model="query.merchantId" placeholder="如 S001" />
        </label>
        <button class="btn" :disabled="!canLoad || loading" @click="loadData">
          {{ loading ? '加载中...' : '刷新会员数据' }}
        </button>
        <button class="btn" @click="goBilling">去P12账单页</button>
      </div>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
      <p v-if="successMsg" class="success">{{ successMsg }}</p>
    </section>

    <section class="card">
      <h2>会员概览</h2>
      <p>当前套餐：{{ overview.currentPlanName || '-' }}（{{ overview.currentPlanCode || '-' }}）</p>
      <p>状态：{{ overview.currentStatusText || '-' }} ｜ 自动续费：{{ overview.autoRenew || '-' }}</p>
      <p>周期：{{ overview.startAt || '-' }} ~ {{ overview.expireAt || '-' }}</p>
      <p>金额：￥{{ overview.amountYuan || '-' }}</p>
      <p class="tip">{{ overview.tipText || '建议优先续费，避免功能中断。' }}</p>
      <div class="stat-grid">
        <span>生效中 {{ overview.activeCount }}</span>
        <span>临近到期 {{ overview.expiringSoonCount }}</span>
        <span>已到期 {{ overview.expiredCount }}</span>
      </div>
    </section>

    <section class="card">
      <h2>可选套餐</h2>
      <ul class="plan-list">
        <li v-for="item in plans.items" :key="item.planCode" class="plan-item">
          <div class="head">
            <strong>{{ item.planName }}</strong>
            <span class="tag">{{ item.planCode }}</span>
          </div>
          <p>说明：{{ item.planDesc }}</p>
          <p>价格：￥{{ item.discountPrice }} / {{ cycleTextMap[item.billingCycle] || item.billingCycle }}</p>
          <p>适用：{{ item.suitableFor }}</p>
          <button class="btn" @click="choosePlan(item)">选择套餐</button>
        </li>
      </ul>
    </section>

    <section class="card">
      <h2>开通会员</h2>
      <div class="grid">
        <label>
          套餐
          <select v-model="createForm.planCode">
            <option value="">请选择套餐</option>
            <option v-for="item in plans.items" :key="item.planCode" :value="item.planCode">
              {{ item.planName }}
            </option>
          </select>
        </label>
        <label>
          计费周期
          <select v-model="createForm.billingCycle">
            <option value="MONTHLY">月付</option>
            <option value="YEARLY">年付</option>
          </select>
        </label>
        <label>
          操作人
          <input v-model="createForm.operator" placeholder="可选" />
        </label>
      </div>
      <div class="actions">
        <button class="btn btn--primary" :disabled="!canCreate || creating" @click="openMember">
          {{ creating ? '开通中...' : '立即开通会员' }}
        </button>
      </div>
    </section>

    <section class="card">
      <h2>我的会员记录</h2>
      <ul class="mine-list">
        <li v-for="item in mine.items" :key="item.subscriptionId" class="mine-item">
          <div class="head">
            <strong>{{ item.planName }}</strong>
            <span class="status">{{ statusTextMap[item.status] || item.status }}</span>
          </div>
          <p>订阅号：{{ item.subscriptionId }} ｜ 套餐编码：{{ item.planCode }}</p>
          <p>周期：{{ item.startAt }} ~ {{ item.expireAt }}</p>
          <p>金额：￥{{ item.amountYuan }} ｜ 自动续费：{{ item.autoRenew }}</p>
        </li>
      </ul>
      <p v-if="mine.items.length === 0" class="tip">暂无会员记录</p>
    </section>
  </main>
</template>

<style scoped>
.h5-member-page {
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
.grid {
  display: grid;
  gap: 10px;
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
.stat-grid {
  margin-top: 10px;
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 8px;
  color: #4b5563;
}
.plan-list,
.mine-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: grid;
  gap: 10px;
}
.plan-item,
.mine-item {
  border: 1px solid #f2f4f7;
  border-radius: 10px;
  padding: 10px;
}
.head {
  display: flex;
  justify-content: space-between;
  gap: 10px;
}
.tag {
  font-size: 12px;
  color: #92400e;
  background: #fff7ed;
  border-radius: 999px;
  padding: 2px 8px;
}
.status {
  font-size: 12px;
  color: #0c7a43;
  background: #ecfdf3;
  border-radius: 999px;
  padding: 2px 8px;
}
.plan-item p,
.mine-item p {
  margin: 6px 0 0;
  color: #4b5563;
}
</style>
