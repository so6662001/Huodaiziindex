<script setup>
import { computed, onMounted, reactive, ref } from 'vue'

const loadingOverview = ref(false)
const loadingTasks = ref(false)
const loadingBatch = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

const query = reactive({
  merchantId: 'S001',
  status: '',
  deliveryCity: '',
  keyword: '',
  page: 1,
  pageSize: 10
})

const overview = reactive({
  merchantId: '',
  totalLeads: 0,
  newCount: 0,
  contactedCount: 0,
  pendingQuoteCount: 0,
  quotedCount: 0,
  wonCount: 0,
  lostCount: 0,
  closedCount: 0,
  quoteRate: '0.0%',
  winRate: '0.0%',
  avgResponseMinutes: '-',
  followUpCount: 0
})

const tasks = ref([])
const total = ref(0)
const selectedLeadIds = ref([])

const statusLabelMap = {
  NEW: '待报价',
  CONTACTED: '跟进中',
  QUOTED: '已报价',
  WON: '已赢单',
  LOST: '已丢单',
  CLOSED: '已关闭'
}

const batchForm = reactive({
  toStatus: 'CONTACTED',
  comment: ''
})

const hasSelection = computed(() => selectedLeadIds.value.length > 0)

async function loadOverview() {
  if (!query.merchantId.trim()) return
  loadingOverview.value = true
  errorMsg.value = ''
  try {
    const params = new URLSearchParams()
    params.set('merchantId', query.merchantId.trim())
    const resp = await fetch(`/api/v1/inquiries/merchant/workbench/overview?${params.toString()}`)
    const json = await resp.json()
    if (json.code !== '0') {
      throw new Error(json.message || '加载工作台概览失败')
    }
    Object.assign(overview, json.data)
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    loadingOverview.value = false
  }
}

async function loadTasks() {
  if (!query.merchantId.trim()) return
  loadingTasks.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const params = new URLSearchParams()
    params.set('merchantId', query.merchantId.trim())
    if (query.status) params.set('status', query.status)
    if (query.deliveryCity.trim()) params.set('deliveryCity', query.deliveryCity.trim())
    if (query.keyword.trim()) params.set('keyword', query.keyword.trim())
    params.set('page', String(query.page))
    params.set('pageSize', String(query.pageSize))
    const resp = await fetch(`/api/v1/inquiries/merchant/workbench/tasks?${params.toString()}`)
    const json = await resp.json()
    if (json.code !== '0') {
      throw new Error(json.message || '加载报价任务失败')
    }
    tasks.value = json.data.items || []
    total.value = Number(json.data.total || 0)
    query.page = Number(json.data.page || query.page)
    query.pageSize = Number(json.data.pageSize || query.pageSize)
    overview.followUpCount = Number(json.data.followUpCount || 0)
    selectedLeadIds.value = []
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    loadingTasks.value = false
  }
}

function toggleSelect(leadId, checked) {
  if (checked) {
    if (!selectedLeadIds.value.includes(leadId)) {
      selectedLeadIds.value.push(leadId)
    }
    return
  }
  selectedLeadIds.value = selectedLeadIds.value.filter((id) => id !== leadId)
}

function toggleSelectAll(checked) {
  if (checked) {
    selectedLeadIds.value = tasks.value.map((item) => item.leadId)
    return
  }
  selectedLeadIds.value = []
}

async function submitBatchStatus() {
  if (!hasSelection.value || !query.merchantId.trim()) return
  loadingBatch.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      merchantId: query.merchantId.trim(),
      leadIds: selectedLeadIds.value,
      status: batchForm.toStatus,
      comment: batchForm.comment.trim() || null,
      operator: 'quote-workbench-ui'
    }
    const resp = await fetch('/api/v1/inquiries/merchant/workbench/tasks/status', {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    })
    const json = await resp.json()
    if (json.code !== '0') {
      throw new Error(json.message || '批量更新失败')
    }
    successMsg.value = `已批量更新 ${json.data.total} 条任务`
    await Promise.all([loadOverview(), loadTasks()])
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    loadingBatch.value = false
  }
}

function prevPage() {
  if (query.page <= 1 || loadingTasks.value) return
  query.page -= 1
  loadTasks()
}

function nextPage() {
  if (query.page * query.pageSize >= total.value || loadingTasks.value) return
  query.page += 1
  loadTasks()
}

onMounted(async () => {
  await Promise.all([loadOverview(), loadTasks()])
})
</script>

<template>
  <main class="quote-workbench-page">
    <section class="card hero">
      <h1>P06 报价工作台</h1>
      <p>聚焦“待报价 / 超时 / 批量处理”，提升商家报价效率与线索跟进时效。</p>
    </section>

    <section class="card filter-box">
      <div class="grid">
        <label>
          商家ID
          <input v-model="query.merchantId" placeholder="如 S001" />
        </label>
        <label>
          任务状态
          <select v-model="query.status">
            <option value="">全部</option>
            <option value="NEW">待报价</option>
            <option value="CONTACTED">跟进中</option>
            <option value="QUOTED">已报价</option>
            <option value="WON">已赢单</option>
            <option value="LOST">已丢单</option>
            <option value="CLOSED">已关闭</option>
          </select>
        </label>
        <label>
          交付城市
          <input v-model="query.deliveryCity" placeholder="如 唐山 / 无锡" />
        </label>
        <label>
          关键词
          <input v-model="query.keyword" placeholder="询价单号/规格/城市" />
        </label>
      </div>
      <div class="actions">
        <button class="btn primary" :disabled="loadingOverview || loadingTasks" @click="loadOverview(); loadTasks()">
          查询工作台
        </button>
      </div>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
      <p v-if="successMsg" class="success">{{ successMsg }}</p>
    </section>

    <section class="card overview-box">
      <h2>工作台概览</h2>
      <p v-if="loadingOverview">概览加载中...</p>
      <div v-else class="overview-grid">
        <article>
          <h3>待处理</h3>
          <p>线索总数：{{ overview.totalLeads }}</p>
          <p>待报价：{{ overview.pendingQuoteCount }}</p>
          <p>跟进中：{{ overview.contactedCount }}</p>
        </article>
        <article>
          <h3>报价表现</h3>
          <p>报价率：{{ overview.quoteRate }}</p>
          <p>赢单率：{{ overview.winRate }}</p>
          <p>平均响应：{{ overview.avgResponseMinutes }} 分钟</p>
        </article>
        <article>
          <h3>结果分布</h3>
          <p>新线索：{{ overview.newCount }}</p>
          <p>已报价：{{ overview.quotedCount }}</p>
          <p>赢单：{{ overview.wonCount }} ｜ 丢单：{{ overview.lostCount }} ｜ 关闭：{{ overview.closedCount }}</p>
        </article>
      </div>
    </section>

    <section class="card task-box">
      <div class="head">
        <h2>报价任务列表</h2>
        <label class="inline">
          <input
            type="checkbox"
            :checked="tasks.length > 0 && selectedLeadIds.length === tasks.length"
            @change="toggleSelectAll($event.target.checked)"
          />
          全选
        </label>
      </div>
      <p v-if="loadingTasks">任务加载中...</p>
      <p v-else-if="tasks.length === 0">暂无任务</p>
      <div v-else class="task-list">
        <article v-for="item in tasks" :key="item.leadId" class="task-item">
          <div class="line">
            <label class="inline">
              <input
                type="checkbox"
                :checked="selectedLeadIds.includes(item.leadId)"
                @change="toggleSelect(item.leadId, $event.target.checked)"
              />
              <strong>{{ item.specText }}</strong>
            </label>
            <span class="status">{{ statusLabelMap[item.status] || item.status }}</span>
          </div>
          <p>询价单：{{ item.inquiryNo }} ｜ 城市：{{ item.deliveryCity }} ｜ 数量：{{ item.demandQtyTon }} 吨</p>
          <p>交付日期：{{ item.expectedDeliveryAt || '-' }} ｜ 跟进时长：{{ item.quoteAgeMinutes }} 分钟</p>
          <p>是否已报价：{{ item.hasQuoted ? '是' : '否' }} ｜ 跟进备注：{{ item.latestRemark || '-' }}</p>
        </article>
      </div>
      <div v-if="total > 0" class="actions">
        <button class="btn" :disabled="query.page <= 1 || loadingTasks" @click="prevPage">上一页</button>
        <button class="btn" :disabled="query.page * query.pageSize >= total || loadingTasks" @click="nextPage">
          下一页
        </button>
        <span class="tip">第 {{ query.page }} 页 / 共 {{ Math.max(Math.ceil(total / query.pageSize), 1) }} 页</span>
      </div>
    </section>

    <section class="card batch-box">
      <h2>批量状态处理</h2>
      <p>已选任务：{{ selectedLeadIds.length }}</p>
      <div class="grid">
        <label>
          目标状态
          <select v-model="batchForm.toStatus">
            <option value="CONTACTED">标记为已联系</option>
            <option value="QUOTED">标记为已报价</option>
            <option value="WON">标记为已赢单</option>
            <option value="LOST">标记为已丢单</option>
            <option value="CLOSED">标记为已关闭</option>
          </select>
        </label>
      </div>
      <label>
        备注
        <textarea v-model="batchForm.comment" rows="2" placeholder="如：已统一电话回访"></textarea>
      </label>
      <div class="actions">
        <button class="btn primary" :disabled="!hasSelection || loadingBatch" @click="submitBatchStatus">
          批量更新状态
        </button>
      </div>
    </section>
  </main>
</template>

<style scoped>
.quote-workbench-page {
  max-width: 1120px;
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
  margin: 0 0 6px;
}

.hero p {
  margin: 0;
  color: #4b5563;
}

.grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
}

label {
  display: grid;
  gap: 6px;
  color: #374151;
}

.inline {
  display: inline-flex;
  align-items: center;
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
  margin-top: 12px;
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

.overview-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
}

.overview-grid article {
  border: 1px solid #f2f2f2;
  border-radius: 10px;
  padding: 12px;
}

.overview-grid h3 {
  margin: 0 0 8px;
  font-size: 15px;
}

.overview-grid p {
  margin: 4px 0;
  color: #4b5563;
}

.head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.task-list {
  display: grid;
  gap: 10px;
}

.task-item {
  border: 1px dashed #ececec;
  border-radius: 10px;
  padding: 12px;
}

.task-item .line {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.status {
  font-size: 12px;
  color: #fff;
  background: #f57c00;
  border-radius: 999px;
  padding: 2px 8px;
}

.task-item p {
  margin: 6px 0 0;
  color: #4b5563;
}

.tip {
  color: #4b5563;
}

.error {
  color: #b42318;
  margin-top: 10px;
}

.success {
  color: #0c7a43;
  margin-top: 10px;
}

@media (max-width: 980px) {
  .grid,
  .overview-grid {
    grid-template-columns: 1fr;
  }
}
</style>
