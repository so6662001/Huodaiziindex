# Huodaizi Backend (P07 资讯详情后端与中台)

本模块为 P07 资讯详情页（NewsDetailPage）提供后端 API 与中台管理接口，采用 Spring Boot 3 + Java 21。

## 目录说明

- `controller/newsdetail/NewsDetailController.java`：P07 资讯详情公共接口
- `controller/newsdetail/NewsDetailAdminController.java`：P07 资讯详情中台接口
- `repository/newsdetail/InMemoryNewsDetailRepository.java`：资讯详情内存仓储（MVP）
- `dto/newsdetail/**`：资讯详情请求与响应模型
- `common/**`、`exception/**`：统一返回与异常处理

## 已实现接口

### 1) 资讯详情公共接口（P07）

Base Path: `/api/v1/news-detail`

- `GET /`
  - 查询参数：`id`（资讯 ID）
  - 返回：
    - 文章核心信息（标题、分类、城市、发布时间、来源、摘要、正文、标签）
    - 相关推荐
    - 阅读提示
    - 右侧广告位

### 2) 资讯详情中台接口（P07）

Base Path: `/api/admin/news-detail/{id}/{section}`

> 安全要求：所有 `/api/admin/**` 接口必须携带请求头  
> `X-Admin-Token: <token>`

- `GET /{id}/{section}`
  - 查询指定文章 + 板块的中台列表（含上下架）
- `POST /{id}/{section}`
  - 新增板块内容
- `PUT /{recordId}`
  - 更新板块内容
- `PUT /{recordId}/status`
  - 上下架（请求体传 `status=ONLINE|OFFLINE`）
- `PUT /{recordId}/pin`
  - 置顶/取消置顶（请求体传 `pinned=true|false`）
- `DELETE /{recordId}`
  - 删除记录

`section` 枚举：
- `ARTICLE`
- `CONTENT`
- `RELATED`
- `TIP`
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

- 当前为 **MVP 内存版中台**，数据保存在 `InMemoryNewsDetailRepository` 中
- 重启服务后数据会重置
- 适用于：
  - P07 接口联调
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
