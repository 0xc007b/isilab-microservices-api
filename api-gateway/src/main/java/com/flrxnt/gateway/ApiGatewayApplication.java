package com.flrxnt.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
        System.out.println("🌐 API Gateway démarré avec succès !");
        System.out.println("📍 Gateway disponible sur : http://localhost:8080");
        System.out.println("📊 Actuator disponible sur : http://localhost:8080/actuator");
    }
}