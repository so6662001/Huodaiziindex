<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const errorMsg = ref('')
const list = ref([])
const summary = reactive({
  inquiryNo: '',
  specText: '',
  demandQtyTon: '',
  deliveryCity: ''
})
const pager = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})
const filters = reactive({
  inquiryId: '',
  contactMobile: '',
  deliveryCycle: '',
  invoiceType: '',
  sortBy: 'TOTAL_PRICE'
})

const totalPages = computed(() => Math.max(Math.ceil(pager.total / pager.pageSize), 1))
const canQuery = computed(() => filters.inquiryId.trim().length > 0 && /^1\d{10}$/.test(filters.contactMobile.trim()))

async function queryCompare() {
  if (!canQuery.value || loading.value) return
  loading.value = true
  errorMsg.value = ''
  try {
    const params = new URLSearchParams()
    params.set('inquiryId', filters.inquiryId.trim())
    params.set('contactMobile', filters.contactMobile.trim())
    params.set('sortBy', filters.sortBy)
    if (filters.deliveryCycle) params.set('deliveryCycle', filters.deliveryCycle)
    if (filters.invoiceType) params.set('invoiceType', filters.invoiceType)
    params.set('page', String(pager.page))
    params.set('pageSize', String(pager.pageSize))
    const resp = await fetch(`/api/v1/inquiries/compare?${params.toString()}`)
    const json = await resp.json()
    if (json.code !== '0') {
      throw new Error(json.message || '查询报价对比失败')
    }
    list.value = json.data.quotes || []
    summary.inquiryNo = json.data.inquiryNo || ''
    summary.specText = json.data.specText || ''
    summary.demandQtyTon = json.data.demandQtyTon || ''
    summary.deliveryCity = json.data.deliveryCity || ''
    pager.total = Number(json.data.total || 0)
    pager.page = Number(json.data.page || pager.page)
    pager.pageSize = Number(json.data.pageSize || pager.pageSize)
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    loading.value = false
  }
}

function prevPage() {
  if (pager.page <= 1 || loading.value) return
  pager.page -= 1
  queryCompare()
}

function nextPage() {
  if (pager.page >= totalPages.value || loading.value) return
  pager.page += 1
  queryCompare()
}

function quoteRate(item) {
  const total = Number(item.totalAmount || 0)
  if (!total) return '-'
  const unit = Number(item.unitPrice || 0)
  if (!unit) return '-'
  return `${unit.toFixed(2)} / 吨`
}

function goDealConfirm(item) {
  if (!item?.quoteId || !filters.inquiryId.trim() || !filters.contactMobile.trim()) {
    return
  }
  const params = new URLSearchParams()
  params.set('inquiryId', filters.inquiryId.trim())
  params.set('quoteId', item.quoteId)
  params.set('contactMobile', filters.contactMobile.trim())
  router.push(`/inquiry/deal/confirm?${params.toString()}`)
}

onMounted(() => {
  const inquiryId = String(route.query.inquiryId || '').trim()
  const contactMobile = String(route.query.contactMobile || '').trim()
  if (inquiryId) {
    filters.inquiryId = inquiryId
  }
  if (contactMobile) {
    filters.contactMobile = contactMobile
  }
  if (filters.inquiryId && /^1\d{10}$/.test(filters.contactMobile)) {
    queryCompare()
  }
})
</script>

<template>
  <main class="compare-page">
    <section class="card">
      <h1>P04 报价对比页（买家）</h1>
      <p class="desc">输入询价单ID与手机号，查看商家报价、税票、交付时效与履约能力，并按关键字段排序。</p>
      <div class="grid">
        <label>
          询价单ID
          <input v-model="filters.inquiryId" placeholder="如 IQ20260418003" />
        </label>
        <label>
          手机号
          <input v-model="filters.contactMobile" placeholder="提交询价时手机号" />
        </label>
        <label>
          排序字段
          <select v-model="filters.sortBy">
            <option value="TOTAL_PRICE">总价</option>
            <option value="UNIT_PRICE">单价</option>
            <option value="DELIVERY_HOURS">交付天数</option>
            <option value="RESPONSE_MINUTES">响应时长</option>
            <option value="SUPPLIER_SCORE">商家评分</option>
          </select>
        </label>
        <label>
          交付周期
          <select v-model="filters.deliveryCycle">
            <option value="">不限</option>
            <option value="现货可排">现货可排</option>
            <option value="2日到货">2日到货</option>
            <option value="3日到货">3日到货</option>
          </select>
        </label>
        <label>
          票据要求
          <select v-model="filters.invoiceType">
            <option value="">不限</option>
            <option value="YES">需要开票</option>
          </select>
        </label>
      </div>
      <div class="actions">
        <button class="btn primary" :disabled="!canQuery || loading" @click="queryCompare">查询报价对比</button>
      </div>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
    </section>

    <section class="card" v-if="summary.inquiryNo">
      <h2>询价概览</h2>
      <p>询价单号：{{ summary.inquiryNo }}</p>
      <p>规格：{{ summary.specText }}</p>
      <p>收货地：{{ summary.deliveryCity }} ｜ 数量：{{ summary.demandQtyTon }} 吨</p>
    </section>

    <section class="card">
      <h2>报价列表</h2>
      <p v-if="loading">加载中...</p>
      <p v-else-if="list.length === 0">暂无报价，请稍后重试。</p>
      <div v-else class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>商家</th>
              <th>级别</th>
              <th>单价</th>
              <th>总价</th>
              <th>税务</th>
              <th>交付</th>
              <th>响应时长</th>
              <th>评分</th>
              <th>履约率</th>
              <th>票据</th>
              <th>运费</th>
              <th>账期</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in list" :key="item.quoteId">
              <td>
                <div class="supplier">
                  <strong>{{ item.supplierName }}</strong>
                  <span>{{ item.supplierCity }}</span>
                </div>
              </td>
              <td>{{ item.supplierLevel }}</td>
              <td>{{ quoteRate(item) }}</td>
              <td>¥{{ item.totalAmount }}</td>
              <td>{{ item.taxMode }}</td>
              <td>{{ item.deliveryDays }}</td>
              <td>{{ item.responseMinutes }}</td>
              <td>{{ item.serviceScore }}</td>
              <td>{{ item.fulfillmentRate }}</td>
              <td>{{ item.canInvoice }}</td>
              <td>{{ item.canFreight }}</td>
              <td>{{ item.paymentTerm }}</td>
              <td>
                <button class="btn primary small" @click="goDealConfirm(item)">确认成交</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div class="actions" v-if="pager.total > 0">
        <button class="btn" :disabled="pager.page <= 1 || loading" @click="prevPage">上一页</button>
        <button class="btn" :disabled="pager.page >= totalPages || loading" @click="nextPage">下一页</button>
        <span class="tip">第 {{ pager.page }} 页 / 共 {{ totalPages }} 页（{{ pager.total }} 条）</span>
      </div>
    </section>
  </main>
</template>

<style scoped>
.compare-page {
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
h1,
h2 {
  margin: 0 0 8px;
}
.desc {
  margin: 0 0 12px;
  color: #4b5563;
}
.grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
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
}
.checkbox {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  margin-top: 24px;
}
.actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 12px;
  align-items: center;
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
.btn.small {
  padding: 6px 10px;
  font-size: 12px;
}
.tip {
  color: #4b5563;
}
.error {
  color: #b42318;
  margin: 10px 0 0;
}
.table-wrap {
  width: 100%;
  overflow-x: auto;
}
table {
  width: 100%;
  border-collapse: collapse;
}
th,
td {
  border-top: 1px solid #f3f4f6;
  text-align: left;
  padding: 10px 8px;
  white-space: nowrap;
}
th {
  color: #6b7280;
  font-weight: 600;
}
.supplier {
  display: grid;
  gap: 4px;
}
.supplier span {
  color: #6b7280;
  font-size: 12px;
}
@media (max-width: 980px) {
  .grid {
    grid-template-columns: 1fr;
  }
  .checkbox {
    margin-top: 0;
  }
}
</style>
