# Huodaizi Backend (P12 运输需求后端与中台)

本模块为 P12 发布运输需求页提供后端 API 与中台管理接口，采用 Spring Boot 3 + Java 21。

## 目录说明

- `controller/transportdemand/TransportDemandController.java`：P12 运输需求公共接口
- `controller/transportdemand/TransportDemandAdminController.java`：P12 运输需求中台接口
- `repository/transportdemand/InMemoryTransportDemandRepository.java`：运输需求内存数据仓储（MVP）
- `dto/transportdemand/**`：运输需求请求与返回模型
- `common/**`、`exception/**`：统一返回与异常处理

## 已实现接口

### 1) 发布运输需求公共接口（P12）

Base Path: `/api/v1/transport-demand`

- `GET /filters`
  - 返回筛选项（起运地/目的地/品类/车型/时效）
- `GET /`
  - 返回运输需求列表，支持筛选与分页参数：
    - `originCity`、`destinationCity`、`goodsCategory`、`vehicleType`、`timeliness`、`invoiceNeed`、`keyword`
    - `page`、`pageSize`
- `GET /{id}`
  - 返回运输需求详情（仅上架记录可见）
- `POST /`
  - 前台发布运输需求（默认上架、默认不置顶）

### 2) 发布运输需求中台接口（P12）

Base Path: `/api/admin/transport-demand`

- `GET /`
  - 中台运输需求列表（含上架/下架记录）
- `POST /`
  - 中台新增运输需求
- `PUT /{id}`
  - 中台编辑运输需求（业务字段、状态、置顶）
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

- 当前为 **MVP 内存版中台**，数据保存在 `InMemoryTransportDemandRepository` 中
- 重启服务后数据会重置
- 适用于：
  - P12 接口联调
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
