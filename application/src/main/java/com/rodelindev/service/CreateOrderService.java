package com.rodelindev.service;

import com.rodelindev.command.CreateOrderCommand;
import com.rodelindev.model.entity.Order;
import com.rodelindev.port.in.CreateOrderUseCase;

public class CreateOrderService implements CreateOrderUseCase {

    @Override
    public Order createOrder(CreateOrderCommand command) {
        return null;
    }
}
