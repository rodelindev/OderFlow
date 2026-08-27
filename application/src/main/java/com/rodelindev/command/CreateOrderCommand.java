package com.rodelindev.command;

public record CreateOrderCommand(
    String customerId
) {
    public CreateOrderCommand {
        if (customerId == null || customerId.isBlank()) {
            throw new IllegalArgumentException("customerId cannot be null");
        }
    }
}
