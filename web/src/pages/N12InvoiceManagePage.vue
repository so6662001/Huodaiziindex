<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const loadingTitles = ref(false)
const loadingInvoices = ref(false)
const loadingInvoiceDetail = ref(false)
const titleSubmitting = ref(false)
const invoiceSubmitting = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

const titles = ref([])
const invoices = ref([])
const selectedInvoiceId = ref('')
const invoiceDetail = ref(null)

const invoiceStatusFilter = ref('')
const keyword = ref('')
const pageNo = ref(1)
const pageSize = ref(10)
const total = ref(0)

const titleForm = reactive({
  titleId: '',
  titleName: '',
  taxNo: '',
  phone: '',
  address: '',
  bankName: '',
  bankAccountNo: '',
  defaultTitle: 'N',
  operator: 'pc-n12-ui'
})

const invoiceForm = reactive({
  orderId: '',
  titleId: '',
  invoiceContent: '货款',
  remark: '',
  operator: 'pc-n12-ui'
})

const token = computed(() => localStorage.getItem('N01_AUTH_TOKEN') || '')
const hasSession = computed(() => token.value.length > 0)
const canSubmitTitle = computed(
  () =>
    hasSession.value &&
    titleForm.titleName.trim() &&
    titleForm.taxNo.trim() &&
    titleForm.address.trim() &&
    titleForm.phone.trim() &&
    !titleSubmitting.value
)
const canSubmitInvoice = computed(
  () =>
    hasSession.value &&
    invoiceForm.orderId.trim() &&
    invoiceForm.titleId.trim() &&
    !invoiceSubmitting.value
)

async function loadTitles() {
  if (!hasSession.value || loadingTitles.value) {
    if (!hasSession.value) errorMsg.value = '请先登录后查看发票信息'
    return
  }
  loadingTitles.value = true
  errorMsg.value = ''
  try {
    const resp = await fetch('/api/v1/auth/invoices/titles', {
      headers: { 'X-Auth-Token': token.value }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `抬头列表加载失败(${resp.status})`)
    }
    titles.value = json.data?.records || []
    if (!invoiceForm.titleId && titles.value.length > 0) {
      invoiceForm.titleId = titles.value[0].titleId
    }
  } catch (error) {
    errorMsg.value = error.message || '抬头列表加载失败'
  } finally {
    loadingTitles.value = false
  }
}

async function submitTitle() {
  if (!canSubmitTitle.value) return
  titleSubmitting.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      titleId: titleForm.titleId.trim() || null,
      titleName: titleForm.titleName.trim(),
      taxNo: titleForm.taxNo.trim(),
      phone: titleForm.phone.trim(),
      address: titleForm.address.trim() || null,
      bankName: titleForm.bankName.trim() || null,
      bankAccountNo: titleForm.bankAccountNo.trim() || null,
      defaultTitle: titleForm.defaultTitle,
      operator: titleForm.operator
    }
    const resp = await fetch('/api/v1/auth/invoices/titles', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Auth-Token': token.value
      },
      body: JSON.stringify(payload)
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `保存抬头失败(${resp.status})`)
    }
    successMsg.value = '抬头保存成功'
    titleForm.titleId = ''
    titleForm.titleName = ''
    titleForm.taxNo = ''
    titleForm.phone = ''
    titleForm.address = ''
    titleForm.bankName = ''
    titleForm.bankAccountNo = ''
    titleForm.defaultTitle = 'N'
    await loadTitles()
  } catch (error) {
    errorMsg.value = error.message || '保存抬头失败'
  } finally {
    titleSubmitting.value = false
  }
}

async function setDefaultTitle(titleId) {
  if (!hasSession.value) return
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const resp = await fetch(`/api/v1/auth/invoices/titles/${titleId}/default`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Auth-Token': token.value
      },
      body: JSON.stringify({ operator: 'pc-n12-ui' })
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `设置默认失败(${resp.status})`)
    }
    successMsg.value = '默认抬头已更新'
    await loadTitles()
  } catch (error) {
    errorMsg.value = error.message || '设置默认失败'
  }
}

async function disableTitle(titleId) {
  if (!hasSession.value) return
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const resp = await fetch(`/api/v1/auth/invoices/titles/${titleId}/status`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Auth-Token': token.value
      },
      body: JSON.stringify({ status: 'INACTIVE', operator: 'pc-n12-ui' })
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `停用抬头失败(${resp.status})`)
    }
    successMsg.value = '抬头已停用'
    await loadTitles()
  } catch (error) {
    errorMsg.value = error.message || '停用抬头失败'
  }
}

async function loadInvoices() {
  if (!hasSession.value || loadingInvoices.value) return
  loadingInvoices.value = true
  errorMsg.value = ''
  try {
    const params = new URLSearchParams({
      pageNo: String(pageNo.value),
      pageSize: String(pageSize.value)
    })
    if (invoiceStatusFilter.value) params.set('status', invoiceStatusFilter.value)
    if (keyword.value.trim()) params.set('keyword', keyword.value.trim())
    const resp = await fetch(`/api/v1/auth/invoices/applications?${params.toString()}`, {
      headers: { 'X-Auth-Token': token.value }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `发票申请列表加载失败(${resp.status})`)
    }
    const data = json.data || {}
    invoices.value = data.records || []
    total.value = data.total || 0
    if (!selectedInvoiceId.value && invoices.value.length > 0) {
      selectedInvoiceId.value = invoices.value[0].applicationId
      await loadInvoiceDetail()
    }
  } catch (error) {
    errorMsg.value = error.message || '发票申请列表加载失败'
  } finally {
    loadingInvoices.value = false
  }
}

async function createInvoiceApplication() {
  if (!canSubmitInvoice.value) return
  invoiceSubmitting.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      orderId: invoiceForm.orderId.trim(),
      titleId: invoiceForm.titleId.trim(),
      invoiceContent: invoiceForm.invoiceContent.trim() || '货款',
      remark: invoiceForm.remark.trim() || null,
      operator: invoiceForm.operator
    }
    const resp = await fetch('/api/v1/auth/invoices/applications', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Auth-Token': token.value
      },
      body: JSON.stringify(payload)
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `发票申请提交失败(${resp.status})`)
    }
    const created = json.data || null
    selectedInvoiceId.value = created?.invoiceApplyId || ''
    successMsg.value = '发票申请已提交'
    invoiceForm.orderId = ''
    invoiceForm.invoiceContent = '货款'
    invoiceForm.remark = ''
    await loadInvoices()
  } catch (error) {
    errorMsg.value = error.message || '发票申请提交失败'
  } finally {
    invoiceSubmitting.value = false
  }
}

async function loadInvoiceDetail() {
  if (!hasSession.value || !selectedInvoiceId.value || loadingInvoiceDetail.value) return
  loadingInvoiceDetail.value = true
  errorMsg.value = ''
  try {
    const resp = await fetch(`/api/v1/auth/invoices/applications/${selectedInvoiceId.value}`, {
      headers: { 'X-Auth-Token': token.value }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `发票申请详情加载失败(${resp.status})`)
    }
    invoiceDetail.value = json.data || null
  } catch (error) {
    errorMsg.value = error.message || '发票申请详情加载失败'
  } finally {
    loadingInvoiceDetail.value = false
  }
}

function chooseInvoice(applicationId) {
  selectedInvoiceId.value = applicationId
  loadInvoiceDetail()
}

function fillTitle(title) {
  titleForm.titleId = title.titleId
  titleForm.titleName = title.titleName
  titleForm.taxNo = title.taxNo
  titleForm.phone = title.contactPhoneMasked || ''
  titleForm.address = title.registeredAddress || ''
  titleForm.bankName = title.bankName || ''
  titleForm.bankAccountNo = ''
  titleForm.defaultTitle = title.defaultTitle ? 'Y' : 'N'
}

function goPaymentResult() {
  router.push('/account/payment-result')
}

function goCashier() {
  router.push('/account/cashier')
}

function goHome() {
  router.push('/')
}

onMounted(async () => {
  await loadTitles()
  await loadInvoices()
})
</script>

<template>
  <main class="n12-page">
    <section class="card hero">
      <h1>PC-N12 发票与抬头管理</h1>
      <p>统一管理企业发票抬头、开票申请与开票进度，支持默认抬头设置与申请记录追踪。</p>
      <div class="hero-actions">
        <button class="btn" @click="goPaymentResult">返回支付结果页</button>
        <button class="btn" @click="goCashier">返回收银台</button>
        <button class="btn" @click="goHome">返回首页</button>
      </div>
    </section>

    <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
    <p v-if="successMsg" class="ok">{{ successMsg }}</p>

    <section class="layout">
      <article class="card left">
        <div class="head">
          <h2>抬头管理</h2>
          <button class="btn" :disabled="loadingTitles" @click="loadTitles">{{ loadingTitles ? '刷新中...' : '刷新' }}</button>
        </div>
        <div class="form-grid">
          <input v-model="titleForm.titleName" placeholder="抬头名称（必填）" />
          <input v-model="titleForm.taxNo" placeholder="税号（必填）" />
          <select v-model="titleForm.invoiceType">
            <option value="VAT_SPECIAL">增值税专票</option>
            <option value="VAT_NORMAL">增值税普票</option>
          </select>
          <input v-model="titleForm.email" placeholder="收票邮箱（可选）" />
          <input v-model="titleForm.address" placeholder="注册地址（可选）" class="span-2" />
          <input v-model="titleForm.bankName" placeholder="开户行（可选）" />
          <input v-model="titleForm.bankAccount" placeholder="银行账号（可选）" />
          <label class="checkbox">
            <input v-model="titleForm.defaultFlag" type="checkbox" />
            保存后设为默认抬头
          </label>
        </div>
        <div class="actions">
          <button class="btn btn--primary" :disabled="!canSubmitTitle" @click="submitTitle">
            {{ titleSubmitting ? '提交中...' : (titleForm.titleId ? '更新抬头' : '新增抬头') }}
          </button>
        </div>
        <ul class="list">
          <li v-for="title in titles" :key="title.titleId">
            <div class="line-1">
              <strong>{{ title.titleName }}</strong>
              <span>{{ title.defaultFlag ? '默认' : title.statusText }}</span>
            </div>
            <p>税号：{{ title.taxNo }}</p>
            <p>类型：{{ title.invoiceTypeText }}</p>
            <p>邮箱：{{ title.email || '-' }}</p>
            <div class="row-actions">
              <button class="btn" @click="fillTitle(title)">编辑</button>
              <button class="btn" @click="setDefaultTitle(title.titleId)">设默认</button>
              <button class="btn" @click="disableTitle(title.titleId)">停用</button>
            </div>
          </li>
        </ul>
      </article>

      <article class="card right">
        <div class="head">
          <h2>开票申请</h2>
          <button class="btn" :disabled="loadingInvoices" @click="loadInvoices">{{ loadingInvoices ? '刷新中...' : '刷新' }}</button>
        </div>
        <div class="form-grid">
          <input v-model="invoiceForm.orderId" placeholder="订单ID（如 OD0001）" />
          <select v-model="invoiceForm.titleId">
            <option value="">选择抬头（必选）</option>
            <option v-for="title in titles" :key="title.titleId" :value="title.titleId">
              {{ title.titleName }}（{{ title.titleId }}）
            </option>
          </select>
          <select v-model="invoiceForm.invoiceType">
            <option value="VAT_SPECIAL">增值税专票</option>
            <option value="VAT_NORMAL">增值税普票</option>
          </select>
          <input v-model="invoiceForm.amount" placeholder="开票金额（必填）" />
          <input v-model="invoiceForm.recipientEmail" placeholder="收票邮箱（可选）" />
          <input v-model="invoiceForm.recipientPhone" placeholder="联系人手机号（可选）" />
          <input v-model="invoiceForm.recipientAddress" placeholder="邮寄地址（可选）" class="span-2" />
          <input v-model="invoiceForm.remark" placeholder="申请备注（可选）" class="span-2" />
        </div>
        <div class="actions">
          <button class="btn btn--primary" :disabled="!canSubmitInvoice" @click="createInvoiceApplication">
            {{ invoiceSubmitting ? '提交中...' : '提交开票申请' }}
          </button>
        </div>

        <div class="filters">
          <select v-model="invoiceStatusFilter">
            <option value="">全部状态</option>
            <option value="SUBMITTED">已提交</option>
            <option value="PROCESSING">开票中</option>
            <option value="ISSUED">已开票</option>
            <option value="VOID">已作废</option>
          </select>
          <input v-model="keyword" placeholder="按申请号/订单号/抬头搜索" />
          <button class="btn" :disabled="loadingInvoices" @click="loadInvoices">筛选</button>
        </div>
        <p class="tip">共 {{ total }} 条开票申请</p>
        <ul class="list">
          <li
            v-for="item in invoices"
            :key="item.applicationId"
            :class="{ active: item.applicationId === selectedInvoiceId }"
            @click="chooseInvoice(item.applicationId)"
          >
            <div class="line-1">
              <strong>{{ item.applicationNo }}</strong>
              <span>{{ item.statusText }}</span>
            </div>
            <p>订单：{{ item.orderNo }}</p>
            <p>抬头：{{ item.titleName }}</p>
            <p>金额：¥{{ item.amount }}</p>
          </li>
        </ul>

        <section v-if="invoiceDetail" class="block">
          <h3>申请详情</h3>
          <div class="meta-grid">
            <p>申请单号：{{ invoiceDetail.applicationNo }}</p>
            <p>申请状态：{{ invoiceDetail.statusText }}（{{ invoiceDetail.status }}）</p>
            <p>订单号：{{ invoiceDetail.orderNo }}（{{ invoiceDetail.orderId }}）</p>
            <p>抬头：{{ invoiceDetail.titleName }}（{{ invoiceDetail.titleId }}）</p>
            <p>开票类型：{{ invoiceDetail.invoiceTypeText }}</p>
            <p>金额：¥{{ invoiceDetail.amount }}</p>
            <p>收票邮箱：{{ invoiceDetail.recipientEmail || '-' }}</p>
            <p>联系电话：{{ invoiceDetail.recipientPhoneMasked || '-' }}</p>
            <p>邮寄地址：{{ invoiceDetail.recipientAddress || '-' }}</p>
            <p>发票号：{{ invoiceDetail.invoiceNo || '-' }}</p>
            <p>更新时间：{{ invoiceDetail.updatedAt || '-' }}</p>
            <p>备注：{{ invoiceDetail.remark || '-' }}</p>
          </div>
        </section>
      </article>
    </section>
  </main>
</template>

<style scoped>
.n12-page {
  max-width: 1240px;
  margin: 0 auto;
  padding: 20px 16px 36px;
  display: grid;
  gap: 14px;
}
.card {
  background: #fff;
  border: 1px solid #ececec;
  border-radius: 12px;
  padding: 16px;
}
.hero {
  background: linear-gradient(135deg, #fff7ed, #ffedd5);
}
.hero h1 {
  margin: 0;
}
.hero p {
  margin: 8px 0 0;
  color: #7c2d12;
}
.hero-actions {
  margin-top: 12px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}
.layout {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
}
.head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.form-grid {
  margin-top: 10px;
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 8px;
}
.span-2 {
  grid-column: span 2;
}
.checkbox {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #374151;
  font-size: 13px;
}
.actions {
  margin-top: 10px;
}
.filters {
  margin-top: 12px;
  display: grid;
  grid-template-columns: 130px 1fr auto;
  gap: 8px;
}
.list {
  list-style: none;
  margin: 10px 0 0;
  padding: 0;
  display: grid;
  gap: 8px;
}
.list li {
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  padding: 10px;
  cursor: pointer;
}
.list li.active {
  border-color: #f57c00;
  background: #fffaf3;
}
.line-1 {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}
.row-actions {
  margin-top: 8px;
  display: flex;
  gap: 8px;
}
.block {
  margin-top: 12px;
  border: 1px solid #f3f4f6;
  border-radius: 10px;
  padding: 12px;
}
.meta-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 6px 10px;
}
.btn {
  height: 34px;
  border-radius: 8px;
  border: 1px solid #d1d5db;
  background: #fff;
  padding: 0 12px;
  cursor: pointer;
}
.btn--primary {
  border-color: #f57c00;
  background: #f57c00;
  color: #fff;
}
input,
select {
  height: 34px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  padding: 0 10px;
}
.tip {
  color: #6b7280;
}
.ok {
  color: #166534;
}
.error {
  color: #b91c1c;
}
@media (max-width: 980px) {
  .layout {
    grid-template-columns: 1fr;
  }
  .form-grid,
  .meta-grid,
  .filters {
    grid-template-columns: 1fr;
  }
  .span-2 {
    grid-column: span 1;
  }
}
</style>
