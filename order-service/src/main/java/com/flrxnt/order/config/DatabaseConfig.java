package com.flrxnt.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

/**
 * Minimal database configuration providing an AuditorAware bean.
 * JPA, auditing and transaction management are enabled centrally (e.g., in the main application),
 * so this class intentionally avoids any enabling annotations.
 */
@Configuration
public class DatabaseConfig {

    /**
     * Provides the current auditor for JPA auditing annotations (@CreatedBy, @LastModifiedBy).
     * Replace the static value with a security-context-based lookup when security is added.
     */
    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> Optional.of("order-service-system");
    }
}
