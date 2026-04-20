<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const loading = ref(false)
const detailLoading = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

const filters = reactive({
  status: '',
  keyword: '',
  pageNo: 1,
  pageSize: 10
})

const scoreList = ref([])
const total = ref(0)
const selectedScoreId = ref('')
const detail = ref(null)

const token = computed(() => localStorage.getItem('N01_AUTH_TOKEN') || '')
const hasSession = computed(() => token.value.length > 0)

async function loadScoreList() {
  if (!hasSession.value || loading.value) {
    if (!hasSession.value) {
      errorMsg.value = '请先登录后查看信用评分明细'
    }
    return
  }
  loading.value = true
  errorMsg.value = ''
  try {
    const params = new URLSearchParams({
      pageNo: String(filters.pageNo),
      pageSize: String(filters.pageSize)
    })
    if (filters.status) params.set('status', filters.status)
    if (filters.keyword.trim()) params.set('keyword', filters.keyword.trim())
    const resp = await fetch(`/api/v1/auth/credit-scores?${params.toString()}`, {
      headers: { 'X-Auth-Token': token.value }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `信用评分列表加载失败(${resp.status})`)
    }
    const data = json.data || {}
    scoreList.value = data.records || []
    total.value = data.total || 0
    if ((!selectedScoreId.value || !scoreList.value.some(item => item.scoreId === selectedScoreId.value)) && scoreList.value.length) {
      selectedScoreId.value = scoreList.value[0].scoreId
      await loadScoreDetail()
    }
  } catch (error) {
    errorMsg.value = error.message || '信用评分列表加载失败'
  } finally {
    loading.value = false
  }
}

async function loadScoreDetail() {
  if (!hasSession.value || !selectedScoreId.value || detailLoading.value) return
  detailLoading.value = true
  errorMsg.value = ''
  try {
    const resp = await fetch(`/api/v1/auth/credit-scores/${selectedScoreId.value}`, {
      headers: { 'X-Auth-Token': token.value }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `信用评分详情加载失败(${resp.status})`)
    }
    detail.value = json.data || null
  } catch (error) {
    errorMsg.value = error.message || '信用评分详情加载失败'
  } finally {
    detailLoading.value = false
  }
}

function pickScore(scoreId) {
  selectedScoreId.value = scoreId
  loadScoreDetail()
}

function levelClass(level) {
  if (level === 'A+') return 'level-a-plus'
  if (level === 'A' || level === 'A-') return 'level-a'
  if (level === 'B+' || level === 'B') return 'level-b'
  return 'level-c'
}

function trendHeight(val) {
  const scoreVal = Number(val || 0)
  const normalized = Math.max(10, Math.min(100, Math.round((scoreVal / 100) * 100)))
  return `${normalized}%`
}

function goInvoiceManage() {
  router.push('/account/invoice-manage')
}

function goHome() {
  router.push('/')
}

function goDispatchAppeal() {
  router.push('/account/dispatch-appeal')
}

onMounted(async () => {
  await loadScoreList()
})
</script>

<template>
  <main class="n13-page">
    <section class="card hero">
      <h1>PC-N13 信用评分明细页</h1>
      <p>查看各期信用评分、维度因子、趋势变化与改进建议，支撑商家运营持续优化。</p>
      <div class="hero-actions">
        <button class="btn" @click="goInvoiceManage">前往发票与抬头管理</button>
        <button class="btn" @click="goDispatchAppeal">分发异议申诉</button>
        <button class="btn" @click="goHome">返回首页</button>
      </div>
    </section>

    <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
    <p v-if="successMsg" class="ok">{{ successMsg }}</p>

    <section class="layout">
      <article class="card left">
        <div class="head">
          <h2>评分记录</h2>
          <button class="btn" :disabled="loading" @click="loadScoreList">{{ loading ? '刷新中...' : '刷新' }}</button>
        </div>
        <div class="filters">
          <select v-model="filters.status">
            <option value="">全部状态</option>
            <option value="ACTIVE">生效中</option>
            <option value="ARCHIVED">已归档</option>
          </select>
          <input v-model="filters.keyword" placeholder="按评分单号/商家名称搜索" />
          <button class="btn" :disabled="loading" @click="loadScoreList">筛选</button>
        </div>
        <p class="tip">共 {{ total }} 条评分记录</p>
        <ul class="list">
          <li
            v-for="item in scoreList"
            :key="item.scoreId"
            :class="{ active: item.scoreId === selectedScoreId }"
            @click="pickScore(item.scoreId)"
          >
            <div class="line-1">
              <strong>{{ item.scoreNo }}</strong>
              <span>{{ item.statusText }}</span>
            </div>
            <p>{{ item.merchantName }}</p>
            <p>总分：{{ item.totalScore }} ｜ 等级：{{ item.level }}</p>
            <p class="muted">版本：{{ item.scoreVersion }} ｜ 更新时间：{{ item.updatedAt || '-' }}</p>
          </li>
        </ul>
      </article>

      <article class="card right">
        <div class="head">
          <h2>评分明细</h2>
          <button class="btn" :disabled="detailLoading || !selectedScoreId" @click="loadScoreDetail">
            {{ detailLoading ? '加载中...' : '刷新详情' }}
          </button>
        </div>

        <section v-if="detail" class="detail">
          <div class="score-banner" :class="levelClass(detail.level)">
            <strong>{{ detail.totalScore }}</strong>
            <span>{{ detail.level }} ｜ {{ detail.levelText }}</span>
            <em>{{ detail.merchantName }}（{{ detail.merchantId }}）</em>
          </div>

          <div class="meta-grid">
            <p>评分单号：{{ detail.scoreNo }}</p>
            <p>评分版本：{{ detail.scoreVersion }}</p>
            <p>排名：{{ detail.rankPercent }}</p>
            <p>状态：{{ detail.statusText }}（{{ detail.status }}）</p>
            <p>风险标签：{{ detail.risks?.join('、') || '-' }}</p>
            <p>更新时间：{{ detail.updatedAt || '-' }}</p>
          </div>

          <section class="block">
            <h3>评分因子</h3>
            <ul class="factor-list">
              <li v-for="factor in detail.factors" :key="factor.factorCode">
                <div class="line-1">
                  <strong>{{ factor.factorName }}</strong>
                  <span>{{ factor.score }}/100（权重{{ factor.weight }}%）</span>
                </div>
                <div class="bar">
                  <div class="bar-inner" :style="{ width: `${Math.max(0, Math.min(100, Number(factor.score) || 0))}%` }"></div>
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
        <p v-else class="tip">请选择左侧评分记录查看详情</p>
      </article>
    </section>
  </main>
</template>

<style scoped>
.n13-page {
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
  grid-template-columns: 1fr 1.3fr;
  gap: 14px;
}
.head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.filters {
  margin-top: 10px;
  display: grid;
  grid-template-columns: 140px 1fr auto;
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
.detail {
  display: grid;
  gap: 12px;
  margin-top: 10px;
}
.score-banner {
  border-radius: 10px;
  padding: 12px;
  display: grid;
  gap: 4px;
}
.score-banner strong {
  font-size: 28px;
  line-height: 1;
}
.level-a-plus {
  background: #ecfdf3;
  color: #0c7a43;
}
.level-a {
  background: #eff6ff;
  color: #1d4ed8;
}
.level-b {
  background: #fff7ed;
  color: #b45309;
}
.level-c {
  background: #fef2f2;
  color: #b42318;
}
.meta-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 6px 10px;
}
.block {
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
  height: 34px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  padding: 0 10px;
}
.muted {
  color: #6b7280;
}
.tip {
  color: #6b7280;
}
.ok {
  color: #166534;
}
.error {
  color: #b91c1c;
}
@media (max-width: 980px) {
  .layout {
    grid-template-columns: 1fr;
  }
  .meta-grid,
  .filters,
  .trend {
    grid-template-columns: 1fr;
  }
}
</style>
