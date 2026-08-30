package com.rodelindev.service;

import com.rodelindev.command.AddItemToOrderCommand;
import com.rodelindev.exception.OrderDomainException;
import com.rodelindev.exception.OrderNotFoundException;
import com.rodelindev.model.entity.Order;
import com.rodelindev.model.entity.OrderItem;
import com.rodelindev.model.vo.Money;
import com.rodelindev.model.vo.OrderId;
import com.rodelindev.port.in.AddItemToOrderUseCase;
import com.rodelindev.port.out.FindOrderByIdPort;
import com.rodelindev.port.out.InventoryService;
import com.rodelindev.port.out.SaveOrderPort;

public class AddItemToOrderService implements AddItemToOrderUseCase {

    private final FindOrderByIdPort findOrderByIdPort;
    private final InventoryService inventoryService;
    private final SaveOrderPort saveOrderPort;

    public AddItemToOrderService(
            FindOrderByIdPort findOrderByIdPort,
            InventoryService inventoryService,
            SaveOrderPort saveOrderPort
    ) {
        this.findOrderByIdPort = findOrderByIdPort;
        this.inventoryService = inventoryService;
        this.saveOrderPort = saveOrderPort;
    }

    @Override
    public Order addItem(AddItemToOrderCommand command) {
        Order order = findOrderByIdPort.findById(OrderId.of(command.orderId()))
                .orElseThrow(() -> new OrderNotFoundException(command.orderId()));

        if (!inventoryService.isAvailable(command.productId(), command.quantity())) {
            throw new OrderDomainException("Product with ID " + command.productId() +
                    " is not available in the requested quantity: " + command.quantity());
        }

        Money unitPrice = Money.of(command.unitPrice(), command.currency());
        OrderItem item = new OrderItem(
                command.productId(),
                command.productName(),
                command.quantity(),
                unitPrice
        );
        order.addItem(item);
        order.calculateTotal();
        return saveOrderPort.save(order);
    }
}
