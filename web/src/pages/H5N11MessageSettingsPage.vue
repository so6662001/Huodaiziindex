<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const loading = ref(false)
const saving = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

const settings = reactive({
  settingId: '',
  userId: '',
  contactMobileMasked: '',
  globalPushEnabled: true,
  appPushEnabled: true,
  smsPushEnabled: false,
  marketingEnabled: false,
  transactionEnabled: true,
  riskEnabled: true,
  doNotDisturbEnabled: false,
  doNotDisturbStart: '22:00',
  doNotDisturbEnd: '08:00',
  latestRemark: '',
  channel: 'H5',
  availableActions: [],
  tipText: '',
  updatedAt: ''
})

const form = reactive({
  systemNoticeEnabled: true,
  orderNoticeEnabled: true,
  financeNoticeEnabled: true,
  marketingNoticeEnabled: false,
  pushEnabled: true,
  smsEnabled: false,
  emailEnabled: true,
  doNotDisturbEnabled: false,
  quietStart: '22:00',
  quietEnd: '08:00',
  operator: 'h5-n11-message-settings-ui'
})

const token = computed(() => localStorage.getItem('H5_N01_AUTH_TOKEN') || localStorage.getItem('N01_AUTH_TOKEN') || '')
const hasSession = computed(() => token.value.length > 0)

function hydrateFormFromSettings() {
  form.systemNoticeEnabled = settings.transactionEnabled
  form.orderNoticeEnabled = settings.transactionEnabled
  form.financeNoticeEnabled = settings.riskEnabled
  form.marketingNoticeEnabled = settings.marketingEnabled
  form.pushEnabled = settings.globalPushEnabled
  form.smsEnabled = settings.smsPushEnabled
  form.emailEnabled = settings.appPushEnabled
  form.doNotDisturbEnabled = settings.doNotDisturbEnabled
  form.quietStart = settings.doNotDisturbStart || '22:00'
  form.quietEnd = settings.doNotDisturbEnd || '08:00'
}

function hydrateSettings(data) {
  settings.settingId = data.settingId || ''
  settings.userId = data.userId || ''
  settings.contactMobileMasked = data.contactMobileMasked || ''
  settings.globalPushEnabled = Boolean(data.globalPushEnabled)
  settings.appPushEnabled = Boolean(data.appPushEnabled)
  settings.smsPushEnabled = Boolean(data.smsPushEnabled)
  settings.marketingEnabled = Boolean(data.marketingEnabled)
  settings.transactionEnabled = Boolean(data.transactionEnabled)
  settings.riskEnabled = Boolean(data.riskEnabled)
  settings.doNotDisturbEnabled = Boolean(data.doNotDisturbEnabled)
  settings.doNotDisturbStart = data.doNotDisturbStart || '22:00'
  settings.doNotDisturbEnd = data.doNotDisturbEnd || '08:00'
  settings.latestRemark = data.latestRemark || ''
  settings.channel = data.channel || 'H5'
  settings.availableActions = Array.isArray(data.availableActions) ? data.availableActions : []
  settings.tipText = data.tipText || ''
  settings.updatedAt = data.updatedAt || ''
}

async function loadSettings() {
  if (!hasSession.value || loading.value) {
    if (!hasSession.value) {
      errorMsg.value = '请先登录后查看消息设置'
    }
    return
  }
  loading.value = true
  errorMsg.value = ''
  try {
    const resp = await fetch('/api/v1/auth/h5/message-settings', {
      headers: { 'X-Auth-Token': token.value }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `消息设置加载失败(${resp.status})`)
    }
    hydrateSettings(json.data || {})
    hydrateFormFromSettings()
  } catch (error) {
    errorMsg.value = error.message || '消息设置加载失败'
  } finally {
    loading.value = false
  }
}

async function submitSettings() {
  if (!hasSession.value || saving.value) return
  if (form.doNotDisturbEnabled && (!form.quietStart || !form.quietEnd)) {
    errorMsg.value = '开启免打扰时需同时设置开始与结束时间'
    return
  }
  saving.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      systemNoticeEnabled: form.systemNoticeEnabled,
      orderNoticeEnabled: form.orderNoticeEnabled,
      financeNoticeEnabled: form.financeNoticeEnabled,
      marketingNoticeEnabled: form.marketingNoticeEnabled,
      pushEnabled: form.pushEnabled,
      smsEnabled: form.smsEnabled,
      emailEnabled: form.emailEnabled,
      doNotDisturbEnabled: form.doNotDisturbEnabled,
      quietStart: form.quietStart || null,
      quietEnd: form.quietEnd || null,
      extraMutedScenes: form.marketingNoticeEnabled ? [] : ['MARKETING'],
      operator: form.operator
    }
    const resp = await fetch('/api/v1/auth/h5/message-settings', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Auth-Token': token.value
      },
      body: JSON.stringify(payload)
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `消息设置保存失败(${resp.status})`)
    }
    hydrateSettings(json.data || {})
    hydrateFormFromSettings()
    successMsg.value = '消息设置已保存'
  } catch (error) {
    errorMsg.value = error.message || '消息设置保存失败'
  } finally {
    saving.value = false
  }
}

function goH5Home() {
  router.push('/h5?city=唐山')
}

function goCreditBrief() {
  router.push('/h5/credit-brief')
}

function goQuickLogin() {
  router.push('/h5/login-quick')
}

onMounted(() => {
  loadSettings()
})
</script>

<template>
  <main class="h5-n11-page">
    <section class="card hero">
      <h1>H5-N11 消息设置页</h1>
      <p>统一配置移动端通知触达策略，支持交易通知、营销通知与免打扰时段管理。</p>
      <div class="hero-actions">
        <button class="btn" @click="goH5Home">返回H5首页</button>
        <button class="btn" @click="goCreditBrief">前往H5-N10信用分简报</button>
        <button class="btn" @click="goQuickLogin">返回H5-N01快捷登录</button>
      </div>
    </section>

    <section class="card">
      <div class="head">
        <h2>消息设置概览</h2>
        <button class="btn" :disabled="loading" @click="loadSettings">{{ loading ? '加载中...' : '刷新' }}</button>
      </div>
      <p class="tip">渠道：{{ settings.channel }} ｜ 登录手机号：{{ settings.contactMobileMasked || '-' }}</p>
      <div class="meta-grid">
        <p>设置ID：{{ settings.settingId || '-' }}</p>
        <p>用户ID：{{ settings.userId || '-' }}</p>
        <p>可用动作：{{ settings.availableActions?.join(' / ') || '-' }}</p>
        <p>更新时间：{{ settings.updatedAt || '-' }}</p>
      </div>
      <p class="tip">{{ settings.tipText || '-' }}</p>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
      <p v-if="successMsg" class="ok">{{ successMsg }}</p>
    </section>

    <section class="card">
      <h2>通知策略</h2>
      <div class="switch-grid">
        <label><input v-model="form.pushEnabled" type="checkbox" /> 全局推送开关</label>
        <label><input v-model="form.emailEnabled" type="checkbox" /> APP站内提醒</label>
        <label><input v-model="form.smsEnabled" type="checkbox" /> 短信提醒</label>
        <label><input v-model="form.marketingNoticeEnabled" type="checkbox" /> 营销通知</label>
        <label><input v-model="form.orderNoticeEnabled" type="checkbox" /> 订单通知</label>
        <label><input v-model="form.financeNoticeEnabled" type="checkbox" /> 风险/财务通知</label>
      </div>

      <h3>免打扰设置</h3>
      <div class="quiet-row">
        <label><input v-model="form.doNotDisturbEnabled" type="checkbox" /> 开启免打扰</label>
        <input v-model="form.quietStart" type="time" />
        <input v-model="form.quietEnd" type="time" />
      </div>

      <div class="actions">
        <button class="btn btn--primary" :disabled="saving || !hasSession" @click="submitSettings">
          {{ saving ? '保存中...' : '保存设置' }}
        </button>
      </div>
    </section>
  </main>
</template>

<style scoped>
.h5-n11-page {
  max-width: 760px;
  margin: 0 auto;
  padding: 12px 12px 28px;
  display: grid;
  gap: 12px;
}
.card {
  background: #fff;
  border: 1px solid #ececec;
  border-radius: 12px;
  padding: 14px;
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
  gap: 8px;
  flex-wrap: wrap;
}
.head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.meta-grid {
  margin-top: 8px;
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 6px 10px;
}
.switch-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 8px;
  margin-top: 8px;
}
.quiet-row {
  margin-top: 8px;
  display: grid;
  grid-template-columns: 1fr 130px 130px;
  gap: 8px;
  align-items: center;
}
.actions {
  margin-top: 12px;
}
.btn {
  height: 34px;
  border-radius: 8px;
  border: 1px solid #d1d5db;
  background: #fff;
  padding: 0 12px;
  cursor: pointer;
}
.btn--primary {
  border-color: #f57c00;
  background: #f57c00;
  color: #fff;
}
input[type='time'] {
  border: 1px solid #d1d5db;
  border-radius: 8px;
  padding: 0 10px;
  height: 34px;
}
label {
  display: flex;
  gap: 8px;
  align-items: center;
}
.tip {
  color: #6b7280;
}
.error {
  color: #b91c1c;
}
.ok {
  color: #166534;
}
@media (max-width: 768px) {
  .meta-grid,
  .switch-grid,
  .quiet-row {
    grid-template-columns: 1fr;
  }
}
</style>
