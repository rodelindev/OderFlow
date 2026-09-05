package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.rodelindev")
public class OrderFlowApplication {

    static void main(String[] args){
        SpringApplication.run(OrderFlowApplication.class, args);
    }
}
