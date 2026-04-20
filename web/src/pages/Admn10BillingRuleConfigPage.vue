<script setup>
import { computed, onMounted, reactive, ref } from 'vue'

const loading = ref(false)
const saving = ref(false)
const detailLoading = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

const query = reactive({
  adminToken: 'test-admin-token',
  ruleStatus: '',
  sceneCode: '',
  billingMode: '',
  keyword: '',
  page: 1,
  pageSize: 10
})

const state = reactive({
  total: 0,
  page: 1,
  pageSize: 10,
  activeCount: 0,
  disabledCount: 0,
  draftCount: 0,
  records: []
})

const selectedRuleId = ref('')
const detail = ref(null)
const form = reactive({
  ruleCode: '',
  ruleName: '',
  sceneCode: 'SUBSCRIPTION',
  billingMode: 'FIXED',
  feeCurrency: 'CNY',
  basePriceYuan: '',
  minFeeYuan: '',
  maxFeeYuan: '',
  ladderConfig: '',
  effectiveFrom: '',
  effectiveTo: '',
  ruleStatus: 'ACTIVE',
  remark: '',
  operator: 'admn10-admin-ui'
})

const canLoad = computed(() => query.adminToken.trim().length > 0)
const canSave = computed(() => {
  return (
    query.adminToken.trim() &&
    form.ruleCode.trim() &&
    form.ruleName.trim() &&
    form.sceneCode.trim() &&
    form.billingMode.trim() &&
    form.feeCurrency.trim() &&
    form.basePriceYuan.trim() &&
    form.minFeeYuan.trim() &&
    form.maxFeeYuan.trim() &&
    form.effectiveFrom.trim() &&
    form.ruleStatus.trim() &&
    form.operator.trim()
  )
})
const totalPages = computed(() => Math.max(Math.ceil(state.total / query.pageSize), 1))

function applyDetailToForm(data) {
  form.ruleCode = data?.ruleCode || ''
  form.ruleName = data?.ruleName || ''
  form.sceneCode = data?.sceneCode || 'SUBSCRIPTION'
  form.billingMode = data?.billingMode || 'FIXED'
  form.feeCurrency = data?.feeCurrency || 'CNY'
  form.basePriceYuan = data?.basePriceYuan || ''
  form.minFeeYuan = data?.minFeeYuan || ''
  form.maxFeeYuan = data?.maxFeeYuan || ''
  form.ladderConfig = data?.ladderConfig || ''
  form.effectiveFrom = data?.effectiveFrom || ''
  form.effectiveTo = data?.effectiveTo || ''
  form.ruleStatus = data?.ruleStatus || 'ACTIVE'
  form.remark = data?.remark || ''
}

function resetFormForCreate() {
  selectedRuleId.value = ''
  detail.value = null
  form.ruleCode = ''
  form.ruleName = ''
  form.sceneCode = 'SUBSCRIPTION'
  form.billingMode = 'FIXED'
  form.feeCurrency = 'CNY'
  form.basePriceYuan = ''
  form.minFeeYuan = ''
  form.maxFeeYuan = ''
  form.ladderConfig = ''
  form.effectiveFrom = new Date().toISOString().slice(0, 10)
  form.effectiveTo = ''
  form.ruleStatus = 'ACTIVE'
  form.remark = ''
}

async function loadRules() {
  if (!canLoad.value || loading.value) return
  loading.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const params = new URLSearchParams()
    if (query.ruleStatus.trim()) params.set('ruleStatus', query.ruleStatus.trim())
    if (query.sceneCode.trim()) params.set('sceneCode', query.sceneCode.trim())
    if (query.billingMode.trim()) params.set('billingMode', query.billingMode.trim())
    if (query.keyword.trim()) params.set('keyword', query.keyword.trim())
    params.set('page', String(query.page))
    params.set('pageSize', String(query.pageSize))
    const resp = await fetch(`/api/admin/billing-rules?${params.toString()}`, {
      headers: { 'X-Admin-Token': query.adminToken.trim() }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `加载失败(${resp.status})`)
    }
    const data = json.data || {}
    state.total = Number(data.total || 0)
    state.page = Number(data.page || query.page)
    state.pageSize = Number(data.pageSize || query.pageSize)
    state.activeCount = Number(data.activeCount || 0)
    state.disabledCount = Number(data.disabledCount || 0)
    state.draftCount = Number(data.draftCount || 0)
    state.records = Array.isArray(data.records) ? data.records : []
    if (state.records.length > 0) {
      const hit =
        selectedRuleId.value &&
        state.records.find((item) => item.ruleId === selectedRuleId.value)
      if (hit) {
        await openDetail(hit.ruleId)
      }
    }
  } catch (error) {
    errorMsg.value = error.message || '加载计费规则失败'
  } finally {
    loading.value = false
  }
}

async function openDetail(ruleId) {
  if (!ruleId || detailLoading.value) return
  detailLoading.value = true
  errorMsg.value = ''
  try {
    const resp = await fetch(`/api/admin/billing-rules/${encodeURIComponent(ruleId)}`, {
      headers: { 'X-Admin-Token': query.adminToken.trim() }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `详情加载失败(${resp.status})`)
    }
    const data = json.data || {}
    selectedRuleId.value = data.ruleId || ruleId
    detail.value = data
    applyDetailToForm(data)
  } catch (error) {
    errorMsg.value = error.message || '加载规则详情失败'
  } finally {
    detailLoading.value = false
  }
}

async function saveRule() {
  if (!canSave.value || saving.value) return
  saving.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      ruleCode: form.ruleCode.trim(),
      ruleName: form.ruleName.trim(),
      sceneCode: form.sceneCode.trim().toUpperCase(),
      billingMode: form.billingMode.trim().toUpperCase(),
      feeCurrency: form.feeCurrency.trim().toUpperCase(),
      basePriceYuan: form.basePriceYuan.trim(),
      minFeeYuan: form.minFeeYuan.trim(),
      maxFeeYuan: form.maxFeeYuan.trim(),
      ladderConfig: form.ladderConfig.trim() || null,
      effectiveFrom: form.effectiveFrom.trim(),
      effectiveTo: form.effectiveTo.trim() || null,
      ruleStatus: form.ruleStatus.trim().toUpperCase(),
      remark: form.remark.trim() || null,
      operator: form.operator.trim()
    }
    const resp = await fetch('/api/admin/billing-rules', {
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
    const data = json.data || {}
    successMsg.value = `规则 ${data.ruleCode || form.ruleCode} 保存成功`
    selectedRuleId.value = data.ruleId || ''
    detail.value = data
    applyDetailToForm(data)
    await loadRules()
  } catch (error) {
    errorMsg.value = error.message || '保存计费规则失败'
  } finally {
    saving.value = false
  }
}

function prevPage() {
  if (query.page <= 1 || loading.value) return
  query.page -= 1
  loadRules()
}

function nextPage() {
  if (query.page >= totalPages.value || loading.value) return
  query.page += 1
  loadRules()
}

onMounted(() => {
  resetFormForCreate()
  loadRules()
})
</script>

<template>
  <main class="admn10-page">
    <section class="card hero">
      <h1>ADM-N10 计费规则配置</h1>
      <p>统一管理套餐订阅、账单出账、结算回款与争议仲裁的计费规则与生效状态。</p>
    </section>

    <section class="card">
      <h2>筛选查询</h2>
      <div class="filters">
        <label>
          Admin Token
          <input v-model="query.adminToken" placeholder="请输入X-Admin-Token" />
        </label>
        <label>
          规则状态
          <select v-model="query.ruleStatus">
            <option value="">全部</option>
            <option value="ACTIVE">ACTIVE</option>
            <option value="DISABLED">DISABLED</option>
            <option value="DRAFT">DRAFT</option>
          </select>
        </label>
        <label>
          场景
          <select v-model="query.sceneCode">
            <option value="">全部</option>
            <option value="SUBSCRIPTION">SUBSCRIPTION</option>
            <option value="BILLING_ORDER">BILLING_ORDER</option>
            <option value="SETTLEMENT">SETTLEMENT</option>
            <option value="ARBITRATION">ARBITRATION</option>
          </select>
        </label>
        <label>
          计费模式
          <select v-model="query.billingMode">
            <option value="">全部</option>
            <option value="FIXED">FIXED</option>
            <option value="LADDER">LADDER</option>
            <option value="RATIO">RATIO</option>
          </select>
        </label>
        <label class="span-2">
          关键词
          <input v-model="query.keyword" placeholder="规则编码/名称/场景" />
        </label>
      </div>
      <div class="actions">
        <button class="btn btn--primary" :disabled="!canLoad || loading" @click="loadRules">
          {{ loading ? '加载中...' : '查询规则' }}
        </button>
        <button class="btn" @click="resetFormForCreate">新建规则</button>
      </div>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
      <p v-if="successMsg" class="ok">{{ successMsg }}</p>
    </section>

    <section class="card">
      <h2>规则统计</h2>
      <div class="kpi-grid">
        <article class="kpi"><span>规则总数</span><strong>{{ state.total }}</strong></article>
        <article class="kpi"><span>生效中</span><strong>{{ state.activeCount }}</strong></article>
        <article class="kpi"><span>已停用</span><strong>{{ state.disabledCount }}</strong></article>
        <article class="kpi"><span>草稿</span><strong>{{ state.draftCount }}</strong></article>
      </div>
    </section>

    <section class="card">
      <div class="head">
        <h2>规则列表</h2>
        <span class="tip">共 {{ state.total }} 条</span>
      </div>
      <p v-if="loading">列表加载中...</p>
      <p v-else-if="state.records.length === 0">暂无规则</p>
      <ul v-else class="rule-list">
        <li
          v-for="item in state.records"
          :key="item.ruleId"
          :class="['rule-item', selectedRuleId === item.ruleId ? 'active' : '']"
          @click="openDetail(item.ruleId)"
        >
          <div class="line">
            <strong>{{ item.ruleName }}</strong>
            <span class="badge">{{ item.ruleCode }}</span>
            <span class="badge">{{ item.sceneCode }}</span>
            <span class="badge">{{ item.billingMode }}</span>
            <span class="badge" :class="{ warn: item.ruleStatus !== 'ACTIVE' }">{{ item.ruleStatus }}</span>
          </div>
          <p class="muted">
            基准 {{ item.basePriceYuan }} ｜ 最低 {{ item.minFeeYuan }} ｜ 最高 {{ item.maxFeeYuan }} ｜ 币种
            {{ item.feeCurrency }}
          </p>
          <p class="muted">生效期：{{ item.effectiveFrom }} ~ {{ item.effectiveTo || '-' }}</p>
        </li>
      </ul>
      <div class="pager">
        <button class="btn" :disabled="query.page <= 1 || loading" @click="prevPage">上一页</button>
        <span>第 {{ query.page }} / {{ totalPages }} 页</span>
        <button class="btn" :disabled="query.page >= totalPages || loading" @click="nextPage">下一页</button>
      </div>
    </section>

    <section class="card">
      <h2>规则编辑</h2>
      <div class="form-grid">
        <label>规则编码<input v-model="form.ruleCode" placeholder="如 BR_SUB_FIXED_001" /></label>
        <label>规则名称<input v-model="form.ruleName" placeholder="如 订阅固定服务费" /></label>
        <label>
          规则场景
          <select v-model="form.sceneCode">
            <option value="SUBSCRIPTION">SUBSCRIPTION</option>
            <option value="BILLING_ORDER">BILLING_ORDER</option>
            <option value="SETTLEMENT">SETTLEMENT</option>
            <option value="ARBITRATION">ARBITRATION</option>
          </select>
        </label>
        <label>
          计费模式
          <select v-model="form.billingMode">
            <option value="FIXED">FIXED</option>
            <option value="LADDER">LADDER</option>
            <option value="RATIO">RATIO</option>
          </select>
        </label>
        <label>币种<input v-model="form.feeCurrency" /></label>
        <label>基准值<input v-model="form.basePriceYuan" placeholder="0/388/0.012" /></label>
        <label>最低费用<input v-model="form.minFeeYuan" /></label>
        <label>最高费用<input v-model="form.maxFeeYuan" /></label>
        <label>生效开始<input v-model="form.effectiveFrom" placeholder="YYYY-MM-DD" /></label>
        <label>生效结束<input v-model="form.effectiveTo" placeholder="YYYY-MM-DD，可空" /></label>
        <label>
          规则状态
          <select v-model="form.ruleStatus">
            <option value="ACTIVE">ACTIVE</option>
            <option value="DISABLED">DISABLED</option>
            <option value="DRAFT">DRAFT</option>
          </select>
        </label>
        <label>操作人<input v-model="form.operator" /></label>
        <label class="span-2">
          阶梯配置（LADDER必填）
          <input v-model="form.ladderConfig" placeholder="0-100:8.5;101-300:7.2" />
        </label>
        <label class="span-2">
          备注
          <input v-model="form.remark" placeholder="可选" />
        </label>
      </div>
      <div class="actions">
        <button class="btn btn--primary" :disabled="!canSave || saving" @click="saveRule">
          {{ saving ? '保存中...' : '保存规则' }}
        </button>
      </div>
    </section>

    <section class="card" v-if="detail">
      <h2>规则详情</h2>
      <p class="muted">
        规则ID：{{ detail.ruleId }} ｜ 更新时间：{{ detail.updatedAt }} ｜ 操作动作：{{
          detail.availableActions?.join(' / ') || '-'
        }}
      </p>
      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>阶梯</th>
              <th>起始</th>
              <th>结束</th>
              <th>价格/比例</th>
              <th>单位</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="step in detail.ruleSteps || []" :key="step.stepNo">
              <td>{{ step.stepNo }}</td>
              <td>{{ step.rangeStart }}</td>
              <td>{{ step.rangeEnd || '-' }}</td>
              <td>{{ step.priceOrRatio }}</td>
              <td>{{ step.unit }}</td>
            </tr>
            <tr v-if="!detail.ruleSteps || detail.ruleSteps.length === 0">
              <td colspan="5">暂无阶梯配置</td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </main>
</template>

<style scoped>
.admn10-page {
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
.form-grid .span-2,
.filters .span-2 {
  grid-column: span 2;
}
label {
  display: grid;
  gap: 6px;
  color: #374151;
}
input,
select {
  width: 100%;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 8px 10px;
  font: inherit;
  box-sizing: border-box;
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
.error {
  color: #b42318;
}
.ok {
  color: #0c7a43;
}
.kpi-grid {
  display: grid;
  gap: 10px;
  grid-template-columns: repeat(4, minmax(0, 1fr));
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
.rule-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: grid;
  gap: 10px;
}
.rule-item {
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  padding: 10px;
  cursor: pointer;
}
.rule-item.active {
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
}
table {
  width: 100%;
  border-collapse: collapse;
  min-width: 560px;
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
  .form-grid,
  .kpi-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 720px) {
  .filters,
  .form-grid,
  .kpi-grid {
    grid-template-columns: 1fr;
  }
  .form-grid .span-2,
  .filters .span-2 {
    grid-column: span 1;
  }
}
</style>
