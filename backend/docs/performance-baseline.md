# P18 接口性能基准报告（本地基线）

## 基准环境

- 服务地址：`http://127.0.0.1:18080`
- 启动方式：Spring Boot 本地运行（单实例）
- 压测工具：`backend/scripts/benchmark_api.py`（Python 标准库实现）
- 并发：20
- 每场景请求数：400
- 超时：10s
- 鉴权令牌：`change-this-admin-token`（仅用于中台授权场景）

> 原始数据见：`backend/docs/performance-baseline.json`

## 场景结果汇总

| 场景 | 方法 | RPS | 成功率 | P50(ms) | P90(ms) | P95(ms) | P99(ms) | Max(ms) |
|---|---|---:|---:|---:|---:|---:|---:|---:|
| public_mine_read | GET | 552.50 | 100% | 16.93 | 28.28 | 57.08 | 393.18 | 430.40 |
| public_submit_write | POST | 3013.71 | 100% | 5.51 | 9.57 | 10.47 | 12.74 | 15.13 |
| admin_list_authorized | GET | 1749.80 | 100% | 8.88 | 17.51 | 21.63 | 26.37 | 28.06 |
| admin_list_unauthorized | GET | 3427.62 | 100%* | 4.73 | 8.49 | 10.09 | 11.42 | 13.78 |
| admin_assign_authorized | PUT | 1099.07 | 100% | 17.09 | 24.61 | 27.84 | 32.78 | 37.58 |

\* `admin_list_unauthorized` 为预期 401 场景，状态分布为 401=400。

## 主要观察

1. **读接口长尾明显**
   - `public_mine_read` 的 P99 达到 **393ms**，远高于 P95（57ms）。
   - 说明该路径在高并发下存在明显长尾抖动，优先优化读链路。

2. **写接口吞吐较高**
   - `public_submit_write` 在本地单实例下达到 **3013 RPS**，分位延迟稳定。

3. **鉴权拒绝路径较快**
   - 未授权中台请求整体耗时低，说明拦截路径开销可控。

4. **中台写操作（assign）比中台读慢**
   - `admin_assign_authorized` P50 17ms，高于 `admin_list_authorized` 的 P50 8.88ms。
   - 主要来自状态更新 + 跟进日志写入链路。

## 瓶颈点定位（当前版本）

1. **`mine` 场景全量内存过滤 + 排序**
   - 当前仓储实现对线索集合进行 stream 过滤排序，在数据量增大时长尾会放大。

2. **对象构建与日志列表拷贝开销**
   - DTO 映射涉及 follow logs 排序与复制，在高并发读下会增加分位延迟。

3. **单实例 JVM 抖动**
   - 本地压测下长尾容易受 GC 与线程竞争影响，导致 P99 抬高。

## 优化建议（按优先级）

### P0（建议立即实施）

1. **`mine` 增加索引化存储策略**
   - 以 `contactPhone` 维护倒排结构（如 `Map<phone, List<leadId>>`），避免每次全量扫描。

2. **仓储侧分页前置**
   - 在可索引集合上先定位、再分页、再映射，减少大列表排序/拷贝。

3. **跟进日志列表按需返回**
   - `mine` 默认仅返回最近1条跟进摘要，完整日志单独接口拉取。

### P1（中期优化）

4. **持久化层替代内存仓储**
   - 切换到 MySQL/PostgreSQL，建立 `(contact_phone, updated_at)` 复合索引。

5. **序列号生成器独立化**
   - 将 ID/编号生成与业务处理解耦，降低写路径锁竞争。

6. **连接池与线程池调优**
   - 明确 Tomcat 线程、队列与 JVM 堆配置，减少尾延迟抖动。

### P2（持续改进）

7. **建立 CI 性能门禁**
   - 将该脚本纳入 CI，持续比对 P95/P99 与 RPS，设置回归阈值报警。

## 复现命令

```bash
python3 backend/scripts/benchmark_api.py \
  --base-url "http://127.0.0.1:18080" \
  --admin-token "change-this-admin-token" \
  --concurrency 20 \
  --requests 400 \
  --output "backend/docs/performance-baseline.json"
```