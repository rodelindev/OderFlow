package com.rodelindev.adapter.in.rest.dto;

import java.math.BigDecimal;

public record AddItemRequest(
        String productId,
        String productName,
        int quantity,
        BigDecimal unitPrice,
        String currency
) {
}
