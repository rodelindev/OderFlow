package com.rodelindev.e2e;

import com.rodelindev.adapter.in.rest.dto.AddItemRequest;
import com.rodelindev.adapter.in.rest.dto.CreateOrderRequest;
import com.rodelindev.adapter.in.rest.dto.OrderResponse;
import com.rodelindev.config.TestSecurityConfig;
import com.rodelindev.model.enums.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@Import(TestSecurityConfig.class)
public class OrderFlowE2ETest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void fullOrderFlow_createAddItemAndPay() {
        // 1. Crear la orden
        ResponseEntity<OrderResponse> createResponse = testRestTemplate.postForEntity("/api/orders",
                new CreateOrderRequest("customer-e2e"),
                OrderResponse.class);

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        OrderResponse created = createResponse.getBody();
        assertNotNull(created);
        assertNotNull(created.id());
        assertEquals("customer-e2e", created.customerId());
        assertEquals(OrderStatus.PENDING, created.status());
        assertTrue(created.items().isEmpty());

        String orderId = created.id();

        // 2. Añadir un item
        ResponseEntity<OrderResponse> addItemResponse = testRestTemplate.postForEntity(
                "/api/orders/" + orderId + "/items",
                new AddItemRequest("PROD-1", "Laptop", 2,
                        new BigDecimal("100.00"), "EUR"),
                OrderResponse.class);

        assertEquals(HttpStatus.OK, addItemResponse.getStatusCode());
        OrderResponse withItem = addItemResponse.getBody();
        assertNotNull(withItem);
        assertEquals(1, withItem.items().size());
        assertEquals("PROD-1", withItem.items().getFirst().productId());
        assertEquals("Laptop", withItem.items().getFirst().productName());
        assertEquals(2, withItem.items().getFirst().quantity());
        assertEquals(OrderStatus.PENDING, withItem.status());
        assertEquals(new BigDecimal("200.00"), withItem.items().getFirst().subtotal());

        // 3. Pagar la orden
        boolean paid = false;

        ResponseEntity<Void> payResponse = testRestTemplate.postForEntity(
                "/api/orders/" + orderId + "/pay",
                null,
                Void.class);

        paid = payResponse.getStatusCode() == HttpStatus.NO_CONTENT;
        assertTrue(paid, "El pago debería haber tenido éxito.");
    }

}
