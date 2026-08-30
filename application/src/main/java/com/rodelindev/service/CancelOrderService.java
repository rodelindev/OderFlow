package com.rodelindev.service;

import com.rodelindev.exception.OrderNotFoundException;
import com.rodelindev.model.entity.Order;
import com.rodelindev.model.vo.OrderId;
import com.rodelindev.port.in.CancelOrderUseCase;
import com.rodelindev.port.out.FindOrderByIdPort;
import com.rodelindev.port.out.SaveOrderPort;

public class CancelOrderService implements CancelOrderUseCase {

    private final FindOrderByIdPort findOrderByIdPort;
    private final SaveOrderPort saveOrderPort;

    public CancelOrderService(FindOrderByIdPort findOrderByIdPort, SaveOrderPort saveOrderPort) {
        this.findOrderByIdPort = findOrderByIdPort;
        this.saveOrderPort = saveOrderPort;
    }

    @Override
    public void cancelOrder(String orderId) {
        Order order = findOrderByIdPort.findById(OrderId.of(orderId))
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        order.cancel();
        saveOrderPort.save(order);
    }
}
