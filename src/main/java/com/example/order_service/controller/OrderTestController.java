package com.example.order_service.controller;

import com.example.order_service.client.ProductClient;
import com.example.order_service.dto.ProductResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order-test")
public class OrderTestController {

    private final ProductClient productClient;

    public OrderTestController(ProductClient productClient) {
        this.productClient = productClient;
    }

    @GetMapping("/product/{id}")
    public ProductResponse getProduct(@PathVariable Long id) {
        return productClient.getProduct(id, null);
    }
}