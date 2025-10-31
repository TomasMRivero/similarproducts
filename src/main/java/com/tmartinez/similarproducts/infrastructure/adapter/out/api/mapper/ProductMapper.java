package com.tmartinez.similarproducts.infrastructure.adapter.out.api.mapper;

import com.tmartinez.similarproducts.domain.model.Product;
import com.tmartinez.similarproducts.infrastructure.adapter.out.api.dto.ProductDetail;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public Product toDomain(ProductDetail productDetail) {
        if (productDetail == null) {
            return null;
        }
        return new Product(productDetail.getId(), productDetail.getName(), productDetail.getPrice().doubleValue(), productDetail.getAvailability());
    }
}
