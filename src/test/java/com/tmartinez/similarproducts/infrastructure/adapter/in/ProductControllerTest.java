package com.tmartinez.similarproducts.infrastructure.adapter.in;

import com.tmartinez.similarproducts.application.port.in.GetSimilarProductDetailListUseCase;
import com.tmartinez.similarproducts.domain.model.Product;
import com.tmartinez.similarproducts.infrastructure.adapter.in.mapper.ProductDetailMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@Import(ProductDetailMapper.class)
public class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetSimilarProductDetailListUseCase service;

    @Test
    void shouldReturnStatus200WithProductDetails() throws Exception {
        when(service.getSimilarProductDetailList("2"))
                .thenReturn(List.of(
                            new Product("1", "product1", 100.0,true),
                            new Product("3", "product3", 300.0, false)));
        mockMvc.perform(get("/product/2/similar"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.similarProducts[0].id").value("1"));
    }

    @Test
    void shouldReturnStatus404() throws Exception {
        when(service.getSimilarProductDetailList("2"))
            .thenReturn(List.of());
        mockMvc.perform(get("/product/2/similar"))
                .andExpect(status().isNotFound());
    }

}
