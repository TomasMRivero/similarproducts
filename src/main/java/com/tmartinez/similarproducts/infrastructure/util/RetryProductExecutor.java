package com.tmartinez.similarproducts.infrastructure.util;

import com.tmartinez.similarproducts.infrastructure.adapter.out.api.dto.ProductDetail;
import com.tmartinez.similarproducts.infrastructure.adapter.out.api.mapper.ProductMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;

import java.util.Objects;
import java.util.Set;

@Slf4j
@Component
public class RetryProductExecutor {
    private final RestTemplate restTemplateRetry;
    private final CacheManager cacheManager;
    private final RetryProductDetailTracker retryTracker;
    private final CacheDependencyTracker dependencyTracker;
    protected final ProductMapper productMapper;

    public RetryProductExecutor(
            @Qualifier("restTemplateRetry") RestTemplate restTemplateRetry,
            CacheManager cacheManager,
            @Qualifier("memoryCacheDependencyTracker") CacheDependencyTracker dependencyTracker,
            RetryProductDetailTracker retryTracker,
            ProductMapper productMapper
    ) {
        this.restTemplateRetry = restTemplateRetry;
        this.cacheManager = cacheManager;
        this.retryTracker = retryTracker;
        this.dependencyTracker = dependencyTracker;
        this.productMapper = productMapper;
    }


    @Async("retryExecutor")
    public void retryInBackground(String productId) {
        if(!retryTracker.startRetry(productId)) return;

        try{
            ProductDetail productDetailResponse = restTemplateRetry.getForObject("/product/{productId}", ProductDetail.class, productId);
            if (Objects.nonNull(productDetailResponse)) {
                Objects.requireNonNull(cacheManager.getCache("productDetails")).put(productId, productMapper.toDomain(productDetailResponse));
            }
            Set<String> roots = dependencyTracker.findDependentProducts(productId);
            for (String root : roots) {
                Cache cache = cacheManager.getCache("similarProductsResponse");
                if(Objects.nonNull(cache)){
                    cache.evict(root);
                    log.info("Invalidada cache del service para {}", root);
                }
            }
        } catch (HttpClientErrorException.NotFound e){
            log.error("Retry failed: Product {} not found", productId);
        } catch (HttpServerErrorException e) {
            log.error("Retry failed: External API Error 5xx when fetching product {}: {}", productId, e.getStatusCode());
        } catch (ResourceAccessException e) {
            log.error("Retry failed: Timeout when fetching product {}}", productId);
        } catch (RestClientException e) {
            log.error("Retry failed: Connection error when fetching {}: {}", productId, e.getMessage());
        } catch (NullPointerException e){
            log.error("Retry failed: cache not found");
        } catch (Exception e){
            log.error("Retry failed: Unexpected error: {}", e.getMessage());
        } finally {
            retryTracker.endRetry(productId);
        }
    }

}
