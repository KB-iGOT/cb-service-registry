package com.igot.service_locator.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServiceLocatorEntityTest {

    @Test
    void testGettersSettersAndBuilder() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode requestPayload = mapper.createObjectNode().put("key", "value");
        ObjectNode authPayload = mapper.createObjectNode().put("auth", "token");

        ServiceLocatorEntity entity = ServiceLocatorEntity.builder()
                .id("1")
                .requestMethod(ServiceLocatorEntity.RequestMethod.POST)
                .url("http://example.com")
                .serviceCode("code")
                .serviceName("service")
                .serviceDescription("description")
                .operationType("CREATE")
                .urlPlaceholder("{id}")
                .isActive(false)
                .isSecureHeader(false)
                .isFormData(true)
                .requestPayload(requestPayload)
                .partnerCode("partner")
                .strictCache(true)
                .strictCacheTimeInMinutes(60L)
                .authPayload(authPayload)
                .build();

        assertEquals("1", entity.getId());
        assertEquals(ServiceLocatorEntity.RequestMethod.POST, entity.getRequestMethod());
        assertEquals("http://example.com", entity.getUrl());
        assertEquals("code", entity.getServiceCode());
        assertEquals("service", entity.getServiceName());
        assertEquals("description", entity.getServiceDescription());
        assertEquals("CREATE", entity.getOperationType());
        assertEquals("{id}", entity.getUrlPlaceholder());
        assertFalse(entity.isActive());
        assertFalse(entity.isSecureHeader());
        assertTrue(entity.isFormData());
        assertEquals(requestPayload, entity.getRequestPayload());
        assertEquals("partner", entity.getPartnerCode());
        assertTrue(entity.isStrictCache());
        assertEquals(60L, entity.getStrictCacheTimeInMinutes());
        assertEquals(authPayload, entity.getAuthPayload());
    }

    @Test
    void testNoArgsAndAllArgsConstructors() {
        ServiceLocatorEntity entity = new ServiceLocatorEntity();
        entity.setId("2");
        entity.setRequestMethod(ServiceLocatorEntity.RequestMethod.GET);
        assertEquals("2", entity.getId());
        assertEquals(ServiceLocatorEntity.RequestMethod.GET, entity.getRequestMethod());

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode requestPayload = mapper.createObjectNode();
        ObjectNode authPayload = mapper.createObjectNode();

        ServiceLocatorEntity entity2 = new ServiceLocatorEntity(
                "3",
                ServiceLocatorEntity.RequestMethod.PUT,
                "url",
                "code",
                "name",
                "desc",
                "opType",
                "placeholder",
                true,
                true,
                false,
                requestPayload,
                "partner",
                false,
                30L,
                authPayload
        );

        assertEquals("3", entity2.getId());
        assertEquals(ServiceLocatorEntity.RequestMethod.PUT, entity2.getRequestMethod());
        assertEquals("url", entity2.getUrl());
    }

    @Test
    void testRequestMethodEnum() {
        for (ServiceLocatorEntity.RequestMethod method : ServiceLocatorEntity.RequestMethod.values()) {
            assertEquals(method, ServiceLocatorEntity.RequestMethod.fromValue(method.toString()));
        }

        assertNull(ServiceLocatorEntity.RequestMethod.fromValue("UNKNOWN"));

        // Check toString
        assertEquals("GET", ServiceLocatorEntity.RequestMethod.GET.toString());
    }
}
