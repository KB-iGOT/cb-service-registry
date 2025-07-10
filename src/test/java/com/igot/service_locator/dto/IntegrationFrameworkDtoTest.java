package com.igot.service_locator.dto;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class IntegrationFrameworkDtoTest {

    @Test
    void testGettersAndSetters() {
        IntegrationFrameworkDto dto = new IntegrationFrameworkDto();

        dto.setUrl("http://example.com");
        dto.setRequestMethod("POST");
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer token");
        dto.setRequestHeader(headers);
        dto.setRequestBody("body");
        dto.setServiceCode("service-code");
        dto.setServiceName("service-name");
        dto.setServiceDescription("description");
        dto.setResponseData("response");
        dto.setOperationType("CREATE");
        dto.setStrictCache(true);
        dto.setStrictCacheTimeInMinutes(60L);
        dto.setAlwaysDataReadFromCache(false);
        dto.setFormData(true);

        assertEquals("http://example.com", dto.getUrl());
        assertEquals("POST", dto.getRequestMethod());
        assertEquals(headers, dto.getRequestHeader());
        assertEquals("body", dto.getRequestBody());
        assertEquals("service-code", dto.getServiceCode());
        assertEquals("service-name", dto.getServiceName());
        assertEquals("description", dto.getServiceDescription());
        assertEquals("response", dto.getResponseData());
        assertEquals("CREATE", dto.getOperationType());
        assertTrue(dto.isStrictCache());
        assertEquals(60L, dto.getStrictCacheTimeInMinutes());
        assertFalse(dto.isAlwaysDataReadFromCache());
        assertTrue(dto.isFormData());
    }
}
