package com.rodelindev.exception;

public class EmptyOrderException extends OrderDomainException {

    public EmptyOrderException() {
        super("An order must have at least one item.");
    }
}
