package com.rodelindev.adapter.out.jpa;

import com.rodelindev.adapter.out.jpa.entity.OrderItemJpaEntity;
import com.rodelindev.adapter.out.jpa.entity.OrderJpaEntity;
import com.rodelindev.model.entity.Order;
import com.rodelindev.model.entity.OrderItem;
import com.rodelindev.model.vo.Money;
import com.rodelindev.model.vo.OrderId;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {

    public OrderJpaEntity toJpa(Order order) {
        OrderJpaEntity e = new OrderJpaEntity();
        e.setId(order.getId().toString());
        e.setCustomerId(order.getCustomerId());
        e.setStatus(order.getStatus());
        if (order.getTotal() != null) {
            e.setTotalAmount(order.getTotal().amount());
            e.setTotalCurrency(order.getTotal().currency());
        }
        e.setCreatedAt(order.getCreatedAt());

        List<OrderItemJpaEntity> itemJpaEntities = order.getItems().stream()
                .map(item -> toItemJpa(item, e))
                .toList();

        e.setItems(itemJpaEntities);
        return e;
    }

    private OrderItemJpaEntity toItemJpa(OrderItem item, OrderJpaEntity parent) {
        OrderItemJpaEntity e = new OrderItemJpaEntity();
        e.setOrder(parent);
        e.setProductId(item.getProductId());
        e.setProductName(item.getProductName());
        e.setQuantity(item.getQuantity());
        e.setUnitPrice(item.getUnitPrice().amount());
        e.setCurrency(item.getUnitPrice().currency());
        return e;
    }

    public Order toDomain(OrderJpaEntity entity) {
        Money total = (entity.getTotalAmount() != null && entity.getTotalCurrency() != null)
                ? Money.of(entity.getTotalAmount(), entity.getTotalCurrency())
                : null;

        List<OrderItem> items = entity.getItems().stream()
                .map(this::toItemDomain)
                .toList();

        return Order.reconstitute(
                OrderId.of(entity.getId()),
                entity.getCustomerId(),
                entity.getStatus(),
                total,
                entity.getCreatedAt(),
                items
        );
    }

    private OrderItem toItemDomain(OrderItemJpaEntity entity) {
        return new OrderItem(
                entity.getProductId(),
                entity.getProductName(),
                entity.getQuantity(),
                Money.of(entity.getUnitPrice(), entity.getCurrency())
        );
    }
}
