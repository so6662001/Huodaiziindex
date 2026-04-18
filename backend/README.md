# Huodaizi Backend (P17 分站中心后端与中台)

本模块为 P17 分站中心页（SiteCenterPage）提供后端 API 与中台管理接口，采用 Spring Boot 3 + Java 21。

## 目录说明

- `controller/sitecenter/SiteCenterController.java`：P17 分站中心公共接口
- `controller/sitecenter/SiteCenterAdminController.java`：P17 分站中心中台接口
- `repository/sitecenter/InMemorySiteCenterRepository.java`：分站中心内存仓储（MVP）
- `dto/sitecenter/**`：分站中心请求与响应模型
- `common/**`、`exception/**`：统一返回与异常处理

## 已实现接口

### 1) 分站中心公共接口（P17）

Base Path: `/api/v1/site-center`

- `GET /overview`
  - 返回分站中心聚合数据：
    - 统计卡片（已开通分站、当日新增供求等）
    - 区域分组与城市分站列表
    - 广告产品列表

### 2) 分站中心中台接口（P17）

Base Path: `/api/admin/site-center/{section}`

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
- `STAT`
- `CITY`
- `AD_PRODUCT`

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

- 当前为 **MVP 内存版中台**，数据保存在 `InMemorySiteCenterRepository` 中
- 重启服务后数据会重置
- 适用于：
  - P17 接口联调
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
