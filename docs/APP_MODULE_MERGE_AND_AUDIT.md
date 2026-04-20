# 代码合并分层与审计清单（PC端 / 移动端 / 中台 / 后端）

本文档用于将“第一版网站到最新开发平台”的现有代码按四类统一归并，并作为运行与流程审计基线。

## 1. 分层定义

- **PC端（Web-PC）**：面向买家/商家的桌面业务页面与路由。
- **移动端（Web-H5）**：`/h5/**` 业务页面与路由。
- **中台（Admin Console）**：`/admin/**` 运营管理、风控、RBAC、审计等页面与路由。
- **后端（Backend API）**：`backend` 模块中的控制器、服务、仓储、DTO、鉴权、审计。

## 2. 前端路由归并结果

### 2.1 PC端路由（30）

- `/`
- `/account/login-register`
- `/account/identity-select`
- `/account/enterprise-certification`
- `/account/onboarding-progress`
- `/account/negotiation-session`
- `/account/order-detail`
- `/account/trade-terms-confirm`
- `/account/after-sale-dispute`
- `/account/after-sale-progress`
- `/account/cashier`
- `/account/payment-result`
- `/account/invoice-manage`
- `/account/credit-score-detail`
- `/account/dispatch-appeal`
- `/inquiry/create`
- `/inquiry/success`
- `/inquiry/compare`
- `/inquiry/deal/confirm`
- `/inquiry/pickup/pass`
- `/inquiry/reconcile/pass`
- `/merchant/credit/score`
- `/merchant/subscription`
- `/merchant/billing`
- `/dispatch/score-rules`
- `/merchant/message-center`
- `/merchant/lead/manage`
- `/merchant/quote/workbench`
- `/site/ad/mine`
- `/site/ad/submit`

### 2.2 移动端路由（21）

- `/h5`
- `/h5/login-quick`
- `/h5/identity-switch`
- `/h5/enterprise-certification`
- `/h5/quote-session`
- `/h5/order-detail`
- `/h5/pickup-scan`
- `/h5/reconcile-detail`
- `/h5/after-sale-create`
- `/h5/lite-pay`
- `/h5/credit-brief`
- `/h5/message-settings`
- `/h5/inquiry/step1`
- `/h5/inquiry/step2`
- `/h5/inquiry/step3`
- `/h5/quote-compare`
- `/h5/merchant/leads`
- `/h5/quick-quote`
- `/h5/pickup-orders`
- `/h5/reconcile-orders`
- `/h5/member`

### 2.3 中台路由（23）

- `/admin/dashboard/a01`
- `/admin/lead-ops/a02`
- `/admin/quote-efficiency/a03`
- `/admin/pickup-monitor/a04`
- `/admin/reconcile-monitor/a05`
- `/admin/dispatch-strategy/a06`
- `/admin/plan-pricing/a07`
- `/admin/risk-alert/a08`
- `/admin/merchant-cert-review/admn01`
- `/admin/buyer-blacklist/admn02`
- `/admin/rbac-role-permission/admn03`
- `/admin/audit-log/admn04`
- `/admin/category-spec-dict/admn05`
- `/admin/lead-quality/admn06`
- `/admin/deal-funnel-analysis/admn08`
- `/admin/arbitration-ticket-center/admn09`
- `/admin/billing-rule-config/admn10`
- `/admin/payment-refund-manage/admn11`
- `/admin/ad-slot-schedule-center/admn12`
- `/admin/credit-model-version-manage/admn13`
- `/admin/ab-experiment-center/admn14`
- `/admin/risk-alert-ticket-center/admn15`
- `/admin/data-api-subscription-manage/admn16`

## 3. 后端模块归并结果（控制器视角）

### 3.1 PC/移动业务域 API（`/api/v1/**`）

- `auth`：登录注册、会话、身份切换、PC/H5交易链路接口
- `inquiry`：询价、报价、成交、提货、对账、消息、工作台
- `siteadlead`：分站广告线索投放与我的投放单
- `identity`：身份切换

### 3.2 中台 API（`/api/admin/**`）

- A01~A08：经营看板、线索运营、报价效率、提货监控、对账监控、策略配置、套餐定价、风险预警
- ADM-N01~N16：认证审核、黑名单、RBAC、审计日志、词库、质检、漏斗、仲裁、计费、支付退款、排期、模型版本、A/B实验、风险工单、数据API订阅
- site-ad-lead admin：线索状态、分配、跟进

## 4. 审计结论（本轮）

- 路由分层归并完成，PC/移动端/中台共 **74** 条路由全部可解析。
- 首页所有 `router.push` 目标均存在对应路由（含 query 参数场景）。
- ADM-N14/15/16 关键流程已回归：
  - ADM-N14 A/B实验
  - ADM-N15 风险工单
  - ADM-N16 数据API订阅
- 已修复关键逻辑缺陷：
  - ADM-N15 `followUpPlan` 与 `latestRemark` 语义混用
  - ADM-N14/16 前端详情字段回填错位
  - 首页无效路由跳转

## 5. 运行验证（本轮执行）

- 后端：`mvn test`（全量）通过
- 后端：ADM-N14/15/16 集成测试组合通过
- 前端：`npm run build` 通过

## 6. 设计与流程一致性说明

- 页面与 API 已按“PC端 / 移动端 / 中台 / 后端”四层组织并能联通。
- 中台侧接口统一走 `X-Admin-Token`，并持续接入 RBAC 与审计模块码/动作码。
- PC/H5 业务流走 `N01/H5` 会话体系，路径与页面职责匹配。

