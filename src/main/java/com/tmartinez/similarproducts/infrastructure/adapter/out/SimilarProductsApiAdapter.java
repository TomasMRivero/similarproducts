package com.tmartinez.similarproducts.infrastructure.adapter.out;

import com.tmartinez.similarproducts.application.exception.ExternalApiErrorException;
import com.tmartinez.similarproducts.application.exception.ExternalApiNotFoundException;
import com.tmartinez.similarproducts.application.exception.ExternalApiTimeoutException;
import com.tmartinez.similarproducts.application.port.out.SimilarProductsOutPort;
import com.tmartinez.similarproducts.domain.model.Product;
import com.tmartinez.similarproducts.infrastructure.adapter.out.api.dto.ProductDetail;
import com.tmartinez.similarproducts.infrastructure.adapter.out.api.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SimilarProductsApiAdapter implements SimilarProductsOutPort {
    private final RestTemplate restTemplate;
    private final ProductMapper productMapper;

    public SimilarProductsApiAdapter(RestTemplate restTemplate, ProductMapper productMapper) {
        this.restTemplate = restTemplate;
        this.productMapper = productMapper;
    }

    @Override
    @Cacheable(value = "relatedProducts", key = "#productId")
    public List<String> getRelatedProducts(String productId) {
        try {
            List response = restTemplate.getForObject("/product/{productId}/similarids", List.class, productId);
            return response.stream().map(Object::toString).toList();
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
    @Cacheable(value = "productDetails", key = "#productId", unless = "#result == null")
    public Product getProductDetails(String productId) {
        try{
            ProductDetail productDetailResponse = restTemplate.getForObject("/product/{productId}", ProductDetail.class, productId);
            return productMapper.toDomain(productDetailResponse);
        } catch (HttpClientErrorException.NotFound e){
            log.error("Product {} not found", productId);
            return null;
        } catch (HttpServerErrorException e) {
            log.error("External API Error 5xx when fetching product {}: {}", productId, e.getStatusCode());
            return null;
        } catch (ResourceAccessException e) {
            log.error("Timeout when fetching product {}}", productId);
            return null;
        } catch (RestClientException e) {
            log.error("Connection error when fetching {}: {}", productId, e.getMessage());
            return null;
        } catch (Exception e){
            log.error("Unexpected error: {}", e.getMessage());
            return null;
        }
    }
}
