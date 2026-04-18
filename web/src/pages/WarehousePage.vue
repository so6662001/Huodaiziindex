<script setup>
import { onMounted } from 'vue'

const filters = {
  cities: ['全部城市', '唐山', '天津', '无锡', '佛山', '武汉'],
  warehouseTypes: ['全部库型', '室内库', '露天场', '综合库'],
  categories: ['全部品类', '螺纹钢', '热卷', '中厚板', '型钢', '管材'],
  lifting: ['全部能力', '10吨以下', '10-20吨', '20吨以上'],
  prices: ['全部价格', '0.8元/吨/天以下', '0.8-1.0元/吨/天', '1.0元/吨/天以上']
}

const warehouses = [
  {
    id: 'W20260418001',
    name: '唐山海港仓储中心',
    city: '唐山',
    type: '室内库',
    capacity: '50,000吨',
    throughput: '日吞吐1,200吨',
    capability: '20吨行车 / 夜间作业',
    categories: ['螺纹钢', '型钢', '热卷'],
    price: '0.9元/吨/天'
  },
  {
    id: 'W20260418002',
    name: '无锡城南钢材仓',
    city: '无锡',
    type: '露天场',
    capacity: '32,000吨',
    throughput: '日吞吐800吨',
    capability: '15吨行车 / 分拣服务',
    categories: ['热卷', '中厚板'],
    price: '0.8元/吨/天'
  },
  {
    id: 'W20260418003',
    name: '佛山顺德物流仓',
    city: '佛山',
    type: '综合库',
    capacity: '26,000吨',
    throughput: '日吞吐620吨',
    capability: '10吨行车 / 临港短驳',
    categories: ['镀锌卷', '管材', '型钢'],
    price: '1.1元/吨/天'
  },
  {
    id: 'W20260418004',
    name: '武汉临港钢材库',
    city: '武汉',
    type: '室内库',
    capacity: '42,000吨',
    throughput: '日吞吐980吨',
    capability: '25吨行车 / 可分批出库',
    categories: ['螺纹钢', '中厚板', '型钢'],
    price: '1.0元/吨/天'
  }
]

onMounted(() => {
  document.title = '找仓库-钢铁仓储服务-货袋子'
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
          <RouterLink to="/site/tangshan">分站中心</RouterLink>
        </nav>
        <div class="topbar__actions">
          <button class="btn btn--ghost">登录</button>
          <button class="btn btn--primary">发布仓储需求</button>
        </div>
      </div>
    </header>

    <main class="spot-main">
      <section class="spot-hero">
        <div class="container">
          <h1>找仓库</h1>
          <p>覆盖多城市仓储资源，按库型、品类、吊装能力与价格快速筛选。</p>
        </div>
      </section>

      <section class="section">
        <div class="container">
          <div class="card warehouse-filter">
            <div class="filter-grid warehouse-filter__grid">
              <div>
                <label>城市</label>
                <select>
                  <option v-for="item in filters.cities" :key="item">{{ item }}</option>
                </select>
              </div>
              <div>
                <label>库型</label>
                <select>
                  <option v-for="item in filters.warehouseTypes" :key="item">{{ item }}</option>
                </select>
              </div>
              <div>
                <label>品类</label>
                <select>
                  <option v-for="item in filters.categories" :key="item">{{ item }}</option>
                </select>
              </div>
              <div>
                <label>吊装能力</label>
                <select>
                  <option v-for="item in filters.lifting" :key="item">{{ item }}</option>
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
                <input type="text" placeholder="仓库名/服务能力/地址" />
              </div>
            </div>
            <div class="warehouse-filter__actions">
              <button class="btn btn--ghost">重置</button>
              <button class="btn btn--primary">查询仓库</button>
            </div>
          </div>
        </div>
      </section>

      <section class="section">
        <div class="container">
          <div class="section__header">
            <h2>仓库列表</h2>
            <span>共 128 家仓库</span>
          </div>
          <div class="warehouse-list">
            <article v-for="item in warehouses" :key="item.id" class="card warehouse-card">
              <div class="warehouse-card__top">
                <h2>{{ item.name }}</h2>
                <span class="tag">{{ item.type }}</span>
              </div>
              <p class="warehouse-card__city">{{ item.city }}</p>
              <div class="warehouse-card__meta">
                <span>库容：{{ item.capacity }}</span>
                <span>{{ item.throughput }}</span>
                <span>{{ item.capability }}</span>
              </div>
              <div class="warehouse-card__tags">
                <span v-for="category in item.categories" :key="category">{{ category }}</span>
              </div>
              <div class="warehouse-card__bottom">
                <p class="spot-card__price">{{ item.price }}</p>
                <div class="spot-card__actions">
                  <button class="btn btn--ghost">查看详情</button>
                  <button class="btn btn--primary">联系TA</button>
                </div>
              </div>
            </article>
          </div>
          <div class="pagination card">
            <button class="page-btn">上一页</button>
            <button class="page-btn active">1</button>
            <button class="page-btn">2</button>
            <button class="page-btn">3</button>
            <button class="page-btn">下一页</button>
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
      <RouterLink class="active" to="/logistics">物流</RouterLink>
      <a href="#">分站</a>
      <a href="#">我的</a>
    </nav>
  </div>
</template>
