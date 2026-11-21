/*
 * Copyright (c) 2022 Jiovan Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jiovan.spring.starter.ratelimit.config;


import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import com.jiovan.spring.starter.ratelimit.aspect.RedisRateLimitAspect;
import com.jiovan.spring.starter.ratelimit.facade.RateLimitCallback;
import com.jiovan.spring.starter.ratelimit.facade.impl.DefaultRateLimitCallback;
import com.jiovan.spring.starter.ratelimit.property.RateLimiterProperties;


/**
 * Configuration for rate limit
 *
 * @author David Hsing
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnBooleanProperty(prefix = RateLimitAutoConfiguration.PROPERTIES_PREFIX, name = "enabled", matchIfMissing = true)
@Import(value = {RateLimitAutoConfiguration.Entry.class, RateLimitAutoConfiguration.Redis.class})
public class RateLimitAutoConfiguration {
    public static final String PROPERTIES_PREFIX = "jiovan.rate-limit";    // $NON-NLS-1$
    public static final String REDIS_TEMPLATE = "rateLimitRedisTemplate";    // $NON-NLS-1$


    @Order(value = 0)
    @EnableConfigurationProperties(value = RateLimiterProperties.class)
    static class Entry {
        /**
         * Returns the default {@link com.jiovan.spring.starter.ratelimit.facade.RateLimitCallback} used when a request is rate limited
         *
         * @param properties the rate limiter properties used to configure the callback
         *
         * @return a {@link com.jiovan.spring.starter.ratelimit.facade.impl.DefaultRateLimitCallback} backed by the given properties
         */
        @Bean
        @ConditionalOnBooleanProperty(prefix = RateLimitAutoConfiguration.PROPERTIES_PREFIX, name = "throw-exception", havingValue = false, matchIfMissing = true)
        @ConditionalOnMissingBean
        public RateLimitCallback defaultRateLimitCallback(@Nonnull RateLimiterProperties properties) {
            return new DefaultRateLimitCallback(properties);
        }
    }


    @Order(value = 1)
    @ConditionalOnProperty(prefix = RateLimitAutoConfiguration.PROPERTIES_PREFIX, name = "storage-type", havingValue = "redis", matchIfMissing = true)
    @ConditionalOnClass(name = "org.springframework.data.redis.core.RedisOperations")
    static class Redis {
        /**
         * Returns the {@link com.jiovan.spring.starter.ratelimit.aspect.RedisRateLimitAspect} that enforces rate limits using Redis
         *
         * @param properties the rate limiter properties governing the rate limit rules
         * @param callback the provider of the callback invoked when a request is limited
         * @param preferredTemplate the explicitly qualified Redis template, preferred when present
         * @param presentTemplate the provider of a fallback Redis template when no preferred one is available
         *
         * @return a {@link com.jiovan.spring.starter.ratelimit.aspect.RedisRateLimitAspect} configured with the resolved properties, callback and template
         */
        @Bean
        @ConditionalOnMissingBean
        @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
        public RedisRateLimitAspect redisRateLimitAspect(@Nonnull RateLimiterProperties properties, @Nonnull ObjectProvider<RateLimitCallback> callback, @Nullable @Qualifier(value = REDIS_TEMPLATE) StringRedisTemplate preferredTemplate, @Nonnull ObjectProvider<StringRedisTemplate> presentTemplate) {
            StringRedisTemplate alias = ObjectUtils.getIfNull(preferredTemplate, presentTemplate.getIfAvailable());
            if (alias == null) {
                throw new IllegalStateException("No RedisTemplate found");
            }
            return new RedisRateLimitAspect(properties, callback.getIfAvailable(), alias);
        }
    }
}
