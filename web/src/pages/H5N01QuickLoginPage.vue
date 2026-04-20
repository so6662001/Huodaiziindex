<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const sending = ref(false)
const logging = ref(false)
const errorMsg = ref('')
const successMsg = ref('')
const session = ref(null)

const form = reactive({
  mobile: '13800138000',
  smsCode: '',
  channel: 'H5',
  operator: 'h5-n01-ui'
})

const codeState = reactive({
  maskedMobile: '',
  expireAt: '',
  remainSeconds: 0,
  hint: ''
})

const canSendCode = computed(() => /^1\d{10}$/.test(form.mobile.trim()) && !sending.value)
const canQuickLogin = computed(
  () => /^1\d{10}$/.test(form.mobile.trim()) && /^\d{6}$/.test(form.smsCode.trim()) && !logging.value
)

let timer = null

function startCountDown(seconds) {
  codeState.remainSeconds = Number(seconds || 0)
  if (timer) {
    clearInterval(timer)
    timer = null
  }
  if (codeState.remainSeconds <= 0) return
  timer = setInterval(() => {
    if (codeState.remainSeconds <= 1) {
      codeState.remainSeconds = 0
      clearInterval(timer)
      timer = null
      return
    }
    codeState.remainSeconds -= 1
  }, 1000)
}

async function sendCode() {
  if (!canSendCode.value) return
  sending.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      mobile: form.mobile.trim(),
      operator: form.operator.trim() || 'h5-n01-ui'
    }
    const resp = await fetch('/api/v1/auth/h5/send-login-code', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `发送验证码失败(${resp.status})`)
    }
    const data = json.data || {}
    codeState.maskedMobile = data.maskedMobile || ''
    codeState.expireAt = data.expireAt || ''
    codeState.hint = data.tipText || ''
    startCountDown(Number(data.ttlSeconds || 0))
    successMsg.value = data.tipText || '验证码已发送（演示环境固定验证码：123456）'
  } catch (error) {
    errorMsg.value = error.message || '发送验证码失败'
  } finally {
    sending.value = false
  }
}

async function quickLogin() {
  if (!canQuickLogin.value) return
  logging.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      mobile: form.mobile.trim(),
      smsCode: form.smsCode.trim(),
      channel: form.channel.trim() || 'H5',
      operator: form.operator.trim() || 'h5-n01-ui'
    }
    const resp = await fetch('/api/v1/auth/h5/quick-login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `快捷登录失败(${resp.status})`)
    }
    session.value = json.data || null
    if (session.value?.token) {
      localStorage.setItem('N01_AUTH_TOKEN', session.value.token)
      localStorage.setItem('H5_N01_AUTH_TOKEN', session.value.token)
    }
    successMsg.value = '快捷登录成功，正在进入H5首页'
    router.push('/h5?city=唐山')
  } catch (error) {
    errorMsg.value = error.message || '快捷登录失败'
  } finally {
    logging.value = false
  }
}

function goH5Home() {
  router.push('/h5?city=唐山')
}

function goH5IdentitySwitch() {
  router.push('/h5/identity-switch')
}

function goH5EnterpriseCertification() {
  router.push('/h5/enterprise-certification')
}

function goH5NegotiationSession() {
  router.push('/h5/negotiation-session')
}

onMounted(() => {
  const cached = localStorage.getItem('H5_N01_AUTH_TOKEN') || localStorage.getItem('N01_AUTH_TOKEN') || ''
  if (cached) {
    successMsg.value = '检测到已有本地会话，可直接进入H5首页'
  }
})
</script>

<template>
  <main class="h5-n01-page">
    <section class="card hero">
      <h1>H5-N01 快捷登录页</h1>
      <p>手机号验证码一键登录，快速进入移动端交易流程。</p>
      <div class="hero-actions">
        <button class="btn" @click="goH5Home">前往H5首页</button>
        <button class="btn" @click="goH5IdentitySwitch">前往H5-N02身份切换</button>
        <button class="btn" @click="goH5EnterpriseCertification">前往H5-N03企业认证</button>
        <button class="btn" @click="goH5NegotiationSession">前往H5-N04议价会话</button>
      </div>
    </section>

    <section class="card">
      <h2>手机号验证码登录</h2>
      <div class="form-grid">
        <label>
          手机号
          <input v-model="form.mobile" placeholder="请输入11位手机号" />
        </label>
        <label>
          验证码
          <input v-model="form.smsCode" placeholder="请输入6位验证码" />
        </label>
        <label>
          终端
          <select v-model="form.channel">
            <option value="H5">H5</option>
            <option value="APP">APP</option>
          </select>
        </label>
        <label>
          操作人
          <input v-model="form.operator" placeholder="可选" />
        </label>
      </div>

      <div class="actions">
        <button class="btn" :disabled="!canSendCode" @click="sendCode">
          {{ sending ? '发送中...' : codeState.remainSeconds > 0 ? `${codeState.remainSeconds}s后重发` : '发送验证码' }}
        </button>
        <button class="btn btn--primary" :disabled="!canQuickLogin" @click="quickLogin">
          {{ logging ? '登录中...' : '快捷登录' }}
        </button>
      </div>

      <div class="tips">
        <p>掩码手机号：{{ codeState.maskedMobile || '-' }}</p>
        <p>验证码过期：{{ codeState.expireAt || '-' }}</p>
        <p>提示：{{ codeState.hint || '演示环境验证码固定为 123456' }}</p>
      </div>

      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
      <p v-if="successMsg" class="ok">{{ successMsg }}</p>
    </section>

    <section class="card" v-if="session">
      <h2>登录会话</h2>
      <p>用户ID：{{ session.userId }}</p>
      <p>账号：{{ session.account }}</p>
      <p>角色：{{ session.role }}</p>
      <p>Token：{{ session.token }}</p>
      <p>过期时间：{{ session.tokenExpireAt }}</p>
    </section>
  </main>
</template>

<style scoped>
.h5-n01-page {
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
}
.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
}
label {
  display: grid;
  gap: 6px;
  color: #374151;
}
input,
select {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 9px 10px;
  font: inherit;
}
.actions {
  margin-top: 10px;
  display: flex;
  gap: 10px;
}
.btn {
  border: 1px solid #ddd;
  border-radius: 8px;
  background: #fff;
  padding: 8px 13px;
  cursor: pointer;
}
.btn--primary {
  border-color: #f57c00;
  background: #f57c00;
  color: #fff;
}
.tips {
  margin-top: 10px;
  color: #4b5563;
}
.tips p {
  margin: 4px 0;
}
.ok {
  color: #0c7a43;
  margin-top: 10px;
}
.error {
  color: #b42318;
  margin-top: 10px;
}
@media (max-width: 768px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
