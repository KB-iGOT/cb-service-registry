package com.igot.service_locator.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.igot.service_locator.entity.IntegrationModel;
import com.igot.service_locator.exceptions.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IntegrationModelValidatorTest {

    @InjectMocks
    private IntegrationModelValidator validator;

    @Mock
    private ObjectMapper mapper;

    private IntegrationModel integrationModel;

    @BeforeEach
    void setUp() {
        integrationModel = new IntegrationModel();
        integrationModel.setServiceCode("testCode");

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("key", "value");

        integrationModel.setRequestBody(requestBody);
    }

    @Test
    void testValidateModel_withValidRequestBody() throws Exception {
        when(mapper.writeValueAsString(any())).thenReturn("{\"key\":\"value\"}");

        assertDoesNotThrow(() -> validator.validateModel(integrationModel));

        assertEquals(Boolean.FALSE, integrationModel.getStrictCache());
    }

    @Test
    void testValidateModel_withNullStrictCache() throws Exception {
        integrationModel.setStrictCache(null);
        when(mapper.writeValueAsString(any())).thenReturn("{\"key\":\"value\"}");

        validator.validateModel(integrationModel);

        assertEquals(Boolean.FALSE, integrationModel.getStrictCache());
    }

    @Test
    void testValidateModel_withInvalidRequestBody() throws Exception {
        doThrow(new JsonProcessingException("invalid") {}).when(mapper).writeValueAsString(any());

        CustomException ex = assertThrows(CustomException.class,
                () -> validator.validateModel(integrationModel));

        assertEquals("REQUEST_BODY", ex.getCode());
    }

    @Test
    void testValidateModel_withBlankServiceCode() {
        integrationModel.setServiceCode("");
        integrationModel.setRequestBody(null);

        CustomException ex = assertThrows(CustomException.class,
                () -> validator.validateModel(integrationModel));

        assertEquals("SERVICE_CODE", ex.getCode());
    }
}
