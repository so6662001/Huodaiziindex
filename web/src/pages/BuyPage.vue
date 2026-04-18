<script setup>
import { onMounted } from 'vue'

const filters = {
  categories: ['全部品类', '螺纹钢', '热卷', '中厚板', '型钢', '管材'],
  specs: ['全部规格', 'HRB400E', 'Q235B', 'Q355B', '10-25mm', '16-40mm'],
  cities: ['全部城市', '郑州', '南京', '武汉', '佛山', '成都'],
  arrivals: ['全部交期', '现货即提', '3天内到货', '7天内到货', '长期采购']
}

const buyItems = [
  {
    id: 'B20260418001',
    title: '求购螺纹钢 HRB400E 16-25mm',
    buyer: '郑州中原钢材采购中心',
    demand: '600吨',
    city: '郑州',
    budget: '3580元/吨',
    arrival: '3天内到货',
    updatedAt: '8分钟前'
  },
  {
    id: 'B20260418002',
    title: '求购热卷 Q235B 4.75*1500',
    buyer: '南京华东制造企业',
    demand: '420吨',
    city: '南京',
    budget: '3750元/吨',
    arrival: '7天内到货',
    updatedAt: '15分钟前'
  },
  {
    id: 'B20260418003',
    title: '求购中厚板 Q355B 20mm',
    buyer: '武汉桥梁工程项目部',
    demand: '300吨',
    city: '武汉',
    budget: '3920元/吨',
    arrival: '现货即提',
    updatedAt: '19分钟前'
  },
  {
    id: 'B20260418004',
    title: '求购镀锌卷 DX51D 1.2mm',
    buyer: '佛山家电制造工厂',
    demand: '260吨',
    city: '佛山',
    budget: '4260元/吨',
    arrival: '5天内到货',
    updatedAt: '24分钟前'
  },
  {
    id: 'B20260418005',
    title: '求购H型钢 Q235B 200*200',
    buyer: '成都基建施工单位',
    demand: '450吨',
    city: '成都',
    budget: '3850元/吨',
    arrival: '长期采购',
    updatedAt: '33分钟前'
  }
]

onMounted(() => {
  document.title = '钢铁求购大厅_采购需求查询与发布-货袋子'
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
          <RouterLink class="active" to="/buy">求购大厅</RouterLink>
          <a href="#">行情中心</a>
          <a href="#">钢铁资讯</a>
          <RouterLink to="/logistics">仓储物流</RouterLink>
          <a href="#">企业黄页</a>
          <a href="#">分站中心</a>
        </nav>
        <div class="topbar__actions">
          <button class="btn btn--ghost">登录</button>
          <button class="btn btn--primary">发布求购</button>
        </div>
      </div>
    </header>

    <main class="spot-main">
      <section class="spot-hero">
        <div class="container">
          <h1>求购大厅</h1>
          <p>实时采购需求汇聚，按品类、规格、交货地与交期筛选，快速对接优质买家。</p>
        </div>
      </section>

      <section class="section">
        <div class="container">
          <div class="card filter-panel">
            <div class="filter-row">
              <label>品类</label>
              <div class="chips">
                <button
                  v-for="(item, idx) in filters.categories"
                  :key="item"
                  :class="['chip', idx === 0 ? 'active' : '']"
                >
                  {{ item }}
                </button>
              </div>
            </div>
            <div class="filter-row">
              <label>规格</label>
              <div class="chips">
                <button
                  v-for="(item, idx) in filters.specs"
                  :key="item"
                  :class="['chip', idx === 0 ? 'active' : '']"
                >
                  {{ item }}
                </button>
              </div>
            </div>
            <div class="filter-grid">
              <div>
                <label>到货城市</label>
                <select>
                  <option v-for="item in filters.cities" :key="item">{{ item }}</option>
                </select>
              </div>
              <div>
                <label>交期</label>
                <select>
                  <option v-for="item in filters.arrivals" :key="item">{{ item }}</option>
                </select>
              </div>
              <div>
                <label>关键词</label>
                <input type="text" placeholder="品类/材质/规格/采购企业" />
              </div>
              <div class="filter-actions">
                <button class="btn btn--ghost">重置</button>
                <button class="btn btn--primary">查询求购</button>
              </div>
            </div>
          </div>
        </div>
      </section>

      <section class="section">
        <div class="container spot-layout">
          <div class="spot-list">
            <article v-for="item in buyItems" :key="item.id" class="card spot-card">
              <div class="spot-card__top">
                <h2>{{ item.title }}</h2>
                <span class="tag buy">求购</span>
              </div>
              <p class="spot-card__seller">{{ item.buyer }}</p>
              <div class="spot-card__meta">
                <span>需求：{{ item.demand }}</span>
                <span>到货地：{{ item.city }}</span>
                <span>交期：{{ item.arrival }}</span>
              </div>
              <div class="spot-card__bottom">
                <p class="spot-card__price">{{ item.budget }}</p>
                <p class="spot-card__time">更新：{{ item.updatedAt }}</p>
                <div class="spot-card__actions">
                  <button class="btn btn--ghost">收藏</button>
                  <button class="btn btn--primary">联系买家</button>
                </div>
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

          <aside class="spot-side">
            <div class="card side-card">
              <p class="ad__flag">广告</p>
              <h3>求购信息推广</h3>
              <p>支持求购需求优先曝光，面向对应城市和品类精准触达供应方。</p>
              <button class="btn btn--primary">立即咨询</button>
            </div>
            <div class="card side-card">
              <h3>发布求购建议</h3>
              <ul>
                <li>1. 明确规格与采购吨位</li>
                <li>2. 填写真实到货城市和交期</li>
                <li>3. 保持联系人可及时沟通</li>
              </ul>
              <RouterLink class="side-link" to="/">返回首页查看更多入口</RouterLink>
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
      <RouterLink class="active" to="/buy">供求</RouterLink>
      <RouterLink to="/logistics">物流</RouterLink>
      <a href="#">分站</a>
      <a href="#">我的</a>
    </nav>
  </div>
</template>
