<script setup>
import { onMounted } from 'vue'

const filters = {
  origins: ['全部起运地', '唐山', '天津', '武汉', '佛山', '无锡'],
  destinations: ['全部目的地', '无锡', '佛山', '广州', '南京', '长沙'],
  vehicleTypes: ['全部车型', '13米平板', '17.5米平板', '13米高栏', '厢式货车'],
  timeliness: ['全部时效', '24小时内', '48小时内', '72小时内', '72小时以上'],
  returnTruck: ['不限', '有回程车', '无回程车']
}

const freights = [
  {
    id: 'F20260418001',
    provider: '唐山宏运车队',
    route: '唐山 → 无锡',
    vehicle: '13米平板',
    loadRange: '30-35吨',
    frequency: '每日发车',
    timeliness: '48小时',
    price: '120元/吨起'
  },
  {
    id: 'F20260418002',
    provider: '天津北方专线',
    route: '天津 → 佛山',
    vehicle: '17.5米平板',
    loadRange: '35-40吨',
    frequency: '隔日发车',
    timeliness: '72小时',
    price: '165元/吨起'
  },
  {
    id: 'F20260418003',
    provider: '武汉联速物流',
    route: '武汉 → 长沙',
    vehicle: '13米高栏',
    loadRange: '28-32吨',
    frequency: '每日发车',
    timeliness: '24小时',
    price: '98元/吨起'
  },
  {
    id: 'F20260418004',
    provider: '佛山华南车队',
    route: '佛山 → 广州',
    vehicle: '厢式货车',
    loadRange: '18-22吨',
    frequency: '每日多班',
    timeliness: '12小时',
    price: '85元/吨起'
  }
]

onMounted(() => {
  document.title = '找车找线-钢铁运输服务-货袋子'
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
          <a href="#">行情中心</a>
          <a href="#">钢铁资讯</a>
          <RouterLink class="active" to="/logistics">仓储物流</RouterLink>
          <a href="#">企业黄页</a>
          <RouterLink :to="{ name: 'siteCity', params: { city: 'tangshan' } }">分站中心</RouterLink>
        </nav>
        <div class="topbar__actions">
          <button class="btn btn--ghost">登录</button>
          <button class="btn btn--primary">发布运输需求</button>
        </div>
      </div>
    </header>

    <main class="freight-main">
      <section class="freight-hero">
        <div class="container">
          <h1>找车找线</h1>
          <p>覆盖多城市车队与专线资源，按起终点、车型、时效快速匹配运输服务。</p>
        </div>
      </section>

      <section class="section">
        <div class="container">
          <div class="card freight-filter">
            <div class="freight-filter__grid">
              <div>
                <label>起运地</label>
                <select>
                  <option v-for="item in filters.origins" :key="item">{{ item }}</option>
                </select>
              </div>
              <div>
                <label>目的地</label>
                <select>
                  <option v-for="item in filters.destinations" :key="item">{{ item }}</option>
                </select>
              </div>
              <div>
                <label>车型</label>
                <select>
                  <option v-for="item in filters.vehicleTypes" :key="item">{{ item }}</option>
                </select>
              </div>
              <div>
                <label>时效</label>
                <select>
                  <option v-for="item in filters.timeliness" :key="item">{{ item }}</option>
                </select>
              </div>
              <div>
                <label>回程车</label>
                <select>
                  <option v-for="item in filters.returnTruck" :key="item">{{ item }}</option>
                </select>
              </div>
              <div>
                <label>关键词</label>
                <input type="text" placeholder="车队名/线路/车型" />
              </div>
            </div>
            <div class="freight-filter__actions">
              <button class="btn btn--ghost">重置</button>
              <button class="btn btn--primary">查询车线</button>
            </div>
          </div>
        </div>
      </section>

      <section class="section">
        <div class="container wh-layout">
          <div class="wh-list">
            <div class="wh-toolbar">
              <p>共 96 条车队与专线</p>
              <div class="wh-toolbar__sort">
                <span>排序：</span>
                <button class="active">默认</button>
                <button>报价低</button>
                <button>时效优先</button>
              </div>
            </div>

            <article v-for="item in freights" :key="item.id" class="card wh-card">
              <div class="wh-card__top">
                <h2>{{ item.provider }}</h2>
                <span class="wh-card__badge">认证专线</span>
              </div>
              <p class="wh-card__route">{{ item.route }}</p>
              <div class="wh-card__meta">
                <span>车型：{{ item.vehicle }}</span>
                <span>载重：{{ item.loadRange }}</span>
                <span>频次：{{ item.frequency }}</span>
                <span>时效：{{ item.timeliness }}</span>
              </div>
              <div class="wh-card__bottom">
                <p class="wh-card__price">{{ item.price }}</p>
                <p class="wh-card__updated">更新：10分钟前</p>
                <div class="wh-card__actions">
                  <button class="btn btn--ghost">查看详情</button>
                  <button class="btn btn--primary">联系TA</button>
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

          <aside class="wh-side">
            <div class="card side-card">
              <p class="ad__flag">广告</p>
              <h3>车队专线推广</h3>
              <p>支持按起终点与车型定向曝光，帮助承运商获得更多有效询盘。</p>
              <button class="btn btn--primary">咨询投放</button>
            </div>
            <div class="card side-card">
              <h3>筛选建议</h3>
              <ul>
                <li>1. 先选起运地和目的地</li>
                <li>2. 按车型与时效筛选候选车队</li>
                <li>3. 优先联系认证服务商</li>
              </ul>
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
      <a href="#">分站</a>
      <a href="#">我的</a>
    </nav>
  </div>
</template>
