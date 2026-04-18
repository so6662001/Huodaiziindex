import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '../pages/HomePage.vue'
import SpotPage from '../pages/SpotPage.vue'
import BuyPage from '../pages/BuyPage.vue'
import LogisticsPage from '../pages/LogisticsPage.vue'
import WarehousePage from '../pages/WarehousePage.vue'
import FreightPage from '../pages/FreightPage.vue'
import StorageDemandPage from '../pages/StorageDemandPage.vue'
import DemandDetailPage from '../pages/DemandDetailPage.vue'
import SiteCityPage from '../pages/SiteCityPage.vue'

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
  },
  {
    path: '/logistics',
    name: 'logistics',
    component: LogisticsPage,
    meta: {
      title: '钢铁仓储物流_找仓库找车线与需求发布-货袋子',
      description:
        '提供钢铁仓库与车队专线信息，支持发布仓储与运输需求，快速对接本地服务商。'
    }
  },
  {
    path: '/logistics/warehouse',
    name: 'warehouse',
    component: WarehousePage,
    meta: {
      title: '找仓库_钢铁仓储服务查询-货袋子',
      description:
        '查询钢铁仓储服务，覆盖库型、库容、吊装能力与参考价格，支持按城市和品类快速联系仓库服务商。'
    }
  },
  {
    path: '/logistics/freight',
    name: 'freight',
    component: FreightPage,
    meta: {
      title: '找车找线_钢铁运输服务查询-货袋子',
      description:
        '提供钢铁运输车队与专线信息，支持按起运地、目的地、车型与时效筛选，快速联系承运服务商。'
    }
  },
  {
    path: '/logistics/demand/storage/new',
    name: 'storageDemandCreate',
    component: StorageDemandPage,
    meta: {
      title: '发布仓储需求_钢铁仓储需求快速发布-货袋子',
      description:
        '在线发布钢铁仓储需求，填写城市、品类、吨位、存储天数与联系方式，平台快速匹配本地仓储服务商。'
    }
  },
  {
    path: '/logistics/demand/:id',
    name: 'demandDetail',
    component: DemandDetailPage,
    meta: {
      title: '仓储物流需求详情-货袋子',
      description: '查看仓储与运输需求详情，包含货品、吨位、时效与服务商推荐信息。'
    }
  },
  {
    path: '/site/:city',
    name: 'siteCity',
    component: SiteCityPage,
    meta: {
      title: '城市分站-货袋子',
      description: '查看城市分站的本地行情、供求、仓储物流和企业信息。'
    }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
