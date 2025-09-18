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


    @Test
    void testDefaultValues() {
        IntegrationFrameworkDto dto = new IntegrationFrameworkDto();
        assertNull(dto.getUrl());
        assertNull(dto.getRequestMethod());
        assertNull(dto.getRequestHeader());
        assertNull(dto.getRequestBody());
        assertNull(dto.getServiceCode());
        assertNull(dto.getServiceName());
        assertNull(dto.getServiceDescription());
        assertNull(dto.getResponseData());
        assertNull(dto.getOperationType());
        assertFalse(dto.isStrictCache());
        assertEquals(0L, dto.getStrictCacheTimeInMinutes());
        assertFalse(dto.isAlwaysDataReadFromCache());
        assertFalse(dto.isFormData());
        assertNotNull(dto.toString());
        assertDoesNotThrow(dto::hashCode);
    }

    @Test
    void testSettersAndGetters() {
        IntegrationFrameworkDto dto = new IntegrationFrameworkDto();
        Map<String, String> headers = new HashMap<>();
        headers.put("k", "v");
        dto.setUrl("url");
        dto.setRequestMethod("GET");
        dto.setRequestHeader(headers);
        dto.setRequestBody("body");
        dto.setServiceCode("code");
        dto.setServiceName("name");
        dto.setServiceDescription("desc");
        dto.setResponseData("resp");
        dto.setOperationType("op");
        dto.setStrictCache(true);
        dto.setStrictCacheTimeInMinutes(15L);
        dto.setAlwaysDataReadFromCache(true);
        dto.setFormData(true);
        assertEquals("url", dto.getUrl());
        assertEquals("GET", dto.getRequestMethod());
        assertEquals(headers, dto.getRequestHeader());
        assertEquals("body", dto.getRequestBody());
        assertEquals("code", dto.getServiceCode());
        assertEquals("name", dto.getServiceName());
        assertEquals("desc", dto.getServiceDescription());
        assertEquals("resp", dto.getResponseData());
        assertEquals("op", dto.getOperationType());
        assertTrue(dto.isStrictCache());
        assertEquals(15L, dto.getStrictCacheTimeInMinutes());
        assertTrue(dto.isAlwaysDataReadFromCache());
        assertTrue(dto.isFormData());
    }

    @Test
    void testEqualsAndHashCode() {
        IntegrationFrameworkDto d1 = new IntegrationFrameworkDto();
        d1.setUrl("url");
        d1.setServiceCode("code");
        IntegrationFrameworkDto d2 = new IntegrationFrameworkDto();
        d2.setUrl("url");
        d2.setServiceCode("code");
        IntegrationFrameworkDto d3 = new IntegrationFrameworkDto();
        d3.setUrl("different");
        d3.setServiceCode("different");
        assertEquals(d1, d2);
        assertNotEquals(d1, d3);
        assertNotEquals(null, d1);
        assertNotEquals(new Object(), d1);
        assertEquals(d1, d1); // same reference
        assertEquals(d1.hashCode(), d2.hashCode());
        assertNotEquals(d1.hashCode(), d3.hashCode());
    }

    @Test
    void testEqualsWhenOneFieldNull() {
        IntegrationFrameworkDto d1 = new IntegrationFrameworkDto();
        d1.setUrl(null);
        d1.setServiceCode("code");
        IntegrationFrameworkDto d2 = new IntegrationFrameworkDto();
        d2.setUrl("url");
        d2.setServiceCode("code");
        assertNotEquals(d1, d2);
    }

    @Test
    void testToStringWithValues() {
        IntegrationFrameworkDto dto = new IntegrationFrameworkDto();
        dto.setUrl("url");
        dto.setRequestMethod("POST");
        String str = dto.toString();
        assertTrue(str.contains("url"));
        assertTrue(str.contains("POST"));
    }


    @Test
    void testEqualsDifferentRequestMethod() {
        IntegrationFrameworkDto d1 = new IntegrationFrameworkDto();
        d1.setUrl("url");
        d1.setRequestMethod("GET");
        IntegrationFrameworkDto d2 = new IntegrationFrameworkDto();
        d2.setUrl("url");
        d2.setRequestMethod("POST");
        assertNotEquals(d1, d2);
    }

    @Test
    void testHashCodeDifferentFields() {
        IntegrationFrameworkDto d1 = new IntegrationFrameworkDto();
        d1.setUrl("url");
        d1.setRequestMethod(null);
        IntegrationFrameworkDto d2 = new IntegrationFrameworkDto();
        d2.setUrl("url");
        d2.setRequestMethod(null);
        assertEquals(d1.hashCode(), d2.hashCode());
        d2.setRequestMethod("PATCH");
        assertNotEquals(d1.hashCode(), d2.hashCode());
    }

    @Test
    void testToStringWithMixedNulls() {
        IntegrationFrameworkDto dto = new IntegrationFrameworkDto();
        dto.setUrl("onlyUrl");
        dto.setRequestMethod(null);
        String str = dto.toString();
        assertTrue(str.contains("onlyUrl"));
    }


    @Test
    void testEqualsDifferentBooleans() {
        IntegrationFrameworkDto d1 = new IntegrationFrameworkDto();
        d1.setStrictCache(true);
        d1.setAlwaysDataReadFromCache(false);
        d1.setFormData(true);
        IntegrationFrameworkDto d2 = new IntegrationFrameworkDto();
        d2.setStrictCache(false);
        d2.setAlwaysDataReadFromCache(true);
        d2.setFormData(false);
        assertNotEquals(d1, d2);
    }

    @Test
    void testEqualsDifferentLongs() {
        IntegrationFrameworkDto d1 = new IntegrationFrameworkDto();
        d1.setStrictCacheTimeInMinutes(5L);
        IntegrationFrameworkDto d2 = new IntegrationFrameworkDto();
        d2.setStrictCacheTimeInMinutes(10L);
        assertNotEquals(d1, d2);
    }

    @Test
    void testEqualsWithDifferentRequestHeader() {
        IntegrationFrameworkDto d1 = new IntegrationFrameworkDto();
        d1.setRequestHeader(Map.of("k1", "v1"));
        IntegrationFrameworkDto d2 = new IntegrationFrameworkDto();
        d2.setRequestHeader(Map.of("k2", "v2"));
        assertNotEquals(d1, d2);
    }

    @Test
    void testEqualsWithDifferentResponseData() {
        IntegrationFrameworkDto d1 = new IntegrationFrameworkDto();
        d1.setResponseData("resp1");
        IntegrationFrameworkDto d2 = new IntegrationFrameworkDto();
        d2.setResponseData("resp2");
        assertNotEquals(d1, d2);
    }

    @Test
    void testToStringWithDifferentFields() {
        IntegrationFrameworkDto dto = new IntegrationFrameworkDto();
        dto.setServiceName("MyService");
        dto.setServiceDescription(null);
        String str = dto.toString();
        assertTrue(str.contains("MyService"));
    }


    @Test
    void testEqualsSameReferenceAndDifferentClass() {
        IntegrationFrameworkDto dto = new IntegrationFrameworkDto();
        assertEquals(dto, dto);
        assertNotEquals(null, dto);
        assertNotEquals("string", dto);
    }

    @Test
    void testEqualsAndHashCodeForAllFieldsEqual() {
        IntegrationFrameworkDto d1 = createPopulatedDto();
        IntegrationFrameworkDto d2 = createPopulatedDto();

        assertEquals(d1, d2);
        assertEquals(d1.hashCode(), d2.hashCode());
    }

    @Test
    void testEqualsAndHashCodeForDifferentValues() {
        IntegrationFrameworkDto d1 = createPopulatedDto();
        IntegrationFrameworkDto d2 = createPopulatedDto();

        d2.setUrl("different");
        d2.setRequestMethod("POST");
        d2.setRequestHeader(Map.of("other", "header"));
        d2.setRequestBody("otherBody");
        d2.setServiceCode("otherCode");
        d2.setServiceName("otherName");
        d2.setServiceDescription("otherDesc");
        d2.setResponseData("otherResp");
        d2.setOperationType("otherOp");
        d2.setStrictCache(false);
        d2.setStrictCacheTimeInMinutes(999L);
        d2.setAlwaysDataReadFromCache(false);
        d2.setFormData(false);

        assertNotEquals(d1, d2);
        assertNotEquals(d1.hashCode(), d2.hashCode());
    }

    @Test
    void testEqualsWithNullVsNonNullFields() {
        IntegrationFrameworkDto d1 = new IntegrationFrameworkDto();
        d1.setUrl(null);
        IntegrationFrameworkDto d2 = new IntegrationFrameworkDto();
        d2.setUrl("url");
        assertNotEquals(d1, d2);

        d1 = new IntegrationFrameworkDto();
        d1.setServiceName(null);
        d2 = new IntegrationFrameworkDto();
        d2.setServiceName("name");
        assertNotEquals(d1, d2);

        d1 = new IntegrationFrameworkDto();
        d1.setRequestHeader(null);
        d2 = new IntegrationFrameworkDto();
        d2.setRequestHeader(Map.of("k", "v"));
        assertNotEquals(d1, d2);

        d1 = new IntegrationFrameworkDto();
        d1.setResponseData(null);
        d2 = new IntegrationFrameworkDto();
        d2.setResponseData("resp");
        assertNotEquals(d1, d2);
    }

    private IntegrationFrameworkDto createPopulatedDto() {
        IntegrationFrameworkDto dto = new IntegrationFrameworkDto();
        dto.setUrl("url");
        dto.setRequestMethod("GET");
        dto.setRequestHeader(Map.of("k", "v"));
        dto.setRequestBody("body");
        dto.setServiceCode("code");
        dto.setServiceName("name");
        dto.setServiceDescription("desc");
        dto.setResponseData("resp");
        dto.setOperationType("op");
        dto.setStrictCache(true);
        dto.setStrictCacheTimeInMinutes(15L);
        dto.setAlwaysDataReadFromCache(true);
        dto.setFormData(true);
        return dto;
    }

}
