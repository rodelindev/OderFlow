package com.rodelindev.service;

import com.rodelindev.exception.OrderDomainException;
import com.rodelindev.exception.OrderNotFoundException;
import com.rodelindev.model.entity.Order;
import com.rodelindev.model.vo.OrderId;
import com.rodelindev.port.in.PayOrderUseCase;
import com.rodelindev.port.out.FindOrderByIdPort;
import com.rodelindev.port.out.NotificationService;
import com.rodelindev.port.out.PaymentGateway;
import com.rodelindev.port.out.SaveOrderPort;

public class PayOrderService implements PayOrderUseCase {

    private final FindOrderByIdPort findOrderByIdPort;
    private final PaymentGateway paymentGateway;
    private final SaveOrderPort saveOrderPort;
    private final NotificationService notificationService;

    public PayOrderService(
            FindOrderByIdPort findOrderByIdPort,
            PaymentGateway paymentGateway,
            SaveOrderPort saveOrderPort,
            NotificationService notificationService
    ) {
        this.findOrderByIdPort = findOrderByIdPort;
        this.paymentGateway = paymentGateway;
        this.saveOrderPort = saveOrderPort;
        this.notificationService = notificationService;
    }

    @Override
    public void payOrder(String orderId) {
        Order order = findOrderByIdPort.findById(OrderId.of(orderId))
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        order.calculateTotal();

        boolean success = paymentGateway.processPayment(orderId, order.getTotal());
        if (!success) {
            throw new OrderDomainException("Payment failed for order: " + orderId);
        }
        order.pay();
        notificationService.notifyOrderStatusChange(orderId, order.getStatus());
        saveOrderPort.save(order);
    }
}
