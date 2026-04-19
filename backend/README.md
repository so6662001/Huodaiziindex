# Huodaizi Backend (P14 找车找线详情后端与中台)

本模块为 P14 找车找线详情页（FreightDetailPage）提供后端 API 与中台管理接口，采用 Spring Boot 3 + Java 21。

## 目录说明

- `controller/freightdetail/FreightDetailController.java`：P14 专线详情公共接口
- `controller/freightdetail/FreightDetailAdminController.java`：P14 专线详情中台接口
- `repository/freightdetail/InMemoryFreightDetailRepository.java`：专线详情内存仓储（MVP）
- `dto/freightdetail/**`：专线详情请求与响应模型
- `common/**`、`exception/**`：统一返回与异常处理

## 已实现接口

### 1) 专线详情公共接口（P14）

Base Path: `/api/v1/freight-detail`

- `GET /`
  - 查询参数：`id`（专线 ID）
  - 返回：
    - 专线核心信息（承运方、线路、车型、载重、班次、时效、报价、服务能力、说明）
    - 相关推荐专线
    - 相关需求
    - 联系方式
    - 广告位

### 2) 专线详情中台接口（P14）

Base Path: `/api/admin/freight-detail/{id}/{section}`

- `GET /{id}/{section}`
  - 查询指定专线 + 板块的中台列表（含上下架）
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
- `SERVICE_TAG`
- `RELATED_LINE`
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

- 当前为 **MVP 内存版中台**，数据保存在 `InMemoryFreightDetailRepository` 中
- 重启服务后数据会重置
- 适用于：
  - P14 接口联调
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
