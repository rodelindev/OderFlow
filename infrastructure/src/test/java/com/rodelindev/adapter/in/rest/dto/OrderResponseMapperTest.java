package com.rodelindev.adapter.in.rest.dto;

import com.rodelindev.model.entity.Order;
import com.rodelindev.model.entity.OrderItem;
import com.rodelindev.model.vo.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class OrderResponseMapperTest {

    private static final String CURRENCY = "EUR";

    private final OrderResponseMapper mapper = new OrderResponseMapper();

    private Order createOrderWithTotal() {
        Order order = Order.create("customer-1");
        order.addItem(new OrderItem("P-1", "Producto 1", 3,
                Money.of(new BigDecimal("10.00"), CURRENCY)));
        order.addItem(new OrderItem("P-2", "Producto 2", 2,
                Money.of(new BigDecimal("5.50"), CURRENCY)));
        order.calculateTotal();
        return order;
    }

    @Test
    void toResponse_shouldMapAllFields_whenOrderHasTotal() {

        Order order = createOrderWithTotal();

        OrderResponse response = mapper.toResponse(order);

        assertEquals(order.getId().toString(), response.id());
        assertEquals("customer-1", response.customerId());
        assertEquals(order.getStatus(), response.status());
        assertEquals(0, new BigDecimal("41.00").compareTo(response.totalAmount()));
        assertEquals(CURRENCY, response.totalCurrency());
        assertEquals(order.getCreatedAt(), response.createdAt());
        assertEquals(2, response.items().size());
        OrderItemResponse first = response.items().getFirst();
        assertEquals("P-1", first.productId());
        assertEquals("Producto 1", first.productName());
        assertEquals(3, first.quantity());
        assertEquals(0, new BigDecimal("10.00").compareTo(first.unitPrice()));
        assertEquals(0, new BigDecimal("30.00").compareTo(first.subtotal()));
        assertEquals(CURRENCY, first.currency());
        OrderItemResponse second = response.items().get(1);
        assertEquals(0, new BigDecimal("11.00").compareTo(second.subtotal()));
    }

    @Test
    void toResponse_shouldMapNullTotal_whenOrderHasNoTotal() {

        Order order = Order.create("customer-1");
        order.addItem(new OrderItem("P-1", "Producto 1", 1,
                Money.of(new BigDecimal("10.00"), CURRENCY)));

        OrderResponse response = mapper.toResponse(order);

        assertNull(response.totalAmount());
        assertNull(response.totalCurrency());
    }
}
