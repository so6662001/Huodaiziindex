<script setup>
import { computed, onMounted, ref, watch } from 'vue'

const currentStep = ref(1)
const isSubmitted = ref(false)

const form = ref({
  title: '',
  city: '唐山',
  goodsCategory: '螺纹钢',
  tonnage: '',
  storageDays: '',
  inboundDate: '',
  needLoading: true,
  needSorting: false,
  contactName: '',
  contactPhone: '',
  companyName: '',
  remark: '',
  agreed: false
})

const cityOptions = ['唐山', '天津', '无锡', '佛山', '武汉', '成都']
const goodsOptions = ['螺纹钢', '热卷', '中厚板', '型钢', '管材', '其他']
const isSubmitting = ref(false)
const submitError = ref('')
const submitHint = ref('')

const isStepOneValid = computed(() => {
  return (
    form.value.title.trim().length >= 8 &&
    form.value.tonnage !== '' &&
    Number(form.value.tonnage) > 0 &&
    form.value.storageDays !== '' &&
    Number(form.value.storageDays) > 0 &&
    form.value.inboundDate !== ''
  )
})

const isStepTwoValid = computed(() => {
  const mobileReg = /^1\d{10}$/
  return (
    form.value.contactName.trim().length >= 2 &&
    mobileReg.test(form.value.contactPhone.trim()) &&
    form.value.companyName.trim().length >= 4 &&
    form.value.agreed
  )
})

const canSubmit = computed(() => isStepOneValid.value && isStepTwoValid.value)

const goNext = () => {
  if (!isStepOneValid.value) return
  currentStep.value = 2
}

const goPrev = () => {
  currentStep.value = 1
}

const submitDemand = async () => {
  if (!canSubmit.value) return
  isSubmitting.value = true
  submitError.value = ''
  submitHint.value = ''
  try {
    const resp = await fetch('/api/v1/storage-demand', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        ...form.value,
        agreed: form.value.agreed
      })
    })
    const json = await resp.json()
    if (json.code !== '0') {
      throw new Error(json.message || '提交失败，请稍后重试')
    }
    isSubmitted.value = true
  } catch (error) {
    submitError.value = error.message || '提交失败，请稍后重试'
  } finally {
    isSubmitting.value = false
  }
}

watch(
  () => currentStep.value,
  () => {
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }
)

onMounted(() => {
  document.title = '发布仓储需求_钢铁仓储需求快速发布-货袋子'
})
</script>

<template>
  <main class="page">
    <header class="hero">
      <h1>P11 发布仓储需求</h1>
      <p>填写需求信息，平台审核后将快速匹配本地仓储服务商。</p>
    </header>

    <section v-if="!isSubmitted" class="card">
      <template v-if="currentStep === 1">
        <h2>步骤1：业务信息</h2>
        <div class="grid">
          <label>需求标题<input v-model="form.title" type="text" /></label>
          <label>城市
            <select v-model="form.city">
              <option v-for="item in cityOptions" :key="item">{{ item }}</option>
            </select>
          </label>
          <label>货品品类
            <select v-model="form.goodsCategory">
              <option v-for="item in goodsOptions" :key="item">{{ item }}</option>
            </select>
          </label>
          <label>预计吨位（吨）<input v-model="form.tonnage" type="number" /></label>
          <label>存储天数（天）<input v-model="form.storageDays" type="number" /></label>
          <label>入库日期<input v-model="form.inboundDate" type="date" /></label>
          <label><input v-model="form.needLoading" type="checkbox" /> 需要装卸服务</label>
          <label><input v-model="form.needSorting" type="checkbox" /> 需要分拣服务</label>
        </div>
        <div class="actions">
          <RouterLink to="/logistics/demand/storage">返回需求列表</RouterLink>
          <button :disabled="!isStepOneValid" @click="goNext">下一步</button>
        </div>
      </template>

      <template v-else>
        <h2>步骤2：联系人信息</h2>
        <div class="grid">
          <label>联系人<input v-model="form.contactName" type="text" /></label>
          <label>联系电话<input v-model="form.contactPhone" type="text" /></label>
          <label>公司名称<input v-model="form.companyName" type="text" /></label>
          <label>备注<textarea v-model="form.remark" rows="4"></textarea></label>
          <label><input v-model="form.agreed" type="checkbox" /> 我已阅读并同意《仓储物流服务协议》</label>
        </div>
        <div class="actions">
          <button @click="goPrev">上一步</button>
          <button :disabled="!canSubmit || isSubmitting" @click="submitDemand">
            {{ isSubmitting ? '提交中...' : '提交审核' }}
          </button>
        </div>
        <p v-if="submitError" class="error">{{ submitError }}</p>
      </template>
    </section>

    <section v-else class="card">
      <h2>需求已提交</h2>
      <p>预计 1 小时内完成审核并分发服务商，请保持电话畅通。</p>
      <div class="actions">
        <RouterLink to="/logistics/demand/storage">查看需求列表</RouterLink>
      </div>
    </section>
  </main>
</template>

<style scoped>
.page {
  max-width: 1080px;
  margin: 0 auto;
  padding: 24px;
  font-family: Arial, sans-serif;
}

.hero {
  margin-bottom: 16px;
}

.card {
  border: 1px solid #ececec;
  border-radius: 10px;
  padding: 16px;
  background: #fff;
}

.grid {
  display: grid;
  gap: 12px;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  margin-bottom: 14px;
}

label {
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 14px;
}

input,
select,
textarea,
button {
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 6px;
}

.actions {
  display: flex;
  gap: 12px;
}
</style>
