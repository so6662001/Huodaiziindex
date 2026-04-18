<script setup>
import { computed, ref, watch } from 'vue'

const currentStep = ref(1)
const isSubmitted = ref(false)

const form = ref({
  title: '',
  originCity: '唐山',
  destinationCity: '无锡',
  goodsCategory: '螺纹钢',
  tonnage: '',
  vehicleType: '13米平板',
  timeliness: '48小时内',
  loadDate: '',
  needInvoice: true,
  needLoading: false,
  contactName: '',
  contactPhone: '',
  companyName: '',
  remark: '',
  agreed: false
})

const cityOptions = ['唐山', '天津', '无锡', '佛山', '武汉', '广州', '长沙']
const goodsOptions = ['螺纹钢', '热卷', '中厚板', '型钢', '管材', '其他']
const vehicleOptions = ['13米平板', '17.5米平板', '13米高栏', '厢式货车', '不限车型']
const timelinessOptions = ['24小时内', '48小时内', '72小时内', '一周内']

const isStepOneValid = computed(() => {
  return (
    form.value.title.trim().length >= 8 &&
    form.value.originCity !== form.value.destinationCity &&
    form.value.tonnage !== '' &&
    Number(form.value.tonnage) > 0 &&
    form.value.loadDate !== ''
  )
})

const isStepTwoValid = computed(() => {
  const mobileReg = /^1\d{10}$/
  return (
    form.value.contactName.trim().length >= 2 &&
    mobileReg.test(form.value.contactPhone.trim()) &&
    form.value.companyName.trim().length >= 4 &&
    form.value.agreed
  )
})

const canSubmit = computed(() => isStepOneValid.value && isStepTwoValid.value)

const goNext = () => {
  if (!isStepOneValid.value) return
  currentStep.value = 2
}

const goPrev = () => {
  currentStep.value = 1
}

const submitDemand = () => {
  if (!canSubmit.value) return
  isSubmitted.value = true
}

watch(
  () => currentStep.value,
  () => {
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }
)
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
          <RouterLink class="btn btn--primary btn-link" to="/logistics/freight">找车找线</RouterLink>
        </div>
      </div>
    </header>

    <main class="demand-main freight-demand-main">
      <section class="demand-hero freight-demand-hero">
        <div class="container">
          <h1>发布运输需求</h1>
          <p>填写路线、车型、吨位与时效，平台将快速匹配车队与专线服务商。</p>
        </div>
      </section>

      <section class="section">
        <div class="container">
          <div class="card demand-steps">
            <div :class="['demand-step', currentStep === 1 ? 'active' : currentStep > 1 ? 'done' : '']">
              <span>1</span>
              <p>填写运输信息</p>
            </div>
            <div class="demand-step__line"></div>
            <div :class="['demand-step', currentStep === 2 ? 'active' : currentStep > 2 ? 'done' : '']">
              <span>2</span>
              <p>填写联系人信息</p>
            </div>
            <div class="demand-step__line"></div>
            <div :class="['demand-step', isSubmitted ? 'active' : '']">
              <span>3</span>
              <p>提交完成</p>
            </div>
          </div>
        </div>
      </section>

      <section v-if="!isSubmitted" class="section">
        <div class="container demand-layout">
          <div class="card demand-form">
            <template v-if="currentStep === 1">
              <div class="section__header">
                <h2>步骤1：运输信息</h2>
                <span>信息越完整，匹配越精准</span>
              </div>
              <div class="demand-form__grid">
                <div class="full">
                  <label>需求标题</label>
                  <input
                    v-model="form.title"
                    type="text"
                    placeholder="示例：唐山到无锡螺纹钢运输需求（8-40字）"
                  />
                </div>
                <div>
                  <label>起运地</label>
                  <select v-model="form.originCity">
                    <option v-for="item in cityOptions" :key="`o-${item}`">{{ item }}</option>
                  </select>
                </div>
                <div>
                  <label>目的地</label>
                  <select v-model="form.destinationCity">
                    <option v-for="item in cityOptions" :key="`d-${item}`">{{ item }}</option>
                  </select>
                </div>
                <div>
                  <label>货品品类</label>
                  <select v-model="form.goodsCategory">
                    <option v-for="item in goodsOptions" :key="item">{{ item }}</option>
                  </select>
                </div>
                <div>
                  <label>预计吨位（吨）</label>
                  <input v-model="form.tonnage" type="number" placeholder="请输入吨位" />
                </div>
                <div>
                  <label>车型要求</label>
                  <select v-model="form.vehicleType">
                    <option v-for="item in vehicleOptions" :key="item">{{ item }}</option>
                  </select>
                </div>
                <div>
                  <label>时效要求</label>
                  <select v-model="form.timeliness">
                    <option v-for="item in timelinessOptions" :key="item">{{ item }}</option>
                  </select>
                </div>
                <div>
                  <label>装货日期</label>
                  <input v-model="form.loadDate" type="date" />
                </div>
                <div class="full demand-checks">
                  <label><input v-model="form.needInvoice" type="checkbox" /> 需要开票</label>
                  <label><input v-model="form.needLoading" type="checkbox" /> 需要装卸协同</label>
                </div>
              </div>
              <div class="demand-form__actions">
                <RouterLink class="btn btn--ghost btn-link" to="/logistics">返回物流首页</RouterLink>
                <button class="btn btn--primary" :disabled="!isStepOneValid" @click="goNext">下一步</button>
              </div>
            </template>

            <template v-else>
              <div class="section__header">
                <h2>步骤2：联系人信息</h2>
                <span>提交后平台将尽快审核并分发</span>
              </div>
              <div class="demand-form__grid">
                <div>
                  <label>联系人</label>
                  <input v-model="form.contactName" type="text" placeholder="请输入联系人姓名" />
                </div>
                <div>
                  <label>联系电话</label>
                  <input v-model="form.contactPhone" type="text" placeholder="请输入11位手机号" />
                </div>
                <div class="full">
                  <label>公司名称</label>
                  <input v-model="form.companyName" type="text" placeholder="请输入公司全称（与营业执照一致）" />
                </div>
                <div class="full">
                  <label>备注</label>
                  <textarea
                    v-model="form.remark"
                    rows="4"
                    placeholder="可填写装货时段、到货要求、回单要求等（选填）"
                  ></textarea>
                </div>
                <div class="full demand-agree">
                  <label>
                    <input v-model="form.agreed" type="checkbox" />
                    我已阅读并同意《仓储物流服务协议》
                  </label>
                </div>
              </div>
              <div class="demand-form__actions">
                <button class="btn btn--ghost" @click="goPrev">上一步</button>
                <button class="btn btn--primary" :disabled="!canSubmit" @click="submitDemand">提交审核</button>
              </div>
            </template>
          </div>

          <aside class="demand-side">
            <div class="card side-card">
              <h3>发布提示</h3>
              <ul>
                <li>1. 路线与吨位越清晰，报价越准确</li>
                <li>2. 建议写明车型与时效要求</li>
                <li>3. 联系方式需保持可接通</li>
              </ul>
            </div>
            <div class="card side-card">
              <p class="ad__flag">广告</p>
              <h3>运输需求置顶推广</h3>
              <p>支持按起终点与车型精准曝光，提升运输需求对接效率。</p>
              <button class="btn btn--primary">咨询投放</button>
            </div>
          </aside>
        </div>
      </section>

      <section v-else class="section">
        <div class="container">
          <div class="card demand-success">
            <h2>需求已提交</h2>
            <p>预计 1 小时内完成审核并分发承运服务商，请保持电话畅通。</p>
            <div class="demand-success__actions">
              <RouterLink class="btn btn--ghost btn-link" to="/logistics">返回物流首页</RouterLink>
              <button class="btn btn--primary">查看我的需求</button>
            </div>
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
