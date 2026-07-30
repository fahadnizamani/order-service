package com.example.order_service.client;

import com.example.order_service.dto.UserResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class UserClient {

    private final RestClient restClient;

    public UserClient(
            @Qualifier("restClientBuilder")
            RestClient.Builder restClientBuilder) {

        this.restClient = restClientBuilder
                .baseUrl("http://localhost:8080")
                .build();
    }

    public UserResponse getUserByEmail(
            String email,
            String authorizationHeader) {

        System.out.println(
                ">>> ORDER-SERVICE looking up user by email: " + email
        );

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/users/by-email")
                        .queryParam("email", email)
                        .build())
                .header("Authorization", authorizationHeader)
                .retrieve()
                .body(UserResponse.class);
    }
}