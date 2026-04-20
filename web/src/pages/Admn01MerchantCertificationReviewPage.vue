<script setup>
import { computed, onMounted, reactive, ref } from 'vue'

const loading = ref(false)
const reviewing = ref(false)
const errorMsg = ref('')
const successMsg = ref('')
const selected = ref(null)

const query = reactive({
  adminToken: 'test-admin-token',
  status: '',
  keyword: '',
  page: 1,
  pageSize: 10
})

const state = reactive({
  records: [],
  total: 0,
  page: 1,
  pageSize: 10,
  status: '',
  keyword: ''
})

const reviewForm = reactive({
  action: 'APPROVE',
  reviewRemark: '',
  reviewer: 'admn01-admin'
})

const canLoad = computed(() => query.adminToken.trim().length > 0)
const canReview = computed(() => selected.value && reviewForm.action.trim())

async function loadList() {
  if (!canLoad.value || loading.value) return
  loading.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const params = new URLSearchParams()
    if (query.status) params.set('status', query.status)
    if (query.keyword.trim()) params.set('keyword', query.keyword.trim())
    params.set('page', String(query.page))
    params.set('pageSize', String(query.pageSize))
    const resp = await fetch(`/api/admin/merchant-certifications?${params.toString()}`, {
      headers: { 'X-Admin-Token': query.adminToken.trim() }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `认证审核列表加载失败(${resp.status})`)
    }
    const data = json.data || {}
    state.records = Array.isArray(data.records) ? data.records : []
    state.total = Number(data.total || 0)
    state.page = Number(data.page || query.page)
    state.pageSize = Number(data.pageSize || query.pageSize)
    state.status = data.status || query.status
    state.keyword = data.keyword || query.keyword
    if (selected.value && !state.records.some((item) => item.certificationId === selected.value.certificationId)) {
      selected.value = null
    }
  } catch (error) {
    errorMsg.value = error.message || '认证审核列表加载失败'
  } finally {
    loading.value = false
  }
}

async function openDetail(row) {
  if (!row?.certificationId || loading.value) return
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const resp = await fetch(`/api/admin/merchant-certifications/${row.certificationId}`, {
      headers: { 'X-Admin-Token': query.adminToken.trim() }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `认证详情加载失败(${resp.status})`)
    }
    selected.value = json.data || null
    reviewForm.action = selected.value?.status === 'APPROVED' ? 'APPROVE' : 'REJECT'
    reviewForm.reviewRemark = ''
  } catch (error) {
    errorMsg.value = error.message || '认证详情加载失败'
  }
}

async function submitReview() {
  if (!canReview.value || reviewing.value) return
  reviewing.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      action: reviewForm.action,
      reviewRemark: reviewForm.reviewRemark.trim() || null,
      reviewer: reviewForm.reviewer.trim() || 'admn01-admin'
    }
    const resp = await fetch(`/api/admin/merchant-certifications/${selected.value.certificationId}/review`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Admin-Token': query.adminToken.trim()
      },
      body: JSON.stringify(payload)
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `审核提交失败(${resp.status})`)
    }
    selected.value = json.data || null
    successMsg.value = reviewForm.action === 'APPROVE' ? '审核已通过' : '已驳回并退回补充'
    await loadList()
  } catch (error) {
    errorMsg.value = error.message || '审核提交失败'
  } finally {
    reviewing.value = false
  }
}

function nextPage() {
  if (query.page * query.pageSize >= state.total || loading.value) return
  query.page += 1
  loadList()
}

function prevPage() {
  if (query.page <= 1 || loading.value) return
  query.page -= 1
  loadList()
}

onMounted(() => {
  loadList()
})
</script>

<template>
  <main class="admn01-page">
    <section class="card hero">
      <h1>ADM-N01 商家认证审核台</h1>
      <p>管理端统一处理商家企业认证申请，支持检索、查看详情与审核通过/驳回。</p>
    </section>

    <section class="card">
      <div class="filters">
        <label>
          Admin Token
          <input v-model="query.adminToken" placeholder="请输入X-Admin-Token" />
        </label>
        <label>
          审核状态
          <select v-model="query.status">
            <option value="">全部</option>
            <option value="PENDING_REVIEW">待审核</option>
            <option value="APPROVED">已通过</option>
            <option value="REJECTED">已驳回</option>
          </select>
        </label>
        <label>
          关键词
          <input v-model="query.keyword" placeholder="公司名/统一信用代码/联系人" />
        </label>
      </div>
      <div class="actions">
        <button class="btn btn--primary" :disabled="!canLoad || loading" @click="loadList">
          {{ loading ? '加载中...' : '查询审核单' }}
        </button>
      </div>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
      <p v-if="successMsg" class="ok">{{ successMsg }}</p>
    </section>

    <section class="stats">
      <article class="stat">
        <span>当前页</span>
        <strong>{{ state.page }}</strong>
      </article>
      <article class="stat">
        <span>每页条数</span>
        <strong>{{ state.pageSize }}</strong>
      </article>
      <article class="stat">
        <span>状态筛选</span>
        <strong>{{ state.status || '全部' }}</strong>
      </article>
      <article class="stat">
        <span>总数</span>
        <strong>{{ state.total }}</strong>
      </article>
    </section>

    <section class="card">
      <h2>认证审核列表</h2>
      <p v-if="loading">加载中...</p>
      <p v-else-if="state.records.length === 0">暂无认证记录</p>
      <ul v-else class="list">
        <li v-for="row in state.records" :key="row.certificationId" class="item">
          <div>
            <h3>{{ row.companyName }}</h3>
            <p>认证单号：{{ row.certificationId }} ｜ 状态：{{ row.statusText }}</p>
            <p>联系人：{{ row.contactName }} {{ row.contactMobileMasked }}</p>
            <p>统一信用代码：{{ row.unifiedSocialCreditCode }}</p>
            <p>提交时间：{{ row.submittedAt || '-' }} ｜ 更新时间：{{ row.updatedAt || '-' }}</p>
            <p>审核备注：{{ row.operator || '-' }}</p>
          </div>
          <button class="btn" @click="openDetail(row)">查看详情</button>
        </li>
      </ul>
      <div class="actions" v-if="state.total > 0">
        <button class="btn" :disabled="query.page <= 1 || loading" @click="prevPage">上一页</button>
        <button class="btn" :disabled="query.page * query.pageSize >= state.total || loading" @click="nextPage">下一页</button>
        <span>第 {{ query.page }} 页 / 共 {{ Math.max(Math.ceil(state.total / query.pageSize), 1) }} 页</span>
      </div>
    </section>

    <section class="card" v-if="selected">
      <h2>审核详情</h2>
      <p>企业：{{ selected.companyName }}</p>
      <p>法人：{{ selected.legalPersonName }}（{{ selected.legalPersonIdNoMasked }}）</p>
      <p>开户行：{{ selected.bankName }} ｜ 账号：{{ selected.bankAccountNoMasked }}</p>
      <p>证照：{{ selected.businessLicenseUrl }}</p>
      <p>法人证件：{{ selected.legalIdFrontUrl }} / {{ selected.legalIdBackUrl }}</p>
      <p>地址：{{ selected.province }} {{ selected.city }} {{ selected.address }}</p>
      <p>申请备注：{{ selected.latestRemark || '-' }}</p>
      <p>审核人：{{ selected.operator || '-' }} ｜ 审核时间：{{ selected.updatedAt || '-' }}</p>

      <div class="review-box">
        <label>
          审核动作
          <select v-model="reviewForm.action">
            <option value="APPROVE">审核通过</option>
            <option value="REJECT">驳回补充</option>
          </select>
        </label>
        <label>
          审核意见
          <textarea v-model="reviewForm.reviewRemark" rows="3" placeholder="可填写审核说明"></textarea>
        </label>
        <button class="btn btn--primary" :disabled="!canReview || reviewing" @click="submitReview">
          {{ reviewing ? '提交中...' : '提交审核结果' }}
        </button>
      </div>
    </section>
  </main>
</template>

<style scoped>
.admn01-page {
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
  .item {
    flex-direction: column;
  }
}
</style>
