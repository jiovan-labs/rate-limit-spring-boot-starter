[English](README.md) | [简体中文](README.zh-CN.md) | [繁體中文](README.zh-TW.md)

# Rate Limit Spring Boot Starter

Spring Boot application integrates rate limitation quickly, to prevent too frequent accesses.

## Quickstart

- Import dependencies

```xml
    <dependency>
        <groupId>com.jiovan.spring.starter</groupId>
        <artifactId>rate-limit-spring-boot-starter</artifactId>
        <version>LATEST</version>
    </dependency>
```

> By default, this starter will auto take effect, you can turn it off by `jiovan.rate-limit.enabled = false`

- Configure Spring Boot `application.yml` with prefix `jiovan.rate-limit` (**Optional**)

```yml
jiovan:
    rate-limit:
        name-prefix: "${spring.application.name}:limiter:"
        denied-html-url: '/error/rate-limited'
        denied-rest-url: '/error/rate-limited'
        storage-type: 'redis'
        throw-exception: false
```

- Annotate your (non-static)  method with `@RateLimit` annotation, done!

> The `keyType` attribute of the annotation, is the limitation ways, supports
  - IP address
  - session
  - username

- This starter needs to save the limitation data to somewhere, currently is
  - redis

## Document

- Github: https://github.com/jiovan-labs/rate-limit-spring-boot-starter

## Requirement

- jdk 17+

## License

This project is under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)

See the `NOTICE.txt` file for required notices and attributions.

## Donation

You like this package? Then [donate to us](https://jiovan.com/donation) to support the development.

## Copyright

Beijing Jiovan Technology Ltd.

## Website

- Jiovan: https://jiovan.com
