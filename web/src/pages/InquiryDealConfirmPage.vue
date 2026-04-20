<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const loadingPreview = ref(false)
const submitting = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

const preview = reactive({
  inquiryId: '',
  inquiryNo: '',
  quoteId: '',
  supplierId: '',
  supplierName: '',
  specText: '',
  demandQtyTon: '',
  deliveryCity: '',
  unitPrice: '',
  totalAmount: '',
  paymentTerm: '',
  deliveryDays: '',
  expectedDeliveryAt: '',
  invoiceNeed: '',
  riskNotice: ''
})

const form = reactive({
  inquiryId: '',
  quoteId: '',
  contactMobile: '',
  buyerCompany: '',
  buyerContact: '',
  buyerPhone: '',
  expectedSignDate: '',
  remark: ''
})

const canPreview = computed(() => {
  return form.inquiryId.trim() && form.quoteId.trim() && /^1\d{10}$/.test(form.contactMobile.trim())
})

const canSubmit = computed(() => {
  return (
    canPreview.value &&
    form.buyerCompany.trim().length >= 2 &&
    form.buyerContact.trim().length >= 2 &&
    /^1\d{10}$/.test(form.buyerPhone.trim())
  )
})

async function loadPreview() {
  if (!canPreview.value || loadingPreview.value) return
  loadingPreview.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const params = new URLSearchParams()
    params.set('inquiryId', form.inquiryId.trim())
    params.set('quoteId', form.quoteId.trim())
    params.set('contactMobile', form.contactMobile.trim())
    const resp = await fetch(`/api/v1/inquiries/deal/preview?${params.toString()}`)
    const json = await resp.json()
    if (json.code !== '0') {
      throw new Error(json.message || '加载成交确认预览失败')
    }
    Object.assign(preview, json.data || {})
    if (!form.expectedSignDate.trim()) {
      form.expectedSignDate = preview.expectedDeliveryAt || ''
    }
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    loadingPreview.value = false
  }
}

async function submitDeal() {
  if (!canSubmit.value || submitting.value) return
  submitting.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      inquiryId: form.inquiryId.trim(),
      quoteId: form.quoteId.trim(),
      contactMobile: form.contactMobile.trim(),
      buyerCompany: form.buyerCompany.trim(),
      buyerContact: form.buyerContact.trim(),
      buyerPhone: form.buyerPhone.trim(),
      expectedSignDate: form.expectedSignDate.trim() || null,
      remark: form.remark.trim() || null
    }
    const resp = await fetch('/api/v1/inquiries/deal/confirm', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    })
    const json = await resp.json()
    if (json.code !== '0') {
      throw new Error(json.message || '成交确认失败')
    }
    successMsg.value = `${json.data.message}（状态：${json.data.dealStatus}）`
    const params = new URLSearchParams()
    params.set('inquiryId', form.inquiryId.trim())
    params.set('quoteId', form.quoteId.trim())
    params.set('contactMobile', form.contactMobile.trim())
    setTimeout(() => {
      router.push(`/inquiry/pickup/pass?${params.toString()}`)
    }, 500)
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    submitting.value = false
  }
}

function backToCompare() {
  if (!form.inquiryId.trim()) {
    router.push('/inquiry/compare')
    return
  }
  const params = new URLSearchParams()
  params.set('inquiryId', form.inquiryId.trim())
  if (form.contactMobile.trim()) {
    params.set('contactMobile', form.contactMobile.trim())
  }
  router.push(`/inquiry/compare?${params.toString()}`)
}

onMounted(() => {
  const inquiryId = String(route.query.inquiryId || '').trim()
  const quoteId = String(route.query.quoteId || '').trim()
  const contactMobile = String(route.query.contactMobile || '').trim()
  if (inquiryId) form.inquiryId = inquiryId
  if (quoteId) form.quoteId = quoteId
  if (contactMobile) form.contactMobile = contactMobile
  if (canPreview.value) {
    loadPreview()
  }
})
</script>

<template>
  <main class="deal-confirm-page">
    <section class="card">
      <h1>P07 成交确认页</h1>
      <p class="desc">买家在报价对比后选择成交商家，确认成交信息并推进履约执行。</p>
    </section>

    <section class="card">
      <h2>成交确认参数</h2>
      <div class="grid">
        <label>
          询价单ID
          <input v-model="form.inquiryId" placeholder="如 IQ20260418001" />
        </label>
        <label>
          报价ID
          <input v-model="form.quoteId" placeholder="如 IQ20260418001-Q2" />
        </label>
        <label>
          询价手机号
          <input v-model="form.contactMobile" placeholder="11位手机号" />
        </label>
      </div>
      <div class="actions">
        <button class="btn primary" :disabled="!canPreview || loadingPreview" @click="loadPreview">
          {{ loadingPreview ? '加载中...' : '加载成交预览' }}
        </button>
        <button class="btn" @click="backToCompare">返回报价对比</button>
      </div>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
      <p v-if="successMsg" class="success">{{ successMsg }}</p>
    </section>

    <section class="card" v-if="preview.quoteId">
      <h2>成交预览</h2>
      <div class="preview-grid">
        <article>
          <h3>询价信息</h3>
          <p>询价单号：{{ preview.inquiryNo }}</p>
          <p>规格：{{ preview.specText }}</p>
          <p>收货地：{{ preview.deliveryCity }} ｜ 数量：{{ preview.demandQtyTon }} 吨</p>
          <p>票据需求：{{ preview.invoiceNeed || '-' }} ｜ 期望交付：{{ preview.expectedDeliveryAt || '-' }}</p>
        </article>
        <article>
          <h3>报价信息</h3>
          <p>商家：{{ preview.supplierName }}（{{ preview.supplierId }}）</p>
          <p>单价：¥{{ preview.unitPrice || '-' }} / 吨</p>
          <p>总价：¥{{ preview.totalAmount || '-' }}</p>
          <p>账期：{{ preview.paymentTerm || '-' }} ｜ 交付天数：{{ preview.deliveryDays || '-' }}</p>
        </article>
      </div>
      <p class="risk">{{ preview.riskNotice }}</p>
    </section>

    <section class="card" v-if="preview.quoteId">
      <h2>提交成交确认</h2>
      <div class="grid">
        <label>
          买方公司
          <input v-model="form.buyerCompany" placeholder="如 唐山工程采购有限公司" />
        </label>
        <label>
          买方联系人
          <input v-model="form.buyerContact" placeholder="如 王经理" />
        </label>
        <label>
          买方联系电话
          <input v-model="form.buyerPhone" placeholder="11位手机号" />
        </label>
      </div>
      <div class="grid">
        <label>
          预计签约日期
          <input v-model="form.expectedSignDate" placeholder="如 2026-05-01" />
        </label>
      </div>
      <label class="remark">
        备注
        <textarea v-model="form.remark" rows="3" placeholder="如：请先发送电子合同模板"></textarea>
      </label>
      <div class="actions">
        <button class="btn primary" :disabled="!canSubmit || submitting" @click="submitDeal">
          {{ submitting ? '提交中...' : '确认成交' }}
        </button>
      </div>
    </section>
  </main>
</template>

<style scoped>
.deal-confirm-page {
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

h1,
h2 {
  margin: 0 0 8px;
}

.desc {
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

input,
textarea {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 9px 10px;
  font: inherit;
}

.remark {
  margin-top: 10px;
}

.actions {
  display: flex;
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

.preview-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.preview-grid article {
  border: 1px solid #f2f2f2;
  border-radius: 10px;
  padding: 12px;
}

.preview-grid h3 {
  margin: 0 0 8px;
}

.preview-grid p {
  margin: 4px 0;
  color: #4b5563;
}

.risk {
  margin-top: 10px;
  color: #b54708;
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
  .preview-grid {
    grid-template-columns: 1fr;
  }
}
</style>
