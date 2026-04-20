<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const loading = ref(false)
const errorMsg = ref('')

const filters = reactive({
  merchantId: ''
})

const score = reactive({
  merchantId: '',
  merchantName: '',
  grade: '',
  score: '',
  rankPercent: '',
  scoreVersion: '',
  dimensions: [],
  trend: [],
  risks: [],
  suggestions: [],
  updatedAt: ''
})

const canQuery = computed(() => filters.merchantId.trim().length > 0)

const levelClass = computed(() => {
  if (score.grade === 'A+') return 'level-a-plus'
  if (score.grade === 'A' || score.grade === 'A-') return 'level-a'
  if (score.grade === 'B' || score.grade === 'B+') return 'level-b'
  return 'level-c'
})

async function loadScore() {
  if (!canQuery.value || loading.value) return
  loading.value = true
  errorMsg.value = ''
  try {
    const params = new URLSearchParams()
    params.set('merchantId', filters.merchantId.trim())
    const resp = await fetch(`/api/v1/inquiries/merchant/credit-score?${params.toString()}`)
    const json = await resp.json()
    if (json.code !== '0') {
      throw new Error(json.message || '加载信用评分失败')
    }
    const data = json.data || {}
    Object.assign(score, {
      merchantId: data.merchantId || '',
      merchantName: data.merchantName || '',
      grade: data.grade || '',
      score: data.score || '',
      rankPercent: data.rankPercent || '',
      scoreVersion: data.scoreVersion || '',
      dimensions: Array.isArray(data.dimensions) ? data.dimensions : [],
      trend: Array.isArray(data.trend) ? data.trend : [],
      risks: Array.isArray(data.risks) ? data.risks : [],
      suggestions: Array.isArray(data.suggestions) ? data.suggestions : [],
      updatedAt: data.updatedAt || ''
    })
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    loading.value = false
  }
}

function trendHeight(val) {
  const scoreVal = Number(val || 0)
  const normalized = Math.max(10, Math.min(100, Math.round((scoreVal / 100) * 100)))
  return `${normalized}%`
}

onMounted(() => {
  const merchantId = String(route.query.merchantId || '').trim()
  if (merchantId) {
    filters.merchantId = merchantId
  } else {
    filters.merchantId = 'S001'
  }
  loadScore()
})
</script>

<template>
  <main class="credit-page">
    <section class="card">
      <h1>P10 商家信用评分页</h1>
      <p class="desc">综合履约、响应、争议、回款等维度，生成商家信用分并给出改进建议。</p>
      <div class="filters">
        <label>
          商家ID
          <input v-model="filters.merchantId" placeholder="如 S001" />
        </label>
        <button class="btn primary" :disabled="!canQuery || loading" @click="loadScore">
          {{ loading ? '加载中...' : '查询信用评分' }}
        </button>
      </div>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
    </section>

    <section class="card" v-if="score.merchantId">
      <div class="headline">
        <div>
          <h2>{{ score.merchantName }}（{{ score.merchantId }}）</h2>
          <p class="muted">更新时间：{{ score.updatedAt || '-' }}</p>
        </div>
        <div class="score-pill" :class="levelClass">
          <strong>{{ score.score }}</strong>
          <span>{{ score.grade }}</span>
        </div>
      </div>
      <p class="score-desc">平台分层：{{ score.grade }} ｜ 排名：{{ score.rankPercent || '-' }}</p>
      <p class="risk-tag">风险标签：{{ score.risks?.join('、') || '无' }}</p>
      <p class="muted">评分版本：{{ score.scoreVersion || '-' }}</p>
    </section>

    <section class="card" v-if="score.dimensions?.length">
      <h2>维度评分</h2>
      <div class="dimension-list">
        <article v-for="item in score.dimensions" :key="item.code" class="dimension-item">
          <header>
            <strong>{{ item.name }}</strong>
            <span>{{ item.score }} / 100</span>
          </header>
          <div class="bar">
            <div class="bar-inner" :style="{ width: `${Math.max(0, Math.min(100, Number(item.score) || 0))}%` }"></div>
          </div>
          <p class="muted">权重 {{ item.weight }}% ｜ {{ item.summary }}</p>
        </article>
      </div>
    </section>

    <section class="card" v-if="score.trend?.length">
      <h2>近4个月信用趋势</h2>
      <div class="trend">
        <div v-for="point in score.trend" :key="point.month" class="trend-col">
          <div class="trend-bar-wrap">
            <div class="trend-bar" :style="{ height: trendHeight(point.creditScore) }"></div>
          </div>
          <strong>{{ point.creditScore }}</strong>
          <span>{{ point.month }}</span>
          <small class="muted">履约{{ point.fulfillmentRate }} / 争议{{ point.disputeRate }}</small>
        </div>
      </div>
    </section>

    <section class="card" v-if="score.suggestions?.length">
      <h2>改进建议</h2>
      <ul class="suggestions">
        <li v-for="(item, idx) in score.suggestions" :key="`${idx}-${item}`">{{ item }}</li>
      </ul>
    </section>
  </main>
</template>

<style scoped>
.credit-page {
  max-width: 1120px;
  margin: 0 auto;
  padding: 20px 16px 36px;
}
.card {
  background: #fff;
  border: 1px solid #ececec;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 14px;
}
.desc {
  margin: 0 0 12px;
  color: #4b5563;
}
.filters {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  align-items: end;
}
label {
  display: grid;
  gap: 6px;
  color: #374151;
}
input {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 9px 10px;
  font: inherit;
}
.checkbox {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}
.btn {
  border: 1px solid #ddd;
  border-radius: 8px;
  background: #fff;
  padding: 9px 14px;
  cursor: pointer;
}
.btn.primary {
  border-color: #f57c00;
  background: #f57c00;
  color: #fff;
}
.error {
  color: #b42318;
  margin-top: 10px;
}
.headline {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
}
.muted {
  color: #6b7280;
  margin: 0;
}
.score-pill {
  min-width: 110px;
  text-align: center;
  border-radius: 12px;
  padding: 10px 14px;
}
.score-pill strong {
  display: block;
  font-size: 28px;
}
.score-pill span {
  display: inline-block;
  margin-top: 2px;
  font-weight: 600;
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
.score-desc {
  margin: 10px 0 6px;
  color: #111827;
}
.risk-tag {
  margin: 0;
  color: #92400e;
}
.dimension-list {
  display: grid;
  gap: 12px;
}
.dimension-item {
  border: 1px solid #f1f5f9;
  border-radius: 10px;
  padding: 10px 12px;
}
.dimension-item header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}
.bar {
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
@media (max-width: 980px) {
  .headline {
    flex-direction: column;
    align-items: start;
  }
  .trend {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
