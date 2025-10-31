package com.tmartinez.similarproducts.infrastructure.adapter.in.dto;

import com.tmartinez.similarproducts.infrastructure.adapter.in.api.dto.ProductDetail;
import lombok.Getter;

import java.util.List;
import java.util.Optional;

@Getter
public class SimilarProductsResponse {
    private Optional<List<ProductDetail>> SimilarProducts;
    public SimilarProductsResponse(List<ProductDetail> products){
        this.SimilarProducts = Optional.of(products);
    }
}
