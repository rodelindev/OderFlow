package com.rodelindev.adapter.out.inventory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SimulatedInventoryServiceTest {

    private final SimulatedInventoryService inventoryService = new SimulatedInventoryService();

    @Test
    void isAvailable_shouldReturnTrue_whenQuantityIsSmall() {

        boolean result = inventoryService.isAvailable("P-1", 1);

        assertTrue(result);
    }

    @Test
    void isAvailable_shouldReturnTrue_whenQuantityIsLarge() {

        boolean result = inventoryService.isAvailable("P-1", 10_000);

        assertTrue(result);
    }
}