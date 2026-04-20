<script setup>
import { computed, onMounted, reactive, ref } from 'vue'

const loading = ref(false)
const saving = ref(false)
const detailLoading = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

const query = reactive({
  adminToken: 'test-admin-token',
  scheduleStatus: '',
  slotType: '',
  cityCode: '',
  keyword: '',
  page: 1,
  pageSize: 10
})

const state = reactive({
  total: 0,
  page: 1,
  pageSize: 10,
  activeCount: 0,
  draftCount: 0,
  pausedCount: 0,
  soldOutCount: 0,
  records: []
})

const selectedScheduleId = ref('')
const detail = ref(null)
const form = reactive({
  slotCode: '',
  slotName: '',
  slotType: 'HOME_TOP_BANNER',
  cityCode: 'NORTH_CHINA',
  cityName: '',
  scheduleStatus: 'ACTIVE',
  scheduleFillStatus: 'ON_SALE',
  startDate: '',
  endDate: '',
  totalSlots: '1',
  soldSlots: '0',
  pricePerDay: '',
  creativeUrl: '',
  advertiserName: '',
  campaignName: '',
  remark: '',
  operator: 'admn12-admin-ui'
})

const canLoad = computed(() => query.adminToken.trim().length > 0)
const canSave = computed(() => {
  return (
    query.adminToken.trim() &&
    form.slotCode.trim() &&
    form.slotName.trim() &&
    form.slotType.trim() &&
    form.cityCode.trim() &&
    form.scheduleStatus.trim() &&
    form.startDate.trim() &&
    form.endDate.trim() &&
    form.totalSlots.trim() &&
    form.soldSlots.trim() &&
    form.pricePerDay.trim() &&
    form.operator.trim()
  )
})
const totalPages = computed(() => Math.max(Math.ceil(state.total / query.pageSize), 1))

function applyDetailToForm(data) {
  form.slotCode = data?.adSlotCode || ''
  form.slotName = data?.slotName || ''
  form.slotType = data?.slotType || 'HOME_TOP_BANNER'
  form.cityCode = data?.cityCode || 'NORTH_CHINA'
  form.cityName = data?.cityName || ''
  form.scheduleStatus = data?.scheduleStatus || 'ACTIVE'
  form.scheduleFillStatus = data?.fillStatus || 'ON_SALE'
  form.startDate = data?.startDate || ''
  form.endDate = data?.endDate || ''
  form.totalSlots = data?.totalSlots || '1'
  form.soldSlots = data?.soldSlots || '0'
  form.pricePerDay = data?.pricePerDay || ''
  form.creativeUrl = data?.creativeUrl || ''
  form.advertiserName = data?.advertiserName || ''
  form.campaignName = data?.campaignName || ''
  form.remark = data?.remark || ''
}

function resetFormForCreate() {
  selectedScheduleId.value = ''
  detail.value = null
  form.slotCode = ''
  form.slotName = ''
  form.slotType = 'HOME_TOP_BANNER'
  form.cityCode = 'NORTH_CHINA'
  form.cityName = ''
  form.scheduleStatus = 'ACTIVE'
  form.scheduleFillStatus = 'ON_SALE'
  form.startDate = new Date().toISOString().slice(0, 10)
  form.endDate = ''
  form.totalSlots = '1'
  form.soldSlots = '0'
  form.pricePerDay = ''
  form.creativeUrl = ''
  form.advertiserName = ''
  form.campaignName = ''
  form.remark = ''
}

async function loadSchedules() {
  if (!canLoad.value || loading.value) return
  loading.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const params = new URLSearchParams()
    if (query.scheduleStatus.trim()) params.set('scheduleStatus', query.scheduleStatus.trim())
    if (query.slotType.trim()) params.set('slotType', query.slotType.trim())
    if (query.cityCode.trim()) params.set('cityCode', query.cityCode.trim())
    if (query.keyword.trim()) params.set('keyword', query.keyword.trim())
    params.set('page', String(query.page))
    params.set('pageSize', String(query.pageSize))
    const resp = await fetch(`/api/admin/ad-slot-schedules?${params.toString()}`, {
      headers: { 'X-Admin-Token': query.adminToken.trim() }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `加载失败(${resp.status})`)
    }
    const data = json.data || {}
    state.total = Number(data.total || 0)
    state.page = Number(data.page || query.page)
    state.pageSize = Number(data.pageSize || query.pageSize)
    state.activeCount = Number(data.activeCount || 0)
    state.draftCount = Number(data.draftCount || 0)
    state.pausedCount = Number(data.pausedCount || 0)
    state.soldOutCount = Number(data.soldOutCount || 0)
    state.records = Array.isArray(data.records) ? data.records : []
  } catch (error) {
    errorMsg.value = error.message || '加载广告位排期失败'
  } finally {
    loading.value = false
  }
}

async function openDetail(scheduleId) {
  if (!scheduleId || detailLoading.value) return
  detailLoading.value = true
  errorMsg.value = ''
  try {
    const resp = await fetch(`/api/admin/ad-slot-schedules/${encodeURIComponent(scheduleId)}`, {
      headers: { 'X-Admin-Token': query.adminToken.trim() }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `详情加载失败(${resp.status})`)
    }
    const data = json.data || {}
    selectedScheduleId.value = data.scheduleId || scheduleId
    detail.value = data
    applyDetailToForm(data)
  } catch (error) {
    errorMsg.value = error.message || '加载排期详情失败'
  } finally {
    detailLoading.value = false
  }
}

async function saveSchedule() {
  if (!canSave.value || saving.value) return
  saving.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      slotCode: form.slotCode.trim(),
      slotName: form.slotName.trim(),
      slotType: form.slotType.trim().toUpperCase(),
      cityCode: form.cityCode.trim().toUpperCase(),
      cityName: form.cityName.trim() || null,
      scheduleStatus: form.scheduleStatus.trim().toUpperCase(),
      scheduleFillStatus: form.scheduleFillStatus.trim().toUpperCase(),
      startDate: form.startDate.trim(),
      endDate: form.endDate.trim(),
      totalSlots: form.totalSlots.trim(),
      soldSlots: form.soldSlots.trim(),
      pricePerDay: form.pricePerDay.trim(),
      creativeUrl: form.creativeUrl.trim() || null,
      advertiserName: form.advertiserName.trim() || null,
      campaignName: form.campaignName.trim() || null,
      remark: form.remark.trim() || null,
      operator: form.operator.trim()
    }
    const resp = await fetch('/api/admin/ad-slot-schedules', {
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
    const data = json.data || {}
    successMsg.value = `排期 ${data.scheduleNo || data.scheduleId || form.slotCode} 保存成功`
    selectedScheduleId.value = data.scheduleId || ''
    detail.value = data
    applyDetailToForm(data)
    await loadSchedules()
  } catch (error) {
    errorMsg.value = error.message || '保存广告位排期失败'
  } finally {
    saving.value = false
  }
}

function prevPage() {
  if (query.page <= 1 || loading.value) return
  query.page -= 1
  loadSchedules()
}

function nextPage() {
  if (query.page >= totalPages.value || loading.value) return
  query.page += 1
  loadSchedules()
}

onMounted(() => {
  resetFormForCreate()
  loadSchedules()
})
</script>

<template>
  <main class="admn12-page">
    <section class="card hero">
      <h1>ADM-N12 广告位排期中心</h1>
      <p>统一管理广告位在不同城市和时段的排期状态、售卖窗口与库存占用情况。</p>
    </section>

    <section class="card">
      <h2>筛选查询</h2>
      <div class="filters">
        <label>
          Admin Token
          <input v-model="query.adminToken" placeholder="请输入X-Admin-Token" />
        </label>
        <label>
          排期状态
          <select v-model="query.scheduleStatus">
            <option value="">全部</option>
            <option value="ACTIVE">ACTIVE</option>
            <option value="DRAFT">DRAFT</option>
            <option value="PAUSED">PAUSED</option>
          </select>
        </label>
        <label>
          广告位类型
          <select v-model="query.slotType">
            <option value="">全部</option>
            <option value="HOME_TOP_BANNER">HOME_TOP_BANNER</option>
            <option value="CITY_RECOMMEND_CAROUSEL">CITY_RECOMMEND_CAROUSEL</option>
            <option value="LOGISTICS_SERVICE_TILE">LOGISTICS_SERVICE_TILE</option>
            <option value="NEWS_FEED_INSERT">NEWS_FEED_INSERT</option>
          </select>
        </label>
        <label>
          城市
          <select v-model="query.cityCode">
            <option value="">全部</option>
            <option value="NORTH_CHINA">NORTH_CHINA</option>
            <option value="EAST_CHINA">EAST_CHINA</option>
            <option value="SOUTH_CHINA">SOUTH_CHINA</option>
            <option value="CENTRAL_CHINA">CENTRAL_CHINA</option>
            <option value="WEST_CHINA">WEST_CHINA</option>
          </select>
        </label>
        <label class="span-2">
          关键词
          <input v-model="query.keyword" placeholder="排期ID/广告位编码/名称/广告主" />
        </label>
      </div>
      <div class="actions">
        <button class="btn btn--primary" :disabled="!canLoad || loading" @click="loadSchedules">
          {{ loading ? '加载中...' : '查询排期' }}
        </button>
        <button class="btn" @click="resetFormForCreate">新建排期</button>
      </div>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
      <p v-if="successMsg" class="ok">{{ successMsg }}</p>
    </section>

    <section class="card">
      <h2>排期统计</h2>
      <div class="kpi-grid">
        <article class="kpi"><span>排期总数</span><strong>{{ state.total }}</strong></article>
        <article class="kpi"><span>投放中</span><strong>{{ state.activeCount }}</strong></article>
        <article class="kpi"><span>待上线</span><strong>{{ state.draftCount }}</strong></article>
        <article class="kpi"><span>暂停中</span><strong>{{ state.pausedCount }}</strong></article>
        <article class="kpi"><span>已售满</span><strong>{{ state.soldOutCount }}</strong></article>
      </div>
    </section>

    <section class="card">
      <div class="head">
        <h2>排期列表</h2>
        <span class="tip">共 {{ state.total }} 条</span>
      </div>
      <p v-if="loading">列表加载中...</p>
      <p v-else-if="state.records.length === 0">暂无排期数据</p>
      <ul v-else class="schedule-list">
        <li
          v-for="item in state.records"
          :key="item.scheduleId"
          :class="['schedule-item', selectedScheduleId === item.scheduleId ? 'active' : '']"
          @click="openDetail(item.scheduleId)"
        >
          <div class="line">
            <strong>{{ item.slotName }}</strong>
            <span class="badge">{{ item.scheduleCode }}</span>
            <span class="badge">{{ item.slotType }}</span>
            <span class="badge">{{ item.cityCode }}</span>
            <span class="badge" :class="{ warn: item.scheduleStatus !== 'ACTIVE' }">{{
              item.scheduleStatus
            }}</span>
          </div>
          <p class="muted">
            单价 {{ item.unitPriceYuan }} ｜ 总库存 {{ item.totalInventory }} ｜ 锁定
            {{ item.lockedInventory }} ｜ 剩余 {{ item.remainingInventory }}
          </p>
          <p class="muted">排期窗口：{{ item.saleStartAt }} ~ {{ item.saleEndAt }}</p>
        </li>
      </ul>
      <div class="pager">
        <button class="btn" :disabled="query.page <= 1 || loading" @click="prevPage">上一页</button>
        <span>第 {{ query.page }} / {{ totalPages }} 页</span>
        <button class="btn" :disabled="query.page >= totalPages || loading" @click="nextPage">下一页</button>
      </div>
    </section>

    <section class="card">
      <h2>排期编辑</h2>
      <div class="form-grid">
        <label>广告位编码<input v-model="form.slotCode" placeholder="如 HOME_TOP_BANNER" /></label>
        <label>广告位名称<input v-model="form.slotName" placeholder="如 首页焦点大图位" /></label>
        <label>
          广告位类型
          <select v-model="form.slotType">
            <option value="HOME_TOP_BANNER">HOME_TOP_BANNER</option>
            <option value="CITY_RECOMMEND_CAROUSEL">CITY_RECOMMEND_CAROUSEL</option>
            <option value="LOGISTICS_SERVICE_TILE">LOGISTICS_SERVICE_TILE</option>
            <option value="NEWS_FEED_INSERT">NEWS_FEED_INSERT</option>
          </select>
        </label>
        <label>
          城市编码
          <select v-model="form.cityCode">
            <option value="NORTH_CHINA">NORTH_CHINA</option>
            <option value="EAST_CHINA">EAST_CHINA</option>
            <option value="SOUTH_CHINA">SOUTH_CHINA</option>
            <option value="CENTRAL_CHINA">CENTRAL_CHINA</option>
            <option value="WEST_CHINA">WEST_CHINA</option>
          </select>
        </label>
        <label>城市名称（可空）<input v-model="form.cityName" placeholder="如 唐山" /></label>
        <label>
          排期状态
          <select v-model="form.scheduleStatus">
            <option value="ACTIVE">ACTIVE</option>
            <option value="DRAFT">DRAFT</option>
            <option value="PAUSED">PAUSED</option>
          </select>
        </label>
        <label>
          售卖状态
          <select v-model="form.scheduleFillStatus">
            <option value="ON_SALE">ON_SALE</option>
            <option value="PARTIAL">PARTIAL</option>
            <option value="SOLD">SOLD</option>
            <option value="OFFLINE">OFFLINE</option>
          </select>
        </label>
        <label>开始日期<input v-model="form.startDate" placeholder="YYYY-MM-DD" /></label>
        <label>结束日期<input v-model="form.endDate" placeholder="YYYY-MM-DD" /></label>
        <label>总库存<input v-model="form.totalSlots" placeholder="如 3" /></label>
        <label>已售库存<input v-model="form.soldSlots" placeholder="如 1" /></label>
        <label>单价/天<input v-model="form.pricePerDay" placeholder="如 ¥9800/天" /></label>
        <label class="span-2">
          素材URL
          <input v-model="form.creativeUrl" placeholder="可空" />
        </label>
        <label>广告主<input v-model="form.advertiserName" placeholder="可空" /></label>
        <label>活动名称<input v-model="form.campaignName" placeholder="可空" /></label>
        <label>操作人<input v-model="form.operator" /></label>
        <label class="span-2">
          备注
          <input v-model="form.remark" placeholder="可空" />
        </label>
      </div>
      <div class="actions">
        <button class="btn btn--primary" :disabled="!canSave || saving" @click="saveSchedule">
          {{ saving ? '保存中...' : '保存排期' }}
        </button>
      </div>
    </section>

    <section class="card" v-if="detail">
      <h2>排期详情</h2>
      <p class="muted">
        排期ID：{{ detail.scheduleId }} ｜ 更新时间：{{ detail.updatedAt }} ｜ 可执行动作：{{
          detail.availableActions?.join(' / ') || '-'
        }}
      </p>
      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>#</th>
              <th>开始</th>
              <th>结束</th>
              <th>窗口状态</th>
              <th>状态文案</th>
              <th>占用方</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in detail.scheduleWindows || []" :key="`${item.index}-${item.startDate}`">
              <td>{{ item.index }}</td>
              <td>{{ item.startDate }}</td>
              <td>{{ item.endDate }}</td>
              <td>{{ item.windowStatus }}</td>
              <td>{{ item.windowStatusText }}</td>
              <td>{{ item.bookedBy || '-' }}</td>
            </tr>
            <tr v-if="!detail.scheduleWindows || detail.scheduleWindows.length === 0">
              <td colspan="6">暂无窗口信息</td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </main>
</template>

<style scoped>
.admn12-page {
  max-width: 1200px;
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
  margin: 0;
}
.hero p {
  margin: 8px 0 0;
  color: #4b5563;
}
.filters,
.form-grid {
  display: grid;
  gap: 10px;
  grid-template-columns: repeat(4, minmax(0, 1fr));
}
.form-grid .span-2,
.filters .span-2 {
  grid-column: span 2;
}
label {
  display: grid;
  gap: 6px;
  color: #374151;
}
input,
select {
  width: 100%;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 8px 10px;
  font: inherit;
  box-sizing: border-box;
}
.actions {
  margin-top: 12px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
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
.kpi-grid {
  display: grid;
  gap: 10px;
  grid-template-columns: repeat(5, minmax(0, 1fr));
}
.kpi {
  background: #f8fafc;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  padding: 10px;
  display: grid;
  gap: 6px;
}
.kpi span {
  font-size: 12px;
  color: #6b7280;
}
.kpi strong {
  font-size: 20px;
  color: #111827;
}
.tip,
.muted {
  color: #6b7280;
  margin: 6px 0 0;
}
.head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  margin-bottom: 10px;
}
.schedule-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: grid;
  gap: 10px;
}
.schedule-item {
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  padding: 10px;
  cursor: pointer;
}
.schedule-item.active {
  border-color: #f57c00;
  box-shadow: 0 0 0 1px #f57c0030 inset;
}
.line {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}
.badge {
  background: #eef2ff;
  color: #3730a3;
  border-radius: 999px;
  padding: 2px 8px;
  font-size: 12px;
}
.badge.warn {
  background: #fff5f5;
  color: #b42318;
}
.pager {
  margin-top: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
}
.table-wrap {
  overflow: auto;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
}
table {
  width: 100%;
  border-collapse: collapse;
  min-width: 680px;
}
th,
td {
  border-bottom: 1px solid #f1f5f9;
  text-align: left;
  vertical-align: middle;
  padding: 8px;
}

@media (max-width: 1024px) {
  .filters,
  .form-grid,
  .kpi-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 720px) {
  .filters,
  .form-grid,
  .kpi-grid {
    grid-template-columns: 1fr;
  }
  .form-grid .span-2,
  .filters .span-2 {
    grid-column: span 1;
  }
}
</style>
