package com.tmartinez.similarproducts.application.port.out;

import com.tmartinez.similarproducts.domain.model.Product;

import java.util.List;


public interface SimilarProductsOutPort {
    List<String> getRelatedProducts(String productId);
    Product getProductDetails(String productId);
}
