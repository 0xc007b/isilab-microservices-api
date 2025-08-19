package com.flrxnt.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Application principale du service Order Management
 *
 * Ce service gère les commandes et communique avec :
 * - Customer Service (via Feign) pour valider les clients
 * - Product Service (via Feign) pour valider les produits et stocks
 * - Eureka Server pour l'enregistrement de service
 */
@SpringBootApplication
@EnableFeignClients(basePackages = "com.flrxnt.order.client")
@EnableJpaRepositories(basePackages = "com.flrxnt.order.repository")
@EnableJpaAuditing
@EnableTransactionManagement
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }

}
