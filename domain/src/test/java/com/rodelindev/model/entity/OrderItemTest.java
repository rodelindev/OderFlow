package com.rodelindev.model.entity;

import com.rodelindev.exception.OrderDomainException;
import com.rodelindev.model.vo.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderItemTest {

    private static final String CURRENCY = "EUR";

    // ── Creación ────────────────────────────────────────────────────

    @Test
    void should_create_item_when_all_fields_are_valid() {

        OrderItem item = new OrderItem(
                "P-1", "Producto 1", 3, Money.of(new BigDecimal("12.50"), CURRENCY));

        assertEquals("P-1", item.getProductId());
        assertEquals("Producto 1", item.getProductName());
        assertEquals(3, item.getQuantity());
        assertEquals(0, new BigDecimal("12.50").compareTo(item.getUnitPrice().amount()));
        assertEquals(CURRENCY, item.getUnitPrice().currency());
    }

    @Test
    void should_throw_null_pointer_when_product_id_is_null() {

        assertThrows(NullPointerException.class,
                () -> new OrderItem(null, "Producto 1", 1,
                        Money.of(new BigDecimal("10.00"), CURRENCY)));
    }

    @Test
    void should_throw_null_pointer_when_product_name_is_null() {

        assertThrows(NullPointerException.class,
                () -> new OrderItem("P-1", null, 1,
                        Money.of(new BigDecimal("10.00"), CURRENCY)));
    }

    @Test
    void should_throw_null_pointer_when_unit_price_is_null() {

        assertThrows(NullPointerException.class,
                () -> new OrderItem("P-1", "Producto 1", 1, null));
    }

    @Test
    void should_throw_domain_exception_when_product_id_is_blank() {

        assertThrows(OrderDomainException.class,
                () -> new OrderItem("   ", "Producto 1", 1,
                        Money.of(new BigDecimal("10.00"), CURRENCY)));
    }

    @Test
    void should_throw_domain_exception_when_quantity_is_zero() {

        assertThrows(OrderDomainException.class,
                () -> new OrderItem("P-1", "Producto 1", 0,
                        Money.of(new BigDecimal("10.00"), CURRENCY)));
    }

    @Test
    void should_throw_domain_exception_when_quantity_is_negative() {

        assertThrows(OrderDomainException.class,
                () -> new OrderItem("P-1", "Producto 1", -2,
                        Money.of(new BigDecimal("10.00"), CURRENCY)));
    }

    // ── Subtotal ────────────────────────────────────────────────────

    @Test
    void should_calculate_subtotal_when_quantity_is_greater_than_one() {

        OrderItem item = new OrderItem(
                "P-1", "Producto 1", 3, Money.of(new BigDecimal("12.50"), CURRENCY));

        Money subtotal = item.calculateSubtotal();

        assertEquals(0, new BigDecimal("37.50").compareTo(subtotal.amount()));
        assertEquals(CURRENCY, subtotal.currency());
    }

    @Test
    void should_calculate_subtotal_when_quantity_is_one() {

        OrderItem item = new OrderItem(
                "P-1", "Producto 1", 1, Money.of(new BigDecimal("12.50"), CURRENCY));

        Money subtotal = item.calculateSubtotal();

        assertEquals(0, new BigDecimal("12.50").compareTo(subtotal.amount()));
    }
}