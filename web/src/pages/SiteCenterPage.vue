<script setup>
import { onMounted } from 'vue'

const stationStats = [
  { label: '已开通城市分站', value: '42' },
  { label: '当日新增供求', value: '1,286' },
  { label: '分站入驻企业', value: '9,600+' },
  { label: '可投放广告位', value: '320' }
]

const stationGroups = [
  {
    region: '华北',
    desc: '建材与型钢流通活跃，港口与园区联动强。',
    cities: [
      { name: '唐山', slug: 'tangshan', focus: '螺纹钢 / 型钢', update: '今日新增 126 条供求' },
      { name: '天津', slug: 'tianjin', focus: '热卷 / 板材', update: '今日新增 92 条供求' },
      { name: '邯郸', slug: 'handan', focus: '中厚板 / 螺纹钢', update: '今日新增 81 条供求' }
    ]
  },
  {
    region: '华东',
    desc: '终端加工与贸易密度高，仓配需求集中。',
    cities: [
      { name: '无锡', slug: 'wuxi', focus: '热卷 / 中厚板', update: '今日新增 138 条供求' },
      { name: '南京', slug: 'nanjing', focus: '建材 / 型钢', update: '今日新增 74 条供求' },
      { name: '上海', slug: 'shanghai', focus: '板材 / 管材', update: '今日新增 67 条供求' }
    ]
  },
  {
    region: '华中华南',
    desc: '项目需求与区域配送协同明显，物流线路密集。',
    cities: [
      { name: '武汉', slug: 'wuhan', focus: '螺纹钢 / 中厚板', update: '今日新增 96 条供求' },
      { name: '佛山', slug: 'foshan', focus: '镀锌卷 / 管材', update: '今日新增 118 条供求' },
      { name: '郑州', slug: 'zhengzhou', focus: '建材 / 型钢', update: '今日新增 83 条供求' }
    ]
  }
]

const adProducts = [
  {
    title: '分站首页焦点位',
    desc: '适合品牌曝光与招商推广，支持按城市、品类定向。',
    price: '3000元/月起'
  },
  {
    title: '分站信息流广告位',
    desc: '融入供求与资讯流，提升点击与询盘转化。',
    price: '120元/天起'
  },
  {
    title: '仓储物流推荐位',
    desc: '面向仓库与车队专线，按线路与车型精准触达。',
    price: '1800元/月起'
  }
]

onMounted(() => {
  document.title = '城市分站中心_钢铁交易分站导航与广告投放-货袋子'
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
          <RouterLink class="active" to="/site">分站中心</RouterLink>
        </nav>
        <div class="topbar__actions">
          <button class="btn btn--ghost">登录</button>
          <button class="btn btn--primary">申请开通分站</button>
        </div>
      </div>
    </header>

    <main class="site-center-main">
      <section class="site-center-hero">
        <div class="container">
          <h1>城市分站中心</h1>
          <p>聚合全国钢铁城市分站，支持按区域快速进入本地供求、行情与仓储物流频道。</p>
        </div>
      </section>

      <section class="section">
        <div class="container site-center-stats">
          <article v-for="item in stationStats" :key="item.label" class="card site-center-stat">
            <p>{{ item.label }}</p>
            <strong>{{ item.value }}</strong>
          </article>
        </div>
      </section>

      <section class="section">
        <div class="container site-center-grid">
          <article v-for="group in stationGroups" :key="group.region" class="card site-center-region">
            <div class="section__header">
              <h2>{{ group.region }}分站</h2>
              <span>{{ group.desc }}</span>
            </div>
            <div class="site-center-city-list">
              <article v-for="city in group.cities" :key="city.slug" class="site-center-city">
                <h3>{{ city.name }}分站</h3>
                <p>{{ city.focus }}</p>
                <span>{{ city.update }}</span>
                <RouterLink class="btn btn--ghost btn-link" :to="`/site/${city.slug}`">进入分站</RouterLink>
              </article>
            </div>
          </article>
        </div>
      </section>

      <section class="section">
        <div class="container site-center-services">
          <article v-for="item in adProducts" :key="item.title" class="card site-center-service">
            <p class="ad__flag">广告</p>
            <h3>{{ item.title }}</h3>
            <p>{{ item.desc }}</p>
            <strong>{{ item.price }}</strong>
            <button class="btn btn--primary">咨询投放</button>
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
      <RouterLink class="active" to="/site">分站</RouterLink>
      <a href="#">我的</a>
    </nav>
  </div>
</template>
