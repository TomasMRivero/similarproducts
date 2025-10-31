package com.tmartinez.similarproducts.application.service;

import com.tmartinez.similarproducts.application.port.out.SimilarProductsOutPort;
import com.tmartinez.similarproducts.domain.model.Product;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Component
public class GetSimilarProductsService {
    private final SimilarProductsOutPort similarProductsOutPort;

    public GetSimilarProductsService(SimilarProductsOutPort similarProductsOutPort) {
        this.similarProductsOutPort = similarProductsOutPort;
    }

    public List<Product> execute(String id) {
        List<String>   relatedProducts = similarProductsOutPort.getRelatedProducts(id);
        return relatedProducts.stream()
                .map(similarProductsOutPort::getProductDetails)
                .toList();
    }
}
