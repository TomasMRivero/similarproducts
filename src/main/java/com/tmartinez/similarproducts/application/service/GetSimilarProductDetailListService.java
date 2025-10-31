package com.tmartinez.similarproducts.application.service;

import com.tmartinez.similarproducts.application.dto.RelatedProductListResponse;
import com.tmartinez.similarproducts.application.port.in.GetSimilarProductDetailListUseCase;
import com.tmartinez.similarproducts.application.port.out.SimilarProductsOutPort;
import com.tmartinez.similarproducts.domain.model.Product;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class GetSimilarProductDetailListService implements GetSimilarProductDetailListUseCase {
    private final SimilarProductsOutPort similarProductsOutPort;

    public GetSimilarProductDetailListService(SimilarProductsOutPort similarProductsOutPort) {
        this.similarProductsOutPort = similarProductsOutPort;
    }

    @Override
    public List<Product> getSimilarProductDetailList(String productId) {
        List<String> relatedProductIds = similarProductsOutPort.getRelatedProducts(productId);
        List<Product> productList = new ArrayList<>();

        relatedProductIds.forEach(
            pid -> {
                Product product = similarProductsOutPort.getProductDetails(pid);
                if (Objects.nonNull(product)) {
                    productList.add(product);
                }
            }
        );

        return productList;
    }
}
