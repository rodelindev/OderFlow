package com.rodelindev.service;

import com.rodelindev.command.CreateOrderCommand;
import com.rodelindev.model.entity.Order;
import com.rodelindev.port.in.CreateOrderUseCase;
import com.rodelindev.port.out.SaveOrderPort;

public class CreateOrderService implements CreateOrderUseCase {

    private final SaveOrderPort saveOrderPort;

    public CreateOrderService(SaveOrderPort saveOrderPort) {
        this.saveOrderPort = saveOrderPort;
    }

    @Override
    public Order createOrder(CreateOrderCommand command) {
        Order order = Order.create(command.customerId());
        return saveOrderPort.save(order);
    }
}
