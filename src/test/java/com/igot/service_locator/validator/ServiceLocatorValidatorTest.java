package com.igot.service_locator.validator;

import com.fasterxml.jackson.databind.JsonNode;
import com.igot.service_locator.entity.ServiceLocatorEntity;
import com.igot.service_locator.exceptions.CustomException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceLocatorValidatorTest {

    ServiceLocatorValidator validator = new ServiceLocatorValidator();

    @Mock
    ServiceLocatorEntity entity;
    @Mock JsonNode authPayload;

    @Test
    void test_validate_nullEntity() {
        CustomException ex = assertThrows(CustomException.class,
                () -> validator.validate(null));
        assertEquals("SERVICE_LOCATOR_CONFIG", ex.getCode());
    }

    @Test
    void test_validate_blankUrl() {
        when(entity.getUrl()).thenReturn("");

        CustomException ex = assertThrows(CustomException.class,
                () -> validator.validate(entity));
        assertEquals("URL", ex.getCode());
    }

    @Test
    void test_validate_blankServiceCode() {
        when(entity.getUrl()).thenReturn("http://test");
        when(entity.getServiceCode()).thenReturn("");

        CustomException ex = assertThrows(CustomException.class,
                () -> validator.validate(entity));
        assertEquals("SERVICE_CODE", ex.getCode());
    }

    @Test
    void test_validate_blankServiceName() {
        when(entity.getUrl()).thenReturn("http://test");
        when(entity.getServiceCode()).thenReturn("code");
        when(entity.getServiceName()).thenReturn("");

        CustomException ex = assertThrows(CustomException.class,
                () -> validator.validate(entity));
        assertEquals("SERVICE_NAME", ex.getCode());
    }

    @Test
    void test_validate_blankOperationType() {
        when(entity.getUrl()).thenReturn("http://test");
        when(entity.getServiceCode()).thenReturn("code");
        when(entity.getServiceName()).thenReturn("name");
        when(entity.getOperationType()).thenReturn("");

        CustomException ex = assertThrows(CustomException.class,
                () -> validator.validate(entity));
        assertEquals("OPERATION_TYPE", ex.getCode());
    }

    @Test
    void test_validate_nullRequestMethod() {
        when(entity.getUrl()).thenReturn("http://test");
        when(entity.getServiceCode()).thenReturn("code");
        when(entity.getServiceName()).thenReturn("name");
        when(entity.getOperationType()).thenReturn("op");
        when(entity.getRequestMethod()).thenReturn(null);

        CustomException ex = assertThrows(CustomException.class,
                () -> validator.validate(entity));
        assertEquals("REQUEST_METHOD", ex.getCode());
    }

    @Test
    void test_validate_secureHeader_missingAuthPayload() {
        when(entity.getUrl()).thenReturn("http://test");
        when(entity.getServiceCode()).thenReturn("code");
        when(entity.getServiceName()).thenReturn("name");
        when(entity.getOperationType()).thenReturn("op");
        when(entity.getRequestMethod()).thenReturn(ServiceLocatorEntity.RequestMethod.GET);
        when(entity.isSecureHeader()).thenReturn(true);
        when(entity.getAuthPayload()).thenReturn(null);

        CustomException ex = assertThrows(CustomException.class,
                () -> validator.validate(entity));
        assertEquals("AUTH_PAYLOAD", ex.getCode());
    }

    @Test
    void test_validate_secureHeader_withValidAuthPayload() {
        when(entity.getUrl()).thenReturn("http://test");
        when(entity.getServiceCode()).thenReturn("code");
        when(entity.getServiceName()).thenReturn("name");
        when(entity.getOperationType()).thenReturn("op");
        when(entity.getRequestMethod()).thenReturn(ServiceLocatorEntity.RequestMethod.GET);
        when(entity.isSecureHeader()).thenReturn(true);
        when(entity.getAuthPayload()).thenReturn(authPayload);
        when(authPayload.isMissingNode()).thenReturn(false);

        assertThrows(CustomException.class,
                () -> validator.validate(entity)); // because the schema file won't be found
    }

    @Test
    void test_validatePayload_schemaResourceNotFound() {
        JsonNode dummyPayload = mock(JsonNode.class);
        CustomException ex = assertThrows(CustomException.class,
                () -> validator.validatePayload("/invalid_schema.json", dummyPayload));
        assertTrue(ex.getCode().contains("Failed to validate payload"));
    }
}

