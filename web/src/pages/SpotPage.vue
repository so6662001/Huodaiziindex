<script setup>
import { onMounted } from 'vue'

const filters = {
  categories: ['全部品类', '螺纹钢', '热卷', '中厚板', '型钢', '管材'],
  specs: ['全部规格', 'HRB400E', 'Q235B', 'Q355B', '10-25mm', '16-40mm'],
  cities: ['全部城市', '唐山', '天津', '无锡', '佛山', '武汉'],
  prices: ['全部价格', '3000以下', '3000-3500', '3500-4000', '4000以上']
}

const spotItems = [
  {
    id: 'S20260418001',
    title: '唐山螺纹钢 HRB400E 12-25mm',
    seller: '唐山宏信钢贸',
    tonnage: '860吨',
    city: '唐山',
    price: '3620元/吨',
    delivery: '现货即提',
    updatedAt: '5分钟前'
  },
  {
    id: 'S20260418002',
    title: '无锡热卷 Q235B 4.75*1500',
    seller: '无锡金港供应链',
    tonnage: '520吨',
    city: '无锡',
    price: '3780元/吨',
    delivery: '2天内发货',
    updatedAt: '12分钟前'
  },
  {
    id: 'S20260418003',
    title: '天津中厚板 Q355B 16-40mm',
    seller: '天津北方钢材',
    tonnage: '300吨',
    city: '天津',
    price: '3950元/吨',
    delivery: '可分批提货',
    updatedAt: '18分钟前'
  },
  {
    id: 'S20260418004',
    title: '佛山镀锌卷 DX51D 1.0mm',
    seller: '佛山汇海钢铁',
    tonnage: '240吨',
    city: '佛山',
    price: '4230元/吨',
    delivery: '现货即提',
    updatedAt: '26分钟前'
  },
  {
    id: 'S20260418005',
    title: '武汉型钢 H型钢 200*200',
    seller: '武汉联盛钢贸',
    tonnage: '410吨',
    city: '武汉',
    price: '3880元/吨',
    delivery: '48小时内发货',
    updatedAt: '31分钟前'
  }
]

onMounted(() => {
  document.title = '钢铁现货大厅_供应信息查询与发布-货袋子'
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
          <RouterLink class="active" to="/spot">现货大厅</RouterLink>
          <RouterLink to="/buy">求购大厅</RouterLink>
          <a href="#">行情中心</a>
          <a href="#">钢铁资讯</a>
          <RouterLink to="/logistics">仓储物流</RouterLink>
          <a href="#">企业黄页</a>
          <RouterLink to="/site/tangshan">分站中心</RouterLink>
        </nav>
        <div class="topbar__actions">
          <button class="btn btn--ghost">登录</button>
          <button class="btn btn--primary">发布现货</button>
        </div>
      </div>
    </header>

    <main class="spot-main">
      <section class="spot-hero">
        <div class="container">
          <h1>现货大厅</h1>
          <p>海量钢材现货，按品类、规格、城市与价格快速筛选，精准对接优质卖家。</p>
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
                <label>交货城市</label>
                <select>
                  <option v-for="item in filters.cities" :key="item">{{ item }}</option>
                </select>
              </div>
              <div>
                <label>价格区间</label>
                <select>
                  <option v-for="item in filters.prices" :key="item">{{ item }}</option>
                </select>
              </div>
              <div>
                <label>关键词</label>
                <input type="text" placeholder="品类/材质/规格/企业" />
              </div>
              <div class="filter-actions">
                <button class="btn btn--ghost">重置</button>
                <button class="btn btn--primary">查询现货</button>
              </div>
            </div>
          </div>
        </div>
      </section>

      <section class="section">
        <div class="container spot-layout">
          <div class="spot-list">
            <article v-for="item in spotItems" :key="item.id" class="card spot-card">
              <div class="spot-card__top">
                <h2>{{ item.title }}</h2>
                <span class="tag">现货</span>
              </div>
              <p class="spot-card__seller">{{ item.seller }}</p>
              <div class="spot-card__meta">
                <span>库存：{{ item.tonnage }}</span>
                <span>城市：{{ item.city }}</span>
                <span>交货：{{ item.delivery }}</span>
              </div>
              <div class="spot-card__bottom">
                <p class="spot-card__price">{{ item.price }}</p>
                <p class="spot-card__time">更新：{{ item.updatedAt }}</p>
                <div class="spot-card__actions">
                  <button class="btn btn--ghost">收藏</button>
                  <button class="btn btn--primary">联系卖家</button>
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
              <h3>现货置顶推广</h3>
              <p>支持按城市和品类精准曝光，提升询盘与成交机会。</p>
              <button class="btn btn--primary">立即咨询</button>
            </div>
            <div class="card side-card">
              <h3>发布现货指南</h3>
              <ul>
                <li>1. 填写真实品类与规格</li>
                <li>2. 明确库存吨位与交货地</li>
                <li>3. 保持联系方式在线可达</li>
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
      <RouterLink class="active" to="/spot">供求</RouterLink>
      <RouterLink to="/logistics">物流</RouterLink>
      <a href="#">分站</a>
      <a href="#">我的</a>
    </nav>
  </div>
</template>
