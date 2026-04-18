# Huodaizi Backend (P15 运输需求发布后端与中台)

本模块为 P15 运输需求发布页（FreightDemandPage）提供后端 API 与中台管理接口，采用 Spring Boot 3 + Java 21。

## 目录说明

- `controller/freightdemand/FreightDemandController.java`：P15 运输需求公共接口
- `controller/freightdemand/FreightDemandAdminController.java`：P15 运输需求中台接口
- `repository/freightdemand/InMemoryFreightDemandRepository.java`：运输需求内存仓储（MVP）
- `dto/freightdemand/**`：运输需求请求与响应模型
- `common/**`、`exception/**`：统一返回与异常处理

## 已实现接口

### 1) 运输需求公共接口（P15）

Base Path: `/api/v1/freight-demand`

- `GET /filter-options`
  - 返回筛选项：起运地、目的地、货品、车型、时效、开票需求
- `GET /list`
  - 查询运输需求列表，支持参数：
    - `originCity`、`destinationCity`、`goodsCategory`
    - `vehicleType`、`timeliness`、`invoiceNeed`、`loadingNeed`
    - `keyword`、`page`、`pageSize`
- `POST /publish`
  - 发布运输需求（对应前端分步表单字段）

### 2) 运输需求中台接口（P15）

Base Path: `/api/admin/freight-demand`

- `GET /`
  - 查询中台运输需求列表（含上下架）
- `POST /`
  - 新增运输需求记录
- `PUT /{id}`
  - 更新运输需求记录
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

- 当前为 **MVP 内存版中台**，数据保存在 `InMemoryFreightDemandRepository` 中
- 重启服务后数据会重置
- 适用于：
  - P15 接口联调
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
