package com.rodelindev.port.in;

import com.rodelindev.model.entity.Order;

public interface GetOrderByIdUseCase {
    Order findById(String orderId);
}
