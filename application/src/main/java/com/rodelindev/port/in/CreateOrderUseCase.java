package com.rodelindev.port.in;

import com.rodelindev.command.CreateOrderCommand;
import com.rodelindev.model.entity.Order;

public interface CreateOrderUseCase {
    Order createOrder(CreateOrderCommand command);
}
