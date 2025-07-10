package com.igot.service_locator.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class IntegrationConfigTest {

    @Test
    void testSettersAndGetters() {
        IntegrationConfig config = new IntegrationConfig();

        config.setIntegrationFwHost("http://localhost:8080");
        config.setIntegrationFwPath("/api/integration");

        assertEquals("http://localhost:8080", config.getIntegrationFwHost());
        assertEquals("/api/integration", config.getIntegrationFwPath());
    }
}
