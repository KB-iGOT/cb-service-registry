package com.igot.service_locator.util;

import com.bazaarvoice.jolt.Chainr;
import com.bazaarvoice.jolt.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.igot.service_locator.config.IntegrationConfig;
import com.igot.service_locator.exceptions.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class DataTransformUtilityTest {

    @InjectMocks
    private DataTransformUtility utility;

    @Mock
    private IntegrationConfig config;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private CbServerProperties cbServerProperties;

    @Mock
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCallContentPartnerReadAPIByPartnerCode_success() {
        when(cbServerProperties.getContentPartnerBaseUrl()).thenReturn("http://host/");
        when(cbServerProperties.getContentPartnerReadApiUrl()).thenReturn("api/");
        JsonNode body = mock(JsonNode.class);
        JsonNode resultNode = mock(JsonNode.class);
        when(body.path("result")).thenReturn(resultNode);

        ResponseEntity<JsonNode> responseEntity =
                new ResponseEntity<>(body, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(JsonNode.class)
        )).thenReturn(responseEntity);

        JsonNode result = utility.callContentPartnerReadAPIByPartnerCode("partnerCode");

        assertSame(resultNode, result);
    }

    @Test
    void testCallContentPartnerReadAPIByPartnerCode_failureStatus() {
        when(cbServerProperties.getContentPartnerBaseUrl()).thenReturn("http://host/");
        when(cbServerProperties.getContentPartnerReadApiUrl()).thenReturn("api/");

        ResponseEntity<JsonNode> responseEntity =
                new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(JsonNode.class)
        )).thenReturn(responseEntity);

        CustomException ex = assertThrows(CustomException.class,
                () -> utility.callContentPartnerReadAPIByPartnerCode("partnerCode"));

        assertTrue(ex.getMessage().contains("Failed to retrieve data"));
    }

    @Test
    void testCallContentPartnerReadAPIByPartnerCode_exception() {
        when(cbServerProperties.getContentPartnerBaseUrl()).thenReturn("http://host/");
        when(cbServerProperties.getContentPartnerReadApiUrl()).thenReturn("api/");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(JsonNode.class)
        )).thenThrow(new RuntimeException("Some error"));

        CustomException ex = assertThrows(CustomException.class,
                () -> utility.callContentPartnerReadAPIByPartnerCode("partnerCode"));

        assertTrue(ex.getMessage().contains("Failed to retrieve data"));
    }

    @Test
    void testGetIntegrationFrameWorkUrl() {
        when(config.getIntegrationFwHost()).thenReturn("http://host");
        when(config.getIntegrationFwPath()).thenReturn("/path");

        String url = utility.getIntegrationFrameWorkUrl().toString();

        assertEquals("http://host/path", url);
    }

    @Test
    void testTransformData_success() throws Exception {
        Object source = Map.of("key", "value");
        List<Object> responseFormat = List.of("spec");

        String json = "{\"key\":\"value\"}";
        Object transformed = Map.of("transformed", true);

        // mocks
        when(mapper.writeValueAsString(source)).thenReturn(json);

        // mock Chainr
        Chainr chainrMock = mock(Chainr.class);
        when(chainrMock.transform(any())).thenReturn(transformed);

        // static mocking of Chainr.fromSpec + JsonUtils.jsonToObject
        try (MockedStatic<Chainr> chainrStatic = mockStatic(Chainr.class);
             MockedStatic<JsonUtils> jsonUtilsStatic = mockStatic(JsonUtils.class)) {

            chainrStatic.when(() -> Chainr.fromSpec(responseFormat)).thenReturn(chainrMock);
            jsonUtilsStatic.when(() -> JsonUtils.jsonToObject(json)).thenReturn(Map.of("parsed", true));

            Object result = utility.transformData(source, responseFormat);

            assertEquals(transformed, result);

            // verify path
            verify(mapper).writeValueAsString(source);
            chainrStatic.verify(() -> Chainr.fromSpec(responseFormat));
            jsonUtilsStatic.verify(() -> JsonUtils.jsonToObject(json));
            verify(chainrMock).transform(any());
        }
    }

    @Test
    void testTransformData_exception() throws Exception {
        Object source = Map.of("key", "value");
        List<Object> responseFormat = List.of("spec");

        when(mapper.writeValueAsString(source)).thenThrow(new JsonProcessingException("fail") {});

        ObjectNode responseNode = mock(ObjectNode.class);
        ObjectNode errorNode = mock(ObjectNode.class);
        ObjectNode extResponseNode = mock(ObjectNode.class);

        when(mapper.createObjectNode()).thenReturn(responseNode, errorNode);  // called twice
        when(mapper.valueToTree(source)).thenReturn(extResponseNode);

        Object result = utility.transformData(source, responseFormat);

        assertTrue(result instanceof ObjectNode);

        verify(mapper).writeValueAsString(source);
        verify(mapper, times(2)).createObjectNode();
        verify(mapper).valueToTree(source);
        verify(errorNode).put(eq("message"), anyString());
    }
}

