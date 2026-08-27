package com.rodelindev.model.enums;

public enum OrderStatus {
    PENDING,
    PAID,
    CANCELLED;

    public boolean canBePaidOrCancelled(){
        return this == PENDING;
    }

    public boolean canItemBeAdded(){
        return this == PENDING;
    }
}
