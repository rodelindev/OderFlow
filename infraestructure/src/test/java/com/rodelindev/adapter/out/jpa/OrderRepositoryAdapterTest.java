package com.rodelindev.adapter.out.jpa;

import com.rodelindev.adapter.out.jpa.entity.OrderJpaEntity;
import com.rodelindev.model.entity.Order;
import com.rodelindev.model.vo.OrderId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderRepositoryAdapterTest {

    private static final String ORDER_ID = "11111111-1111-1111-1111-111111111111";

    @Mock
    private SpringDataOrderRepository springRepository;

    @Mock
    private OrderMapper mapper;

    @InjectMocks
    private OrderRepositoryAdapter adapter;

    @Test
    void save_shouldReturnMappedDomainOrder_whenRepositorySavesEntity() {

        Order order = Order.create("customer-1");
        OrderJpaEntity entity = new OrderJpaEntity();
        OrderJpaEntity savedEntity = new OrderJpaEntity();
        Order savedOrder = Order.create("customer-1");
        when(mapper.toJpa(order)).thenReturn(entity);
        when(springRepository.save(entity)).thenReturn(savedEntity);
        when(mapper.toDomain(savedEntity)).thenReturn(savedOrder);

        Order result = adapter.save(order);

        assertSame(savedOrder, result);
        verify(mapper).toJpa(order);
        verify(springRepository).save(entity);
        verify(mapper).toDomain(savedEntity);
    }

    @Test
    void findById_shouldReturnDomainOrder_whenEntityExists() {

        OrderId orderId = OrderId.of(ORDER_ID);
        OrderJpaEntity entity = new OrderJpaEntity();
        Order order = Order.create("customer-1");
        when(springRepository.findById(ORDER_ID)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(order);

        Optional<Order> result = adapter.findById(orderId);

        assertTrue(result.isPresent());
        assertSame(order, result.get());
        verify(springRepository).findById(ORDER_ID);
        verify(mapper).toDomain(entity);
    }

    @Test
    void findById_shouldReturnEmpty_whenOrderDoesNotExist() {

        OrderId orderId = OrderId.of(ORDER_ID);
        when(springRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

        Optional<Order> result = adapter.findById(orderId);

        assertTrue(result.isEmpty());
        verify(springRepository).findById(ORDER_ID);
        verify(mapper, never()).toDomain(any());
    }
}
