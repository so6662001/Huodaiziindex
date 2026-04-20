<script setup>
import { computed, onMounted, reactive, ref } from 'vue'

const loading = ref(false)
const detailLoading = ref(false)
const saving = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

const query = reactive({
  adminToken: 'test-admin-token',
  versionStatus: '',
  scenarioCode: '',
  keyword: '',
  page: 1,
  pageSize: 10
})

const state = reactive({
  total: 0,
  page: 1,
  pageSize: 10,
  activeCount: 0,
  grayCount: 0,
  draftCount: 0,
  retiredCount: 0,
  records: []
})

const selectedVersionId = ref('')
const detail = ref(null)
const form = reactive({
  modelCode: '',
  modelName: '',
  versionNo: '',
  versionStatus: 'DRAFT',
  scenarioCode: 'LEAD_DISPATCH',
  effectiveFrom: '',
  effectiveTo: '',
  baseScore: '100',
  passThreshold: '75',
  riskThreshold: '60',
  factorsText: 'FULFILLMENT:履约稳定性:35:POSITIVE',
  remark: '',
  operator: 'admn13-admin-ui'
})

const canLoad = computed(() => query.adminToken.trim().length > 0)
const canSave = computed(() => {
  return (
    query.adminToken.trim() &&
    form.modelCode.trim() &&
    form.modelName.trim() &&
    form.versionNo.trim() &&
    form.versionStatus.trim() &&
    form.scenarioCode.trim() &&
    form.effectiveFrom.trim() &&
    form.baseScore.trim() &&
    form.passThreshold.trim() &&
    form.riskThreshold.trim() &&
    form.operator.trim()
  )
})
const totalPages = computed(() => Math.max(Math.ceil(state.total / query.pageSize), 1))

function parseFactorsText() {
  return form.factorsText
    .split('\n')
    .map((line) => line.trim())
    .filter(Boolean)
    .map((line) => {
      const [factorCode, factorName, weightPercent, impactDirection] = line.split(':')
      return {
        factorCode: (factorCode || '').trim(),
        factorName: (factorName || '').trim(),
        weightPercent: (weightPercent || '').trim(),
        impactDirection: ((impactDirection || 'POSITIVE').trim() || 'POSITIVE').toUpperCase()
      }
    })
    .filter((item) => item.factorCode && item.factorName && item.weightPercent)
}

function formatFactorsText(factors) {
  if (!Array.isArray(factors) || factors.length === 0) return ''
  return factors
    .map((item) => `${item.factorCode || ''}:${item.factorName || ''}:${item.weightPercent || ''}:${item.impactDirection || 'POSITIVE'}`)
    .join('\n')
}

function applyDetailToForm(data) {
  form.modelCode = data?.modelCode || ''
  form.modelName = data?.modelName || ''
  form.versionNo = data?.versionNo || ''
  form.versionStatus = data?.versionStatus || 'DRAFT'
  form.scenarioCode = data?.scenarioCode || 'LEAD_DISPATCH'
  form.effectiveFrom = data?.effectiveFrom || ''
  form.effectiveTo = data?.effectiveTo || ''
  form.baseScore = data?.baseScore || '100'
  form.passThreshold = data?.passThreshold || '75'
  form.riskThreshold = data?.riskThreshold || '60'
  form.factorsText = formatFactorsText(data?.factors)
  form.remark = data?.remark || ''
}

function resetFormForCreate() {
  selectedVersionId.value = ''
  detail.value = null
  form.modelCode = ''
  form.modelName = ''
  form.versionNo = ''
  form.versionStatus = 'DRAFT'
  form.scenarioCode = 'LEAD_DISPATCH'
  form.effectiveFrom = new Date().toISOString().slice(0, 10)
  form.effectiveTo = ''
  form.baseScore = '100'
  form.passThreshold = '75'
  form.riskThreshold = '60'
  form.factorsText = 'FULFILLMENT:履约稳定性:35:POSITIVE'
  form.remark = ''
}

async function loadList() {
  if (!canLoad.value || loading.value) return
  loading.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const params = new URLSearchParams()
    if (query.versionStatus.trim()) params.set('versionStatus', query.versionStatus.trim())
    if (query.scenarioCode.trim()) params.set('scenarioCode', query.scenarioCode.trim())
    if (query.keyword.trim()) params.set('keyword', query.keyword.trim())
    params.set('page', String(query.page))
    params.set('pageSize', String(query.pageSize))
    const resp = await fetch(`/api/admin/credit-model-versions?${params.toString()}`, {
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
    state.grayCount = Number(data.grayCount || 0)
    state.draftCount = Number(data.draftCount || 0)
    state.retiredCount = Number(data.retiredCount || 0)
    state.records = Array.isArray(data.records) ? data.records : []
  } catch (error) {
    errorMsg.value = error.message || '加载信用模型版本失败'
  } finally {
    loading.value = false
  }
}

async function openDetail(versionId) {
  if (!versionId || detailLoading.value) return
  detailLoading.value = true
  errorMsg.value = ''
  try {
    const resp = await fetch(`/api/admin/credit-model-versions/${encodeURIComponent(versionId)}`, {
      headers: { 'X-Admin-Token': query.adminToken.trim() }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `详情加载失败(${resp.status})`)
    }
    const data = json.data || {}
    selectedVersionId.value = data.versionId || versionId
    detail.value = data
    applyDetailToForm(data)
  } catch (error) {
    errorMsg.value = error.message || '加载模型版本详情失败'
  } finally {
    detailLoading.value = false
  }
}

async function saveVersion() {
  if (!canSave.value || saving.value) return
  saving.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      modelCode: form.modelCode.trim().toUpperCase(),
      modelName: form.modelName.trim(),
      versionNo: form.versionNo.trim().toUpperCase(),
      versionStatus: form.versionStatus.trim().toUpperCase(),
      applicableScope: form.scenarioCode.trim().toUpperCase(),
      effectiveFrom: form.effectiveFrom.trim(),
      effectiveTo: form.effectiveTo.trim() || null,
      baseScore: form.baseScore.trim(),
      passThreshold: form.passThreshold.trim(),
      riskThreshold: form.riskThreshold.trim(),
      factors: parseFactorsText(),
      remark: form.remark.trim() || null,
      operator: form.operator.trim()
    }
    const resp = await fetch('/api/admin/credit-model-versions', {
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
    successMsg.value = `模型版本 ${data.versionNo || form.versionNo} 保存成功`
    selectedVersionId.value = data.versionId || ''
    detail.value = data
    applyDetailToForm(data)
    await loadList()
  } catch (error) {
    errorMsg.value = error.message || '保存信用模型版本失败'
  } finally {
    saving.value = false
  }
}

function prevPage() {
  if (query.page <= 1 || loading.value) return
  query.page -= 1
  loadList()
}

function nextPage() {
  if (query.page >= totalPages.value || loading.value) return
  query.page += 1
  loadList()
}

onMounted(() => {
  resetFormForCreate()
  loadList()
})
</script>

<template>
  <main class="admn13-page">
    <section class="card hero">
      <h1>ADM-N13 信用模型版本管理</h1>
      <p>统一管理信用评分模型版本、生效周期、阈值策略与因子权重配置。</p>
    </section>

    <section class="card">
      <h2>筛选查询</h2>
      <div class="filters">
        <label>
          Admin Token
          <input v-model="query.adminToken" placeholder="请输入X-Admin-Token" />
        </label>
        <label>
          版本状态
          <select v-model="query.versionStatus">
            <option value="">全部</option>
            <option value="ACTIVE">ACTIVE</option>
            <option value="GRAY">GRAY</option>
            <option value="DRAFT">DRAFT</option>
            <option value="RETIRED">RETIRED</option>
          </select>
        </label>
        <label>
          适用场景
          <select v-model="query.scenarioCode">
            <option value="">全部</option>
            <option value="LEAD_DISPATCH">LEAD_DISPATCH</option>
            <option value="ORDER_RECONCILE">ORDER_RECONCILE</option>
            <option value="AFTER_SALE">AFTER_SALE</option>
          </select>
        </label>
        <label class="span-2">
          关键词
          <input v-model="query.keyword" placeholder="模型编码/版本号/模型名称" />
        </label>
      </div>
      <div class="actions">
        <button class="btn btn--primary" :disabled="!canLoad || loading" @click="loadList">
          {{ loading ? '加载中...' : '查询版本' }}
        </button>
        <button class="btn" @click="resetFormForCreate">新建版本</button>
      </div>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
      <p v-if="successMsg" class="ok">{{ successMsg }}</p>
    </section>

    <section class="card">
      <h2>版本统计</h2>
      <div class="kpi-grid">
        <article class="kpi"><span>版本总数</span><strong>{{ state.total }}</strong></article>
        <article class="kpi"><span>生效中</span><strong>{{ state.activeCount }}</strong></article>
        <article class="kpi"><span>灰度中</span><strong>{{ state.grayCount }}</strong></article>
        <article class="kpi"><span>草稿</span><strong>{{ state.draftCount }}</strong></article>
        <article class="kpi"><span>已退役</span><strong>{{ state.retiredCount }}</strong></article>
      </div>
    </section>

    <section class="card">
      <div class="head">
        <h2>版本列表</h2>
        <span class="tip">共 {{ state.total }} 条</span>
      </div>
      <p v-if="loading">列表加载中...</p>
      <p v-else-if="state.records.length === 0">暂无信用模型版本</p>
      <ul v-else class="version-list">
        <li
          v-for="item in state.records"
          :key="item.versionId"
          :class="['version-item', selectedVersionId === item.versionId ? 'active' : '']"
          @click="openDetail(item.versionId)"
        >
          <div class="line">
            <strong>{{ item.modelName }}</strong>
            <span class="badge">{{ item.modelCode }}</span>
            <span class="badge">{{ item.versionNo }}</span>
            <span class="badge">{{ item.applicableScope }}</span>
            <span class="badge" :class="{ warn: item.versionStatus !== 'ACTIVE' }">{{ item.versionStatus }}</span>
          </div>
          <p class="muted">
            评分基线 {{ item.baseScore }} ｜ 通过阈值 {{ item.passThreshold }} ｜ 风险阈值 {{ item.riskThreshold }}
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
      <h2>版本编辑</h2>
      <div class="form-grid">
        <label>模型编码<input v-model="form.modelCode" placeholder="如 CREDIT_LEAD_SCORE" /></label>
        <label>模型名称<input v-model="form.modelName" placeholder="如 线索分发信用模型" /></label>
        <label>版本号<input v-model="form.versionNo" placeholder="如 V2026.05" /></label>
        <label>
          版本状态
          <select v-model="form.versionStatus">
            <option value="ACTIVE">ACTIVE</option>
            <option value="GRAY">GRAY</option>
            <option value="DRAFT">DRAFT</option>
            <option value="RETIRED">RETIRED</option>
          </select>
        </label>
        <label>
          适用场景
          <select v-model="form.scenarioCode">
            <option value="LEAD_DISPATCH">LEAD_DISPATCH</option>
            <option value="ORDER_RECONCILE">ORDER_RECONCILE</option>
            <option value="AFTER_SALE">AFTER_SALE</option>
          </select>
        </label>
        <label>生效开始<input v-model="form.effectiveFrom" placeholder="YYYY-MM-DD" /></label>
        <label>生效结束<input v-model="form.effectiveTo" placeholder="YYYY-MM-DD，可空" /></label>
        <label>评分基线<input v-model="form.baseScore" placeholder="如 100" /></label>
        <label>通过阈值<input v-model="form.passThreshold" placeholder="如 75" /></label>
        <label>风险阈值<input v-model="form.riskThreshold" placeholder="如 60" /></label>
        <label>操作人<input v-model="form.operator" /></label>
        <label class="span-2">
          因子权重（每行：编码:名称:权重:方向）
          <textarea v-model="form.factorsText" rows="5" placeholder="FULFILLMENT:履约稳定性:35:POSITIVE" />
        </label>
        <label class="span-2">
          备注
          <input v-model="form.remark" placeholder="可空" />
        </label>
      </div>
      <div class="actions">
        <button class="btn btn--primary" :disabled="!canSave || saving" @click="saveVersion">
          {{ saving ? '保存中...' : '保存版本' }}
        </button>
      </div>
    </section>

    <section class="card" v-if="detail">
      <h2>版本详情</h2>
      <p class="muted">
        版本ID：{{ detail.versionId }} ｜ 更新时间：{{ detail.updatedAt }} ｜ 可执行动作：{{
          detail.availableActions?.join(' / ') || '-'
        }}
      </p>
      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>因子编码</th>
              <th>因子名称</th>
              <th>权重%</th>
              <th>影响方向</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in detail.factors || []" :key="item.factorCode">
              <td>{{ item.factorCode }}</td>
              <td>{{ item.factorName }}</td>
              <td>{{ item.weightPercent }}</td>
              <td>{{ item.impactDirection }}</td>
            </tr>
            <tr v-if="!detail.factors || detail.factors.length === 0">
              <td colspan="4">暂无因子配置</td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </main>
</template>

<style scoped>
.admn13-page {
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
select,
textarea {
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
  grid-template-columns: repeat(5, minmax(0, 1fr));
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
.version-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: grid;
  gap: 10px;
}
.version-item {
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  padding: 10px;
  cursor: pointer;
}
.version-item.active {
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
  min-width: 620px;
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
