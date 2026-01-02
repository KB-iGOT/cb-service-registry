package com.igot.service_locator.controller;

import com.igot.service_locator.dto.PaginatedRequestDto;
import com.igot.service_locator.dto.PaginatedResponse;
import com.igot.service_locator.dto.ServiceLocatorDto;
import com.igot.service_locator.entity.ServiceLocatorEntity;
import com.igot.service_locator.service.ServiceLocatorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceLocatorControllerTest {

    @InjectMocks
    ServiceLocatorController controller;

    @Mock
    ServiceLocatorService serviceLocatorService;

    @Test
    void testCreateServiceConfig() {
        ServiceLocatorEntity entity = new ServiceLocatorEntity();
        when(serviceLocatorService.createOrUpdateServiceConfig(entity)).thenReturn(entity);

        ServiceLocatorEntity result = controller.createServiceConfig(entity);

        assertEquals(entity, result);
        verify(serviceLocatorService).createOrUpdateServiceConfig(entity);
    }

    @Test
    void testGetAllServiceConfig() {
        PaginatedRequestDto dto = new PaginatedRequestDto();
        PaginatedResponse expected = new PaginatedResponse();
        when(serviceLocatorService.getAllServiceConfig(dto)).thenReturn(expected);

        ResponseEntity<PaginatedResponse> response = controller.getAllServiceConfig(dto);

        assertEquals(ResponseEntity.ok(expected), response);
        verify(serviceLocatorService).getAllServiceConfig(dto);
    }

    @Test
    void testUpdateServiceConfig() {
        ServiceLocatorEntity entity = new ServiceLocatorEntity();
        when(serviceLocatorService.createOrUpdateServiceConfig(entity)).thenReturn(entity);

        ServiceLocatorEntity result = controller.updateServiceConfig(entity);

        assertEquals(entity, result);
        verify(serviceLocatorService).createOrUpdateServiceConfig(entity);
    }

    @Test
    void testDeleteServiceConfig() {
        String id = "123";
        when(serviceLocatorService.deleteServiceConfig(id)).thenReturn("Data deleted successfully with id ");

        String result = controller.deleteServiceConfig(id);

        assertEquals("Data deleted successfully with id " + id, result);
        verify(serviceLocatorService).deleteServiceConfig(id);
    }

    @Test
    void testSearchServiceConfig() {
        ServiceLocatorDto dto = new ServiceLocatorDto();
        List<ServiceLocatorEntity> expected = Collections.singletonList(new ServiceLocatorEntity());
        when(serviceLocatorService.searchServiceConfig(dto)).thenReturn(expected);

        List<ServiceLocatorEntity> result = controller.searchServiceConfig(dto);

        assertEquals(expected, result);
        verify(serviceLocatorService).searchServiceConfig(dto);
    }

    @Test
    void testReadServiceConfig_withIsActiveTrue() {
        String id = "123";
        boolean isActive = true;
        ServiceLocatorEntity expected = ServiceLocatorEntity.builder().build();
        when(serviceLocatorService.readServiceConfig(id, isActive)).thenReturn(expected);

        ResponseEntity<ServiceLocatorEntity> response = controller.readServiceConfig(id, isActive);

        assertEquals(ResponseEntity.ok(expected), response);
        verify(serviceLocatorService).readServiceConfig(id, isActive);
    }

    @Test
    void testReadServiceConfig_withIsActiveFalse() {
        String id = "123";
        boolean isActive = false;
        ServiceLocatorEntity expected = ServiceLocatorEntity.builder().build();
        when(serviceLocatorService.readServiceConfig(id, isActive)).thenReturn(expected);

        ResponseEntity<ServiceLocatorEntity> response = controller.readServiceConfig(id, isActive);

        assertEquals(ResponseEntity.ok(expected), response);
        verify(serviceLocatorService).readServiceConfig(id, isActive);
    }
}
