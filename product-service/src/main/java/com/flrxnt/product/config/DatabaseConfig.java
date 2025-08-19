package com.flrxnt.product.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Optional;

@Configuration
@EnableJpaRepositories(basePackages = "com.flrxnt.product.repository")
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@EnableTransactionManagement
public class DatabaseConfig {

    /**
     * Configuration de l'auditeur pour JPA Auditing
     * Peut être étendu pour récupérer l'utilisateur connecté
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return new SpringSecurityAuditorAware();
    }

    /**
     * Implémentation simple de l'auditeur
     * Dans un vrai projet, ceci devrait récupérer l'utilisateur authentifié
     */
    public static class SpringSecurityAuditorAware implements AuditorAware<String> {
        @Override
        public Optional<String> getCurrentAuditor() {
            // TODO: Intégrer avec Spring Security pour récupérer l'utilisateur connecté
            // Pour l'instant, on retourne "system" comme auditeur par défaut
            return Optional.of("system");
        }
    }
}
