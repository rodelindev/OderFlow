package com.rodelindev.port.out;

import com.rodelindev.model.vo.Money;

public interface PaymentGateway {
    boolean processPayment(String orderId, Money amount);
}
