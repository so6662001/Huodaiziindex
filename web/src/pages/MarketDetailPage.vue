<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const symbolMap = {
  rebar: '螺纹钢',
  hrc: '热卷',
  plate: '中厚板',
  section: '型钢'
}

const cityMap = {
  tangshan: '唐山',
  wuxi: '无锡',
  shanghai: '上海',
  tianjin: '天津',
  wuhan: '武汉',
  foshan: '佛山'
}

const symbolName = computed(() => symbolMap[String(route.params.symbol)] ?? '钢材品种')
const cityName = computed(() => cityMap[String(route.params.city)] ?? '重点城市')

const quoteSummary = computed(() => [
  { label: '今日参考价', value: '3,620 元/吨', trend: '+25' },
  { label: '近7日波动', value: '2.9%', trend: '-0.4%' },
  { label: '近30日区间', value: '3,510 - 3,780', trend: '+130' }
])

const relatedNews = computed(() => [
  `${cityName.value}${symbolName.value}现货价格窄幅上行，终端按需补库。`,
  `原料价格趋稳，${symbolName.value}成本端支撑增强。`,
  `${cityName.value}区域运输恢复，跨区调货活跃度提升。`
])

const relatedSupply = computed(() => [
  `${cityName.value}${symbolName.value}现货供应，库存 500 吨，支持分批提货。`,
  `${cityName.value}${symbolName.value}求购需求，采购量 300 吨，3 天内交付。`,
  `${cityName.value}${symbolName.value}仓储服务推荐，室内库与装卸能力齐备。`
])

const chartTitle = computed(() => `${cityName.value}${symbolName.value}价格走势`)
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
          <RouterLink class="active" to="/market">行情中心</RouterLink>
          <RouterLink to="/news">钢铁资讯</RouterLink>
          <RouterLink to="/logistics">仓储物流</RouterLink>
          <a href="#">企业黄页</a>
          <RouterLink to="/site/tangshan">分站中心</RouterLink>
        </nav>
        <div class="topbar__actions">
          <button class="btn btn--ghost">登录</button>
          <button class="btn btn--primary">订阅该品种</button>
        </div>
      </div>
    </header>

    <main class="market-detail-main">
      <section class="market-detail-hero">
        <div class="container">
          <h1>{{ cityName }}{{ symbolName }}行情详情</h1>
          <p>聚焦 {{ cityName }} {{ symbolName }} 价格走势、区间变化与交易机会。</p>
        </div>
      </section>

      <section class="section">
        <div class="container market-detail-layout">
          <div class="card market-detail-card">
            <div class="section__header">
              <h2>{{ chartTitle }}</h2>
              <span>更新时间：10:30</span>
            </div>
            <div class="market-detail-chart" aria-label="行情详情走势图占位">
              <strong>{{ chartTitle }}</strong>
              <p>近 30 日趋势图占位（后续接行情接口）</p>
            </div>
            <div class="market-detail-summary">
              <article v-for="item in quoteSummary" :key="item.label">
                <p>{{ item.label }}</p>
                <strong>{{ item.value }}</strong>
                <span :class="Number(item.trend.replace('%', '')) >= 0 ? 'up' : 'down'">{{ item.trend }}</span>
              </article>
            </div>
          </div>

          <aside class="market-detail-side">
            <div class="card side-card">
              <h3>操作入口</h3>
              <div class="market-detail-actions">
                <RouterLink class="btn btn--primary btn-link" to="/spot">发布相关供应</RouterLink>
                <RouterLink class="btn btn--ghost btn-link" to="/buy">发布相关求购</RouterLink>
              </div>
            </div>
            <div class="card side-card">
              <p class="ad__flag">广告</p>
              <h3>行情数据服务</h3>
              <p>支持多城市、多品种订阅，按日推送价格变化与市场解读。</p>
              <button class="btn btn--primary">立即咨询</button>
            </div>
          </aside>
        </div>
      </section>

      <section class="section">
        <div class="container market-detail-grid">
          <div class="card market-detail-list">
            <div class="section__header">
              <h2>关联资讯</h2>
              <a href="#">更多资讯</a>
            </div>
            <ul>
              <li v-for="item in relatedNews" :key="item">
                <p>{{ item }}</p>
              </li>
            </ul>
          </div>
          <div class="card market-detail-list">
            <div class="section__header">
              <h2>相关供求</h2>
              <a href="#">查看更多</a>
            </div>
            <ul>
              <li v-for="item in relatedSupply" :key="item">
                <p>{{ item }}</p>
              </li>
            </ul>
          </div>
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
      <RouterLink class="active" to="/market">行情</RouterLink>
      <a href="#">我的</a>
    </nav>
  </div>
</template>
