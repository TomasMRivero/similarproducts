package com.tmartinez.similarproducts.application.port.out;

import com.tmartinez.similarproducts.domain.model.Product;

import java.util.List;
import java.util.concurrent.CompletableFuture;


public interface SimilarProductsOutPort {
    List<String> getRelatedProducts(String productId);
    Product getProductDetails(String productId);
    CompletableFuture<Product> getProductDetailsAsync(String productId);
}
