package com.tmartinez.similarproducts.application.dto;

import com.tmartinez.similarproducts.domain.model.Product;
import lombok.Data;

import java.util.List;

@Data
public class RelatedProductListResponse {
    private final List<Product> products;
    private final List<String> absentIds;

    public RelatedProductListResponse(List<Product> products, List<String> absentIds) {
        this.products = products;
        this.absentIds = absentIds;
    }

    public boolean isPartialLoad() {
        return !products.isEmpty() && !absentIds.isEmpty();
    }
}
