package com.rodelindev.port.out;

import com.rodelindev.model.entity.Order;

public interface SaveOrderPort {
    Order save(Order order);
}
