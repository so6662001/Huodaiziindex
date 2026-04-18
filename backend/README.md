# Huodaizi Backend (P18 分站广告投放后端与中台)

本模块为 P18 分站广告投放页（SiteAdPage）提供后端 API 与中台管理接口，采用 Spring Boot 3 + Java 21。

## 目录说明

- `controller/sitead/SiteAdController.java`：P18 分站广告公共接口
- `controller/sitead/SiteAdAdminController.java`：P18 分站广告中台接口
- `repository/sitead/InMemorySiteAdRepository.java`：分站广告内存仓储（MVP）
- `dto/sitead/**`：分站广告请求与响应模型
- `common/**`、`exception/**`：统一返回与异常处理

## 已实现接口

### 1) 分站广告公共接口（P18）

Base Path: `/api/v1/site-ad`

- `GET /options`
  - 返回投放表单选项（城市、广告位类型、投放周期）
- `GET /products`
  - 返回广告位产品列表（名称、价格、描述、链接）
- `POST /publish`
  - 提交投放需求（城市、广告位、周期、公司、联系人、手机号、预算、诉求、协议勾选）

### 2) 分站广告中台接口（P18）

Base Path: `/api/admin/site-ad/{section}`

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
- `PRODUCT`
- `DEMAND`
- `OPTION`

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

- 当前为 **MVP 内存版中台**，数据保存在 `InMemorySiteAdRepository` 中
- 重启服务后数据会重置
- 适用于：
  - P18 接口联调
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
