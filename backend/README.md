# Huodaizi Backend (P11-2 仓储需求前后端)

本模块实现 P11-2（仓储需求列表与检索子页）所需后端接口与中台管理接口，基于 Spring Boot 3 + Java 21。

## 公共接口

Base Path: `/api/v1/storage-demand`

- `GET /filters`
  - 返回筛选项：`cityOptions`、`goodsCategoryOptions`、`serviceNeedOptions`
- `GET /`
  - 列表查询，支持参数：
    - `city`
    - `goodsCategory`
    - `serviceNeed`（`仅仓储`/`需要装卸`/`需要分拣`/`装卸+分拣`）
    - `keyword`
    - `page`、`pageSize`
- `GET /{id}`
  - 需求详情
- `POST /`
  - 发布仓储需求

## 中台接口

Base Path: `/api/admin/storage-demand`

- `GET /` 列表
- `POST /` 新增
- `PUT /{id}` 更新
- `PUT /{id}/status` 上下架（`ONLINE/OFFLINE`）
- `PUT /{id}/pin` 置顶/取消置顶
- `DELETE /{id}` 删除

## 当前实现说明

- 当前为 MVP 内存实现，数据保存在 `InMemoryStorageDemandRepository`
- 已内置示例数据，便于前端直接联调
- 提供联系人与手机号脱敏展示
