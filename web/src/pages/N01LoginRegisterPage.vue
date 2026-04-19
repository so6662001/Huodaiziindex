<script setup>
import { computed, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const mode = ref('login')
const loading = ref(false)
const errorMsg = ref('')
const successMsg = ref('')
const session = ref(null)

const loginForm = reactive({
  account: '13800138000',
  password: 'Demo@123456',
  contactMobile: '13800138000'
})

const registerForm = reactive({
  accountType: 'MOBILE',
  mobile: '',
  password: '',
  confirmPassword: '',
  smsCode: '123456',
  companyName: '',
  contactName: '',
  operator: 'pc-n01-ui'
})

const canLogin = computed(() => {
  return loginForm.account.trim() && loginForm.password.trim() && /^1\d{10}$/.test(loginForm.contactMobile.trim())
})

const canRegister = computed(() => {
  return (
    /^1\d{10}$/.test(registerForm.mobile.trim()) &&
    registerForm.password.trim().length >= 6 &&
    registerForm.confirmPassword.trim().length >= 6 &&
    registerForm.smsCode.trim().length === 6
  )
})

function switchMode(nextMode) {
  if (loading.value) return
  mode.value = nextMode
  errorMsg.value = ''
  successMsg.value = ''
}

function setSession(data) {
  session.value = data || null
  if (session.value?.token) {
    localStorage.setItem('N01_AUTH_TOKEN', session.value.token)
  }
}

async function login() {
  if (!canLogin.value || loading.value) return
  loading.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      account: loginForm.account.trim(),
      password: loginForm.password,
      contactMobile: loginForm.contactMobile.trim()
    }
    const resp = await fetch('/api/v1/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `登录失败(${resp.status})`)
    }
    setSession(json.data)
    successMsg.value = '登录成功，正在进入身份选择'
    router.push('/account/identity-select')
  } catch (error) {
    errorMsg.value = error.message || '登录失败'
  } finally {
    loading.value = false
  }
}

async function register() {
  if (!canRegister.value || loading.value) return
  loading.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      accountType: registerForm.accountType,
      mobile: registerForm.mobile.trim(),
      password: registerForm.password,
      confirmPassword: registerForm.confirmPassword,
      smsCode: registerForm.smsCode.trim(),
      companyName: registerForm.companyName.trim() || null,
      contactName: registerForm.contactName.trim() || null,
      operator: registerForm.operator.trim() || null
    }
    const resp = await fetch('/api/v1/auth/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `注册失败(${resp.status})`)
    }
    setSession(json.data)
    successMsg.value = '注册成功，正在进入身份选择'
    router.push('/account/identity-select')
  } catch (error) {
    errorMsg.value = error.message || '注册失败'
  } finally {
    loading.value = false
  }
}

async function loadSession() {
  if (loading.value) return
  const token = localStorage.getItem('N01_AUTH_TOKEN') || ''
  if (!token) {
    errorMsg.value = '暂无本地登录态'
    return
  }
  loading.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const resp = await fetch('/api/v1/auth/session', {
      headers: { 'X-Auth-Token': token }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `会话校验失败(${resp.status})`)
    }
    setSession(json.data)
    successMsg.value = '会话有效'
  } catch (error) {
    errorMsg.value = error.message || '会话校验失败'
  } finally {
    loading.value = false
  }
}

async function logout() {
  const token = localStorage.getItem('N01_AUTH_TOKEN') || ''
  if (!token || loading.value) {
    session.value = null
    localStorage.removeItem('N01_AUTH_TOKEN')
    return
  }
  loading.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const resp = await fetch('/api/v1/auth/logout', {
      method: 'POST',
      headers: { 'X-Auth-Token': token }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `退出失败(${resp.status})`)
    }
    localStorage.removeItem('N01_AUTH_TOKEN')
    session.value = null
    successMsg.value = '已退出登录'
  } catch (error) {
    errorMsg.value = error.message || '退出失败'
  } finally {
    loading.value = false
  }
}

function goHome() {
  router.push('/')
}
</script>

<template>
  <main class="n01-page">
    <section class="card hero">
      <h1>PC-N01 登录 / 注册</h1>
      <p>支持手机号注册、账号登录、会话校验与退出登录。</p>
      <div class="hero-actions">
        <button class="btn" @click="goHome">返回首页</button>
      </div>
    </section>

    <section class="card">
      <div class="tabs">
        <button class="tab" :class="{ active: mode === 'login' }" @click="switchMode('login')">登录</button>
        <button class="tab" :class="{ active: mode === 'register' }" @click="switchMode('register')">注册</button>
      </div>

      <form v-if="mode === 'login'" class="form-grid" @submit.prevent="login">
        <label>
          账号（手机号）
          <input v-model="loginForm.account" placeholder="请输入手机号" />
        </label>
        <label>
          密码
          <input v-model="loginForm.password" type="password" placeholder="请输入密码" />
        </label>
        <label>
          联系手机号
          <input v-model="loginForm.contactMobile" placeholder="11位手机号" />
        </label>
        <div class="actions">
          <button class="btn btn--primary" :disabled="!canLogin || loading" type="submit">
            {{ loading ? '登录中...' : '立即登录' }}
          </button>
        </div>
      </form>

      <form v-else class="form-grid" @submit.prevent="register">
        <label>
          账号类型
          <select v-model="registerForm.accountType">
            <option value="MOBILE">手机号账号</option>
          </select>
        </label>
        <label>
          手机号
          <input v-model="registerForm.mobile" placeholder="请输入手机号" />
        </label>
        <label>
          密码
          <input v-model="registerForm.password" type="password" placeholder="至少6位" />
        </label>
        <label>
          确认密码
          <input v-model="registerForm.confirmPassword" type="password" placeholder="再次输入密码" />
        </label>
        <label>
          短信验证码
          <input v-model="registerForm.smsCode" placeholder="6位验证码" />
        </label>
        <label>
          企业名称（选填）
          <input v-model="registerForm.companyName" placeholder="如 唐山弘达钢贸" />
        </label>
        <label>
          联系人（选填）
          <input v-model="registerForm.contactName" placeholder="如 张经理" />
        </label>
        <div class="actions">
          <button class="btn btn--primary" :disabled="!canRegister || loading" type="submit">
            {{ loading ? '注册中...' : '注册并登录' }}
          </button>
        </div>
      </form>

      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
      <p v-if="successMsg" class="ok">{{ successMsg }}</p>
    </section>

    <section class="card">
      <h2>会话管理</h2>
      <div class="actions">
        <button class="btn" :disabled="loading" @click="loadSession">校验会话</button>
        <button class="btn" :disabled="loading" @click="logout">退出登录</button>
      </div>
      <div class="session-box" v-if="session">
        <p>用户ID：{{ session.userId }}</p>
        <p>账号：{{ session.account }}</p>
        <p>角色：{{ session.role }}</p>
        <p>联系方式：{{ session.contactMobile }}</p>
        <p>Token过期时间：{{ session.tokenExpireAt }}</p>
      </div>
      <p v-else class="tip">当前无有效会话</p>
    </section>
  </main>
</template>

<style scoped>
.n01-page {
  max-width: 860px;
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
}
.tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 12px;
}
.tab {
  border: 1px solid #ddd;
  border-radius: 8px;
  background: #fff;
  padding: 8px 14px;
  cursor: pointer;
}
.tab.active {
  border-color: #f57c00;
  color: #f57c00;
  font-weight: 600;
}
.form-grid {
  display: grid;
  gap: 10px;
}
label {
  display: grid;
  gap: 6px;
  color: #374151;
}
input,
select {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 9px 10px;
  font: inherit;
}
.actions {
  display: flex;
  gap: 10px;
  align-items: center;
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
.error {
  color: #b42318;
  margin-top: 10px;
}
.ok {
  color: #0c7a43;
  margin-top: 10px;
}
.tip {
  color: #6b7280;
}
.session-box {
  margin-top: 10px;
  border: 1px dashed #f5d0a7;
  border-radius: 8px;
  background: #fffaf4;
  padding: 10px;
}
.session-box p {
  margin: 4px 0;
}
</style>
