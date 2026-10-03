package com.rodelindev.config;

import com.rodelindev.port.in.AddItemToOrderUseCase;
import com.rodelindev.port.in.CancelOrderUseCase;
import com.rodelindev.port.in.CreateOrderUseCase;
import com.rodelindev.port.in.PayOrderUseCase;
import com.rodelindev.port.out.*;
import com.rodelindev.service.*;
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
    public PayOrderUseCase payOrderUseCase(
            FindOrderByIdPort findOrderByIdPort,
            PaymentGateway paymentGateway,
            SaveOrderPort saveOrderPort,
            NotificationService notificationService
    ) {
        return new PayOrderService(findOrderByIdPort, paymentGateway, saveOrderPort, notificationService);
    }

    @Bean
    public CancelOrderUseCase cancelOrderUseCase(
            FindOrderByIdPort findOrderByIdPort,
            SaveOrderPort saveOrderPort
    ) {
        return new CancelOrderService(findOrderByIdPort, saveOrderPort);
    }

    @Bean
    public GetOrderByIdService getOrderByIdService(FindOrderByIdPort findOrderByIdPort) {
        return new GetOrderByIdService(findOrderByIdPort);
    }
}
