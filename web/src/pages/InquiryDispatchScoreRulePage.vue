<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const loading = ref(false)
const errorMsg = ref('')

const filters = reactive({
  merchantId: 'S001',
  scene: 'MERCHANT_LEAD'
})

const state = reactive({
  merchantId: '',
  ruleVersion: '',
  ruleName: '',
  scoreRange: '',
  formula: '',
  explanation: '',
  dimensions: [],
  bonusItems: [],
  penaltyItems: [],
  cases: [],
  updatedAt: ''
})

const canQuery = computed(() => filters.merchantId.trim().length > 0)

async function loadRule() {
  if (!canQuery.value || loading.value) return
  loading.value = true
  errorMsg.value = ''
  try {
    const params = new URLSearchParams()
    params.set('merchantId', filters.merchantId.trim())
    params.set('scene', filters.scene)
    const resp = await fetch(`/api/v1/inquiries/dispatch/score-rules?${params.toString()}`)
    const json = await resp.json()
    if (json.code !== '0') {
      throw new Error(json.message || '评分规则加载失败')
    }
    const data = json.data || {}
    Object.assign(state, {
      merchantId: data.merchantId || '',
      ruleVersion: data.ruleVersion || '',
      ruleName: data.ruleName || '',
      scoreRange: data.scoreRange || '',
      formula: data.formula || '',
      explanation: data.explanation || '',
      dimensions: Array.isArray(data.dimensions) ? data.dimensions : [],
      bonusItems: Array.isArray(data.bonusItems) ? data.bonusItems : [],
      penaltyItems: Array.isArray(data.penaltyItems) ? data.penaltyItems : [],
      cases: Array.isArray(data.cases) ? data.cases : [],
      updatedAt: data.updatedAt || ''
    })
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  const merchantId = String(route.query.merchantId || '').trim()
  if (merchantId) {
    filters.merchantId = merchantId
  }
  loadRule()
})
</script>

<template>
  <main class="rule-page">
    <section class="card">
      <h1>P13 分发评分规则公开页</h1>
      <p class="desc">公开平台线索分发评分框架，解释维度权重、加减分机制与示例，帮助商家理解流量分发逻辑。</p>
      <div class="filters">
        <label>
          商家ID
          <input v-model="filters.merchantId" placeholder="如 S001" />
        </label>
        <label>
          场景
          <select v-model="filters.scene">
            <option value="MERCHANT_LEAD">线索分发</option>
          </select>
        </label>
        <button class="btn primary" :disabled="!canQuery || loading" @click="loadRule">
          {{ loading ? '加载中...' : '刷新规则' }}
        </button>
      </div>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
    </section>

    <section class="card" v-if="state.ruleVersion">
      <div class="overview">
        <article>
          <h2>{{ state.ruleName }}</h2>
          <p class="muted">版本：{{ state.ruleVersion }} ｜ 更新时间：{{ state.updatedAt || '-' }}</p>
          <p class="muted">适用商家：{{ state.merchantId || '-' }}</p>
        </article>
        <article class="score-box">
          <strong>{{ state.scoreRange || '0-100' }}</strong>
          <span>评分区间</span>
        </article>
      </div>
      <p class="formula"><strong>评分公式：</strong>{{ state.formula || '-' }}</p>
      <p class="formula"><strong>公开说明：</strong>{{ state.explanation || '-' }}</p>
    </section>

    <section class="card">
      <h2>维度权重（公开）</h2>
      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>维度</th>
              <th>权重</th>
              <th>定义</th>
              <th>评分方式</th>
              <th>影响方向</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="!state.dimensions.length">
              <td colspan="5" class="empty">暂无数据</td>
            </tr>
            <tr v-for="item in state.dimensions" :key="item.code">
              <td>{{ item.name }}（{{ item.code }}）</td>
              <td>{{ item.weight }}%</td>
              <td>{{ item.description }}</td>
              <td>{{ item.scoringMethod }}</td>
              <td>{{ item.impactDirection }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>

    <section class="grid-2">
      <section class="card">
        <h2>加分规则</h2>
        <ul class="rule-list">
          <li v-if="!state.bonusItems.length" class="empty">暂无加分规则</li>
          <li v-for="item in state.bonusItems" :key="item.code">
            <p class="title">{{ item.name }}（{{ item.code }}）</p>
            <p>分值变化：<strong>+{{ item.scoreImpact }}</strong></p>
            <p>触发条件：{{ item.triggerCondition }}</p>
            <p class="muted">示例：{{ item.example }}</p>
          </li>
        </ul>
      </section>

      <section class="card">
        <h2>减分规则</h2>
        <ul class="rule-list">
          <li v-if="!state.penaltyItems.length" class="empty">暂无减分规则</li>
          <li v-for="item in state.penaltyItems" :key="item.code">
            <p class="title">{{ item.name }}（{{ item.code }}）</p>
            <p>扣分区间：<strong>{{ item.deductionRange }}</strong></p>
            <p>触发条件：{{ item.triggerCondition }}</p>
            <p class="muted">修复建议：{{ item.recoverSuggestion }}</p>
          </li>
        </ul>
      </section>
    </section>

    <section class="card">
      <h2>样例解释（公开）</h2>
      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>样例</th>
              <th>场景</th>
              <th>评分变化</th>
              <th>流量影响</th>
              <th>解释</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="!state.cases.length">
              <td colspan="5" class="empty">暂无样例</td>
            </tr>
            <tr v-for="item in state.cases" :key="item.title">
              <td>{{ item.title }}</td>
              <td>{{ item.scene }}</td>
              <td>{{ item.scoreBefore }} → {{ item.scoreAfter }}</td>
              <td>{{ item.impact }}</td>
              <td>{{ item.explanation }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </main>
</template>

<style scoped>
.rule-page {
  max-width: 1160px;
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
  color: #4b5563;
  margin: 0 0 12px;
}
.filters {
  display: flex;
  flex-wrap: wrap;
  align-items: end;
  gap: 10px;
}
label {
  display: grid;
  gap: 6px;
  color: #374151;
}
input,
select {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 9px 10px;
  font: inherit;
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
.overview {
  display: flex;
  justify-content: space-between;
  gap: 10px;
}
.score-box {
  width: 160px;
  border: 1px solid #ffedd5;
  background: #fff7ed;
  border-radius: 10px;
  display: grid;
  place-items: center;
  padding: 10px;
}
.score-box strong {
  font-size: 30px;
  color: #b45309;
}
.score-box span {
  color: #9a3412;
}
.formula {
  color: #374151;
}
.muted {
  color: #6b7280;
}
.table-wrap {
  overflow-x: auto;
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
  padding: 10px 8px;
  font-size: 14px;
  vertical-align: top;
}
th {
  color: #374151;
  background: #fafafa;
}
.empty {
  text-align: center;
  color: #6b7280;
}
.grid-2 {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}
.rule-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: grid;
  gap: 10px;
}
.rule-list li {
  border: 1px solid #f3f4f6;
  border-radius: 10px;
  padding: 10px;
}
.rule-list .title {
  margin: 0 0 6px;
  font-weight: 700;
  color: #111827;
}
.rule-list p {
  margin: 4px 0;
}
@media (max-width: 980px) {
  .overview {
    display: grid;
  }
  .score-box {
    width: 100%;
  }
  .grid-2 {
    grid-template-columns: repeat(1, minmax(0, 1fr));
  }
}
</style>
