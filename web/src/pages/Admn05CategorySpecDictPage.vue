<script setup>
import { computed, onMounted, reactive, ref } from 'vue'

const loading = ref(false)
const submitting = ref(false)
const errorMsg = ref('')
const successMsg = ref('')
const selected = ref(null)

const query = reactive({
  adminToken: 'test-admin-token',
  keyword: '',
  status: '',
  sceneCode: '',
  page: 1,
  pageSize: 10
})

const state = reactive({
  records: [],
  total: 0,
  page: 1,
  pageSize: 10,
  activeCount: 0,
  disabledCount: 0
})

const form = reactive({
  categoryCode: '',
  categoryName: '',
  specName: '',
  specValue: '',
  sceneCode: 'BUYER_INQUIRY',
  status: 'ACTIVE',
  sortNo: 100,
  remark: '',
  operator: 'admn05-admin'
})

const canLoad = computed(() => query.adminToken.trim().length > 0)
const canSubmit = computed(
  () =>
    form.categoryCode.trim() &&
    form.categoryName.trim() &&
    form.specName.trim() &&
    form.specValue.trim() &&
    form.sceneCode.trim()
)
const totalPages = computed(() => Math.max(Math.ceil(state.total / query.pageSize), 1))

const sceneOptions = [
  { value: '', label: '全部场景' },
  { value: 'BUYER_INQUIRY', label: '买家询价' },
  { value: 'MERCHANT_QUOTE', label: '商家报价' },
  { value: 'RISK_CONTROL', label: '风控审核' }
]

const statusOptions = [
  { value: '', label: '全部状态' },
  { value: 'ACTIVE', label: '启用' },
  { value: 'DISABLED', label: '禁用' }
]

async function loadList() {
  if (!canLoad.value || loading.value) return
  loading.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const params = new URLSearchParams()
    if (query.keyword.trim()) params.set('keyword', query.keyword.trim())
    if (query.status) params.set('status', query.status)
    if (query.sceneCode) params.set('sceneCode', query.sceneCode)
    params.set('page', String(query.page))
    params.set('pageSize', String(query.pageSize))
    const resp = await fetch(`/api/admin/category-spec-dicts?${params.toString()}`, {
      headers: { 'X-Admin-Token': query.adminToken.trim() }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `词库列表加载失败(${resp.status})`)
    }
    const data = json.data || {}
    state.records = Array.isArray(data.records) ? data.records : []
    state.total = Number(data.total || 0)
    state.page = Number(data.page || query.page)
    state.pageSize = Number(data.pageSize || query.pageSize)
    state.activeCount = Number(data.activeCount || 0)
    state.disabledCount = Number(data.disabledCount || 0)
    if (selected.value && !state.records.some((item) => item.dictId === selected.value.dictId)) {
      selected.value = null
    }
  } catch (error) {
    errorMsg.value = error.message || '词库列表加载失败'
  } finally {
    loading.value = false
  }
}

async function openDetail(row) {
  if (!row?.dictId || loading.value) return
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const resp = await fetch(`/api/admin/category-spec-dicts/${row.dictId}`, {
      headers: { 'X-Admin-Token': query.adminToken.trim() }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `词库详情加载失败(${resp.status})`)
    }
    selected.value = json.data || null
  } catch (error) {
    errorMsg.value = error.message || '词库详情加载失败'
  }
}

async function upsertDictItem() {
  if (!canSubmit.value || submitting.value) return
  submitting.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      categoryCode: form.categoryCode.trim().toUpperCase(),
      categoryName: form.categoryName.trim(),
      specName: form.specName.trim(),
      specValue: form.specValue.trim(),
      sceneCode: form.sceneCode,
      status: form.status,
      sortNo: Number(form.sortNo || 0),
      remark: form.remark.trim() || null,
      operator: form.operator.trim() || 'admn05-admin'
    }
    const resp = await fetch('/api/admin/category-spec-dicts', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Admin-Token': query.adminToken.trim()
      },
      body: JSON.stringify(payload)
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `词库保存失败(${resp.status})`)
    }
    selected.value = json.data || null
    successMsg.value = '词库项已保存'
    await loadList()
  } catch (error) {
    errorMsg.value = error.message || '词库保存失败'
  } finally {
    submitting.value = false
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
  loadList()
})
</script>

<template>
  <main class="admn05-page">
    <section class="card hero">
      <h1>ADM-N05 类目/规格词库管理</h1>
      <p>统一维护类目与规格词库，覆盖买家询价、商家报价与风控审核场景，支撑标准化录入与检索。</p>
    </section>

    <section class="card">
      <div class="filters">
        <label>
          Admin Token
          <input v-model="query.adminToken" placeholder="请输入X-Admin-Token" />
        </label>
        <label>
          状态
          <select v-model="query.status">
            <option v-for="op in statusOptions" :key="op.value" :value="op.value">{{ op.label }}</option>
          </select>
        </label>
        <label>
          场景
          <select v-model="query.sceneCode">
            <option v-for="op in sceneOptions" :key="op.value" :value="op.value">{{ op.label }}</option>
          </select>
        </label>
        <label class="span-2">
          关键词
          <input v-model="query.keyword" placeholder="dictId/类目/规格/备注 模糊匹配" />
        </label>
      </div>
      <div class="actions">
        <button class="btn btn--primary" :disabled="!canLoad || loading" @click="loadList">
          {{ loading ? '加载中...' : '查询词库' }}
        </button>
      </div>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
      <p v-if="successMsg" class="ok">{{ successMsg }}</p>
    </section>

    <section class="stats">
      <article class="stat"><span>词库总数</span><strong>{{ state.total }}</strong></article>
      <article class="stat"><span>启用</span><strong>{{ state.activeCount }}</strong></article>
      <article class="stat"><span>禁用</span><strong>{{ state.disabledCount }}</strong></article>
      <article class="stat"><span>当前页</span><strong>{{ query.page }}</strong></article>
    </section>

    <section class="card">
      <h2>词库列表</h2>
      <p v-if="loading">加载中...</p>
      <p v-else-if="state.records.length === 0">暂无词库记录</p>
      <ul v-else class="list">
        <li v-for="row in state.records" :key="row.dictId" class="item">
          <div>
            <h3>{{ row.categoryName }} / {{ row.specName }}</h3>
            <p>词库ID：{{ row.dictId }}</p>
            <p>类目编码：{{ row.categoryCode }} ｜ 规格值：{{ row.specValue }}</p>
            <p>场景：{{ row.sceneText }} ｜ 状态：{{ row.statusText }} ｜ 排序：{{ row.sortNo }}</p>
            <p>更新人：{{ row.updatedBy || '-' }} ｜ 更新时间：{{ row.updatedAt || '-' }}</p>
          </div>
          <button class="btn" @click="openDetail(row)">查看详情</button>
        </li>
      </ul>
      <div class="actions" v-if="state.total > 0">
        <button class="btn" :disabled="query.page <= 1 || loading" @click="prevPage">上一页</button>
        <button class="btn" :disabled="query.page >= totalPages || loading" @click="nextPage">下一页</button>
        <span>第 {{ query.page }} 页 / 共 {{ totalPages }} 页</span>
      </div>
    </section>

    <section class="card">
      <h2>新增/更新词库项</h2>
      <div class="form-grid">
        <label>
          类目编码
          <input v-model="form.categoryCode" placeholder="示例：REBAR" />
        </label>
        <label>
          类目名称
          <input v-model="form.categoryName" placeholder="示例：螺纹钢" />
        </label>
        <label>
          规格名称
          <input v-model="form.specName" placeholder="示例：规格" />
        </label>
        <label>
          规格值
          <input v-model="form.specValue" placeholder="示例：HRB400E Φ20*12m" />
        </label>
        <label>
          场景
          <select v-model="form.sceneCode">
            <option v-for="op in sceneOptions.filter((item) => item.value)" :key="op.value" :value="op.value">
              {{ op.label }}
            </option>
          </select>
        </label>
        <label>
          状态
          <select v-model="form.status">
            <option value="ACTIVE">启用</option>
            <option value="DISABLED">禁用</option>
          </select>
        </label>
        <label>
          排序号
          <input v-model.number="form.sortNo" type="number" min="0" />
        </label>
        <label>
          操作人
          <input v-model="form.operator" placeholder="admn05-admin" />
        </label>
        <label class="span-2">
          备注
          <textarea v-model="form.remark" rows="2" placeholder="场景说明、来源、停用原因等" />
        </label>
      </div>
      <div class="actions">
        <button class="btn btn--primary" :disabled="!canSubmit || submitting" @click="upsertDictItem">
          {{ submitting ? '提交中...' : '保存词库项' }}
        </button>
      </div>
    </section>

    <section class="card" v-if="selected">
      <h2>词库详情</h2>
      <p>词库ID：{{ selected.dictId }}</p>
      <p>类目：{{ selected.categoryName }}（{{ selected.categoryCode }}）</p>
      <p>规格：{{ selected.specName }} ｜ {{ selected.specValue }}</p>
      <p>场景：{{ selected.sceneText }}（{{ selected.sceneCode }}）</p>
      <p>状态：{{ selected.statusText }} ｜ 排序：{{ selected.sortNo }}</p>
      <p>备注：{{ selected.remark || '-' }}</p>
      <p>可用动作：{{ (selected.availableActions || []).join(' / ') }}</p>
      <p>更新人：{{ selected.updatedBy || '-' }} ｜ 更新时间：{{ selected.updatedAt || '-' }}</p>
    </section>
  </main>
</template>

<style scoped>
.admn05-page {
  max-width: 1100px;
  margin: 0 auto;
  padding: 14px;
}
.card {
  background: #fff;
  border: 1px solid #ececec;
  border-radius: 12px;
  padding: 14px;
  margin-bottom: 12px;
}
.hero {
  background: linear-gradient(135deg, #fff7ed, #ffedd5);
}
.hero h1 {
  margin: 0 0 8px;
}
.hero p {
  margin: 0;
  color: #4b5563;
}
.filters,
.form-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
}
.span-2 {
  grid-column: span 2;
}
label {
  display: grid;
  gap: 6px;
}
input,
select,
textarea {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 9px 10px;
  font: inherit;
}
.actions {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 10px;
  flex-wrap: wrap;
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
.stats {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 10px;
  margin-bottom: 12px;
}
.stat {
  background: #fff;
  border: 1px solid #ececec;
  border-radius: 10px;
  padding: 10px;
  display: grid;
  gap: 6px;
}
.stat span {
  color: #6b7280;
}
.stat strong {
  font-size: 24px;
}
.list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: grid;
  gap: 10px;
}
.item {
  border: 1px solid #ececec;
  border-radius: 10px;
  padding: 10px;
  display: flex;
  justify-content: space-between;
  gap: 10px;
}
.item h3 {
  margin: 0 0 6px;
}
.item p {
  margin: 2px 0;
  color: #4b5563;
}
@media (max-width: 860px) {
  .filters,
  .form-grid,
  .stats {
    grid-template-columns: 1fr;
  }
  .span-2 {
    grid-column: span 1;
  }
  .item {
    flex-direction: column;
  }
}
</style>
