<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const loadingPlans = ref(false)
const loadingMine = ref(false)
const submitting = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

const filters = reactive({
  merchantId: ''
})

const state = reactive({
  plans: [],
  recommendPlanCode: '',
  mine: []
})

const createForm = reactive({
  planCode: '',
  billingCycle: 'MONTHLY',
  operator: ''
})

const canQuery = computed(() => filters.merchantId.trim().length > 0)
const canSubmit = computed(
  () =>
    createForm.planCode.trim().length > 0 &&
    createForm.billingCycle.trim().length > 0 &&
    filters.merchantId.trim().length > 0
)

const cycleTextMap = {
  MONTHLY: '月付',
  QUARTERLY: '季付',
  YEARLY: '年付'
}

const statusTextMap = {
  ACTIVE: '生效中',
  EXPIRING_SOON: '临近到期',
  EXPIRED: '已到期'
}

async function loadPlans() {
  if (!canQuery.value || loadingPlans.value) return
  loadingPlans.value = true
  errorMsg.value = ''
  try {
    const params = new URLSearchParams()
    params.set('merchantId', filters.merchantId.trim())
    const resp = await fetch(`/api/v1/inquiries/merchant/subscription/plans?${params.toString()}`)
    const json = await resp.json()
    if (json.code !== '0') {
      throw new Error(json.message || '套餐加载失败')
    }
    const data = json.data || {}
    state.plans = Array.isArray(data.plans) ? data.plans : []
    state.recommendPlanCode = data.recommendPlanCode || ''
    if (!createForm.planCode && state.plans.length > 0) {
      createForm.planCode = state.recommendPlanCode || state.plans[0].planCode || ''
      createForm.billingCycle = state.plans[0].billingCycle || 'MONTHLY'
    }
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    loadingPlans.value = false
  }
}

async function loadMine() {
  if (!canQuery.value || loadingMine.value) return
  loadingMine.value = true
  errorMsg.value = ''
  try {
    const params = new URLSearchParams()
    params.set('merchantId', filters.merchantId.trim())
    const resp = await fetch(`/api/v1/inquiries/merchant/subscription/mine?${params.toString()}`)
    const json = await resp.json()
    if (json.code !== '0') {
      throw new Error(json.message || '订阅列表加载失败')
    }
    const data = json.data || {}
    state.mine = Array.isArray(data.items) ? data.items : []
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    loadingMine.value = false
  }
}

async function createSubscription() {
  if (!canSubmit.value || submitting.value) return
  submitting.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      merchantId: filters.merchantId.trim(),
      planCode: createForm.planCode.trim(),
      billingCycle: createForm.billingCycle,
      operator: createForm.operator || '运营同学'
    }
    const resp = await fetch('/api/v1/inquiries/merchant/subscription', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    })
    const json = await resp.json()
    if (json.code !== '0') {
      throw new Error(json.message || '开通订阅失败')
    }
    const data = json.data || {}
    successMsg.value = `开通成功：${data.planName || ''}（订阅号 ${data.subscriptionNo || ''}）`
    await loadMine()
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    submitting.value = false
  }
}

function choosePlan(planCode, defaultCycle) {
  createForm.planCode = planCode
  if (defaultCycle) {
    createForm.billingCycle = defaultCycle
  }
}

async function refreshAll() {
  await Promise.all([loadPlans(), loadMine()])
}

onMounted(() => {
  const merchantId = String(route.query.merchantId || '').trim()
  filters.merchantId = merchantId || 'S001'
  refreshAll()
})
</script>

<template>
  <main class="subscription-page">
    <section class="card">
      <h1>P11 套餐与订阅页（商家）</h1>
      <p class="desc">选择 SaaS 套餐并完成开通，管理“我的订阅”与续费周期。</p>
      <div class="filters">
        <label>
          商家ID
          <input v-model="filters.merchantId" placeholder="如 S001" />
        </label>
        <button class="btn primary" :disabled="!canQuery || loadingPlans || loadingMine" @click="refreshAll">
          刷新
        </button>
      </div>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
      <p v-if="successMsg" class="success">{{ successMsg }}</p>
    </section>

    <section class="card">
      <div class="section-head">
        <h2>套餐中心</h2>
        <span class="muted">{{ loadingPlans ? '加载中...' : `共 ${state.plans.length} 个套餐` }}</span>
      </div>
      <div class="plan-grid">
        <article
          v-for="plan in state.plans"
          :key="plan.planCode"
          class="plan-card"
          :class="{ highlight: state.recommendPlanCode === plan.planCode }"
        >
          <header>
            <h3>{{ plan.planName }}</h3>
            <span class="tag">{{ plan.planCode }}</span>
          </header>
          <p class="price">￥{{ plan.discountPrice }}/{{ cycleTextMap[plan.billingCycle] || plan.billingCycle }}</p>
          <p class="muted">原价：￥{{ plan.originPrice }} ｜ 适用：{{ plan.suitableFor }}</p>
          <p class="muted">{{ plan.planDesc }}</p>
          <ul class="feature-list">
            <li v-for="f in plan.features" :key="`${plan.planCode}-${f.code}`">
              <strong>{{ f.name }}</strong>：{{ f.value }}
            </li>
          </ul>
          <div class="plan-actions">
            <button
              class="btn"
              :class="{ primary: createForm.planCode === plan.planCode }"
              @click="choosePlan(plan.planCode, plan.billingCycle)"
            >
              {{ createForm.planCode === plan.planCode ? '已选中' : '选择套餐' }}
            </button>
            <span class="muted">{{ plan.recommended ? '推荐套餐' : '可选套餐' }}</span>
          </div>
        </article>
      </div>
    </section>

    <section class="card">
      <h2>开通订阅</h2>
      <div class="subscribe-form">
        <label>
          套餐
          <select v-model="createForm.planCode">
            <option value="">请选择套餐</option>
            <option v-for="plan in state.plans" :key="plan.planCode" :value="plan.planCode">
              {{ plan.planName }}
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
          <input v-model="createForm.operator" placeholder="如 王运营" />
        </label>
        <button class="btn primary" :disabled="!canSubmit || submitting" @click="createSubscription">
          {{ submitting ? '开通中...' : '立即开通' }}
        </button>
      </div>
    </section>

    <section class="card">
      <div class="section-head">
        <h2>我的订阅</h2>
        <span class="muted">{{ loadingMine ? '加载中...' : `${state.mine.length} 条` }}</span>
      </div>
      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>订阅号</th>
              <th>套餐</th>
              <th>周期</th>
              <th>实付</th>
              <th>状态</th>
              <th>生效/到期</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="!state.mine.length">
              <td colspan="6" class="empty">暂无订阅记录</td>
            </tr>
            <tr v-for="row in state.mine" :key="row.subscriptionId">
              <td>{{ row.subscriptionId }}</td>
              <td>{{ row.planName }}</td>
              <td>{{ cycleTextMap[row.billingCycle] || row.billingCycle }}</td>
              <td>￥{{ row.amountYuan }}</td>
              <td>
                <span class="status" :data-status="row.status">{{ statusTextMap[row.status] || row.status }}</span>
              </td>
              <td>{{ row.startAt }} ~ {{ row.expireAt }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </main>
</template>

<style scoped>
.subscription-page {
  max-width: 1160px;
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
  color: #4b5563;
  margin: 0 0 12px;
}
.filters,
.subscribe-form {
  display: flex;
  flex-wrap: wrap;
  align-items: end;
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
.checkbox {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
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
.error {
  color: #b42318;
  margin-top: 10px;
}
.success {
  color: #0c7a43;
  margin-top: 10px;
}
.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}
.muted {
  color: #6b7280;
  margin: 0;
}
.plan-grid {
  display: grid;
  gap: 12px;
  grid-template-columns: repeat(3, minmax(0, 1fr));
}
.plan-card {
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  padding: 12px;
}
.plan-card.highlight {
  border-color: #f57c00;
  box-shadow: 0 0 0 1px #f57c0033 inset;
}
.plan-card header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}
.plan-card h3 {
  margin: 0;
}
.tag {
  padding: 2px 8px;
  border-radius: 999px;
  background: #fff7ed;
  color: #b45309;
  font-size: 12px;
}
.price {
  margin: 10px 0 6px;
  color: #111827;
  font-weight: 700;
}
.feature-list {
  margin: 8px 0 0;
  padding-left: 18px;
  color: #374151;
}
.feature-list li {
  margin: 4px 0;
}
.plan-actions {
  margin-top: 10px;
  display: grid;
  gap: 8px;
}
.table-wrap {
  overflow-x: auto;
}
table {
  width: 100%;
  border-collapse: collapse;
  min-width: 780px;
}
th,
td {
  border-bottom: 1px solid #f1f5f9;
  text-align: left;
  padding: 10px 8px;
  font-size: 14px;
}
th {
  color: #374151;
  background: #fafafa;
}
.empty {
  text-align: center;
  color: #6b7280;
}
.status[data-status='ACTIVE'] {
  color: #0c7a43;
}
.status[data-status='TRIALING'] {
  color: #1d4ed8;
}
.status[data-status='EXPIRED'],
.status[data-status='CANCELLED'] {
  color: #92400e;
}
@media (max-width: 980px) {
  .plan-grid {
    grid-template-columns: repeat(1, minmax(0, 1fr));
  }
}
</style>
