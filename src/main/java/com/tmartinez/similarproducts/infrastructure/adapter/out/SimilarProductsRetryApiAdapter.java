package com.tmartinez.similarproducts.infrastructure.adapter.out;

import com.tmartinez.similarproducts.application.exception.ExternalApiErrorException;
import com.tmartinez.similarproducts.application.exception.ExternalApiNotFoundException;
import com.tmartinez.similarproducts.application.exception.ExternalApiTimeoutException;
import com.tmartinez.similarproducts.application.port.out.SimilarProductsOutPort;
import com.tmartinez.similarproducts.domain.model.Product;
import com.tmartinez.similarproducts.infrastructure.adapter.out.api.dto.ProductDetail;
import com.tmartinez.similarproducts.infrastructure.adapter.out.api.mapper.ProductMapper;
import com.tmartinez.similarproducts.infrastructure.util.CacheDependencyTracker;
import com.tmartinez.similarproducts.infrastructure.util.RetryProductDetailTracker;
import com.tmartinez.similarproducts.infrastructure.util.RetryProductExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;


@Slf4j
@Component("similarProductsRetryApiAdapter")
public class SimilarProductsRetryApiAdapter implements SimilarProductsOutPort {
    protected final RestTemplate restTemplate;
    protected final ProductMapper productMapper;
    private final RetryProductDetailTracker retryTracker;
    private final CacheDependencyTracker dependencyTracker;
    private final RetryProductExecutor retryProductExecutor;

    public SimilarProductsRetryApiAdapter(
            ProductMapper productMapper,
            RetryProductDetailTracker retryTracker,
            @Qualifier("restTemplate") RestTemplate restTemplate,
            @Qualifier("memoryCacheDependencyTracker") CacheDependencyTracker dependencyTracker,
            RetryProductExecutor retryProductExecutor
    ){
        this.restTemplate = restTemplate;
        this.productMapper = productMapper;
        this.retryTracker = retryTracker;
        this.dependencyTracker = dependencyTracker;
        this.retryProductExecutor = retryProductExecutor;
    }

    @Override
    @Cacheable(value = "relatedProducts", key = "#productId")
    public List<String> getRelatedProducts(String productId) {
        try {
            List response = restTemplate.getForObject("/product/{productId}/similarids", List.class, productId);
            List<String> similarIds = response.stream().map(Object::toString).toList();
            dependencyTracker.register(productId, Set.copyOf(similarIds));
            return similarIds;
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Product {} not found", productId);
            throw new ExternalApiNotFoundException(e);
        } catch (HttpServerErrorException e) {
            String msg = String.format("External API Error 5xx when fetching related products for product %s", productId);
            throw new ExternalApiErrorException(msg, e);
        } catch (ResourceAccessException e) {
            String msg = String.format("Timeout when fetching related products for product %s", productId);
            throw new ExternalApiTimeoutException(msg, e);
        } catch (RestClientException e) {
            String msg = String.format("Connection error when fetching related products for product %s: %s", productId, e.getMessage());
            throw new ExternalApiErrorException(msg, e);
        } catch (Exception e){
            String msg = String.format("Unexpected error: %s", e.getMessage());
            throw new ExternalApiErrorException(msg, e);
        }
    }

    @Override
    public Product getProductDetails(String productId) {
        throw new NotImplementedException("Not Implemented");
    }

    @Override
    @Async("productDetailExecutor")
    @Cacheable(value = "productDetails", key = "#productId", unless = "#result == null")
    public CompletableFuture<Product> getProductDetailsAsync(String productId) {
        if(!retryTracker.startRetry(productId)) CompletableFuture.completedFuture(null);;

        try{
            ProductDetail productDetailResponse = restTemplate.getForObject("/product/{productId}", ProductDetail.class, productId);
            Product domainProduct = productMapper.toDomain(productDetailResponse);
            dependencyTracker.removeProduct(productId);
            return CompletableFuture.completedFuture(domainProduct);
        } catch (HttpClientErrorException.NotFound e){
            log.error("Product {} not found", productId);
        } catch (HttpServerErrorException e) {
            log.error("External API Error 5xx when fetching product {}: {}", productId, e.getStatusCode());
        } catch (ResourceAccessException e) {
            log.error("Timeout when fetching product {}}", productId);
        } catch (RestClientException e) {
            log.error("Connection error when fetching {}: {}", productId, e.getMessage());
        } catch (Exception e){
            log.error("Unexpected error: {}", e.getMessage());
        } finally{
            retryTracker.endRetry(productId);
        }
        retryProductExecutor.retryInBackground(productId);
        return CompletableFuture.completedFuture(null);
    }


}
