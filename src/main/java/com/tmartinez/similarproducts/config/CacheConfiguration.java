package com.tmartinez.similarproducts.config;


import com.google.common.cache.CacheBuilder;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

@EnableCaching
@Configuration
public class CacheConfiguration implements CachingConfigurer {

    @Bean
    public CacheManager cacheManager() {
       SimpleCacheManager cacheManager = new SimpleCacheManager();
        Cache relatedProductsCache = new ConcurrentMapCache(
                "relatedProducts",
                CacheBuilder.newBuilder()
                        .expireAfterWrite(5, TimeUnit.MINUTES)
                        .maximumSize(100)
                        .build()
                        .asMap(),
                false
        );

        Cache productDetailCache = new ConcurrentMapCache(
                "productDetails",
                CacheBuilder.newBuilder()
                        .expireAfterWrite(30, TimeUnit.MINUTES)
                        .maximumSize(100)
                        .build()
                        .asMap(),
                false
        );

        Cache similarProductsResponseCache = new ConcurrentMapCache(
                "similarProductsResponse",
                CacheBuilder.newBuilder()
                        .expireAfterWrite(5, TimeUnit.MINUTES)
                        .maximumSize(100)
                        .build()
                        .asMap(),
                false
        );
        cacheManager.setCaches(List.of(relatedProductsCache, productDetailCache,  similarProductsResponseCache));
        return cacheManager;
    }

    @Override
    public KeyGenerator keyGenerator() {
        return new SimpleKeyGenerator();
    }
}

