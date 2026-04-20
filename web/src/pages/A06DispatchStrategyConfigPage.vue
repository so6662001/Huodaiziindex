<script setup>
import { computed, onMounted, reactive, ref } from 'vue'

const loading = ref(false)
const saving = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

const query = reactive({
  adminToken: 'test-admin-token',
  sceneCode: 'MERCHANT_LEAD',
  keyword: '',
  enabled: '',
  page: 1,
  pageSize: 10
})

const state = reactive({
  items: [],
  total: 0,
  page: 1,
  pageSize: 10,
  activeSceneCount: 0,
  totalBonusCount: 0,
  totalPenaltyCount: 0,
  avgDimensionWeight: '0',
  updatedAt: ''
})

const selected = ref(null)

const form = reactive({
  sceneCode: '',
  sceneName: '',
  ruleVersion: '',
  scoreFormula: '',
  updateCycle: '',
  paidFactorDesc: '',
  disclosuresText: '',
  operator: 'a06-admin-ui',
  remark: '',
  dimensions: [],
  bonuses: [],
  penalties: []
})

const canLoad = computed(() => query.adminToken.trim())
const canSave = computed(() => {
  return (
    query.adminToken.trim() &&
    form.sceneCode.trim() &&
    form.sceneName.trim() &&
    form.ruleVersion.trim() &&
    form.scoreFormula.trim() &&
    form.updateCycle.trim() &&
    form.paidFactorDesc.trim() &&
    form.operator.trim() &&
    form.dimensions.length > 0 &&
    form.bonuses.length > 0 &&
    form.penalties.length > 0
  )
})

const totalPages = computed(() => Math.max(Math.ceil(state.total / query.pageSize), 1))

function mapSelectedToForm(item) {
  form.sceneCode = item.sceneCode || ''
  form.sceneName = item.sceneName || ''
  form.ruleVersion = item.ruleVersion || ''
  form.scoreFormula = item.scoreFormula || ''
  form.updateCycle = item.updateCycle || ''
  form.paidFactorDesc = item.paidFactorDesc || ''
  form.disclosuresText = (item.disclosures || []).join('\n')
  form.dimensions = (item.dimensions || []).map((d) => ({
    code: d.code || '',
    name: d.name || '',
    weight: Number(d.weight || 0),
    description: d.description || '',
    scoreMethod: d.scoreMethod || '',
    dataSource: d.dataSource || ''
  }))
  form.bonuses = (item.bonuses || []).map((b) => ({
    code: b.code || '',
    name: b.name || '',
    scoreChange: b.scoreChange || '',
    trigger: b.triggerCondition || '',
    cap: b.cap || ''
  }))
  form.penalties = (item.penalties || []).map((p) => ({
    code: p.code || '',
    name: p.name || '',
    scoreChange: p.scoreChange || '',
    trigger: p.trigger || '',
    recovery: p.recovery || ''
  }))
}

async function loadData() {
  if (!canLoad.value || loading.value) return
  loading.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const params = new URLSearchParams()
    if (query.sceneCode.trim()) params.set('sceneCode', query.sceneCode.trim())
    if (query.keyword.trim()) params.set('keyword', query.keyword.trim())
    if (query.enabled.trim()) params.set('enabled', query.enabled.trim())
    params.set('page', String(query.page))
    params.set('pageSize', String(query.pageSize))
    const resp = await fetch(`/api/admin/dispatch-strategy/rules?${params.toString()}`, {
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
    state.activeSceneCount = Number(data.activeSceneCount || 0)
    state.totalBonusCount = Number(data.totalBonusCount || 0)
    state.totalPenaltyCount = Number(data.totalPenaltyCount || 0)
    state.avgDimensionWeight = String(data.avgDimensionWeight || '0')
    state.updatedAt = data.updatedAt || ''
    if (state.items.length > 0) {
      const current = selected.value
      const hit = current
        ? state.items.find((item) => item.sceneCode === current.sceneCode)
        : state.items[0]
      selected.value = hit || state.items[0]
      mapSelectedToForm(selected.value)
    } else {
      selected.value = null
    }
  } catch (error) {
    errorMsg.value = error.message || '加载分发策略失败'
  } finally {
    loading.value = false
  }
}

function selectItem(item) {
  selected.value = item
  mapSelectedToForm(item)
  successMsg.value = ''
}

function dimensionWeightSum() {
  return form.dimensions.reduce((sum, item) => sum + Number(item.weight || 0), 0)
}

async function saveRule() {
  if (!canSave.value || saving.value) return
  saving.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const disclosures = form.disclosuresText
      .split('\n')
      .map((line) => line.trim())
      .filter(Boolean)
    const payload = {
      sceneCode: form.sceneCode.trim(),
      sceneName: form.sceneName.trim(),
      ruleVersion: form.ruleVersion.trim(),
      scoreFormula: form.scoreFormula.trim(),
      updateCycle: form.updateCycle.trim(),
      paidFactorDesc: form.paidFactorDesc.trim(),
      dimensions: form.dimensions.map((d) => ({
        code: d.code.trim(),
        name: d.name.trim(),
        weight: Number(d.weight || 0),
        description: d.description.trim(),
        scoreMethod: d.scoreMethod.trim(),
        dataSource: d.dataSource.trim()
      })),
      bonuses: form.bonuses.map((b) => ({
        code: b.code.trim(),
        name: b.name.trim(),
        scoreChange: b.scoreChange.trim(),
        trigger: b.trigger.trim(),
        cap: b.cap.trim()
      })),
      penalties: form.penalties.map((p) => ({
        code: p.code.trim(),
        name: p.name.trim(),
        scoreChange: p.scoreChange.trim(),
        trigger: p.trigger.trim(),
        recovery: p.recovery.trim()
      })),
      disclosures,
      operator: form.operator.trim(),
      remark: form.remark.trim() || null
    }
    const resp = await fetch(`/api/admin/dispatch-strategy/rules/${encodeURIComponent(form.sceneCode.trim())}`, {
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
    successMsg.value = '策略保存成功'
    await loadData()
  } catch (error) {
    errorMsg.value = error.message || '保存失败'
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
  <main class="a06-page">
    <section class="card hero">
      <h1>A06 分发策略配置页</h1>
      <p>在管理端统一维护分发策略规则版本、维度权重、加减分机制与公开说明，确保策略可配置、可追溯。</p>
    </section>

    <section class="card">
      <h2>查询筛选</h2>
      <div class="filters">
        <label>
          Admin Token
          <input v-model="query.adminToken" placeholder="请输入X-Admin-Token" />
        </label>
        <label>
          场景编码
          <input v-model="query.sceneCode" placeholder="如 MERCHANT_LEAD" />
        </label>
        <label>
          关键词
          <input v-model="query.keyword" placeholder="场景名/版本/公式" />
        </label>
        <label>
          是否启用
          <select v-model="query.enabled">
            <option value="">全部</option>
            <option value="true">仅启用</option>
          </select>
        </label>
      </div>
      <div class="actions">
        <button class="btn btn--primary" :disabled="!canLoad || loading" @click="loadData">
          {{ loading ? '加载中...' : '查询策略' }}
        </button>
      </div>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
      <p v-if="successMsg" class="ok">{{ successMsg }}</p>
    </section>

    <section class="card">
      <h2>配置概览</h2>
      <div class="kpi-grid">
        <article class="kpi"><span>策略场景数</span><strong>{{ state.total }}</strong></article>
        <article class="kpi"><span>启用场景</span><strong>{{ state.activeSceneCount }}</strong></article>
        <article class="kpi"><span>加分规则数</span><strong>{{ state.totalBonusCount }}</strong></article>
        <article class="kpi"><span>减分规则数</span><strong>{{ state.totalPenaltyCount }}</strong></article>
        <article class="kpi"><span>平均维度权重</span><strong>{{ state.avgDimensionWeight }}</strong></article>
        <article class="kpi"><span>更新时间</span><strong>{{ state.updatedAt || '-' }}</strong></article>
      </div>
    </section>

    <section class="card">
      <div class="head">
        <h2>策略列表</h2>
        <span class="tip">共 {{ state.total }} 条</span>
      </div>
      <p v-if="loading">列表加载中...</p>
      <p v-else-if="state.items.length === 0">暂无策略</p>
      <ul v-else class="rule-list">
        <li
          v-for="item in state.items"
          :key="item.sceneCode"
          :class="['rule-item', selected?.sceneCode === item.sceneCode ? 'active' : '']"
          @click="selectItem(item)"
        >
          <div class="line">
            <strong>{{ item.sceneName }}</strong>
            <span class="badge">{{ item.sceneCode }}</span>
            <span class="badge">{{ item.ruleVersion }}</span>
          </div>
          <p class="muted">维度{{ item.dimensionCount }}｜加分{{ item.bonusCount }}｜减分{{ item.penaltyCount }}</p>
          <p class="muted">{{ item.scoreFormula }}</p>
        </li>
      </ul>
      <div class="pager">
        <button class="btn" :disabled="query.page <= 1 || loading" @click="prevPage">上一页</button>
        <span>第 {{ query.page }} / {{ totalPages }} 页</span>
        <button class="btn" :disabled="query.page >= totalPages || loading" @click="nextPage">下一页</button>
      </div>
    </section>

    <section class="card" v-if="selected">
      <h2>策略编辑</h2>
      <div class="form-grid">
        <label>场景编码<input v-model="form.sceneCode" readonly /></label>
        <label>规则名称<input v-model="form.sceneName" /></label>
        <label>规则版本<input v-model="form.ruleVersion" /></label>
        <label>更新周期<input v-model="form.updateCycle" /></label>
        <label class="span-2">评分公式<input v-model="form.scoreFormula" /></label>
        <label class="span-2">付费因子说明<input v-model="form.paidFactorDesc" /></label>
      </div>

      <h3>维度配置（权重合计：{{ dimensionWeightSum() }}）</h3>
      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>编码</th>
              <th>名称</th>
              <th>权重</th>
              <th>描述</th>
              <th>评分方式</th>
              <th>数据源</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(item, idx) in form.dimensions" :key="`${item.code}-${idx}`">
              <td><input v-model="item.code" /></td>
              <td><input v-model="item.name" /></td>
              <td><input v-model="item.weight" type="number" /></td>
              <td><input v-model="item.description" /></td>
              <td><input v-model="item.scoreMethod" /></td>
              <td><input v-model="item.dataSource" /></td>
            </tr>
          </tbody>
        </table>
      </div>

      <h3>加分规则</h3>
      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>编码</th>
              <th>名称</th>
              <th>分值变化</th>
              <th>触发条件</th>
              <th>上限</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(item, idx) in form.bonuses" :key="`${item.code}-${idx}`">
              <td><input v-model="item.code" /></td>
              <td><input v-model="item.name" /></td>
              <td><input v-model="item.scoreChange" /></td>
              <td><input v-model="item.trigger" /></td>
              <td><input v-model="item.cap" /></td>
            </tr>
          </tbody>
        </table>
      </div>

      <h3>减分规则</h3>
      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>编码</th>
              <th>名称</th>
              <th>分值变化</th>
              <th>触发条件</th>
              <th>恢复说明</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(item, idx) in form.penalties" :key="`${item.code}-${idx}`">
              <td><input v-model="item.code" /></td>
              <td><input v-model="item.name" /></td>
              <td><input v-model="item.scoreChange" /></td>
              <td><input v-model="item.trigger" /></td>
              <td><input v-model="item.recovery" /></td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="form-grid">
        <label class="span-2">
          公开说明（每行一条）
          <textarea v-model="form.disclosuresText" rows="4"></textarea>
        </label>
        <label>
          操作人
          <input v-model="form.operator" />
        </label>
        <label>
          备注
          <input v-model="form.remark" placeholder="可选" />
        </label>
      </div>

      <div class="actions">
        <button class="btn btn--primary" :disabled="!canSave || saving" @click="saveRule">
          {{ saving ? '保存中...' : '保存策略' }}
        </button>
      </div>
    </section>
  </main>
</template>

<style scoped>
.a06-page {
  max-width: 1220px;
  margin: 0 auto;
  padding: 20px 16px 32px;
  display: grid;
  gap: 14px;
}

.card {
  border: 1px solid #fde7cc;
  border-radius: 12px;
  background: #fff;
  padding: 14px;
}

.hero {
  background: linear-gradient(135deg, #fff7ed, #ffedd5);
}

.hero h1 {
  margin: 0 0 6px;
  color: #b45309;
}

.hero p {
  margin: 0;
  color: #9a3412;
}

.filters,
.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 10px;
}

.span-2 {
  grid-column: span 2;
}

label {
  display: grid;
  gap: 6px;
  font-size: 13px;
  color: #6b7280;
}

input,
select,
textarea {
  border: 1px solid #fdba74;
  border-radius: 8px;
  padding: 8px 10px;
  font-size: 14px;
  outline: none;
}

input:focus,
select:focus,
textarea:focus {
  border-color: #f97316;
}

.actions {
  margin-top: 10px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.btn {
  border: 1px solid #fb923c;
  background: #fff;
  color: #9a3412;
  border-radius: 8px;
  padding: 8px 12px;
  cursor: pointer;
}

.btn--primary {
  background: #f97316;
  color: #fff;
}

.btn:disabled {
  cursor: not-allowed;
  opacity: 0.6;
}

.error {
  margin-top: 8px;
  color: #dc2626;
}

.ok {
  margin-top: 8px;
  color: #059669;
}

.kpi-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  gap: 10px;
}

.kpi {
  border: 1px solid #fed7aa;
  border-radius: 10px;
  padding: 10px;
  background: #fff7ed;
  display: grid;
  gap: 6px;
}

.kpi span {
  font-size: 12px;
  color: #9a3412;
}

.kpi strong {
  font-size: 18px;
  color: #ea580c;
}

.head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.tip {
  color: #6b7280;
  font-size: 12px;
}

.rule-list {
  margin: 10px 0 0;
  padding: 0;
  list-style: none;
  display: grid;
  gap: 10px;
}

.rule-item {
  border: 1px solid #fed7aa;
  border-radius: 10px;
  padding: 10px;
  background: #fff;
  cursor: pointer;
}

.rule-item.active {
  border-color: #f97316;
  box-shadow: 0 0 0 2px rgba(249, 115, 22, 0.12);
}

.line {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.badge {
  border: 1px solid #f97316;
  color: #c2410c;
  border-radius: 999px;
  padding: 2px 8px;
  font-size: 12px;
}

.muted {
  margin: 6px 0 0;
  color: #6b7280;
  font-size: 13px;
}

.pager {
  margin-top: 10px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.table-wrap {
  overflow-x: auto;
  margin-top: 8px;
}

table {
  width: 100%;
  border-collapse: collapse;
  min-width: 840px;
}

th,
td {
  border-bottom: 1px solid #f3f4f6;
  text-align: left;
  padding: 8px;
  font-size: 13px;
}

th {
  color: #374151;
  background: #fffbf5;
}
</style>
