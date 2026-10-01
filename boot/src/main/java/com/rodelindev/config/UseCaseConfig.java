package com.rodelindev.config;

import com.rodelindev.port.in.AddItemToOrderUseCase;
import com.rodelindev.port.in.CancelOrderUseCase;
import com.rodelindev.port.in.CreateOrderUseCase;
import com.rodelindev.port.in.PayOrderUseCase;
import com.rodelindev.port.out.FindOrderByIdPort;
import com.rodelindev.port.out.InventoryService;
import com.rodelindev.port.out.PaymentGateway;
import com.rodelindev.port.out.SaveOrderPort;
import com.rodelindev.service.AddItemToOrderService;
import com.rodelindev.service.CancelOrderService;
import com.rodelindev.service.CreateOrderService;
import com.rodelindev.service.PayOrderService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public CreateOrderUseCase createOrderUseCase(SaveOrderPort port) {
        return new CreateOrderService(port);
    }

    @Bean
    public AddItemToOrderUseCase addItemToOrderUseCase(
            SaveOrderPort saveOrderPort,
            FindOrderByIdPort findOrderByIdPort,
            InventoryService inventoryService
    ) {
        return new AddItemToOrderService(findOrderByIdPort, inventoryService, saveOrderPort);
    }

    @Bean
    public GetOrderByIdUseCase GetOrderByIdUseCase(
            FindOrderByIdPort findOrderByIdPort
    ) {
        return new GetOrderByIdService(findOrderByIdPort);
    }

    @Bean
    public PayOrderUseCase payOrderUseCase(
            FindOrderByIdPort findOrderByIdPort,
            PaymentGateway paymentGateway,
            SaveOrderPort saveOrderPort
    ) {
        return new PayOrderService(findOrderByIdPort, paymentGateway, saveOrderPort);
    }

    @Bean
    public CancelOrderUseCase cancelOrderUseCase(
            FindOrderByIdPort findOrderByIdPort,
            SaveOrderPort saveOrderPort
    ) {
        return new CancelOrderService(findOrderByIdPort, saveOrderPort);
    }
}
