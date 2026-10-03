package com.rodelindev.port.out;

import com.rodelindev.model.enums.OrderStatus;

public interface NotificationService {
    void notifyOrderStatusChange(String orderId, OrderStatus status);
}
