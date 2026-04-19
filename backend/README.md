# Huodaizi Backend (P13 仓库详情后端与中台)

本模块为 P13 仓库详情页（WarehouseDetailPage）提供后端 API 与中台管理接口，采用 Spring Boot 3 + Java 21。

## 目录说明

- `controller/warehousedetail/WarehouseDetailController.java`：P13 仓库详情公共接口
- `controller/warehousedetail/WarehouseDetailAdminController.java`：P13 仓库详情中台接口
- `repository/warehousedetail/InMemoryWarehouseDetailRepository.java`：仓库详情内存仓储（MVP）
- `dto/warehousedetail/**`：仓库详情请求与响应模型
- `common/**`、`exception/**`：统一返回与异常处理

## 已实现接口

### 1) 仓库详情公共接口（P13）

Base Path: `/api/v1/warehouse-detail`

- `GET /`
  - 查询参数：`id`（仓库 ID）
  - 返回：
    - 仓库核心信息（城市、库型、库容、吞吐、能力、报价、地址、作业时间、标签、说明）
    - 相关推荐仓库
    - 相关需求
    - 联系方式（脱敏）
    - 广告位

### 2) 仓库详情中台接口（P13）

Base Path: `/api/admin/warehouse-detail/{id}/{section}`

- `GET /{id}/{section}`
  - 查询指定仓库 + 板块的中台列表（含上下架）
- `POST /{id}/{section}`
  - 新增板块内容
- `PUT /{id}/{section}/{recordId}`
  - 更新板块内容
- `PUT /{id}/{section}/{recordId}/status`
  - 上下架（请求体传 `status=ONLINE|OFFLINE`）
- `PUT /{id}/{section}/{recordId}/pin`
  - 置顶/取消置顶（请求体传 `pinned=true|false`）
- `DELETE /{id}/{section}/{recordId}`
  - 删除记录

`section` 枚举：
- `MAIN`
- `RELATED_WAREHOUSE`
- `RELATED_DEMAND`
- `CONTACT`
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

- 当前为 **MVP 内存版中台**，数据保存在 `InMemoryWarehouseDetailRepository` 中
- 重启服务后数据会重置
- 适用于：
  - P13 接口联调
  - 中台字段与交互流程确认
  - 后续接 DB/MyBatis/JPA 前的 API 冻结

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
