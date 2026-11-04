package com.snapit.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class CorsConfigurationTest {
    @Test
    void allowsOnlyConfiguredFrontendOriginsAndBearerRequestHeaders() {
        CorsConfigurationSource source = new SecurityConfig()
                .corsConfigurationSource("http://localhost:5173, https://app.snapit.example");
        CorsConfiguration configuration = source.getCorsConfiguration(
                new MockHttpServletRequest("OPTIONS", "/api/saved-items")
        );
        assertNotNull(configuration);

        assertEquals("http://localhost:5173", configuration.checkOrigin("http://localhost:5173"));
        assertEquals("https://app.snapit.example", configuration.checkOrigin("https://app.snapit.example"));
        assertNull(configuration.checkOrigin("https://untrusted.example"));
        assertEquals(java.util.List.of("Authorization", "Content-Type"), configuration.getAllowedHeaders());
        assertEquals(Boolean.FALSE, configuration.getAllowCredentials());
    }

    @Test
    void doesNotConfigureCorsForNonApiPaths() {
        CorsConfigurationSource source = new SecurityConfig()
                .corsConfigurationSource("http://localhost:5173");

        assertNull(source.getCorsConfiguration(new MockHttpServletRequest("GET", "/actuator/health")));
    }
}
