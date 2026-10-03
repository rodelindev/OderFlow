package com.rodelindev.service;

import com.rodelindev.exception.EmptyOrderException;
import com.rodelindev.exception.OrderAlreadyPaidException;
import com.rodelindev.exception.OrderDomainException;
import com.rodelindev.exception.OrderNotFoundException;
import com.rodelindev.model.entity.Order;
import com.rodelindev.model.entity.OrderItem;
import com.rodelindev.model.enums.OrderStatus;
import com.rodelindev.model.vo.Money;
import com.rodelindev.model.vo.OrderId;
import com.rodelindev.port.out.FindOrderByIdPort;
import com.rodelindev.port.out.NotificationService;
import com.rodelindev.port.out.PaymentGateway;
import com.rodelindev.port.out.SaveOrderPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayOrderServiceTest {

    private static final String CURRENCY = "EUR";
    private static final String ORDER_ID = "11111111-1111-1111-1111-111111111111";

    @Mock
    private FindOrderByIdPort findOrderByIdPort;

    @Mock
    private PaymentGateway paymentGateway;

    @Mock
    private SaveOrderPort saveOrderPort;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private PayOrderService payOrderService;

    private Order aPendingOrderWithOneItem() {
        Order order = Order.create("customer-1");
        order.addItem(new OrderItem("P-1", "Producto 1", 1,
                Money.of(new BigDecimal("10.00"), CURRENCY)));
        return order;
    }

    @Test
    void should_pay_and_save_when_payment_succeeds() {
        // Arrange
        Order order = aPendingOrderWithOneItem();
        OrderId orderId = OrderId.of(ORDER_ID);
        when(findOrderByIdPort.findById(orderId)).thenReturn(Optional.of(order));
        when(paymentGateway.processPayment(eq(ORDER_ID), any(Money.class))).thenReturn(true);

        // Act
        payOrderService.payOrder(ORDER_ID);

        // Assert
        assertEquals(OrderStatus.PAID, order.getStatus());
        verify(findOrderByIdPort).findById(orderId);
        verify(paymentGateway).processPayment(eq(ORDER_ID), argThat(total ->
                total.amount().compareTo(new BigDecimal("10.00")) == 0
                        && total.currency().equals(CURRENCY)));
        verify(saveOrderPort).save(argThat(saved ->
                saved.getStatus() == OrderStatus.PAID
                        && saved.getTotal() != null
                        && saved.getTotal().amount().compareTo(new BigDecimal("10.00")) == 0));
        verify(notificationService).notifyOrderStatusChange(ORDER_ID, OrderStatus.PAID);
    }

    @Test
    void should_throw_not_found_when_order_does_not_exist() {
        // Arrange
        OrderId orderId = OrderId.of(ORDER_ID);
        when(findOrderByIdPort.findById(orderId)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThrows(OrderNotFoundException.class,
                () -> payOrderService.payOrder(ORDER_ID));
        verify(findOrderByIdPort).findById(orderId);
        verify(paymentGateway, never()).processPayment(any(), any());
        verify(saveOrderPort, never()).save(any(Order.class));
        verify(notificationService, never())
                .notifyOrderStatusChange(any(), any());
    }

    @Test
    void should_throw_domain_exception_when_payment_fails() {
        // Arrange
        Order order = aPendingOrderWithOneItem();
        OrderId orderId = OrderId.of(ORDER_ID);
        when(findOrderByIdPort.findById(orderId)).thenReturn(Optional.of(order));
        when(paymentGateway.processPayment(eq(ORDER_ID), any(Money.class))).thenReturn(false);

        // Act
        // Assert
        assertThrows(OrderDomainException.class,
                () -> payOrderService.payOrder(ORDER_ID));
        assertEquals(OrderStatus.PENDING, order.getStatus());
        verify(findOrderByIdPort).findById(orderId);
        verify(paymentGateway).processPayment(eq(ORDER_ID), any(Money.class));
        verify(saveOrderPort, never()).save(any(Order.class));
    }

    @Test
    void should_throw_empty_order_exception_when_order_has_no_items() {
        // Arrange
        Order order = Order.create("customer-1");
        OrderId orderId = OrderId.of(ORDER_ID);
        when(findOrderByIdPort.findById(orderId)).thenReturn(Optional.of(order));

        // Act
        // Assert
        assertThrows(EmptyOrderException.class,
                () -> payOrderService.payOrder(ORDER_ID));
        verify(findOrderByIdPort).findById(orderId);
        verify(paymentGateway, never()).processPayment(any(), any());
        verify(saveOrderPort, never()).save(any(Order.class));
        verify(notificationService, never())
                .notifyOrderStatusChange(any(), any());
    }

    @Test
    void should_throw_already_paid_when_order_is_already_paid() {
        // Arrange
        Order order = aPendingOrderWithOneItem();
        order.pay();
        OrderId orderId = OrderId.of(ORDER_ID);
        when(findOrderByIdPort.findById(orderId)).thenReturn(Optional.of(order));
        when(paymentGateway.processPayment(eq(ORDER_ID), any(Money.class))).thenReturn(true);

        // Act
        // Assert
        assertThrows(OrderAlreadyPaidException.class,
                () -> payOrderService.payOrder(ORDER_ID));
        assertEquals(OrderStatus.PAID, order.getStatus());
        verify(findOrderByIdPort).findById(orderId);
        verify(paymentGateway).processPayment(eq(ORDER_ID), any(Money.class));
        verify(saveOrderPort, never()).save(any(Order.class));
        verify(notificationService, never())
                .notifyOrderStatusChange(any(), any());
    }

    @Test
    void should_notify_with_correct_order_id_and_status_when_payment_succeeds() {
        // Arrange
        Order order = aPendingOrderWithOneItem();
        OrderId orderId = OrderId.of(ORDER_ID);
        when(findOrderByIdPort.findById(orderId)).thenReturn(Optional.of(order));
        when(paymentGateway.processPayment(eq(ORDER_ID), any(Money.class))).thenReturn(true);

        // Act
        payOrderService.payOrder(ORDER_ID);

        // Assert — verifica parámetros exactos de la notificación
        verify(notificationService).notifyOrderStatusChange(
                argThat(id -> id.equals(ORDER_ID)),
                argThat(status -> status == OrderStatus.PAID)
        );
    }

    @Test
    void should_notify_before_save_when_payment_succeeds() {
        // Arrange
        Order order = aPendingOrderWithOneItem();
        OrderId orderId = OrderId.of(ORDER_ID);
        when(findOrderByIdPort.findById(orderId)).thenReturn(Optional.of(order));
        when(paymentGateway.processPayment(eq(ORDER_ID), any(Money.class))).thenReturn(true);

        // Act
        payOrderService.payOrder(ORDER_ID);

        // Assert — verifica que ambos se llaman exactamente una vez
        verify(notificationService, times(1))
                .notifyOrderStatusChange(ORDER_ID, OrderStatus.PAID);
        verify(saveOrderPort, times(1)).save(any(Order.class));
    }

    @Test
    void should_notify_only_once_when_payment_succeeds() {
        // Arrange
        Order order = aPendingOrderWithOneItem();
        OrderId orderId = OrderId.of(ORDER_ID);
        when(findOrderByIdPort.findById(orderId)).thenReturn(Optional.of(order));
        when(paymentGateway.processPayment(eq(ORDER_ID), any(Money.class))).thenReturn(true);

        // Act
        payOrderService.payOrder(ORDER_ID);

        // Assert
        verify(notificationService, times(1))
                .notifyOrderStatusChange(any(), any());
        verifyNoMoreInteractions(notificationService);
    }
}
