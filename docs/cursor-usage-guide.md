# 在 Cursor 中使用本套开发提示词的完整指南

> 这是给开发者的**实操手册**，告诉你如何把已经准备好的 `docs/development-prompt.md` + `docs/ai-prompts-per-module.md` + 设计稿，**正确地喂给 Cursor**，让 AI 按规范帮你写代码。

---

## 0. 准备工作（一次性）

### 0.1 确认 Cursor 已安装
- 下载地址：<https://cursor.com/>
- 推荐版本：0.40+（支持 `.cursor/rules/*.mdc` 新格式）
- 旧版本（< 0.40）会自动读取 `.cursorrules` 根文件，本仓库两个都备好了

### 0.2 把本仓库拉到本地
```bash
git clone <repo-url> huodaizi
cd huodaizi
cursor .
```

### 0.3 验证项目规则已生效
打开 Cursor 后，按 `Cmd/Ctrl + Shift + P` → 输入 `Cursor: Rules` → 应该能看到：
- `.cursorrules`（旧格式，根目录）
- `.cursor/rules/00-core.mdc` ← Always Apply 核心约束
- `.cursor/rules/10-backend-java.mdc` ← Java 文件触发
- `.cursor/rules/20-frontend-vue.mdc` ← Vue/TS 文件触发
- `.cursor/rules/30-seo.mdc` ← SEO 相关
- `.cursor/rules/40-git-commit.mdc` ← Git 提交触发

> ⚠️ 任何时候 AI 似乎"忘了"项目约束，回到 `Cmd/Ctrl + Shift + P` → `Cursor: Reload Rules`。

---

## 1. 三种开发模式速查

| 模式 | 入口 | 适合场景 |
|---|---|---|
| **Composer Agent**（推荐）| `Cmd/Ctrl + I` | 多文件大任务（实现整个组件 / 接口 / 模块）|
| **Inline Chat** | `Cmd/Ctrl + K` | 单文件局部修改 |
| **Chat Panel** | `Cmd/Ctrl + L` | 提问 / 解释 / 设计讨论，不动文件 |

> 本项目**强烈推荐用 Composer Agent**：它能自动跨文件、调用工具、运行测试。

---

## 2. 标准开发流程（5 步法）

### 步骤 ① 选定本次任务的模块

打开 `docs/ai-prompts-per-module.md`，从 13 个模块里选一个，例如「模块 5 · 智能找钢工具」。

### 步骤 ② 在 Composer 开新对话

按 `Cmd/Ctrl + I` 打开 Composer，**先按下面格式喂上下文**：

```
请阅读以下文件作为本次任务的上下文：
@docs/development-prompt.md
@docs/ai-prompts-per-module.md
@designs/mockup-tools.html
@designs/assets/workbench.css
@designs/assets/tools.css

我要实现的模块：智能找钢工具（对应 ai-prompts-per-module.md 的模块 5）
对应设计稿区块：mockup-tools.html 的 #tool-1 智能找钢章节

按 .cursor/rules/00-core.mdc 的工作流执行：先列文件清单，等我确认。
```

> 📌 `@文件名` 是 Cursor 的关键语法，把对应文件**完整内容塞进 AI 上下文**。

### 步骤 ③ 等 AI 列出文件清单 → 人工 Review

AI 会先输出类似：

```
我将创建/修改以下文件：

新增（后端）：
- src/main/java/com/huodaizi/find/controller/AiParseController.java
- src/main/java/com/huodaizi/find/service/AiParseService.java
- src/main/java/com/huodaizi/find/service/impl/AiParseServiceImpl.java
- src/main/java/com/huodaizi/find/dto/ParseRequestDTO.java
- src/main/java/com/huodaizi/find/dto/ParseResponseVO.java
- src/main/java/com/huodaizi/find/parser/RuleBasedParser.java
- src/test/java/com/huodaizi/find/service/AiParseServiceTest.java

新增（前端）：
- frontend/src/views/tools/find/FindView.vue
- frontend/src/views/tools/find/components/AiInput.vue
- frontend/src/views/tools/find/components/AiParsedTags.vue
- frontend/src/views/tools/find/components/MatchResultCard.vue
- frontend/src/api/find.ts
- frontend/src/stores/find.ts

修改：
- frontend/src/router/index.ts（增加 /tools/find 路由）

数据库变更：
- 无（复用现有 t_resource）

新依赖：
- 无

确认无误请回复"开始"，或提出调整。
```

**这一步必须人工 Review**，确保：
- 文件路径符合项目目录约定
- 没有遗漏关键文件
- 没有引入未在白名单的依赖
- 表结构变更已经 DBA 心里有数

### 步骤 ④ 让 AI 生成代码

确认后回复：`开始`。AI 会按清单逐文件生成代码。

**生成过程中你可以**：
- 在右侧 diff 面板逐文件 Review
- 看到不对的随时打断（按 ESC）
- 通过 Composer 底部的 ⟲ 按钮局部重写某文件

### 步骤 ⑤ 让 AI 运行测试 + 收尾

代码生成完毕后，发：

```
请按 00-core.mdc 的收尾要求执行：
1. 运行 mvn test（后端）和 pnpm test（前端），确保全绿
2. 输出变更文件清单
3. 输出新增依赖（如有）
4. 输出 SQL DDL（如有，提醒 DBA Review）
5. 输出本地运行方式
6. 输出关键决策注释
7. 列出所有 @TODO 标记的位置
```

AI 会执行 `mvn test` 等命令，把失败的测试自动修。

---

## 3. 不同任务类型的 prompt 模板

### 3.1 实现一个新模块
直接复制 `docs/ai-prompts-per-module.md` 里对应模块的 prompt，前面加：

```
@docs/development-prompt.md @docs/ai-prompts-per-module.md
@designs/mockup-home-v2.html  ← 或对应的设计稿

[粘贴模块 prompt]

按 .cursor/rules/00-core.mdc 工作流，先列文件清单。
```

### 3.2 修复 Bug

```
@<出 bug 的文件>
@docs/development-prompt.md（如果涉及业务约束）

问题：[描述 bug 现象 + 复现步骤 + 期望行为]

请：
1. 定位根因
2. 提出修复方案（不要直接改）
3. 我确认后再改
4. 改完后必须补一个回归测试用例
```

### 3.3 Code Review

```
@<待 review 的文件>

请按 .cursor/rules/00-core.mdc + 10-backend-java.mdc 的规范，
对这个文件做 Code Review，输出：
- 不符合规范的地方（按严重程度排序）
- 性能/安全/可维护性隐患
- 建议改进点（带代码片段）
不要直接改文件，只输出建议。
```

### 3.4 接口联调

```
@docs/development-prompt.md 第 5 章 API 接口规范
@<后端 Controller 文件>
@<前端 api/xxx.ts 文件>

请确认前后端接口契约一致：
- 路径、方法、请求参数、响应字段
- 错误码处理
- 不一致的地方列出
```

### 3.5 SEO 模板生成

```
@docs/development-prompt.md 第 6 章
@designs/mockup-home-v2.html

实现 HomeSeoController + templates/seo/home.html。
要求：
- 严格遵守 30-seo.mdc 全部约束
- HTML 直出（无 JS 异步加载）
- 含完整 title/description/canonical/og/JSON-LD
- 内链 ≥ 30 个业务 <a>
- 用 curl -A "Baiduspider/2.0" 验证后输出结果
```

---

## 4. 高效使用 Cursor 的 10 个技巧

### 4.1 充分用 `@` 喂上下文
- `@文件名`：插入完整文件
- `@Folders`：插入整个目录
- `@Docs`：调用文档站（如 vue.js 官方文档）
- `@Web`：联网搜索
- `@Git`：调用 git 历史/diff
- `@Recommended`：智能推荐

### 4.2 用 Codebase Index
按 `Cmd/Ctrl + Shift + P` → `Cursor: Index Codebase`，让 Cursor 索引整个项目，之后 `@Codebase` 可以让 AI 全局检索。

### 4.3 善用 Notepads（笔记本）
把常用的提示词存成 Notepad（Cursor 左下角）：
- 「**对照设计稿严格实现**」笔记本：贴上 `@docs/development-prompt.md @设计稿`
- 「**SEO 验证清单**」笔记本：贴上验证命令
- 在 Composer 里用 `@Notepad` 调用

### 4.4 自定义快捷键
Cursor 设置里把这些设到顺手键：
- Composer Agent：`Cmd/Ctrl + I`
- Inline Chat：`Cmd/Ctrl + K`
- Apply Changes：`Cmd/Ctrl + Enter`
- Reject Changes：`Cmd/Ctrl + Backspace`

### 4.5 模型选择
- **写新代码**：用 Claude Sonnet 或 GPT-4o
- **调试/分析**：用 Claude Opus（深度思考）
- **大规模重构**：用支持长上下文的模型（Sonnet 4 / GPT-4.1）
- **简单查询**：用 GPT-4o-mini（省钱）

### 4.6 让 AI 边写边解释
在 prompt 里加：
```
每写一个关键决策处，加注释说明"为什么这么写"+"参考 docs 哪一章"
```

### 4.7 让 AI 自己跑命令验证
新版 Composer Agent 可以自己执行 shell：
```
代码写完后，执行：
1. mvn test 看是否通过
2. 如果失败，自动修复直到通过
3. 然后 git diff 给我看变更
```

### 4.8 善用 `/` 命令
Composer 里输入 `/` 可以快速插入：
- `/explain` 解释代码
- `/test` 生成测试
- `/doc` 生成 JSDoc/JavaDoc
- `/refactor` 重构

### 4.9 用 Inline Chat 做局部精修
选中一段代码 → `Cmd/Ctrl + K`：
- 「优化性能」
- 「加错误处理」
- 「翻译注释为中文」
- 「按 BEM 重命名 CSS class」

### 4.10 阶段提交
每个模块写完立刻 commit（Cursor 集成的 Source Control 面板）：
- AI 会按 `40-git-commit.mdc` 规范生成 commit message
- 例：`feat(tools-find): 实现 AI 自然语言解析 + 智能找钢页`

---

## 5. 常见踩坑

### 5.1 AI 突然不按规范了
**原因**：长对话后规则文件被挤出上下文
**解决**：
- 在 prompt 最前面再次贴 `@.cursor/rules/00-core.mdc`
- 或者新开一个 Composer 对话

### 5.2 AI 跑去 Mock 数据
**原因**：没明确要求接真实 API
**解决**：prompt 里写"必须接真实 API（从 OpenAPI 生成的 TS 类型），不要 Mock"

### 5.3 AI 引入了未授权依赖
**原因**：规则 OK，但 AI 觉得方便就装了
**解决**：
- 任何新依赖必须先停下询问
- 收尾时让 AI grep `package.json` / `pom.xml` 列出新增项
- CI 加白名单检查

### 5.4 AI 改坏了设计稿对应的视觉
**原因**：没把设计稿 @ 进来
**解决**：每次涉及 UI 都必须 `@designs/mockup-xxx.html`

### 5.5 AI 生成的 Java 代码包名不对
**原因**：没指定包结构
**解决**：在 prompt 里指明"包名必须 `com.huodaizi.<module>.<layer>`"

### 5.6 测试一直跑不过
**新版做法**：直接让 Agent 模式自动跑测试 + 自动修
```
请运行 mvn test，如果失败：
1. 不要改测试预期
2. 修业务代码使测试通过
3. 修完再跑一遍直到全绿
```

---

## 6. 一个完整示例（端到端）

**任务**：实现首页"钢厂出厂价快报"区块

### 步骤 1 · 打开 Composer
按 `Cmd/Ctrl + I`

### 步骤 2 · 喂上下文（复制以下整段）

```
@docs/development-prompt.md
@docs/ai-prompts-per-module.md
@designs/mockup-home-v2.html
@designs/assets/workbench.css

任务：实现"主流钢厂出厂价快报"区块（对应 ai-prompts-per-module.md 的模块 3）

设计稿位置：mockup-home-v2.html 的 .mill-pn 区块
（含 4 品类 Tab + 7 列表格 + 4 卡统计 + 钢厂动态列表）

按 .cursor/rules/00-core.mdc 工作流执行：
1. 先阅读上面 4 个文件
2. 列出将创建/修改的文件清单
3. 标出新增依赖（如有）+ SQL DDL（如有）
4. 等我回复"开始"再写代码
```

### 步骤 3 · AI 列清单（等待）

AI 输出类似：

```
我将创建：

【后端】
- src/main/java/com/huodaizi/mill/controller/MillPriceController.java
- src/main/java/com/huodaizi/mill/service/MillPriceService.java
- src/main/java/com/huodaizi/mill/service/impl/MillPriceServiceImpl.java
- src/main/java/com/huodaizi/mill/mapper/MillPriceMapper.java
- src/main/java/com/huodaizi/mill/entity/MillPriceEntity.java
- src/main/java/com/huodaizi/mill/dto/MillPriceVO.java
- src/main/java/com/huodaizi/mill/task/MillPriceCrawlTask.java
- src/main/java/com/huodaizi/mill/crawler/strategy/ShaGangCrawler.java
- src/main/java/com/huodaizi/mill/crawler/strategy/YongGangCrawler.java
- src/test/java/com/huodaizi/mill/service/MillPriceServiceTest.java

【前端】
- frontend/src/components/MillPrices/MillPrices.vue
- frontend/src/components/MillPrices/MillPriceTable.vue
- frontend/src/components/MillPrices/MillStatCards.vue
- frontend/src/components/MillDynamics/MillDynamics.vue
- frontend/src/api/mill.ts

【数据库】
新增表 t_mill_price（DDL 见下），需 DBA Review。

【依赖】
- 无新增（jsoup 已在 pom 里用作 HTML 解析）

【SQL DDL】
CREATE TABLE t_mill_price (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  mill_code VARCHAR(32) NOT NULL,
  ...
);

确认无误请回复"开始"。
```

### 步骤 4 · 回复"开始"，AI 生成代码

AI 按清单逐文件生成，右侧 diff 实时可见。

### 步骤 5 · 收尾

```
请执行收尾：
1. mvn test
2. 输出变更文件清单 + 新依赖检查 + 测试结果 + 本地运行方式
3. 列出所有 @TODO
4. 生成 commit message（按 40-git-commit.mdc）
```

### 步骤 6 · 提交

Cursor 左侧 Source Control → 用 AI 给的 commit message 提交。

---

## 7. 团队协作建议

### 7.1 谁负责哪个模块
建议按 `docs/ai-prompts-per-module.md` 的 13 个模块给团队分工，避免冲突。

### 7.2 PR 时的 AI 自检
在 PR 模板里加一段：
```
请 Cursor 按 .cursor/rules/00-core.mdc + 40-git-commit.mdc 自检本 PR：
- 命名规范
- 禁用清单
- 测试覆盖
- 关键决策是否注释
- 与设计稿/文档的一致性
输出问题列表，无问题输出 "PASS"
```

### 7.3 大重构怎么办
- 不要一次性让 AI 重构整个项目
- 每次重构一个模块 + 一个 PR
- 用 `@Git` 让 AI 看历史 commit 理解上下文

### 7.4 不写代码的角色
- **产品**：用 Chat Panel `@docs/development-prompt.md` 问"XX 功能有没有覆盖"
- **设计**：用 Chat Panel `@designs/` 让 AI 检查实现和设计稿的差异
- **测试**：用 Composer `@测试代码` 让 AI 补充 case

---

## 8. 维护本指南

每次设计稿/文档大变更（如新增工具、调整约束），按下面顺序更新：
1. 改 `docs/development-prompt.md`
2. 改对应章节的 `.cursor/rules/*.mdc`
3. 改 `docs/ai-prompts-per-module.md` 对应模块
4. 改本指南（如使用方式变了）
5. PR Review 时确认 4 处同步

---

## 9. 一句话速记

> **每次开始：`@docs/development-prompt.md @设计稿 + 模块 prompt → 等 AI 列清单 → 确认 → 写代码 → 收尾测试 → 提交**

祝编码愉快 🚀

---

**版本**：v1.0 · 2026-05-27
