<script setup>
import { computed, onMounted, reactive, ref } from 'vue'

const loading = ref(false)
const submitting = ref(false)
const errorMsg = ref('')
const successMsg = ref('')
const selected = ref(null)

const query = reactive({
  adminToken: 'test-admin-token',
  source: '',
  qualityStatus: '',
  riskLevel: '',
  reviewer: '',
  keyword: '',
  page: 1,
  pageSize: 10
})

const state = reactive({
  records: [],
  total: 0,
  page: 1,
  pageSize: 10,
  passCount: 0,
  rejectCount: 0,
  pendingCount: 0
})

const reviewForm = reactive({
  qualityStatus: 'PASS',
  riskLevel: 'LOW',
  qualityScore: 90,
  ruleCode: '',
  reviewRemark: '',
  reviewer: 'admn06-reviewer'
})

const canLoad = computed(() => query.adminToken.trim().length > 0)
const canSubmit = computed(() => selected.value && reviewForm.qualityStatus.trim() && reviewForm.reviewer.trim())
const totalPages = computed(() => Math.max(Math.ceil(state.total / query.pageSize), 1))

const sourceOptions = [
  { value: '', label: '全部来源' },
  { value: 'INQUIRY', label: '询价线索' },
  { value: 'SITE_AD', label: '广告线索' }
]

const statusOptions = [
  { value: '', label: '全部质检状态' },
  { value: 'PENDING', label: '待审核' },
  { value: 'PASS', label: '通过' },
  { value: 'REJECT', label: '驳回' },
  { value: 'RECHECK', label: '待复检' }
]

const riskOptions = [
  { value: '', label: '全部风险等级' },
  { value: 'LOW', label: '低风险' },
  { value: 'MEDIUM', label: '中风险' },
  { value: 'HIGH', label: '高风险' }
]

async function loadList() {
  if (!canLoad.value || loading.value) return
  loading.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const params = new URLSearchParams()
    if (query.source) params.set('source', query.source)
    if (query.qualityStatus) params.set('qualityStatus', query.qualityStatus)
    if (query.riskLevel) params.set('riskLevel', query.riskLevel)
    if (query.reviewer.trim()) params.set('reviewer', query.reviewer.trim())
    if (query.keyword.trim()) params.set('keyword', query.keyword.trim())
    params.set('page', String(query.page))
    params.set('pageSize', String(query.pageSize))
    const resp = await fetch(`/api/admin/lead-quality?${params.toString()}`, {
      headers: { 'X-Admin-Token': query.adminToken.trim() }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `线索质检加载失败(${resp.status})`)
    }
    const data = json.data || {}
    state.records = Array.isArray(data.records) ? data.records : []
    state.total = Number(data.total || 0)
    state.page = Number(data.page || query.page)
    state.pageSize = Number(data.pageSize || query.pageSize)
    state.passCount = Number(data.passCount || 0)
    state.rejectCount = Number(data.rejectCount || 0)
    state.pendingCount = Number(data.pendingCount || 0)
    if (selected.value && !state.records.some((item) => item.qualityId === selected.value.qualityId)) {
      selected.value = null
    }
  } catch (error) {
    errorMsg.value = error.message || '线索质检加载失败'
  } finally {
    loading.value = false
  }
}

async function openDetail(row) {
  if (!row?.qualityId || loading.value) return
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const resp = await fetch(`/api/admin/lead-quality/${row.qualityId}`, {
      headers: { 'X-Admin-Token': query.adminToken.trim() }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `线索质检详情加载失败(${resp.status})`)
    }
    selected.value = json.data || null
    reviewForm.qualityStatus = selected.value?.qualityStatus || 'PASS'
    reviewForm.riskLevel = selected.value?.riskLevel || 'LOW'
    reviewForm.qualityScore = Number(selected.value?.qualityScore || 80)
    reviewForm.ruleCode = selected.value?.latestIssueTag || ''
    reviewForm.reviewRemark = ''
  } catch (error) {
    errorMsg.value = error.message || '线索质检详情加载失败'
  }
}

async function submitReview() {
  if (!canSubmit.value || submitting.value) return
  submitting.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      qualityStatus: reviewForm.qualityStatus,
      riskLevel: reviewForm.riskLevel || null,
      qualityScore: Number(reviewForm.qualityScore || 0),
      ruleCode: reviewForm.ruleCode.trim() || null,
      reviewRemark: reviewForm.reviewRemark.trim() || null,
      reviewer: reviewForm.reviewer.trim()
    }
    const resp = await fetch(`/api/admin/lead-quality/${selected.value.qualityId}/review`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'X-Admin-Token': query.adminToken.trim()
      },
      body: JSON.stringify(payload)
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `质检复核失败(${resp.status})`)
    }
    selected.value = json.data || null
    successMsg.value = '线索质检复核完成'
    await loadList()
  } catch (error) {
    errorMsg.value = error.message || '质检复核失败'
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
  <main class="admn06-page">
    <section class="card hero">
      <h1>ADM-N06 线索质检中心</h1>
      <p>统一治理询价线索与广告线索的质检评分、风险标签与复核动作，提升线索分发质量与成交效率。</p>
    </section>

    <section class="card">
      <div class="filters">
        <label>
          Admin Token
          <input v-model="query.adminToken" placeholder="请输入X-Admin-Token" />
        </label>
        <label>
          来源
          <select v-model="query.source">
            <option v-for="op in sourceOptions" :key="op.value" :value="op.value">{{ op.label }}</option>
          </select>
        </label>
        <label>
          质检状态
          <select v-model="query.qualityStatus">
            <option v-for="op in statusOptions" :key="op.value" :value="op.value">{{ op.label }}</option>
          </select>
        </label>
        <label>
          风险等级
          <select v-model="query.riskLevel">
            <option v-for="op in riskOptions" :key="op.value" :value="op.value">{{ op.label }}</option>
          </select>
        </label>
        <label>
          复核人
          <input v-model="query.reviewer" placeholder="reviewer 模糊匹配" />
        </label>
        <label class="span-2">
          关键词
          <input v-model="query.keyword" placeholder="qualityId/leadId/leadNo/问题标签" />
        </label>
      </div>
      <div class="actions">
        <button class="btn btn--primary" :disabled="!canLoad || loading" @click="loadList">
          {{ loading ? '加载中...' : '查询质检线索' }}
        </button>
      </div>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
      <p v-if="successMsg" class="ok">{{ successMsg }}</p>
    </section>

    <section class="stats">
      <article class="stat"><span>质检总数</span><strong>{{ state.total }}</strong></article>
      <article class="stat"><span>通过</span><strong>{{ state.passCount }}</strong></article>
      <article class="stat"><span>驳回</span><strong>{{ state.rejectCount }}</strong></article>
      <article class="stat"><span>待处理</span><strong>{{ state.pendingCount }}</strong></article>
    </section>

    <section class="card">
      <h2>质检列表</h2>
      <p v-if="loading">加载中...</p>
      <p v-else-if="state.records.length === 0">暂无质检记录</p>
      <ul v-else class="list">
        <li v-for="row in state.records" :key="row.qualityId" class="item">
          <div>
            <h3>{{ row.sourceText }} / {{ row.leadNo }}</h3>
            <p>质检ID：{{ row.qualityId }} ｜ 线索ID：{{ row.leadId }}</p>
            <p>企业：{{ row.companyName }} ｜ 城市：{{ row.city }}</p>
            <p>状态：{{ row.qualityStatusText }} ｜ 风险：{{ row.riskLevelText }} ｜ 分数：{{ row.qualityScore }}</p>
            <p>问题标签：{{ row.latestIssue || '-' }}</p>
            <p>复核人：{{ row.reviewer || '-' }} ｜ 更新时间：{{ row.updatedAt || '-' }}</p>
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

    <section class="card" v-if="selected">
      <h2>质检详情与复核</h2>
      <p>质检ID：{{ selected.qualityId }}</p>
      <p>来源：{{ selected.sourceText }} ｜ 线索编号：{{ selected.leadNo }}</p>
      <p>企业：{{ selected.companyName }} ｜ 城市：{{ selected.city }} ｜ 负责人：{{ selected.owner || '-' }}</p>
      <p>状态：{{ selected.qualityStatusText }} ｜ 风险：{{ selected.riskLevelText }} ｜ 评分：{{ selected.qualityScore }}</p>
      <p>问题数：{{ selected.issueCountText }} ｜ 最新标签：{{ selected.latestIssueTag || '-' }}</p>
      <p>问题详情：{{ selected.latestIssueDetail || '-' }}</p>
      <p>复核人：{{ selected.reviewer || '-' }} ｜ 复核意见：{{ selected.reviewComment || '-' }}</p>

      <div class="form-grid">
        <label>
          质检状态
          <select v-model="reviewForm.qualityStatus">
            <option value="PASS">PASS 通过</option>
            <option value="REJECT">REJECT 驳回</option>
            <option value="RECHECK">RECHECK 待复检</option>
            <option value="PENDING">PENDING 待审核</option>
          </select>
        </label>
        <label>
          风险等级
          <select v-model="reviewForm.riskLevel">
            <option value="LOW">LOW 低风险</option>
            <option value="MEDIUM">MEDIUM 中风险</option>
            <option value="HIGH">HIGH 高风险</option>
          </select>
        </label>
        <label>
          质检分
          <input v-model="reviewForm.qualityScore" type="number" min="0" max="100" />
        </label>
        <label>
          规则编码
          <input v-model="reviewForm.ruleCode" placeholder="示例：CONTACT_INVALID" />
        </label>
        <label>
          复核人
          <input v-model="reviewForm.reviewer" placeholder="示例：admn06-reviewer" />
        </label>
        <label class="span-2">
          复核意见
          <textarea v-model="reviewForm.reviewRemark" rows="3" placeholder="请输入复核意见" />
        </label>
      </div>
      <div class="actions">
        <button class="btn btn--primary" :disabled="!canSubmit || submitting" @click="submitReview">
          {{ submitting ? '提交中...' : '提交质检复核' }}
        </button>
      </div>
    </section>
  </main>
</template>

<style scoped>
.admn06-page {
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
  margin: 0 0 8px;
}
.item p {
  margin: 4px 0;
  color: #4b5563;
}
</style>
