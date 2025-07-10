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

import java.util.HashMap;

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
}
