package com.rodelindev.model.entity;

import com.rodelindev.exception.EmptyOrderException;
import com.rodelindev.exception.OrderAlreadyCancelledException;
import com.rodelindev.exception.OrderAlreadyPaidException;
import com.rodelindev.exception.OrderDomainException;
import com.rodelindev.model.enums.OrderStatus;
import com.rodelindev.model.vo.Money;
import com.rodelindev.model.vo.OrderId;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    private static final String CURRENCY = "EUR";

    private OrderItem anItem(BigDecimal unitPrice, int quantity) {
        return new OrderItem("P-1", "Producto 1", quantity, Money.of(unitPrice, CURRENCY));
    }

    // ── Fábrica create ──────────────────────────────────────────────

    @Test
    void should_create_pending_order_when_customer_id_is_valid() {

        Order order = Order.create("customer-1");

        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals("customer-1", order.getCustomerId());
        assertNotNull(order.getId());
        assertNotNull(order.getCreatedAt());
        assertTrue(order.getItems().isEmpty());
    }

    @Test
    void should_generate_different_ids_when_creating_two_orders() {

        Order first = Order.create("customer-1");
        Order second = Order.create("customer-1");

        assertNotEquals(first.getId(), second.getId());
    }

    @Test
    void should_throw_domain_exception_when_customer_id_is_null() {

        assertThrows(OrderDomainException.class, () -> Order.create(null));
    }

    @Test
    void should_throw_domain_exception_when_customer_id_is_blank() {

        assertThrows(OrderDomainException.class, () -> Order.create("   "));
    }

    // ── Gestión de items ────────────────────────────────────────────

    @Test
    void should_add_item_when_order_is_pending() {

        Order order = Order.create("customer-1");
        OrderItem item = anItem(new BigDecimal("10.00"), 1);

        order.addItem(item);

        assertEquals(1, order.getItems().size());
        assertEquals(item, order.getItems().getFirst());
    }

    @Test
    void should_throw_null_pointer_when_item_is_null() {

        Order order = Order.create("customer-1");

        assertThrows(NullPointerException.class, () -> order.addItem(null));
    }

    @Test
    void should_reject_item_when_order_is_paid() {

        Order order = Order.create("customer-1");
        order.addItem(anItem(new BigDecimal("10.00"), 1));
        order.pay();
        OrderItem item = anItem(new BigDecimal("5.00"), 1);

        assertThrows(OrderDomainException.class, () -> order.addItem(item));
    }

    @Test
    void should_reject_item_when_order_is_cancelled() {

        Order order = Order.create("customer-1");
        order.cancel();
        OrderItem item = anItem(new BigDecimal("5.00"), 1);

        assertThrows(OrderDomainException.class, () -> order.addItem(item));
    }

    @Test
    void should_return_unmodifiable_list_when_getting_items() {

        Order order = Order.create("customer-1");
        order.addItem(anItem(new BigDecimal("10.00"), 1));

        assertThrows(UnsupportedOperationException.class,
                () -> order.getItems().add(anItem(new BigDecimal("5.00"), 1)));
    }

    // ── Cálculo de total ────────────────────────────────────────────

    @Test
    void should_calculate_total_when_order_has_items() {

        Order order = Order.create("customer-1");
        order.addItem(anItem(new BigDecimal("10.00"), 2));
        order.addItem(anItem(new BigDecimal("5.50"), 1));

        order.calculateTotal();

        assertEquals(0, new BigDecimal("25.50").compareTo(order.getTotal().amount()));
        assertEquals(CURRENCY, order.getTotal().currency());
    }

    @Test
    void should_throw_empty_order_exception_when_calculating_total_without_items() {

        Order order = Order.create("customer-1");

        assertThrows(EmptyOrderException.class, order::calculateTotal);
    }

    @Test
    void should_throw_domain_exception_when_total_is_zero() {

        Order order = Order.create("customer-1");
        order.addItem(anItem(new BigDecimal("0.00"), 1));

        assertThrows(OrderDomainException.class, order::calculateTotal);
    }

    // ── Transición pay ──────────────────────────────────────────────

    @Test
    void should_pay_order_when_pending_with_items() {

        Order order = Order.create("customer-1");
        order.addItem(anItem(new BigDecimal("10.00"), 1));

        order.pay();

        assertEquals(OrderStatus.PAID, order.getStatus());
        assertNotNull(order.getTotal());
        assertEquals(0, new BigDecimal("10.00").compareTo(order.getTotal().amount()));
    }

    @Test
    void should_throw_already_paid_exception_when_paying_twice() {

        Order order = Order.create("customer-1");
        order.addItem(anItem(new BigDecimal("10.00"), 1));
        order.pay();

        assertThrows(OrderAlreadyPaidException.class, order::pay);
    }

    @Test
    void should_throw_domain_exception_when_paying_cancelled_order() {

        Order order = Order.create("customer-1");
        order.cancel();

        assertThrows(OrderDomainException.class, order::pay);
    }

    @Test
    void should_remain_pending_when_pay_fails() {

        Order order = Order.create("customer-1");

        assertThrows(EmptyOrderException.class, order::pay);

        assertEquals(OrderStatus.PENDING, order.getStatus());
    }

    // ── Transición cancel ───────────────────────────────────────────

    @Test
    void should_cancel_order_when_pending() {

        Order order = Order.create("customer-1");

        order.cancel();

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void should_throw_already_cancelled_exception_when_cancelling_paid_order() {

        Order order = Order.create("customer-1");
        order.addItem(anItem(new BigDecimal("10.00"), 1));
        order.pay();

        assertThrows(OrderAlreadyCancelledException.class, order::cancel);
    }

    @Test
    void should_throw_already_cancelled_exception_when_cancelling_twice() {

        Order order = Order.create("customer-1");
        order.cancel();

        assertThrows(OrderAlreadyCancelledException.class, order::cancel);
    }

    // ── Reconstitucion ──────────────────────────────────────────────

    @Test
    void should_preserve_all_fields_when_reconstituting() {

        OrderId id = OrderId.generate();
        LocalDateTime createdAt = LocalDateTime.of(2026, 1, 15, 10, 30);
        OrderItem item = anItem(new BigDecimal("10.00"), 2);
        Money total = Money.of(new BigDecimal("20.00"), CURRENCY);
        List<OrderItem> items = new ArrayList<>(List.of(item));

        Order order = Order.reconstitute(
                id, "customer-1", OrderStatus.PAID, total, createdAt, items);

        assertEquals(id, order.getId());
        assertEquals("customer-1", order.getCustomerId());
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(0, total.amount().compareTo(order.getTotal().amount()));
        assertEquals(createdAt, order.getCreatedAt());
        assertEquals(1, order.getItems().size());
    }

    @Test
    void should_not_be_affected_when_original_items_list_changes_after_reconstitute() {

        List<OrderItem> items = new ArrayList<>(List.of(anItem(new BigDecimal("10.00"), 1)));
        Order order = Order.reconstitute(
                OrderId.generate(), "customer-1", OrderStatus.PENDING,
                null, LocalDateTime.now(), items);

        items.clear();

        assertEquals(1, order.getItems().size());
    }
}
