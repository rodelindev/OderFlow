package com.rodelindev.service;

import com.rodelindev.exception.OrderNotFoundException;
import com.rodelindev.model.entity.Order;
import com.rodelindev.model.vo.OrderId;
import com.rodelindev.port.in.GetOrderByIdUseCase;
import com.rodelindev.port.out.FindOrderByIdPort;

public class GetOrderByIdService implements GetOrderByIdUseCase {

    private final FindOrderByIdPort findOrderByIdPort;

    public GetOrderByIdService(
            FindOrderByIdPort findOrderByIdPort
    ) {
        this.findOrderByIdPort = findOrderByIdPort;
    }

    @Override
    public Order findById(String orderId) {
        return findOrderByIdPort.findById(OrderId.of(orderId))
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }
}
