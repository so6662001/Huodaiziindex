<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const loading = ref(false)
const switching = ref(false)
const errorMsg = ref('')
const successMsg = ref('')
const state = reactive({
  userId: '',
  account: '',
  currentIdentityCode: '',
  currentIdentityName: '',
  identities: [],
  tokenExpireAt: ''
})

const token = computed(() => localStorage.getItem('N01_AUTH_TOKEN') || '')
const hasSession = computed(() => token.value.length > 0)
const canSwitch = computed(() => hasSession.value && state.identities.length > 0)

async function loadOptions() {
  if (!hasSession.value || loading.value) {
    if (!hasSession.value) {
      errorMsg.value = '请先登录后再选择身份'
    }
    return
  }
  loading.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const resp = await fetch('/api/v1/identity/options', {
      headers: { 'X-Auth-Token': token.value }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `身份列表加载失败(${resp.status})`)
    }
    Object.assign(state, json.data || {})
  } catch (error) {
    errorMsg.value = error.message || '身份列表加载失败'
  } finally {
    loading.value = false
  }
}

async function switchIdentity(identityCode) {
  if (!hasSession.value || switching.value) return
  switching.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const resp = await fetch('/api/v1/identity/switch', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Auth-Token': token.value
      },
      body: JSON.stringify({ identityCode })
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `身份切换失败(${resp.status})`)
    }
    const data = json.data || {}
    successMsg.value = data.message || '身份切换成功'
    state.currentIdentityCode = data.activeIdentityCode || identityCode
    state.currentIdentityName = data.activeIdentityName || state.currentIdentityName
    await loadOptions()
  } catch (error) {
    errorMsg.value = error.message || '身份切换失败'
  } finally {
    switching.value = false
  }
}

function goLoginRegister() {
  router.push('/account/login-register')
}

function goHome() {
  router.push('/')
}

function goCertification() {
  router.push('/account/enterprise-certification')
}

function goOnboardingProgress() {
  router.push('/account/onboarding-progress')
}

function goNegotiationSession() {
  router.push('/account/negotiation-session')
}

function goOrderDetail() {
  router.push('/account/order-detail')
}

function goTradeTermsConfirm() {
  router.push('/account/trade-terms-confirm')
}

function goAfterSaleDispute() {
  router.push('/account/after-sale-dispute')
}

function goTarget(identityCode) {
  if (identityCode === 'BUYER') {
    router.push('/inquiry/create')
    return
  }
  if (identityCode === 'SUPPLIER') {
    router.push('/merchant/quote/workbench')
    return
  }
  router.push('/admin/dashboard/a01')
}

onMounted(() => {
  loadOptions()
})
</script>

<template>
  <main class="n02-page">
    <section class="card hero">
      <h1>PC-N02 身份选择</h1>
      <p>根据当前业务场景切换身份：采购方 / 供应方 / 运营方。</p>
      <div class="hero-actions">
        <button class="btn" @click="goLoginRegister">返回登录/注册</button>
        <button class="btn" @click="goHome">返回首页</button>
      </div>
    </section>

    <section class="card">
      <div class="head">
        <h2>当前会话</h2>
        <button class="btn" :disabled="loading" @click="loadOptions">{{ loading ? '刷新中...' : '刷新' }}</button>
      </div>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
      <p v-if="successMsg" class="ok">{{ successMsg }}</p>
      <div class="meta" v-if="state.userId">
        <p>用户ID：{{ state.userId }}</p>
        <p>账号：{{ state.account }}</p>
        <p>当前身份：{{ state.currentIdentityName }}（{{ state.currentIdentityCode }}）</p>
        <p>Token过期：{{ state.tokenExpireAt }}</p>
      </div>
      <p v-else class="tip">请先登录后选择身份</p>
    </section>

    <section class="card">
      <h2>可选身份</h2>
      <p v-if="loading">加载中...</p>
      <p v-else-if="!state.identities.length" class="tip">暂无可选身份</p>
      <div v-else class="grid">
        <article
          v-for="identity in state.identities"
          :key="identity.identityCode"
          class="identity-card"
          :class="{ active: identity.selected }"
        >
          <header>
            <h3>{{ identity.identityName }}</h3>
            <span class="tag">{{ identity.identityCode }}</span>
          </header>
          <p class="desc">{{ identity.sceneDesc }}</p>
          <div class="actions">
            <button
              class="btn"
              :class="{ 'btn--primary': identity.selected }"
              :disabled="!canSwitch || switching"
              @click="switchIdentity(identity.identityCode)"
            >
              {{ identity.selected ? '当前身份' : '切换为该身份' }}
            </button>
            <button
              v-if="identity.identityCode === 'BUYER'"
              class="btn"
              :disabled="switching"
              @click="goCertification"
            >
              企业认证提交
            </button>
            <button
              v-if="identity.identityCode === 'BUYER'"
              class="btn"
              :disabled="switching"
              @click="goOnboardingProgress"
            >
              入驻审核进度
            </button>
            <button
              v-if="identity.identityCode === 'BUYER'"
              class="btn"
              :disabled="switching"
              @click="goNegotiationSession"
            >
              议价会话页
            </button>
            <button
              v-if="identity.identityCode === 'BUYER'"
              class="btn"
              :disabled="switching"
              @click="goOrderDetail"
            >
              订单详情页
            </button>
            <button
              v-if="identity.identityCode === 'BUYER'"
              class="btn"
              :disabled="switching"
              @click="goTradeTermsConfirm"
            >
              交易条款确认页
            </button>
            <button
              v-if="identity.identityCode === 'BUYER'"
              class="btn"
              :disabled="switching"
              @click="goAfterSaleDispute"
            >
              售后/争议发起页
            </button>
            <button class="btn" :disabled="switching" @click="goTarget(identity.identityCode)">进入工作台</button>
          </div>
        </article>
      </div>
    </section>
  </main>
</template>

<style scoped>
.n02-page {
  max-width: 980px;
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
}
.head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.meta p {
  margin: 5px 0;
  color: #374151;
}
.grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}
.identity-card {
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  padding: 12px;
}
.identity-card.active {
  border-color: #f57c00;
  box-shadow: 0 0 0 1px #f57c0033 inset;
}
.identity-card header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
}
.identity-card h3 {
  margin: 0;
}
.tag {
  font-size: 12px;
  color: #f57c00;
  border: 1px solid #f57c0038;
  background: #fffaf4;
  padding: 2px 8px;
  border-radius: 999px;
}
.desc {
  color: #6b7280;
  min-height: 40px;
}
.actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.btn {
  border: 1px solid #ddd;
  border-radius: 8px;
  background: #fff;
  padding: 8px 12px;
  cursor: pointer;
}
.btn--primary {
  border-color: #f57c00;
  background: #f57c00;
  color: #fff;
}
.error {
  color: #b42318;
}
.ok {
  color: #0c7a43;
}
.tip {
  color: #6b7280;
}
@media (max-width: 960px) {
  .grid {
    grid-template-columns: 1fr;
  }
}
</style>
