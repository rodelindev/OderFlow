package com.rodelindev.adapter.out.payment;

import com.rodelindev.model.vo.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SimulatedPaymentGatewayTest {

    private final SimulatedPaymentGateway gateway = new SimulatedPaymentGateway();

    @Test
    void processPayment_shouldReturnTrue_whenAmountIsPositive() {

        Money amount = Money.of(new BigDecimal("25.50"), "EUR");

        boolean result = gateway.processPayment("11111111-1111-1111-1111-111111111111", amount);

        assertTrue(result);
    }

    @Test
    void processPayment_shouldReturnTrue_whenAmountIsZero() {

        Money amount = Money.zero("EUR");

        boolean result = gateway.processPayment("11111111-1111-1111-1111-111111111111", amount);

        assertTrue(result);
    }
}
