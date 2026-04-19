<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const errorMsg = ref('')
const submitMsg = ref('')

const queryForm = reactive({
  contactPhone: '',
  keyword: '',
  page: 1,
  pageSize: 10
})

const list = ref([])

const statusMap = {
  SUBMITTED: '已提交',
  ASSIGNED: '已分配',
  CONTACTED: '已联系',
  PROPOSAL_SENT: '已提案',
  CONVERTED: '已成交',
  CLOSED: '已关闭'
}

const hasResult = computed(() => list.value.length > 0)

async function queryMine() {
  loading.value = true
  errorMsg.value = ''
  submitMsg.value = ''
  list.value = []
  try {
    const params = new URLSearchParams()
    if (queryForm.contactPhone.trim()) params.set('contactPhone', queryForm.contactPhone.trim())
    if (queryForm.keyword.trim()) params.set('keyword', queryForm.keyword.trim())
    params.set('page', String(queryForm.page))
    params.set('pageSize', String(queryForm.pageSize))
    const resp = await fetch(`/api/v1/site-ad-lead/mine?${params.toString()}`)
    const json = await resp.json()
    if (json.code !== '0') {
      throw new Error(json.message || '查询失败')
    }
    list.value = json.data.items || []
    submitMsg.value = `共找到 ${json.data.total || 0} 条投放单`
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    loading.value = false
  }
}

function toPublish() {
  router.push('/site/ad/submit')
}

onMounted(() => {
  document.title = '我的投放单_分站广告线索管理-货袋子'
  const phoneFromQuery = String(route.query.contactPhone || '').trim()
  if (phoneFromQuery) {
    queryForm.contactPhone = phoneFromQuery
    queryMine()
  }
})
</script>

<template>
  <main class="lead-mine-page">
    <section class="card hero">
      <h1>我的投放单（P18 运营闭环）</h1>
      <p>查询已提交广告线索，跟踪状态、负责人和最新跟进记录。</p>
    </section>

    <section class="card query-box">
      <div class="row">
        <label>
          手机号
          <input v-model="queryForm.contactPhone" type="text" placeholder="请输入提交时手机号（11位）" />
        </label>
        <label>
          关键词
          <input v-model="queryForm.keyword" type="text" placeholder="可输入投放单号/广告位/公司名称" />
        </label>
      </div>
      <div class="row actions">
        <button class="btn btn--primary" :disabled="loading" @click="queryMine">查询我的投放单</button>
        <button class="btn btn--ghost" @click="toPublish">继续提交投放需求</button>
      </div>
      <p v-if="submitMsg" class="tip">{{ submitMsg }}</p>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
    </section>

    <section class="card list-box">
      <h2>线索列表</h2>
      <p v-if="loading">加载中...</p>
      <p v-else-if="!hasResult">暂无数据，请先提交投放需求后再查询。</p>
      <ul v-else class="lead-list">
        <li v-for="item in list" :key="item.id" class="lead-item">
          <div class="head">
            <h3>{{ item.placementName }} · {{ item.city }}</h3>
            <span class="status">{{ statusMap[item.status] || item.status }}</span>
          </div>
          <p>投放单号：{{ item.leadNo }}</p>
          <p>投放周期：{{ item.duration }} ｜ 预算：{{ item.budget || '-' }}</p>
          <p>负责人：{{ item.owner || '待分配' }} ｜ 下次跟进：{{ item.nextActionAt || '-' }}</p>
          <p>更新时间：{{ item.updatedAt }}</p>
          <p>最新跟进：{{ item.followLogs?.length ? item.followLogs[0].content : '暂无跟进记录' }}</p>
        </li>
      </ul>
    </section>
  </main>
</template>

<style scoped>
.lead-mine-page {
  max-width: 1120px;
  margin: 0 auto;
  padding: 24px 16px 40px;
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
  color: #666;
}
.row {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}
label {
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-width: 260px;
  flex: 1;
}
input {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 9px 10px;
}
.actions {
  margin-top: 12px;
}
.btn {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 9px 14px;
  background: #fff;
  cursor: pointer;
}
.btn--primary {
  border-color: #f57c00;
  background: #f57c00;
  color: #fff;
}
.btn--ghost {
  color: #666;
}
.tip {
  color: #0c7a43;
  margin-top: 10px;
}
.error {
  color: #b42318;
  margin-top: 10px;
}
.lead-list {
  list-style: none;
  margin: 0;
  padding: 0;
}
.lead-item {
  border-top: 1px dashed #ececec;
  padding: 12px 0;
}
.lead-item:first-child {
  border-top: 0;
  padding-top: 4px;
}
.head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.head h3 {
  margin: 0;
  font-size: 16px;
}
.status {
  font-size: 12px;
  color: #fff;
  background: #f57c00;
  padding: 2px 8px;
  border-radius: 999px;
}
.lead-item p {
  margin: 6px 0 0;
  color: #555;
}
</style>
