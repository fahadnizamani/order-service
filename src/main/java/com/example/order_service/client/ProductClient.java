package com.example.order_service.client;

import com.example.order_service.dto.ProductResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ProductClient {

    private final RestClient restClient;

    public ProductClient(
            @Qualifier("loadBalancedRestClientBuilder")
            RestClient.Builder restClientBuilder) {

        this.restClient = restClientBuilder
                .baseUrl("http://PRODUCT-SERVICE")
                .build();
    }

    public ProductResponse getProduct(
            Long productId,
            String authorizationHeader) {

        System.out.println(
                ">>> ORDER-SERVICE calling PRODUCT-SERVICE for product: "
                        + productId
        );

        return restClient.get()
                .uri("/api/products/{id}", productId)
                .header("Authorization", authorizationHeader)
                .retrieve()
                .body(ProductResponse.class);
    }
}