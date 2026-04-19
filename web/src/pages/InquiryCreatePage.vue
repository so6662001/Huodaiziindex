<script setup>
import { computed, reactive, ref } from 'vue'
const loading = ref(false)
const errorMsg = ref('')
const successMsg = ref('')
const listLoading = ref(false)
const listErrorMsg = ref('')
const step = ref(1)
const inquiryList = ref([])
const query = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const form = reactive({
  categoryCode: '螺纹钢',
  specText: '',
  deliveryCity: '唐山',
  demandQtyTon: '',
  expectedDeliveryAt: '',
  invoiceNeed: 'ANY',
  contactMobile: '',
  remark: ''
})

const canStep2 = computed(() => form.categoryCode.trim().length > 0)
const canStep3 = computed(() => form.specText.trim().length > 0)
const canSubmit = computed(() => {
  return (
    form.deliveryCity.trim().length > 0 &&
    Number(form.demandQtyTon) > 0 &&
    /^1\d{10}$/.test(form.contactMobile.trim())
  )
})

function goNext() {
  if (step.value === 1 && !canStep2.value) return
  if (step.value === 2 && !canStep3.value) return
  if (step.value < 3) step.value += 1
}

function goPrev() {
  if (step.value > 1) step.value -= 1
}

async function submitInquiry() {
  if (!canSubmit.value || loading.value) return
  loading.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      categoryCode: form.categoryCode.trim(),
      specText: form.specText.trim(),
      deliveryCity: form.deliveryCity.trim(),
      demandQtyTon: Number(form.demandQtyTon),
      expectedDeliveryAt: form.expectedDeliveryAt || null,
      invoiceNeed: form.invoiceNeed,
      contactMobile: form.contactMobile.trim(),
      remark: form.remark.trim() || null
    }
    const resp = await fetch('/api/v1/inquiries', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    })
    const json = await resp.json()
    if (json.code !== '0') {
      throw new Error(json.message || '提交询价失败')
    }
    successMsg.value = `${json.data.message}（询价单号：${json.data.inquiryNo}）`
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    loading.value = false
  }
}

async function goMine() {
  if (!/^1\d{10}$/.test(form.contactMobile.trim())) return
  listLoading.value = true
  listErrorMsg.value = ''
  inquiryList.value = []
  query.total = 0
  try {
    const params = new URLSearchParams()
    params.set('contactMobile', form.contactMobile.trim())
    params.set('page', String(query.page))
    params.set('pageSize', String(query.pageSize))
    const resp = await fetch(`/api/v1/inquiries?${params.toString()}`)
    const json = await resp.json()
    if (json.code !== '0') {
      throw new Error(json.message || '查询询价单失败')
    }
    inquiryList.value = json.data.items || []
    query.total = Number(json.data.total || 0)
    query.page = Number(json.data.page || query.page)
    query.pageSize = Number(json.data.pageSize || query.pageSize)
  } catch (error) {
    listErrorMsg.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    listLoading.value = false
  }
}

function prevPage() {
  if (query.page <= 1 || listLoading.value) return
  query.page -= 1
  goMine()
}

function nextPage() {
  if (query.page * query.pageSize >= query.total || listLoading.value) return
  query.page += 1
  goMine()
}
</script>

<template>
  <main class="inquiry-page">
    <section class="card hero">
      <h1>AI询价（P02）</h1>
      <p>3步完成：品类 → 规格 → 收货地与数量。系统将自动推送给匹配商家。</p>
    </section>

    <section class="card steps">
      <div class="step-line">
        <span :class="{ active: step >= 1 }">1. 品类</span>
        <span :class="{ active: step >= 2 }">2. 规格</span>
        <span :class="{ active: step >= 3 }">3. 收货地与需求</span>
      </div>

      <div v-if="step === 1" class="form-block">
        <label>
          品类
          <select v-model="form.categoryCode">
            <option value="螺纹钢">螺纹钢</option>
            <option value="热轧卷板">热轧卷板</option>
            <option value="中厚板">中厚板</option>
            <option value="冷轧板卷">冷轧板卷</option>
          </select>
        </label>
      </div>

      <div v-else-if="step === 2" class="form-block">
        <label>
          规格
          <input v-model="form.specText" placeholder="如 Q235B 3.0*1500*C" />
        </label>
      </div>

      <div v-else class="form-block">
        <label>
          收货地
          <input v-model="form.deliveryCity" placeholder="如 唐山" />
        </label>
        <label>
          需求数量（吨）
          <input v-model="form.demandQtyTon" type="number" min="0" step="0.001" placeholder="如 120" />
        </label>
        <label>
          期望交期（选填）
          <input v-model="form.expectedDeliveryAt" type="date" />
        </label>
        <label>
          票据需求
          <select v-model="form.invoiceNeed">
            <option value="ANY">不限</option>
            <option value="YES">需要</option>
            <option value="NO">不需要</option>
          </select>
        </label>
        <label>
          联系手机
          <input v-model="form.contactMobile" placeholder="11位手机号" />
        </label>
        <label>
          备注（选填）
          <textarea v-model="form.remark" rows="3" placeholder="如 需要可回单，优先当日装车"></textarea>
        </label>
      </div>

      <div class="actions">
        <button class="btn" :disabled="step === 1 || loading" @click="goPrev">上一步</button>
        <button v-if="step < 3" class="btn btn--primary" :disabled="loading" @click="goNext">下一步</button>
        <button v-else class="btn btn--primary" :disabled="!canSubmit || loading" @click="submitInquiry">
          {{ loading ? '提交中...' : '提交询价' }}
        </button>
        <button class="btn btn--ghost" :disabled="!/^1\\d{10}$/.test(form.contactMobile.trim())" @click="goMine">
          查询我的询价单
        </button>
      </div>

      <p v-if="successMsg" class="success">{{ successMsg }}</p>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
    </section>

    <section class="card">
      <h2>我的询价单</h2>
      <p v-if="listLoading">加载中...</p>
      <p v-else-if="listErrorMsg" class="error">{{ listErrorMsg }}</p>
      <p v-else-if="inquiryList.length === 0">暂无数据，提交后可在此查看。</p>
      <ul v-else class="list">
        <li v-for="item in inquiryList" :key="item.inquiryId" class="list-item">
          <h3>{{ item.specText }}</h3>
          <p>询价单号：{{ item.inquiryNo }} ｜ 状态：{{ item.inquiryStatus }}</p>
          <p>收货地：{{ item.deliveryCity }} ｜ 数量：{{ item.demandQtyTon }} 吨</p>
          <p>票据：{{ item.invoiceNeed }} ｜ 已报价商家：{{ item.quoteCount }}</p>
          <p>创建时间：{{ item.createdAt }}</p>
        </li>
      </ul>
      <div v-if="query.total > 0" class="actions">
        <button class="btn" :disabled="query.page <= 1 || listLoading" @click="prevPage">上一页</button>
        <button class="btn" :disabled="query.page * query.pageSize >= query.total || listLoading" @click="nextPage">
          下一页
        </button>
        <span class="tip">第 {{ query.page }} 页 / 共 {{ Math.max(Math.ceil(query.total / query.pageSize), 1) }} 页</span>
      </div>
    </section>
  </main>
</template>

<style scoped>
.inquiry-page {
  max-width: 960px;
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
.hero h1 {
  margin: 0 0 8px;
}
.hero p {
  margin: 0;
  color: #666;
}
.step-line {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 8px;
  margin-bottom: 14px;
}
.step-line span {
  display: inline-flex;
  justify-content: center;
  align-items: center;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  min-height: 38px;
  color: #666;
}
.step-line span.active {
  border-color: #f57c00;
  background: #fff7ed;
  color: #f57c00;
  font-weight: 600;
}
.form-block {
  display: grid;
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
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 14px;
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
.btn--ghost {
  color: #666;
}
.success {
  color: #1f7a43;
  margin: 10px 0 0;
}
.error {
  color: #b42318;
  margin: 10px 0 0;
}
.list {
  list-style: none;
  margin: 10px 0 0;
  padding: 0;
}
.list-item {
  border-top: 1px dashed #ececec;
  padding: 12px 0;
}
.list-item:first-child {
  border-top: 0;
}
.list-item h3 {
  margin: 0 0 6px;
  font-size: 16px;
}
.list-item p {
  margin: 4px 0 0;
  color: #4b5563;
}
.tip {
  color: #4b5563;
  display: inline-flex;
  align-items: center;
}
@media (max-width: 760px) {
  .step-line {
    grid-template-columns: 1fr;
  }
}
</style>
