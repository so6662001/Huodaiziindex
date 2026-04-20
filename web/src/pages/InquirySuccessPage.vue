<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const errorMsg = ref('')
const payload = reactive({
  inquiryId: '',
  inquiryNo: '',
  inquiryStatus: '',
  tip: '',
  recommendAction: '',
  quoteCount: 0,
  fastQuoteCount: 0,
  comparedCount: 0
})

const hasData = computed(() => payload.inquiryId && payload.inquiryNo)
const statusLabel = computed(() => {
  const map = {
    OPEN: '已发起',
    QUOTING: '报价中',
    DEAL_DONE: '已成交',
    CLOSED: '已关闭'
  }
  return map[payload.inquiryStatus] || payload.inquiryStatus || '-'
})

async function loadSuccess() {
  const inquiryId = String(route.query.inquiryId || '').trim()
  const contactMobile = String(route.query.contactMobile || '').trim()
  if (!inquiryId || !/^1\d{10}$/.test(contactMobile)) {
    errorMsg.value = '缺少参数，请从询价提交页重新进入'
    return
  }
  loading.value = true
  errorMsg.value = ''
  try {
    const params = new URLSearchParams()
    params.set('inquiryId', inquiryId)
    params.set('contactMobile', contactMobile)
    const resp = await fetch(`/api/v1/inquiries/success?${params.toString()}`)
    const json = await resp.json()
    if (json.code !== '0') {
      throw new Error(json.message || '加载成功页信息失败')
    }
    Object.assign(payload, json.data || {})
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    loading.value = false
  }
}

function goCompare() {
  if (!payload.inquiryId) return
  const mobile = String(route.query.contactMobile || '').trim()
  const params = new URLSearchParams()
  params.set('inquiryId', payload.inquiryId)
  if (mobile) {
    params.set('contactMobile', mobile)
  }
  router.push(`/inquiry/compare?${params.toString()}`)
}

function goCreate() {
  router.push('/inquiry/create')
}

onMounted(loadSuccess)
</script>

<template>
  <main class="success-page">
    <section class="card hero">
      <h1>P03 AI询价成功</h1>
      <p>询价已提交，系统正在将需求推送给优质商家。</p>
    </section>

    <section class="card">
      <p v-if="loading">加载中...</p>
      <p v-else-if="errorMsg" class="error">{{ errorMsg }}</p>
      <template v-else-if="hasData">
        <div class="grid">
          <article class="item">
            <h3>询价单号</h3>
            <p>{{ payload.inquiryNo }}</p>
          </article>
          <article class="item">
            <h3>当前状态</h3>
            <p>{{ statusLabel }}</p>
          </article>
          <article class="item">
            <h3>已返回报价</h3>
            <p>{{ payload.quoteCount }} 家</p>
          </article>
          <article class="item">
            <h3>快速响应（30分钟内）</h3>
            <p>{{ payload.fastQuoteCount }} 家</p>
          </article>
        </div>

        <div class="tip-box">
          <p>{{ payload.tip }}</p>
          <p>{{ payload.recommendAction }}</p>
        </div>

        <div class="actions">
          <button class="btn primary" @click="goCompare">去报价对比（P04）</button>
          <button class="btn" @click="goCreate">继续发起询价</button>
        </div>
      </template>
    </section>
  </main>
</template>

<style scoped>
.success-page {
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
.grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
}
.item {
  border: 1px solid #f3f4f6;
  border-radius: 10px;
  padding: 10px;
}
.item h3 {
  margin: 0 0 6px;
  font-size: 14px;
  color: #6b7280;
}
.item p {
  margin: 0;
  font-size: 18px;
  color: #111827;
}
.tip-box {
  margin-top: 12px;
  border: 1px solid #ffedd5;
  background: #fff7ed;
  border-radius: 10px;
  padding: 10px 12px;
  color: #9a3412;
}
.tip-box p {
  margin: 0 0 6px;
}
.tip-box p:last-child {
  margin-bottom: 0;
}
.actions {
  display: flex;
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
.btn.primary {
  border-color: #f57c00;
  background: #f57c00;
  color: #fff;
}
.error {
  color: #b42318;
}
@media (max-width: 760px) {
  .grid {
    grid-template-columns: 1fr;
  }
  .actions {
    flex-direction: column;
  }
}
</style>
