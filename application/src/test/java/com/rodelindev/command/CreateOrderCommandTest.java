package com.rodelindev.command;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateOrderCommandTest {

    @Test
    void should_create_command_when_customer_id_is_valid() {
        CreateOrderCommand command = new CreateOrderCommand("customer-1");
        assertEquals("customer-1", command.customerId());
    }

    @Test
    void should_throw_exception_when_customer_id_is_null() {
        assertThrows(IllegalArgumentException.class, () -> new CreateOrderCommand(null));
    }

    @Test
    void should_throw_exception_when_customer_id_is_blank() {
        assertThrows(IllegalArgumentException.class, () -> new CreateOrderCommand("   "));
    }
}
