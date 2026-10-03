package com.rodelindev.adapter.out.notification;

import com.rodelindev.model.enums.OrderStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class SimulatedNotificationServiceTest {

    private final SimulatedNotificationService notificationService = new SimulatedNotificationService();

    @Test
    void should_not_throw_when_notifying_paid_status() {
        // Arrange
        String orderId = "11111111-1111-1111-1111-111111111111";

        // Act & Assert
        assertDoesNotThrow(() ->
                notificationService.notifyOrderStatusChange(orderId, OrderStatus.PAID)
        );
    }

    @Test
    void should_not_throw_when_notifying_cancelled_status() {
        // Arrange
        String orderId = "11111111-1111-1111-1111-111111111111";

        // Act & Assert
        assertDoesNotThrow(() ->
                notificationService.notifyOrderStatusChange(orderId, OrderStatus.CANCELLED)
        );
    }

    @Test
    void should_not_throw_when_notifying_pending_status() {
        // Arrange
        String orderId = "22222222-2222-2222-2222-222222222222";

        // Act & Assert
        assertDoesNotThrow(() ->
                notificationService.notifyOrderStatusChange(orderId, OrderStatus.PENDING)
        );
    }
}