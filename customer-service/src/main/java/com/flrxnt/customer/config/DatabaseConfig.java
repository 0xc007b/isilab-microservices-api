package com.flrxnt.customer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Optional;

@Configuration
@EnableJpaRepositories(basePackages = "com.flrxnt.customer.repository")
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@EnableTransactionManagement
public class DatabaseConfig {

    /**
     * Configuration pour l'audit automatique des entités JPA
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            // Pour l'instant, on retourne "system" comme auditeur
            // Dans une vraie application, on récupérerait l'utilisateur connecté
            return Optional.of("system");
        };
    }

    /**
     * Configuration spécifique pour l'environnement de développement
     */
    @Configuration
    @Profile("dev")
    static class DevDatabaseConfig {
        // Configuration spécifique pour le développement si nécessaire
    }

    /**
     * Configuration spécifique pour l'environnement de test
     */
    @Configuration
    @Profile("test")
    static class TestDatabaseConfig {
        // Configuration spécifique pour les tests si nécessaire
    }

    /**
     * Configuration spécifique pour l'environnement de production
     */
    @Configuration
    @Profile("prod")
    static class ProdDatabaseConfig {
        // Configuration spécifique pour la production si nécessaire
    }

    /**
     * Propriétés de configuration de la base de données
     */
    @ConfigurationProperties(prefix = "spring.datasource")
    public static class DatabaseProperties {
        private String url;
        private String username;
        private String password;
        private String driverClassName;

        // Getters et Setters
        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getDriverClassName() {
            return driverClassName;
        }

        public void setDriverClassName(String driverClassName) {
            this.driverClassName = driverClassName;
        }
    }
}
