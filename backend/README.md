# Huodaizi Backend (P18 线索运营闭环)

本模块实现 P18 提交后的后续运营闭环：分站广告线索管理（我的投放单 + 中台跟进）。

## 公共接口

Base Path: `/api/v1/site-ad-lead`

- `POST /submit`
  - 提交广告投放线索
- `GET /mine?contactPhone=...&keyword=...&page=1&pageSize=10`
  - 查询“我的投放单”列表（按手机号）

## 中台接口

Base Path: `/api/admin/site-ad-lead`

- `GET /`
  - 中台线索列表（支持 status/keyword/phone/page/pageSize）
- `GET /{id}`
  - 线索详情
- `PUT /{id}/status`
  - 更新线索状态（`SUBMITTED`/`ASSIGNED`/`CONTACTED`/`PROPOSAL_SENT`/`CONVERTED`/`CLOSED`）
- `PUT /{id}/assign`
  - 指派跟进人
- `POST /{id}/follow`
  - 添加跟进记录

## 闭环状态定义

- `SUBMITTED`：已提交
- `ASSIGNED`：已分配
- `CONTACTED`：已联系
- `PROPOSAL_SENT`：已发方案
- `CONVERTED`：已转化
- `CLOSED`：已关闭

## 当前实现说明

- 当前为 MVP 内存实现，重启后数据重置
- 支持“提交 -> 我的投放单查看 -> 中台分配/跟进 -> 状态变化回流”的闭环联调

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
