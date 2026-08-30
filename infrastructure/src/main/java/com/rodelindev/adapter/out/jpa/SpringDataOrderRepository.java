package com.rodelindev.adapter.out.jpa;

import com.rodelindev.adapter.out.jpa.entity.OrderJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataOrderRepository extends JpaRepository<OrderJpaEntity,String> { }
