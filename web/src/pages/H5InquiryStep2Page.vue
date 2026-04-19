<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const submitting = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

const state = reactive({
  draftId: '',
  categoryCode: '',
  specText: '',
  deliveryCity: '',
  demandQtyTon: '',
  invoiceNeed: '',
  contactMobileMasked: '',
  expectedDeliveryOptions: [],
  settleTypeOptions: []
})

const form = reactive({
  expectedDeliveryAt: '',
  deliveryTimeRange: '09:00-18:00',
  unloadSupport: 'N',
  needInvoice: 'Y',
  step2Remark: ''
})

const canSubmit = computed(() => state.draftId && form.needInvoice && form.unloadSupport)

async function loadInit() {
  if (loading.value) return
  loading.value = true
  errorMsg.value = ''
  try {
    const draftId = String(route.query.draftId || '').trim()
    if (!draftId) {
      throw new Error('缺少draftId，请先完成Step1')
    }
    const params = new URLSearchParams()
    params.set('draftId', draftId)
    const resp = await fetch(`/api/v1/inquiries/h5/inquiry/step2/init?${params.toString()}`)
    const json = await resp.json()
    if (json.code !== '0') {
      throw new Error(json.message || '初始化Step2失败')
    }
    const data = json.data || {}
    state.draftId = data.draftId || draftId
    state.categoryCode = data.categoryCode || ''
    state.specText = data.specText || ''
    state.deliveryCity = data.deliveryCity || ''
    state.demandQtyTon = data.demandQtyTon || ''
    state.invoiceNeed = data.invoiceNeed || ''
    state.contactMobileMasked = data.contactMobileMasked || ''
    state.expectedDeliveryOptions = Array.isArray(data.expectedDeliveryOptions) ? data.expectedDeliveryOptions : []
    state.settleTypeOptions = Array.isArray(data.settleTypeOptions) ? data.settleTypeOptions : []
    successMsg.value = data.tipText || ''
    if (!form.expectedDeliveryAt && state.expectedDeliveryOptions.length > 0) {
      form.expectedDeliveryAt = state.expectedDeliveryOptions[0].code
    }
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    loading.value = false
  }
}

async function submitStep2() {
  if (!canSubmit.value || submitting.value) return
  submitting.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      draftId: state.draftId,
      expectedDeliveryAt: form.expectedDeliveryAt || null,
      deliveryTimeRange: form.deliveryTimeRange.trim() || null,
      unloadSupport: form.unloadSupport,
      needInvoice: form.needInvoice,
      step2Remark: form.step2Remark.trim() || null
    }
    const resp = await fetch('/api/v1/inquiries/h5/inquiry/step2/submit', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    })
    const json = await resp.json()
    if (json.code !== '0') {
      throw new Error(json.message || '提交Step2失败')
    }
    const data = json.data || {}
    successMsg.value = data.successMessage || '提交成功'
    if (data.successUrl) {
      router.push(data.successUrl)
    }
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadInit()
})
</script>

<template>
  <main class="h5-step2-page">
    <section class="card hero">
      <h1>H03 H5询价 Step2</h1>
      <p>补充交期与履约偏好后提交询价，系统将自动匹配商家报价。</p>
    </section>

    <section class="card basic">
      <h2>Step1已填写信息</h2>
      <p>品类：{{ state.categoryCode || '-' }}</p>
      <p>规格：{{ state.specText || '-' }}</p>
      <p>收货地：{{ state.deliveryCity || '-' }} ｜ 数量：{{ state.demandQtyTon || '-' }} 吨</p>
      <p>票据需求：{{ state.invoiceNeed || '-' }} ｜ 联系手机：{{ state.contactMobileMasked || '-' }}</p>
    </section>

    <section class="card form-card">
      <p v-if="loading">加载中...</p>
      <template v-else>
        <label>
          期望交期
          <select v-model="form.expectedDeliveryAt">
            <option v-for="item in state.expectedDeliveryOptions" :key="item.code" :value="item.code">
              {{ item.name }}
            </option>
          </select>
        </label>
        <label>
          收货时段
          <input v-model="form.deliveryTimeRange" placeholder="如 09:00-18:00" />
        </label>
        <label>
          需要卸货协助
          <select v-model="form.unloadSupport">
            <option value="Y">需要</option>
            <option value="N">不需要</option>
          </select>
        </label>
        <label>
          是否需要发票
          <select v-model="form.needInvoice">
            <option value="Y">需要</option>
            <option value="N">不需要</option>
          </select>
        </label>
        <label>
          结算偏好
          <select>
            <option v-for="item in state.settleTypeOptions" :key="item.code" :value="item.code">
              {{ item.name }}
            </option>
          </select>
        </label>
        <label>
          补充说明（选填）
          <textarea v-model="form.step2Remark" rows="3" placeholder="如 需地磅单据、优先上午到货"></textarea>
        </label>

        <div class="actions">
          <button class="btn btn--primary" :disabled="!canSubmit || submitting" @click="submitStep2">
            {{ submitting ? '提交中...' : '提交询价' }}
          </button>
          <button class="btn" :disabled="loading || submitting" @click="loadInit">刷新</button>
        </div>
      </template>
      <p v-if="successMsg" class="success">{{ successMsg }}</p>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
    </section>
  </main>
</template>

<style scoped>
.h5-step2-page {
  max-width: 760px;
  margin: 0 auto;
  padding: 12px 12px 28px;
}
.card {
  background: #fff;
  border: 1px solid #ececec;
  border-radius: 12px;
  padding: 14px;
  margin-bottom: 12px;
}
.hero h1 {
  margin: 0 0 8px;
}
.hero p {
  margin: 0;
  color: #4b5563;
}
.basic h2 {
  margin: 0 0 8px;
}
.basic p {
  margin: 6px 0 0;
  color: #4b5563;
}
.form-card {
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
.success {
  color: #1f7a43;
  margin: 0;
}
.error {
  color: #b42318;
  margin: 0;
}
</style>
