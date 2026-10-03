package com.rodelindev.adapter.out.notification;

import com.rodelindev.model.enums.OrderStatus;
import com.rodelindev.port.out.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SimulatedNotificationService implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(SimulatedNotificationService.class);

    @Override
    public void notifyOrderStatusChange(String orderId, OrderStatus status) {
        log.info("[NOTIFICATION-SIMULATED] Processing for order {} with status {}", orderId, status);
    }
}
