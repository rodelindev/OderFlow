package com.rodelindev.adapter.out.jpa;

import com.rodelindev.adapter.out.jpa.entity.OrderJpaEntity;
import com.rodelindev.model.entity.Order;
import com.rodelindev.model.vo.OrderId;
import com.rodelindev.port.out.FindOrderByIdPort;
import com.rodelindev.port.out.SaveOrderPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OrderRepositoryAdapter implements SaveOrderPort, FindOrderByIdPort {

    private final SpringDataOrderRepository springRepository;
    private final OrderMapper mapper;

    public OrderRepositoryAdapter(
            SpringDataOrderRepository springRepository,
            OrderMapper mapper
    ) {
        this.springRepository = springRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Order> findById(OrderId orderId) {
        return springRepository.findById(orderId.toString())
                .map(mapper::toDomain);
    }

    @Override
    public Order save(Order order) {
        OrderJpaEntity entity = mapper.toJpa(order);
        OrderJpaEntity saved = springRepository.save(entity);
        return mapper.toDomain(saved);
    }
}
