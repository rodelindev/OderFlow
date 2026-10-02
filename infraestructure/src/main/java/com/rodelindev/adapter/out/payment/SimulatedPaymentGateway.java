package com.rodelindev.adapter.out.payment;

import com.rodelindev.model.vo.Money;
import com.rodelindev.port.out.PaymentGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SimulatedPaymentGateway implements PaymentGateway {

    private static final Logger log = LoggerFactory.getLogger(SimulatedPaymentGateway.class);

    @Override
    public boolean processPayment(String orderId, Money amount) {
        log.info("[PAYMENT-SIMULATED] Processing for order {}", orderId);
        return true;
    }
}
