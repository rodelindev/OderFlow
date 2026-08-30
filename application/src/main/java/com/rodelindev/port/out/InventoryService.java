package com.rodelindev.port.out;

public interface InventoryService {
    boolean isAvailable(String productId, int quantity);
}
