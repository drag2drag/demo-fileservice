package com.zurich.api;

import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Extracted from KeycloakSecurityConfig to avoid circular dependency
 * see https://stackoverflow.com/questions/70207564/spring-boot-2-6-regression-how-can-i-fix-keycloak-circular-dependency-in-adapte
 */
@Configuration
public class KeycloakSecurityConfig2 {
    @Bean
    public KeycloakConfigResolver getKeycloakConfigResolver() {
        return new KeycloakSpringBootConfigResolver();
    }
}
