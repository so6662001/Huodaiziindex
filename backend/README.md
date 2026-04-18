# Huodaizi Backend (P01 后端与中台)

本模块为 P01 首页提供后端 API 与中台管理接口，采用 Spring Boot 3 + Java 21。

## 目录说明

- `controller/HomeController.java`：P01 首页公共接口
- `controller/AdminController.java`：中台管理接口
- `repository/InMemoryHomeRepository.java`：内存数据仓储（MVP）
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
