<script setup>
import { onMounted } from 'vue'

const quoteItems = [
  { name: '螺纹钢', city: '唐山', price: '3,620', trend: '+25' },
  { name: '热轧卷板', city: '无锡', price: '3,780', trend: '-15' },
  { name: '中厚板', city: '上海', price: '3,950', trend: '+10' },
  { name: '型钢', city: '天津', price: '3,730', trend: '+8' }
]

const spotItems = [
  { title: '唐山螺纹钢HRB400E 12-25', tonnage: '860吨', location: '唐山', updatedAt: '5分钟前' },
  { title: '无锡热卷Q235B 3.0*1250', tonnage: '520吨', location: '无锡', updatedAt: '12分钟前' },
  { title: '天津中厚板Q355B 16-40', tonnage: '300吨', location: '天津', updatedAt: '18分钟前' }
]

const buyItems = [
  { title: '求购螺纹钢HRB400E 16-25', demand: '600吨', location: '郑州', updatedAt: '8分钟前' },
  { title: '求购热卷Q235B 4.75*1500', demand: '420吨', location: '南京', updatedAt: '15分钟前' },
  { title: '求购工字钢Q235B 20#', demand: '260吨', location: '武汉', updatedAt: '20分钟前' }
]

const warehouseItems = [
  { name: '唐山海港仓储中心', feature: '室内库 / 20吨行车', price: '0.9元/吨/天' },
  { name: '无锡城南钢材仓', feature: '露天场 / 可夜间作业', price: '0.8元/吨/天' }
]

const freightItems = [
  { route: '唐山 → 无锡', vehicle: '13米平板', price: '120元/吨起' },
  { route: '天津 → 佛山', vehicle: '17.5米平板', price: '165元/吨起' }
]

const stationItems = ['唐山分站', '邯郸分站', '无锡分站', '佛山分站', '武汉分站', '成都分站']

const newsItems = [
  '钢材库存连续两周下降，建材成交回暖',
  '华东热卷报价小幅震荡，终端按需采购',
  '物流运价趋稳，北材南下线路需求增长'
]

onMounted(() => {
  document.title = '钢铁现货交易与仓储物流信息平台-货袋子'
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
          <RouterLink class="active" to="/">首页</RouterLink>
          <RouterLink to="/spot">现货大厅</RouterLink>
          <RouterLink to="/buy">求购大厅</RouterLink>
          <a href="#">行情中心</a>
          <a href="#">钢铁资讯</a>
          <RouterLink to="/logistics">仓储物流</RouterLink>
          <a href="#">企业黄页</a>
          <a href="#">分站中心</a>
        </nav>
        <div class="topbar__actions">
          <button class="btn btn--ghost">登录</button>
          <button class="btn btn--primary">免费发布</button>
        </div>
      </div>
    </header>

    <main>
      <section class="hero">
        <div class="container hero__inner">
          <div class="hero__text">
            <h1>钢铁交易，一站直达</h1>
            <p>覆盖现货、求购、行情、仓储物流与城市分站，帮助钢贸企业高效获客与撮合交易。</p>
            <div class="hero__quick">
              <button class="btn btn--primary">发布供应</button>
              <button class="btn btn--ghost">发布求购</button>
              <RouterLink class="btn btn--ghost" to="/logistics">发布物流需求</RouterLink>
            </div>
          </div>
          <div class="hero__search card">
            <h2>一键检索供求与服务</h2>
            <div class="search__row">
              <select>
                <option>全部城市</option>
                <option>唐山</option>
                <option>无锡</option>
                <option>佛山</option>
              </select>
              <input type="text" placeholder="请输入品类、规格、企业或城市" />
              <button class="btn btn--primary">搜索</button>
            </div>
            <p class="search__tips">热门搜索：螺纹钢 | 热卷 | 中厚板 | 唐山到无锡专线</p>
          </div>
        </div>
      </section>

      <section class="section">
        <div class="container">
          <div class="section__header">
            <h2>今日行情速览</h2>
            <span>更新时间：10:30</span>
          </div>
          <div class="quote-grid">
            <article v-for="item in quoteItems" :key="item.name" class="card quote">
              <p class="quote__name">{{ item.name }} · {{ item.city }}</p>
              <p class="quote__price">{{ item.price }} 元/吨</p>
              <p :class="['quote__trend', Number(item.trend) >= 0 ? 'up' : 'down']">
                {{ item.trend }} 元
              </p>
            </article>
          </div>
        </div>
      </section>

      <section class="section">
        <div class="container supply">
          <div class="card list-card">
            <div class="section__header">
              <h2>最新供应</h2>
              <RouterLink to="/spot">查看更多</RouterLink>
            </div>
            <ul>
              <li v-for="item in spotItems" :key="item.title">
                <p>{{ item.title }}</p>
                <span>{{ item.tonnage }} · {{ item.location }} · {{ item.updatedAt }}</span>
              </li>
            </ul>
          </div>
          <div class="card list-card">
            <div class="section__header">
              <h2>最新求购</h2>
              <RouterLink to="/buy">查看更多</RouterLink>
            </div>
            <ul>
              <li v-for="item in buyItems" :key="item.title">
                <p>{{ item.title }}</p>
                <span>{{ item.demand }} · {{ item.location }} · {{ item.updatedAt }}</span>
              </li>
            </ul>
          </div>
        </div>
      </section>

      <section class="section">
        <div class="container logistics">
          <div class="card logistics__block">
            <div class="section__header">
              <h2>仓储推荐</h2>
              <RouterLink to="/logistics">进入仓储物流</RouterLink>
            </div>
            <ul>
              <li v-for="item in warehouseItems" :key="item.name">
                <p>{{ item.name }}</p>
                <span>{{ item.feature }} · {{ item.price }}</span>
              </li>
            </ul>
          </div>
          <div class="card logistics__block">
            <div class="section__header">
              <h2>车队专线</h2>
              <a href="#">找车找线</a>
            </div>
            <ul>
              <li v-for="item in freightItems" :key="item.route">
                <p>{{ item.route }}</p>
                <span>{{ item.vehicle }} · {{ item.price }}</span>
              </li>
            </ul>
          </div>
        </div>
      </section>

      <section class="section">
        <div class="container city">
          <div class="section__header">
            <h2>热门城市分站</h2>
            <a href="#">查看全部分站</a>
          </div>
          <div class="city__tags">
            <a v-for="item in stationItems" :key="item" href="#">{{ item }}</a>
          </div>
        </div>
      </section>

      <section class="section">
        <div class="container info">
          <div class="card info__news">
            <div class="section__header">
              <h2>钢铁资讯</h2>
              <a href="#">更多资讯</a>
            </div>
            <ul>
              <li v-for="item in newsItems" :key="item">{{ item }}</li>
            </ul>
          </div>
          <aside class="card info__ad">
            <p class="ad__flag">广告</p>
            <h3>分站广告招商</h3>
            <p>支持按城市、品类、频道定向投放，帮助仓库、车队与钢贸商精准获客。</p>
            <button class="btn btn--primary">咨询投放</button>
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
      <RouterLink class="active" to="/">首页</RouterLink>
      <RouterLink to="/buy">供求</RouterLink>
      <RouterLink to="/logistics">物流</RouterLink>
      <a href="#">分站</a>
      <a href="#">我的</a>
    </nav>
  </div>
  
</template>
