package com.tmartinez.similarproducts.infrastructure.adapter.in.mapper;

import com.tmartinez.similarproducts.domain.model.Product;
import com.tmartinez.similarproducts.infrastructure.adapter.in.api.dto.ProductDetail;

import java.math.BigDecimal;
import java.util.List;

public class ProductDetailMapper {
    public ProductDetail toInDTO(Product product){
        if(product == null){
            return null;
        }
        return new ProductDetail(product.getId(), product.getName(), BigDecimal.valueOf(product.getPrice()), product.getAvailability());
    }
    public List<ProductDetail> toInDTO(List<Product> products){
        if(products == null){
            return null;
        }
        return products.stream().map(this::toInDTO).toList();
    }
}
