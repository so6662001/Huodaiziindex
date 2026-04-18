<script setup>
import { computed, watchEffect } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const warehouseDetailMap = {
  W20260418001: {
    id: 'W20260418001',
    name: '唐山海港仓储中心',
    city: '唐山',
    type: '室内库',
    capacity: '50,000吨',
    throughput: '日吞吐1,200吨',
    capability: '20吨行车 / 夜间作业',
    quote: '0.9元/吨/天',
    address: '唐山市海港开发区港前路 88 号',
    workTime: '7x24 小时作业',
    serviceTags: ['螺纹钢', '型钢', '热卷', '可分批出库', '短驳协同'],
    desc: '靠近主干道与港区，适合钢材集散、中转与短期周转存储。'
  },
  W20260418002: {
    id: 'W20260418002',
    name: '无锡城南钢材仓',
    city: '无锡',
    type: '露天场',
    capacity: '32,000吨',
    throughput: '日吞吐800吨',
    capability: '15吨行车 / 分拣服务',
    quote: '0.8元/吨/天',
    address: '无锡市惠山区钢材物流园 3 区',
    workTime: '6:00-22:00',
    serviceTags: ['热卷', '中厚板', '分拣打包', '装卸外包'],
    desc: '面向华东市场流通，支持到货验收、分拣与批次化出库。'
  },
  W20260418003: {
    id: 'W20260418003',
    name: '佛山顺德物流仓',
    city: '佛山',
    type: '综合库',
    capacity: '26,000吨',
    throughput: '日吞吐620吨',
    capability: '10吨行车 / 临港短驳',
    quote: '1.1元/吨/天',
    address: '佛山市顺德区港前大道 66 号',
    workTime: '7:00-23:00',
    serviceTags: ['镀锌卷', '管材', '型钢', '短途配送'],
    desc: '覆盖珠三角配送网络，适合区域内高频出入库与短驳联动。'
  },
  W20260418004: {
    id: 'W20260418004',
    name: '武汉临港钢材库',
    city: '武汉',
    type: '室内库',
    capacity: '42,000吨',
    throughput: '日吞吐980吨',
    capability: '25吨行车 / 可分批出库',
    quote: '1.0元/吨/天',
    address: '武汉市青山区临港大道 18 号',
    workTime: '7x24 小时作业',
    serviceTags: ['螺纹钢', '中厚板', '型钢', '夜间作业'],
    desc: '辐射华中核心市场，支持项目制出入库与多批次分拨。'
  }
}

const fallbackId = 'W20260418001'

const currentWarehouse = computed(() => {
  const warehouseId = route.params.id
  return warehouseDetailMap[warehouseId] || warehouseDetailMap[fallbackId]
})

const relatedWarehouses = computed(() =>
  Object.values(warehouseDetailMap).filter((item) => item.id !== currentWarehouse.value.id).slice(0, 3)
)

const relatedDemands = [
  { id: 'D20260418001', title: '唐山螺纹钢短期仓储需求，需2天内入库' },
  { id: 'D20260418002', title: '佛山终端企业短期仓储需求，需夜间作业' },
  { id: 'D20260418003', title: '武汉项目部求中板仓储，需分批出库' }
]

watchEffect(() => {
  document.title = `${currentWarehouse.value.name}_仓库详情-货袋子`
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
          <RouterLink class="btn btn--primary btn-link" to="/logistics/warehouse">返回仓库列表</RouterLink>
        </div>
      </div>
    </header>

    <main class="warehouse-detail-main">
      <section class="warehouse-detail-hero">
        <div class="container">
          <p class="warehouse-detail-breadcrumb">
            <RouterLink to="/logistics">仓储物流</RouterLink>
            <span>/</span>
            <RouterLink to="/logistics/warehouse">找仓库</RouterLink>
            <span>/</span>
            <span>仓库详情</span>
          </p>
          <h1>{{ currentWarehouse.name }}</h1>
          <p>{{ currentWarehouse.desc }}</p>
        </div>
      </section>

      <section class="section">
        <div class="container warehouse-detail-layout">
          <div>
            <article class="card warehouse-detail-card">
              <div class="warehouse-detail-card__top">
                <h2>{{ currentWarehouse.city }} · {{ currentWarehouse.type }}</h2>
                <span class="tag">认证仓库</span>
              </div>
              <div class="warehouse-detail-meta">
                <span>库容：{{ currentWarehouse.capacity }}</span>
                <span>{{ currentWarehouse.throughput }}</span>
                <span>{{ currentWarehouse.capability }}</span>
                <span>作业时间：{{ currentWarehouse.workTime }}</span>
              </div>
              <p class="warehouse-detail-quote">{{ currentWarehouse.quote }}</p>
              <p class="warehouse-detail-address">仓库地址：{{ currentWarehouse.address }}</p>
            </article>

            <article class="card warehouse-detail-card">
              <div class="section__header">
                <h2>服务能力</h2>
                <span>以平台审核信息为准</span>
              </div>
              <div class="warehouse-detail-tags">
                <span v-for="tag in currentWarehouse.serviceTags" :key="tag">{{ tag }}</span>
              </div>
              <p class="warehouse-detail-note">
                仓储费用会受作业时段、装卸方式、货品形态与存储周期影响，建议联系时补充入库计划与出库节奏。
              </p>
            </article>

            <article class="card warehouse-detail-card">
              <div class="section__header">
                <h2>相关推荐仓库</h2>
                <RouterLink to="/logistics/warehouse">查看更多</RouterLink>
              </div>
              <ul class="warehouse-detail-list">
                <li v-for="item in relatedWarehouses" :key="item.id">
                  <RouterLink :to="`/logistics/warehouse/${item.id}`">{{ item.name }} · {{ item.city }}</RouterLink>
                  <span>{{ item.quote }} · {{ item.type }}</span>
                </li>
              </ul>
            </article>

            <article class="card warehouse-detail-card">
              <div class="section__header">
                <h2>相关需求</h2>
                <RouterLink to="/logistics">返回物流首页</RouterLink>
              </div>
              <ul class="warehouse-detail-list">
                <li v-for="item in relatedDemands" :key="item.id">
                  <RouterLink :to="`/logistics/demand/${item.id}`">{{ item.title }}</RouterLink>
                </li>
              </ul>
            </article>
          </div>

          <aside class="warehouse-detail-side">
            <div class="card side-card">
              <h3>联系方式</h3>
              <ul>
                <li>联系人：张**</li>
                <li>联系电话：139****4832</li>
                <li>服务状态：可接新单</li>
              </ul>
              <p class="warehouse-detail-side-note">登录后可查看完整联系方式并发起在线沟通。</p>
              <div class="warehouse-detail-side-actions">
                <button class="btn btn--ghost">在线咨询</button>
                <button class="btn btn--primary">登录后查看电话</button>
              </div>
            </div>
            <div class="card side-card">
              <p class="ad__flag">广告</p>
              <h3>仓储服务推广</h3>
              <p>支持按城市、品类和库型精准投放，提升仓储线索对接效率。</p>
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

    <nav class="mobile-tabs" aria-label="移动端底部导航">
      <RouterLink to="/">首页</RouterLink>
      <RouterLink to="/buy">供求</RouterLink>
      <RouterLink class="active" to="/logistics">物流</RouterLink>
      <RouterLink to="/news">资讯</RouterLink>
      <a href="#">我的</a>
    </nav>
  </div>
</template>
