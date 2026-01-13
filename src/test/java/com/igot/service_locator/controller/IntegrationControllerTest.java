package com.igot.service_locator.controller;

import com.igot.service_locator.dto.ServiceRequestDto;
import com.igot.service_locator.entity.IntegrationModel;
import com.igot.service_locator.service.IntegrationModelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IntegrationControllerTest {

    private IntegrationController controller;
    private IntegrationModelService service;

    @BeforeEach
    void setUp() {
        service = mock(IntegrationModelService.class);
        controller = new IntegrationController(service);
        controller.service = service;
    }

    @Test
    void testCallExternalApiService() throws IOException {
        IntegrationModel model = new IntegrationModel();
        Object expectedResponse = "response";

        when(service.getDetailsFromExternalService(model)).thenReturn(expectedResponse);

        Object actualResponse = controller.callExternalApiService(model);

        assertEquals(expectedResponse, actualResponse);
        verify(service, times(1)).getDetailsFromExternalService(model);
    }

    @Test
    void testCallExternalApiServiceByConfigId() throws IOException {
        String id = "123";
        ServiceRequestDto dto = new ServiceRequestDto();
        Object expectedResponse = "response";

        when(service.getRequestPayloadByConfigId(dto, id)).thenReturn(expectedResponse);

        Object actualResponse = controller.callExternalApiServiceByConfigId(dto, id);

        assertEquals(expectedResponse, actualResponse);
        verify(service, times(1)).getRequestPayloadByConfigId(dto, id);
    }

    @Test
    void testCallExternalProgressApiByConfigId() {
        String id = "456";
        ServiceRequestDto dto = new ServiceRequestDto();
        Object expectedResponse = "response";

        when(service.getProgressRequestPayloadByConfigId(dto, id)).thenReturn(expectedResponse);

        Object actualResponse = controller.callExternalProgressApiByConfigId(dto, id);

        assertEquals(expectedResponse, actualResponse);
        verify(service, times(1)).getProgressRequestPayloadByConfigId(dto, id);
    }
}
