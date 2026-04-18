<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const cityOptions = ['全国', '唐山', '天津', '邯郸', '无锡', '南京', '上海', '佛山', '武汉', '郑州', '成都']
const placementOptions = ['分站首页焦点位', '分站信息流广告位', '分站仓储物流推荐位', '分站品牌推广位']
const durationOptions = ['7天', '15天', '30天', '90天']

const adProducts = [
  {
    name: '分站首页焦点位',
    price: '3000元/月起',
    desc: '适合品牌招商与新品推广，支持城市和品类定向。'
  },
  {
    name: '分站信息流广告位',
    price: '120元/天起',
    desc: '融入分站供求与资讯流，提升曝光与线索转化效率。'
  },
  {
    name: '分站仓储物流推荐位',
    price: '1800元/月起',
    desc: '面向仓库与车队服务商，支持线路与车型精准投放。'
  }
]

const cityFromQuery = computed(() => {
  const city = String(route.query.city || '').trim()
  if (!city) return '全国'
  return cityOptions.includes(city) ? city : '全国'
})

const placementFromQuery = computed(() => {
  const placement = String(route.query.placement || route.query.adType || route.query.slot || '').trim()
  if (!placement) return placementOptions[0]

  const slotAliasMap = {
    'logistics-banner': '分站仓储物流推荐位',
    '分站品牌专区': '分站品牌推广位'
  }
  const normalizedPlacement = slotAliasMap[placement] || placement
  return placementOptions.includes(normalizedPlacement) ? normalizedPlacement : placementOptions[0]
})

const submitted = ref(false)
const form = ref({
  city: cityFromQuery.value,
  placement: placementFromQuery.value,
  duration: durationOptions[2],
  companyName: '',
  contactName: '',
  phone: '',
  budget: '',
  remark: '',
  agreed: false
})

const canSubmit = computed(() => {
  const mobileReg = /^1\d{10}$/
  return (
    form.value.companyName.trim().length >= 4 &&
    form.value.contactName.trim().length >= 2 &&
    mobileReg.test(form.value.phone.trim()) &&
    form.value.agreed
  )
})

const submitForm = () => {
  if (!canSubmit.value) return
  submitted.value = true
}

onMounted(() => {
  document.title = '分站广告投放_城市分站广告位合作-货袋子'
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
          <RouterLink to="/logistics">仓储物流</RouterLink>
          <a href="#">企业黄页</a>
          <RouterLink class="active" to="/site">分站中心</RouterLink>
        </nav>
        <div class="topbar__actions">
          <button class="btn btn--ghost">登录</button>
          <RouterLink class="btn btn--primary btn-link" to="/site">返回分站中心</RouterLink>
        </div>
      </div>
    </header>

    <main class="site-ad-main">
      <section class="site-ad-hero">
        <div class="container">
          <h1>分站广告投放</h1>
          <p>覆盖城市分站首页、信息流与仓储物流推荐位，帮助企业精准触达本地采购与销售客户。</p>
        </div>
      </section>

      <section class="section">
        <div class="container site-ad-products">
          <article v-for="item in adProducts" :key="item.name" class="card site-ad-product">
            <p class="ad__flag">广告位</p>
            <h2>{{ item.name }}</h2>
            <p>{{ item.desc }}</p>
            <strong>{{ item.price }}</strong>
          </article>
        </div>
      </section>

      <section class="section" v-if="!submitted">
        <div class="container site-ad-layout">
          <article class="card site-ad-form">
            <div class="section__header">
              <h2>提交投放需求</h2>
              <span>1小时内商务顾问回电</span>
            </div>
            <div class="site-ad-form-grid">
              <div>
                <label>投放城市</label>
                <select v-model="form.city">
                  <option v-for="item in cityOptions" :key="item">{{ item }}</option>
                </select>
              </div>
              <div>
                <label>广告位类型</label>
                <select v-model="form.placement">
                  <option v-for="item in placementOptions" :key="item">{{ item }}</option>
                </select>
              </div>
              <div>
                <label>投放周期</label>
                <select v-model="form.duration">
                  <option v-for="item in durationOptions" :key="item">{{ item }}</option>
                </select>
              </div>
              <div>
                <label>预算区间（元）</label>
                <input v-model="form.budget" type="text" placeholder="例如：5000-10000（选填）" />
              </div>
              <div class="full">
                <label>公司名称</label>
                <input v-model="form.companyName" type="text" placeholder="请输入公司全称" />
              </div>
              <div>
                <label>联系人</label>
                <input v-model="form.contactName" type="text" placeholder="请输入联系人姓名" />
              </div>
              <div>
                <label>联系电话</label>
                <input v-model="form.phone" type="text" placeholder="请输入11位手机号" />
              </div>
              <div class="full">
                <label>投放诉求</label>
                <textarea
                  v-model="form.remark"
                  rows="4"
                  placeholder="可填写投放目标、品类偏好、期望曝光时段等（选填）"
                ></textarea>
              </div>
              <div class="full site-ad-agreement">
                <label>
                  <input v-model="form.agreed" type="checkbox" />
                  我已阅读并同意《广告投放服务协议》
                </label>
              </div>
            </div>
            <div class="site-ad-actions">
              <RouterLink class="btn btn--ghost btn-link" to="/site">返回分站中心</RouterLink>
              <button class="btn btn--primary" :disabled="!canSubmit" @click="submitForm">提交需求</button>
            </div>
          </article>

          <aside class="site-ad-side">
            <div class="card side-card">
              <h3>服务流程</h3>
              <ul>
                <li>1. 提交需求并确认投放城市/广告位</li>
                <li>2. 商务顾问提供排期与报价方案</li>
                <li>3. 素材审核通过后按计划上线</li>
                <li>4. 提供曝光与线索数据复盘</li>
              </ul>
            </div>
            <div class="card side-card">
              <h3>投放建议</h3>
              <ul>
                <li>1. 优先选择目标成交城市分站</li>
                <li>2. 结合品类旺季规划投放周期</li>
                <li>3. 素材突出价格与服务优势</li>
              </ul>
            </div>
          </aside>
        </div>
      </section>

      <section class="section" v-else>
        <div class="container">
          <article class="card site-ad-success">
            <h3>投放需求已提交</h3>
            <p>商务顾问将尽快与您联系，确认排期与报价方案，请保持电话畅通。</p>
            <div class="site-ad-success__actions">
              <RouterLink class="btn btn--ghost btn-link" to="/site">返回分站中心</RouterLink>
              <RouterLink class="btn btn--primary btn-link" to="/site/tangshan">查看示例分站</RouterLink>
            </div>
          </article>
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
      <RouterLink class="active" to="/site">分站</RouterLink>
      <a href="#">我的</a>
    </nav>
  </div>
</template>
