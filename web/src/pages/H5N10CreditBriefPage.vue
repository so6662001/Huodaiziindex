<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const loading = ref(false)
const detailLoading = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

const state = reactive({
  pageNo: 1,
  pageSize: 10,
  total: 0,
  channel: 'H5',
  contactMobileMasked: '',
  activeScoreId: '',
  records: []
})

const selectedScoreId = ref('')
const detail = ref(null)
const gradeFilter = ref('')
const keyword = ref('')

const token = computed(() => localStorage.getItem('H5_N01_AUTH_TOKEN') || localStorage.getItem('N01_AUTH_TOKEN') || '')
const hasSession = computed(() => token.value.length > 0)

function scoreBarWidth(score) {
  const val = Number(score || 0)
  const normalized = Math.max(0, Math.min(100, val))
  return `${normalized}%`
}

function trendHeight(score) {
  const val = Number(score || 0)
  const normalized = Math.max(12, Math.min(100, val))
  return `${normalized}%`
}

function gradeClass(grade) {
  if (grade === 'A+') return 'grade-a-plus'
  if (grade === 'A' || grade === 'A-') return 'grade-a'
  if (grade === 'B+' || grade === 'B') return 'grade-b'
  return 'grade-c'
}

async function loadScores() {
  if (!hasSession.value || loading.value) {
    if (!hasSession.value) {
      errorMsg.value = '请先登录后查看信用分简报'
    }
    return
  }
  loading.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const params = new URLSearchParams({
      pageNo: String(state.pageNo),
      pageSize: String(state.pageSize)
    })
    if (gradeFilter.value) params.set('grade', gradeFilter.value)
    if (keyword.value.trim()) params.set('keyword', keyword.value.trim())
    const resp = await fetch(`/api/v1/auth/h5/credit-brief/scores?${params.toString()}`, {
      headers: { 'X-Auth-Token': token.value }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `信用分简报列表加载失败(${resp.status})`)
    }
    const data = json.data || {}
    state.pageNo = Number(data.pageNo || 1)
    state.pageSize = Number(data.pageSize || 10)
    state.total = Number(data.total || 0)
    state.channel = data.channel || 'H5'
    state.contactMobileMasked = data.contactMobileMasked || ''
    state.activeScoreId = data.activeScoreId || ''
    state.records = Array.isArray(data.records) ? data.records : []

    if (!selectedScoreId.value) {
      selectedScoreId.value = state.activeScoreId || state.records[0]?.scoreId || ''
      if (selectedScoreId.value) await loadDetail()
      return
    }
    const exists = state.records.some((item) => item.scoreId === selectedScoreId.value)
    if (!exists) {
      selectedScoreId.value = state.activeScoreId || state.records[0]?.scoreId || ''
      if (selectedScoreId.value) {
        await loadDetail()
      } else {
        detail.value = null
      }
    }
  } catch (error) {
    errorMsg.value = error.message || '信用分简报列表加载失败'
  } finally {
    loading.value = false
  }
}

async function loadDetail() {
  if (!hasSession.value || !selectedScoreId.value || detailLoading.value) return
  detailLoading.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const resp = await fetch(`/api/v1/auth/h5/credit-brief/scores/${selectedScoreId.value}`, {
      headers: { 'X-Auth-Token': token.value }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `信用分简报详情加载失败(${resp.status})`)
    }
    detail.value = json.data || null
  } catch (error) {
    errorMsg.value = error.message || '信用分简报详情加载失败'
  } finally {
    detailLoading.value = false
  }
}

function chooseScore(scoreId) {
  selectedScoreId.value = scoreId
  loadDetail()
}

function goH5Home() {
  router.push('/h5?city=唐山')
}

function goLitePay() {
  router.push('/h5/lite-pay')
}

function goQuickLogin() {
  router.push('/h5/login-quick')
}

function goMessageSettings() {
  router.push('/h5/message-settings')
}

onMounted(() => {
  loadScores()
})
</script>

<template>
  <main class="h5-n10-page">
    <section class="card hero">
      <h1>H5-N10 信用分简报页</h1>
      <p>移动端快速查看信用评分概览、风险标签、因子拆解与趋势变化。</p>
      <div class="hero-actions">
        <button class="btn" @click="goH5Home">返回H5首页</button>
        <button class="btn" @click="goLitePay">前往H5-N09轻支付</button>
        <button class="btn" @click="goMessageSettings">前往H5-N11消息设置</button>
        <button class="btn" @click="goQuickLogin">返回H5-N01快捷登录</button>
      </div>
    </section>

    <section class="card">
      <div class="head">
        <h2>信用分简报列表</h2>
        <button class="btn" :disabled="loading" @click="loadScores">{{ loading ? '刷新中...' : '刷新' }}</button>
      </div>
      <div class="filters">
        <select v-model="gradeFilter">
          <option value="">全部等级</option>
          <option value="A+">A+</option>
          <option value="A">A</option>
          <option value="A-">A-</option>
          <option value="B+">B+</option>
          <option value="B">B</option>
          <option value="C">C</option>
        </select>
        <input v-model="keyword" placeholder="按评分ID/商家名称/商家ID搜索" />
        <button class="btn" :disabled="loading" @click="loadScores">筛选</button>
      </div>
      <p class="tip">渠道：{{ state.channel }} ｜ 登录手机号：{{ state.contactMobileMasked || '-' }} ｜ 共 {{ state.total }} 条</p>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
      <ul class="list">
        <li
          v-for="item in state.records"
          :key="item.scoreId"
          :class="{ active: item.scoreId === selectedScoreId }"
          @click="chooseScore(item.scoreId)"
        >
          <div class="line-1">
            <strong>{{ item.scoreId }}</strong>
            <span>{{ item.grade }} ｜ {{ item.totalScore }}分</span>
          </div>
          <p>商家：{{ item.merchantName }}（{{ item.merchantId }}）</p>
          <p>评估周期：{{ item.periodMonth }} ｜ 排名：{{ item.rankPercent }}</p>
          <p>风险：{{ item.riskLevelText }}（{{ item.riskLevel }}）</p>
          <p>快捷动作：{{ item.quickActionText }}</p>
          <p class="muted">更新时间：{{ item.updatedAt || '-' }}</p>
        </li>
      </ul>
    </section>

    <section class="card" v-if="detail">
      <div class="head">
        <h2>信用分简报详情</h2>
        <button class="btn" :disabled="detailLoading || !selectedScoreId" @click="loadDetail">
          {{ detailLoading ? '加载中...' : '刷新详情' }}
        </button>
      </div>
      <p v-if="successMsg" class="ok">{{ successMsg }}</p>
      <div class="score-banner" :class="gradeClass(detail.grade)">
        <strong>{{ detail.totalScore }}</strong>
        <span>{{ detail.grade }} ｜ {{ detail.riskLevelText }}</span>
        <em>{{ detail.merchantName }}（{{ detail.merchantId }}）</em>
      </div>

      <div class="meta-grid">
        <p>评分ID：{{ detail.scoreId }}</p>
        <p>评估周期：{{ detail.periodMonth }}</p>
        <p>排名：{{ detail.rankPercent }}</p>
        <p>风险等级：{{ detail.riskLevelText }}（{{ detail.riskLevel }}）</p>
        <p>风险标签：{{ detail.riskTags || '-' }}</p>
        <p>模型版本：{{ detail.modelVersion || '-' }}</p>
        <p>可用动作：{{ detail.availableActions?.join(' / ') || '-' }}</p>
        <p>更新时间：{{ detail.updatedAt || '-' }}</p>
      </div>
      <p class="tip">{{ detail.tipText || '-' }}</p>

      <section class="block">
        <h3>信用因子</h3>
        <ul class="factor-list">
          <li v-for="factor in detail.factors" :key="factor.factorCode">
            <div class="line-1">
              <strong>{{ factor.factorName }}</strong>
              <span>{{ factor.score }}/100（权重{{ factor.weight }}%）</span>
            </div>
            <div class="bar">
              <div class="bar-inner" :style="{ width: scoreBarWidth(factor.score) }"></div>
            </div>
            <p class="muted">趋势：{{ factor.trend }} ｜ {{ factor.summary }}</p>
          </li>
        </ul>
      </section>

      <section class="block">
        <h3>趋势</h3>
        <div class="trend">
          <div v-for="point in detail.trend" :key="point.month" class="trend-col">
            <div class="trend-bar-wrap">
              <div class="trend-bar" :style="{ height: trendHeight(point.creditScore) }"></div>
            </div>
            <strong>{{ point.creditScore }}</strong>
            <span>{{ point.month }}</span>
            <small class="muted">履约{{ point.fulfillmentRate }} / 争议{{ point.disputeRate }}</small>
          </div>
        </div>
      </section>

      <section class="block">
        <h3>改进建议</h3>
        <ul class="suggestions">
          <li v-for="(item, idx) in detail.suggestions" :key="`${idx}-${item}`">{{ item }}</li>
        </ul>
      </section>
    </section>
  </main>
</template>

<style scoped>
.h5-n10-page {
  max-width: 760px;
  margin: 0 auto;
  padding: 12px 12px 28px;
  display: grid;
  gap: 12px;
}
.card {
  background: #fff;
  border: 1px solid #ececec;
  border-radius: 12px;
  padding: 14px;
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
  margin-top: 10px;
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
.head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.filters {
  margin-top: 10px;
  display: grid;
  grid-template-columns: 130px 1fr auto;
  gap: 8px;
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
}
.list li.active {
  border-color: #f57c00;
  background: #fffaf3;
}
.line-1 {
  display: flex;
  justify-content: space-between;
  gap: 8px;
}
.score-banner {
  border-radius: 10px;
  padding: 12px;
  display: grid;
  gap: 4px;
  margin-top: 10px;
}
.score-banner strong {
  font-size: 28px;
  line-height: 1;
}
.grade-a-plus {
  background: #ecfdf3;
  color: #0c7a43;
}
.grade-a {
  background: #eff6ff;
  color: #1d4ed8;
}
.grade-b {
  background: #fff7ed;
  color: #b45309;
}
.grade-c {
  background: #fef2f2;
  color: #b42318;
}
.meta-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 6px 10px;
  margin-top: 10px;
}
.block {
  margin-top: 12px;
  border: 1px solid #f3f4f6;
  border-radius: 10px;
  padding: 12px;
}
.block h3 {
  margin: 0 0 8px;
}
.factor-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: grid;
  gap: 8px;
}
.factor-list li {
  border: 1px solid #f1f5f9;
  border-radius: 8px;
  padding: 8px;
}
.bar {
  margin-top: 6px;
  height: 8px;
  border-radius: 999px;
  background: #f3f4f6;
  overflow: hidden;
}
.bar-inner {
  height: 100%;
  background: linear-gradient(90deg, #f59e0b, #f57c00);
}
.trend {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
  align-items: end;
}
.trend-col {
  display: grid;
  gap: 6px;
  justify-items: center;
}
.trend-bar-wrap {
  width: 46px;
  height: 130px;
  border-radius: 10px;
  background: #f3f4f6;
  display: flex;
  align-items: end;
  overflow: hidden;
}
.trend-bar {
  width: 100%;
  background: linear-gradient(180deg, #f59e0b, #f57c00);
}
.suggestions {
  margin: 0;
  padding-left: 18px;
  color: #374151;
}
.btn {
  height: 34px;
  border-radius: 8px;
  border: 1px solid #d1d5db;
  background: #fff;
  padding: 0 12px;
  cursor: pointer;
}
input,
select {
  border: 1px solid #d1d5db;
  border-radius: 8px;
  padding: 8px 10px;
}
input,
select {
  height: 34px;
}
.tip {
  color: #6b7280;
}
.muted {
  color: #6b7280;
}
.error {
  color: #b91c1c;
}
.ok {
  color: #166534;
}
@media (max-width: 768px) {
  .filters,
  .meta-grid,
  .trend {
    grid-template-columns: 1fr;
  }
}
</style>
