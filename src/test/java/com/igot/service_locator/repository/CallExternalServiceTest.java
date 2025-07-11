package com.igot.service_locator.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.igot.service_locator.exceptions.CustomException;
import com.igot.service_locator.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CallExternalServiceTest {

    @InjectMocks
    private CallExternalService callExternalService;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFetchResult_success() {
        StringBuilder uri = new StringBuilder("http://test");
        Object requestData = Map.of("key", "value");
        Map<String, Object> expectedResponse = Map.of("result", "ok");

        when(restTemplate.postForObject(uri.toString(), requestData, Map.class))
                .thenReturn(expectedResponse);

        Object result = callExternalService.fetchResult(uri, requestData);

        assertEquals(expectedResponse, result);
        verify(restTemplate).postForObject(uri.toString(), requestData, Map.class);
    }

    @Test
    void testFetchResult_HttpClientErrorException() {
        StringBuilder uri = new StringBuilder("http://test");
        Object requestData = Map.of("key", "value");

        HttpClientErrorException ex = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request", "error body".getBytes(), null);

        when(restTemplate.postForObject(uri.toString(), requestData, Map.class))
                .thenThrow(ex);

        CustomException thrown = assertThrows(CustomException.class, () ->
                callExternalService.fetchResult(uri, requestData));

        assertEquals(Constants.EXTERNAL_SERVICE_CALL_EXCEPTION, thrown.getCode());
        assertEquals("error body", thrown.getMessage());
    }

    @Test
    void testFetchResult_GenericException() {
        StringBuilder uri = new StringBuilder("http://test");
        Object requestData = Map.of("key", "value");

        when(restTemplate.postForObject(uri.toString(), requestData, Map.class))
                .thenThrow(new RuntimeException("Something went wrong"));

        CustomException thrown = assertThrows(CustomException.class, () ->
                callExternalService.fetchResult(uri, requestData));

        assertEquals(Constants.EXTERNAL_SERVICE_CALL_EXCEPTION, thrown.getCode());
        assertEquals("Something went wrong", thrown.getMessage());
    }

    @Test
    void testFetchResultForFile_success() {
        StringBuilder uri = new StringBuilder("http://test");
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", "data");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        ResponseEntity<JsonNode> responseEntity = mock(ResponseEntity.class);
        JsonNode jsonNode = mock(JsonNode.class);

        when(responseEntity.getBody()).thenReturn(jsonNode);
        when(restTemplate.exchange(eq(uri.toString()), eq(HttpMethod.POST), any(HttpEntity.class), eq(JsonNode.class)))
                .thenReturn(responseEntity);

        Object result = callExternalService.fetchResultForFile(uri, body);

        assertEquals(jsonNode, result);
    }

    @Test
    void testFetchResultForFile_HttpClientErrorException() {
        StringBuilder uri = new StringBuilder("http://test");
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", "data");

        HttpClientErrorException ex = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request", "error body".getBytes(), null);

        when(restTemplate.exchange(eq(uri.toString()), eq(HttpMethod.POST), any(HttpEntity.class), eq(JsonNode.class)))
                .thenThrow(ex);

        CustomException thrown = assertThrows(CustomException.class, () ->
                callExternalService.fetchResultForFile(uri, body));

        assertEquals(Constants.EXTERNAL_SERVICE_CALL_EXCEPTION, thrown.getCode());
        assertEquals("error body", thrown.getMessage());
    }

    @Test
    void testFetchResultForFile_GenericException() {
        StringBuilder uri = new StringBuilder("http://test");
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", "data");

        when(restTemplate.exchange(eq(uri.toString()), eq(HttpMethod.POST), any(HttpEntity.class), eq(JsonNode.class)))
                .thenThrow(new RuntimeException("Some error"));

        Object result = callExternalService.fetchResultForFile(uri, body);

        assertNull(result);
    }
}
