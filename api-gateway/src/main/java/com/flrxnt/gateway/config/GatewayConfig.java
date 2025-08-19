package com.flrxnt.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

/**
 * Configuration des routes du Gateway
 */
@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()

                // ===============================================
                // ROUTES POUR CLIENT SERVICE
                // ===============================================
                .route("client-service", r -> r
                        .path("/api/clients/**")
                        .and()
                        .method(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE)
                        .filters(f -> f
                                .stripPrefix(0)
                                .addRequestHeader("X-Gateway", "api-gateway")
                                .addRequestHeader("X-Request-Time", String.valueOf(System.currentTimeMillis()))
                        )
                        .uri("lb://client-service")
                )

                // ===============================================
                // ROUTES POUR PRODUCT SERVICE
                // ===============================================
                .route("product-service", r -> r
                        .path("/api/products/**")
                        .and()
                        .method(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE)
                        .filters(f -> f
                                .stripPrefix(0)
                                .addRequestHeader("X-Gateway", "api-gateway")
                                .addRequestHeader("X-Request-Time", String.valueOf(System.currentTimeMillis()))
                        )
                        .uri("lb://product-service")
                )

                // ===============================================
                // ROUTES POUR ORDER SERVICE
                // ===============================================
                .route("order-service", r -> r
                        .path("/api/orders/**")
                        .and()
                        .method(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE)
                        .filters(f -> f
                                .stripPrefix(0)
                                .addRequestHeader("X-Gateway", "api-gateway")
                                .addRequestHeader("X-Request-Time", String.valueOf(System.currentTimeMillis()))
                        )
                        .uri("lb://order-service")
                )

                // ===============================================
                // ROUTE POUR EUREKA SERVER (optionnel)
                // ===============================================
                .route("eureka-server", r -> r
                        .path("/eureka/**")
                        .filters(f -> f.stripPrefix(0))
                        .uri("http://eureka-server:8761")
                )

                // ===============================================
                // ROUTE DE SANTÉ GLOBALE
                // ===============================================
                .route("health-check", r -> r
                        .path("/health")
                        .filters(f -> f.stripPrefix(0))
                        .uri("http://localhost:8080/actuator/health")
                )

                .build();
    }
}
