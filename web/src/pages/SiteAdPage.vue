<script setup>
import { onMounted, ref } from 'vue'

const loading = ref(false)
const products = ref([])
const errorMsg = ref('')

async function fetchProducts() {
  loading.value = true
  errorMsg.value = ''
  try {
    const resp = await fetch('/api/v1/site-ad-detail/list')
    const json = await resp.json()
    if (json.code !== '0') {
      throw new Error(json.message || '加载广告位失败')
    }
    products.value = json.data || []
  } catch (error) {
    errorMsg.value = error.message || '系统繁忙，请稍后重试'
    products.value = []
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  document.title = '分站广告投放_城市分站广告位合作-货袋子'
  await fetchProducts()
})
</script>

<template>
  <main class="site-ad-page">
    <section class="hero card">
      <h1>分站广告投放</h1>
      <p>选择广告位查看详情并提交投放线索，支持按城市和目标客户精准定向。</p>
    </section>

    <section v-if="errorMsg" class="card error">{{ errorMsg }}</section>

    <section class="card">
      <div class="head">
        <h2>广告位产品</h2>
      </div>
      <p v-if="loading">加载中...</p>
      <ul v-else-if="products.length" class="grid">
        <li v-for="item in products" :key="item.id" class="card product">
          <p class="flag">广告位</p>
          <h3>{{ item.name }}</h3>
          <p>{{ item.desc }}</p>
          <strong>{{ item.price }}</strong>
          <RouterLink class="btn btn--primary" :to="`/site/ad/${item.id}`">查看详情</RouterLink>
        </li>
      </ul>
      <p v-else>暂无广告位数据</p>
    </section>
  </main>
</template>

<style scoped>
.site-ad-page {
  max-width: 1120px;
  margin: 0 auto;
  padding: 24px 16px 40px;
}
.hero {
  margin-bottom: 16px;
}
.head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.grid {
  list-style: none;
  margin: 12px 0 0;
  padding: 0;
  display: grid;
  gap: 12px;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
}
.product {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.flag {
  color: #f57c00;
  font-size: 12px;
}
.error {
  color: #b42318;
}
</style>
