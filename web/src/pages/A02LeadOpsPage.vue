<script setup>
import { computed, onMounted, reactive, ref } from 'vue'

const loading = ref(false)
const opLoading = ref(false)
const errorMsg = ref('')
const opMsg = ref('')

const query = reactive({
  adminToken: 'test-admin-token',
  source: 'ALL',
  status: '',
  keyword: '',
  owner: '',
  city: '',
  page: 1,
  pageSize: 10
})

const sourceOptions = [
  { value: 'ALL', label: '全部来源' },
  { value: 'INQUIRY', label: '询价线索' },
  { value: 'SITE_AD', label: '广告线索' }
]

const statusOptions = [
  { value: '', label: '全部状态' },
  { value: 'NEW', label: '新线索' },
  { value: 'FOLLOWING', label: '跟进中' },
  { value: 'QUOTED', label: '已报价/提案' },
  { value: 'WON', label: '已赢单/成交' },
  { value: 'CLOSED', label: '已关闭' }
]

const state = reactive({
  overview: {
    total: 0,
    inquiryLeadTotal: 0,
    adLeadTotal: 0,
    newCount: 0,
    followingCount: 0,
    quotedOrProposalCount: 0,
    wonOrConvertedCount: 0,
    closedOrLostCount: 0
  },
  items: [],
  total: 0,
  page: 1,
  pageSize: 10,
  source: 'ALL'
})

const selected = ref(null)

const assignForm = reactive({
  ownerName: '',
  team: '',
  operator: 'admin-ui',
  comment: ''
})

const statusForm = reactive({
  status: 'FOLLOWING',
  operator: 'admin-ui',
  comment: ''
})

const followForm = reactive({
  content: '',
  nextActionAt: '',
  operator: 'admin-ui'
})

const quoteForm = reactive({
  merchantId: 'S001',
  supplierName: '',
  unitPrice: '',
  totalAmount: '',
  deliveryDays: '',
  paymentTerm: '月结30天',
  quoteRemark: ''
})

const canLoad = computed(() => query.adminToken.trim().length > 0)
const canAssign = computed(() => selected.value && assignForm.ownerName.trim())
const canStatus = computed(() => selected.value && statusForm.status.trim())
const canFollow = computed(() => selected.value && followForm.content.trim())
const canQuote = computed(() => {
  return (
    selected.value &&
    selected.value.source === 'INQUIRY' &&
    quoteForm.merchantId.trim() &&
    quoteForm.supplierName.trim() &&
    Number(quoteForm.unitPrice) > 0 &&
    Number(quoteForm.totalAmount) > 0 &&
    Number(quoteForm.deliveryDays) >= 0
  )
})

async function loadList() {
  if (!canLoad.value || loading.value) return
  loading.value = true
  errorMsg.value = ''
  opMsg.value = ''
  try {
    const params = new URLSearchParams()
    if (query.source) params.set('source', query.source)
    if (query.status) params.set('status', query.status)
    if (query.keyword.trim()) params.set('keyword', query.keyword.trim())
    if (query.owner.trim()) params.set('owner', query.owner.trim())
    if (query.city.trim()) params.set('city', query.city.trim())
    params.set('page', String(query.page))
    params.set('pageSize', String(query.pageSize))
    const resp = await fetch(`/api/admin/lead-ops/leads?${params.toString()}`, {
      headers: { 'X-Admin-Token': query.adminToken.trim() }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `加载失败(${resp.status})`)
    }
    state.overview = json.data.overview || state.overview
    state.items = json.data.items || []
    state.total = Number(json.data.total || 0)
    state.page = Number(json.data.page || query.page)
    state.pageSize = Number(json.data.pageSize || query.pageSize)
    state.source = json.data.source || query.source
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    loading.value = false
  }
}

async function loadDetail(item) {
  selected.value = null
  opMsg.value = ''
  errorMsg.value = ''
  try {
    const resp = await fetch(`/api/admin/lead-ops/leads/${item.source}/${item.leadId}`, {
      headers: { 'X-Admin-Token': query.adminToken.trim() }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `详情加载失败(${resp.status})`)
    }
    selected.value = json.data
    assignForm.ownerName = json.data.owner || ''
    assignForm.team = ''
    assignForm.comment = ''
    statusForm.status = json.data.status === 'NEW' ? 'FOLLOWING' : json.data.status
    statusForm.comment = ''
    followForm.content = ''
    followForm.nextActionAt = ''
    quoteForm.supplierName = json.data.owner || ''
    quoteForm.unitPrice = ''
    quoteForm.totalAmount = ''
    quoteForm.deliveryDays = ''
    quoteForm.quoteRemark = ''
  } catch (error) {
    errorMsg.value = error.message || '加载详情失败'
  }
}

async function submitAssign() {
  if (!canAssign.value) return
  opLoading.value = true
  opMsg.value = ''
  errorMsg.value = ''
  try {
    const payload = {
      ownerName: assignForm.ownerName.trim(),
      team: assignForm.team.trim() || null,
      operator: assignForm.operator.trim() || 'admin-ui',
      comment: assignForm.comment.trim() || null
    }
    const resp = await fetch(`/api/admin/lead-ops/leads/${selected.value.source}/${selected.value.leadId}/assign`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'X-Admin-Token': query.adminToken.trim()
      },
      body: JSON.stringify(payload)
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `分配失败(${resp.status})`)
    }
    selected.value = json.data
    opMsg.value = '负责人分配成功'
    await loadList()
  } catch (error) {
    errorMsg.value = error.message || '分配失败'
  } finally {
    opLoading.value = false
  }
}

async function submitStatus() {
  if (!canStatus.value) return
  opLoading.value = true
  opMsg.value = ''
  errorMsg.value = ''
  try {
    const payload = {
      status: statusForm.status.trim(),
      operator: statusForm.operator.trim() || 'admin-ui',
      comment: statusForm.comment.trim() || null
    }
    const resp = await fetch(`/api/admin/lead-ops/leads/${selected.value.source}/${selected.value.leadId}/status`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'X-Admin-Token': query.adminToken.trim()
      },
      body: JSON.stringify(payload)
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `状态更新失败(${resp.status})`)
    }
    selected.value = json.data
    opMsg.value = '状态更新成功'
    await loadList()
  } catch (error) {
    errorMsg.value = error.message || '状态更新失败'
  } finally {
    opLoading.value = false
  }
}

async function submitFollow() {
  if (!canFollow.value) return
  opLoading.value = true
  opMsg.value = ''
  errorMsg.value = ''
  try {
    const payload = {
      content: followForm.content.trim(),
      nextActionAt: followForm.nextActionAt.trim() || null,
      operator: followForm.operator.trim() || 'admin-ui'
    }
    const resp = await fetch(`/api/admin/lead-ops/leads/${selected.value.source}/${selected.value.leadId}/follow`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Admin-Token': query.adminToken.trim()
      },
      body: JSON.stringify(payload)
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `新增跟进失败(${resp.status})`)
    }
    selected.value = json.data
    opMsg.value = '跟进记录已提交'
    followForm.content = ''
    followForm.nextActionAt = ''
    await loadList()
  } catch (error) {
    errorMsg.value = error.message || '新增跟进失败'
  } finally {
    opLoading.value = false
  }
}

async function submitQuickQuote() {
  if (!canQuote.value) return
  opLoading.value = true
  opMsg.value = ''
  errorMsg.value = ''
  try {
    const payload = {
      merchantId: quoteForm.merchantId.trim(),
      supplierName: quoteForm.supplierName.trim(),
      unitPrice: String(Number(quoteForm.unitPrice)),
      totalAmount: String(Number(quoteForm.totalAmount)),
      deliveryDays: String(Number(quoteForm.deliveryDays)),
      paymentTerm: quoteForm.paymentTerm.trim() || '月结30天',
      quoteRemark: quoteForm.quoteRemark.trim() || null,
      operator: 'admin-ui'
    }
    const resp = await fetch(
      `/api/admin/lead-ops/leads/${selected.value.source}/${selected.value.leadId}/quick-quote`,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'X-Admin-Token': query.adminToken.trim()
        },
        body: JSON.stringify(payload)
      }
    )
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `快捷报价失败(${resp.status})`)
    }
    selected.value = json.data
    opMsg.value = '快捷报价成功'
    await loadList()
  } catch (error) {
    errorMsg.value = error.message || '快捷报价失败'
  } finally {
    opLoading.value = false
  }
}

function prevPage() {
  if (loading.value || query.page <= 1) return
  query.page -= 1
  loadList()
}

function nextPage() {
  if (loading.value || query.page * query.pageSize >= state.total) return
  query.page += 1
  loadList()
}

onMounted(() => {
  loadList()
})
</script>

<template>
  <main class="a02-page">
    <section class="card hero">
      <h1>A02 线索运营中心</h1>
      <p>统一管理询价线索与广告线索，支持分配、状态流转、跟进记录及询价快捷报价。</p>
    </section>

    <section class="card">
      <div class="filters">
        <label>
          Admin Token
          <input v-model="query.adminToken" placeholder="请输入X-Admin-Token" />
        </label>
        <label>
          线索来源
          <select v-model="query.source">
            <option v-for="op in sourceOptions" :key="op.value" :value="op.value">{{ op.label }}</option>
          </select>
        </label>
        <label>
          状态
          <select v-model="query.status">
            <option v-for="op in statusOptions" :key="op.value" :value="op.value">{{ op.label }}</option>
          </select>
        </label>
        <label>
          关键词
          <input v-model="query.keyword" placeholder="线索号/规格/企业/城市" />
        </label>
        <label>
          负责人
          <input v-model="query.owner" placeholder="负责人名" />
        </label>
        <label>
          城市
          <input v-model="query.city" placeholder="如 唐山 / 无锡" />
        </label>
      </div>
      <div class="actions">
        <button class="btn btn--primary" :disabled="!canLoad || loading" @click="loadList">
          {{ loading ? '加载中...' : '查询线索' }}
        </button>
      </div>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
      <p class="tip">当前来源：{{ state.source }} ｜ 共 {{ state.total }} 条</p>
    </section>

    <section class="card">
      <h2>总览指标</h2>
      <div class="kpi-grid">
        <article class="kpi"><span>线索总量</span><strong>{{ state.overview.total }}</strong></article>
        <article class="kpi"><span>询价线索</span><strong>{{ state.overview.inquiryLeadTotal }}</strong></article>
        <article class="kpi"><span>广告线索</span><strong>{{ state.overview.adLeadTotal }}</strong></article>
        <article class="kpi"><span>新线索</span><strong>{{ state.overview.newCount }}</strong></article>
        <article class="kpi"><span>跟进中</span><strong>{{ state.overview.followingCount }}</strong></article>
        <article class="kpi"><span>已报价/提案</span><strong>{{ state.overview.quotedOrProposalCount }}</strong></article>
        <article class="kpi"><span>已赢单/成交</span><strong>{{ state.overview.wonOrConvertedCount }}</strong></article>
        <article class="kpi"><span>已关闭/丢单</span><strong>{{ state.overview.closedOrLostCount }}</strong></article>
      </div>
    </section>

    <section class="card">
      <h2>线索列表</h2>
      <p v-if="loading">加载中...</p>
      <p v-else-if="state.items.length === 0">暂无线索数据</p>
      <ul v-else class="lead-list">
        <li v-for="item in state.items" :key="`${item.source}-${item.leadId}`" class="lead-item">
          <div class="head">
            <strong>{{ item.specOrPlacementName }}</strong>
            <span class="status">{{ item.statusText }}</span>
          </div>
          <p>来源：{{ item.source }} ｜ 线索号：{{ item.leadNo }}</p>
          <p>企业：{{ item.companyName }} ｜ 城市：{{ item.city }}</p>
          <p>需求/预算：{{ item.demandOrBudget }} ｜ 负责人：{{ item.owner }}</p>
          <p>最新跟进：{{ item.latestFollow || '-' }}</p>
          <p>更新时间：{{ item.updatedAt }}</p>
          <button class="btn" @click="loadDetail(item)">查看详情并操作</button>
        </li>
      </ul>
      <div v-if="state.total > 0" class="actions">
        <button class="btn" :disabled="query.page <= 1 || loading" @click="prevPage">上一页</button>
        <button class="btn" :disabled="query.page * query.pageSize >= state.total || loading" @click="nextPage">
          下一页
        </button>
        <span class="tip">第 {{ query.page }} 页 / 共 {{ Math.max(Math.ceil(state.total / query.pageSize), 1) }} 页</span>
      </div>
    </section>

    <section class="card" v-if="selected">
      <h2>线索详情与管理动作</h2>
      <p class="tip">来源：{{ selected.source }} ｜ 状态：{{ selected.statusText }} ｜ 线索号：{{ selected.leadNo }}</p>
      <p class="tip">企业：{{ selected.companyName }} ｜ 联系人：{{ selected.contactNameMasked }} {{ selected.contactMobileMasked }}</p>
      <p class="tip">规格/广告位：{{ selected.specOrPlacementName }} ｜ 需求/预算：{{ selected.demandOrBudget }}</p>
      <p class="tip">最新跟进：{{ selected.latestFollow || '-' }}</p>
      <p v-if="opMsg" class="ok">{{ opMsg }}</p>

      <div class="op-grid">
        <article class="op-card">
          <h3>分配负责人</h3>
          <label>负责人<input v-model="assignForm.ownerName" placeholder="如 运营A" /></label>
          <label>团队<input v-model="assignForm.team" placeholder="如 A组" /></label>
          <label>备注<textarea v-model="assignForm.comment" rows="2" placeholder="分配说明"></textarea></label>
          <button class="btn btn--primary" :disabled="opLoading || !canAssign" @click="submitAssign">提交分配</button>
        </article>

        <article class="op-card">
          <h3>状态流转</h3>
          <label>
            新状态
            <select v-model="statusForm.status">
              <option value="NEW">NEW</option>
              <option value="FOLLOWING">FOLLOWING</option>
              <option value="CONTACTED">CONTACTED</option>
              <option value="QUOTED">QUOTED</option>
              <option value="WON">WON</option>
              <option value="CLOSED">CLOSED</option>
              <option value="LOST">LOST</option>
              <option value="SUBMITTED">SUBMITTED</option>
              <option value="ASSIGNED">ASSIGNED</option>
              <option value="PROPOSAL_SENT">PROPOSAL_SENT</option>
              <option value="CONVERTED">CONVERTED</option>
            </select>
          </label>
          <label>备注<textarea v-model="statusForm.comment" rows="2" placeholder="状态变更说明"></textarea></label>
          <button class="btn btn--primary" :disabled="opLoading || !canStatus" @click="submitStatus">更新状态</button>
        </article>

        <article class="op-card">
          <h3>新增跟进</h3>
          <label>跟进内容<textarea v-model="followForm.content" rows="2" placeholder="例如：已电话沟通"></textarea></label>
          <label>下次跟进<input v-model="followForm.nextActionAt" placeholder="如 明日10:00回访" /></label>
          <button class="btn btn--primary" :disabled="opLoading || !canFollow" @click="submitFollow">提交跟进</button>
        </article>
      </div>

      <article class="op-card" v-if="selected.source === 'INQUIRY'">
        <h3>快捷报价（仅询价线索）</h3>
        <div class="op-grid quote-grid">
          <label>商家ID<input v-model="quoteForm.merchantId" placeholder="如 S001" /></label>
          <label>供应商<input v-model="quoteForm.supplierName" placeholder="如 唐山弘达钢贸" /></label>
          <label>单价(元/吨)<input v-model="quoteForm.unitPrice" type="number" min="0" /></label>
          <label>总价(元)<input v-model="quoteForm.totalAmount" type="number" min="0" /></label>
          <label>交期(天)<input v-model="quoteForm.deliveryDays" type="number" min="0" /></label>
          <label>账期<input v-model="quoteForm.paymentTerm" placeholder="如 月结30天" /></label>
          <label class="full">报价备注<textarea v-model="quoteForm.quoteRemark" rows="2"></textarea></label>
        </div>
        <button class="btn btn--primary" :disabled="opLoading || !canQuote" @click="submitQuickQuote">提交快捷报价</button>
      </article>
    </section>
  </main>
</template>

<style scoped>
.a02-page {
  max-width: 1180px;
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
.hero h1 {
  margin: 0 0 8px;
}
.hero p {
  margin: 0;
  color: #4b5563;
}
.filters {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
}
label {
  display: grid;
  gap: 6px;
  color: #374151;
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
  gap: 10px;
  margin-top: 12px;
  align-items: center;
  flex-wrap: wrap;
}
.btn {
  border: 1px solid #ddd;
  border-radius: 8px;
  background: #fff;
  padding: 9px 14px;
  cursor: pointer;
}
.btn--primary {
  border-color: #f57c00;
  background: #f57c00;
  color: #fff;
}
.error {
  color: #b42318;
  margin-top: 8px;
}
.ok {
  color: #0c7a43;
  margin: 8px 0;
}
.tip {
  color: #6b7280;
  margin: 8px 0 0;
}
.kpi-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 10px;
}
.kpi {
  border: 1px solid #ececec;
  border-radius: 10px;
  padding: 10px;
  display: grid;
  gap: 6px;
}
.kpi span {
  color: #6b7280;
  font-size: 13px;
}
.kpi strong {
  font-size: 22px;
  color: #111827;
}
.lead-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: grid;
  gap: 10px;
}
.lead-item {
  border: 1px solid #ececec;
  border-radius: 10px;
  padding: 10px;
}
.lead-item p {
  margin: 6px 0;
  color: #374151;
}
.head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
}
.status {
  border-radius: 999px;
  background: #fff4e5;
  color: #9a4a00;
  font-size: 12px;
  padding: 2px 10px;
}
.op-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
  margin-top: 10px;
}
.op-card {
  border: 1px solid #ececec;
  border-radius: 10px;
  padding: 10px;
  display: grid;
  gap: 8px;
}
.op-card h3 {
  margin: 0;
  font-size: 15px;
}
.quote-grid {
  grid-template-columns: repeat(3, minmax(0, 1fr));
}
.full {
  grid-column: 1 / -1;
}
@media (max-width: 900px) {
  .filters,
  .kpi-grid,
  .op-grid,
  .quote-grid {
    grid-template-columns: 1fr;
  }
}
</style>
