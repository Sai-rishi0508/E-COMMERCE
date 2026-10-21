package com.rishi.springbootecom.model.dto;

public record OrderItemRequest(
        int productId,
        int quantity
) {}
