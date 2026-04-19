# Huodaizi Backend (P01 + P02 + P03 + P08 + P09 + P10 + P11 后端与中台)

本模块为 P01 首页、P02 现货大厅、P03 求购大厅、P08 仓储物流首页、P09 找仓库页、P10 找车找线页与 P11 发布仓储需求页提供后端 API 与中台管理接口，采用 Spring Boot 3 + Java 21。

## 目录说明

- `controller/HomeController.java`：P01 首页公共接口
- `controller/AdminController.java`：中台管理接口
- `controller/spot/SpotController.java`：P02 现货大厅公共接口
- `controller/spot/SpotAdminController.java`：P02 现货大厅中台接口
- `controller/buy/BuyController.java`：P03 求购大厅公共接口
- `controller/buy/BuyAdminController.java`：P03 求购大厅中台接口
- `controller/logistics/LogisticsController.java`：P08 仓储物流公共接口
- `controller/logistics/LogisticsAdminController.java`：P08 仓储物流中台接口
- `controller/warehouse/WarehouseController.java`：P09 找仓库公共接口
- `controller/warehouse/WarehouseAdminController.java`：P09 找仓库中台接口
- `controller/freight/FreightController.java`：P10 找车找线公共接口
- `controller/freight/FreightAdminController.java`：P10 找车找线中台接口
- `controller/storagedemand/StorageDemandController.java`：P11 仓储需求公共接口
- `controller/storagedemand/StorageDemandAdminController.java`：P11 仓储需求中台接口
- `repository/InMemoryHomeRepository.java`：内存数据仓储（MVP）
- `repository/spot/InMemorySpotRepository.java`：现货大厅内存数据仓储（MVP）
- `repository/buy/InMemoryBuyRepository.java`：求购大厅内存数据仓储（MVP）
- `repository/logistics/InMemoryLogisticsRepository.java`：仓储物流内存数据仓储（MVP）
- `repository/warehouse/InMemoryWarehouseRepository.java`：找仓库内存数据仓储（MVP）
- `repository/freight/InMemoryFreightRepository.java`：找车找线内存数据仓储（MVP）
- `repository/storagedemand/InMemoryStorageDemandRepository.java`：仓储需求内存数据仓储（MVP）
- `dto/**`：返回与请求模型
- `common/**`、`exception/**`：统一返回与异常处理

## 已实现接口

### 1) 首页公共接口（P01）

Base Path: `/api/v1/home`

- `GET /overview`
  - 返回首页聚合数据：
    - 首页配置
    - 行情速览
    - 最新供应
    - 最新求购
    - 仓储推荐
    - 车队专线
    - 热门分站
    - 最新资讯
    - 广告位

- `GET /search?city=&type=&keyword=`
  - 全局检索（MVP）
  - 支持按城市、类型、关键词筛选
  - 返回中台实体结果（便于前端快速打通）

### 2) 中台管理接口（P01）

Base Path: `/api/admin`

- `GET /config`
- `PUT /config`
- `GET /{type}`
- `POST /{type}`
- `PUT /{type}/{id}`
- `DELETE /{type}/{id}`

`type` 枚举：
- `HOME_CONFIG`
- `MARKET_QUOTE`
- `SUPPLY`
- `DEMAND`
- `WAREHOUSE`
- `FREIGHT`
- `STATION`
- `NEWS`
- `AD_SLOT`

### 3) 现货大厅公共接口（P02）

Base Path: `/api/v1/spot`

- `GET /filters`
  - 返回筛选项（品类/规格/城市/价格区间）
- `GET /`
  - 返回现货列表，支持筛选与分页参数：
    - `category`、`spec`、`city`、`priceRange`、`keyword`
    - `page`、`pageSize`
- `GET /{id}`
  - 返回现货详情（仅上架记录可见）
- `POST /`
  - 前台发布现货（默认上架、默认不置顶）

### 4) 现货大厅中台接口（P02）

Base Path: `/api/admin/spot`

- `GET /`
  - 中台现货列表（含上架/下架记录）
- `POST /`
  - 中台新增现货
- `PUT /{id}`
  - 中台编辑现货（基础字段、状态、置顶）
- `PUT /{id}/status`
  - 上下架（请求体传 `status=ONLINE|OFFLINE`）
- `PUT /{id}/pin`
  - 置顶/取消置顶（请求体传 `pinned=true|false`）
- `DELETE /{id}`
  - 删除记录

### 5) 求购大厅公共接口（P03）

Base Path: `/api/v1/buy`

- `GET /filters`
  - 返回筛选项（品类/规格/城市/交期）
- `GET /`
  - 返回求购列表，支持筛选与分页参数：
    - `category`、`spec`、`city`、`arrival`、`keyword`
    - `page`、`pageSize`
- `GET /{id}`
  - 返回求购详情（仅上架记录可见）
- `POST /`
  - 前台发布求购（默认上架、默认不置顶）

### 6) 求购大厅中台接口（P03）

Base Path: `/api/admin/buy`

- `GET /`
  - 中台求购列表（含上架/下架记录）
- `POST /`
  - 中台新增求购
- `PUT /{id}`
  - 中台编辑求购（基础字段、状态、置顶）
- `PUT /{id}/status`
  - 上下架（请求体传 `status=ONLINE|OFFLINE`）
- `PUT /{id}/pin`
  - 置顶/取消置顶（请求体传 `pinned=true|false`）
- `DELETE /{id}`
  - 删除记录

### 7) 仓储物流公共接口（P08）

Base Path: `/api/v1/logistics`

- `GET /overview`
  - 返回仓储物流首页聚合数据：
    - 快捷入口
    - 推荐仓库
    - 推荐车队专线
    - 最新仓储需求
    - 最新运输需求
    - 热门城市入口
    - 招商广告位
- `GET /search`
  - 支持按 `city`、`type`、`keyword` 搜索仓储物流资源
  - `type` 支持：`WAREHOUSE`、`FREIGHT`、`STORAGE_DEMAND`、`TRANSPORT_DEMAND`、`CITY_STATION`、`AD_SLOT`

### 8) 仓储物流中台接口（P08）

Base Path: `/api/admin/logistics`

- `GET /{section}`
  - 按板块查询中台列表
- `POST /{section}`
  - 按板块新增资源
- `PUT /{section}/{id}`
  - 更新资源字段（标题、副标题、价格、状态、置顶等）
- `PUT /{section}/{id}/status`
  - 上下架（请求体传 `status=ONLINE|OFFLINE`）
- `PUT /{section}/{id}/pin`
  - 置顶/取消置顶（请求体传 `pinned=true|false`）
- `DELETE /{section}/{id}`
  - 删除记录

`section` 枚举：
- `QUICK_ENTRY`
- `WAREHOUSE`
- `FREIGHT`
- `STORAGE_DEMAND`
- `TRANSPORT_DEMAND`
- `CITY_STATION`
- `AD_SLOT`

### 9) 找仓库公共接口（P09）

Base Path: `/api/v1/warehouse`

- `GET /filters`
  - 返回筛选项（城市/库型/品类/吊装能力/价格区间）
- `GET /`
  - 返回仓库列表，支持筛选与分页参数：
    - `city`、`warehouseType`、`category`、`lifting`、`priceRange`、`keyword`
    - `page`、`pageSize`
- `GET /{id}`
  - 返回仓库详情（仅上架记录可见）
- `POST /`
  - 前台发布仓库（默认上架、默认不置顶）

### 10) 找仓库中台接口（P09）

Base Path: `/api/admin/warehouse`

- `GET /`
  - 中台仓库列表（含上架/下架记录）
- `POST /`
  - 中台新增仓库
- `PUT /{id}`
  - 中台编辑仓库（基础字段、状态、置顶）
- `PUT /{id}/status`
  - 上下架（请求体传 `status=ONLINE|OFFLINE`）
- `PUT /{id}/pin`
  - 置顶/取消置顶（请求体传 `pinned=true|false`）
- `DELETE /{id}`
  - 删除记录

### 11) 找车找线公共接口（P10）

Base Path: `/api/v1/freight`

- `GET /filters`
  - 返回筛选项（起运地/目的地/车型/时效/回程车）
- `GET /`
  - 返回车队与专线列表，支持筛选与分页参数：
    - `origin`、`destination`、`vehicleType`、`timeliness`、`returnTruck`、`keyword`
    - `page`、`pageSize`
- `GET /{id}`
  - 返回车线详情（仅上架记录可见）
- `POST /`
  - 前台发布车线（默认上架、默认不置顶）

### 12) 找车找线中台接口（P10）

Base Path: `/api/admin/freight`

- `GET /`
  - 中台车线列表（含上架/下架记录）
- `POST /`
  - 中台新增车线
- `PUT /{id}`
  - 中台编辑车线（基础字段、状态、置顶）
- `PUT /{id}/status`
  - 上下架（请求体传 `status=ONLINE|OFFLINE`）
- `PUT /{id}/pin`
  - 置顶/取消置顶（请求体传 `pinned=true|false`）
- `DELETE /{id}`
  - 删除记录

### 13) 发布仓储需求公共接口（P11）

Base Path: `/api/v1/storage-demand`

- `GET /filters`
  - 返回筛选项（城市/品类/服务需求）
- `GET /`
  - 返回仓储需求列表，支持筛选与分页参数：
    - `city`、`goodsCategory`、`serviceNeed`、`keyword`
    - `page`、`pageSize`
- `GET /{id}`
  - 返回仓储需求详情（仅上架记录可见）
- `POST /`
  - 前台发布仓储需求（默认上架、默认不置顶）

### 14) 发布仓储需求中台接口（P11）

Base Path: `/api/admin/storage-demand`

> 安全要求：所有 `/api/admin/**` 接口必须携带请求头  
> `X-Admin-Token: <token>`

- `GET /`
  - 中台仓储需求列表（含上架/下架记录）
- `POST /`
  - 中台新增仓储需求
- `PUT /{id}`
  - 中台编辑仓储需求（业务字段、状态、置顶）
- `PUT /{id}/status`
  - 上下架（请求体传 `status=ONLINE|OFFLINE`）
- `PUT /{id}/pin`
  - 置顶/取消置顶（请求体传 `pinned=true|false`）
- `DELETE /{id}`
  - 删除记录

## 返回结构

统一返回：

```json
{
  "code": "0",
  "message": "OK",
  "data": {}
}
```

失败返回：

```json
{
  "code": "BAD_REQUEST",
  "message": "错误描述",
  "data": null
}
```

## 当前实现说明

- 当前为 **MVP 内存版中台**，数据保存在 `InMemoryHomeRepository` 中
- P02 现货数据保存在 `InMemorySpotRepository` 中
- P03 求购数据保存在 `InMemoryBuyRepository` 中
- P08 仓储物流数据保存在 `InMemoryLogisticsRepository` 中
- P09 找仓库数据保存在 `InMemoryWarehouseRepository` 中
- P10 找车找线数据保存在 `InMemoryFreightRepository` 中
- P11 仓储需求数据保存在 `InMemoryStorageDemandRepository` 中
- 重启服务后数据会重置
- 适用于：
  - P01 接口联调
  - 中台字段与交互流程确认
  - 后续接 DB/MyBatis/JPA 前的 API 冻结

## 启动方式

环境需求：
- JDK 21
- Maven 3.9+

启动：

```bash
cd backend
mvn spring-boot:run
```

## 后续建议（下一阶段）

1. 接入 MySQL + Flyway，替换内存仓储
2. 为中台接口加鉴权（JWT + RBAC）
3. 增加分页与排序（特别是供求、资讯、广告位列表）
4. 增加 OpenAPI 文档（springdoc）
5. 增加集成测试与数据校验测试
