package com.rodelindev.adapter.out.inventory;

import com.rodelindev.port.out.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SimulatedInventoryService implements InventoryService {

    private static final Logger log = LoggerFactory.getLogger(SimulatedInventoryService.class);

    @Override
    public boolean isAvailable(String productId, int quantity) {
        log.info("[INVENTORY-SIMULATED] Checking availability for product {} and quantity {}", productId, quantity);
        return true;
    }
}
