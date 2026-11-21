[English](README.md) | [簡體中文](README.zh-CN.md) | [繁體中文](README.zh-TW.md)

# 限流 Spring Boot Starter

讓 Spring Boot 應用快速整合限流能力，防止訪問過於頻繁。

## 快速開始

- 匯入相依性

```xml
    <dependency>
        <groupId>com.jiovan.spring.starter</groupId>
        <artifactId>rate-limit-spring-boot-starter</artifactId>
        <version>LATEST</version>
    </dependency>
```

> 預設情況下，此 starter 會自動生效，你可以透過 `jiovan.rate-limit.enabled = false` 關閉它。

- 使用字首 `jiovan.rate-limit` 設定 Spring Boot 的 `application.yml`（**可選**）

```yml
jiovan:
    rate-limit:
        name-prefix: "${spring.application.name}:limiter:"
        denied-html-url: '/error/rate-limited'
        denied-rest-url: '/error/rate-limited'
        storage-type: 'redis'
        throw-exception: false
```

- 在（非靜態）方法上標註 `@RateLimit` 註解即可！

> 註解的 `keyType` 屬性表示限流方式，支援
  - IP 地址
  - 會話
  - 使用者名稱

- 此 starter 需要將限流資料儲存到某處，目前支援
  - redis

## 文件

- GitHub：https://github.com/jiovan-labs/rate-limit-spring-boot-starter

## 環境要求

- jdk 17+

## 許可證

本專案採用 [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0) 許可證。

請參閱 `NOTICE.txt` 檔案瞭解所需的宣告與歸屬資訊。

## 捐贈

喜歡這個工具包嗎？歡迎[向我們捐贈](https://jiovan.com/donation)以支援開發。

## 版權

北京玖漫科技有限公司

## 網站

- 玖漫：https://jiovan.com
