# Huodaizi Backend (P06 钢铁资讯后端与中台)

本模块为 P06 钢铁资讯列表页（NewsPage）提供后端 API 与中台管理接口，采用 Spring Boot 3 + Java 21。

## 目录说明

- `controller/news/NewsController.java`：P06 钢铁资讯公共接口
- `controller/news/NewsAdminController.java`：P06 钢铁资讯中台接口
- `repository/news/InMemoryNewsRepository.java`：资讯内存仓储（MVP）
- `dto/news/**`：资讯请求与响应模型
- `common/**`、`exception/**`：统一返回与异常处理

## 已实现接口

### 1) 钢铁资讯公共接口（P06）

Base Path: `/api/v1/news`

- `GET /overview`
  - 返回资讯页聚合数据：
    - 分类 tabs
    - 列表数据（支持筛选/分页）
    - 热门阅读
- `GET /`
  - 返回资讯列表，支持参数：
    - `category`、`city`、`keyword`
    - `page`、`pageSize`

### 2) 钢铁资讯中台接口（P06）

Base Path: `/api/admin/news`

- `GET /{section}`
  - 查询指定板块中台列表（含上下架）
- `POST /{section}`
  - 新增板块内容
- `PUT /{section}/{id}`
  - 更新板块内容
- `PUT /{section}/{id}/status`
  - 上下架（请求体传 `status=ONLINE|OFFLINE`）
- `PUT /{section}/{id}/pin`
  - 置顶/取消置顶（请求体传 `pinned=true|false`）
- `DELETE /{section}/{id}`
  - 删除记录

`section` 枚举：
- `NEWS`
- `HOT_READ`
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

- 当前为 **MVP 内存版中台**，数据保存在 `InMemoryNewsRepository` 中
- 重启服务后数据会重置
- 适用于：
  - P06 接口联调
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
