<script setup>
import { computed, onMounted, reactive, ref } from 'vue'

const loading = ref(false)
const saving = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

const query = reactive({
  adminToken: 'test-admin-token',
  keyword: '',
  planType: '',
  enabled: '',
  recommended: '',
  page: 1,
  pageSize: 10
})

const state = reactive({
  items: [],
  total: 0,
  page: 1,
  pageSize: 10,
  enabledCount: 0,
  disabledCount: 0,
  recommendedCount: 0,
  avgDiscountRate: '0%',
  totalMonthlyAmount: '0',
  updatedAt: ''
})

const selected = ref(null)
const form = reactive({
  planCode: '',
  planName: '',
  planType: '',
  billingCycle: 'MONTHLY',
  price: '',
  originalPrice: '',
  suitableFor: '',
  recommended: false,
  enabled: true,
  operator: 'a07-admin-ui',
  remark: '',
  features: []
})

const canLoad = computed(() => query.adminToken.trim().length > 0)
const canSave = computed(() => {
  return (
    query.adminToken.trim() &&
    form.planCode.trim() &&
    form.planName.trim() &&
    form.planType.trim() &&
    form.billingCycle.trim() &&
    form.price.trim() &&
    form.originalPrice.trim() &&
    form.suitableFor.trim() &&
    form.operator.trim() &&
    form.features.length > 0
  )
})
const totalPages = computed(() => Math.max(Math.ceil(state.total / query.pageSize), 1))

function normalizeFeature(feature, idx) {
  return {
    code: feature.code || '',
    name: feature.name || '',
    value: feature.value || '',
    highlight: feature.highlight || '标准',
    sort: Number(feature.sort || idx + 1)
  }
}

function mapSelectedToForm(item) {
  form.planCode = item.planCode || ''
  form.planName = item.planName || ''
  form.planType = item.planType || ''
  form.billingCycle = item.billingCycle || 'MONTHLY'
  form.price = item.price || ''
  form.originalPrice = item.originalPrice || ''
  form.suitableFor = item.suitableFor || ''
  form.recommended = Boolean(item.recommended)
  form.enabled = Boolean(item.enabled)
  form.features = (item.features || []).map((feature, idx) => normalizeFeature(feature, idx))
}

async function loadData() {
  if (!canLoad.value || loading.value) return
  loading.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const params = new URLSearchParams()
    if (query.keyword.trim()) params.set('keyword', query.keyword.trim())
    if (query.planType.trim()) params.set('planType', query.planType.trim())
    if (query.enabled.trim()) params.set('enabled', query.enabled.trim())
    if (query.recommended.trim()) params.set('recommended', query.recommended.trim())
    params.set('page', String(query.page))
    params.set('pageSize', String(query.pageSize))
    const resp = await fetch(`/api/admin/plan-pricing/plans?${params.toString()}`, {
      headers: { 'X-Admin-Token': query.adminToken.trim() }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `加载失败(${resp.status})`)
    }
    const data = json.data || {}
    state.items = Array.isArray(data.items) ? data.items : []
    state.total = Number(data.total || 0)
    state.page = Number(data.page || query.page)
    state.pageSize = Number(data.pageSize || query.pageSize)
    state.enabledCount = Number(data.enabledCount || 0)
    state.disabledCount = Number(data.disabledCount || 0)
    state.recommendedCount = Number(data.recommendedCount || 0)
    state.avgDiscountRate = data.avgDiscountRate || '0%'
    state.totalMonthlyAmount = data.totalMonthlyAmount || '0'
    state.updatedAt = data.updatedAt || ''
    if (state.items.length > 0) {
      const current = selected.value
      const hit = current ? state.items.find((item) => item.planCode === current.planCode) : state.items[0]
      selected.value = hit || state.items[0]
      mapSelectedToForm(selected.value)
    } else {
      selected.value = null
    }
  } catch (error) {
    errorMsg.value = error.message || '加载套餐与定价失败'
  } finally {
    loading.value = false
  }
}

function selectItem(item) {
  selected.value = item
  mapSelectedToForm(item)
  successMsg.value = ''
}

function addFeature() {
  form.features.push({
    code: '',
    name: '',
    value: '',
    highlight: '标准',
    sort: form.features.length + 1
  })
}

function removeFeature(idx) {
  form.features.splice(idx, 1)
  form.features.forEach((item, index) => {
    item.sort = index + 1
  })
}

async function savePlan() {
  if (!canSave.value || saving.value) return
  saving.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      planCode: form.planCode.trim(),
      planName: form.planName.trim(),
      planType: form.planType.trim().toUpperCase(),
      billingCycle: form.billingCycle,
      price: form.price.trim(),
      originalPrice: form.originalPrice.trim(),
      suitableFor: form.suitableFor.trim(),
      recommended: form.recommended,
      enabled: form.enabled,
      operator: form.operator.trim(),
      remark: form.remark.trim() || null,
      features: form.features.map((item, idx) => ({
        code: item.code.trim().toUpperCase(),
        name: item.name.trim(),
        value: item.value.trim(),
        highlight: item.highlight?.trim() || '标准',
        sort: Number(item.sort || idx + 1)
      }))
    }
    const resp = await fetch(`/api/admin/plan-pricing/plans/${encodeURIComponent(form.planCode.trim())}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'X-Admin-Token': query.adminToken.trim()
      },
      body: JSON.stringify(payload)
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `保存失败(${resp.status})`)
    }
    successMsg.value = `套餐 ${json.data.planCode} 保存成功`
    await loadData()
  } catch (error) {
    errorMsg.value = error.message || '保存套餐失败'
  } finally {
    saving.value = false
  }
}

function prevPage() {
  if (query.page <= 1 || loading.value) return
  query.page -= 1
  loadData()
}

function nextPage() {
  if (query.page >= totalPages.value || loading.value) return
  query.page += 1
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<template>
  <main class="a07-page">
    <section class="card hero">
      <h1>A07 套餐与定价管理</h1>
      <p>统一管理套餐价格、计费周期、推荐位与启停状态，联动订阅与账单数据评估商业化效果。</p>
    </section>

    <section class="card">
      <h2>查询筛选</h2>
      <div class="filters">
        <label>
          Admin Token
          <input v-model="query.adminToken" placeholder="请输入X-Admin-Token" />
        </label>
        <label>
          关键词
          <input v-model="query.keyword" placeholder="套餐编码/名称/适用对象" />
        </label>
        <label>
          套餐类型
          <input v-model="query.planType" placeholder="如 BASIC/PRO/ENTERPRISE" />
        </label>
        <label>
          启停状态
          <select v-model="query.enabled">
            <option value="">全部</option>
            <option value="true">启用</option>
            <option value="false">停用</option>
          </select>
        </label>
        <label>
          推荐位
          <select v-model="query.recommended">
            <option value="">全部</option>
            <option value="true">推荐</option>
            <option value="false">非推荐</option>
          </select>
        </label>
      </div>
      <div class="actions">
        <button class="btn btn--primary" :disabled="!canLoad || loading" @click="loadData">
          {{ loading ? '加载中...' : '查询套餐' }}
        </button>
      </div>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
      <p v-if="successMsg" class="ok">{{ successMsg }}</p>
    </section>

    <section class="card">
      <h2>定价概览</h2>
      <div class="kpi-grid">
        <article class="kpi"><span>套餐总数</span><strong>{{ state.total }}</strong></article>
        <article class="kpi"><span>启用套餐</span><strong>{{ state.enabledCount }}</strong></article>
        <article class="kpi"><span>停用套餐</span><strong>{{ state.disabledCount }}</strong></article>
        <article class="kpi"><span>推荐套餐</span><strong>{{ state.recommendedCount }}</strong></article>
        <article class="kpi"><span>平均折扣率</span><strong>{{ state.avgDiscountRate }}</strong></article>
        <article class="kpi"><span>月度定价总额</span><strong>¥{{ state.totalMonthlyAmount }}</strong></article>
      </div>
      <p class="tip">数据更新时间：{{ state.updatedAt || '-' }}</p>
    </section>

    <section class="card">
      <div class="head">
        <h2>套餐列表</h2>
        <span class="tip">共 {{ state.total }} 条</span>
      </div>
      <p v-if="loading">列表加载中...</p>
      <p v-else-if="state.items.length === 0">暂无套餐</p>
      <ul v-else class="plan-list">
        <li
          v-for="item in state.items"
          :key="item.planCode"
          :class="['plan-item', selected?.planCode === item.planCode ? 'active' : '']"
          @click="selectItem(item)"
        >
          <div class="line">
            <strong>{{ item.planName }}</strong>
            <span class="badge">{{ item.planCode }}</span>
            <span class="badge">{{ item.planType }}</span>
            <span class="badge" :class="{ warn: !item.enabled }">{{ item.enabled ? '启用' : '停用' }}</span>
          </div>
          <p class="muted">
            ¥{{ item.price }} / {{ item.billingCycle }} ｜ 原价 ¥{{ item.originalPrice }} ｜ {{ item.suitableFor }}
          </p>
          <p class="muted">
            生效订阅 {{ item.activeSubscriptionCount }} ｜ 待收账单 {{ item.pendingBillCount }} ｜ 累计回款
            {{ item.totalRevenueYuan }}
          </p>
        </li>
      </ul>
      <div class="pager">
        <button class="btn" :disabled="query.page <= 1 || loading" @click="prevPage">上一页</button>
        <span>第 {{ query.page }} / {{ totalPages }} 页</span>
        <button class="btn" :disabled="query.page >= totalPages || loading" @click="nextPage">下一页</button>
      </div>
    </section>

    <section class="card" v-if="selected">
      <h2>套餐编辑</h2>
      <div class="form-grid">
        <label>套餐编码<input v-model="form.planCode" readonly /></label>
        <label>套餐名称<input v-model="form.planName" /></label>
        <label>套餐类型<input v-model="form.planType" /></label>
        <label>
          计费周期
          <select v-model="form.billingCycle">
            <option value="MONTHLY">MONTHLY</option>
            <option value="QUARTERLY">QUARTERLY</option>
            <option value="YEARLY">YEARLY</option>
          </select>
        </label>
        <label>现价<input v-model="form.price" /></label>
        <label>原价<input v-model="form.originalPrice" /></label>
        <label class="span-2">适用对象<input v-model="form.suitableFor" /></label>
        <label class="inline-check">
          <input v-model="form.recommended" type="checkbox" />
          推荐套餐
        </label>
        <label class="inline-check">
          <input v-model="form.enabled" type="checkbox" />
          启用套餐（停用后前台不可开通）
        </label>
      </div>

      <div class="head">
        <h3>权益配置</h3>
        <button class="btn" @click="addFeature">新增权益</button>
      </div>
      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>编码</th>
              <th>名称</th>
              <th>说明</th>
              <th>标签</th>
              <th>排序</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(item, idx) in form.features" :key="`${item.code}-${idx}`">
              <td><input v-model="item.code" /></td>
              <td><input v-model="item.name" /></td>
              <td><input v-model="item.value" /></td>
              <td><input v-model="item.highlight" /></td>
              <td><input v-model="item.sort" type="number" min="1" max="99" /></td>
              <td><button class="btn danger" @click="removeFeature(idx)">删除</button></td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="form-grid">
        <label>
          操作人
          <input v-model="form.operator" />
        </label>
        <label class="span-2">
          备注
          <input v-model="form.remark" placeholder="可选" />
        </label>
      </div>

      <div class="actions">
        <button class="btn btn--primary" :disabled="!canSave || saving" @click="savePlan">
          {{ saving ? '保存中...' : '保存套餐配置' }}
        </button>
      </div>
    </section>
  </main>
</template>

<style scoped>
.a07-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px 16px 40px;
}
.card {
  background: #fff;
  border: 1px solid #ececec;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 14px;
}
.hero h1 {
  margin: 0;
}
.hero p {
  margin: 8px 0 0;
  color: #4b5563;
}
.filters,
.form-grid {
  display: grid;
  gap: 10px;
  grid-template-columns: repeat(4, minmax(0, 1fr));
}
.form-grid .span-2 {
  grid-column: span 2;
}
label {
  display: grid;
  gap: 6px;
  color: #374151;
}
input,
select,
textarea {
  width: 100%;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 8px 10px;
  font: inherit;
  box-sizing: border-box;
}
.inline-check {
  display: flex;
  align-items: center;
  gap: 8px;
}
.actions {
  margin-top: 12px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
}
.btn {
  border: 1px solid #ddd;
  border-radius: 8px;
  background: #fff;
  padding: 8px 12px;
  cursor: pointer;
}
.btn--primary {
  border-color: #f57c00;
  background: #f57c00;
  color: #fff;
}
.btn.danger {
  border-color: #fda29b;
  color: #b42318;
}
.error {
  color: #b42318;
}
.ok {
  color: #0c7a43;
}
.kpi-grid {
  display: grid;
  gap: 10px;
  grid-template-columns: repeat(6, minmax(0, 1fr));
}
.kpi {
  background: #f8fafc;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  padding: 10px;
  display: grid;
  gap: 6px;
}
.kpi span {
  font-size: 12px;
  color: #6b7280;
}
.kpi strong {
  font-size: 20px;
  color: #111827;
}
.tip,
.muted {
  color: #6b7280;
  margin: 6px 0 0;
}
.head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  margin-bottom: 10px;
}
.plan-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: grid;
  gap: 10px;
}
.plan-item {
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  padding: 10px;
  cursor: pointer;
}
.plan-item.active {
  border-color: #f57c00;
  box-shadow: 0 0 0 1px #f57c0030 inset;
}
.line {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}
.badge {
  background: #eef2ff;
  color: #3730a3;
  border-radius: 999px;
  padding: 2px 8px;
  font-size: 12px;
}
.badge.warn {
  background: #fff5f5;
  color: #b42318;
}
.pager {
  margin-top: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
}
.table-wrap {
  overflow: auto;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  margin-bottom: 10px;
}
table {
  width: 100%;
  border-collapse: collapse;
  min-width: 840px;
}
th,
td {
  border-bottom: 1px solid #f1f5f9;
  text-align: left;
  vertical-align: middle;
  padding: 8px;
}

@media (max-width: 1024px) {
  .filters,
  .form-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
  .kpi-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 720px) {
  .filters,
  .form-grid,
  .kpi-grid {
    grid-template-columns: 1fr;
  }
  .form-grid .span-2 {
    grid-column: span 1;
  }
}
</style>
