<script setup>
import { computed, ref } from 'vue'

const categoryTabs = ['螺纹钢', '热卷', '中厚板', '型钢']
const cityTabs = ['唐山', '无锡', '上海', '天津', '武汉', '佛山']

const activeCategory = ref(categoryTabs[0])
const activeCity = ref(cityTabs[0])
const activeRange = ref('7日')

const symbolMap = {
  螺纹钢: 'rebar',
  热卷: 'hrc',
  中厚板: 'plate',
  型钢: 'section'
}

const quotePanels = [
  { label: '今日参考价', value: '3,620 元/吨', trend: '+25' },
  { label: '近7日波动', value: '2.9%', trend: '-0.4%' },
  { label: '近30日区间', value: '3,510 - 3,780', trend: '+130' }
]

const marketRows = [
  {
    name: '螺纹钢 HRB400E',
    city: '唐山',
    latest: '3,620',
    high: '3,678',
    low: '3,592',
    change: '+25'
  },
  {
    name: '热卷 Q235B 4.75',
    city: '无锡',
    latest: '3,780',
    high: '3,826',
    low: '3,752',
    change: '-15'
  },
  {
    name: '中厚板 Q355B 20mm',
    city: '上海',
    latest: '3,950',
    high: '3,980',
    low: '3,916',
    change: '+10'
  },
  {
    name: 'H型钢 200*200',
    city: '天津',
    latest: '3,730',
    high: '3,748',
    low: '3,705',
    change: '+8'
  }
]

const insightItems = [
  '北方建材成交回暖，螺纹现货价格小幅走强。',
  '华东热卷维持窄幅震荡，终端按需采购为主。',
  '部分区域运输成本回落，跨区调货意愿提升。'
]

const signalItems = [
  { title: '库存信号', value: '社会库存周降 2.1%', tag: '偏多' },
  { title: '需求信号', value: '工地开工率稳步恢复', tag: '中性偏多' },
  { title: '成本信号', value: '焦炭价格趋稳', tag: '中性' }
]

const rangeButtons = ['7日', '30日', '90日']

const chartTitle = computed(() => `${activeCity.value}${activeCategory.value}价格走势（${activeRange.value}）`)
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
          <a href="#">钢铁资讯</a>
          <RouterLink to="/logistics">仓储物流</RouterLink>
          <a href="#">企业黄页</a>
          <RouterLink to="/site/tangshan">分站中心</RouterLink>
        </nav>
        <div class="topbar__actions">
          <button class="btn btn--ghost">登录</button>
          <button class="btn btn--primary">订阅行情</button>
        </div>
      </div>
    </header>

    <main class="market-main">
      <section class="market-hero">
        <div class="container">
          <h1>行情中心</h1>
          <p>按品种、城市和周期查看钢铁价格变化，辅助采购、销售与库存决策。</p>
          <div class="market-tabs">
            <button
              v-for="item in categoryTabs"
              :key="item"
              :class="['market-tab', activeCategory === item ? 'active' : '']"
              @click="activeCategory = item"
            >
              {{ item }}
            </button>
          </div>
        </div>
      </section>

      <section class="section">
        <div class="container market-layout">
          <div class="card market-board">
            <div class="market-board__header">
              <div class="market-city-tabs">
                <button
                  v-for="item in cityTabs"
                  :key="item"
                  :class="['market-city-tab', activeCity === item ? 'active' : '']"
                  @click="activeCity = item"
                >
                  {{ item }}
                </button>
              </div>
              <div class="market-range-tabs">
                <button
                  v-for="item in rangeButtons"
                  :key="item"
                  :class="['market-range-btn', activeRange === item ? 'active' : '']"
                  @click="activeRange = item"
                >
                  {{ item }}
                </button>
              </div>
            </div>

            <div class="market-quotes">
              <article v-for="item in quotePanels" :key="item.label" class="market-quote-card">
                <p>{{ item.label }}</p>
                <strong>{{ item.value }}</strong>
                <span :class="Number(item.trend.replace('%', '')) >= 0 ? 'up' : 'down'">{{ item.trend }}</span>
              </article>
            </div>

            <div class="market-chart card">
              <div class="market-chart__header">
                <h2>{{ chartTitle }}</h2>
                <span>更新时间：10:30</span>
              </div>
              <div class="market-chart__canvas" aria-label="行情走势图占位"></div>
            </div>
          </div>

          <aside class="market-side">
            <div class="card side-card">
              <h3>市场解读</h3>
              <ul>
                <li v-for="item in insightItems" :key="item">{{ item }}</li>
              </ul>
              <button class="btn btn--ghost">查看全部解读</button>
            </div>
            <div class="card side-card">
              <p class="ad__flag">广告</p>
              <h3>行情订阅服务</h3>
              <p>按品种和城市订阅每日行情，支持企业微信和短信推送。</p>
              <button class="btn btn--primary">立即开通</button>
            </div>
          </aside>
        </div>
      </section>

      <section class="section">
        <div class="container market-grid">
          <div class="card market-table-card">
            <div class="section__header">
              <h2>行情快照</h2>
              <a href="#">导出数据</a>
            </div>
            <div class="market-table-wrap">
              <table class="market-table">
                <thead>
                  <tr>
                    <th>品种</th>
                    <th>城市</th>
                    <th>最新价</th>
                    <th>高位</th>
                    <th>低位</th>
                    <th>涨跌</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="row in marketRows" :key="row.name">
                    <td>{{ row.name }}</td>
                    <td>{{ row.city }}</td>
                    <td>{{ row.latest }}</td>
                    <td>{{ row.high }}</td>
                    <td>{{ row.low }}</td>
                    <td :class="Number(row.change) >= 0 ? 'up' : 'down'">{{ row.change }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
          <div class="card market-signal">
            <div class="section__header">
              <h2>市场信号</h2>
              <a href="#">策略建议</a>
            </div>
            <ul>
              <li v-for="item in signalItems" :key="item.title">
                <h3>{{ item.title }}</h3>
                <p>{{ item.value }}</p>
                <span>{{ item.tag }}</span>
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
