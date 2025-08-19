package com.flrxnt.order.config;

import feign.Logger;
import feign.Request;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Configuration centralisée pour OpenFeign (timeouts, logs, retry, error decoding).
 *
 * NOTE:
 * - Cette configuration NE déclare PAS @EnableFeignClients pour éviter tout double enregistrement
 *   des clients Feign. L'activation du scan Feign doit être réalisée ailleurs (ex: classe principale).
 */
@Configuration
public class FeignConfig {

    /**
     * Configure les timeouts Feign (connexion et lecture).
     */
    @Bean
    public Request.Options feignRequestOptions() {
        return new Request.Options(
                10, TimeUnit.SECONDS,  // connect timeout
                60, TimeUnit.SECONDS,  // read timeout
                true                   // follow redirects
        );
    }

    /**
     * Définit le niveau de logs pour Feign.
     */
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }

    /**
     * Politique de retry pour Feign (exponentiel léger).
     */
    @Bean
    public Retryer feignRetryer() {
        // Délai initial 100ms, délai max 1s, 3 tentatives
        return new Retryer.Default(100, 1000, 3);
    }

    /**
     * Décodeur d'erreurs personnalisé pour Feign.
     */
    @Bean
    public ErrorDecoder feignErrorDecoder() {
        return new CustomFeignErrorDecoder();
    }

    /**
     * Décodeur d'erreurs personnalisé pour traiter proprement les statuts retournés
     * par les services externes.
     */
    public static class CustomFeignErrorDecoder implements ErrorDecoder {

        private final ErrorDecoder defaultErrorDecoder = new Default();

        @Override
        public Exception decode(String methodKey, feign.Response response) {
            switch (response.status()) {
                case 400:
                    return new IllegalArgumentException("Requête invalide vers " + methodKey);
                case 404:
                    if (methodKey.contains("ClientServiceClient")) {
                        return new RuntimeException("Client non trouvé");
                    } else if (methodKey.contains("ProductServiceClient")) {
                        return new RuntimeException("Produit non trouvé");
                    }
                    return new RuntimeException("Ressource non trouvée");
                case 503:
                    return new RuntimeException("Service temporairement indisponible: " + methodKey);
                default:
                    return defaultErrorDecoder.decode(methodKey, response);
            }
        }
    }
}
