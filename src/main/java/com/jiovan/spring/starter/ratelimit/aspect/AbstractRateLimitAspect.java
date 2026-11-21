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

package com.jiovan.spring.starter.ratelimit.aspect;


import java.lang.reflect.Method;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import com.jiovan.plexus.common.toolkit.constant.AssertMessageConst;
import com.jiovan.plexus.common.toolkit.constant.CharVariantConst;
import com.jiovan.plexus.common.toolkit.util.StringUtilsWraps;
import com.jiovan.plexus.spring.toolkit.util.AopUtilsWraps;
import com.jiovan.plexus.spring.toolkit.util.BeanFactoryWraps;
import com.jiovan.plexus.spring.toolkit.util.RequestMappingWraps;
import com.jiovan.plexus.spring.toolkit.util.WebUtilsWraps;
import com.jiovan.spring.starter.ratelimit.annotation.RateLimit;
import com.jiovan.spring.starter.ratelimit.enumeration.LimiterTriggerType;
import com.jiovan.spring.starter.ratelimit.facade.RateLimitCallback;
import com.jiovan.spring.starter.ratelimit.facade.RateLimitInformant;
import com.jiovan.spring.starter.ratelimit.property.RateLimiterProperties;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


/**
 * Abstract aspect for rate limit
 *
 * @author David Hsing
 */
@RequiredArgsConstructor
@Slf4j
public abstract class AbstractRateLimitAspect implements ApplicationContextAware {
    protected final RateLimiterProperties limiterProperties;
    protected RateLimitCallback limitCallback;

    @Setter
    protected ApplicationContext applicationContext;

    public AbstractRateLimitAspect(@Nonnull RateLimiterProperties properties, @Nullable RateLimitCallback callback) {
        this.limiterProperties = properties;
        this.limitCallback = callback;
    }

    /**
     * Applies the {@link com.jiovan.spring.starter.ratelimit.annotation.RateLimit} policy around the intercepted join point
     *
     * @param point the join point representing the annotated method invocation
     *
     * @return the result of the intercepted method, or the result produced by the rate limit callback
     *
     * @throws Throwable if the intercepted method or the rate limiting process throws an exception
     */
    @Around(value = "@annotation(com.jiovan.spring.starter.ratelimit.annotation.RateLimit)")
    public Object aroundPoint(@Nonnull ProceedingJoinPoint point) throws Throwable {
        Method method = AopUtilsWraps.getTargetMethod(point);
        Assert.notNull(method, AssertMessageConst.NOT_NULL);
        RateLimit annotation = AnnotationUtils.getAnnotation(method, RateLimit.class);
        if (annotation == null || annotation.ttl() <= 0L || annotation.unit() == null) {
            return point.proceed();
        }
        if (!RequestMappingWraps.anyMapping(method)) {
            if (log.isWarnEnabled()) {
                log.warn("Method '{}.{}' annotated with '@{}' should also annotated with '@RequestMapping/@GetMapping/@PostMapping/@PatchMapping/@PutMapping/@DeleteMapping' as well", method.getDeclaringClass().getCanonicalName(), RateLimit.class.getSimpleName(), method.getName());
            }
        }
        String identifier = determineIdentifier(method, annotation);
        if (StringUtils.isBlank(identifier)) {
            return point.proceed();
        }
        return processPoint(point, identifier, annotation);
    }

    @Nullable
    protected String determineIdentifier(@Nonnull Method method, @Nonnull RateLimit annotation) throws Exception {
        String name = StringUtils.join(limiterProperties.getNamePrefix(), ClassUtils.getQualifiedMethodName(method), limiterProperties.getNameSuffix());
        StringBuilder builder = new StringBuilder(annotation.triggerType().getValue());
        builder.append(CharVariantConst.SQUARE_BRACKET_LEFT);
        if (annotation.triggerType() == LimiterTriggerType.IP_ADDRESS || annotation.triggerType() == LimiterTriggerType.SESSION) {
            HttpServletRequest request = WebUtilsWraps.getContextServletRequest();
            if (request == null) {
                return null;
            }
            if (annotation.triggerType() == LimiterTriggerType.IP_ADDRESS) {
                builder.append(WebUtilsWraps.getRemoteAddress(request));
            } else if (annotation.triggerType() == LimiterTriggerType.SESSION) {
                builder.append(WebUtilsWraps.getSessionId(request));
            }
        } else if (annotation.triggerType() == LimiterTriggerType.USERNAME) {
            RateLimitInformant informant = BeanFactoryWraps.getBean(applicationContext, RateLimitInformant.class);
            if (informant == null) {
                throw new NoSuchBeanDefinitionException(RateLimitInformant.class);
            }
            builder.append(informant.getUsername());
        }
        builder.append(CharVariantConst.SQUARE_BRACKET_RIGHT);
        return StringUtilsWraps.joinWithColon(name, builder.toString());
    }

    protected abstract Object processPoint(@Nonnull ProceedingJoinPoint point, @Nonnull String identifier, @Nonnull RateLimit annotation) throws Throwable;
}
