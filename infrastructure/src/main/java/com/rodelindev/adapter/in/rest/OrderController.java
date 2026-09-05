package com.rodelindev.adapter.in.rest;

import com.rodelindev.adapter.in.rest.dto.AddItemRequest;
import com.rodelindev.adapter.in.rest.dto.CreateOrderRequest;
import com.rodelindev.adapter.in.rest.dto.OrderResponse;
import com.rodelindev.adapter.in.rest.dto.OrderResponseMapper;
import com.rodelindev.command.AddItemToOrderCommand;
import com.rodelindev.command.CreateOrderCommand;
import com.rodelindev.model.entity.Order;
import com.rodelindev.port.in.AddItemToOrderUseCase;
import com.rodelindev.port.in.CancelOrderUseCase;
import com.rodelindev.port.in.CreateOrderUseCase;
import com.rodelindev.port.in.PayOrderUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final AddItemToOrderUseCase addItemToOrderUseCase;
    private final PayOrderUseCase payOrderUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;
    private final OrderResponseMapper responseMapper;

    public OrderController(
            CreateOrderUseCase createOrderUseCase,
            AddItemToOrderUseCase addItemToOrderUseCase,
            PayOrderUseCase payOrderUseCase,
            CancelOrderUseCase cancelOrderUseCase,
            OrderResponseMapper responseMapper
    ) {
        this.createOrderUseCase = createOrderUseCase;
        this.addItemToOrderUseCase = addItemToOrderUseCase;
        this.payOrderUseCase = payOrderUseCase;
        this.cancelOrderUseCase = cancelOrderUseCase;
        this.responseMapper = responseMapper;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody CreateOrderRequest req
    ) {

        Order order = createOrderUseCase.createOrder(new CreateOrderCommand(req.customerId()));

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(order.getId())
                .toUri();

        return ResponseEntity.created(location).body(responseMapper.toResponse(order));
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<OrderResponse> addItem(
            @PathVariable String id,
            @RequestBody AddItemRequest req
    ) {
        Order order = addItemToOrderUseCase.addItem(new AddItemToOrderCommand(
                id,
                req.productId(),
                req.productName(),
                req.quantity(),
                req.unitPrice(),
                req.currency()
        ));

        return ResponseEntity.ok(responseMapper.toResponse(order));
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<Void> payOrder(@PathVariable String id) {
        payOrderUseCase.payOrder(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable String id) {
        cancelOrderUseCase.cancelOrder(id);
        return ResponseEntity.noContent().build();
    }
}
