package com.rodelindev.port.out;

import com.rodelindev.model.entity.Order;
import com.rodelindev.model.vo.OrderId;

import java.util.Optional;

public interface FindOrderByIdPort {
    Optional<Order> findById(OrderId orderId);
}
