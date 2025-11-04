package com.tmartinez.similarproducts.application.service;

import com.tmartinez.similarproducts.application.exception.ExternalApiException;
import com.tmartinez.similarproducts.application.port.in.GetSimilarProductDetailListUseCase;
import com.tmartinez.similarproducts.application.port.out.SimilarProductsOutPort;
import com.tmartinez.similarproducts.domain.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class GetSimilarProductDetailListService implements GetSimilarProductDetailListUseCase {
    private final SimilarProductsOutPort similarProductsOutPort;

    public GetSimilarProductDetailListService(
            @Qualifier("similarProductsRetryApiAdapter")SimilarProductsOutPort similarProductsOutPort
    ) {
        this.similarProductsOutPort = similarProductsOutPort;
    }

    @Override
    @Cacheable(value = "similarProductsResponse", key = "#productId", unless = "#result.isEmpty()")
    public List<Product> getSimilarProductDetailList(String productId) {
        try{
            List<String> relatedProductIds = similarProductsOutPort.getRelatedProducts(productId);

            List<CompletableFuture<Product>> futures = relatedProductIds.stream()
                    .map(similarProductsOutPort::getProductDetailsAsync)
                    .toList();

            return futures.stream()
                    .map(CompletableFuture::join)
                    .filter(Objects::nonNull)
                    .toList();
        } catch (ExternalApiException ex) {
            log.error(ex.getMessage(), ex);
            throw ex;
        }

    }
}
