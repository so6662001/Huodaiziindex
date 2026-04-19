<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const errorMsg = ref('')
const submitted = ref(false)
const submitMsg = ref('')

const detail = reactive({
  placement: null,
  benefits: [],
  faqs: []
})

const form = reactive({
  city: '全国',
  duration: '30天',
  companyName: '',
  contactName: '',
  contactPhone: '',
  budget: '',
  remark: '',
  agreed: false
})

const placementId = computed(() => String(route.params.id || '').trim())

const canSubmit = computed(() => {
  const mobileReg = /^1\d{10}$/
  return (
    form.companyName.trim().length >= 4 &&
    form.contactName.trim().length >= 2 &&
    mobileReg.test(form.contactPhone.trim()) &&
    form.agreed
  )
})

async function fetchDetail() {
  loading.value = true
  errorMsg.value = ''
  try {
    const params = new URLSearchParams({ id: placementId.value })
    const resp = await fetch(`/api/v1/site-ad-detail?${params.toString()}`)
    const json = await resp.json()
    if (json.code !== '0') {
      throw new Error(json.message || '加载详情失败')
    }
    detail.placement = json.data.placement
    detail.benefits = json.data.benefits || []
    detail.faqs = json.data.faqs || []
    if (detail.placement?.recommendedCities?.length) {
      form.city = detail.placement.recommendedCities[0]
    }
    document.title = `${detail.placement?.title || '分站广告位详情'}-货袋子`
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    loading.value = false
  }
}

async function submitLead() {
  if (!canSubmit.value) return
  const payload = {
    placementId: placementId.value,
    city: form.city,
    duration: form.duration,
    companyName: form.companyName,
    contactName: form.contactName,
    contactPhone: form.contactPhone,
    budget: form.budget,
    remark: form.remark,
    agreed: form.agreed
  }
    const resp = await fetch('/api/v1/site-ad-detail/lead', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  })
  const json = await resp.json()
  if (json.code !== '0') {
    throw new Error(json.message || '提交失败')
  }
  submitted.value = true
    submitMsg.value = `线索编号：${json.data.leadNo || '-'}，提交成功`
}

async function handleSubmit() {
  try {
    await submitLead()
  } catch (error) {
    errorMsg.value = error.message || '提交失败'
  }
}

function goBack() {
  router.push('/site/ad')
}

watch(
  () => placementId.value,
  () => {
    submitted.value = false
    submitMsg.value = ''
    fetchDetail()
  }
)

onMounted(fetchDetail)
</script>

<template>
  <main class="site-ad-detail-page">
    <section class="hero card">
      <button class="back-btn" @click="goBack">返回广告位列表</button>
      <h1>P18 分站广告位详情页</h1>
      <p v-if="detail.placement">{{ detail.placement.name }} · {{ detail.placement.price }}</p>
      <p v-else>加载中...</p>
    </section>

    <section v-if="errorMsg" class="card error">{{ errorMsg }}</section>

    <section v-if="detail.placement" class="content-grid">
      <article class="card detail-main">
        <h2>{{ detail.placement.name }}</h2>
        <p class="desc">{{ detail.placement.desc }}</p>

        <div class="meta-grid">
          <div><span>曝光位置</span><strong>{{ detail.placement.exposure }}</strong></div>
          <div><span>支持定向</span><strong>{{ detail.placement.support.join(' / ') }}</strong></div>
          <div><span>结算方式</span><strong>{{ detail.placement.settlement }}</strong></div>
          <div><span>参考价格</span><strong>{{ detail.placement.price }}</strong></div>
          <div><span>覆盖城市</span><strong>{{ detail.placement.cityScope }}</strong></div>
          <div><span>人群画像</span><strong>{{ detail.placement.audience }}</strong></div>
          <div><span>素材规格</span><strong>{{ detail.placement.materialSpec }}</strong></div>
          <div><span>更新时间</span><strong>{{ detail.placement.updatedAt }}</strong></div>
        </div>

        <h3>投放收益</h3>
        <ul class="benefit-list">
          <li v-for="item in detail.benefits" :key="item.title">
            <h4>{{ item.title }}</h4>
            <p>{{ item.content }}</p>
          </li>
        </ul>

        <h3>常见问题</h3>
        <ul class="faq-list">
          <li v-for="item in detail.faqs" :key="item.question">
            <strong>{{ item.question }}</strong>
            <p>{{ item.answer }}</p>
          </li>
        </ul>
      </article>

      <aside class="card detail-form">
        <h3>提交投放意向</h3>
        <p>1小时内商务顾问回电</p>

        <div v-if="!submitted" class="form-grid">
          <label>投放城市<input v-model="form.city" type="text" placeholder="如：唐山" /></label>
          <label>投放周期<input v-model="form.duration" type="text" placeholder="如：30天" /></label>
          <label>预算区间<input v-model="form.budget" type="text" placeholder="如：5000-10000" /></label>
          <label>公司名称<input v-model="form.companyName" type="text" placeholder="请输入公司全称" /></label>
          <label>联系人<input v-model="form.contactName" type="text" placeholder="请输入联系人" /></label>
          <label>联系电话<input v-model="form.phone" type="text" placeholder="请输入11位手机号" /></label>
          <label class="full">投放诉求<textarea v-model="form.remark" rows="4" placeholder="可选填"></textarea></label>
          <label class="full agree"><input v-model="form.agreed" type="checkbox" /> 我已阅读并同意《广告投放服务协议》</label>
          <button class="btn primary full" :disabled="!canSubmit || loading" @click="handleSubmit">提交需求</button>
        </div>

        <div v-else class="submit-ok">
          <h4>提交成功</h4>
          <p>{{ submitMsg }}</p>
          <button class="btn" @click="goBack">返回广告位列表</button>
        </div>
      </aside>
    </section>
  </main>
</template>

<style scoped>
.site-ad-detail-page {
  max-width: 1180px;
  margin: 0 auto;
  padding: 20px 16px 40px;
}
.card {
  background: #fff;
  border: 1px solid #ececec;
  border-radius: 12px;
  padding: 16px;
}
.hero {
  margin-bottom: 16px;
}
.back-btn {
  border: 0;
  background: #fff2e6;
  color: #f57c00;
  border-radius: 8px;
  padding: 6px 10px;
  cursor: pointer;
  margin-bottom: 8px;
}
.content-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 14px;
}
.desc {
  color: #555;
}
.meta-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
  margin: 12px 0;
}
.meta-grid div {
  background: #fafafa;
  border-radius: 8px;
  padding: 10px;
}
.meta-grid span {
  display: block;
  font-size: 12px;
  color: #888;
}
.meta-grid strong {
  color: #333;
}
.benefit-list,
.faq-list {
  list-style: none;
  padding: 0;
  margin: 8px 0 0;
}
.benefit-list li,
.faq-list li {
  border-top: 1px dashed #ececec;
  padding: 10px 0;
}
.detail-form p {
  color: #666;
}
.form-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 10px;
}
label {
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 13px;
}
input,
textarea,
button {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 9px 10px;
}
.full {
  width: 100%;
}
.agree {
  flex-direction: row;
  align-items: center;
}
.btn {
  cursor: pointer;
  background: #fff;
}
.btn.primary {
  background: #f57c00;
  border-color: #f57c00;
  color: #fff;
}
.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.submit-ok h4 {
  margin: 0 0 8px;
}
.error {
  color: #b42318;
  margin-bottom: 14px;
}
@media (max-width: 900px) {
  .content-grid {
    grid-template-columns: 1fr;
  }
}
</style>
