<script setup>
import { computed, watchEffect } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const newsDetailMap = {
  N20260418001: {
    id: 'N20260418001',
    title: '多地基建项目推进，建材成交回暖',
    category: '市场',
    city: '唐山',
    publishAt: '2026-04-18 09:30',
    source: '货袋子研究院',
    tags: ['螺纹钢', '成交', '基建'],
    summary: '重点城市建材成交量环比提升，终端补库情绪恢复，交易节奏逐步加快。',
    content: [
      '本周华北、华东多个重点城市建材成交较上周明显回升，主要受基建项目开工节奏加快和终端阶段性补库推动。市场询单活跃度提升，低价资源成交占比下降。',
      '从区域表现看，唐山、天津的螺纹钢与盘螺资源流转速度较快，贸易商出货节奏趋于均衡。华东部分城市因天气改善，工地需求恢复，成交重心小幅上移。',
      '短期来看，市场仍将围绕库存变化与需求兑现展开博弈。若后续项目资金到位持续改善，现货市场有望保持偏稳偏强运行。'
    ],
    relatedIds: ['N20260418002', 'N20260418003']
  },
  N20260418002: {
    id: 'N20260418002',
    title: '钢铁行业阶段性去库存，社会库存连续下降',
    category: '库存',
    city: '无锡',
    publishAt: '2026-04-18 10:10',
    source: '货袋子数据中心',
    tags: ['库存', '去库', '社会库存'],
    summary: '主要品类社会库存连续下降，资源流转加快，市场信心边际修复。',
    content: [
      '监测数据显示，螺纹钢、热卷等主要品类社会库存连续两周下降，部分城市去库斜率扩大。贸易环节库存周转天数下降，补库策略趋于谨慎而高频。',
      '库存回落背后，一方面是终端采购节奏改善，另一方面是区域间资源调配效率提升。仓储与短驳协同的改善对资源快速出库形成支撑。',
      '若后续需求维持当前水平，预计库存仍有进一步回落空间，但节奏将受到价格波动和到货量变化影响。'
    ],
    relatedIds: ['N20260418001', 'N20260418004']
  },
  N20260418003: {
    id: 'N20260418003',
    title: '运输成本趋稳，北材南下线路运价小幅回调',
    category: '产业链',
    city: '天津',
    publishAt: '2026-04-18 10:45',
    source: '货袋子物流频道',
    tags: ['运输', '运价', '北材南下'],
    summary: '干线运价波动收窄，回程车资源恢复，跨区调货效率提升。',
    content: [
      '近期北材南下主流线路运价小幅回调，整体运输成本趋稳。随着回程车资源逐步恢复，部分热门线路的订车时效明显改善。',
      '对贸易与终端企业而言，运价波动收窄有助于提升报价稳定性，并降低跨区域调货的不确定性。线路稳定度提升也在一定程度上支持了库存优化。',
      '后续需持续关注油价波动、区域治超政策和天气因素对运输时效的影响。'
    ],
    relatedIds: ['N20260418001', 'N20260418004']
  },
  N20260418004: {
    id: 'N20260418004',
    title: '行业规范进一步完善，交易合规要求持续强化',
    category: '政策',
    city: '上海',
    publishAt: '2026-04-18 11:20',
    source: '货袋子政策频道',
    tags: ['政策', '合规', '交易规范'],
    summary: '交易流程与资质审核标准进一步明确，平台合规能力成为核心竞争点。',
    content: [
      '近期多项行业规范与指引持续完善，对交易流程、票据流转、资质核验提出更清晰要求。平台型企业的风控和合规体系建设重要性持续提升。',
      '在执行层面，建议企业重点关注合同条款标准化、对手方资质核验、履约节点留痕等关键环节，以降低交易纠纷与信用风险。',
      '中长期看，合规能力将与服务能力共同构成行业平台竞争壁垒。'
    ],
    relatedIds: ['N20260418002', 'N20260418003']
  }
}

const fallbackId = 'N20260418001'

const currentNews = computed(() => {
  const newsId = route.params.id
  return newsDetailMap[newsId] || newsDetailMap[fallbackId]
})

const relatedNews = computed(() =>
  currentNews.value.relatedIds
    .map((id) => newsDetailMap[id])
    .filter((item) => Boolean(item))
)

watchEffect(() => {
  document.title = `${currentNews.value.title}-货袋子`
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
          <RouterLink class="active" to="/news">钢铁资讯</RouterLink>
          <RouterLink to="/logistics">仓储物流</RouterLink>
          <a href="#">企业黄页</a>
          <RouterLink to="/site/tangshan">分站中心</RouterLink>
        </nav>
        <div class="topbar__actions">
          <button class="btn btn--ghost">登录</button>
          <button class="btn btn--primary">订阅资讯</button>
        </div>
      </div>
    </header>

    <main class="news-detail-main">
      <section class="news-detail-hero">
        <div class="container">
          <p class="news-detail-breadcrumb">
            <RouterLink to="/news">钢铁资讯</RouterLink>
            <span>/</span>
            <span>{{ currentNews.category }}</span>
          </p>
          <h1>{{ currentNews.title }}</h1>
          <div class="news-detail-meta">
            <span>{{ currentNews.category }}</span>
            <span>{{ currentNews.city }}</span>
            <span>{{ currentNews.publishAt }}</span>
            <span>来源：{{ currentNews.source }}</span>
          </div>
        </div>
      </section>

      <section class="section">
        <div class="container news-detail-layout">
          <div>
            <article class="card news-detail-article">
              <p class="news-detail-summary">{{ currentNews.summary }}</p>
              <p v-for="(text, idx) in currentNews.content" :key="idx" class="news-detail-paragraph">
                {{ text }}
              </p>
              <div class="news-detail-tags">
                <span v-for="tag in currentNews.tags" :key="tag"># {{ tag }}</span>
              </div>
              <div class="news-detail-actions">
                <button class="btn btn--ghost">分享文章</button>
                <RouterLink class="btn btn--primary btn-link" to="/market">查看相关行情</RouterLink>
              </div>
            </article>

            <article class="card news-related-card">
              <div class="section__header">
                <h2>相关推荐</h2>
                <RouterLink to="/news">返回资讯列表</RouterLink>
              </div>
              <ul>
                <li v-for="item in relatedNews" :key="item.id">
                  <RouterLink :to="`/news/${item.id}`">{{ item.title }}</RouterLink>
                  <span>{{ item.category }} · {{ item.publishAt }}</span>
                </li>
              </ul>
            </article>
          </div>

          <aside class="news-detail-side">
            <div class="card side-card">
              <h3>阅读提示</h3>
              <ul>
                <li>1. 关注品类与城市维度差异</li>
                <li>2. 结合库存与成交数据交叉验证</li>
                <li>3. 配合行情中心进行价格判断</li>
              </ul>
            </div>
            <div class="card side-card">
              <p class="ad__flag">广告</p>
              <h3>品牌资讯合作</h3>
              <p>支持资讯内容与城市分站联动投放，提升品牌曝光与线索获取。</p>
              <button class="btn btn--primary">咨询合作</button>
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
      <RouterLink to="/logistics">物流</RouterLink>
      <RouterLink class="active" to="/news">资讯</RouterLink>
      <a href="#">我的</a>
    </nav>
  </div>
</template>
