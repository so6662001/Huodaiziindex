<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const errorMsg = ref('')

const query = reactive({
  city: '',
  keyword: ''
})

const home = reactive({
  city: '',
  weather: '',
  updateTime: '',
  quickNavs: [],
  banners: [],
  marketCards: [],
  recommendations: []
})

const hasData = computed(
  () =>
    home.quickNavs.length > 0 ||
    home.banners.length > 0 ||
    home.marketCards.length > 0 ||
    home.recommendations.length > 0
)

async function loadHome() {
  if (loading.value) return
  loading.value = true
  errorMsg.value = ''
  try {
    const params = new URLSearchParams()
    params.set('city', query.city.trim() || '全国')
    if (query.keyword.trim()) {
      params.set('keyword', query.keyword.trim())
    }
    const resp = await fetch(`/api/v1/inquiries/h5/home?${params.toString()}`)
    const json = await resp.json()
    if (json.code !== '0') {
      throw new Error(json.message || '加载H5首页失败')
    }
    const data = json.data || {}
    home.city = data.city || query.city || '全国'
    home.weather = data.weather || '-'
    home.updateTime = data.updateTime || ''
    home.quickNavs = Array.isArray(data.quickNavs) ? data.quickNavs : []
    home.banners = Array.isArray(data.banners) ? data.banners : []
    home.marketCards = Array.isArray(data.marketCards) ? data.marketCards : []
    home.recommendations = Array.isArray(data.recommendations) ? data.recommendations : []
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    loading.value = false
  }
}

function goto(url) {
  if (!url) return
  if (url.includes('/h5/auth/quick-login')) {
    router.push('/h5/auth/quick-login')
    return
  }
  if (url === '/inquiry/create') {
    router.push('/h5/inquiry/step1')
    return
  }
  if (url.includes('/merchant/lead/manage')) {
    router.push('/h5/merchant/leads?merchantId=S001')
    return
  }
  if (url.includes('/h5/quick-quote') || url.includes('/quick-quote')) {
    router.push('/h5/quick-quote?merchantId=S001')
    return
  }
  if (url.includes('/h5/pickup-orders') || url.includes('/pickup-orders')) {
    router.push('/h5/pickup-orders?contactMobile=13800138000')
    return
  }
  if (url.includes('/h5/reconcile-orders') || url.includes('/reconcile-orders')) {
    router.push('/h5/reconcile-orders?contactMobile=13800138000')
    return
  }
  if (url.includes('/h5/member') || url.includes('/subscription')) {
    router.push('/h5/member?merchantId=S001')
    return
  }
  router.push(url)
}

function goQuickLogin() {
  router.push('/h5/login-quick')
}

function goIdentitySwitch() {
  router.push('/h5/identity-switch')
}

function goEnterpriseCertification() {
  router.push('/h5/enterprise-certification')
}

function goNegotiationSession() {
  router.push('/h5/negotiation-session')
}

function goQuoteSession() {
  router.push('/h5/quote-session')
}

function colorClass(item) {
  const text = String(item?.changeRate || '')
  if (text.startsWith('+')) return 'up'
  if (text.startsWith('-')) return 'down'
  return 'flat'
}

onMounted(() => {
  query.city = String(route.query.city || '全国')
  query.keyword = String(route.query.keyword || '')
  loadHome()
})
</script>

<template>
  <main class="h5-home">
    <section class="hero card">
      <div class="hero-top">
        <h1>H01 H5 首页</h1>
        <span class="muted">{{ home.city }}｜{{ home.weather }}</span>
      </div>
      <p class="muted">更新时间：{{ home.updateTime || '-' }}</p>
      <div class="hero-actions">
        <button class="btn primary" @click="goQuickLogin">H5-N01 快捷登录</button>
        <button class="btn" @click="goIdentitySwitch">H5-N02 身份切换</button>
        <button class="btn" @click="goEnterpriseCertification">H5-N03 企业认证</button>
        <button class="btn" @click="goQuoteSession">H5-N04 报价会话</button>
      </div>
      <div class="filters">
        <label>
          城市
          <input v-model="query.city" placeholder="如 唐山" />
        </label>
        <label>
          关键词
          <input v-model="query.keyword" placeholder="规格/品类" />
        </label>
        <button class="btn primary" :disabled="loading" @click="loadHome">
          {{ loading ? '加载中...' : '刷新首页' }}
        </button>
      </div>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
    </section>

    <section v-if="home.banners.length" class="card">
      <h2>焦点位</h2>
      <div class="banner-list">
        <article v-for="item in home.banners" :key="item.bannerId" class="banner-item">
          <div>
            <h3>{{ item.title }}</h3>
            <p class="muted">{{ item.subtitle }}</p>
          </div>
          <button class="btn" @click="goto(item.actionUrl)">{{ item.actionText || '查看' }}</button>
        </article>
      </div>
    </section>

    <section v-if="home.quickNavs.length" class="card">
      <h2>金刚位</h2>
      <div class="quick-nav-grid">
        <button v-for="item in home.quickNavs" :key="item.code" class="quick-nav" @click="goto(item.route)">
          <strong>{{ item.name }}</strong>
          <span class="muted">{{ item.tag || '-' }}</span>
        </button>
      </div>
    </section>

    <section v-if="home.marketCards.length" class="card">
      <h2>行情快报</h2>
      <div class="market-list">
        <article v-for="item in home.marketCards" :key="item.marketId" class="market-item">
          <h3>{{ item.title }}</h3>
          <p class="muted">{{ item.spec }}｜{{ item.city }}</p>
          <div class="price-row">
            <strong>¥{{ item.price }}/吨</strong>
            <span class="change" :class="colorClass(item)">{{ item.changeRate }}</span>
          </div>
          <p class="muted">库存：{{ item.stock }} 吨</p>
        </article>
      </div>
    </section>

    <section v-if="home.recommendations.length" class="card">
      <h2>推荐商家</h2>
      <div class="merchant-list">
        <article v-for="item in home.recommendations" :key="item.merchantId" class="merchant-item">
          <div class="merchant-main">
            <h3>{{ item.merchantName }}</h3>
            <p class="muted">{{ item.tag }}</p>
            <p class="muted">主营：{{ item.mainSpec }} ｜ 城市：{{ item.city }}</p>
          </div>
          <div class="merchant-side">
            <strong>评分 {{ item.score }}</strong>
            <span class="muted">响应 {{ item.responseMinutes }} 分钟</span>
            <button class="btn primary" @click="goto(item.actionUrl)">进入商家页</button>
          </div>
        </article>
      </div>
    </section>

    <section v-if="!loading && !hasData" class="card">
      <p class="muted">暂无首页数据</p>
    </section>
  </main>
</template>

<style scoped>
.h5-home {
  max-width: 900px;
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
.hero-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}
h1 {
  margin: 0;
  font-size: 24px;
}
h2 {
  margin: 0 0 10px;
  font-size: 18px;
}
h3 {
  margin: 0 0 6px;
  font-size: 16px;
}
.muted {
  color: #6b7280;
  margin: 0;
}
.error {
  color: #b42318;
  margin-top: 10px;
}
.hero-actions {
  margin-top: 10px;
  display: flex;
  gap: 8px;
}
.filters {
  display: grid;
  grid-template-columns: 1fr 1fr auto;
  gap: 8px;
  margin-top: 10px;
}
label {
  display: grid;
  gap: 6px;
  color: #374151;
}
input {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 8px 10px;
  font: inherit;
}
.btn {
  border: 1px solid #ddd;
  border-radius: 8px;
  background: #fff;
  padding: 8px 12px;
  cursor: pointer;
}
.btn.primary {
  border-color: #f57c00;
  background: #f57c00;
  color: #fff;
}
.banner-list,
.market-list,
.merchant-list {
  display: grid;
  gap: 10px;
}
.banner-item,
.market-item,
.merchant-item {
  border: 1px solid #f2f4f7;
  border-radius: 10px;
  padding: 10px;
}
.banner-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}
.quick-nav-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 8px;
}
.quick-nav {
  border: 1px solid #f2f4f7;
  border-radius: 10px;
  padding: 10px;
  text-align: left;
  background: #fff;
  cursor: pointer;
  display: grid;
  gap: 4px;
}
.price-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 8px 0;
}
.change.up {
  color: #dc2626;
}
.change.down {
  color: #16a34a;
}
.change.flat {
  color: #6b7280;
}
.merchant-item {
  display: flex;
  justify-content: space-between;
  gap: 10px;
}
.merchant-side {
  display: grid;
  gap: 6px;
  justify-items: end;
}
@media (max-width: 768px) {
  .filters {
    grid-template-columns: 1fr;
  }
  .quick-nav-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
  .merchant-item {
    grid-template-columns: 1fr;
    display: grid;
  }
  .merchant-side {
    justify-items: start;
  }
}
</style>
