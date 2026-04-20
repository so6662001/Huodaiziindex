<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const loading = ref(false)
const detailLoading = ref(false)
const createSubmitting = ref(false)
const statusSubmitting = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

const query = reactive({
  status: '',
  keyword: '',
  pageNo: 1,
  pageSize: 10
})

const list = ref([])
const total = ref(0)
const selectedAppealId = ref('')
const detail = ref(null)

const createForm = reactive({
  sceneCode: 'MERCHANT_LEAD',
  targetId: 'S001',
  appealType: 'SCORE_MISMATCH',
  appealReason: '',
  evidenceFiles: '',
  operator: 'pc-n14-ui'
})

const statusForm = reactive({
  status: 'PROCESSING',
  remark: '',
  operator: 'pc-n14-ui'
})

const token = computed(() => localStorage.getItem('N01_AUTH_TOKEN') || '')
const hasSession = computed(() => token.value.length > 0)
const canCreate = computed(() =>
  hasSession.value &&
  createForm.targetId.trim() &&
  createForm.appealReason.trim() &&
  !createSubmitting.value
)
const canUpdateStatus = computed(() =>
  hasSession.value && selectedAppealId.value && !statusSubmitting.value
)

function appealTypeText(type) {
  if (type === 'SCORE_MISMATCH') return '评分结果异议'
  if (type === 'RULE_MISREAD') return '规则解读异议'
  if (type === 'DATA_ERROR') return '数据错误异议'
  if (type === 'UNFAIR_TRAFFIC') return '流量分发不公'
  return '其他'
}

function processLogs(logs) {
  if (!logs) return []
  if (Array.isArray(logs)) return logs
  return String(logs)
    .split(',')
    .map((item) => item.trim())
    .filter(Boolean)
}

async function loadList() {
  if (!hasSession.value || loading.value) {
    if (!hasSession.value) errorMsg.value = '请先登录后查看分发异议申诉'
    return
  }
  loading.value = true
  errorMsg.value = ''
  try {
    const params = new URLSearchParams({
      pageNo: String(query.pageNo),
      pageSize: String(query.pageSize)
    })
    if (query.status) params.set('status', query.status)
    if (query.keyword.trim()) params.set('keyword', query.keyword.trim())
    const resp = await fetch(`/api/v1/auth/dispatch-appeals?${params.toString()}`, {
      headers: { 'X-Auth-Token': token.value }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `申诉列表加载失败(${resp.status})`)
    }
    const data = json.data || {}
    list.value = data.records || []
    total.value = data.total || 0
    if ((!selectedAppealId.value || !list.value.some(item => item.appealId === selectedAppealId.value)) && list.value.length > 0) {
      selectedAppealId.value = list.value[0].appealId
      await loadDetail()
    }
  } catch (error) {
    errorMsg.value = error.message || '申诉列表加载失败'
  } finally {
    loading.value = false
  }
}

async function loadDetail() {
  if (!hasSession.value || !selectedAppealId.value || detailLoading.value) return
  detailLoading.value = true
  errorMsg.value = ''
  try {
    const resp = await fetch(`/api/v1/auth/dispatch-appeals/${selectedAppealId.value}`, {
      headers: { 'X-Auth-Token': token.value }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `申诉详情加载失败(${resp.status})`)
    }
    detail.value = json.data || null
  } catch (error) {
    errorMsg.value = error.message || '申诉详情加载失败'
  } finally {
    detailLoading.value = false
  }
}

async function createAppeal() {
  if (!canCreate.value) return
  createSubmitting.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      sceneCode: createForm.sceneCode.trim(),
      targetId: createForm.targetId.trim(),
      appealType: createForm.appealType,
      appealReason: createForm.appealReason.trim(),
      evidenceFiles: createForm.evidenceFiles.trim(),
      operator: createForm.operator.trim()
    }
    const resp = await fetch('/api/v1/auth/dispatch-appeals', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Auth-Token': token.value
      },
      body: JSON.stringify(payload)
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `提交申诉失败(${resp.status})`)
    }
    const created = json.data || null
    successMsg.value = '分发异议申诉已提交'
    createForm.appealReason = ''
    createForm.evidenceFiles = ''
    selectedAppealId.value = created?.appealId || ''
    await loadList()
  } catch (error) {
    errorMsg.value = error.message || '提交申诉失败'
  } finally {
    createSubmitting.value = false
  }
}

async function updateStatus() {
  if (!canUpdateStatus.value) return
  statusSubmitting.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      status: statusForm.status,
      remark: statusForm.remark.trim() || null,
      operator: statusForm.operator.trim()
    }
    const resp = await fetch(`/api/v1/auth/dispatch-appeals/${selectedAppealId.value}/status`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Auth-Token': token.value
      },
      body: JSON.stringify(payload)
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `更新状态失败(${resp.status})`)
    }
    successMsg.value = '申诉状态已更新'
    statusForm.remark = ''
    detail.value = json.data || null
    await loadList()
  } catch (error) {
    errorMsg.value = error.message || '更新状态失败'
  } finally {
    statusSubmitting.value = false
  }
}

function pickAppeal(appealId) {
  selectedAppealId.value = appealId
  loadDetail()
}

function goCreditScoreDetail() {
  router.push('/account/credit-score-detail')
}

function goHome() {
  router.push('/')
}

onMounted(async () => {
  await loadList()
})
</script>

<template>
  <main class="n14-page">
    <section class="card hero">
      <h1>PC-N14 分发异议申诉页</h1>
      <p>面向线索分发争议场景，提交证据并跟踪平台处理进度，保障分发规则透明与申诉闭环。</p>
      <div class="hero-actions">
        <button class="btn" @click="goCreditScoreDetail">前往信用评分明细</button>
        <button class="btn" @click="goHome">返回首页</button>
      </div>
    </section>

    <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
    <p v-if="successMsg" class="ok">{{ successMsg }}</p>

    <section class="layout">
      <article class="card left">
        <div class="head">
          <h2>申诉提交</h2>
          <button class="btn" :disabled="loading" @click="loadList">{{ loading ? '刷新中...' : '刷新' }}</button>
        </div>
        <div class="form-grid">
          <input v-model="createForm.sceneCode" placeholder="场景编码（MERCHANT_LEAD）" />
          <input v-model="createForm.targetId" placeholder="争议对象ID（如 S001）" />
          <select v-model="createForm.appealType">
            <option value="SCORE_MISMATCH">评分结果异议</option>
            <option value="RULE_MISREAD">规则解读异议</option>
            <option value="DATA_ERROR">数据错误异议</option>
            <option value="UNFAIR_TRAFFIC">流量分发不公</option>
            <option value="OTHER">其他</option>
          </select>
          <input :value="appealTypeText(createForm.appealType)" disabled />
          <textarea
            v-model="createForm.appealReason"
            rows="4"
            class="span-2"
            placeholder="申诉原因说明（必填）"
          ></textarea>
          <textarea
            v-model="createForm.evidenceFiles"
            rows="4"
            class="span-2"
            placeholder="证据URL，逗号分隔（可选）"
          ></textarea>
        </div>
        <div class="actions">
          <button class="btn btn--primary" :disabled="!canCreate" @click="createAppeal">
            {{ createSubmitting ? '提交中...' : '提交申诉' }}
          </button>
        </div>
      </article>

      <article class="card right">
        <div class="head">
          <h2>申诉记录</h2>
          <button class="btn" :disabled="loading" @click="loadList">{{ loading ? '刷新中...' : '刷新' }}</button>
        </div>
        <div class="filters">
          <select v-model="query.status">
            <option value="">全部状态</option>
            <option value="SUBMITTED">已提交</option>
            <option value="PROCESSING">处理中</option>
            <option value="RESOLVED">已解决</option>
            <option value="CLOSED">已关闭</option>
          </select>
          <input v-model="query.keyword" placeholder="按申诉单号/标题/场景搜索" />
          <button class="btn" :disabled="loading" @click="loadList">筛选</button>
        </div>
        <p class="tip">共 {{ total }} 条申诉</p>
        <ul class="list">
          <li
            v-for="item in list"
            :key="item.appealId"
            :class="{ active: item.appealId === selectedAppealId }"
            @click="pickAppeal(item.appealId)"
          >
            <div class="line-1">
              <strong>{{ item.appealId }}</strong>
              <span>{{ item.statusText }}</span>
            </div>
            <p>场景：{{ item.sceneName }} ｜ 商家：{{ item.merchantName }}</p>
            <p>原因：{{ item.issueTypeText }} ｜ 备注：{{ item.latestRemark || '-' }}</p>
            <p class="muted">更新时间：{{ item.updatedAt || '-' }}</p>
          </li>
        </ul>

        <section v-if="detail" class="block">
          <div class="head">
            <h3>申诉详情</h3>
            <button class="btn" :disabled="detailLoading" @click="loadDetail">{{ detailLoading ? '加载中...' : '刷新详情' }}</button>
          </div>
          <div class="meta-grid">
            <p>申诉单号：{{ detail.appealId }}</p>
            <p>状态：{{ detail.statusText }}（{{ detail.status }}）</p>
            <p>场景：{{ detail.sceneName }}（{{ detail.sceneCode }}）</p>
            <p>争议对象ID：{{ detail.disputedLeadId || '-' }}</p>
            <p>关联规则版本：{{ detail.disputedRuleCode || '-' }}</p>
            <p>原因类型：{{ detail.reasonTypeText }}</p>
            <p>商家：{{ detail.merchantName }}</p>
            <p>最近备注：{{ detail.latestRemark || '-' }}</p>
            <p>创建时间：{{ detail.createdAt || '-' }}</p>
            <p>更新时间：{{ detail.updatedAt || '-' }}</p>
          </div>
          <p class="desc">{{ detail.reasonDescription }}</p>
          <section class="sub-block">
            <h4>证据材料</h4>
            <ul class="evidence-list">
              <li v-if="!detail.evidenceFiles" class="muted">暂无证据</li>
              <li v-else>{{ detail.evidenceFiles }}</li>
            </ul>
          </section>
          <section class="sub-block">
            <h4>处理时间线</h4>
            <ul class="timeline">
              <li v-for="(log, idx) in processLogs(detail.processLogs)" :key="`${idx}-${log}`">
                {{ log }}
              </li>
            </ul>
          </section>
          <section class="sub-block">
            <h4>状态处理</h4>
            <div class="status-form">
              <select v-model="statusForm.status">
                <option value="SUBMITTED">已提交</option>
                <option value="PROCESSING">处理中</option>
                <option value="APPROVED">审核通过</option>
                <option value="REJECTED">审核驳回</option>
                <option value="CLOSED">标记已关闭</option>
              </select>
              <input v-model="statusForm.remark" placeholder="处理备注（可选）" />
              <button class="btn btn--primary" :disabled="!canUpdateStatus" @click="updateStatus">
                {{ statusSubmitting ? '提交中...' : '更新状态' }}
              </button>
            </div>
          </section>
        </section>
      </article>
    </section>
  </main>
</template>

<style scoped>
.n14-page {
  max-width: 1240px;
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
  grid-template-columns: 1fr 1.2fr;
  gap: 14px;
}
.head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.form-grid {
  margin-top: 10px;
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 8px;
}
.filters {
  margin-top: 10px;
  display: grid;
  grid-template-columns: 140px 1fr auto;
  gap: 8px;
}
.actions {
  margin-top: 10px;
}
.span-2 {
  grid-column: span 2;
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
.block {
  margin-top: 12px;
  border: 1px solid #f3f4f6;
  border-radius: 10px;
  padding: 12px;
}
.sub-block {
  margin-top: 10px;
  border-top: 1px dashed #e5e7eb;
  padding-top: 10px;
}
.sub-block h4 {
  margin: 0 0 8px;
}
.meta-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 6px 10px;
}
.desc {
  margin: 10px 0 0;
  color: #374151;
}
.evidence-list,
.timeline {
  margin: 0;
  padding-left: 18px;
  display: grid;
  gap: 6px;
}
.status-form {
  display: grid;
  grid-template-columns: 150px 1fr auto;
  gap: 8px;
}
.btn {
  height: 34px;
  border-radius: 8px;
  border: 1px solid #d1d5db;
  background: #fff;
  padding: 0 12px;
  cursor: pointer;
}
.btn--primary {
  border-color: #f57c00;
  background: #f57c00;
  color: #fff;
}
input,
select,
textarea {
  border: 1px solid #d1d5db;
  border-radius: 8px;
  padding: 8px 10px;
  font: inherit;
}
.tip,
.muted {
  color: #6b7280;
}
.ok {
  color: #166534;
}
.error {
  color: #b91c1c;
}
@media (max-width: 980px) {
  .layout,
  .meta-grid,
  .form-grid,
  .filters,
  .status-form {
    grid-template-columns: 1fr;
  }
  .span-2 {
    grid-column: span 1;
  }
}
</style>
