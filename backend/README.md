# Huodaizi Backend (P04 行情中心后端与中台)

本模块为 P04 行情中心页（MarketPage）提供后端 API 与中台管理接口，采用 Spring Boot 3 + Java 21。

## 目录说明

- `controller/market/MarketController.java`：P04 行情中心公共接口
- `controller/market/MarketAdminController.java`：P04 行情中心中台接口
- `repository/market/InMemoryMarketRepository.java`：行情中心内存仓储（MVP）
- `dto/market/**`：行情中心请求与响应模型
- `common/**`、`exception/**`：统一返回与异常处理

## 已实现接口

### 1) 行情中心公共接口（P04）

Base Path: `/api/v1/market`

- `GET /overview`
  - 返回行情中心聚合数据：
    - 行情面板（今日参考价/7日波动/30日区间）
    - 行情快照表
    - 市场信号
- 支持参数：
  - `category`（品种，如螺纹钢）
  - `city`（城市，如唐山）
  - `range`（周期：7日/30日/90日）

### 2) 行情中心中台接口（P04）

Base Path: `/api/admin/market`

> 安全要求：所有 `/api/admin/**` 接口必须携带请求头  
> `X-Admin-Token: <token>`

- `GET /{section}`
  - 查询指定板块中台列表（含上下架）
- `POST /{section}`
  - 新增板块内容
- `PUT /{section}/{id}`
  - 更新板块内容
- `PUT /{section}/{id}/status`
  - 上下架（请求体传 `status=ONLINE|OFFLINE`）
- `PUT /{section}/{id}/pin`
  - 置顶/取消置顶（请求体传 `pinned=true|false`）
- `DELETE /{section}/{id}`
  - 删除记录

`section` 枚举：
- `QUOTE_PANEL`
- `SNAPSHOT`
- `INSIGHT`
- `SIGNAL`

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

- 当前为 **MVP 内存版中台**，数据保存在 `InMemoryMarketRepository` 中
- 重启服务后数据会重置
- 适用于：
  - P04 接口联调
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
  http://127.0.0.1:8080/api/admin/market/SNAPSHOT
```
