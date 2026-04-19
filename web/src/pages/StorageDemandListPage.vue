<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const loading = ref(false)
const errorMsg = ref('')
const total = ref(0)
const list = ref([])

const filterOptions = reactive({
  cityOptions: [],
  goodsCategoryOptions: [],
  serviceNeedOptions: []
})

const filters = reactive({
  city: '',
  goodsCategory: '',
  serviceNeed: '',
  keyword: '',
  page: 1,
  pageSize: 10
})

const pageCount = computed(() => {
  if (total.value <= 0) {
    return 1
  }
  return Math.ceil(total.value / filters.pageSize)
})

async function fetchFilterOptions() {
  const resp = await fetch('/api/v1/storage-demand/filters')
  const json = await resp.json()
  if (json.code !== '0') {
    throw new Error(json.message || '获取筛选项失败')
  }
  filterOptions.cityOptions = json.data.cityOptions || []
  filterOptions.goodsCategoryOptions = json.data.goodsCategoryOptions || []
  filterOptions.serviceNeedOptions = json.data.serviceNeedOptions || []
}

function buildQuery() {
  const params = new URLSearchParams()
  if (filters.city) params.set('city', filters.city)
  if (filters.goodsCategory) params.set('goodsCategory', filters.goodsCategory)
  if (filters.serviceNeed) params.set('serviceNeed', filters.serviceNeed)
  if (filters.keyword.trim()) params.set('keyword', filters.keyword.trim())
  params.set('page', String(filters.page))
  params.set('pageSize', String(filters.pageSize))
  return params.toString()
}

async function fetchList() {
  loading.value = true
  errorMsg.value = ''
  try {
    const resp = await fetch(`/api/v1/storage-demand?${buildQuery()}`)
    const json = await resp.json()
    if (json.code !== '0') {
      throw new Error(json.message || '获取列表失败')
    }
    list.value = json.data.items || []
    total.value = json.data.total || 0
    filters.page = json.data.page || 1
    filters.pageSize = json.data.pageSize || 10
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
    list.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

async function refresh() {
  await fetchList()
}

async function resetFilters() {
  filters.city = ''
  filters.goodsCategory = ''
  filters.serviceNeed = ''
  filters.keyword = ''
  filters.page = 1
  await fetchList()
}

async function onSearch() {
  filters.page = 1
  await fetchList()
}

async function prevPage() {
  if (filters.page <= 1) return
  filters.page -= 1
  await fetchList()
}

async function nextPage() {
  if (filters.page >= pageCount.value) return
  filters.page += 1
  await fetchList()
}

function gotoPublish() {
  router.push('/logistics/demand/storage/new')
}

onMounted(async () => {
  document.title = '仓储需求大厅_钢铁仓储需求查询-货袋子'
  try {
    await fetchFilterOptions()
    await fetchList()
  } catch (error) {
    errorMsg.value = error.message || '初始化失败'
  }
})
</script>

<template>
  <main class="storage-demand-list">
    <section class="hero">
      <h1>仓储需求大厅（P11-2）</h1>
      <p>按城市、品类、服务需求快速筛选仓储需求，支持查看详情与对接。</p>
    </section>

    <section class="filters card">
      <div class="row">
        <label>
          城市
          <select v-model="filters.city">
            <option value="">全部</option>
            <option v-for="item in filterOptions.cityOptions" :key="item" :value="item">{{ item }}</option>
          </select>
        </label>

        <label>
          品类
          <select v-model="filters.goodsCategory">
            <option value="">全部</option>
            <option v-for="item in filterOptions.goodsCategoryOptions" :key="item" :value="item">{{ item }}</option>
          </select>
        </label>

        <label>
          服务需求
          <select v-model="filters.serviceNeed">
            <option value="">全部</option>
            <option v-for="item in filterOptions.serviceNeedOptions" :key="item" :value="item">{{ item }}</option>
          </select>
        </label>
      </div>

      <div class="row">
        <label class="keyword">
          关键词
          <input v-model="filters.keyword" placeholder="输入标题/公司关键词" />
        </label>
        <button @click="onSearch">搜索</button>
        <button class="ghost" @click="resetFilters">重置</button>
        <button class="primary" @click="gotoPublish">发布仓储需求</button>
        <button class="ghost" @click="refresh">刷新</button>
      </div>
    </section>

    <section v-if="errorMsg" class="error card">{{ errorMsg }}</section>

    <section class="list card">
      <div class="head">
        <h2>需求列表</h2>
        <span>共 {{ total }} 条</span>
      </div>
      <p v-if="loading">加载中...</p>
      <ul v-else-if="list.length">
        <li v-for="item in list" :key="item.id">
          <h3>{{ item.title }}</h3>
          <p>{{ item.city }} · {{ item.goodsCategory }} · {{ item.tonnage }}吨 · {{ item.storageDays }}天</p>
          <p>{{ item.serviceNeed }} · {{ item.companyName }}</p>
          <p>联系人：{{ item.contactNameMasked }}（{{ item.contactPhoneMasked }}）</p>
          <small>更新时间：{{ item.updatedAt }}</small>
        </li>
      </ul>
      <p v-else>暂无数据</p>

      <div class="pager">
        <button :disabled="filters.page <= 1 || loading" @click="prevPage">上一页</button>
        <span>第 {{ filters.page }} / {{ pageCount }} 页</span>
        <button :disabled="filters.page >= pageCount || loading" @click="nextPage">下一页</button>
      </div>
    </section>
  </main>
</template>

<style scoped>
.storage-demand-list {
  max-width: 1120px;
  margin: 0 auto;
  padding: 24px 16px 40px;
}
.hero {
  margin-bottom: 16px;
}
.card {
  background: #fff;
  border: 1px solid #ececec;
  border-radius: 10px;
  padding: 16px;
  margin-bottom: 16px;
}
.row {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  margin-bottom: 10px;
}
label {
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-width: 180px;
}
.keyword {
  min-width: 280px;
  flex: 1;
}
select,
input,
button {
  height: 36px;
  border: 1px solid #d7d7d7;
  border-radius: 6px;
  padding: 0 10px;
}
button {
  cursor: pointer;
  background: #fff;
}
button.primary {
  background: #f57c00;
  color: #fff;
  border-color: #f57c00;
}
button.ghost {
  color: #666;
}
.head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
ul {
  list-style: none;
  padding: 0;
  margin: 0;
}
li {
  padding: 12px 0;
  border-bottom: 1px dashed #e6e6e6;
}
li:last-child {
  border-bottom: 0;
}
li h3 {
  margin: 0 0 6px;
}
li p {
  margin: 0 0 4px;
  color: #444;
}
li small {
  color: #888;
}
.pager {
  margin-top: 10px;
  display: flex;
  gap: 10px;
  align-items: center;
}
.error {
  color: #b42318;
}
</style>
