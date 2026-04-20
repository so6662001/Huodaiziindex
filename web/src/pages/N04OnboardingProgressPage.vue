<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const loading = ref(false)
const errorMsg = ref('')
const data = ref(null)

const token = computed(() => localStorage.getItem('N01_AUTH_TOKEN') || '')
const hasSession = computed(() => token.value.length > 0)

async function loadProgress() {
  if (!hasSession.value || loading.value) {
    if (!hasSession.value) {
      errorMsg.value = '请先登录后查看入驻审核进度'
    }
    return
  }
  loading.value = true
  errorMsg.value = ''
  try {
    const resp = await fetch('/api/v1/auth/onboarding/progress', {
      headers: { 'X-Auth-Token': token.value }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `进度加载失败(${resp.status})`)
    }
    data.value = json.data || null
  } catch (error) {
    errorMsg.value = error.message || '进度加载失败'
  } finally {
    loading.value = false
  }
}

function goCertification() {
  router.push('/account/enterprise-certification')
}

function goIdentitySelect() {
  router.push('/account/identity-select')
}

function goNegotiationSession() {
  router.push('/account/negotiation-session')
}

function goOrderDetail() {
  router.push('/account/order-detail')
}

function goHome() {
  router.push('/')
}

onMounted(() => {
  loadProgress()
})
</script>

<template>
  <main class="n04-page">
    <section class="card hero">
      <h1>PC-N04 入驻审核进度</h1>
      <p>查看企业入驻审核的节点状态、处理人、预计完成时间与当前建议动作。</p>
      <div class="hero-actions">
        <button class="btn" @click="goCertification">返回企业认证</button>
        <button class="btn" @click="goNegotiationSession">进入议价会话</button>
        <button class="btn" @click="goOrderDetail">进入订单详情</button>
        <button class="btn" @click="goIdentitySelect">返回身份选择</button>
        <button class="btn" @click="goHome">返回首页</button>
      </div>
    </section>

    <section class="card">
      <div class="head">
        <h2>审核总览</h2>
        <button class="btn" :disabled="loading" @click="loadProgress">{{ loading ? '刷新中...' : '刷新进度' }}</button>
      </div>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
      <div v-if="data" class="overview">
        <p>认证单号：{{ data.certificationId || '-' }}</p>
        <p>企业名称：{{ data.companyName || '-' }}</p>
        <p>当前状态：{{ data.status }}</p>
        <p>状态文案：{{ data.statusText }}</p>
        <p>当前步骤：{{ data.currentStepName }}</p>
        <p>当前步骤编码：{{ data.currentStepCode }}</p>
        <p>进度：{{ data.progressPercent }}%</p>
        <p>预计完成：{{ data.expectedFinishAt || '-' }}</p>
        <p>更新时间：{{ data.updatedAt || '-' }}</p>
      </div>
      <p v-else class="tip">暂无进度数据</p>
    </section>

    <section class="card">
      <h2>审核节点时间线</h2>
      <p v-if="!data?.nodes?.length" class="tip">暂无节点信息</p>
      <ol v-else class="timeline">
        <li
          v-for="node in data.nodes"
          :key="node.nodeCode"
          class="timeline-item"
          :class="node.status.toLowerCase()"
        >
          <div class="dot"></div>
          <div class="body">
            <div class="line-1">
              <strong>{{ node.nodeName }}</strong>
              <span class="status">{{ node.statusText }}</span>
            </div>
            <p class="desc">{{ node.nodeDescription }}</p>
            <p class="meta">
              完成时间：{{ node.finishedAt || '-' }}
            </p>
          </div>
        </li>
      </ol>
    </section>
  </main>
</template>

<style scoped>
.n04-page {
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
  flex-wrap: wrap;
}
.head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.overview p {
  margin: 5px 0;
  color: #374151;
}
.timeline {
  list-style: none;
  margin: 0;
  padding: 0;
  display: grid;
  gap: 10px;
}
.timeline-item {
  display: grid;
  grid-template-columns: 14px 1fr;
  gap: 10px;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  padding: 10px;
}
.dot {
  width: 10px;
  height: 10px;
  border-radius: 999px;
  margin-top: 6px;
  background: #9ca3af;
}
.timeline-item.done .dot {
  background: #16a34a;
}
.timeline-item.processing .dot {
  background: #f57c00;
}
.timeline-item.todo .dot {
  background: #9ca3af;
}
.line-1 {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
}
.status {
  font-size: 12px;
  color: #6b7280;
}
.desc,
.meta {
  margin: 4px 0 0;
  color: #4b5563;
}
.btn {
  border: 1px solid #ddd;
  border-radius: 8px;
  background: #fff;
  padding: 8px 12px;
  cursor: pointer;
}
.error {
  color: #b42318;
}
.tip {
  color: #6b7280;
}
</style>
