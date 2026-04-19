<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const loadingList = ref(false)
const loadingDetail = ref(false)
const submittingPay = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

const filters = reactive({
  merchantId: '',
  status: '',
  keyword: '',
  page: 1,
  pageSize: 10
})

const state = reactive({
  list: [],
  total: 0,
  unpaidCount: 0,
  partialPaidCount: 0,
  paidCount: 0,
  totalReceivableAmount: '0',
  totalPaidAmount: '0',
  totalOutstandingAmount: '0'
})

const detail = ref(null)
const selectedBillId = ref('')

const payForm = reactive({
  payAmount: '',
  payChannel: 'BANK_TRANSFER',
  operator: '',
  remark: ''
})

const statusTextMap = {
  UNPAID: '待支付',
  PARTIAL_PAID: '部分支付',
  PAID: '已支付',
  OVERDUE: '已逾期'
}

const canQuery = computed(() => filters.merchantId.trim().length > 0)
const canPay = computed(
  () =>
    !!selectedBillId.value &&
    filters.merchantId.trim().length > 0 &&
    payForm.payAmount.trim().length > 0
)

async function loadList() {
  if (!canQuery.value || loadingList.value) return
  loadingList.value = true
  errorMsg.value = ''
  try {
    const params = new URLSearchParams()
    params.set('merchantId', filters.merchantId.trim())
    if (filters.status.trim()) params.set('status', filters.status.trim())
    if (filters.keyword.trim()) params.set('keyword', filters.keyword.trim())
    params.set('page', String(filters.page))
    params.set('pageSize', String(filters.pageSize))
    const resp = await fetch(`/api/v1/inquiries/merchant/billing/orders?${params.toString()}`)
    const json = await resp.json()
    if (json.code !== '0') {
      throw new Error(json.message || '账单列表加载失败')
    }
    const data = json.data || {}
    state.list = Array.isArray(data.items) ? data.items : []
    state.total = Number(data.total || 0)
    state.unpaidCount = Number(data.unpaidCount || 0)
    state.partialPaidCount = Number(data.partialPaidCount || 0)
    state.paidCount = Number(data.paidCount || 0)
    state.totalReceivableAmount = String(data.totalReceivableAmount || '0')
    state.totalPaidAmount = String(data.totalPaidAmount || '0')
    state.totalOutstandingAmount = String(data.totalOutstandingAmount || '0')
    if (!selectedBillId.value && state.list.length > 0) {
      await selectBill(state.list[0].billId)
    }
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    loadingList.value = false
  }
}

async function selectBill(billId) {
  if (!billId || !canQuery.value || loadingDetail.value) return
  loadingDetail.value = true
  errorMsg.value = ''
  successMsg.value = ''
  selectedBillId.value = billId
  try {
    const params = new URLSearchParams()
    params.set('merchantId', filters.merchantId.trim())
    const resp = await fetch(`/api/v1/inquiries/merchant/billing/orders/${billId}?${params.toString()}`)
    const json = await resp.json()
    if (json.code !== '0') {
      throw new Error(json.message || '账单详情加载失败')
    }
    detail.value = json.data || null
    payForm.payAmount = detail.value?.order?.unpaidAmountYuan || ''
    payForm.payChannel = 'BANK_TRANSFER'
    payForm.operator = ''
    payForm.remark = ''
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    loadingDetail.value = false
  }
}

async function submitPay() {
  if (!canPay.value || submittingPay.value) return
  submittingPay.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      merchantId: filters.merchantId.trim(),
      payAmount: payForm.payAmount.trim(),
      payChannel: payForm.payChannel.trim(),
      operator: payForm.operator || '财务同学',
      remark: payForm.remark
    }
    const resp = await fetch(`/api/v1/inquiries/merchant/billing/orders/${selectedBillId.value}/pay`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    })
    const json = await resp.json()
    if (json.code !== '0') {
      throw new Error(json.message || '登记回款失败')
    }
    successMsg.value = json.data?.message || '回款登记成功'
    await Promise.all([loadList(), selectBill(selectedBillId.value)])
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    submittingPay.value = false
  }
}

onMounted(() => {
  filters.merchantId = String(route.query.merchantId || '').trim() || 'S001'
  loadList()
})
</script>

<template>
  <main class="billing-page">
    <section class="card">
      <h1>P12 计费与账单页（商家）</h1>
      <p class="desc">查看订阅账单、核对账单明细并登记回款。</p>
      <div class="filters">
        <label>
          商家ID
          <input v-model="filters.merchantId" placeholder="如 S001" />
        </label>
        <label>
          状态
          <select v-model="filters.status">
            <option value="">全部</option>
            <option value="UNPAID">待支付</option>
            <option value="PARTIAL_PAID">部分支付</option>
            <option value="PAID">已支付</option>
            <option value="OVERDUE">已逾期</option>
          </select>
        </label>
        <label>
          关键词
          <input v-model="filters.keyword" placeholder="账单号/套餐/订阅号" />
        </label>
        <button class="btn primary" :disabled="!canQuery || loadingList" @click="loadList">
          {{ loadingList ? '加载中...' : '查询账单' }}
        </button>
      </div>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
      <p v-if="successMsg" class="success">{{ successMsg }}</p>
    </section>

    <section class="card summary-grid">
      <article>
        <h3>账单总数</h3>
        <p>{{ state.total }}</p>
      </article>
      <article>
        <h3>待支付</h3>
        <p>{{ state.unpaidCount }}</p>
      </article>
      <article>
        <h3>部分支付</h3>
        <p>{{ state.partialPaidCount }}</p>
      </article>
      <article>
        <h3>已支付</h3>
        <p>{{ state.paidCount }}</p>
      </article>
      <article>
        <h3>应收总额</h3>
        <p>￥{{ state.totalReceivableAmount }}</p>
      </article>
      <article>
        <h3>已收总额</h3>
        <p>￥{{ state.totalPaidAmount }}</p>
      </article>
      <article>
        <h3>待收总额</h3>
        <p>￥{{ state.totalOutstandingAmount }}</p>
      </article>
    </section>

    <section class="card">
      <div class="section-head">
        <h2>账单列表</h2>
        <span class="muted">{{ state.list.length }} 条</span>
      </div>
      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>账单号</th>
              <th>套餐</th>
              <th>账期</th>
              <th>应收</th>
              <th>已收</th>
              <th>待收</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="!state.list.length">
              <td colspan="8" class="empty">暂无账单</td>
            </tr>
            <tr
              v-for="row in state.list"
              :key="row.billId"
              :class="{ active: selectedBillId === row.billId }"
              @click="selectBill(row.billId)"
            >
              <td>{{ row.billNo }}</td>
              <td>{{ row.planName }}</td>
              <td>{{ row.periodStart }} ~ {{ row.periodEnd }}</td>
              <td>￥{{ row.amountYuan }}</td>
              <td>￥{{ row.paidAmountYuan }}</td>
              <td>￥{{ row.unpaidAmountYuan }}</td>
              <td>
                <span class="status" :data-status="row.status">{{ statusTextMap[row.status] || row.status }}</span>
              </td>
              <td>
                <button class="btn mini" @click.stop="selectBill(row.billId)">查看明细</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>

    <section class="card">
      <div class="section-head">
        <h2>账单详情与回款登记</h2>
        <span class="muted">{{ loadingDetail ? '加载中...' : selectedBillId || '未选择账单' }}</span>
      </div>
      <div v-if="detail && detail.order" class="detail-layout">
        <div class="detail-panel">
          <p><strong>账单号：</strong>{{ detail.order.billNo }}</p>
          <p><strong>订阅号：</strong>{{ detail.order.subscriptionNo }}</p>
          <p><strong>套餐：</strong>{{ detail.order.planName }}</p>
          <p><strong>账单说明：</strong>{{ detail.planDesc }}</p>
          <p><strong>税率：</strong>{{ detail.taxRate }} ｜ 税额：￥{{ detail.taxAmountYuan }}</p>
          <p><strong>不含税金额：</strong>￥{{ detail.netAmountYuan }}</p>
          <p><strong>最近备注：</strong>{{ detail.latestRemark || '-' }}</p>
        </div>
        <div class="detail-panel">
          <h3>登记回款</h3>
          <div class="pay-form">
            <label>
              回款金额
              <input v-model="payForm.payAmount" placeholder="如 3999" />
            </label>
            <label>
              回款渠道
              <select v-model="payForm.payChannel">
                <option value="BANK_TRANSFER">银行转账</option>
                <option value="ALIPAY">支付宝</option>
                <option value="WECHAT_PAY">微信支付</option>
              </select>
            </label>
            <label>
              操作人
              <input v-model="payForm.operator" placeholder="如 财务A" />
            </label>
            <label>
              备注
              <input v-model="payForm.remark" placeholder="可选" />
            </label>
            <button class="btn primary" :disabled="!canPay || submittingPay" @click="submitPay">
              {{ submittingPay ? '提交中...' : '登记回款' }}
            </button>
          </div>
        </div>
      </div>
      <p v-else class="muted">请选择账单查看详情</p>
    </section>
  </main>
</template>

<style scoped>
.billing-page {
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
.btn.mini {
  padding: 4px 8px;
  font-size: 12px;
}
.error {
  color: #b42318;
  margin-top: 10px;
}
.success {
  color: #0c7a43;
  margin-top: 10px;
}
.summary-grid {
  display: grid;
  grid-template-columns: repeat(7, minmax(0, 1fr));
  gap: 10px;
}
.summary-grid article {
  border: 1px solid #f3f4f6;
  border-radius: 10px;
  padding: 10px;
}
.summary-grid h3 {
  margin: 0;
  color: #6b7280;
  font-size: 13px;
}
.summary-grid p {
  margin: 6px 0 0;
  font-size: 18px;
  font-weight: 700;
  color: #111827;
}
.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}
.muted {
  color: #6b7280;
  margin: 0;
}
.table-wrap {
  overflow-x: auto;
}
table {
  width: 100%;
  border-collapse: collapse;
  min-width: 960px;
}
th,
td {
  border-bottom: 1px solid #f1f5f9;
  text-align: left;
  padding: 10px 8px;
  font-size: 14px;
}
th {
  color: #374151;
  background: #fafafa;
}
tr.active {
  background: #fff7ed;
}
.empty {
  text-align: center;
  color: #6b7280;
}
.status[data-status='UNPAID'] {
  color: #92400e;
}
.status[data-status='PARTIAL_PAID'] {
  color: #1d4ed8;
}
.status[data-status='PAID'] {
  color: #0c7a43;
}
.status[data-status='OVERDUE'] {
  color: #b42318;
}
.detail-layout {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}
.detail-panel {
  border: 1px solid #f3f4f6;
  border-radius: 10px;
  padding: 12px;
}
.detail-panel p {
  margin: 0 0 8px;
  color: #374151;
}
.pay-form {
  display: grid;
  gap: 10px;
}
@media (max-width: 980px) {
  .summary-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
  .detail-layout {
    grid-template-columns: 1fr;
  }
}
</style>
