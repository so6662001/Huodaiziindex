<script setup>
import { computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const demandMap = {
  D20260418001: {
    id: 'D20260418001',
    title: '唐山螺纹钢短期仓储需求',
    type: 'STORAGE',
    typeText: '仓储需求',
    cityOrRoute: '唐山',
    goods: '螺纹钢',
    tonnage: '300吨',
    timeWindow: '2天内入库',
    publishTime: '2026-04-18 10:36',
    contactNameMasked: '张**',
    contactPhoneMasked: '138****1024'
  },
  D20260418002: {
    id: 'D20260418002',
    title: '佛山终端企业短期仓储需求',
    type: 'STORAGE',
    typeText: '仓储需求',
    cityOrRoute: '佛山',
    goods: '热卷',
    tonnage: '500吨',
    timeWindow: '3天内入库',
    publishTime: '2026-04-18 11:02',
    contactNameMasked: '李**',
    contactPhoneMasked: '139****5620'
  },
  D20260418003: {
    id: 'D20260418003',
    title: '唐山到无锡螺纹钢运输需求',
    type: 'TRANSPORT',
    typeText: '运输需求',
    cityOrRoute: '唐山 → 无锡',
    goods: '螺纹钢',
    tonnage: '260吨',
    timeWindow: '48小时内到货',
    publishTime: '2026-04-18 11:30',
    contactNameMasked: '王**',
    contactPhoneMasked: '137****7834'
  }
}

const relatedProviders = [
  {
    name: '唐山海港仓储中心',
    tags: ['室内库', '20吨行车', '可夜间作业'],
    desc: '仓储能力稳定，支持分批出入库与装卸协同。'
  },
  {
    name: '无锡城南钢材仓',
    tags: ['露天场', '分拣服务', '短驳协同'],
    desc: '擅长中短期仓储与城市配送联动。'
  },
  {
    name: '津唐专线车队',
    tags: ['13米平板', '华东线路', '当日接单'],
    desc: '覆盖唐山、无锡、常州等主流线路。'
  }
]

const relatedDemands = [
  { id: 'D20260418011', title: '南京项目部求中板仓储，需分批出库' },
  { id: 'D20260418012', title: '天津到广州热卷运输需求，要求48小时到货' },
  { id: 'D20260418013', title: '武汉终端企业求型钢仓储，可夜间作业' }
]

const demand = computed(() => {
  const id = route.params.id
  return demandMap[id] || demandMap.D20260418001
})

onMounted(() => {
  document.title = `${demand.value.typeText}详情_${demand.value.cityOrRoute}_${demand.value.goods}-货袋子`
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
          <RouterLink class="btn btn--primary btn-link" to="/logistics/demand/storage/new">
            发布需求
          </RouterLink>
        </div>
      </div>
    </header>

    <main class="detail-main">
      <section class="detail-hero">
        <div class="container">
          <h1>需求详情</h1>
          <p>查看需求信息、联系入口与推荐服务商，快速推进交易对接。</p>
        </div>
      </section>

      <section class="section">
        <div class="container detail-layout">
          <div class="detail-content">
            <article class="card detail-card">
              <div class="detail-card__top">
                <h2>{{ demand.title }}</h2>
                <span class="tag" :class="demand.type === 'TRANSPORT' ? 'buy' : ''">{{ demand.typeText }}</span>
              </div>
              <div class="detail-card__meta">
                <span>地区/路线：{{ demand.cityOrRoute }}</span>
                <span>货品：{{ demand.goods }}</span>
                <span>吨位：{{ demand.tonnage }}</span>
                <span>时效要求：{{ demand.timeWindow }}</span>
                <span>发布时间：{{ demand.publishTime }}</span>
              </div>
            </article>

            <article class="card detail-card">
              <div class="section__header">
                <h2>联系方式</h2>
                <span>登录后可查看完整联系方式</span>
              </div>
              <div class="detail-contact">
                <p>联系人：{{ demand.contactNameMasked }}</p>
                <p>联系电话：{{ demand.contactPhoneMasked }}</p>
                <p class="detail-note">请核实服务商资质后再进行线下交易。</p>
              </div>
              <div class="detail-actions">
                <button class="btn btn--ghost">在线咨询</button>
                <button class="btn btn--primary">登录后查看电话</button>
              </div>
            </article>

            <article class="card detail-card">
              <div class="section__header">
                <h2>推荐服务商</h2>
                <RouterLink to="/logistics">查看更多</RouterLink>
              </div>
              <ul class="detail-provider-list">
                <li v-for="provider in relatedProviders" :key="provider.name">
                  <h3>{{ provider.name }}</h3>
                  <div class="detail-provider-tags">
                    <span v-for="tag in provider.tags" :key="tag">{{ tag }}</span>
                  </div>
                  <p>{{ provider.desc }}</p>
                  <button class="btn btn--ghost">联系TA</button>
                </li>
              </ul>
            </article>

            <article class="card detail-card">
              <div class="section__header">
                <h2>相关需求</h2>
                <RouterLink to="/logistics">返回需求列表</RouterLink>
              </div>
              <ul class="detail-related-list">
                <li v-for="item in relatedDemands" :key="item.id">
                  <RouterLink :to="`/logistics/demand/${item.id}`">{{ item.title }}</RouterLink>
                </li>
              </ul>
            </article>
          </div>

          <aside class="detail-side">
            <div class="card side-card">
              <h3>操作建议</h3>
              <ul>
                <li>1. 优先联系已认证服务商</li>
                <li>2. 沟通时确认装卸与时效细节</li>
                <li>3. 约定节点并留存沟通记录</li>
              </ul>
            </div>
            <div class="card side-card">
              <p class="ad__flag">广告</p>
              <h3>高质量线索推广</h3>
              <p>按城市和品类精准触达，提升需求对接效率。</p>
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

    <div class="detail-mobile-actions">
      <button class="btn btn--ghost">在线咨询</button>
      <button class="btn btn--primary">电话联系</button>
    </div>

    <nav class="mobile-tabs" aria-label="移动端底部导航">
      <RouterLink to="/">首页</RouterLink>
      <RouterLink to="/buy">供求</RouterLink>
      <RouterLink class="active" to="/logistics">物流</RouterLink>
      <a href="#">分站</a>
      <a href="#">我的</a>
    </nav>
  </div>
</template>
