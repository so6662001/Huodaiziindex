<script setup>
import { computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const cityMap = {
  tangshan: { name: '唐山', code: 'TS' },
  handan: { name: '邯郸', code: 'HD' },
  tianjin: { name: '天津', code: 'TJ' },
  wuxi: { name: '无锡', code: 'WX' },
  nanjing: { name: '南京', code: 'NJ' },
  shanghai: { name: '上海', code: 'SH' },
  foshan: { name: '佛山', code: 'FS' },
  wuhan: { name: '武汉', code: 'WH' },
  zhengzhou: { name: '郑州', code: 'ZZ' },
  chengdu: { name: '成都', code: 'CD' }
}

const currentCity = computed(() => {
  const slug = String(route.params.city || '').toLowerCase()
  return cityMap[slug] || { name: decodeURIComponent(slug || '城市分站'), code: 'CITY' }
})

const marketItems = computed(() => [
  { name: '螺纹钢', price: '3,620', trend: '+25' },
  { name: '热卷', price: '3,780', trend: '-10' },
  { name: '中厚板', price: '3,950', trend: '+8' }
])

const localSpotItems = computed(() => [
  {
    id: `${currentCity.value.code}-S001`,
    title: `${currentCity.value.name}螺纹钢 HRB400E 12-25mm`,
    meta: '现货 420吨 · 可当天提货'
  },
  {
    id: `${currentCity.value.code}-S002`,
    title: `${currentCity.value.name}热卷 Q235B 4.75*1500`,
    meta: '现货 300吨 · 支持分批发货'
  },
  {
    id: `${currentCity.value.code}-S003`,
    title: `${currentCity.value.name}中厚板 Q355B 16-40mm`,
    meta: '现货 210吨 · 仓库直发'
  }
])

const localBuyItems = computed(() => [
  {
    id: `${currentCity.value.code}-B001`,
    title: `求购${currentCity.value.name}螺纹钢 HRB400E`,
    meta: '需求 500吨 · 3天内到货'
  },
  {
    id: `${currentCity.value.code}-B002`,
    title: `求购${currentCity.value.name}热卷 Q235B`,
    meta: '需求 260吨 · 现货优先'
  },
  {
    id: `${currentCity.value.code}-B003`,
    title: `求购${currentCity.value.name}型钢 H200`,
    meta: '需求 180吨 · 长期合作'
  }
])

const localLogistics = computed(() => [
  {
    name: `${currentCity.value.name}海港仓储中心`,
    desc: '室内库 / 20吨行车 / 日吞吐1200吨'
  },
  {
    name: `${currentCity.value.name}城南车队专线`,
    desc: `${currentCity.value.name}→无锡 / 13米平板 / 48小时`
  },
  {
    name: `${currentCity.value.name}临港物流联盟`,
    desc: '仓配一体 / 短驳+干线 / 支持夜间作业'
  }
])

const localCompanies = computed(() => [
  `${currentCity.value.name}宏信钢贸有限公司`,
  `${currentCity.value.name}联盛供应链有限公司`,
  `${currentCity.value.name}顺达仓储物流有限公司`
])

onMounted(() => {
  document.title = `${currentCity.value.name}钢铁交易信息平台_本地供求行情物流-货袋子`
})
</script>

<template>
  <div class="page">
    <header class="topbar">
      <div class="container topbar__inner">
        <RouterLink class="brand brand--link" to="/">
          <span class="brand__logo">货</span>
          <div>
            <p class="brand__name">货袋子</p>
            <p class="brand__tag">钢铁交易平台</p>
          </div>
        </RouterLink>
        <nav class="nav">
          <RouterLink to="/">首页</RouterLink>
          <RouterLink to="/spot">现货大厅</RouterLink>
          <RouterLink to="/buy">求购大厅</RouterLink>
          <RouterLink to="/market">行情中心</RouterLink>
          <RouterLink to="/news">钢铁资讯</RouterLink>
          <RouterLink to="/logistics">仓储物流</RouterLink>
          <a href="#">企业黄页</a>
          <RouterLink class="active" :to="`/site/${route.params.city || 'tangshan'}`">分站中心</RouterLink>
        </nav>
        <div class="topbar__actions">
          <button class="btn btn--ghost">登录</button>
          <button class="btn btn--primary">发布本地供求</button>
        </div>
      </div>
    </header>

    <main class="site-main">
      <section class="site-hero">
        <div class="container">
          <h1>货袋子 · {{ currentCity.name }}分站</h1>
          <p>服务{{ currentCity.name }}本地钢铁交易，聚合本地行情、供求、仓储物流与企业资源。</p>
        </div>
      </section>

      <section class="section">
        <div class="container">
          <div class="card site-market">
            <div class="section__header">
              <h2>本地行情简报</h2>
              <span>更新时间：10:30</span>
            </div>
            <div class="site-market__grid">
              <article v-for="item in marketItems" :key="item.name">
                <p>{{ item.name }}</p>
                <h3>{{ item.price }} 元/吨</h3>
                <span :class="Number(item.trend) >= 0 ? 'up' : 'down'">{{ item.trend }} 元</span>
              </article>
            </div>
          </div>
        </div>
      </section>

      <section class="section">
        <div class="container site-grid">
          <div class="card site-block">
            <div class="section__header">
              <h2>本地供应精选</h2>
              <RouterLink to="/spot">查看更多</RouterLink>
            </div>
            <ul>
              <li v-for="item in localSpotItems" :key="item.id">
                <p>{{ item.title }}</p>
                <span>{{ item.meta }}</span>
              </li>
            </ul>
          </div>
          <div class="card site-block">
            <div class="section__header">
              <h2>本地求购精选</h2>
              <RouterLink to="/buy">查看更多</RouterLink>
            </div>
            <ul>
              <li v-for="item in localBuyItems" :key="item.id">
                <p>{{ item.title }}</p>
                <span>{{ item.meta }}</span>
              </li>
            </ul>
          </div>
        </div>
      </section>

      <section class="section">
        <div class="container site-grid">
          <div class="card site-block">
            <div class="section__header">
              <h2>本地仓储物流</h2>
              <RouterLink to="/logistics">进入仓储物流</RouterLink>
            </div>
            <ul>
              <li v-for="item in localLogistics" :key="item.name">
                <p>{{ item.name }}</p>
                <span>{{ item.desc }}</span>
              </li>
            </ul>
          </div>
          <div class="card site-block">
            <div class="section__header">
              <h2>本地企业黄页</h2>
              <a href="#">查看更多</a>
            </div>
            <ul>
              <li v-for="item in localCompanies" :key="item">
                <p>{{ item }}</p>
                <span>主营：钢材贸易 / 仓储物流 / 供应链服务</span>
              </li>
            </ul>
          </div>
        </div>
      </section>

      <section class="section">
        <div class="container site-ad-grid">
          <article class="card site-ad site-ad--banner">
            <p class="ad__flag">广告</p>
            <h3>{{ currentCity.name }}分站品牌推广位</h3>
            <p>适合本地钢贸商、仓储与车队品牌曝光，支持按品类和时段定向。</p>
            <RouterLink
              class="btn btn--primary btn-link"
              :to="`/site/ad?city=${encodeURIComponent(currentCity.name)}&placement=${encodeURIComponent('分站品牌推广位')}`"
            >
              咨询投放
            </RouterLink>
          </article>
          <article class="card site-ad">
            <p class="ad__flag">广告</p>
            <h3>{{ currentCity.name }}信息流广告位</h3>
            <p>在分站供求流中精准触达本地采购与销售客户。</p>
          </article>
          <article class="card site-ad">
            <p class="ad__flag">广告</p>
            <h3>{{ currentCity.name }}仓储物流推荐位</h3>
            <p>优先展示本地仓库与专线，提升有效询盘与成交机会。</p>
          </article>
        </div>
      </section>
    </main>

    <footer class="footer">
      <div class="container footer__inner">
        <p>© 2026 货袋子 huodaizi.com</p>
        <div>
          <a href="#">用户协议</a>
          <a href="#">隐私政策</a>
          <a href="#">站点地图</a>
          <a href="#">联系我们</a>
        </div>
      </div>
    </footer>

    <nav class="mobile-tabs" aria-label="移动端底部导航">
      <RouterLink to="/">首页</RouterLink>
      <RouterLink to="/buy">供求</RouterLink>
      <RouterLink to="/logistics">物流</RouterLink>
      <RouterLink class="active" :to="`/site/${route.params.city || 'tangshan'}`">分站</RouterLink>
      <a href="#">我的</a>
    </nav>
  </div>
</template>
