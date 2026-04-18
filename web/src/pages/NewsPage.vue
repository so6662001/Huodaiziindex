<script setup>
import { ref } from 'vue'

const categoryTabs = ['全部', '政策', '市场', '库存', '产业链']
const activeTab = ref(categoryTabs[0])

const newsItems = [
  {
    id: 'N20260418001',
    title: '多地基建项目推进，建材成交回暖',
    summary: '本周重点城市建材成交量回升，终端补库意愿增强，市场情绪偏稳。',
    category: '市场',
    city: '唐山',
    publishAt: '2026-04-18 09:30'
  },
  {
    id: 'N20260418002',
    title: '钢铁行业阶段性去库存，社会库存连续下降',
    summary: '主要品类社会库存已连续两周下行，部分区域资源流转速度明显提升。',
    category: '库存',
    city: '无锡',
    publishAt: '2026-04-18 10:10'
  },
  {
    id: 'N20260418003',
    title: '运输成本趋稳，北材南下线路运价小幅回调',
    summary: '干线运价波动收窄，部分热门线路出现回程车资源恢复，跨区调货效率提高。',
    category: '产业链',
    city: '天津',
    publishAt: '2026-04-18 10:45'
  },
  {
    id: 'N20260418004',
    title: '行业规范进一步完善，交易合规要求持续强化',
    summary: '相关政策持续明确交易流程与资质要求，平台型企业合规能力成为核心竞争力。',
    category: '政策',
    city: '上海',
    publishAt: '2026-04-18 11:20'
  }
]

const hotItems = [
  '螺纹钢价格震荡走强，终端采购节奏加快',
  '热卷现货流通改善，华东成交稳中有增',
  '仓储物流需求活跃，短驳与专线协同增强',
  '库存边际下降，贸易商挺价意愿提升'
]
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

    <main class="news-main">
      <section class="news-hero">
        <div class="container">
          <h1>钢铁资讯</h1>
          <p>聚合政策、市场、库存与产业链动态，帮助企业把握行情节奏与经营机会。</p>
          <div class="news-tabs">
            <button
              v-for="item in categoryTabs"
              :key="item"
              :class="['news-tab', activeTab === item ? 'active' : '']"
              @click="activeTab = item"
            >
              {{ item }}
            </button>
          </div>
        </div>
      </section>

      <section class="section">
        <div class="container news-layout">
          <div>
            <article v-for="item in newsItems" :key="item.id" class="card news-card">
              <p class="news-card__meta">{{ item.category }} · {{ item.city }} · {{ item.publishAt }}</p>
              <h2>{{ item.title }}</h2>
              <p class="news-card__summary">{{ item.summary }}</p>
              <div class="news-card__actions">
                <button class="btn btn--ghost">查看详情</button>
                <RouterLink class="btn btn--primary btn-link" to="/market">查看相关行情</RouterLink>
              </div>
            </article>

            <div class="pagination card">
              <button class="page-btn">上一页</button>
              <button class="page-btn active">1</button>
              <button class="page-btn">2</button>
              <button class="page-btn">3</button>
              <button class="page-btn">下一页</button>
            </div>
          </div>

          <aside class="news-side">
            <div class="card side-card">
              <h3>热门阅读</h3>
              <ul>
                <li v-for="item in hotItems" :key="item">{{ item }}</li>
              </ul>
            </div>
            <div class="card side-card">
              <p class="ad__flag">广告</p>
              <h3>品牌资讯合作</h3>
              <p>支持城市与品类定向曝光，帮助企业提升行业影响力与线索转化。</p>
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
