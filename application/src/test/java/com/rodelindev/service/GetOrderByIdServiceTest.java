package com.rodelindev.service;

import com.rodelindev.exception.OrderNotFoundException;
import com.rodelindev.model.entity.Order;
import com.rodelindev.model.vo.OrderId;
import com.rodelindev.port.out.FindOrderByIdPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetOrderByIdServiceTest {

    private static final String ORDER_ID = "11111111-1111-1111-1111-111111111111";

    @Mock
    private FindOrderByIdPort findOrderByIdPort;

    @InjectMocks
    private GetOrderByIdService getOrderByIdService;

    @Test
    void should_return_order_when_order_exists() {
        // Arrange
        OrderId orderId = OrderId.of(ORDER_ID);
        Order expectedOrder = Order.create("customer-1");
        when(findOrderByIdPort.findById(orderId))
                .thenReturn(Optional.of(expectedOrder));

        // Act
        Order result = getOrderByIdService.findById(ORDER_ID);

        // Assert
        assertNotNull(result);
        assertEquals(expectedOrder, result);
        verify(findOrderByIdPort, times(1)).findById(orderId);
    }

    @Test
    void should_throw_exception_when_order_not_found() {
        // Arrange
        OrderId orderId = OrderId.of(ORDER_ID);
        when(findOrderByIdPort.findById(orderId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                OrderNotFoundException.class,
                () -> getOrderByIdService.findById(ORDER_ID)
        );
        verify(findOrderByIdPort, times(1)).findById(orderId);
    }
}
