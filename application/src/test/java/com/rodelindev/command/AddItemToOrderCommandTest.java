package com.rodelindev.command;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AddItemToOrderCommandTest {

    @Test
    void should_create_command_when_all_fields_are_valid() {

        AddItemToOrderCommand command = new AddItemToOrderCommand(
                "11111111-1111-1111-1111-111111111111",
                "P-1",
                "Producto 1",
                2,
                new BigDecimal("10.00"),
                "EUR");

        assertEquals("11111111-1111-1111-1111-111111111111", command.orderId());
        assertEquals("P-1", command.productId());
        assertEquals("Producto 1", command.productName());
        assertEquals(2, command.quantity());
        assertEquals(0, new BigDecimal("10.00").compareTo(command.unitPrice()));
        assertEquals("EUR", command.currency());
    }

    @Test
    void should_throw_exception_when_order_id_is_null() {

        assertThrows(IllegalArgumentException.class,
                () -> new AddItemToOrderCommand(null, "P-1", "Producto 1", 1,
                        new BigDecimal("10.00"), "EUR"));
    }

    @Test
    void should_throw_exception_when_order_id_is_blank() {

        assertThrows(IllegalArgumentException.class,
                () -> new AddItemToOrderCommand("   ", "P-1", "Producto 1", 1,
                        new BigDecimal("10.00"), "EUR"));
    }

    @Test
    void should_throw_exception_when_product_id_is_null() {

        assertThrows(IllegalArgumentException.class,
                () -> new AddItemToOrderCommand("11111111-1111-1111-1111-111111111111",
                        null, "Producto 1", 1, new BigDecimal("10.00"), "EUR"));
    }

    @Test
    void should_throw_exception_when_product_id_is_blank() {

        assertThrows(IllegalArgumentException.class,
                () -> new AddItemToOrderCommand("11111111-1111-1111-1111-111111111111",
                        "   ", "Producto 1", 1, new BigDecimal("10.00"), "EUR"));
    }

    @Test
    void should_throw_exception_when_quantity_is_zero() {

        assertThrows(IllegalArgumentException.class,
                () -> new AddItemToOrderCommand("11111111-1111-1111-1111-111111111111",
                        "P-1", "Producto 1", 0, new BigDecimal("10.00"), "EUR"));
    }

    @Test
    void should_throw_exception_when_quantity_is_negative() {

        assertThrows(IllegalArgumentException.class,
                () -> new AddItemToOrderCommand("11111111-1111-1111-1111-111111111111",
                        "P-1", "Producto 1", -3, new BigDecimal("10.00"), "EUR"));
    }
}