# Huodaizi Backend (P18 分站广告位详情页前后端)

本模块实现 P18 广告位详情页（P18-Detail）后端与中台接口，基于 Spring Boot 3 + Java 21。

## 公共接口

Base Path: `/api/v1/site-ad-detail`

- `GET /?id={placementId}`
  - 返回广告位详情聚合数据：
    - 广告位主信息（名称、价格、位置、描述、支持定向）
    - 价值亮点列表
    - 常见问题列表
- `GET /products`
  - 返回广告位产品列表（用于投放页跳转详情）
- `POST /submit`
  - 提交详情页线索（城市、广告位、周期、公司、联系人、手机号、预算、诉求、协议勾选）

## 中台接口

Base Path: `/api/admin/site-ad-detail/{placementId}/{section}`

- `GET /{placementId}/{section}`
  - 查询指定板块列表（含上下架）
- `POST /{placementId}/{section}`
  - 新增板块内容
- `PUT /{placementId}/{section}/{id}`
  - 更新板块内容
- `PUT /{placementId}/{section}/{id}/status`
  - 上下架（`ONLINE/OFFLINE`）
- `PUT /{placementId}/{section}/{id}/pin`
  - 置顶/取消置顶（`pinned=true|false`）
- `DELETE /{placementId}/{section}/{id}`
  - 删除记录

`section` 枚举：
- `PLACEMENT`
- `BENEFIT`
- `FAQ`
- `LEAD`

## 当前实现说明

- 当前为 MVP 内存实现，数据保存在 `InMemorySiteAdDetailRepository`
- 已内置示例广告位详情数据和线索示例数据
- 适用于 P18 详情页前后端联调与中台流程确认
