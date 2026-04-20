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
  accountStatus: '',
  blacklistStatus: '',
  page: 1,
  pageSize: 10
})

const state = reactive({
  records: [],
  total: 0,
  page: 1,
  pageSize: 10,
  blacklistedCount: 0,
  normalCount: 0,
  accountStatus: '',
  blacklistStatus: '',
  keyword: ''
})

const form = reactive({
  action: 'BLACKLIST',
  reasonCode: 'RISK_CONTROL',
  remark: '',
  operator: 'admn02-admin'
})

const canLoad = computed(() => query.adminToken.trim().length > 0)
const canSubmit = computed(() => selected.value && form.action.trim().length > 0)
const totalPages = computed(() => Math.max(Math.ceil(state.total / query.pageSize), 1))

async function loadList() {
  if (!canLoad.value || loading.value) return
  loading.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const params = new URLSearchParams()
    if (query.keyword.trim()) params.set('keyword', query.keyword.trim())
    if (query.accountStatus) params.set('accountStatus', query.accountStatus)
    if (query.blacklistStatus) params.set('blacklistStatus', query.blacklistStatus)
    params.set('page', String(query.page))
    params.set('pageSize', String(query.pageSize))
    const resp = await fetch(`/api/admin/buyers?${params.toString()}`, {
      headers: { 'X-Admin-Token': query.adminToken.trim() }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `买家列表加载失败(${resp.status})`)
    }
    const data = json.data || {}
    state.records = Array.isArray(data.records) ? data.records : []
    state.total = Number(data.total || 0)
    state.page = Number(data.page || query.page)
    state.pageSize = Number(data.pageSize || query.pageSize)
    state.blacklistedCount = Number(data.blacklistedCount || 0)
    state.normalCount = Number(data.normalCount || 0)
    state.accountStatus = data.accountStatus || query.accountStatus
    state.blacklistStatus = data.blacklistStatus || query.blacklistStatus
    state.keyword = data.keyword || query.keyword
    if (selected.value && !state.records.some((item) => item.userId === selected.value.userId)) {
      selected.value = null
    }
  } catch (error) {
    errorMsg.value = error.message || '买家列表加载失败'
  } finally {
    loading.value = false
  }
}

async function openDetail(row) {
  if (!row?.userId || loading.value) return
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const resp = await fetch(`/api/admin/buyers/${row.userId}`, {
      headers: { 'X-Admin-Token': query.adminToken.trim() }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `买家详情加载失败(${resp.status})`)
    }
    selected.value = json.data || null
    if (selected.value?.blacklisted) {
      form.action = 'UNBLACKLIST'
      form.reasonCode = 'RECOVERED'
      form.remark = '风控复核通过，解除限制'
    } else {
      form.action = 'BLACKLIST'
      form.reasonCode = 'RISK_CONTROL'
      form.remark = '疑似异常交易行为，暂时限制'
    }
  } catch (error) {
    errorMsg.value = error.message || '买家详情加载失败'
  }
}

async function submitAction() {
  if (!canSubmit.value || submitting.value) return
  submitting.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      action: form.action,
      reasonCode: form.reasonCode.trim() || null,
      remark: form.remark.trim() || null,
      operator: form.operator.trim() || 'admn02-admin'
    }
    const resp = await fetch(`/api/admin/buyers/${selected.value.userId}/blacklist`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'X-Admin-Token': query.adminToken.trim()
      },
      body: JSON.stringify(payload)
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `提交失败(${resp.status})`)
    }
    selected.value = json.data || null
    successMsg.value = form.action === 'BLACKLIST' ? '买家已加入黑名单' : '买家已解除黑名单'
    await loadList()
  } catch (error) {
    errorMsg.value = error.message || '提交失败'
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
  <main class="admn02-page">
    <section class="card hero">
      <h1>ADM-N02 买家与黑名单管理</h1>
      <p>统一查看买家账户状态与风控分层，支持拉黑/解除拉黑操作闭环。</p>
    </section>

    <section class="card">
      <div class="filters">
        <label>
          Admin Token
          <input v-model="query.adminToken" placeholder="请输入X-Admin-Token" />
        </label>
        <label>
          账户状态
          <select v-model="query.accountStatus">
            <option value="">全部</option>
            <option value="ACTIVE">正常</option>
            <option value="DISABLED">已禁用</option>
          </select>
        </label>
        <label>
          黑名单状态
          <select v-model="query.blacklistStatus">
            <option value="">全部</option>
            <option value="NORMAL">正常</option>
            <option value="BLACKLISTED">黑名单中</option>
          </select>
        </label>
        <label class="span-2">
          关键词
          <input v-model="query.keyword" placeholder="userId/手机号/企业名/联系人" />
        </label>
      </div>
      <div class="actions">
        <button class="btn btn--primary" :disabled="!canLoad || loading" @click="loadList">
          {{ loading ? '加载中...' : '查询买家' }}
        </button>
      </div>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
      <p v-if="successMsg" class="ok">{{ successMsg }}</p>
    </section>

    <section class="stats">
      <article class="stat">
        <span>买家总数</span>
        <strong>{{ state.total }}</strong>
      </article>
      <article class="stat">
        <span>正常买家</span>
        <strong>{{ state.normalCount }}</strong>
      </article>
      <article class="stat">
        <span>黑名单买家</span>
        <strong>{{ state.blacklistedCount }}</strong>
      </article>
      <article class="stat">
        <span>当前页</span>
        <strong>{{ query.page }}</strong>
      </article>
    </section>

    <section class="card">
      <h2>买家列表</h2>
      <p v-if="loading">加载中...</p>
      <p v-else-if="state.records.length === 0">暂无买家记录</p>
      <ul v-else class="list">
        <li v-for="row in state.records" :key="row.userId" class="item">
          <div>
            <h3>{{ row.companyName || '-' }}</h3>
            <p>买家ID：{{ row.userId }} ｜ 账号：{{ row.accountMasked }}</p>
            <p>联系人：{{ row.contactName || '-' }} {{ row.contactMobileMasked || '-' }}</p>
            <p>账户状态：{{ row.accountStatusText }} ｜ 黑名单：{{ row.blacklistStatusText }}</p>
            <p>风险等级：{{ row.riskLevel }} ｜ 最近订单：{{ row.lastOrderAt || '-' }}</p>
            <p>更新时间：{{ row.updatedAt || '-' }}</p>
          </div>
          <button class="btn" @click="openDetail(row)">查看并处理</button>
        </li>
      </ul>
      <div class="actions" v-if="state.total > 0">
        <button class="btn" :disabled="query.page <= 1 || loading" @click="prevPage">上一页</button>
        <button class="btn" :disabled="query.page >= totalPages || loading" @click="nextPage">下一页</button>
        <span>第 {{ query.page }} 页 / 共 {{ totalPages }} 页</span>
      </div>
    </section>

    <section class="card" v-if="selected">
      <h2>买家详情与黑名单操作</h2>
      <p>买家ID：{{ selected.userId }}</p>
      <p>企业名称：{{ selected.companyName || '-' }}</p>
      <p>联系人：{{ selected.contactName || '-' }} {{ selected.contactMobileMasked || '-' }}</p>
      <p>账户状态：{{ selected.accountStatusText }} ｜ 黑名单状态：{{ selected.blacklistStatusText }}</p>
      <p>风控等级：{{ selected.riskLevel }} ｜ 最近订单：{{ selected.lastOrderAt || '-' }}</p>
      <p>最近拉黑原因：{{ selected.latestBlacklistReason || '-' }}</p>
      <p>操作人：{{ selected.latestBlacklistOperator || '-' }} ｜ 操作时间：{{ selected.latestBlacklistAt || '-' }}</p>
      <p>备注：{{ selected.latestBlacklistRemark || '-' }}</p>

      <div class="review-box">
        <label>
          操作动作
          <select v-model="form.action">
            <option value="BLACKLIST">加入黑名单</option>
            <option value="UNBLACKLIST">解除黑名单</option>
          </select>
        </label>
        <label>
          原因编码
          <input v-model="form.reasonCode" placeholder="示例：RISK_CONTROL / RECOVERED" />
        </label>
        <label>
          操作说明
          <textarea v-model="form.remark" rows="3" placeholder="请输入处置说明"></textarea>
        </label>
        <button class="btn btn--primary" :disabled="!canSubmit || submitting" @click="submitAction">
          {{ submitting ? '提交中...' : '提交处置' }}
        </button>
      </div>
    </section>
  </main>
</template>

<style scoped>
.admn02-page {
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
.filters {
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
  margin: 5px 0;
}
.review-box {
  margin-top: 10px;
  display: grid;
  gap: 10px;
}
@media (max-width: 960px) {
  .filters,
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
