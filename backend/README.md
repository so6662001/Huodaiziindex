# Backend Maven 开发环境说明

本目录已配置为可直接编译运行的 Spring Boot + Maven 项目（JDK 21）。

## 1. 环境要求

- JDK 21
- Maven 3.8+（或使用仓库内 Maven Wrapper）

## 2. 推荐命令（优先使用 Wrapper）

在 `backend` 目录下执行：

```bash
./mvnw -v
./mvnw clean test
./mvnw clean package
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

如果本机已安装 Maven，也可使用：

```bash
mvn -v
mvn clean package
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## 3. 关键配置

- `pom.xml`
  - Spring Boot 3.3.5
  - Java 21
  - 包含 `web` / `validation` / `actuator` / `test`
- `src/main/resources/application.yml`
  - 默认端口 `8080`
  - 默认 profile `dev`
- `src/main/resources/application-dev.yml`
  - 开发环境日志级别配置
- `.mvn/jvm.config`
  - UTF-8 与 TLS 兼容参数，确保本地/CI 一致
- `mvnw` / `mvnw.cmd` / `.mvn/wrapper/maven-wrapper.properties`
  - Maven Wrapper，避免环境差异

## 4. 启动后验证接口

- 前台健康检查：`GET /api/v1/health`
- 中台健康检查：`GET /api/admin/health`
- Actuator 健康检查：`GET /actuator/health`

## 5. 常见问题

1) `./mvnw: Permission denied`

```bash
chmod +x ./mvnw
```

2) 端口冲突（8080）

可临时指定端口：

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```
