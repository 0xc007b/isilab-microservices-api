package com.flrxnt.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

/**
 * Configuration CORS pour le Gateway
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();

        // Origines autorisées (à adapter selon vos besoins)
        corsConfig.setAllowedOriginPatterns(Collections.singletonList("*"));

        // Méthodes HTTP autorisées
        corsConfig.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));

        // Headers autorisés
        corsConfig.setAllowedHeaders(Arrays.asList(
                "authorization", "content-type", "x-auth-token",
                "x-gateway", "x-request-time"
        ));

        // Exposer certains headers au client
        corsConfig.setExposedHeaders(Arrays.asList(
                "x-auth-token", "x-total-count", "x-gateway"
        ));

        // Autoriser les credentials
        corsConfig.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}