package com.rodelindev.service;

import com.rodelindev.command.AddItemToOrderCommand;
import com.rodelindev.exception.OrderDomainException;
import com.rodelindev.exception.OrderNotFoundException;
import com.rodelindev.model.entity.Order;
import com.rodelindev.model.entity.OrderItem;
import com.rodelindev.model.enums.OrderStatus;
import com.rodelindev.model.vo.Money;
import com.rodelindev.model.vo.OrderId;
import com.rodelindev.port.out.FindOrderByIdPort;
import com.rodelindev.port.out.InventoryService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddItemToOrderServiceTest {

    private static final String CURRENCY = "EUR";
    private static final String ORDER_ID = "11111111-1111-1111-1111-111111111111";

    @Mock
    private FindOrderByIdPort findOrderByIdPort;

    @Mock
    private InventoryService inventoryService;

    @Mock
    private SaveOrderPort saveOrderPort;

    @InjectMocks
    private AddItemToOrderService addItemToOrderService;

    private Order aPendingOrder() {
        return Order.create("customer-1");
    }

    @Test
    void should_add_item_and_save_when_order_exists_and_product_is_available() {
        // Arrange
        Order order = aPendingOrder();
        OrderId orderId = OrderId.of(ORDER_ID);
        when(findOrderByIdPort.findById(orderId)).thenReturn(Optional.of(order));
        when(inventoryService.isAvailable("P-1", 2)).thenReturn(true);
        when(saveOrderPort.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        AddItemToOrderCommand command = new AddItemToOrderCommand(
                ORDER_ID, "P-1", "Producto 1", 2, new BigDecimal("10.00"), CURRENCY);

        // Act
        Order result = addItemToOrderService.addItem(command);

        // Assert
        assertEquals(1, result.getItems().size());
        OrderItem item = result.getItems().getFirst();
        assertEquals("P-1", item.getProductId());
        assertEquals(2, item.getQuantity());
        assertEquals(0, new BigDecimal("20.00").compareTo(result.getTotal().amount()));
        assertEquals(CURRENCY, result.getTotal().currency());
        assertEquals(OrderStatus.PENDING, result.getStatus());
        verify(findOrderByIdPort).findById(orderId);
        verify(inventoryService).isAvailable("P-1", 2);
        verify(saveOrderPort).save(argThat(saved ->
                saved.getItems().size() == 1
                        && saved.getTotal() != null
                        && saved.getTotal().amount().compareTo(new BigDecimal("20.00")) == 0));
    }

    @Test
    void should_throw_not_found_when_order_does_not_exist() {
        // Arrange
        OrderId orderId = OrderId.of(ORDER_ID);
        when(findOrderByIdPort.findById(orderId)).thenReturn(Optional.empty());
        AddItemToOrderCommand command = new AddItemToOrderCommand(
                ORDER_ID, "P-1", "Producto 1", 1, new BigDecimal("10.00"), CURRENCY);

        // Act
        // Assert
        assertThrows(OrderNotFoundException.class,
                () -> addItemToOrderService.addItem(command));
        verify(findOrderByIdPort).findById(orderId);
        verify(inventoryService, never()).isAvailable("P-1", 1);
        verify(saveOrderPort, never()).save(any(Order.class));
    }

    @Test
    void should_throw_domain_exception_when_product_is_not_available() {
        // Arrange
        Order order = aPendingOrder();
        OrderId orderId = OrderId.of(ORDER_ID);
        when(findOrderByIdPort.findById(orderId)).thenReturn(Optional.of(order));
        when(inventoryService.isAvailable("P-1", 1)).thenReturn(false);
        AddItemToOrderCommand command = new AddItemToOrderCommand(
                ORDER_ID, "P-1", "Producto 1", 1, new BigDecimal("10.00"), CURRENCY);

        // Act
        // Assert
        assertThrows(OrderDomainException.class,
                () -> addItemToOrderService.addItem(command));
        verify(findOrderByIdPort).findById(orderId);
        verify(inventoryService).isAvailable("P-1", 1);
        verify(saveOrderPort, never()).save(any(Order.class));
    }

    @Test
    void should_propagate_domain_exception_when_order_is_not_pending() {
        // Arrange
        Order order = aPendingOrder();
        order.addItem(new OrderItem("P-1", "Producto 1", 1,
                Money.of(new BigDecimal("10.00"), CURRENCY)));
        order.pay();
        OrderId orderId = OrderId.of(ORDER_ID);
        when(findOrderByIdPort.findById(orderId)).thenReturn(Optional.of(order));
        when(inventoryService.isAvailable("P-2", 1)).thenReturn(true);
        AddItemToOrderCommand command = new AddItemToOrderCommand(
                ORDER_ID, "P-2", "Producto 2", 1, new BigDecimal("5.00"), CURRENCY);

        // Act
        // Assert
        assertThrows(OrderDomainException.class,
                () -> addItemToOrderService.addItem(command));
        verify(findOrderByIdPort).findById(orderId);
        verify(inventoryService).isAvailable("P-2", 1);
        verify(saveOrderPort, never()).save(any(Order.class));
    }

    @Test
    void should_propagate_domain_exception_when_calculated_total_is_zero() {
        // Arrange
        Order order = aPendingOrder();
        OrderId orderId = OrderId.of(ORDER_ID);
        when(findOrderByIdPort.findById(orderId)).thenReturn(Optional.of(order));
        when(inventoryService.isAvailable("P-1", 1)).thenReturn(true);
        AddItemToOrderCommand command = new AddItemToOrderCommand(
                ORDER_ID, "P-1", "Producto 1", 1, new BigDecimal("0.00"), CURRENCY);

        // Act
        // Assert
        assertThrows(OrderDomainException.class,
                () -> addItemToOrderService.addItem(command));
        verify(findOrderByIdPort).findById(orderId);
        verify(inventoryService).isAvailable("P-1", 1);
        verify(saveOrderPort, never()).save(any(Order.class));
    }
}
