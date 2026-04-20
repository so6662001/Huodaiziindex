<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const loading = ref(false)
const submitting = ref(false)
const errorMsg = ref('')
const successMsg = ref('')
const detail = ref(null)

const form = reactive({
  companyName: '',
  unifiedSocialCreditCode: '',
  legalPersonName: '',
  legalPersonIdNo: '',
  contactName: '',
  contactMobile: '',
  businessLicenseUrl: '',
  legalIdFrontUrl: '',
  legalIdBackUrl: '',
  bankAccountName: '',
  bankAccountNo: '',
  bankName: '',
  province: '',
  city: '',
  address: '',
  remark: '',
  operator: 'h5-n03-ui'
})

const token = computed(() => localStorage.getItem('H5_N01_AUTH_TOKEN') || localStorage.getItem('N01_AUTH_TOKEN') || '')
const hasSession = computed(() => token.value.length > 0)
const canSubmit = computed(() => {
  return (
    form.companyName.trim() &&
    /^[0-9A-Z]{18}$/.test(form.unifiedSocialCreditCode.trim().toUpperCase()) &&
    form.legalPersonName.trim() &&
    /^(\d{15}|\d{17}[0-9Xx])$/.test(form.legalPersonIdNo.trim()) &&
    form.contactName.trim() &&
    /^1\d{10}$/.test(form.contactMobile.trim()) &&
    form.businessLicenseUrl.trim() &&
    form.legalIdFrontUrl.trim() &&
    form.legalIdBackUrl.trim() &&
    form.bankAccountName.trim() &&
    /^\d{8,30}$/.test(form.bankAccountNo.trim()) &&
    form.bankName.trim() &&
    form.province.trim() &&
    form.city.trim() &&
    form.address.trim()
  )
})

function fillForm(data) {
  if (!data) return
  form.companyName = data.companyName || ''
  form.unifiedSocialCreditCode = data.unifiedSocialCreditCode || ''
  form.legalPersonName = data.legalPersonName || ''
  form.contactName = data.contactName || ''
  form.businessLicenseUrl = data.businessLicenseUrl || ''
  form.legalIdFrontUrl = data.legalIdFrontUrl || ''
  form.legalIdBackUrl = data.legalIdBackUrl || ''
  form.bankAccountName = data.bankAccountName || ''
  form.bankName = data.bankName || ''
  form.province = data.province || ''
  form.city = data.city || ''
  form.address = data.address || ''
  form.remark = data.remark || ''
}

async function loadDetail() {
  if (!hasSession.value || loading.value) {
    if (!hasSession.value) {
      errorMsg.value = '请先登录后再提交企业认证'
    }
    return
  }
  loading.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const resp = await fetch('/api/v1/auth/h5/enterprise-certification/detail', {
      headers: { 'X-Auth-Token': token.value }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `认证详情加载失败(${resp.status})`)
    }
    detail.value = json.data || null
    fillForm(detail.value)
  } catch (error) {
    errorMsg.value = error.message || '认证详情加载失败'
  } finally {
    loading.value = false
  }
}

async function submitCertification() {
  if (!hasSession.value || !canSubmit.value || submitting.value) return
  submitting.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      companyName: form.companyName.trim(),
      unifiedSocialCreditCode: form.unifiedSocialCreditCode.trim().toUpperCase(),
      legalPersonName: form.legalPersonName.trim(),
      legalPersonIdNo: form.legalPersonIdNo.trim().toUpperCase(),
      contactName: form.contactName.trim(),
      contactMobile: form.contactMobile.trim(),
      businessLicenseUrl: form.businessLicenseUrl.trim(),
      legalIdFrontUrl: form.legalIdFrontUrl.trim(),
      legalIdBackUrl: form.legalIdBackUrl.trim(),
      bankAccountName: form.bankAccountName.trim(),
      bankAccountNo: form.bankAccountNo.trim(),
      bankName: form.bankName.trim(),
      province: form.province.trim(),
      city: form.city.trim(),
      address: form.address.trim(),
      remark: form.remark.trim() || null,
      operator: form.operator.trim() || null
    }
    const resp = await fetch('/api/v1/auth/h5/enterprise-certification/submit', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Auth-Token': token.value
      },
      body: JSON.stringify(payload)
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `企业认证提交失败(${resp.status})`)
    }
    detail.value = json.data || null
    successMsg.value = '企业认证资料已提交，当前状态：审核中'
  } catch (error) {
    errorMsg.value = error.message || '企业认证提交失败'
  } finally {
    submitting.value = false
  }
}

function goQuickLogin() {
  router.push('/h5/login-quick')
}

function goIdentitySwitch() {
  router.push('/h5/identity-switch')
}

function goH5Home() {
  router.push('/h5?city=唐山')
}

function goNegotiationSession() {
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

function goLitePay() {
  router.push('/h5/lite-pay')
}

function goCreditBrief() {
  router.push('/h5/credit-brief')
}

onMounted(() => {
  loadDetail()
})
</script>

<template>
  <main class="h5-n03-page">
    <section class="card hero">
      <h1>H5-N03 企业认证页</h1>
      <p>移动端提交企业资质信息，审核通过后可开通完整交易与履约能力。</p>
      <div class="hero-actions">
        <button class="btn" @click="goQuickLogin">返回快捷登录</button>
        <button class="btn" @click="goIdentitySwitch">前往身份切换</button>
        <button class="btn" @click="goNegotiationSession">前往H5-N04报价会话</button>
        <button class="btn" @click="goOrderDetail">前往H5-N05订单详情</button>
        <button class="btn" @click="goPickupScan">前往H5-N06扫码提货</button>
        <button class="btn" @click="goReconcileDetail">前往H5-N07对账详情</button>
        <button class="btn" @click="goAfterSaleCreate">前往H5-N08售后发起</button>
        <button class="btn" @click="goLitePay">前往H5-N09轻支付</button>
        <button class="btn" @click="goCreditBrief">前往H5-N10信用分简报</button>
        <button class="btn" @click="goH5Home">返回H5首页</button>
      </div>
    </section>

    <section class="card">
      <div class="head">
        <h2>认证状态</h2>
        <button class="btn" :disabled="loading" @click="loadDetail">{{ loading ? '刷新中...' : '刷新详情' }}</button>
      </div>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
      <p v-if="successMsg" class="ok">{{ successMsg }}</p>
      <div v-if="detail" class="status-box">
        <p>认证单号：{{ detail.certificationId || '-' }}</p>
        <p>审核状态：{{ detail.statusText || detail.status || '待提交' }}</p>
        <p>渠道：{{ detail.channel || 'H5' }}</p>
        <p>提交时间：{{ detail.submittedAt || '-' }}</p>
        <p>更新时间：{{ detail.updatedAt || '-' }}</p>
      </div>
      <p v-else class="tip">暂无认证记录</p>
    </section>

    <section class="card">
      <h2>认证资料</h2>
      <form class="form-grid" @submit.prevent="submitCertification">
        <label>
          企业名称
          <input v-model="form.companyName" placeholder="如：唐山弘达钢贸有限公司" />
        </label>
        <label>
          统一社会信用代码
          <input v-model="form.unifiedSocialCreditCode" placeholder="18位数字/大写字母" />
        </label>
        <label>
          法人姓名
          <input v-model="form.legalPersonName" placeholder="请输入法人姓名" />
        </label>
        <label>
          法人证件号
          <input v-model="form.legalPersonIdNo" placeholder="15或18位证件号" />
        </label>
        <label>
          联系人姓名
          <input v-model="form.contactName" placeholder="请输入联系人" />
        </label>
        <label>
          联系手机号
          <input v-model="form.contactMobile" placeholder="11位手机号" />
        </label>
        <label>
          营业执照URL
          <input v-model="form.businessLicenseUrl" placeholder="https://..." />
        </label>
        <label>
          法人证件正面URL
          <input v-model="form.legalIdFrontUrl" placeholder="https://..." />
        </label>
        <label>
          法人证件反面URL
          <input v-model="form.legalIdBackUrl" placeholder="https://..." />
        </label>
        <label>
          对公账户名
          <input v-model="form.bankAccountName" placeholder="与营业执照一致" />
        </label>
        <label>
          对公账号
          <input v-model="form.bankAccountNo" placeholder="8-30位银行卡号" />
        </label>
        <label>
          开户行
          <input v-model="form.bankName" placeholder="如：中国工商银行唐山分行" />
        </label>
        <label>
          省份
          <input v-model="form.province" placeholder="如：河北省" />
        </label>
        <label>
          城市
          <input v-model="form.city" placeholder="如：唐山市" />
        </label>
        <label>
          详细地址
          <input v-model="form.address" placeholder="如：路北区建设路100号" />
        </label>
        <label>
          备注（选填）
          <input v-model="form.remark" placeholder="可填写补充说明" />
        </label>
        <div class="actions">
          <button class="btn btn--primary" :disabled="!canSubmit || submitting" type="submit">
            {{ submitting ? '提交中...' : '提交企业认证' }}
          </button>
        </div>
      </form>
    </section>
  </main>
</template>

<style scoped>
.h5-n03-page {
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
  gap: 8px;
  flex-wrap: wrap;
}
.head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.status-box p {
  margin: 6px 0;
  color: #374151;
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
input {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 9px 10px;
  font: inherit;
}
.actions {
  margin-top: 4px;
  display: flex;
  gap: 10px;
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
