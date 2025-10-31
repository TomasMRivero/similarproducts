package com.tmartinez.similarproducts.application.port.in;

import com.tmartinez.similarproducts.domain.model.Product;

import java.util.List;

public interface GetSimilarProductDetailListUseCase {
    List<Product> getSimilarProductDetailList(String productId);
}
