<script setup>
import { computed, onMounted, reactive, ref } from 'vue'

const loading = ref(false)
const detailLoading = ref(false)
const saving = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

const query = reactive({
  adminToken: 'test-admin-token',
  experimentStatus: '',
  scenarioCode: '',
  optimizationStage: '',
  keyword: '',
  page: 1,
  pageSize: 10
})

const state = reactive({
  total: 0,
  page: 1,
  pageSize: 10,
  runningCount: 0,
  draftCount: 0,
  completedCount: 0,
  pausedCount: 0,
  records: []
})

const selectedExperimentId = ref('')
const detail = ref(null)
const form = reactive({
  experimentCode: '',
  experimentName: '',
  scenarioCode: 'LEAD_DISPATCH',
  experimentStatus: 'DRAFT',
  optimizationStage: 'DRAFTING',
  trafficPercent: '30',
  targetMetricCode: 'QUOTE_TO_DEAL_RATE',
  baselineValue: '',
  targetValue: '',
  startDate: '',
  endDate: '',
  owner: '策略运营组',
  metricsText:
    'QUOTE_TO_DEAL_RATE:报价转成交率:23.8%:27.1%:13.87%:99.1%\nLEAD_RESPONSE_SPEED:线索响应时效:11.2分钟:9.7分钟:13.39%:97.8%',
  remark: '',
  operator: 'admn14-admin-ui'
})

const canLoad = computed(() => query.adminToken.trim().length > 0)
const canSave = computed(() => {
  return (
    query.adminToken.trim() &&
    form.experimentCode.trim() &&
    form.experimentName.trim() &&
    form.scenarioCode.trim() &&
    form.experimentStatus.trim() &&
    form.optimizationStage.trim() &&
    form.trafficPercent.trim() &&
    form.targetMetricCode.trim() &&
    form.baselineValue.trim() &&
    form.targetValue.trim() &&
    form.startDate.trim() &&
    form.owner.trim() &&
    form.operator.trim()
  )
})
const totalPages = computed(() => Math.max(Math.ceil(state.total / query.pageSize), 1))

function parseMetricsText() {
  return form.metricsText
    .split('\n')
    .map((line) => line.trim())
    .filter(Boolean)
    .map((line) => {
      const [metricCode, metricName, controlValue, variantValue, upliftRate, confidenceLevel] = line.split(':')
      return {
        metricCode: (metricCode || '').trim(),
        metricName: (metricName || '').trim(),
        controlValue: (controlValue || '').trim(),
        variantValue: (variantValue || '').trim(),
        upliftRate: (upliftRate || '').trim(),
        confidenceLevel: (confidenceLevel || '').trim()
      }
    })
    .filter((item) => item.metricCode && item.metricName)
}

function formatMetricsText(metrics) {
  if (!Array.isArray(metrics) || metrics.length === 0) return ''
  return metrics
    .map(
      (item) =>
        `${item.metricCode || ''}:${item.metricName || ''}:${item.controlValue || ''}:${item.variantValue || ''}:${item.upliftRate || ''}:${item.confidenceLevel || ''}`
    )
    .join('\n')
}

function applyDetailToForm(data) {
  form.experimentCode = data?.experimentCode || ''
  form.experimentName = data?.experimentName || ''
  form.scenarioCode = data?.scenarioCode || 'LEAD_DISPATCH'
  form.experimentStatus = data?.experimentStatus || 'DRAFT'
  form.optimizationStage = data?.optimizationStage || 'DRAFTING'
  form.trafficPercent = (data?.trafficSplitPlan || 'A:30% / B:70%').match(/A:(\d+)%/)?.[1] || '30'
  form.targetMetricCode = data?.targetMetric || 'QUOTE_TO_DEAL_RATE'
  form.baselineValue = data?.baselineValue || ''
  form.targetValue = data?.targetValue || ''
  form.startDate = data?.startDate || ''
  form.endDate = data?.endDate || ''
  form.owner = data?.owner || '策略运营组'
  form.metricsText = formatMetricsText(data?.metrics)
  form.remark = data?.remark || ''
  form.operator = data?.owner || form.operator
}

function resetFormForCreate() {
  selectedExperimentId.value = ''
  detail.value = null
  form.experimentCode = ''
  form.experimentName = ''
  form.scenarioCode = 'LEAD_DISPATCH'
  form.experimentStatus = 'DRAFT'
  form.optimizationStage = 'DRAFTING'
  form.trafficPercent = '30'
  form.targetMetricCode = 'QUOTE_TO_DEAL_RATE'
  form.baselineValue = ''
  form.targetValue = ''
  form.startDate = new Date().toISOString().slice(0, 10)
  form.endDate = ''
  form.owner = '策略运营组'
  form.metricsText =
    'QUOTE_TO_DEAL_RATE:报价转成交率:23.8%:27.1%:13.87%:99.1%\nLEAD_RESPONSE_SPEED:线索响应时效:11.2分钟:9.7分钟:13.39%:97.8%'
  form.remark = ''
}

async function loadList() {
  if (!canLoad.value || loading.value) return
  loading.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const params = new URLSearchParams()
    if (query.experimentStatus.trim()) params.set('experimentStatus', query.experimentStatus.trim())
    if (query.scenarioCode.trim()) params.set('scenarioCode', query.scenarioCode.trim())
    if (query.optimizationStage.trim()) params.set('optimizationStage', query.optimizationStage.trim())
    if (query.keyword.trim()) params.set('keyword', query.keyword.trim())
    params.set('page', String(query.page))
    params.set('pageSize', String(query.pageSize))
    const resp = await fetch(`/api/admin/ab-experiments?${params.toString()}`, {
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
    state.runningCount = Number(data.runningCount || 0)
    state.draftCount = Number(data.draftCount || 0)
    state.completedCount = Number(data.completedCount || 0)
    state.pausedCount = Number(data.pausedCount || 0)
    state.records = Array.isArray(data.records) ? data.records : []
  } catch (error) {
    errorMsg.value = error.message || '加载A/B实验列表失败'
  } finally {
    loading.value = false
  }
}

async function openDetail(experimentId) {
  if (!experimentId || detailLoading.value) return
  detailLoading.value = true
  errorMsg.value = ''
  try {
    const resp = await fetch(`/api/admin/ab-experiments/${encodeURIComponent(experimentId)}`, {
      headers: { 'X-Admin-Token': query.adminToken.trim() }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `详情加载失败(${resp.status})`)
    }
    const data = json.data || {}
    selectedExperimentId.value = data.experimentId || experimentId
    detail.value = data
    applyDetailToForm(data)
  } catch (error) {
    errorMsg.value = error.message || '加载A/B实验详情失败'
  } finally {
    detailLoading.value = false
  }
}

async function saveExperiment() {
  if (!canSave.value || saving.value) return
  saving.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      experimentCode: form.experimentCode.trim().toUpperCase(),
      experimentName: form.experimentName.trim(),
      scenarioCode: form.scenarioCode.trim().toUpperCase(),
      experimentStatus: form.experimentStatus.trim().toUpperCase(),
      optimizationStage: form.optimizationStage.trim().toUpperCase(),
      trafficPercent: form.trafficPercent.trim(),
      targetMetricCode: form.targetMetricCode.trim().toUpperCase(),
      baselineValue: form.baselineValue.trim(),
      targetValue: form.targetValue.trim(),
      startDate: form.startDate.trim(),
      endDate: form.endDate.trim() || null,
      owner: form.owner.trim(),
      metrics: parseMetricsText(),
      remark: form.remark.trim() || null,
      operator: form.operator.trim()
    }
    const resp = await fetch('/api/admin/ab-experiments', {
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
    successMsg.value = `实验 ${data.experimentCode || form.experimentCode} 保存成功`
    selectedExperimentId.value = data.experimentId || ''
    detail.value = data
    applyDetailToForm(data)
    await loadList()
  } catch (error) {
    errorMsg.value = error.message || '保存A/B实验失败'
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
  <main class="admn14-page">
    <section class="card hero">
      <h1>ADM-N14 A/B实验中心</h1>
      <p>面向线索分发、对账流程与售后流程的 A/B 实验持续优化配置与结果沉淀。</p>
    </section>

    <section class="card">
      <h2>筛选查询</h2>
      <div class="filters">
        <label>
          Admin Token
          <input v-model="query.adminToken" placeholder="请输入X-Admin-Token" />
        </label>
        <label>
          实验状态
          <select v-model="query.experimentStatus">
            <option value="">全部</option>
            <option value="RUNNING">RUNNING</option>
            <option value="DRAFT">DRAFT</option>
            <option value="PAUSED">PAUSED</option>
            <option value="COMPLETED">COMPLETED</option>
          </select>
        </label>
        <label>
          场景
          <select v-model="query.scenarioCode">
            <option value="">全部</option>
            <option value="LEAD_DISPATCH">LEAD_DISPATCH</option>
            <option value="RECONCILE_FLOW">RECONCILE_FLOW</option>
            <option value="AFTER_SALE_FLOW">AFTER_SALE_FLOW</option>
            <option value="PAYMENT_ROUTE">PAYMENT_ROUTE</option>
          </select>
        </label>
        <label>
          优化阶段
          <select v-model="query.optimizationStage">
            <option value="">全部</option>
            <option value="DRAFTING">DRAFTING</option>
            <option value="SCALING">SCALING</option>
            <option value="RELEASED">RELEASED</option>
            <option value="ROLLBACK">ROLLBACK</option>
          </select>
        </label>
        <label class="span-2">
          关键词
          <input v-model="query.keyword" placeholder="实验编码/名称/指标/负责人" />
        </label>
      </div>
      <div class="actions">
        <button class="btn btn--primary" :disabled="!canLoad || loading" @click="loadList">
          {{ loading ? '加载中...' : '查询实验' }}
        </button>
        <button class="btn" @click="resetFormForCreate">新建实验</button>
      </div>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
      <p v-if="successMsg" class="ok">{{ successMsg }}</p>
    </section>

    <section class="card">
      <h2>实验统计</h2>
      <div class="kpi-grid">
        <article class="kpi"><span>实验总数</span><strong>{{ state.total }}</strong></article>
        <article class="kpi"><span>运行中</span><strong>{{ state.runningCount }}</strong></article>
        <article class="kpi"><span>草稿</span><strong>{{ state.draftCount }}</strong></article>
        <article class="kpi"><span>已完成</span><strong>{{ state.completedCount }}</strong></article>
        <article class="kpi"><span>已暂停</span><strong>{{ state.pausedCount }}</strong></article>
      </div>
    </section>

    <section class="card">
      <div class="head">
        <h2>实验列表</h2>
        <span class="tip">共 {{ state.total }} 条</span>
      </div>
      <p v-if="loading">列表加载中...</p>
      <p v-else-if="state.records.length === 0">暂无实验数据</p>
      <ul v-else class="exp-list">
        <li
          v-for="item in state.records"
          :key="item.experimentId"
          :class="['exp-item', selectedExperimentId === item.experimentId ? 'active' : '']"
          @click="openDetail(item.experimentId)"
        >
          <div class="line">
            <strong>{{ item.experimentName }}</strong>
            <span class="badge">{{ item.experimentCode }}</span>
            <span class="badge">{{ item.scenarioCode }}</span>
            <span class="badge">{{ item.optimizationStage }}</span>
            <span class="badge" :class="{ warn: item.experimentStatus !== 'RUNNING' }">{{
              item.experimentStatus
            }}</span>
          </div>
          <p class="muted">
            流量 {{ item.trafficPercent }}% ｜ 主指标 {{ item.primaryMetric }} ｜ 预期提升 {{ item.liftPercent }} ｜ 置信度
            {{ item.confidenceLevel }}
          </p>
          <p class="muted">负责人：{{ item.owner }} ｜ 更新时间：{{ item.updatedAt }}</p>
        </li>
      </ul>
      <div class="pager">
        <button class="btn" :disabled="query.page <= 1 || loading" @click="prevPage">上一页</button>
        <span>第 {{ query.page }} / {{ totalPages }} 页</span>
        <button class="btn" :disabled="query.page >= totalPages || loading" @click="nextPage">下一页</button>
      </div>
    </section>

    <section class="card">
      <h2>实验编辑</h2>
      <div class="form-grid">
        <label>实验编码<input v-model="form.experimentCode" placeholder="如 AB_LEAD_DISPATCH_BIAS_2026Q2" /></label>
        <label>实验名称<input v-model="form.experimentName" placeholder="如 线索分发权重偏置优化实验" /></label>
        <label>
          场景
          <select v-model="form.scenarioCode">
            <option value="LEAD_DISPATCH">LEAD_DISPATCH</option>
            <option value="RECONCILE_FLOW">RECONCILE_FLOW</option>
            <option value="AFTER_SALE_FLOW">AFTER_SALE_FLOW</option>
            <option value="PAYMENT_ROUTE">PAYMENT_ROUTE</option>
          </select>
        </label>
        <label>
          实验状态
          <select v-model="form.experimentStatus">
            <option value="RUNNING">RUNNING</option>
            <option value="DRAFT">DRAFT</option>
            <option value="PAUSED">PAUSED</option>
            <option value="COMPLETED">COMPLETED</option>
          </select>
        </label>
        <label>
          优化阶段
          <select v-model="form.optimizationStage">
            <option value="DRAFTING">DRAFTING</option>
            <option value="SCALING">SCALING</option>
            <option value="RELEASED">RELEASED</option>
            <option value="ROLLBACK">ROLLBACK</option>
          </select>
        </label>
        <label>流量A占比(%)<input v-model="form.trafficPercent" placeholder="如 35" /></label>
        <label>目标指标编码<input v-model="form.targetMetricCode" placeholder="如 QUOTE_TO_DEAL_RATE" /></label>
        <label>对照组基线值<input v-model="form.baselineValue" placeholder="如 23.8%" /></label>
        <label>实验组目标值<input v-model="form.targetValue" placeholder="如 27.1%" /></label>
        <label>开始日期<input v-model="form.startDate" placeholder="YYYY-MM-DD" /></label>
        <label>结束日期<input v-model="form.endDate" placeholder="YYYY-MM-DD，可空" /></label>
        <label>负责人<input v-model="form.owner" /></label>
        <label>操作人<input v-model="form.operator" /></label>
        <label class="span-2">
          指标快照（每行：编码:名称:对照值:实验值:提升率:置信度）
          <textarea
            v-model="form.metricsText"
            rows="5"
            placeholder="QUOTE_TO_DEAL_RATE:报价转成交率:23.8%:27.1%:13.87%:99.1%"
          />
        </label>
        <label class="span-2">
          备注
          <input v-model="form.remark" placeholder="可空" />
        </label>
      </div>
      <div class="actions">
        <button class="btn btn--primary" :disabled="!canSave || saving" @click="saveExperiment">
          {{ saving ? '保存中...' : '保存实验' }}
        </button>
      </div>
    </section>

    <section class="card" v-if="detail">
      <h2>实验详情</h2>
      <p class="muted">
        实验ID：{{ detail.experimentId }} ｜ 更新时间：{{ detail.updatedAt }} ｜ 可执行动作：{{
          detail.availableActions?.join(' / ') || '-'
        }}
      </p>
      <p class="muted">
        流量分配：{{ detail.trafficSplitPlan }} ｜ 胜出版本：{{ detail.winnerVariant }} ｜ 预期增益：{{ detail.expectedGainRate }}
      </p>
      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>指标编码</th>
              <th>指标名称</th>
              <th>对照组</th>
              <th>实验组</th>
              <th>提升率</th>
              <th>置信度</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in detail.metrics || []" :key="item.metricCode">
              <td>{{ item.metricCode }}</td>
              <td>{{ item.metricName }}</td>
              <td>{{ item.controlValue }}</td>
              <td>{{ item.variantValue }}</td>
              <td>{{ item.upliftRate }}</td>
              <td>{{ item.confidenceLevel }}</td>
            </tr>
            <tr v-if="!detail.metrics || detail.metrics.length === 0">
              <td colspan="6">暂无指标快照</td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </main>
</template>

<style scoped>
.admn14-page {
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
.exp-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: grid;
  gap: 10px;
}
.exp-item {
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  padding: 10px;
  cursor: pointer;
}
.exp-item.active {
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
  min-width: 760px;
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
