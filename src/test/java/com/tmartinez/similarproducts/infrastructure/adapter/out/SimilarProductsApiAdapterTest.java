package com.tmartinez.similarproducts.infrastructure.adapter.out;

import com.tmartinez.similarproducts.application.exception.ExternalApiErrorException;
import com.tmartinez.similarproducts.application.exception.ExternalApiNotFoundException;
import com.tmartinez.similarproducts.application.exception.ExternalApiTimeoutException;
import com.tmartinez.similarproducts.domain.model.Product;
import com.tmartinez.similarproducts.infrastructure.adapter.out.api.mapper.ProductMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.ResponseCreator;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class SimilarProductsApiAdapterTest {
    private SimilarProductsApiAdapter similarProductsApiAdapter;
    private MockRestServiceServer mockRestServiceServer;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        similarProductsApiAdapter = new SimilarProductsApiAdapter(restTemplate, new ProductMapper());
        mockRestServiceServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void shouldReturnRelatedProductIdList() {
        mockRestServiceServer.expect(once(), requestTo("/product/1/similarids"))
                .andRespond(withSuccess("[\"2\", \"3\"]", MediaType.APPLICATION_JSON));

        List<String> result = similarProductsApiAdapter.getRelatedProducts("1");

        assertThat(result).containsExactly("2", "3");
        mockRestServiceServer.verify();
    }

    @Test
    void shouldThrowRelatedProductIdList() {
        mockRestServiceServer.expect(once(), requestTo("/product/1/similarids"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThrows(ExternalApiNotFoundException.class, () -> similarProductsApiAdapter.getRelatedProducts("1"));
        mockRestServiceServer.verify();
    }

    @Test
    void shouldThrowOnStatus500RelatedProductIdList() {
        mockRestServiceServer.expect(once(), requestTo("/product/1/similarids"))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrows(ExternalApiErrorException.class, () -> similarProductsApiAdapter.getRelatedProducts("1"));
        mockRestServiceServer.verify();
    }


    @Test
    void shouldThrowOnStatusTimeoutRelatedProductIdList() {
        mockRestServiceServer.expect(once(), requestTo("/product/2/similarids"))
                .andRespond(delayedResponse());

        assertThrows(ExternalApiTimeoutException.class, () -> similarProductsApiAdapter.getRelatedProducts("2"));
        mockRestServiceServer.verify();
    }

    @Test
    void shouldReturnProductDetail() {
        mockRestServiceServer.expect(once(), requestTo("/product/1"))
                .andRespond(withSuccess("{\"id\": \"1\", \"name\":\"product1\", \"price\": 100.0, \"availability\": true}", MediaType.APPLICATION_JSON));

        Product result = similarProductsApiAdapter.getProductDetails("1");

        assertNotNull(result);
        assertThat(result.getId()).isEqualTo("1");
        assertThat(result.getName()).isEqualTo("product1");
        assertThat(result.getPrice()).isEqualTo(100);
        assertThat(result.getAvailability()).isEqualTo(true);
        mockRestServiceServer.verify();
    }

    @Test
    void shouldNotThrowProductDetail() {
        mockRestServiceServer.expect(once(), requestTo("/product/1"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertDoesNotThrow(() -> similarProductsApiAdapter.getProductDetails("1"));
        mockRestServiceServer.verify();
    }

    @Test
    void shouldBeNullIfNotFoundProductDetail() {
        mockRestServiceServer.expect(once(), requestTo("/product/1"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertNull(similarProductsApiAdapter.getProductDetails("1"));
        mockRestServiceServer.verify();
    }

    @Test
    void shouldBeNullOnStatus500ProductDetail() {
        mockRestServiceServer.expect(once(), requestTo("/product/1"))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        assertNull(similarProductsApiAdapter.getProductDetails("1"));
        mockRestServiceServer.verify();
    }

    @Test
    void shouldBeNullOnTimeoutProductDetail() {
        mockRestServiceServer.expect(once(), requestTo("/product/2"))
                .andRespond(delayedResponse());

        assertNull(similarProductsApiAdapter.getProductDetails("2"));
        mockRestServiceServer.verify();
    }


    private static ResponseCreator delayedResponse() {
        return request -> { throw new ResourceAccessException("Simulated timeout"); };
    }
}