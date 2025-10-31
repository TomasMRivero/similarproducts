package com.tmartinez.similarproducts.application.service;

import com.tmartinez.similarproducts.application.port.out.SimilarProductsOutPort;
import com.tmartinez.similarproducts.domain.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class GetSimilarProductsServiceTest {

    private SimilarProductsOutPort similarProductsOutPort;
    private GetSimilarProductsService service;

    @BeforeEach
    public void setUp()
    {
        similarProductsOutPort = Mockito.mock(SimilarProductsOutPort.class);
        service = new GetSimilarProductsService(similarProductsOutPort);
    }

    @Test
    void shouldReturnDetailsForAllRelatedProducts() {
        when(similarProductsOutPort.getRelatedProducts("1"))
                .thenReturn(List.of("2","3"));

        when(similarProductsOutPort.getProductDetails("2"))
                .thenReturn(new Product("2", "product2", 100.0, true));
        when(similarProductsOutPort.getProductDetails("3"))
            .thenReturn(new Product("3", "product3", 200.0, false));

        List<Product> result = service.execute("1");

        assertThat(result).hasSize(2);
        assertThat(result).extracting("id").containsExactlyInAnyOrder("2", "3");

    }
}
