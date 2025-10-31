package com.tmartinez.similarproducts.infrastructure.adapter.out;

import com.tmartinez.similarproducts.application.port.out.SimilarProductsOutPort;
import com.tmartinez.similarproducts.domain.model.Product;
import com.tmartinez.similarproducts.infrastructure.adapter.out.api.dto.ProductDetail;
import com.tmartinez.similarproducts.infrastructure.adapter.out.api.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;

import java.util.Collections;
import java.util.List;

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
    public List<String> getRelatedProducts(String productId) {
        try{
            return restTemplate.getForObject("product/{productId}/similarids", List.class, productId);
        } catch (HttpClientErrorException.NotFound e) {
            log.error("Product {} not found", productId);
            return Collections.emptyList();
        } catch (HttpServerErrorException e) {
            log.error("External API Error 5xx when fetching related products for product {}: {}", productId, e.getStatusCode());
            throw e;
        } catch (RestClientException e) {
            log.error("Connection error when fetching related products for product {}: {}", productId, e.getMessage());
            throw new RuntimeException(e);
        } catch (Exception e){
            log.error("Unexpected error: {}", e.getMessage());
            throw new RuntimeException(e);
        }

    }

    @Override
    public Product getProductDetails(String productId) {
        try{
            ProductDetail productDetailResponse = restTemplate.getForObject("/product/{productId}", ProductDetail.class, productId);
            return productMapper.toDomain(productDetailResponse);
        } catch (HttpClientErrorException.NotFound e){
            log.error("Product {} not found", productId);
            return null;
        } catch (HttpServerErrorException e) {
            log.error("External API Error 5xx when fetching product {}: {}", productId, e.getStatusCode());
            throw e;
        } catch (RestClientException e) {
            log.error("Connection error when fetching {}: {}", productId, e.getMessage());
            throw new RuntimeException(e);
        } catch (Exception e){
            log.error("Unexpected error: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
