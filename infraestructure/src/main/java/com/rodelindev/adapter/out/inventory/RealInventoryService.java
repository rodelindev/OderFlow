package com.rodelindev.adapter.out.inventory;

import com.rodelindev.adapter.out.inventory.dto.InventoryRequestDTO;
import com.rodelindev.adapter.out.inventory.dto.InventoryResponseDTO;
import com.rodelindev.port.out.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Duration;

@Component
@ConditionalOnProperty(prefix = "inventory", name = "mode", havingValue = "real")
public class RealInventoryService implements InventoryService {

    private static final Logger log = LoggerFactory.getLogger(RealInventoryService.class);

    private final RestClient restClient;

    public RealInventoryService(
            @Value("${inventory.api.url}") String apiUrl,
            @Value("${inventory.api.connect-timeout-ms}") long connectTimeoutMs,
            @Value("${inventory.api.read-timeout-ms}") long readTimeoutMs
    ) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofMillis(connectTimeoutMs));
        requestFactory.setReadTimeout(Duration.ofMillis(readTimeoutMs));

        this.restClient = RestClient.builder()
                .requestFactory(requestFactory)
                .baseUrl(apiUrl)
                .build();
    }

    @Override
    public boolean isAvailable(String productId, int quantity) {
        log.info("[INVENTORY-REAL] Checking availability for product {} and quantity {}", productId, quantity);

        try {
            InventoryResponseDTO responseDTO = restClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new InventoryRequestDTO(productId, quantity))
                    .retrieve()
                    .body(InventoryResponseDTO.class);

            return responseDTO != null && Boolean.TRUE.equals(responseDTO.status());
        } catch (RestClientException ex) {
            log.error("[INVENTORY-REAL] Error checking availability for product {}: {}", productId, ex.getMessage(), ex);
            return false;
        }
    }
}
