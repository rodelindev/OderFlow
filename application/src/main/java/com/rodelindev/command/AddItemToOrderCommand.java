package com.rodelindev.command;

import java.math.BigDecimal;

public record AddItemToOrderCommand(
        String orderId,
        String productId,
        String productName,
        int quantity,
        BigDecimal unitPrice,
        String currency
) {
    public AddItemToOrderCommand {
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("orderId must not be null or blank");
        }
        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("productId must not be null or blank");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be a positive integer");
        }
    }
}
