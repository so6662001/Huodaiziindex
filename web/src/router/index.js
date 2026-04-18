import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '../pages/HomePage.vue'
import SpotPage from '../pages/SpotPage.vue'
import BuyPage from '../pages/BuyPage.vue'

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomePage,
    meta: {
      title: '钢铁现货交易与仓储物流信息平台-货袋子',
      description:
        '货袋子提供钢铁现货、求购、行情、仓储物流与城市分站服务，支持按品类、城市快速筛选与发布。'
    }
  },
  {
    path: '/spot',
    name: 'spot',
    component: SpotPage,
    meta: {
      title: '钢铁现货大厅_供应信息查询与发布-货袋子',
      description:
        '汇聚全国钢铁现货供应信息，支持按品类、规格、城市、价格筛选，快速联系卖家并发布现货。'
    }
  },
  {
    path: '/buy',
    name: 'buy',
    component: BuyPage,
    meta: {
      title: '钢铁求购大厅_采购需求查询与发布-货袋子',
      description:
        '实时更新钢铁求购信息，覆盖多城市与多品类，支持快速筛选并联系买家，提升供需撮合效率。'
    }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
