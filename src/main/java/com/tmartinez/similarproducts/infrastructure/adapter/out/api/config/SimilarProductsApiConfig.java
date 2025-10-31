package com.tmartinez.similarproducts.infrastructure.adapter.out.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SimilarProductsApiConfig {
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder, @Value("${similarproducts.api.baseurl}") String baseUrl) {
        return builder.rootUri(baseUrl).build();
    }
}
