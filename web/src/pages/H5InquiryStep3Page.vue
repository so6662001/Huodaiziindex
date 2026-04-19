<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const errorMsg = ref('')

const summary = reactive({
  draftId: '',
  inquiryId: '',
  inquiryNo: '',
  inquiryStatus: '',
  specText: '',
  deliveryCity: '',
  demandQtyTon: '',
  contactMobileMasked: '',
  createdAt: '',
  quoteCount: 0,
  nextSteps: [],
  compareUrl: '',
  tipText: ''
})

async function loadStep3() {
  if (loading.value) return
  loading.value = true
  errorMsg.value = ''
  try {
    const draftId = String(route.query.draftId || '').trim()
    if (!draftId) {
      throw new Error('缺少draftId，请先完成Step1和Step2')
    }
    const params = new URLSearchParams()
    params.set('draftId', draftId)
    const resp = await fetch(`/api/v1/inquiries/h5/inquiry/step3?${params.toString()}`)
    const json = await resp.json()
    if (json.code !== '0') {
      throw new Error(json.message || '加载Step3失败')
    }
    const data = json.data || {}
    summary.draftId = data.draftId || draftId
    summary.inquiryId = data.inquiryId || ''
    summary.inquiryNo = data.inquiryNo || ''
    summary.inquiryStatus = data.inquiryStatus || ''
    summary.specText = data.specText || ''
    summary.deliveryCity = data.deliveryCity || ''
    summary.demandQtyTon = data.demandQtyTon || ''
    summary.contactMobileMasked = data.contactMobileMasked || ''
    summary.createdAt = data.createdAt || ''
    summary.quoteCount = Number(data.quoteCount || 0)
    summary.nextSteps = Array.isArray(data.nextSteps) ? data.nextSteps : []
    summary.compareUrl = data.compareUrl || ''
    summary.tipText = data.tipText || ''
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    loading.value = false
  }
}

function gotoCompare() {
  if (!summary.compareUrl) return
  router.push(summary.compareUrl)
}

function restartInquiry() {
  router.push('/h5/inquiry/step1')
}

onMounted(() => {
  loadStep3()
})
</script>

<template>
  <main class="h5-step3-page">
    <section class="card hero">
      <h1>H04 H5询价 Step3</h1>
      <p>询价已提交成功，系统正在匹配商家报价，你可以继续查看报价对比并推进成交。</p>
    </section>

    <section class="card">
      <p v-if="loading">加载中...</p>
      <template v-else>
        <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
        <template v-else>
          <h2>询价结果摘要</h2>
          <p>草稿ID：{{ summary.draftId || '-' }}</p>
          <p>询价ID：{{ summary.inquiryId || '-' }}</p>
          <p>询价单号：{{ summary.inquiryNo || '-' }}</p>
          <p>状态：{{ summary.inquiryStatus || '-' }}</p>
          <p>规格：{{ summary.specText || '-' }}</p>
          <p>收货地：{{ summary.deliveryCity || '-' }} ｜ 数量：{{ summary.demandQtyTon || '-' }} 吨</p>
          <p>联系手机：{{ summary.contactMobileMasked || '-' }}</p>
          <p>创建时间：{{ summary.createdAt || '-' }}</p>
          <p>当前报价数：{{ summary.quoteCount }}</p>
          <p class="tip">{{ summary.tipText || '-' }}</p>

          <div class="actions">
            <button class="btn btn--primary" :disabled="!summary.compareUrl" @click="gotoCompare">查看报价对比</button>
            <button class="btn" @click="restartInquiry">再发一单</button>
            <button class="btn" @click="loadStep3">刷新状态</button>
          </div>

          <div v-if="summary.nextSteps.length > 0" class="next-steps">
            <h3>建议下一步</h3>
            <ol>
              <li v-for="(step, idx) in summary.nextSteps" :key="`${idx}-${step}`">{{ step }}</li>
            </ol>
          </div>
        </template>
      </template>
    </section>
  </main>
</template>

<style scoped>
.h5-step3-page {
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
h2 {
  margin: 0 0 8px;
}
p {
  margin: 6px 0 0;
  color: #4b5563;
}
.tip {
  color: #1f7a43;
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
.next-steps {
  margin-top: 14px;
}
.next-steps h3 {
  margin: 0 0 8px;
}
.next-steps ol {
  margin: 0;
  padding-left: 18px;
  color: #4b5563;
}
.next-steps li {
  margin: 6px 0 0;
}
.error {
  color: #b42318;
}
</style>
