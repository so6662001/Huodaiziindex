<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const initLoading = ref(false)
const saving = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

const state = reactive({
  city: '全国',
  draftId: '',
  categories: [],
  deliveryCities: []
})

const form = reactive({
  categoryCode: 'REBAR',
  specText: '',
  deliveryCity: '唐山',
  demandQtyTon: '',
  invoiceNeed: 'ANY',
  contactMobile: '',
  remark: ''
})

const canSave = computed(() => {
  return (
    form.categoryCode.trim().length > 0 &&
    form.specText.trim().length > 0 &&
    form.deliveryCity.trim().length > 0 &&
    Number(form.demandQtyTon) > 0 &&
    /^1\d{10}$/.test(form.contactMobile.trim())
  )
})

async function loadInit() {
  if (initLoading.value) return
  initLoading.value = true
  errorMsg.value = ''
  try {
    const city = String(route.query.city || '全国').trim() || '全国'
    const params = new URLSearchParams()
    params.set('city', city)
    const resp = await fetch(`/api/v1/inquiries/h5/inquiry/step1/init?${params.toString()}`)
    const json = await resp.json()
    if (json.code !== '0') {
      throw new Error(json.message || '初始化H5询价Step1失败')
    }
    const data = json.data || {}
    state.city = data.city || city
    state.draftId = data.draftId || ''
    state.categories = Array.isArray(data.categories) ? data.categories : []
    state.deliveryCities = Array.isArray(data.deliveryCities) ? data.deliveryCities : []
    form.categoryCode = data.defaultCategoryCode || form.categoryCode
    if (state.deliveryCities.length > 0 && !state.deliveryCities.some((item) => item.code === form.deliveryCity)) {
      form.deliveryCity = state.deliveryCities[0].code
    }
    successMsg.value = data.tipText || ''
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    initLoading.value = false
  }
}

async function saveStep1() {
  if (!canSave.value || saving.value) return
  saving.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      draftId: state.draftId,
      categoryCode: form.categoryCode.trim(),
      specText: form.specText.trim(),
      deliveryCity: form.deliveryCity.trim(),
      demandQtyTon: Number(form.demandQtyTon),
      invoiceNeed: form.invoiceNeed,
      contactMobile: form.contactMobile.trim(),
      remark: form.remark.trim() || null
    }
    const resp = await fetch('/api/v1/inquiries/h5/inquiry/step1/save', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    })
    const json = await resp.json()
    if (json.code !== '0') {
      throw new Error(json.message || '保存Step1失败')
    }
    const data = json.data || {}
    successMsg.value = `已保存：${data.summaryText || ''}`
    if (data.nextStepUrl) {
      router.push(data.nextStepUrl)
    }
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  loadInit()
})
</script>

<template>
  <main class="h5-step1-page">
    <section class="card hero">
      <h1>H02 H5询价 Step1</h1>
      <p>先提交核心需求：品类、规格、收货地、数量与联系方式，系统将保存草稿并进入下一步。</p>
      <p class="muted">当前城市：{{ state.city }}</p>
    </section>

    <section class="card form-card">
      <p v-if="initLoading">初始化中...</p>
      <template v-else>
        <label>
          品类
          <select v-model="form.categoryCode">
            <option v-for="item in state.categories" :key="item.code" :value="item.code">
              {{ item.name }}
            </option>
          </select>
        </label>
        <label>
          规格
          <input v-model="form.specText" placeholder="如 HRB400E Φ20*12m" />
        </label>
        <label>
          收货地
          <select v-model="form.deliveryCity">
            <option v-for="item in state.deliveryCities" :key="item.code" :value="item.code">
              {{ item.name }}
            </option>
          </select>
        </label>
        <label>
          需求数量（吨）
          <input v-model="form.demandQtyTon" type="number" min="0" step="0.001" placeholder="如 120" />
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
          <textarea v-model="form.remark" rows="3" placeholder="补充交付要求、装车时段等"></textarea>
        </label>

        <div class="actions">
          <button class="btn btn--primary" :disabled="!canSave || saving || !state.draftId" @click="saveStep1">
            {{ saving ? '保存中...' : '保存并进入Step2' }}
          </button>
          <button class="btn" :disabled="!state.draftId || initLoading || saving" @click="router.push(`/h5/inquiry/step2?draftId=${encodeURIComponent(state.draftId)}`)">
            直接去Step2
          </button>
          <button class="btn" :disabled="initLoading || saving" @click="loadInit">重新初始化</button>
        </div>
      </template>

      <p v-if="successMsg" class="success">{{ successMsg }}</p>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
    </section>
  </main>
</template>

<style scoped>
.h5-step1-page {
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
.muted {
  margin-top: 8px !important;
  color: #6b7280 !important;
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
