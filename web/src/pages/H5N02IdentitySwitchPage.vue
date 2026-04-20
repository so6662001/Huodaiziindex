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
  companyName: '',
  currentIdentityCode: '',
  currentIdentityName: '',
  tokenExpireAt: '',
  identities: []
})

const token = computed(() => localStorage.getItem('H5_N01_AUTH_TOKEN') || localStorage.getItem('N01_AUTH_TOKEN') || '')
const hasSession = computed(() => token.value.length > 0)

function workbenchRoute(code) {
  if (code === 'BUYER') return '/h5/inquiry/step1?city=唐山'
  if (code === 'SUPPLIER') return '/h5/merchant/leads?merchantId=S001'
  return '/admin/dashboard/a01'
}

async function loadIdentities() {
  if (!hasSession.value || loading.value) {
    if (!hasSession.value) {
      errorMsg.value = '请先登录后再切换身份'
    }
    return
  }
  loading.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const resp = await fetch('/api/v1/identity/h5/options', {
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
    const resp = await fetch('/api/v1/identity/h5/switch', {
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
    const target = data.workbenchRoute || workbenchRoute(identityCode)
    await loadIdentities()
    router.push(target)
  } catch (error) {
    errorMsg.value = error.message || '身份切换失败'
  } finally {
    switching.value = false
  }
}

function goQuickLogin() {
  router.push('/h5/login-quick')
}

function goH5Home() {
  router.push('/h5?city=唐山')
}

function goCertification() {
  router.push('/h5/enterprise-certification')
}

function goNegotiation() {
  router.push('/h5/quote-session')
}

function goOrderDetail() {
  router.push('/h5/order-detail')
}

function goPickupScan() {
  router.push('/h5/pickup-scan')
}

function goReconcileDetail() {
  router.push('/h5/reconcile-detail')
}

function goAfterSaleCreate() {
  router.push('/h5/after-sale-create')
}

onMounted(() => {
  loadIdentities()
})
</script>

<template>
  <main class="h5-n02-page">
    <section class="card hero">
      <h1>H5-N02 身份切换页</h1>
      <p>快速在采购方、供应方、运营方之间切换，直接进入对应移动工作台。</p>
      <div class="hero-actions">
        <button class="btn" @click="goQuickLogin">返回快捷登录</button>
        <button class="btn" @click="goCertification">企业认证</button>
        <button class="btn" @click="goNegotiation">议价会话</button>
        <button class="btn" @click="goOrderDetail">订单详情</button>
        <button class="btn" @click="goPickupScan">扫码提货</button>
        <button class="btn" @click="goReconcileDetail">对账详情</button>
        <button class="btn" @click="goAfterSaleCreate">售后发起</button>
        <button class="btn" @click="goH5Home">返回H5首页</button>
      </div>
    </section>

    <section class="card">
      <div class="head">
        <h2>当前会话</h2>
        <button class="btn" :disabled="loading" @click="loadIdentities">{{ loading ? '刷新中...' : '刷新' }}</button>
      </div>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
      <p v-if="successMsg" class="ok">{{ successMsg }}</p>
      <div class="meta" v-if="state.userId">
        <p>用户ID：{{ state.userId }}</p>
        <p>账号：{{ state.account }}</p>
        <p>企业：{{ state.companyName || '-' }}</p>
        <p>当前身份：{{ state.currentIdentityName }}（{{ state.currentIdentityCode }}）</p>
        <p>Token过期：{{ state.tokenExpireAt }}</p>
      </div>
      <p v-else class="tip">请先登录后切换身份</p>
    </section>

    <section class="card">
      <h2>可切换身份</h2>
      <p v-if="loading">加载中...</p>
      <p v-else-if="!state.identities.length" class="tip">暂无可切换身份</p>
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
              :disabled="switching || !identity.enabled"
              @click="switchIdentity(identity.identityCode)"
            >
              {{ identity.selected ? '当前身份' : '切换并进入工作台' }}
            </button>
          </div>
        </article>
      </div>
    </section>
  </main>
</template>

<style scoped>
.h5-n02-page {
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
  margin-top: 10px;
  display: flex;
  gap: 10px;
}
.head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.meta p {
  margin: 6px 0;
  color: #374151;
}
.grid {
  display: grid;
  gap: 10px;
}
.identity-card {
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  padding: 10px;
}
.identity-card.active {
  border-color: #f57c00;
  box-shadow: 0 0 0 1px #f57c0033 inset;
}
.identity-card header {
  display: flex;
  justify-content: space-between;
  align-items: center;
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
}
.actions {
  display: flex;
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
</style>
