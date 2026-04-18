<script setup>
import { onMounted } from 'vue'

const quickEntries = [
  {
    title: '发仓储需求',
    desc: '快速匹配本地仓库',
    button: '立即发布',
    to: '/logistics/demand/storage/new'
  },
  { title: '发运输需求', desc: '对接车队与专线', button: '立即发布', to: '/logistics/demand/freight/new' },
  { title: '找仓库', desc: '按城市和库型筛选', button: '立即查找', to: '/logistics/warehouse' },
  { title: '找车找线', desc: '按线路快速询价', button: '立即查找', to: '/logistics/freight' }
]

const warehouseItems = [
  {
    name: '唐山海港仓储中心',
    city: '唐山',
    feature: '室内库 / 20吨行车 / 可夜间作业',
    price: '0.9元/吨/天'
  },
  {
    name: '无锡城南钢材仓',
    city: '无锡',
    feature: '露天场 / 分拣服务 / 日吞吐800吨',
    price: '0.8元/吨/天'
  },
  {
    name: '佛山顺德物流仓',
    city: '佛山',
    feature: '室内库 / 临港短驳 / 24小时值守',
    price: '1.1元/吨/天'
  }
]

const freightItems = [
  {
    line: '唐山 → 无锡',
    vehicle: '13米平板',
    leadTime: '48小时',
    price: '120元/吨起'
  },
  {
    line: '天津 → 佛山',
    vehicle: '17.5米平板',
    leadTime: '72小时',
    price: '165元/吨起'
  },
  {
    line: '武汉 → 成都',
    vehicle: '13米高栏',
    leadTime: '36小时',
    price: '108元/吨起'
  }
]

const demandItems = {
  storage: [
    {
      id: 'SD2026041801',
      title: '郑州钢贸商求300吨室内库，需2天内入库'
    },
    {
      id: 'SD2026041802',
      title: '佛山终端企业求500吨短期仓储，可夜间作业'
    },
    {
      id: 'SD2026041803',
      title: '南京项目部求200吨中板仓储，需分批出库'
    }
  ],
  transport: [
    {
      id: 'TD2026041801',
      title: '唐山到无锡螺纹钢 260吨，13米平板'
    },
    {
      id: 'TD2026041802',
      title: '天津到广州热卷 180吨，48小时到货'
    },
    {
      id: 'TD2026041803',
      title: '武汉到长沙型钢 120吨，次日发车'
    }
  ]
}

const stationItems = ['唐山分站', '邯郸分站', '天津分站', '无锡分站', '佛山分站', '武汉分站']

onMounted(() => {
  document.title = '钢铁仓储物流_找仓库找车线与需求发布-货袋子'
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
            发布仓储需求
          </RouterLink>
        </div>
      </div>
    </header>

    <main class="log-main">
      <section class="log-hero">
        <div class="container log-hero__inner">
          <div>
            <h1>仓储物流</h1>
            <p>找仓库、找车线、发需求，一站完成钢铁仓储物流供需对接。</p>
          </div>
          <div class="card log-search">
            <h2>快速匹配服务商</h2>
            <div class="log-search__row">
              <select>
                <option>全部城市</option>
                <option>唐山</option>
                <option>无锡</option>
                <option>佛山</option>
                <option>武汉</option>
              </select>
              <select>
                <option>全部类型</option>
                <option>仓储需求</option>
                <option>运输需求</option>
                <option>仓库服务</option>
                <option>车队专线</option>
              </select>
              <input type="text" placeholder="输入仓库、路线、车型或关键词" />
              <button class="btn btn--primary">搜索</button>
            </div>
          </div>
        </div>
      </section>

      <section class="section">
        <div class="container quick-grid">
          <article v-for="item in quickEntries" :key="item.title" class="card quick-card">
            <h3>{{ item.title }}</h3>
            <p>{{ item.desc }}</p>
            <RouterLink v-if="item.to" class="btn btn--ghost btn-link" :to="item.to">
              {{ item.button }}
            </RouterLink>
            <button v-else class="btn btn--ghost">{{ item.button }}</button>
          </article>
        </div>
      </section>

      <section class="section">
        <div class="container log-layout">
          <div class="card log-block">
            <div class="section__header">
              <h2>推荐仓库</h2>
              <RouterLink to="/logistics/warehouse">查看更多</RouterLink>
            </div>
            <ul>
              <li v-for="item in warehouseItems" :key="item.name">
                <p>{{ item.name }} · {{ item.city }}</p>
                <span>{{ item.feature }} · {{ item.price }}</span>
              </li>
            </ul>
          </div>
          <div class="card log-block">
            <div class="section__header">
              <h2>推荐车队专线</h2>
              <RouterLink to="/logistics/freight">查看更多</RouterLink>
            </div>
            <ul>
              <li v-for="item in freightItems" :key="item.line">
                <p>{{ item.line }} · {{ item.vehicle }}</p>
                <span>时效：{{ item.leadTime }} · {{ item.price }}</span>
              </li>
            </ul>
          </div>
        </div>
      </section>

      <section class="section">
        <div class="container log-layout">
          <div class="card log-block">
            <div class="section__header">
              <h2>最新仓储需求</h2>
              <RouterLink to="/logistics/demand/storage/new">发布仓储需求</RouterLink>
            </div>
            <ul>
              <li v-for="item in demandItems.storage" :key="item.id">
                <RouterLink :to="`/logistics/demand/${item.id}`">
                  <p>{{ item.title }}</p>
                </RouterLink>
              </li>
            </ul>
          </div>
          <div class="card log-block">
            <div class="section__header">
              <h2>最新运输需求</h2>
              <RouterLink to="/logistics/demand/freight/new">发布运输需求</RouterLink>
            </div>
            <ul>
              <li v-for="item in demandItems.transport" :key="item.id">
                <RouterLink :to="`/logistics/demand/${item.id}`">
                  <p>{{ item.title }}</p>
                </RouterLink>
              </li>
            </ul>
          </div>
        </div>
      </section>

      <section class="section">
        <div class="container city">
          <div class="section__header">
            <h2>热门城市入口</h2>
            <a href="#">查看全部分站</a>
          </div>
          <div class="city__tags">
            <a v-for="item in stationItems" :key="item" href="#">{{ item }}</a>
          </div>
        </div>
      </section>

      <section class="section">
        <div class="container">
          <aside class="card info__ad">
            <p class="ad__flag">广告</p>
            <h3>仓储物流招商专区</h3>
            <p>支持城市、路线、车型与品类精准投放，帮助仓库与车队获取高质量线索。</p>
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
      <RouterLink to="/">首页</RouterLink>
      <RouterLink to="/buy">供求</RouterLink>
      <RouterLink class="active" to="/logistics">物流</RouterLink>
      <a href="#">分站</a>
      <a href="#">我的</a>
    </nav>
  </div>
</template>
