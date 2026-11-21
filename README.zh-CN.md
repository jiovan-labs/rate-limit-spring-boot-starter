[English](README.md) | [简体中文](README.zh-CN.md) | [繁體中文](README.zh-TW.md)

# 限流 Spring Boot Starter

让 Spring Boot 应用快速集成限流能力，防止访问过于频繁。

## 快速开始

- 导入依赖

```xml
    <dependency>
        <groupId>com.jiovan.spring.starter</groupId>
        <artifactId>rate-limit-spring-boot-starter</artifactId>
        <version>LATEST</version>
    </dependency>
```

> 默认情况下，此 starter 会自动生效，你可以通过 `jiovan.rate-limit.enabled = false` 关闭它。

- 使用前缀 `jiovan.rate-limit` 配置 Spring Boot 的 `application.yml`（**可选**）

```yml
jiovan:
    rate-limit:
        name-prefix: "${spring.application.name}:limiter:"
        denied-html-url: '/error/rate-limited'
        denied-rest-url: '/error/rate-limited'
        storage-type: 'redis'
        throw-exception: false
```

- 在（非静态）方法上标注 `@RateLimit` 注解即可！

> 注解的 `keyType` 属性表示限流方式，支持
  - IP 地址
  - 会话
  - 用户名

- 此 starter 需要将限流数据保存到某处，目前支持
  - redis

## 文档

- GitHub：https://github.com/jiovan-labs/rate-limit-spring-boot-starter

## 环境要求

- jdk 17+

## 许可证

本项目采用 [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0) 许可证。

请参阅 `NOTICE.txt` 文件了解所需的声明与归属信息。

## 捐赠

喜欢这个工具包吗？欢迎[向我们捐赠](https://jiovan.com/donation)以支持开发。

## 版权

北京玖漫科技有限公司

## 网站

- 玖漫：https://jiovan.com
