package com.tmartinez.similarproducts.application.service;

import com.tmartinez.similarproducts.application.port.in.GetSimilarProductDetailListUseCase;
import com.tmartinez.similarproducts.application.port.out.SimilarProductsOutPort;
import com.tmartinez.similarproducts.domain.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

public class GetSimilarProductDetailListServiceTest
{
    private SimilarProductsOutPort adapter;
    private GetSimilarProductDetailListUseCase service;

    @BeforeEach
    void setUp()
    {
        adapter = Mockito.mock(SimilarProductsOutPort.class);
        service = new GetSimilarProductDetailListService(adapter);
    }

    @Test
    void shouldGetEveryDetail() {
        when(adapter.getRelatedProducts("1")).thenReturn(List.of("2", "3"));
        when(adapter.getProductDetailsAsync("2")).thenReturn(CompletableFuture.completedFuture(new Product("2", "product2", 200.0, true)));
        when(adapter.getProductDetailsAsync("3")).thenReturn(CompletableFuture.completedFuture(new Product("3", "product3", 300.0, false)));

        List<Product> result = service.getSimilarProductDetailList("1");

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Product::getId).containsExactlyInAnyOrder("2", "3");
        assertThat(result).extracting(Product::getName).containsExactlyInAnyOrder("product2", "product3");
    }

    @Test
    void shouldGetPartialListDetail() {
        when(adapter.getRelatedProducts("1")).thenReturn(List.of("2", "3"));
        when(adapter.getProductDetailsAsync("2")).thenReturn(CompletableFuture.completedFuture(new Product("2", "product2", 200.0, true)));
        when(adapter.getProductDetailsAsync("3")).thenReturn(CompletableFuture.completedFuture(null));

        List<Product> result = service.getSimilarProductDetailList("1");

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(new Product("2", "product2", 200.0, true));
    }

}
