package com.igot.service_locator.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class IntegrationModelTest {

    @Test
    void testBuilderAndGetters() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode requestBody = mapper.createObjectNode().put("key", "value");

        IntegrationModel model = IntegrationModel.builder()
                .requestBody(requestBody)
                .urlMap(Map.of("key1", "value1"))
                .serviceCode("service-code")
                .strictCache(true)
                .headerMap(Map.of("h1", "v1"))
                .hostAddress("127.0.0.1")
                .strictCacheTimeInMinutes(60L)
                .alwaysDataReadFromCache(true)
                .partnerCode("partner")
                .transformJson(List.of("t1", "t2"))
                .build();

        assertEquals(requestBody, model.getRequestBody());
        assertEquals("service-code", model.getServiceCode());
        assertEquals(Map.of("key1", "value1"), model.getUrlMap());
        assertTrue(model.getStrictCache());
        assertEquals(Map.of("h1", "v1"), model.getHeaderMap());
        assertEquals("127.0.0.1", model.getHostAddress());
        assertEquals(60L, model.getStrictCacheTimeInMinutes());
        assertTrue(model.isAlwaysDataReadFromCache());
        assertEquals("partner", model.getPartnerCode());
        assertEquals(List.of("t1", "t2"), model.getTransformJson());
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        IntegrationModel model = new IntegrationModel();

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode requestBody = mapper.createObjectNode();

        model.setRequestBody(requestBody);
        model.setUrlMap(Map.of("k", "v"));
        model.setServiceCode("sc");
        model.setStrictCache(false);
        model.setHeaderMap(Map.of("h", "v"));
        model.setHostAddress("localhost");
        model.setStrictCacheTimeInMinutes(10);
        model.setAlwaysDataReadFromCache(false);
        model.setPartnerCode("pcode");
        model.setTransformJson(List.of());

        assertEquals(requestBody, model.getRequestBody());
        assertEquals(Map.of("k", "v"), model.getUrlMap());
        assertEquals("sc", model.getServiceCode());
        assertFalse(model.getStrictCache());
        assertEquals(Map.of("h", "v"), model.getHeaderMap());
        assertEquals("localhost", model.getHostAddress());
        assertEquals(10, model.getStrictCacheTimeInMinutes());
        assertFalse(model.isAlwaysDataReadFromCache());
        assertEquals("pcode", model.getPartnerCode());
        assertEquals(List.of(), model.getTransformJson());
    }

    @Test
    void testAllArgsConstructor() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode requestBody = mapper.createObjectNode();

        IntegrationModel model = new IntegrationModel(
                requestBody,
                Map.of("u", "v"),
                "code",
                false,
                Map.of("h", "v"),
                "host",
                5L,
                false,
                "partner",
                List.of()
        );

        assertNotNull(model);
        assertEquals(requestBody, model.getRequestBody());
        assertEquals("code", model.getServiceCode());
        assertEquals(Map.of("u", "v"), model.getUrlMap());
    }

    @Test
    void testToString() {
        IntegrationModel model = IntegrationModel.builder()
                .serviceCode("code")
                .build();

        String str = model.toString();
        assertNotNull(str);
        assertTrue(str.contains("serviceCode=code"));
    }
}
