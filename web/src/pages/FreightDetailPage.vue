<script setup>
import { computed, watchEffect } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const freightDetailMap = {
  F20260418001: {
    id: 'F20260418001',
    provider: '唐山宏运车队',
    route: '唐山 → 无锡',
    vehicle: '13米平板',
    loadRange: '30-35吨',
    frequency: '每日发车',
    timeliness: '48小时',
    quote: '120元/吨起',
    serviceTags: ['可回单', '支持夜装', '北材南下稳定班次'],
    desc: '覆盖唐山至无锡主流钢材流向，支持整车与拼车，适合中短周期补库运输。'
  },
  F20260418002: {
    id: 'F20260418002',
    provider: '天津北方专线',
    route: '天津 → 佛山',
    vehicle: '17.5米平板',
    loadRange: '35-40吨',
    frequency: '隔日发车',
    timeliness: '72小时',
    quote: '165元/吨起',
    serviceTags: ['重货专线', '跨省调车', '装卸协同'],
    desc: '适配热卷、板材跨区运输，提供时效承诺与异常节点反馈。'
  },
  F20260418003: {
    id: 'F20260418003',
    provider: '武汉联速物流',
    route: '武汉 → 长沙',
    vehicle: '13米高栏',
    loadRange: '28-32吨',
    frequency: '每日发车',
    timeliness: '24小时',
    quote: '98元/吨起',
    serviceTags: ['次日达', '短驳协同', '高频发车'],
    desc: '聚焦华中区域短途周转，适合项目配送与紧急调货场景。'
  },
  F20260418004: {
    id: 'F20260418004',
    provider: '佛山华南车队',
    route: '佛山 → 广州',
    vehicle: '厢式货车',
    loadRange: '18-22吨',
    frequency: '每日多班',
    timeliness: '12小时',
    quote: '85元/吨起',
    serviceTags: ['城际快运', '可预约装车', '珠三角覆盖'],
    desc: '面向珠三角城际配送，时效稳定，支持多点卸货。'
  }
}

const fallbackId = 'F20260418001'

const currentFreight = computed(() => {
  const freightId = route.params.id
  return freightDetailMap[freightId] || freightDetailMap[fallbackId]
})

const relatedFreights = computed(() =>
  Object.values(freightDetailMap).filter((item) => item.id !== currentFreight.value.id).slice(0, 3)
)

const relatedDemands = [
  { id: 'D20260418003', title: '唐山到无锡螺纹钢运输需求，48小时内到货' },
  { id: 'D20260418002', title: '佛山终端企业短期仓储需求，需短驳协同' },
  { id: 'D20260418001', title: '唐山螺纹钢短期仓储需求，要求可夜间作业' }
]

watchEffect(() => {
  document.title = `${currentFreight.value.route}_运输专线详情-货袋子`
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
          <RouterLink class="active" to="/logistics">仓储物流</RouterLink>
          <a href="#">企业黄页</a>
          <RouterLink to="/site/tangshan">分站中心</RouterLink>
        </nav>
        <div class="topbar__actions">
          <button class="btn btn--ghost">登录</button>
          <RouterLink class="btn btn--primary btn-link" to="/logistics/freight">返回车线列表</RouterLink>
        </div>
      </div>
    </header>

    <main class="freight-detail-main">
      <section class="freight-detail-hero">
        <div class="container">
          <p class="freight-detail-breadcrumb">
            <RouterLink to="/logistics">仓储物流</RouterLink>
            <span>/</span>
            <RouterLink to="/logistics/freight">找车找线</RouterLink>
            <span>/</span>
            <span>专线详情</span>
          </p>
          <h1>{{ currentFreight.provider }}</h1>
          <p>{{ currentFreight.desc }}</p>
        </div>
      </section>

      <section class="section">
        <div class="container freight-detail-layout">
          <div>
            <article class="card freight-detail-card">
              <div class="freight-detail-card__top">
                <h2>{{ currentFreight.route }}</h2>
                <span class="tag">认证专线</span>
              </div>
              <div class="freight-detail-meta">
                <span>车型：{{ currentFreight.vehicle }}</span>
                <span>载重：{{ currentFreight.loadRange }}</span>
                <span>班次：{{ currentFreight.frequency }}</span>
                <span>时效：{{ currentFreight.timeliness }}</span>
              </div>
              <p class="freight-detail-quote">{{ currentFreight.quote }}</p>
            </article>

            <article class="card freight-detail-card">
              <div class="section__header">
                <h2>服务能力</h2>
                <span>以平台审核信息为准</span>
              </div>
              <div class="freight-detail-tags">
                <span v-for="tag in currentFreight.serviceTags" :key="tag">{{ tag }}</span>
              </div>
              <p class="freight-detail-note">
                报价会受装货地、卸货地、车型调度、发车时段等因素影响，建议在询价时补充装卸条件与到货时限。
              </p>
            </article>

            <article class="card freight-detail-card">
              <div class="section__header">
                <h2>相关推荐专线</h2>
                <RouterLink to="/logistics/freight">查看更多</RouterLink>
              </div>
              <ul class="freight-detail-list">
                <li v-for="item in relatedFreights" :key="item.id">
                  <RouterLink :to="`/logistics/freight/${item.id}`">{{ item.provider }} · {{ item.route }}</RouterLink>
                  <span>{{ item.timeliness }} · {{ item.quote }}</span>
                </li>
              </ul>
            </article>

            <article class="card freight-detail-card">
              <div class="section__header">
                <h2>相关需求</h2>
                <RouterLink to="/logistics">返回物流首页</RouterLink>
              </div>
              <ul class="freight-detail-list">
                <li v-for="item in relatedDemands" :key="item.id">
                  <RouterLink :to="`/logistics/demand/${item.id}`">{{ item.title }}</RouterLink>
                </li>
              </ul>
            </article>
          </div>

          <aside class="freight-detail-side">
            <div class="card side-card">
              <h3>联系方式</h3>
              <ul>
                <li>联系人：王**</li>
                <li>联系电话：138****7862</li>
                <li>服务状态：可接新单</li>
              </ul>
              <p class="freight-detail-side-note">登录后可查看完整联系方式并发起在线沟通。</p>
              <div class="freight-detail-side-actions">
                <button class="btn btn--ghost">在线咨询</button>
                <button class="btn btn--primary">登录后查看电话</button>
              </div>
            </div>
            <div class="card side-card">
              <p class="ad__flag">广告</p>
              <h3>车队专线推广</h3>
              <p>支持按起终点与车型精准投放，提升运输线索转化效率。</p>
              <button class="btn btn--primary">咨询投放</button>
            </div>
          </aside>
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
      <RouterLink class="active" to="/logistics">物流</RouterLink>
      <RouterLink to="/news">资讯</RouterLink>
      <a href="#">我的</a>
    </nav>
  </div>
</template>
