package com.rodelindev.adapter.out.jpa;

import com.rodelindev.adapter.out.jpa.entity.OrderItemJpaEntity;
import com.rodelindev.adapter.out.jpa.entity.OrderJpaEntity;
import com.rodelindev.model.entity.Order;
import com.rodelindev.model.entity.OrderItem;
import com.rodelindev.model.enums.OrderStatus;
import com.rodelindev.model.vo.Money;
import com.rodelindev.model.vo.OrderId;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderMapperTest {

    private static final String CURRENCY = "EUR";
    private static final String ORDER_ID = "11111111-1111-1111-1111-111111111111";

    private final OrderMapper mapper = new OrderMapper();

    private Order createOrderWithTotal() {
        Order order = Order.create("customer-1");
        order.addItem(new OrderItem("P-1", "Producto 1", 2,
                Money.of(new BigDecimal("10.00"), CURRENCY)));
        order.addItem(new OrderItem("P-2", "Producto 2", 1,
                Money.of(new BigDecimal("5.50"), CURRENCY)));
        order.calculateTotal();
        return order;
    }

    private OrderJpaEntity createJpaEntityWithTotal() {
        OrderJpaEntity entity = new OrderJpaEntity();
        entity.setId(ORDER_ID);
        entity.setCustomerId("customer-1");
        entity.setStatus(OrderStatus.PAID);
        entity.setTotalAmount(new BigDecimal("25.50"));
        entity.setTotalCurrency(CURRENCY);
        entity.setCreatedAt(LocalDateTime.of(2026, 1, 15, 10, 30));

        OrderItemJpaEntity item = new OrderItemJpaEntity();
        item.setProductId("P-1");
        item.setProductName("Producto 1");
        item.setQuantity(2);
        item.setUnitPrice(new BigDecimal("10.00"));
        item.setCurrency(CURRENCY);
        item.setOrder(entity);
        entity.setItems(new ArrayList<>(List.of(item)));
        return entity;
    }

    @Test
    void toJpa_shouldMapOrderFields_whenOrderHasTotal() {

        Order order = createOrderWithTotal();

        OrderJpaEntity entity = mapper.toJpa(order);

        assertEquals(order.getId().toString(), entity.getId());
        assertEquals("customer-1", entity.getCustomerId());
        assertEquals(OrderStatus.PENDING, entity.getStatus());
        assertEquals(0, new BigDecimal("25.50").compareTo(entity.getTotalAmount()));
        assertEquals(CURRENCY, entity.getTotalCurrency());
        assertEquals(order.getCreatedAt(), entity.getCreatedAt());
    }

    @Test
    void toJpa_shouldLinkItemsToParent_whenOrderHasItems() {

        Order order = createOrderWithTotal();

        OrderJpaEntity entity = mapper.toJpa(order);

        assertEquals(2, entity.getItems().size());
        for (OrderItemJpaEntity item : entity.getItems()) {
            assertSame(entity, item.getOrder());
        }
        OrderItemJpaEntity first = entity.getItems().getFirst();
        assertEquals("P-1", first.getProductId());
        assertEquals("Producto 1", first.getProductName());
        assertEquals(2, first.getQuantity());
        assertEquals(0, new BigDecimal("10.00").compareTo(first.getUnitPrice()));
        assertEquals(CURRENCY, first.getCurrency());
        assertTrue(entity.getItems().stream()
                .anyMatch(item -> item.getProductId().equals("P-2")));
    }

    @Test
    void toJpa_shouldLeaveTotalNull_whenOrderHasNoTotal() {

        Order order = Order.create("customer-1");
        order.addItem(new OrderItem("P-1", "Producto 1", 1,
                Money.of(new BigDecimal("10.00"), CURRENCY)));

        OrderJpaEntity entity = mapper.toJpa(order);

        assertNull(entity.getTotalAmount());
        assertNull(entity.getTotalCurrency());
    }

    @Test
    void toDomain_shouldMapAllFields_whenEntityHasTotal() {

        OrderJpaEntity entity = createJpaEntityWithTotal();

        Order order = mapper.toDomain(entity);

        assertEquals(OrderId.of(ORDER_ID), order.getId());
        assertEquals("customer-1", order.getCustomerId());
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(0, new BigDecimal("25.50").compareTo(order.getTotal().amount()));
        assertEquals(CURRENCY, order.getTotal().currency());
        assertEquals(entity.getCreatedAt(), order.getCreatedAt());
        assertEquals(1, order.getItems().size());
        OrderItem item = order.getItems().getFirst();
        assertEquals("P-1", item.getProductId());
        assertEquals("Producto 1", item.getProductName());
        assertEquals(2, item.getQuantity());
        assertEquals(0, new BigDecimal("10.00").compareTo(item.getUnitPrice().amount()));
        assertEquals(CURRENCY, item.getUnitPrice().currency());
    }

    @Test
    void toDomain_shouldReturnNullTotal_whenEntityHasNoTotal() {

        OrderJpaEntity entity = createJpaEntityWithTotal();
        entity.setTotalAmount(null);
        entity.setTotalCurrency(null);

        Order order = mapper.toDomain(entity);

        assertNull(order.getTotal());
    }

    @Test
    void toDomain_shouldRoundTrip_whenMappingToJpaAndBack() {

        Order original = createOrderWithTotal();

        Order roundTripped = mapper.toDomain(mapper.toJpa(original));

        assertEquals(original.getId(), roundTripped.getId());
        assertEquals(original.getCustomerId(), roundTripped.getCustomerId());
        assertEquals(original.getStatus(), roundTripped.getStatus());
        assertEquals(original.getCreatedAt(), roundTripped.getCreatedAt());
        assertEquals(0, original.getTotal().amount().compareTo(roundTripped.getTotal().amount()));
        assertEquals(original.getTotal().currency(), roundTripped.getTotal().currency());
        assertEquals(original.getItems().size(), roundTripped.getItems().size());
        for (int i = 0; i < original.getItems().size(); i++) {
            OrderItem expected = original.getItems().get(i);
            OrderItem actual = roundTripped.getItems().get(i);
            assertEquals(expected.getProductId(), actual.getProductId());
            assertEquals(expected.getProductName(), actual.getProductName());
            assertEquals(expected.getQuantity(), actual.getQuantity());
            assertEquals(0, expected.getUnitPrice().amount().compareTo(actual.getUnitPrice().amount()));
            assertEquals(expected.getUnitPrice().currency(), actual.getUnitPrice().currency());
        }
    }
}
