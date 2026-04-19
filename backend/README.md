# Huodaizi Backend (P16 城市分站后端与中台)

本模块为 P16 城市分站页（SiteCity）提供后端 API 与中台管理接口，采用 Spring Boot 3 + Java 21。

## 目录说明

- `controller/sitecity/SiteCityController.java`：P16 城市分站公共接口
- `controller/sitecity/SiteCityAdminController.java`：P16 城市分站中台接口
- `repository/sitecity/InMemorySiteCityRepository.java`：城市分站内存仓储（MVP）
- `dto/sitecity/**`：城市分站请求与响应模型
- `common/**`、`exception/**`：统一返回与异常处理

## 已实现接口

### 1) 城市分站公共接口（P16）

Base Path: `/api/v1/site-city`

- `GET /{city}`
  - 返回城市分站聚合数据：
    - 行情简报（MARKET）
    - 本地供应精选（SPOT）
    - 本地求购精选（BUY）
    - 本地仓储物流（LOGISTICS）
    - 本地企业黄页（COMPANY）
    - 分站广告位（AD）

### 2) 城市分站中台接口（P16）

Base Path: `/api/admin/site-city`

- `GET /{city}/{section}`
  - 查询指定城市、指定板块的中台列表（含上下架）
- `POST /{city}/{section}`
  - 新增板块内容
- `PUT /{city}/{section}/{id}`
  - 更新板块内容（标题、副标题、价格、涨跌、状态、置顶等）
- `PUT /{city}/{section}/{id}/status`
  - 上下架（请求体传 `status=ONLINE|OFFLINE`）
- `PUT /{city}/{section}/{id}/pin`
  - 置顶/取消置顶（请求体传 `pinned=true|false`）
- `DELETE /{city}/{section}/{id}`
  - 删除记录

`section` 枚举：
- `MARKET`
- `SPOT`
- `BUY`
- `LOGISTICS`
- `COMPANY`
- `AD`

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

- 当前为 **MVP 内存版中台**，数据保存在 `InMemorySiteCityRepository` 中
- 覆盖默认城市：唐山、无锡、佛山、武汉、郑州、成都
- 城市参数支持 slug 与中文城市名，未命中城市会回退到通用占位数据
- 重启服务后数据会重置

## 中台鉴权（已启用）

- 所有 `/api/admin/**` 接口默认开启静态 Token 鉴权。
- 请求头需携带：`X-Admin-Token: change-this-admin-token`（可在配置中修改）。

配置位置（`src/main/resources/application.yml`）：

```yaml
huodaizi:
  admin:
    auth:
      enabled: true
      token: change-this-admin-token
```

## 启动方式

环境需求：
- JDK 21
- Maven 3.9+

启动：

```bash
cd backend
mvn spring-boot:run
```
