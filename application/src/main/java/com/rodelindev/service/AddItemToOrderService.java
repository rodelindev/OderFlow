package com.rodelindev.service;

import com.rodelindev.command.AddItemToOrderCommand;
import com.rodelindev.model.entity.Order;
import com.rodelindev.port.in.AddItemToOrderUseCase;

public class AddItemToOrderService implements AddItemToOrderUseCase {

    @Override
    public Order addItem(AddItemToOrderCommand command) {
        return null;
    }
}
