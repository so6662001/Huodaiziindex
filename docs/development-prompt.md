# 货袋子 V2 平台开发提示词（Development Brief）

> **本文档定位**  
> 给开发团队、AI 编程助手（Cursor/Claude/Copilot）使用的完整开发指引。任何代码生成、需求拆解、Code Review 都应以此为唯一基准。  
> 配套设计稿位于 `designs/` 目录（4 个 HTML 文件 + 截图）：  
> - `designs/mockup-home-v2.html` 首页  
> - `designs/mockup-shanghai.html` 上海分站  
> - `designs/mockup-tools.html` 4 大工具详情页  
> - `designs/index.html` 设计稿索引

---

## 第 0 章 · 必读约定

1. **不允许**在未与产品/设计确认前修改设计稿表达的功能、字段、布局、配色。
2. **不允许**私自引入未在《第 2 章 技术栈》中列出的依赖；新增依赖须走变更评审。
3. **必须**遵守《第 7 章 URL & 301 规范》中的全部路径与重定向，否则 SEO 改造失败。
4. **必须**遵守《第 10 章 代码规范》全部内容，CI 会强制 lint + 单测 + 类型检查。
5. **必须**为每个对外接口写 OpenAPI/Swagger 注解，前端调用必须先生成 TypeScript 类型。
6. **禁止**在公开页面（首页 / 分站 / 资源详情 / 商家详情 / 资讯）中嵌入 wujie 子应用；wujie 仅承载登录后的工作台模块（详见《第 8.6 节》）。
7. **禁止**将敏感数据（密钥、Token、内部 IP）写入前端代码或 Git；统一从环境变量读取。
8. 所有时间字段统一使用 UTC（数据库 `TIMESTAMP WITH TIME ZONE`），前端按浏览器时区展示。
9. 所有金额字段（价格、调价幅度）统一使用 `BIGINT` 存储「分」，避免浮点。前端展示「元/吨」。
10. 红涨绿跌（中国行业惯例），全平台统一。

---

## 第 1 章 · 项目背景

### 1.1 平台定位
**货袋子** 是面向全国的钢材现货交易平台，**主域名 `www.huodaizi.com`**，定位「行情中心 + 资源池 + 钢贸操盘工具」。

- **核心用户**：钢贸商家（卖方）、采购方（工程项目、地产、生产企业）
- **核心场景**：找货、看价、订阅、求购、了解钢厂动态、本地交易
- **暂不开放**：撮合（无业务数据）、批量比价

### 1.2 现状与目标
| 项 | 现状 | 目标 |
|---|---|---|
| 前端 | Vue3 SPA（Element Plus + Naive UI + wujie 微前端 + ag-grid + vxe-table），HTML 空壳 | 公开页采用 SSR 等价方案（Java + Thymeleaf 输出 SEO 着陆页），用户端仍走 Vue3 SPA |
| SEO | 爬虫抓到空白，无 sitemap，无结构化数据 | 全量可抓取 + sitemap + JSON-LD + 站长平台推送 |
| URL | `/fullscreen`、`/fullscreen-classic` 等功能名 | 语义化 + 301 + canonical |
| 信息架构 | 散乱、无频道分层 | 按 v2 设计稿统一信息架构 |
| 视觉风格 | 杂 | 统一 v2 工作台风格（橙 #FF6A00 + 浅色 + 局部深色指数大屏） |

### 1.3 业务模块清单
1. 首页（行情大屏 + 三栏作战台 + 钢厂出厂价 + 热力图 + 资讯 + 工具）
2. 分站（共 20 个，以上海分站为模板）
3. 找钢材列表 + 资源详情
4. 找求购列表 + 求购详情
5. 商家黄页 + 商家详情
6. 行情中心（K 线 / 多线对比 / 热力图）
7. 钢厂出厂价频道
8. 钢材资讯频道（5 个栏目，新增）
9. 4 个工具：智能找钢 / 价格订阅 / 报价雷达 / 行情对比
10. 关于我们 / 帮助中心 / 商家入驻 / 联系
11. 用户工作台（登录后，wujie 子应用）

---

## 第 2 章 · 技术栈约束

### 2.1 后端
| 项 | 选型 | 版本 |
|---|---|---|
| 语言 | Java | 17 LTS |
| 框架 | Spring Boot | 3.2.x |
| Web | Spring MVC | 内置 |
| 模板引擎（SEO 着陆页） | Thymeleaf | 3.1.x |
| 持久化 | MyBatis-Plus | 3.5.x |
| 数据库 | MySQL | 8.0 |
| 缓存 | Redis | 7.x |
| 消息 | RocketMQ / Kafka（按现状二选一） | — |
| 检索 | Elasticsearch | 8.x |
| 文档 | springdoc-openapi | 2.x |
| 工具 | Hutool / Lombok / MapStruct | latest |
| 构建 | Maven | 3.9+ |
| 测试 | JUnit 5 + Mockito + Testcontainers | — |
| 日志 | SLF4J + Logback + ELK | — |

### 2.2 前端
| 项 | 选型 | 版本 |
|---|---|---|
| 语言 | TypeScript | 5.x |
| 框架 | Vue | 3.4+ |
| 路由 | vue-router | 4.x |
| 状态 | Pinia | 2.x |
| UI（保留现状） | Element Plus / Naive UI | 现状版本 |
| 表格（保留现状） | vxe-table / ag-grid（仅工作台后台） | 现状 |
| 微前端（保留现状） | wujie-vue3 | 现状 |
| 图表 | ECharts | 5.x（统一） |
| HTTP | axios | 1.x |
| 构建 | Vite | 5.x |
| 单测 | Vitest + @vue/test-utils | latest |
| E2E | Playwright | latest |

### 2.3 SEO / 运维
| 项 | 选型 |
|---|---|
| 反向代理 | Nginx 1.24+ |
| CDN | 腾讯云 / 阿里云 CDN |
| 监控 | Prometheus + Grafana |
| APM | SkyWalking |
| 日志 | ELK |
| 站长平台 | 百度站长 / Bing Webmaster / Google Search Console |

### 2.4 禁用清单
- ❌ 不允许引入新的 UI 框架（如 Ant Design Vue、Vant）— 与现有 Element Plus 冲突
- ❌ 不允许 Nuxt SSR 改造（与微前端冲突，且改造面太大）
- ❌ 不允许在公开页加载 ag-grid / vxe-table（体积过大，影响 LCP）
- ❌ 不允许使用 jQuery 写新代码
- ❌ 不允许使用 moment（已在弃用），统一用 dayjs

---

## 第 3 章 · 业务功能清单（设计稿 → 实现映射）

> 阅读方式：以 `designs/mockup-home-v2.html` 等 4 个 HTML 文件为视觉真源，本章按"区块 → 数据来源 → API → 交互"映射到实现。

### 3.1 首页 V2 区块清单

#### 3.1.1 顶部条 / Header / Nav
- **顶部条**：城市切换（按浏览器定位 + Cookie）、登录入口、APP 下载、商家入驻、客服电话
- **Header Logo + 搜索框**：搜索 type = 找钢材 / 找求购 / 找商家 / 找资讯，提交跳转对应列表页带 `q` 参数
- **顶部 4 个工具快捷入口**：仅作为入口跳转 `/tools/find` `/tools/subscribe` `/tools/radar` `/tools/compare`
- **主导航**：8 个一级导航（详见 7.1）；右侧实时数据展示当日访问数、商家数

#### 3.1.2 钢材指数大屏（深色，唯一深色区）
- 7 个 cell：综合指数 / 螺纹指数 / 热卷指数 / 冷轧指数 / 中厚板指数 / 涨跌分布 / 市场速报
- **数据来源**：`t_index_daily` 日聚合表（每日 09:00 由定时任务从 `t_offer` 报价表聚合）
- **API**：`GET /api/v1/index/today` 返回完整大屏数据
- **滚动 Ticker**：`GET /api/v1/index/ticker` 返回近 60 秒滚动行情，前端每 30 秒拉一次

#### 3.1.3 三栏作战台

**左栏（220px）**：
1. **找钢工具 6 个**：链接到 `/tools/*`
2. **品类导航 9 条**：链接到 `/resource?cat=xxx`，附带每类资源数（缓存 5 分钟）
3. **地区导航 8 条**：链接到 `/s/{cityCode}`，附带每城市资源数

**中栏**：
1. **实时资源流 Tab**（5 个）：实时资源 / 涨幅榜 / 跌幅榜 / 热度榜 / 新发
2. **常驻筛选条**：品类 / 材质 / 产地 / 城市 / 仓库 / 价格区间 + 💾 保存方案 + 订阅（联动价格订阅工具）
3. **资源表 10 行**：分页，每行字段：收藏 ★ / 品名规格 / 材质 / 产地 / 仓库 / 库存 / 现价 / 涨跌 / 趋势 sparkline / 发布时间 / 询价按钮
4. **API**：`GET /api/v1/resource/list?filters...`
5. **询价按钮**：未登录 → 跳登录；登录后 → 弹窗发送询价（写入 `t_inquiry` 表，推送给商家）
6. **涨跌双榜**：`GET /api/v1/index/ranking?type=up|down&limit=8`

**右栏（280px）**：
1. **个人卡**：未登录显示登录入口；登录后显示用户名 + 关注/求购/订阅 数
2. **我的关注**：`GET /api/v1/user/watches?limit=5`
3. **我的求购**：`GET /api/v1/user/purchases?limit=3`
4. **我的预警**：`GET /api/v1/user/alerts?limit=3`，dot 状态指示
5. **最近浏览**：localStorage 维护（不依赖后端）
6. **侧边广告**：从 `t_ad` 表取 `slot = home_right_side` 的有效广告

#### 3.1.4 钢厂出厂价 + 钢厂动态
- **钢厂出厂价表**：`GET /api/v1/mill/prices?category=rebar` 返回 8 家主流钢厂
- **数据来源**：`t_mill_price` 表，由「钢厂价格爬取任务」每日 08:00 爬取钢厂官网公告 + 人工补录
- **品类 Tab**：4 个（rebar / hrc / crc / plate）
- **底部统计卡 4 个**：上调 / 持平 / 下调 / 平均涨幅
- **钢厂动态**：`GET /api/v1/mill/news?limit=6`，4 种 tag（up/down/info/gold = 上调/下调/检修/新品）

#### 3.1.5 钢市热力图（20 城）
- **20 个城市清单**：固定（详见 7.4），每城市 1 个 tile
- **数据**：`GET /api/v1/index/heatmap` 返回各城市综合指数 + 活跃度 + 资源数 + 热力等级
- **热力等级算法**：活跃度评分（询价数 × 0.4 + 发布数 × 0.3 + 浏览量 × 0.3）/ 标准化到 1-5 档

#### 3.1.6 资讯（5 栏目）+ 工具入口（5 卡）
- 资讯：`GET /api/v1/news/list?category=xxx&limit=5`
- 工具入口：静态 5 卡 → 跳工具页

#### 3.1.7 KPI / 友链 / 页脚
- KPI：`GET /api/v1/stats/platform`，缓存 1 小时
- 友链：静态配置
- 页脚：静态 + ICP 备案号

### 3.2 上海分站 (`/s/shanghai`)

> 模板字段化：每个分站除了城市相关字段不同，结构 100% 一致。

#### 3.2.1 与首页的差异
| 区块 | 首页 | 分站 |
|---|---|---|
| 顶部条 | "切换城市" | "上海站 / 切换 / 返回总站" |
| Logo | 货袋子 | 货袋子 + SHANGHAI 标签 |
| Nav | 全国导航 | 上海前缀（上海首页/上海资源/...）+ 钢厂在沪代理 |
| 指数大屏 | 全国 | 本地（上海综合指数）+ 市场分布（宝山/浦东/嘉定/...） |
| 资源流 | 全国资源 | 仅本地资源（按 `city_code=shanghai` 过滤）|
| 钢厂出厂价 | 出厂价 | **钢厂在沪代理价**（多了"上海代理商"+"上海仓库"列）|
| 钢厂动态 | 全国 | 上海/华东本地动态 |
| 热力图 | 全国 20 城 | **长三角 12 城**（详见 7.4.2）|
| 广告位 | 仅右侧 1 个 | **6 个**（①顶部 ②③④右侧 ⑤中部 ⑥商家下方）|
| SEO 文字区 | 无 | **必须有**（核心长尾词 + 周边城市互链）|
| KPI | 全国 | 本地（上海累计成交 / 本地商家 / 本地资源 / 钢厂在沪代理）|

#### 3.2.2 广告位规范
- 数据表 `t_ad`，字段：`slot`（位置）、`city_code`、`start_at`、`end_at`、`image_url`、`title`、`desc`、`link`、`phone`、`status`
- 6 个 slot：`top_banner` / `side_1` / `side_2` / `side_3` / `mid_banner` / `merchant_banner`
- 每个 slot 同一时间仅 1 条有效（按优先级 + 时间）
- 渲染时**必须显示"广告 AD"角标**
- 后台支持：上传图片、HTML 富文本、设置投放城市、按月按位售卖

#### 3.2.3 分站列表
首批 20 个城市（详见 7.4.1）。

### 3.3 4 大工具详情页

#### 3.3.1 智能找钢 `/tools/find`

**核心组件**：
1. **AI 大输入框**：自然语言输入 + 5 种输入方式（文字/语音/Excel/图片/高级筛选）
2. **AI 解析**：调用 `POST /api/v1/ai/parse` 返回识别条件（品类/材质/规格/数量/城市/价格/交付）
   - 第一期可以**规则解析**（正则 + 词典），不强制接 LLM
   - 后续可升级为 LLM（DeepSeek/Qwen/OpenAI），通过配置开关
3. **匹配结果列表**：`POST /api/v1/resource/match` 入参为解析后的条件，返回带匹配度的资源
4. **匹配度评分算法**：
   ```
   score = 品类(20) + 材质(15) + 规格(15) + 价格区间(20) + 库存满足(10) + 城市匹配(15) + 商家信用(5)
   ```
5. **每条卡片操作**：立即询价 / 🔔 订阅价格（联动价格订阅工具）
6. **底部操作**：保存为方案 / 价格订阅 / 一键发求购 / 联系平台找货

**禁止**：不允许出现"+ 加入对比"、"批量比价"、"批量询价"任何字样。

#### 3.3.2 价格订阅 `/tools/subscribe`

**核心组件**：
1. **订阅列表**：`GET /api/v1/user/subscriptions`
   - 每条：品类/规格/钢厂/城市、当前价、涨跌、提醒条件徽章、推送渠道、近 30 日告警次数、价格 sparkline
2. **添加订阅表单**：右侧粘性
   - 关注对象：品类（必）/ 规格 / 钢厂 / 城市
   - 提醒条件（满足任一即推送）：跌破 X / 涨破 Y / 变化幅度 ≥ Z%
   - 推送渠道（多选）：微信 / 钉钉 / 邮件 / 短信
   - 推送时段：工作日 8-20 / 全天 / 仅工作时间
3. **告警时间线**：`GET /api/v1/user/alerts?limit=20`，dot 颜色按方向区分

**后端定时任务**：每 1 分钟扫描 `t_subscription` × 当前价，触发条件命中即写入 `t_alert` 并按渠道推送（微信走企业微信机器人 / 钉钉走 webhook / 短信走阿里云）。

#### 3.3.3 报价雷达 `/tools/radar`

**双视角 Tab**：
- **采购方视角**：我的求购列表 + SVG 雷达扫描图 + 收到的报价表
  - `GET /api/v1/user/purchases` + `GET /api/v1/purchase/{id}/quotes`
  - 雷达扫描图：纯前端 SVG 动画，不依赖后端
- **供应商视角**：匹配我库存的求购列表
  - `GET /api/v1/merchant/matched-purchases`

**核心交互**：
- 发布求购：表单（品名/数量/规格/期望价/交付地/截止时间）→ `POST /api/v1/purchase`
- 立即报价：商家点击 → 弹窗填报价 → `POST /api/v1/purchase/{id}/quote`
- 在线议价：进入 IM 房间（与商家私聊，复用现有 IM 模块）

#### 3.3.4 行情对比 `/tools/compare`

**4 个对比 Tab**：城市对比 / 品类对比 / 钢厂对比 / 时间段对比

**核心组件**：
1. **筛选器**：品类下拉 + 多城市勾选 chip + 时间范围
2. **多线走势图**（ECharts）：每条线末端带城市价格标签
3. **数据表**：当前价/30 日均/最高/最低/日涨跌/30 日涨幅/波动率/价差/趋势 sparkline
4. **热力图**（10 城 × 8 品类）：色阶 6 档，悬停显示精确值

**API**：
- `GET /api/v1/index/compare?type=city&category=rebar&cities=shanghai,tangshan&from=...&to=...`
- 返回各序列的时间点 + 价格数组，前端用 ECharts 绘制

### 3.4 新闻频道（M3 新增）

**栏目结构**（5 个，固定）：
- `interpretation` 行情解读（每日 1-2 篇，自动+人工）
- `policy` 政策法规
- `industry` 行业动态
- `notice` 平台公告
- `knowledge` 钢材知识

**URL 规则**：
- 列表：`/news`、`/news/{category}`（语义化路径）
- 详情：`/news/{id}.html`（带 .html 后缀，百度偏好）

**详情页结构（SEO 核心）**：
```html
<h1>新闻标题</h1>
面包屑：首页 > 钢材资讯 > 行情解读 > 当前标题
正文（h2/h3 小标题，图片 alt 必填）
文末：相关阅读 6 条 + 相关品类链接 + 相关城市分站链接
JSON-LD: NewsArticle
独立 meta description（取正文前 150 字）
```

**配套设施**：
- `news-sitemap.xml`（Google News Sitemap 规范）
- 百度站长平台实时推送 API（发文后自动调一次 `urls.push`）
- RSS：`/rss/news.xml`

---

## 第 4 章 · 数据模型（核心表结构）

> 命名规范：表名 `t_` 前缀；字段全小写下划线；主键 `id BIGINT AUTO_INCREMENT`；逻辑删除 `deleted TINYINT`；审计字段 `created_at` / `updated_at` / `created_by` / `updated_by`。

### 4.1 核心实体清单
- `t_user` 用户
- `t_merchant` 商家
- `t_resource` 资源（卖货挂单）
- `t_purchase` 求购
- `t_quote` 报价（求购的回复）
- `t_inquiry` 询价（资源的询价）
- `t_subscription` 价格订阅
- `t_alert` 告警记录
- `t_watch` 关注
- `t_offer_history` 报价历史（用于聚合行情）
- `t_index_daily` 钢材指数日聚合
- `t_mill_price` 钢厂出厂价 / 在沪代理价
- `t_mill_news` 钢厂动态
- `t_news` 资讯文章
- `t_ad` 广告位
- `t_city` 城市表（含 20 个分站）
- `t_category` 品类表

### 4.2 关键字段（仅列示与设计稿相关的关键字段，完整 DDL 由 DBA 出）

#### t_resource（资源/挂货）
```sql
id BIGINT PK
merchant_id BIGINT FK
category_code VARCHAR(32)        -- rebar/hrc/crc/plate/wire/section/tube/galvanized
material_code VARCHAR(32)        -- HRB400E/Q235B/SPCC/...
spec VARCHAR(64)                 -- φ20×9000
mill_code VARCHAR(32)            -- sha gang / yong gang ...
city_code VARCHAR(32)            -- 城市
warehouse VARCHAR(128)           -- 上海·宝山钢市 A 区
stock_ton DECIMAL(10,2)
price_cent BIGINT                -- 价格（分）
price_change_cent BIGINT         -- 日涨跌（分）
status TINYINT                   -- 0 草稿 / 1 在售 / 2 下架 / 3 售罄
created_at TIMESTAMP
updated_at TIMESTAMP
```

#### t_subscription（价格订阅）
```sql
id BIGINT PK
user_id BIGINT FK
category_code VARCHAR(32)
material_code VARCHAR(32) NULL
spec VARCHAR(64) NULL
mill_code VARCHAR(32) NULL
city_code VARCHAR(32) NULL
cond_below_cent BIGINT NULL      -- 跌破
cond_above_cent BIGINT NULL      -- 涨破
cond_change_pct DECIMAL(5,2) NULL -- 变化幅度
channels VARCHAR(64)             -- 逗号分隔: wechat,dingtalk,sms,email
time_slot VARCHAR(32)            -- workday_8_20 / all_day / work_hour
enabled TINYINT
created_at TIMESTAMP
```

#### t_index_daily（钢材指数日聚合）
```sql
date DATE
scope VARCHAR(32)                -- composite / category_rebar / city_shanghai
value DECIMAL(10,2)
open DECIMAL(10,2)
high DECIMAL(10,2)
low DECIMAL(10,2)
volume_ton DECIMAL(12,2)
PRIMARY KEY (date, scope)
```

#### t_mill_price（钢厂出厂价 / 在沪代理价）
```sql
id BIGINT PK
mill_code VARCHAR(32)
agent_city_code VARCHAR(32) NULL  -- 上海代理仓所在城市；NULL 为出厂价
agent_name VARCHAR(128) NULL
warehouse VARCHAR(128) NULL
category_code VARCHAR(32)
spec VARCHAR(64)
price_cent BIGINT
change_cent BIGINT
status VARCHAR(16)                -- up / flat / down
publish_at TIMESTAMP
source_url VARCHAR(256)
```

#### t_ad（广告位）
```sql
id BIGINT PK
slot VARCHAR(64)                  -- home_right_side / sh_top_banner / sh_side_1 ...
city_code VARCHAR(32) NULL        -- NULL 为全国
priority INT                      -- 数字越大越优先
title VARCHAR(128)
description VARCHAR(256)
image_url VARCHAR(256)
link_url VARCHAR(256)
phone VARCHAR(32)
html_content TEXT                 -- 富文本广告
start_at TIMESTAMP
end_at TIMESTAMP
status TINYINT                    -- 0 待审/1 投放中/2 已下线
```

#### t_news（资讯）
```sql
id BIGINT PK
slug VARCHAR(128) UNIQUE          -- URL slug，例如 sha-gang-up-30-yuan-20260527
category_code VARCHAR(32)         -- interpretation/policy/industry/notice/knowledge
title VARCHAR(255)
summary VARCHAR(500)              -- meta description 用
content LONGTEXT                  -- 富文本
cover_url VARCHAR(256)
author VARCHAR(64)
related_category VARCHAR(32) NULL -- 关联品类
related_city VARCHAR(32) NULL     -- 关联城市
seo_keywords VARCHAR(255)
publish_at TIMESTAMP
view_count BIGINT DEFAULT 0
hot TINYINT DEFAULT 0
status TINYINT
```

### 4.3 索引规范
- 所有 `t_resource` 的查询索引：`(status, city_code, category_code, created_at)`, `(category_code, price_cent)`, `(merchant_id)`
- `t_index_daily`：`(scope, date)`
- `t_mill_price`：`(category_code, publish_at)`, `(mill_code, agent_city_code)`
- `t_subscription`：`(user_id)`, `(enabled, category_code)`
- `t_news`：`(status, publish_at)`, `(category_code, publish_at)`, `slug UNIQUE`

---

## 第 5 章 · API 接口规范

### 5.1 通用约定
- 所有 API 前缀：`/api/v1/`
- 响应格式：
  ```json
  {
    "code": 0,
    "message": "ok",
    "data": { ... },
    "trace_id": "xxx"
  }
  ```
- 错误码：`0` 成功；`4xxx` 业务错；`5xxx` 系统错；详见 `docs/error-codes.md`
- 鉴权：`Authorization: Bearer <jwt>`
- 限流：默认每用户 60 req/min，敏感接口（询价/报价/订阅）30 req/min
- 必须用 springdoc-openapi 注解，自动生成 OpenAPI 3.0 文档

### 5.2 接口清单（精简版）

| 模块 | Method | Path | 说明 |
|---|---|---|---|
| 行情 | GET | `/api/v1/index/today` | 首页指数大屏数据 |
| 行情 | GET | `/api/v1/index/ticker` | 滚动 ticker 数据 |
| 行情 | GET | `/api/v1/index/kline?scope=composite&period=daily&limit=60` | K 线 |
| 行情 | GET | `/api/v1/index/ranking?type=up|down&scope=national|shanghai&limit=8` | 涨跌榜 |
| 行情 | GET | `/api/v1/index/heatmap?scope=national|yangtze` | 热力图 |
| 行情 | GET | `/api/v1/index/compare?type=city|category|mill|time&...` | 行情对比 |
| 资源 | GET | `/api/v1/resource/list?cat=&mat=&mill=&city=&priceFrom=&priceTo=&page=&size=` | 资源列表 |
| 资源 | GET | `/api/v1/resource/{id}` | 资源详情 |
| 资源 | POST | `/api/v1/resource/match` | 智能匹配 |
| 资源 | POST | `/api/v1/inquiry` | 发起询价 |
| 求购 | GET | `/api/v1/purchase/list` | 求购列表 |
| 求购 | POST | `/api/v1/purchase` | 发布求购 |
| 求购 | GET | `/api/v1/purchase/{id}/quotes` | 求购的报价列表 |
| 求购 | POST | `/api/v1/purchase/{id}/quote` | 报价 |
| 商家 | GET | `/api/v1/merchant/list` | 商家列表 |
| 商家 | GET | `/api/v1/merchant/{id}` | 商家详情 |
| 钢厂 | GET | `/api/v1/mill/prices?category=rebar&scope=national|shanghai` | 出厂价/代理价 |
| 钢厂 | GET | `/api/v1/mill/news?limit=6` | 钢厂动态 |
| 订阅 | GET | `/api/v1/user/subscriptions` | 我的订阅 |
| 订阅 | POST | `/api/v1/user/subscriptions` | 新增订阅 |
| 订阅 | DELETE | `/api/v1/user/subscriptions/{id}` | 取消订阅 |
| 告警 | GET | `/api/v1/user/alerts` | 告警列表 |
| 关注 | GET/POST/DELETE | `/api/v1/user/watches` | 关注 |
| AI | POST | `/api/v1/ai/parse` | 自然语言解析 |
| 资讯 | GET | `/api/v1/news/list?category=` | 资讯列表 |
| 资讯 | GET | `/api/v1/news/{id}` | 资讯详情 |
| 广告 | GET | `/api/v1/ad?slot=&city=` | 取广告（按位 + 城市） |
| 平台 | GET | `/api/v1/stats/platform` | KPI 数据 |

### 5.3 缓存策略
| 接口 | TTL | 说明 |
|---|---|---|
| `/api/v1/index/today` | 60s | 大屏数据 |
| `/api/v1/index/ticker` | 30s | 滚动行情 |
| `/api/v1/index/heatmap` | 5min | 热力图 |
| `/api/v1/mill/prices` | 5min | 出厂价 |
| `/api/v1/resource/list` | 不缓存 | 实时性强 |
| `/api/v1/news/list` | 5min | 资讯列表 |
| `/api/v1/stats/platform` | 1h | KPI |
| `/api/v1/ad` | 5min | 广告 |

---

## 第 6 章 · SEO 渲染架构

### 6.1 总体架构
```
                 ┌─────────────────────────────────────────────┐
   爬虫 UA       │  Nginx 入口（按 UA + Path 分流）             │
 (Baiduspider,  │   ├─ 命中爬虫白名单 → 转 Java SEO 着陆页       │
  Googlebot,    │   ├─ 普通用户 → Vue3 SPA                     │
  GPTBot, etc.) │   ├─ /sitemap*.xml /robots.txt → 静态文件     │
                 └─────────────────────────────────────────────┘
```

### 6.2 Nginx UA 分流（关键配置）

```nginx
map $http_user_agent $is_bot {
    default 0;
    ~*(Baiduspider|Sogou|YisouSpider|360Spider|HaoSouSpider|Bytespider|toutiao|Googlebot|bingbot|YandexBot|DuckDuckBot|GPTBot|ClaudeBot|PerplexityBot|OAI-SearchBot|Applebot) 1;
}

server {
    listen 80;
    server_name www.huodaizi.com huodaizi.com;

    location = /robots.txt { alias /var/www/robots.txt; }
    location ~ ^/sitemap.*\.xml$ { alias /var/www/$uri; }

    location / {
        if ($is_bot) {
            proxy_pass http://java-seo-backend;
            break;
        }
        proxy_pass http://vue-spa-frontend;
    }

    # 301 老路径
    location = /fullscreen { return 301 /; }
    location = /fullscreen/about { return 301 /about; }
    location = /fullscreen-classic { return 301 /; }
}
```

### 6.3 Java SEO 着陆页（Thymeleaf）

**控制器结构**：
```
src/main/java/com/huodaizi/seo/
├── controller/
│   ├── HomeSeoController.java          GET /
│   ├── CategorySeoController.java      GET /resource[/{cat}]
│   ├── ResourceSeoController.java      GET /resource/{slug}.html
│   ├── ShopSeoController.java          GET /shop/{id}
│   ├── SubstationSeoController.java    GET /s/{cityCode}
│   ├── NewsSeoController.java          GET /news[/{cat}], /news/{id}.html
│   └── AboutSeoController.java         GET /about, /help, /join
├── service/
│   └── SeoDataService.java             查询数据 + 拼装 SEO Model
└── template/  → src/main/resources/templates/seo/
    ├── home.html
    ├── category.html
    ├── resource-detail.html
    ├── shop.html
    ├── substation.html
    ├── news-list.html
    └── news-detail.html
```

**关键模板要求**：每个模板**必须**包含：
1. `<title>` 含核心关键词，长度 ≤ 60 字
2. `<meta name="description">` 长度 ≤ 160 字
3. `<meta name="keywords">` 5-10 个关键词
4. `<link rel="canonical">` 指向规范 URL
5. `<meta property="og:*">` Open Graph 完整
6. JSON-LD（详见 6.5）
7. **正文必须直出**：所有数据由后端渲染，不依赖 JS
8. **内链必须**：每个页面 ≥ 30 个 `<a>` 业务链接
9. **图片必须**：所有 `<img>` 必填 `alt` + `width` + `height`

### 6.4 sitemap 生成

**结构**：
```
/sitemap.xml             ← 索引文件
├── /sitemap-static.xml      首页/关于/帮助 等静态页
├── /sitemap-resource.xml    资源详情页（分片，每 5 万条一个）
├── /sitemap-shop.xml        商家详情页
├── /sitemap-site.xml        20 个分站
├── /sitemap-news.xml        资讯（Google News Sitemap 规范）
└── /sitemap-category.xml    品类/材质组合页
```

**实现**：
- Java 定时任务每日 02:00 重新生成
- 每个 sitemap-*.xml ≤ 50,000 URLs / 50 MB
- 新增资讯/资源时通过 RocketMQ 异步追加
- 同时调用百度推送 API（urls.push）+ Bing IndexNow

### 6.5 JSON-LD 结构化数据

**最少包含**：
- 全站：`Organization` + `WebSite`（带 `SearchAction`）
- 资源详情：`Product` + `Offer`（价格/产地/规格）
- 商家详情：`LocalBusiness`
- 资讯详情：`NewsArticle`
- 任何列表/详情：`BreadcrumbList`

**示例（资讯详情）**：
```html
<script type="application/ld+json">
{
  "@context": "https://schema.org",
  "@type": "NewsArticle",
  "headline": "今日螺纹钢现货价格小幅上涨 沙钢出厂上调 30 元",
  "datePublished": "2026-05-27T09:12:00+08:00",
  "author": {"@type": "Organization", "name": "货袋子"},
  "publisher": {"@type": "Organization", "name": "货袋子", "logo": {...}},
  "image": "...",
  "mainEntityOfPage": {"@type": "WebPage", "@id": "https://www.huodaizi.com/news/xxx.html"}
}
</script>
```

### 6.6 robots.txt 与站长平台

**robots.txt**：
```
User-agent: *
Allow: /
Allow: /s/
Allow: /resource/
Allow: /shop/
Allow: /news/
Disallow: /login
Disallow: /auth_redirect
Disallow: /home/
Disallow: /api/
Disallow: /fullscreen-classic

Sitemap: https://www.huodaizi.com/sitemap.xml
```

**站长平台对接**：
- 百度普通收录 API：发布资讯/资源时调用
- 百度快速收录 API（如已开通）
- Bing IndexNow
- Google Search Console（仅验证，无主动推送 API）

---

## 第 7 章 · URL 规则与 301 映射

### 7.1 新 URL 规则

| 类型 | URL 模板 | 示例 |
|---|---|---|
| 首页 | `/` | `/` |
| 找钢材列表 | `/resource[/{cat}]` | `/resource`, `/resource/rebar` |
| 资源详情 | `/resource/{slug}.html` | `/resource/rebar-hrb400e-d20-shanghai-202605.html` |
| 找求购列表 | `/purchase[/{cat}]` | `/purchase` |
| 求购详情 | `/purchase/{id}.html` | `/purchase/20260527001.html` |
| 商家列表 | `/shops[/{city}]` | `/shops`, `/shops/shanghai` |
| 商家详情 | `/shop/{id}` | `/shop/12345` |
| 行情中心 | `/quote[/{cat}]` | `/quote`, `/quote/rebar` |
| 钢厂出厂价 | `/mill[/{millCode}]` | `/mill`, `/mill/shagang` |
| 分站 | `/s/{cityCode}` | `/s/shanghai` |
| 资讯列表 | `/news[/{cat}]` | `/news`, `/news/interpretation` |
| 资讯详情 | `/news/{id}.html` | `/news/20260527001.html` |
| 工具 | `/tools/{tool}` | `/tools/find`, `/tools/subscribe`, `/tools/radar`, `/tools/compare` |
| 关于 | `/about` | |
| 帮助 | `/help` | |
| 入驻 | `/join` | |
| 联系 | `/contact` | |

**规则**：
- 全小写
- 中划线分词
- 详情页用 `.html` 后缀（百度偏好）
- slug 含核心关键词（品类/材质/规格/城市/月份）
- 每个页面 `<link rel="canonical">` 指向规范 URL

### 7.2 老 URL 301 映射（必须做）

| 老路径 | 新路径 |
|---|---|
| `/fullscreen` | `/` |
| `/fullscreen/about` | `/about` |
| `/fullscreen/help` | `/help` |
| `/fullscreen-classic` | `/` |
| `/home/*`（旧后台路径） | 301 → `/` （或 410） |

301 在 Nginx 层做，不走应用层。

### 7.3 内链规范
- 首页底部友链 + 全国分站瓷砖（喂爬虫）
- 分站底部周边城市互链（喂爬虫）
- 资源详情底部相关资源 6 条
- 资讯详情底部相关阅读 6 条 + 相关品类 + 相关分站

### 7.4 城市清单

#### 7.4.1 全国 20 个分站（首页热力图）
唐山 / 邯郸 / 张家港 / 无锡 / 莱芜 / 日照 / 鞍山 / 本溪 / 包头 / 太原 / 马鞍山 / 武汉 / 重庆 / 攀枝花 / 长沙 / 上海 / 天津 / 北京 / 广州 / 杭州

#### 7.4.2 长三角 12 城（上海分站热力图）
上海 / 苏州 / 无锡 / 张家港 / 南通 / 常州 / 南京 / 杭州 / 嘉兴 / 宁波 / 绍兴 / 合肥

---

## 第 8 章 · 前端架构

### 8.1 项目目录
```
frontend/
├── src/
│   ├── api/                  axios 实例 + 接口模块
│   ├── assets/               静态资源
│   ├── components/           通用组件
│   │   ├── IndexDashboard/   钢材指数大屏
│   │   ├── ResourceStream/   资源流表
│   │   ├── KLineChart/       K 线（ECharts 封装）
│   │   ├── HeatMap/          热力图
│   │   ├── RadarSweep/       报价雷达
│   │   ├── PriceSparkline/   迷你折线
│   │   ├── AdSlot/           广告位
│   │   └── ...
│   ├── views/                页面
│   │   ├── home/             首页
│   │   ├── substation/       分站
│   │   ├── resource/         资源
│   │   ├── tools/
│   │   │   ├── find/         智能找钢
│   │   │   ├── subscribe/    价格订阅
│   │   │   ├── radar/        报价雷达
│   │   │   └── compare/      行情对比
│   │   ├── news/             资讯
│   │   └── ...
│   ├── router/               vue-router
│   ├── stores/               Pinia
│   ├── styles/               全局样式 + 主题（橙色变量）
│   ├── utils/                工具
│   ├── App.vue
│   └── main.ts
├── vite.config.ts
└── package.json
```

### 8.2 组件命名
- 组件文件：`PascalCase.vue`（如 `ResourceStream.vue`）
- 组件名（template 中）：`<resource-stream>` kebab-case
- 单文件组件 `<script setup lang="ts">`，组合式 API
- Props/Emits 必须显式 TypeScript 类型化

### 8.3 状态管理（Pinia）
- 一个 view 一个 store（如 `useHomeStore` / `useFindStore`）
- 全局 store：`useUserStore` / `useCityStore` / `useThemeStore`
- 异步用 `async/await`；不允许在 setup 顶层 await 阻塞渲染

### 8.4 路由
- 公开页（首页/资源/分站/资讯/工具/关于）走 vue-router 直接渲染
- 工作台/订单/账户 等私有页通过 wujie 加载已有子应用（`/workspace/*`）
- 路由级代码分割：`() => import('./HomeView.vue')`

### 8.5 API 模块
```typescript
// src/api/index.ts
import axios from 'axios'
export const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE,
  timeout: 10000
})
http.interceptors.response.use(
  res => res.data.code === 0 ? res.data.data : Promise.reject(res.data),
  err => Promise.reject(err)
)

// src/api/resource.ts
export const listResource = (params: ListResourceParams) =>
  http.get<ResourceListVO>('/api/v1/resource/list', { params })
```

- 所有接口类型由 OpenAPI Generator 从后端 swagger 生成 → `src/api/__generated__/`
- 禁止手写 `any`

### 8.6 wujie 微前端边界（重要）

| 路径 | 模式 | 说明 |
|---|---|---|
| `/`, `/resource/*`, `/news/*`, `/s/*`, `/about` 等**公开页** | **本地直渲（无 wujie）** | SEO 必须，wujie 内不可索引 |
| `/tools/*` | **本地直渲** | SEO 同样必要 |
| `/workspace/*`, `/orders/*`, `/account/*` 等**登录后** | **wujie 加载子应用** | 后台业务保留现状 |

### 8.7 ECharts 统一封装
- 在 `components/charts/` 下封装 `<KLine>`, `<MultiLine>`, `<HeatGrid>`, `<MiniSparkline>`
- 颜色主题：红涨 `#E63946` / 绿跌 `#10B981` / 持平 `#9CA3AF` / 主橙 `#FF6A00`
- 字体：`DIN, Helvetica Neue, Arial, sans-serif`

---

## 第 9 章 · 后端架构

### 9.1 Maven 模块
```
huodaizi/
├── pom.xml                   (parent)
├── api/                      DTO + OpenAPI
├── common/                   工具/常量/异常
├── domain/                   领域实体 + Repository
├── service/                  业务服务
├── web/                      Spring MVC Controllers (REST API)
├── seo/                      Thymeleaf SEO 着陆页 Controllers
├── task/                     定时任务（爬虫/聚合/推送）
└── starter/                  Spring Boot 主入口
```

### 9.2 分层架构（强约束）
- `controller` 仅负责参数校验、调用 service、返回 VO
- `service` 业务编排 + 事务
- `repository`/`mapper` 仅 SQL
- `domain` 实体 + 业务方法（充血模型，禁止贫血）
- DTO 严格分层：`XxxRequestDTO` / `XxxResponseVO` / `XxxEntity` 三套对象，用 MapStruct 转换

### 9.3 异常处理
- 业务异常：`BusinessException(code, message)`，code 走错误码表
- 系统异常：`@RestControllerAdvice` 统一捕获，返回 `5000`
- 严禁 `throws Exception` 一把抓
- 严禁 `catch(Exception e) { e.printStackTrace(); }`

### 9.4 日志规范
- SLF4J 占位符：`log.info("user {} login from {}", userId, ip)`，**禁止字符串拼接**
- ERROR 必须带堆栈
- 业务关键路径加 INFO（订单/订阅创建/告警触发）
- 高频路径用 DEBUG，生产关闭

### 9.5 鉴权
- JWT（HS256），Token 含 `userId / role / exp`
- 公开页（SEO + 列表）允许匿名
- 写操作（询价/报价/订阅）必须登录
- 商家后台另起一套 RBAC，按 `role + permission` 控制

### 9.6 定时任务
| 任务 | 频率 | 说明 |
|---|---|---|
| 钢厂价格爬取 | 08:00 daily | 爬取钢厂官网公告，写入 `t_mill_price` |
| 钢材指数聚合 | 每整点 | 从 `t_offer_history` 聚合到 `t_index_daily` |
| sitemap 重生 | 02:00 daily | 全量重生 |
| 百度推送 | 实时 | 资讯/资源发布时触发 |
| 价格订阅扫描 | 每 1 分钟 | 命中条件即推送 |
| 资讯热度更新 | 每 15 分钟 | 浏览量聚合 |

---

## 第 10 章 · 代码规范

### 10.1 Java 规范
- **代码风格**：Google Java Style（4 空格缩进）
- **检查工具**：CheckStyle + SpotBugs + Sonar
- **包命名**：`com.huodaizi.<module>.<layer>`，例 `com.huodaizi.resource.service`
- **类命名**：
  - Entity：`XxxEntity` / `Xxx`
  - DTO：`XxxRequestDTO` / `XxxResponseVO`
  - Service：`XxxService` (接口) + `XxxServiceImpl`
  - Controller：`XxxController` / `XxxSeoController`
  - Mapper：`XxxMapper`
- **方法命名**：动宾，例 `findById`, `createOrder`, `pushAlert`
- **常量**：`UPPER_SNAKE_CASE`
- **不允许**：`System.out.println`、`e.printStackTrace`、未使用的 import、TODO 不带责任人

### 10.2 TypeScript / Vue 规范
- **代码风格**：ESLint + Prettier，禁止个人配置覆盖
- **TS 严格模式**：`strict: true` + `noImplicitAny: true`
- **组件名**：`PascalCase.vue`
- **变量**：`camelCase`，常量 `UPPER_SNAKE_CASE`
- **不允许**：`any`、未使用的 import、`console.log` 提交（除 dev 环境）

### 10.3 CSS 规范
- 采用 BEM 命名（已有遗留代码可保留），新组件用 `<style scoped>`
- 颜色变量统一从 `src/styles/_vars.scss` 读取：
  ```scss
  $c-primary: #FF6A00;
  $c-primary-dark: #E55A00;
  $c-primary-bg: #FFF4EC;
  $c-up: #E63946;   // 红涨
  $c-down: #10B981; // 绿跌
  $c-text: #1F2937;
  $c-text-2: #6B7280;
  $c-line: #E5E7EB;
  ```
- 禁用 `!important`（特殊情况注释说明）
- 禁止内联 `style="..."`（设计稿 HTML 中存在的内联仅用于 mockup，正式代码必须迁移到 SCSS）

### 10.4 Git 规范
- **分支**：`main` / `develop` / `feature/xxx` / `hotfix/xxx`
- **Commit Message**：Conventional Commits
  - `feat: 添加智能找钢 AI 解析接口`
  - `fix(seo): 修复分站 canonical 错误`
  - `refactor:`、`test:`、`docs:`、`chore:`
- **MR/PR**：必须 1+ Reviewer，必须 CI 全绿，必须关联 Issue
- **禁止 push 到 main**：通过 PR 合并

### 10.5 注释规范
- **类**：JavaDoc 写「干什么」+「负责人」+「关键约束」
- **方法**：参数 + 返回 + 异常 + 业务约束
- **不要**写"什么是什么"的废话注释（如 `// 这是用户 ID`）
- **业务约束/坑点**：必须注释

---

## 第 11 章 · 测试策略

### 11.1 后端
- **单元测试**：所有 Service 方法（覆盖率 ≥ 70%）
- **集成测试**：每个 Controller 用 MockMvc 或 RestAssured（覆盖率 ≥ 60%）
- **数据库测试**：Testcontainers + MySQL/Redis
- **SEO 模板测试**：每个 SeoController 至少 1 个测试，断言 title/description/canonical/JSON-LD 字段存在

### 11.2 前端
- **单元测试**：utils 函数 + composables（覆盖率 ≥ 70%）
- **组件测试**：核心组件（IndexDashboard / ResourceStream / KLineChart）
- **E2E**：Playwright 跑关键 5 路：
  1. 首页加载 → 资源流出现
  2. 搜索 → 列表页 → 资源详情
  3. 智能找钢 → AI 解析 → 询价
  4. 订阅工具 → 添加订阅
  5. 分站切换上海 → 本地数据正确

### 11.3 SEO 抓取测试
- 每次部署前用 curl + Baiduspider UA 抓取 10 个核心页面，断言 HTML 直出
- 用 Lighthouse 跑首页/分站，SEO 分数 ≥ 90，性能 ≥ 80

---

## 第 12 章 · 性能预算

### 12.1 首屏（LCP）
- 首页 / 分站 LCP < 2.5s（4G 弱网）
- 工具页 LCP < 3s

### 12.2 资源体积（gzip 后）
- 首页 JS < 200 KB（仅 Vue + Pinia + Router + ECharts core + 业务）
- 首屏 CSS < 50 KB
- ECharts 按需引入（仅 line/bar/heatmap，不全量打包）
- ag-grid / vxe-table / wujie 子应用**禁止**出现在公开页 chunk

### 12.3 图片
- 全部 WebP + lazyload + 宽高占位
- 主图 < 200 KB

### 12.4 缓存
- 静态资源 CDN + 1 年强缓存（带 hash）
- HTML 不缓存（爬虫页可加 1h CDN）
- API：按 5.3 节缓存策略

---

## 第 13 章 · 安全与合规

### 13.1 安全
- HTTPS 全站，HSTS 开启
- 防 SQL 注入：MyBatis 参数化（禁 `${}` 拼接）
- 防 XSS：Vue 默认转义，注入 HTML（资讯富文本）必须 DOMPurify 过滤
- 防 CSRF：JWT + SameSite Cookie
- 限流：Sentinel 或 Redis 计数
- 敏感信息：商家手机号脱敏（前 3 + 后 4），公开页禁止暴露

### 13.2 合规
- ICP 备案号必须页脚展示
- 公安备案号必须页脚展示
- 用户协议 / 隐私政策必须可访问
- Cookie 同意条 ≥ EU GDPR / 国内《个保法》要求

---

## 第 14 章 · 里程碑（M1 - M5）

> 不估计日历时间，按交付里程碑组织：

### M1 · SEO 抓取改造（关键路径）
- [ ] Nginx UA 分流配置
- [ ] Java + Thymeleaf SEO 着陆页（7 类：首页/品类/资源详情/商家/分站/资讯列表/资讯详情）
- [ ] sitemap 索引 + 5 个 sitemap-*.xml 生成器
- [ ] robots.txt 更新 + 部署
- [ ] JSON-LD 结构化数据全覆盖
- [ ] 百度站长平台接入 + 主动推送 API
- [ ] Bing Webmaster / Google Search Console 接入
- [ ] 老 URL 301 全部接入

### M2 · 首页 + 分站 + 工具页改版
- [ ] Vue3 改版首页（按 v2 设计稿）
- [ ] 20 个分站模板上线（以上海分站为基准）
- [ ] 4 个工具页（智能找钢 / 价格订阅 / 报价雷达 / 行情对比）
- [ ] 6 个广告位后台 + 投放管理
- [ ] URL 重构 + canonical
- [ ] ECharts 封装（K线/多线/热力图/sparkline/雷达）

### M3 · 新闻频道
- [ ] `t_news` + 编辑后台
- [ ] 5 栏目列表 + 详情页
- [ ] news-sitemap + RSS
- [ ] 发文自动推送百度
- [ ] 资讯 JSON-LD（NewsArticle）

### M4 · 性能 & 微前端边界
- [ ] 公开页移出 wujie，仅工作台保留
- [ ] 按需加载重型组件（ag-grid/vxe-table 仅工作台）
- [ ] 图片 WebP + lazyload + CDN
- [ ] Lighthouse SEO ≥ 90、性能 ≥ 80
- [ ] 性能监控（SkyWalking）

### M5 · 数据观测
- [ ] 百度索引量 / 关键词排名 / 抓取频次周报
- [ ] 用户行为埋点（搜索 / 询价 / 订阅 / 报价雷达）
- [ ] 业务大盘（成交吨数 / 商家活跃 / 资讯阅读 / 工具使用 PV）

---

## 第 15 章 · 验收清单（DoD - Definition of Done）

每个里程碑必须满足以下全部条件才可视为完成：

### 通用
- [ ] 代码通过 CI（lint + 单测 + 集成测试 + Lighthouse）
- [ ] PR 至少 1 名 Reviewer Approve
- [ ] 涉及表结构变更已通过 DBA 评审
- [ ] 涉及 API 变更已更新 OpenAPI 文档
- [ ] 涉及 SEO 的变更已用 Baiduspider UA curl 验证 HTML 直出

### 首页 / 分站
- [ ] 视觉与设计稿一致（误差 ≤ 2px）
- [ ] 主要数据从真实 API 取，非 Mock
- [ ] LCP < 2.5s（4G）
- [ ] HTML 直出含完整 SEO meta + JSON-LD + ≥ 30 个 `<a>`
- [ ] 移动端响应式正常（≥ 1280px 适配 100%，移动端可后续阶段）

### 工具页
- [ ] 4 个工具页全部可访问 + 真实 API 调用
- [ ] 智能找钢的 AI 解析至少规则版可用
- [ ] 价格订阅可成功创建 + 触发告警 + 微信/短信推送
- [ ] 报价雷达可发布求购 + 收到报价 + 议价跳转 IM
- [ ] 行情对比能切换 4 个 Tab + 多线图正确

### 资讯频道
- [ ] 5 栏目全部上线
- [ ] 详情页结构化数据（NewsArticle）正确
- [ ] news-sitemap 接入百度 + Bing
- [ ] RSS 可访问 `/rss/news.xml`

### SEO
- [ ] robots.txt + sitemap.xml 可访问
- [ ] 站长平台抓取诊断通过
- [ ] 首页/分站/资源详情/资讯详情 在百度搜索"site:huodaizi.com" 可见（至少 100 条）

---

## 第 16 章 · 给 AI 编程助手的使用建议

如果用 Cursor/Claude 等 AI 编程助手实现：

1. **每次只让 AI 实现一个模块**（如"实现首页钢材指数大屏组件"），不要让它一次实现整个首页。
2. **每次都附带本文档**作为 system prompt：「严格遵守 `docs/development-prompt.md` 的全部约束」。
3. **每次都让 AI 列出它将创建/修改的文件清单**，确认无误再让它生成代码。
4. **每个组件实现完成后**：
   - 让 AI 写对应的单测
   - 让 AI 写 JSDoc/JavaDoc
   - 让 AI 用 `grep` 检查是否引入了禁用依赖（如 jQuery / moment）
5. **数据库变更**：让 AI 先生成 SQL DDL → 人工 DBA Review → 再写 MyBatis Mapper
6. **API 实现**：先让 AI 写 OpenAPI yaml → 用 OpenAPI Generator 生成 TypeScript 类型 → 再让 AI 实现前后端
7. **设计稿映射**：让 AI 打开 `designs/mockup-home-v2.html` 对照实现，**禁止 AI 自己脑补设计**

### 推荐的提示词模板

```
你是一名遵循《货袋子 V2 开发提示词》的全栈工程师。

任务：实现 [模块名]，对应设计稿区块：[mockup-xxx.html 的具体位置]
约束：
- 严格遵守 docs/development-prompt.md 全部条款
- 后端用 Java 17 + Spring Boot 3.2 + MyBatis-Plus
- 前端用 Vue 3.4 + TypeScript + Pinia + ECharts
- 颜色变量 / 命名 / 注释 / 测试 全部按文档第 10/11 章
- 禁止引入未列出的依赖
- 禁止在公开页加载 wujie / ag-grid / vxe-table

输出：
1. 文件清单（新增 / 修改）
2. 完整代码
3. 单元测试
4. 如何运行验证
5. 关键决策的注释
```

---

## 附录 A · 错误码表（节选）
| Code | 含义 |
|---|---|
| 0 | 成功 |
| 4001 | 参数错误 |
| 4002 | 未登录 |
| 4003 | 无权限 |
| 4101 | 资源不存在 |
| 4102 | 资源已下架 |
| 4201 | 订阅条件冲突 |
| 4301 | 求购已关闭 |
| 4302 | 已报过价 |
| 4401 | AI 解析失败 |
| 5000 | 系统错误 |
| 5001 | 数据库错误 |
| 5002 | 缓存错误 |
| 5101 | 第三方推送失败 |

## 附录 B · 关键依赖版本锁定

> 详见 `pom.xml` 与 `package.json`，CI 检查锁定文件 hash。

## 附录 C · 关键 Schema 文件
- 数据库 DDL：`docs/db/schema.sql`（DBA 出）
- OpenAPI yaml：`docs/api/openapi.yaml`（后端出，前端从此生成 TS 类型）
- 错误码表：`docs/error-codes.md`

---

**文档版本**：v1.0 · 2026-05-27  
**维护人**：架构组  
**反馈渠道**：在 PR 评论中 @架构组，或 Issue 标签 `dev-brief`
