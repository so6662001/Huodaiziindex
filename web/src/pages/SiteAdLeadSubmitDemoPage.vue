<script setup>
import { computed, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const loading = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

const form = reactive({
  placementId: 'SA001',
  placementName: '分站首页焦点位',
  city: '唐山',
  duration: '30天',
  budget: '5000-10000',
  companyName: '',
  contactName: '',
  contactPhone: '',
  remark: '',
  agreed: false
})

const canSubmit = computed(() => {
  const mobileReg = /^1\d{10}$/
  return (
    form.companyName.trim().length >= 4 &&
    form.contactName.trim().length >= 2 &&
    mobileReg.test(form.contactPhone.trim()) &&
    form.agreed
  )
})

async function submitLead() {
  if (!canSubmit.value || loading.value) return
  loading.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const resp = await fetch('/api/v1/site-ad-lead/submit', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(form)
    })
    const json = await resp.json()
    if (json.code !== '0') {
      throw new Error(json.message || '提交失败')
    }
    successMsg.value = `${json.data.message}（单号：${json.data.leadNo}）`
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
  } finally {
    loading.value = false
  }
}

function gotoMine() {
  if (!form.contactPhone.trim()) return
  router.push(`/site/ad/mine?contactPhone=${encodeURIComponent(form.contactPhone.trim())}`)
}
</script>

<template>
  <main class="app-shell">
    <header class="page-title">
      <h1>提交投放线索（P18闭环）</h1>
      <p>提交后可在“我的投放单”查看状态流转与最新跟进。</p>
    </header>

    <section class="card">
      <div class="toolbar">
        <input v-model="form.placementId" placeholder="广告位ID，如 SA001" />
        <input v-model="form.placementName" placeholder="广告位名称" />
        <input v-model="form.city" placeholder="投放城市" />
        <input v-model="form.duration" placeholder="投放周期，如 30天" />
        <input v-model="form.budget" placeholder="预算区间，如 5000-10000" />
        <input v-model="form.companyName" placeholder="公司名称（至少4位）" />
        <input v-model="form.contactName" placeholder="联系人（至少2位）" />
        <input v-model="form.contactPhone" placeholder="联系电话（11位）" />
      </div>
      <div class="toolbar">
        <input
          v-model="form.remark"
          style="min-width: 360px"
          placeholder="投放诉求（选填）"
        />
        <label style="display: inline-flex; align-items: center; gap: 6px">
          <input v-model="form.agreed" type="checkbox" />
          同意投放协议
        </label>
      </div>
      <div class="toolbar">
        <button class="primary" :disabled="!canSubmit || loading" @click="submitLead">提交线索</button>
        <button :disabled="!form.contactPhone.trim()" @click="gotoMine">查看我的投放单</button>
      </div>
      <p v-if="successMsg" style="color: #1f7a43">{{ successMsg }}</p>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
    </section>
  </main>
</template>
