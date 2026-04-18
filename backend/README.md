# Huodaizi Backend (P05 行情详情后端与中台)

本模块为 P05 行情详情页（MarketDetailPage）提供后端 API 与中台管理接口，采用 Spring Boot 3 + Java 21。

## 目录说明

- `controller/marketdetail/MarketDetailController.java`：P05 行情详情公共接口
- `controller/marketdetail/MarketDetailAdminController.java`：P05 行情详情中台接口
- `repository/marketdetail/InMemoryMarketDetailRepository.java`：行情详情内存仓储（MVP）
- `dto/marketdetail/**`：行情详情请求与响应模型
- `common/**`、`exception/**`：统一返回与异常处理

## 已实现接口

### 1) 行情详情公共接口（P05）

Base Path: `/api/v1/market-detail`

- `GET /?symbol=&city=`
  - 返回行情详情聚合数据：
    - 标题与城市品种信息
    - 三项行情摘要（今日参考价/近7日波动/近30日区间）
    - 关联资讯
    - 相关供求
    - 分页广告位

### 2) 行情详情中台接口（P05）

Base Path: `/api/admin/market-detail`

- `GET /{symbol}/{city}/{section}`
  - 查询指定品种+城市+板块的中台列表（含上下架）
- `POST /{symbol}/{city}/{section}`
  - 新增板块内容
- `PUT /{symbol}/{city}/{section}/{id}`
  - 更新板块内容
- `PUT /{symbol}/{city}/{section}/{id}/status`
  - 上下架（请求体传 `status=ONLINE|OFFLINE`）
- `PUT /{symbol}/{city}/{section}/{id}/pin`
  - 置顶/取消置顶（请求体传 `pinned=true|false`）
- `DELETE /{symbol}/{city}/{section}/{id}`
  - 删除记录

`section` 枚举：
- `SUMMARY`
- `NEWS`
- `RELATED`
- `ACTION`
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

- 当前为 **MVP 内存版中台**，数据保存在 `InMemoryMarketDetailRepository` 中
- 重启服务后数据会重置
- 适用于：
  - P05 接口联调
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
