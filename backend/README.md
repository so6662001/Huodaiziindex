# Huodaizi Backend (P01 + P02 后端与中台)

本模块为 P01 首页与 P02 现货大厅提供后端 API 与中台管理接口，采用 Spring Boot 3 + Java 21。

## 目录说明

- `controller/HomeController.java`：P01 首页公共接口
- `controller/AdminController.java`：中台管理接口
- `controller/spot/SpotController.java`：P02 现货大厅公共接口
- `controller/spot/SpotAdminController.java`：P02 现货大厅中台接口
- `repository/InMemoryHomeRepository.java`：内存数据仓储（MVP）
- `repository/spot/InMemorySpotRepository.java`：现货大厅内存数据仓储（MVP）
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

> 安全要求：所有 `/api/admin/**` 接口必须携带请求头  
> `X-Admin-Token: <token>`

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

本地调试中台接口示例：

```bash
curl -H "X-Admin-Token: change-this-admin-token" \
  http://127.0.0.1:8080/api/admin/spot
```

## 后续建议（下一阶段）

1. 接入 MySQL + Flyway，替换内存仓储
2. 为中台接口加鉴权（JWT + RBAC）
3. 增加分页与排序（特别是供求、资讯、广告位列表）
4. 增加 OpenAPI 文档（springdoc）
5. 增加集成测试与数据校验测试
