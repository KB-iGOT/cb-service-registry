package com.igot.service_locator.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.igot.service_locator.dto.ServiceRequestDto;
import com.igot.service_locator.entity.IntegrationModel;
import com.igot.service_locator.entity.ServiceLocatorEntity;
import com.igot.service_locator.exceptions.CustomException;
import com.igot.service_locator.service.ServiceLocatorService;
import com.igot.service_locator.util.IntegrationFrameworkUtil;
import com.igot.service_locator.util.IntegrationModelValidator;
import com.igot.service_locator.util.Constants;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IntegrationModelServiceImplTest {

    @InjectMocks
    IntegrationModelServiceImpl service;

    @Mock
    ServiceLocatorService serviceLocatorService;

    @Mock
    IntegrationFrameworkUtil integrationFrameworkUtil;

    @Mock
    ObjectMapper mapper;

    @Mock
    IntegrationModelValidator modelValidator;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetRequestPayloadByConfigId_happyPath() throws Exception {
        String id = "test-id";
        ServiceRequestDto dto = new ServiceRequestDto();
        dto.setRequestMap(new HashMap<>());
        dto.setUrlMap(new HashMap<>());
        dto.setHeaderMap(new HashMap<>());

        ServiceLocatorEntity entity = new ServiceLocatorEntity();
        entity.setServiceCode("svc");
        entity.setPartnerCode("partner");
        entity.setStrictCache(true);
        entity.setStrictCacheTimeInMinutes(5);

        ObjectNode jsonNode = mock(ObjectNode.class);
        entity.setRequestPayload(jsonNode);

        when(serviceLocatorService.readServiceConfig(id, true)).thenReturn(entity);
        when(jsonNode.isMissingNode()).thenReturn(false);

        IntegrationModel expectedModel = new IntegrationModel();
        expectedModel.setServiceCode("svc");
        expectedModel.setPartnerCode("partner");
        expectedModel.setStrictCache(true);
        expectedModel.setStrictCacheTimeInMinutes(5);

        // when treeToValue is called inside replaceServiceRequestDtoPlaceholders
        when(mapper.treeToValue(any(JsonNode.class), eq(IntegrationModel.class)))
                .thenReturn(expectedModel);

        when(serviceLocatorService.readServiceConfigByServiceCode(anyString())).thenReturn(entity);
        when(integrationFrameworkUtil.callExternalServiceApi(any(), any())).thenReturn("response");

        Object result = service.getRequestPayloadByConfigId(dto, id);

        assertEquals("response", result);

        verify(serviceLocatorService).readServiceConfig(id, true);
        verify(serviceLocatorService).readServiceConfigByServiceCode("svc");
        verify(integrationFrameworkUtil).callExternalServiceApi(any(), any());
        verify(mapper).treeToValue(any(), eq(IntegrationModel.class));
    }

    @Test
    void testGetRequestPayloadByConfigId_missingNode() {
        String id = "test-id";
        ServiceRequestDto dto = new ServiceRequestDto();

        ServiceLocatorEntity entity = new ServiceLocatorEntity();
        JsonNode jsonNode = mock(JsonNode.class);
        entity.setRequestPayload(jsonNode);

        when(serviceLocatorService.readServiceConfig(id, true)).thenReturn(entity);
        when(jsonNode.isMissingNode()).thenReturn(true);

        CustomException ex = assertThrows(CustomException.class, () -> {
            service.getRequestPayloadByConfigId(dto, id);
        });

        assertEquals(Constants.ERROR, ex.getCode());
        assertEquals(500, ex.getHttpStatusCode().value());
        assertEquals("requestDto not present in Db with given Id ", ex.getMessage());
    }

    @Test
    void testGetRequestPayloadByConfigId_whenReadServiceFails() {
        String id = "test-id";
        ServiceRequestDto dto = new ServiceRequestDto();

        when(serviceLocatorService.readServiceConfig(id, true)).thenThrow(new RuntimeException("DB error"));

        CustomException ex = assertThrows(CustomException.class, () -> {
            service.getRequestPayloadByConfigId(dto, id);
        });

        assertEquals(Constants.ERROR, ex.getCode());
        assertEquals("DB error", ex.getMessage());
        assertEquals(500, ex.getHttpStatusCode().value());
    }

    @Test
    void testGetProgressRequestPayloadByConfigId_missingNode() {
        String id = "test-id";
        ServiceRequestDto dto = new ServiceRequestDto();

        ServiceLocatorEntity entity = new ServiceLocatorEntity();
        JsonNode jsonNode = mock(JsonNode.class);
        entity.setRequestPayload(jsonNode);

        when(serviceLocatorService.readServiceConfig(id, true)).thenReturn(entity);
        when(jsonNode.isMissingNode()).thenReturn(true);

        CustomException ex = assertThrows(CustomException.class, () -> {
            service.getProgressRequestPayloadByConfigId(dto, id);
        });

        assertEquals(Constants.ERROR, ex.getCode());
        assertEquals(400, ex.getHttpStatusCode().value());
        assertEquals("requestDto not present in Db with given Id ", ex.getMessage());

        verify(serviceLocatorService).readServiceConfig(id, true);
    }

    @Test
    void testGetProgressRequestPayloadByConfigId_exceptionInReadService() {
        String id = "test-id";
        ServiceRequestDto dto = new ServiceRequestDto();

        when(serviceLocatorService.readServiceConfig(id, true))
                .thenThrow(new RuntimeException("db error"));

        CustomException ex = assertThrows(CustomException.class, () -> {
            service.getProgressRequestPayloadByConfigId(dto, id);
        });

        assertEquals(Constants.ERROR, ex.getCode());
        assertEquals("db error", ex.getMessage());
        assertEquals(400, ex.getHttpStatusCode().value());
    }

    @Test
    void testGetDetailsFromExternalService_happyPath() throws Exception {
        IntegrationModel model = new IntegrationModel();
        model.setServiceCode("svc");
        model.setUrlMap(new HashMap<>());
        ServiceLocatorEntity entity = new ServiceLocatorEntity();
        entity.setServiceCode("svc");
        entity.setUrl("http://example.com/{id}");
        entity.setUrlPlaceholder("{id}");
        when(serviceLocatorService.readServiceConfigByServiceCode("svc")).thenReturn(entity);
        when(integrationFrameworkUtil.callExternalServiceApi(any(), any())).thenReturn("ok");
        Object result = service.getDetailsFromExternalService(model);
        assertEquals("ok", result);
        verify(modelValidator).validateModel(model);
        verify(serviceLocatorService).readServiceConfigByServiceCode("svc");
        verify(integrationFrameworkUtil).callExternalServiceApi(any(), any());
    }

    @Test
    void testGetDetailsFromExternalService_validatorThrows()  {
        IntegrationModel model = new IntegrationModel();
        doThrow(new RuntimeException("fail")).when(modelValidator).validateModel(any());
        CustomException ex = assertThrows(CustomException.class, () -> {
            service.getDetailsFromExternalService(model);
        });
        assertEquals(Constants.ERROR, ex.getCode());
        assertEquals("fail", ex.getMessage());
        assertEquals(500, ex.getHttpStatusCode().value());
    }

    @Test
    void testReplaceServiceRequestDtoPlaceholders_replacesMaps() throws Exception {
        ObjectNode node = new ObjectMapper().createObjectNode();
        ObjectNode requestBody = node.putObject("requestBody");
        requestBody.put("field", "{key1}");
        ObjectNode urlMap = node.putObject("urlMap");
        urlMap.put("url", "{key2}");
        ObjectNode headerMap = node.putObject("headerMap");
        headerMap.put("hdr", "{key3}");
        ServiceRequestDto dto = new ServiceRequestDto();
        dto.setRequestMap(Map.of("key1", "value1"));
        dto.setUrlMap(Map.of("key2", "value2"));
        dto.setHeaderMap(Map.of("key3", "value3"));
        IntegrationModel model = new IntegrationModel();
        when(mapper.treeToValue(any(JsonNode.class), eq(IntegrationModel.class))).thenReturn(model);
        var method = IntegrationModelServiceImpl.class
                .getDeclaredMethod("replaceServiceRequestDtoPlaceholders", JsonNode.class, ServiceRequestDto.class);
        method.setAccessible(true);
        IntegrationModel result = (IntegrationModel) method.invoke(service, node, dto);
        assertNotNull(result);
        assertEquals("value1", node.path("requestBody").get("field").asText());
        assertEquals("value2", node.path("urlMap").get("url").asText());
        assertEquals("value3", node.path("headerMap").get("hdr").asText());
    }

    @Test
    void testReplaceServiceRequestDtoPlaceholders_treeToValueThrows() throws Exception {
        ObjectNode node = new ObjectMapper().createObjectNode();
        ServiceRequestDto dto = new ServiceRequestDto();
        when(mapper.treeToValue(any(JsonNode.class), eq(IntegrationModel.class)))
                .thenThrow(new RuntimeException("json fail"));
        var method = IntegrationModelServiceImpl.class
                .getDeclaredMethod("replaceServiceRequestDtoPlaceholders", JsonNode.class, ServiceRequestDto.class);
        method.setAccessible(true);
        Throwable thrown = assertThrows(InvocationTargetException.class,
                () -> method.invoke(service, node, dto));
        CustomException ex = (CustomException) thrown.getCause();
        assertEquals(Constants.ERROR, ex.getCode());
        assertEquals("json fail", ex.getMessage());
    }

    @Test
    void testReplaceUrlPlaceholders_withValue() throws Exception {
        ServiceLocatorEntity entity = new ServiceLocatorEntity();
        entity.setUrl("http://example.com/{id}");
        entity.setUrlPlaceholder("{id}");
        Map<String,String> map = Map.of("id", "123");

        var method = IntegrationModelServiceImpl.class
                .getDeclaredMethod("replaceUrlPlaceholders", ServiceLocatorEntity.class, Map.class);
        method.setAccessible(true);

        String result = (String) method.invoke(service, entity, map);
        assertEquals("http://example.com/123", result);
    }

    @Test
    void testReplaceUrlPlaceholders_missingKey() throws Exception {
        ServiceLocatorEntity entity = new ServiceLocatorEntity();
        entity.setUrl("http://example.com/{id}");
        entity.setUrlPlaceholder("{id}");
        Map<String,String> map = new HashMap<>();
        var method = IntegrationModelServiceImpl.class
                .getDeclaredMethod("replaceUrlPlaceholders", ServiceLocatorEntity.class, Map.class);
        method.setAccessible(true);
        String result = (String) method.invoke(service, entity, map);
        assertEquals("http://example.com/", result);
    }

    @Test
    void testReplaceUrlPlaceholders_blankValue() throws Exception {
        ServiceLocatorEntity entity = new ServiceLocatorEntity();
        entity.setUrl("http://example.com/{id}");
        entity.setUrlPlaceholder("{id}");
        Map<String,String> map = Map.of("id", "");
        var method = IntegrationModelServiceImpl.class
                .getDeclaredMethod("replaceUrlPlaceholders", ServiceLocatorEntity.class, Map.class);
        method.setAccessible(true);
        String result = (String) method.invoke(service, entity, map);
        assertEquals("http://example.com/", result);
    }

    @Test
    void testReplaceUrlPlaceholders_nullPlaceholder() throws Exception {
        ServiceLocatorEntity entity = new ServiceLocatorEntity();
        entity.setUrl("http://example.com/{id}");
        entity.setUrlPlaceholder(null);
        Map<String, String> map = Map.of("id", "123");
        var method = IntegrationModelServiceImpl.class
                .getDeclaredMethod("replaceUrlPlaceholders", ServiceLocatorEntity.class, Map.class);
        method.setAccessible(true);
        InvocationTargetException ex = assertThrows(InvocationTargetException.class,
                () -> method.invoke(service, entity, map));
        assertInstanceOf(NullPointerException.class, ex.getCause());
        assertEquals("Cannot invoke \"String.split(String)\" because \"urlPlaceholder\" is null",
                ex.getCause().getMessage());
    }


}
