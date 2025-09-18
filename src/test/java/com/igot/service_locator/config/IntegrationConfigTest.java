package com.igot.service_locator.config;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class IntegrationConfigTest {

    @Test
    void testDefaultValuesAreNull() {
        IntegrationConfig config = new IntegrationConfig();
        Assertions.assertNull(config.getIntegrationFwHost());
        Assertions.assertNull(config.getIntegrationFwPath());
        Assertions.assertNotNull(config.toString());
        assertDoesNotThrow(config::hashCode);
    }

    @Test
    void testSettersAndGetters() {
        IntegrationConfig config = new IntegrationConfig();
        config.setIntegrationFwHost("http://localhost:8080");
        config.setIntegrationFwPath("/api/integration");
        assertEquals("http://localhost:8080", config.getIntegrationFwHost());
        assertEquals("/api/integration", config.getIntegrationFwPath());
    }

    @Test
    void testEqualsWithSameReference() {
        IntegrationConfig config = new IntegrationConfig();
        assertEquals(config, config);
    }

    @Test
    void testEqualsWithNullAndDifferentClass() {
        IntegrationConfig config = new IntegrationConfig();
        Assertions.assertNotEquals(null, config);
        Assertions.assertNotEquals("some string", config);
    }

    @Test
    void testEqualsAndHashCodeWithEqualObjects() {
        IntegrationConfig c1 = new IntegrationConfig();
        c1.setIntegrationFwHost("host");
        c1.setIntegrationFwPath("path");

        IntegrationConfig c2 = new IntegrationConfig();
        c2.setIntegrationFwHost("host");
        c2.setIntegrationFwPath("path");

        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    void testEqualsAndHashCodeWithDifferentObjects() {
        IntegrationConfig c1 = new IntegrationConfig();
        c1.setIntegrationFwHost("host1");
        c1.setIntegrationFwPath("path1");

        IntegrationConfig c2 = new IntegrationConfig();
        c2.setIntegrationFwHost("host2");
        c2.setIntegrationFwPath("path2");

        Assertions.assertNotEquals(c1, c2);
        Assertions.assertNotEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    void testToStringWithValues() {
        IntegrationConfig config = new IntegrationConfig();
        config.setIntegrationFwHost("host");
        config.setIntegrationFwPath("path");
        String str = config.toString();
        Assertions.assertTrue(str.contains("host"));
        Assertions.assertTrue(str.contains("path"));
    }

    @Test
    void testEqualsWhenBothFieldsNull() {
        IntegrationConfig c1 = new IntegrationConfig();
        c1.setIntegrationFwHost(null);
        c1.setIntegrationFwPath(null);
        IntegrationConfig c2 = new IntegrationConfig();
        c2.setIntegrationFwHost(null);
        c2.setIntegrationFwPath(null);
        assertEquals(c1, c2);
    }

    @Test
    void testHashCodeWithMixedNulls() {
        IntegrationConfig c1 = new IntegrationConfig();
        c1.setIntegrationFwHost("host");
        c1.setIntegrationFwPath(null);
        IntegrationConfig c2 = new IntegrationConfig();
        c2.setIntegrationFwHost("host");
        c2.setIntegrationFwPath(null);
        assertEquals(c1.hashCode(), c2.hashCode());
    }
    @Test
    void testEqualsWhenOnlyPathDiffers() {
        IntegrationConfig c1 = new IntegrationConfig();
        c1.setIntegrationFwHost("sameHost");
        c1.setIntegrationFwPath("path1");
        IntegrationConfig c2 = new IntegrationConfig();
        c2.setIntegrationFwHost("sameHost");
        c2.setIntegrationFwPath("path2");
        Assertions.assertNotEquals(c1, c2);
    }

    @Test
    void testHashCodeWithHostNullPathSet() {
        IntegrationConfig c1 = new IntegrationConfig();
        c1.setIntegrationFwHost(null);
        c1.setIntegrationFwPath("path");
        IntegrationConfig c2 = new IntegrationConfig();
        c2.setIntegrationFwHost(null);
        c2.setIntegrationFwPath("path");
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    void testToStringWithHostNull() {
        IntegrationConfig config = new IntegrationConfig();
        config.setIntegrationFwHost(null);
        config.setIntegrationFwPath("pathOnly");
        String str = config.toString();
        Assertions.assertTrue(str.contains("pathOnly"));
    }

    @Test
    void testToStringWithPathNull() {
        IntegrationConfig config = new IntegrationConfig();
        config.setIntegrationFwHost("hostOnly");
        config.setIntegrationFwPath(null);
        String str = config.toString();
        Assertions.assertTrue(str.contains("hostOnly"));
    }
}
