# 货袋子 V2 · 分模块 AI 编程提示词（可直接复制使用）

> 使用方式：把每个 prompt 块直接复制到 Cursor / Claude / Copilot Chat 等 AI 编程助手中。  
> 前置：先把 `docs/development-prompt.md` 全文作为 system prompt 加载，或要求 AI 先阅读它。

---

## 通用前置（每个 prompt 开头都加）

```
你是一名严格遵循《docs/development-prompt.md》的全栈工程师。
开始前请：
1. 完整阅读 docs/development-prompt.md
2. 完整阅读对应设计稿（designs/mockup-*.html）
3. 列出你将创建/修改的文件清单，等我确认后再写代码

技术栈：Java 17 + Spring Boot 3.2 + MyBatis-Plus + MySQL + Redis + Vue 3.4 + TypeScript + Pinia + ECharts
配色：红涨 #E63946 / 绿跌 #10B981 / 主橙 #FF6A00
约束：禁止 wujie/ag-grid/vxe-table 出现在公开页 chunk；禁止 any/console.log/printStackTrace；
      所有金额按"分"BIGINT 存储；所有 API 必须有 OpenAPI 注解。
```

---

## 🎯 模块 1 · 钢材指数大屏（首页顶部深色区）

```
任务：实现首页顶部"钢材指数大屏"组件

设计稿：designs/mockup-home-v2.html 的 .ticker 区块（深色，7 个 cell + 滚动 ticker）

请实现：

【后端】
1. t_index_daily 表：date / scope / value / open / high / low / volume_ton，主键(date, scope)
2. GET /api/v1/index/today 返回大屏数据（综合 + 4 品类 + 涨跌分布 + 市场速报）
3. GET /api/v1/index/ticker 返回滚动行情（近 60 秒 10 条）
4. Service 缓存：today 60s，ticker 30s（Redis）
5. 单元测试覆盖 Service

【前端】
1. components/IndexDashboard/IndexDashboard.vue
2. composables/useIndexDashboard.ts（封装 polling 30s）
3. 子组件：
   - IndexMainCell.vue（综合指数 + sparkline）
   - IndexCategoryCell.vue（品类指数 + mini sparkline）
   - IndexDistributionCell.vue（涨跌分布比例条）
   - IndexStatsCell.vue（市场速报列表）
   - IndexTicker.vue（滚动行情）
4. sparkline 用 ECharts，主题色按设计稿
5. 单元测试覆盖关键 props + 数据更新

【输出】
- 文件清单
- 完整代码（含 JavaDoc/JSDoc）
- 单元测试
- 跑通方式
```

---

## ⚡ 模块 2 · 资源流（首页中栏主表）

```
任务：实现首页"实时资源流"组件

设计稿：designs/mockup-home-v2.html 的 .wb-main .panel（含 5 个 Tab + 筛选条 + 10 行表）

请实现：

【后端】
1. t_resource 表（按 docs/development-prompt.md 第 4.2 节）
2. GET /api/v1/resource/list 支持筛选：cat/mat/mill/city/wh/priceFrom/priceTo/sort/page/size
3. ES 索引：按 (category_code, city_code, status, price_cent) 建索引
4. 接口必须支持分页（Page<ResourceVO>）
5. 询价接口 POST /api/v1/inquiry，写入 t_inquiry + 推送 IM 消息

【前端】
1. components/ResourceStream/ResourceStream.vue（含 5 个 Tab）
2. 筛选条：6 个下拉 + 价格区间 + "保存方案 + 订阅"按钮
3. 资源表 10 行：★收藏/品名规格/材质/产地/仓库/库存/现价/涨跌/趋势 sparkline/发布/询价按钮
4. 询价按钮：未登录跳登录，登录后弹窗
5. trend sparkline 用 ECharts mini line
6. 单测：筛选条件改变触发 API 调用、收藏切换、点击询价
```

---

## 🏭 模块 3 · 钢厂出厂价快报

```
任务：实现"主流钢厂出厂价快报"区块

设计稿：designs/mockup-home-v2.html 的 .mill-pn（含 4 品类 Tab + 7 列表格 + 4 卡统计）

请实现：

【后端】
1. t_mill_price 表
2. GET /api/v1/mill/prices?category=rebar&scope=national
3. GET /api/v1/mill/news?limit=6（钢厂动态）
4. 定时任务 MillPriceCrawlTask：每日 08:00 爬取 8 家钢厂官网公告
   - 公告解析失败必须告警（钉钉）
   - 每家钢厂独立的 Crawler 实现（Strategy 模式）
5. 缓存：prices 5min（按 category），news 5min

【前端】
1. components/MillPrices/MillPrices.vue
2. 4 品类 Tab 切换：rebar/hrc/crc/plate
3. 7 列表格：钢厂/产地/规格/出厂价/日变化/状态徽章/时间/公告链接
4. 底部 4 卡：上调/持平/下调/平均涨幅，数字滚动效果（vueuse 的 useTransition）
5. 上海分站版本：传 scope=shanghai，多展示"上海代理商"+"上海仓库"列

【特别注意】
钢厂动态时间线列表的 4 种 tag（up/down/info/gold）颜色按设计稿。
```

---

## 🗺️ 模块 4 · 钢市热力图（20 城）

```
任务：实现"钢市热力图"组件

设计稿：designs/mockup-home-v2.html 的 .heatmap-wrap
分站版：designs/mockup-shanghai.html 的 12 城长三角版本

请实现：

【后端】
1. GET /api/v1/index/heatmap?scope=national|yangtze
2. 返回各城市：cityCode/name/cnLabel/badge/priceCurrent/priceChange/heatLevel(1-5)/活跃度/资源数
3. heatLevel 计算算法：
   score = inquiry_count*0.4 + new_resource_count*0.3 + pv*0.3
   按 score 归一化到 1-5 档
4. 缓存 5min

【前端】
1. components/HeatMap/HeatMap.vue
2. 接收 cities props（数组），grid-template-columns 自适应（5 列 / 6 列）
3. tile hover 上浮 + shadow
4. 每个 tile：城市名 + badge（钢都/沙钢/华东 等）+ 当前价 + 涨跌符号 + 活跃度 + 资源数
5. 5 档色阶：浅橙 → 深橙
6. 全国 20 城清单与长三角 12 城清单写在 const，避免散落
```

---

## 🎯 模块 5 · 智能找钢工具

```
任务：实现智能找钢工具页 /tools/find

设计稿：designs/mockup-tools.html 的 #tool-1 智能找钢章节

【后端】
1. POST /api/v1/ai/parse 入参 { text: string }，返回结构化条件
   - 第一期用规则解析：正则 + 词典（品类/材质/规格/数量/价格/城市）
   - 解析失败时返回 errors，前端引导用户补全
2. POST /api/v1/resource/match 入参条件 JSON，返回带 score 的资源列表
3. score 算法：品类(20) + 材质(15) + 规格(15) + 价格(20) + 库存(10) + 城市(15) + 信用(5)
4. AI 解析接口必须保留扩展点（后续可切换到 LLM）

【前端】
1. views/tools/find/FindView.vue
2. 子组件：
   - AiInput.vue（大输入框 + 示例 chip）
   - AiParsedTags.vue（识别条件 tag 可删除）
   - MatchResultCard.vue（带匹配度圆环 + 商家信息）
3. 输入方式 5 种（文字/语音/Excel/图片/高级筛选），第一期只实现"文字"+"高级筛选"，其他按钮置灰加 Tooltip "敬请期待"
4. 询价按钮 + 🔔订阅价格 按钮
5. 底部操作 4 个：保存方案 / 价格订阅 / 一键发求购 / 联系平台找货

【禁止】
不允许出现"+ 加入对比"、"批量比价"、"批量询价"、"对比表"任何字样
```

---

## 🔔 模块 6 · 价格订阅工具

```
任务：实现价格订阅工具页 /tools/subscribe

设计稿：designs/mockup-tools.html 的 #tool-2（原 #tool-3）价格订阅章节

【后端】
1. t_subscription 表（按 docs/development-prompt.md 4.2 节）
2. t_alert 表（订阅触发记录）
3. CRUD 接口：GET/POST/PUT/DELETE /api/v1/user/subscriptions[/{id}]
4. GET /api/v1/user/alerts?limit=20 告警列表
5. 定时任务 SubscriptionScanTask 每 1 分钟：
   - 扫描所有 enabled=1 的订阅
   - 与 t_resource 当前价对比
   - 命中条件（跌破/涨破/变化%）→ 写 t_alert + 推送
6. 推送 4 渠道：
   - 微信：企业微信机器人 webhook
   - 钉钉：webhook
   - 短信：阿里云短信
   - 邮件：JavaMail
7. 推送时段控制：workday_8_20 / all_day / work_hour，非时段告警合并到日报

【前端】
1. views/tools/subscribe/SubscribeView.vue
2. 子组件：
   - SubscriptionCard.vue（含 sparkline + 条件徽章 + 渠道）
   - SubscriptionForm.vue（右侧粘性表单）
   - AlertTimeline.vue（时间线 dot + body + 时间）
3. 表单字段：关注对象 + 提醒条件（3 种） + 推送渠道（4 多选） + 推送时段
4. 表单校验：至少 1 个提醒条件 + 至少 1 个推送渠道
```

---

## 📡 模块 7 · 报价雷达

```
任务：实现报价雷达工具页 /tools/radar

设计稿：designs/mockup-tools.html 的 #tool-3（原 #tool-4）报价雷达章节

【后端】
1. t_purchase 表 + t_quote 表
2. 求购 CRUD：POST/PUT/DELETE /api/v1/purchase[/{id}]
3. GET /api/v1/user/purchases 我的求购
4. GET /api/v1/purchase/{id}/quotes 求购的报价
5. POST /api/v1/purchase/{id}/quote 商家报价
6. GET /api/v1/merchant/matched-purchases 商家视角：匹配我库存的求购
7. 匹配逻辑：
   - t_resource 的 (category, material, spec, city) 对 t_purchase 同字段做匹配
   - 商家发布资源时也触发一次匹配通知
   - 求购发布时给所有匹配商家推送

【前端】
1. views/tools/radar/RadarView.vue
2. 双视角 Tab：buyer / supplier
3. buyer 视图：
   - 我的求购卡列表（扫描中/已成交状态）
   - SVG 雷达扫描动画图（纯前端，4s 一圈）
   - 收到的报价表（最优价 🏆）
4. supplier 视图：
   - 匹配我库存的求购列表
   - 立即报价按钮 → 弹窗
5. 立即议价跳 IM（复用现有 IM 模块）

【SVG 雷达细节】
- 中心：我的求购点（脉冲）
- 扫描扇：旋转动画
- 已报价点：绿色发光
- 候选商家：浅灰小点
- 距离环：50km / 100km / 200km
- 罗盘方位
```

---

## 📊 模块 8 · 行情对比

```
任务：实现行情对比工具页 /tools/compare

设计稿：designs/mockup-tools.html 的 #tool-4（原 #tool-5）行情对比章节

【后端】
1. GET /api/v1/index/compare 支持 4 种 type：
   - type=city：多城市同品类对比
   - type=category：多品类同城市对比
   - type=mill：多钢厂对比
   - type=time：同序列时间段对比
2. 入参示例：?type=city&category=rebar&cities=shanghai,tangshan,tianjin&from=2026-04-27&to=2026-05-27
3. 返回各序列的 time-value 数组
4. 计算汇总：均价/最高/最低/波动率/价差
5. 缓存 5min

【前端】
1. views/tools/compare/CompareView.vue
2. 4 个 Tab：city/category/mill/time
3. 筛选器：品类下拉 + 多城市勾选 chip（6/20） + 时间范围
4. ECharts 多线图：
   - 6 城价格走势 30 日
   - 每条线末端带城市价格标签
   - 价差区间虚线提示
5. 数据表 + 趋势 sparkline 列
6. 热力图：10 城 × 8 品类，6 档色阶
7. 底部"套利提示"文案（产品后续会人工写规则）
```

---

## 📰 模块 9 · 资讯频道

```
任务：实现资讯频道（列表 + 详情）

设计稿：参考 mockup-home-v2.html 资讯三列样式 + designs/news.html（待出）

【后端】
1. t_news 表
2. 编辑后台 CRUD：POST/PUT/DELETE /admin/api/news（需 RBAC）
3. 公开接口：GET /api/v1/news/list?category=&page=&size=
4. GET /api/v1/news/{id} 详情
5. 发文后异步任务：
   - 重生 news-sitemap
   - 推送百度普通收录/快速收录
   - 发 RSS（写入 /rss/news.xml）
6. 浏览量统计：Redis HyperLogLog（去重）

【前端】
1. views/news/NewsListView.vue（5 栏目 Tab）
2. views/news/NewsDetailView.vue
3. 详情页结构：
   - <h1> 标题
   - 面包屑
   - 正文（v-html，DOMPurify 过滤）
   - 文末相关阅读 6 条 + 相关品类 + 相关分站
   - 评论暂不做
4. 列表筛选：栏目 / 时间 / 热度

【SEO 必须】
- 详情页 SSR：Java SEO Controller 渲染 Thymeleaf
- JSON-LD: NewsArticle
- meta description 取正文前 150 字
- canonical 指向 /news/{id}.html
```

---

## 📢 模块 10 · 广告位（分站核心）

```
任务：实现广告位系统（首页 1 个 + 分站 6 个）

设计稿：designs/mockup-shanghai.html 的 6 个 .sh-ad-* 区块

【后端】
1. t_ad 表（按 docs/development-prompt.md 4.2 节）
2. 6 个 slot 枚举：sh_top_banner / sh_side_1 / sh_side_2 / sh_side_3 / sh_mid_banner / sh_merchant_banner
3. 首页 slot：home_right_side
4. GET /api/v1/ad?slot=sh_top_banner&city=shanghai
   - 按优先级 + 时间窗口 + 状态选最优
   - 同 slot 同 city 仅返回 1 条
5. 后台：投放管理 / 按月按位售卖 / 数据统计
6. 点击追踪：写 t_ad_click，异步聚合到日统计

【前端】
1. components/AdSlot/AdSlot.vue 通用广告位组件
2. props：slot, city, layout (banner|thin|side)
3. 必须显示"广告 AD"角标
4. 三种布局：
   - banner（120px 顶部通栏）
   - thin（80px 中部/商家下方）
   - side（侧边小广告 280×220）
5. 点击跳 link_url + 上报 click

【埋点】
- exposure：曝光（IntersectionObserver）
- click：点击
- 上报到 GA / 自建埋点
```

---

## 🔍 模块 11 · SEO 着陆页（Java + Thymeleaf）

```
任务：实现 SEO 着陆页（仅给爬虫看）

设计稿：以 designs/mockup-home-v2.html / mockup-shanghai.html 为视觉真源，但
       简化为纯 HTML+CSS（无 JS），所有数据后端直出

【后端 - 必做 7 个 Controller】
1. HomeSeoController       GET /
2. CategorySeoController   GET /resource[/{cat}]
3. ResourceSeoController   GET /resource/{slug}.html
4. ShopSeoController       GET /shop/{id}
5. SubstationSeoController GET /s/{cityCode}
6. NewsSeoController       GET /news[/{cat}], /news/{id}.html
7. AboutSeoController      GET /about, /help, /join

【每个模板必须包含】
- <title> 长度 ≤ 60 字
- <meta name="description"> ≤ 160 字
- <meta name="keywords"> 5-10 个
- <link rel="canonical">
- 全套 og:* + twitter:*
- JSON-LD（详见 docs/development-prompt.md 6.5）
- 正文直出，所有数据由 Service 查询后渲染
- 每个页面 ≥ 30 个业务 <a>
- 所有 <img> 必填 alt + width + height

【Nginx 分流配置】
按 docs/development-prompt.md 6.2 节实现 UA 白名单分流

【验证】
- curl -A "Baiduspider/2.0" https://www.huodaizi.com/ 必须返回完整 HTML
- 用 Lighthouse 跑 SEO 分数 ≥ 90
- 用 Schema.org Validator 验证 JSON-LD
```

---

## 🗺️ 模块 12 · Sitemap & 站长平台

```
任务：实现 sitemap + 百度站长 + Bing IndexNow

【后端】
1. 主索引 /sitemap.xml
2. 5 个子 sitemap：sitemap-static.xml / sitemap-resource.xml / sitemap-shop.xml / sitemap-site.xml / sitemap-news.xml
3. SitemapGenerator 定时任务：每日 02:00 全量重生
4. 资讯/资源发布时通过 RocketMQ 异步追加 + 推送百度
5. 推送 API：
   - 百度普通收录 POST http://data.zz.baidu.com/urls?site=...
   - 百度快速收录（如有）
   - Bing IndexNow POST https://api.indexnow.org/indexnow
6. 推送结果记录 t_seo_push_log
7. 失败重试 + 钉钉告警

【验证】
- curl https://www.huodaizi.com/sitemap.xml 必须可访问
- 用百度站长平台抓取诊断验证
- 用百度站长平台 sitemap 提交工具验证
```

---

## 🚀 模块 13 · 性能优化

```
任务：实现性能优化（M4 里程碑）

【任务清单】
1. wujie 子应用从公开页 chunk 移除
   - 检查所有公开 view（home/resource/news/s/tools），grep "wujie"，全部移除
   - 仅 workspace 路由保留 wujie
2. ag-grid / vxe-table 同样从公开页移除
3. Vite 配置按需切片：
   - vendor chunk: vue + router + pinia
   - echarts chunk: 仅 line/bar/heatmap，禁止全量
   - 业务 chunk: 路由级代码分割
4. 图片优化：
   - 全部转 WebP（imagemin-webp）
   - <img> 必填 width/height（避免 CLS）
   - lazyload（IntersectionObserver）
5. CDN：
   - 静态资源（hash 文件名）1 年强缓存
   - HTML 不缓存
   - 爬虫页可加 1h CDN
6. Lighthouse 跑分：
   - SEO ≥ 90
   - 性能 ≥ 80
   - LCP < 2.5s
7. SkyWalking 埋点

【验证】
- 提交前跑 Lighthouse CI
- 检查 dist/ 下 chunk 体积：每个 ≤ 300KB（gzip 前），首页首屏 ≤ 500KB
```

---

## 📋 给所有 AI 任务的统一收尾

每个任务完成后，AI 必须输出：

1. **变更文件清单**
2. **新依赖**（如有）+ 是否在白名单
3. **数据库变更 SQL**（如有）+ DBA Review 提醒
4. **接口契约**（OpenAPI yaml 片段）
5. **单元测试** + 覆盖率
6. **如何本地跑通**（命令清单）
7. **关键决策的注释**（为什么这么写）
8. **不确定的地方**（标记 `@TODO @产品` 等责任人）

---

**版本**：v1.0 · 2026-05-27  
**配套**：docs/development-prompt.md（完整开发指引）
