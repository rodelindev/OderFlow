package com.rodelindev.port.in;

import com.rodelindev.command.AddItemToOrderCommand;
import com.rodelindev.model.entity.Order;

public interface AddItemToOrderUseCase {
    Order addItem(AddItemToOrderCommand command);
}
