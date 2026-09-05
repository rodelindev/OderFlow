package com.rodelindev.adapter.in.rest.dto;

import com.rodelindev.model.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        String id,
        String customerId,
        OrderStatus status,
        BigDecimal totalAmount,
        String totalCurrency,
        LocalDateTime createdAt,
        List<OrderItemResponse> items
) {
}
