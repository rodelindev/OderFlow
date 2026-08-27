package com.rodelindev.command;

import java.math.BigDecimal;

public record AddItemToOrderCommand(
        String orderId,
        String productId,
        String productName,
        int quantity,
        BigDecimal unitPrice,
        String currency
) {
    public AddItemToOrderCommand {

    }
}
