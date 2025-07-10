package com.igot.service_locator.plugins.coursera;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.igot.service_locator.plugins.AuthPluginService;
import com.igot.service_locator.repository.CallExternalService;
import com.igot.service_locator.util.CbServerProperties;
import com.igot.service_locator.util.DataTransformUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthPluginServiceTest {

    @Mock ObjectMapper objectMapper;
    @Mock CbServerProperties cbServerProperties;
    @Mock CallExternalService callExternalService;
    @Mock DataTransformUtility dataTransformUtility;

    @InjectMocks
    AuthPluginService authPluginService;

    @Mock JsonNode jsonNode;

    @BeforeEach
    void setup() {
        cbServerProperties.courseraAuthApiCacheTtl = 10L;
    }

    @Test
    void testGenerateAuthHeader_clientAuthUrlAndCredentials() {
        // Arrange
        when(jsonNode.has("clientAuthUrl")).thenReturn(true);
        when(jsonNode.has("clientCredentials")).thenReturn(true);

        JsonNode clientCredentialsNode = mock(JsonNode.class);
        when(jsonNode.get("clientCredentials")).thenReturn(clientCredentialsNode);
        when(clientCredentialsNode.asText()).thenReturn("encodedCreds");

        JsonNode clientAuthUrlNode = mock(JsonNode.class);
        when(jsonNode.get("clientAuthUrl")).thenReturn(clientAuthUrlNode);
        when(clientAuthUrlNode.asText()).thenReturn("https://auth.url");

        lenient().when(dataTransformUtility.getIntegrationFrameWorkUrl())
                .thenReturn(new StringBuilder("integrationUrl"));

        ObjectNode requestBody = mock(ObjectNode.class);
        when(objectMapper.createObjectNode()).thenReturn(requestBody);

        Object dummyResponse = new Object();
        when(callExternalService.fetchResult(any(), any()))
                .thenReturn(dummyResponse);

        JsonNode jsonResponse = mock(JsonNode.class);
        JsonNode responseData = mock(JsonNode.class);
        JsonNode accessToken = mock(JsonNode.class);

        when(objectMapper.convertValue(eq(dummyResponse), any(TypeReference.class)))
                .thenReturn(jsonResponse);

        when(jsonResponse.path("responseData")).thenReturn(responseData);
        when(responseData.get("access_token")).thenReturn(accessToken);
        when(accessToken.asText()).thenReturn("access-token");

        // Act
        String result = authPluginService.generateAuthHeader(jsonNode);

        // Assert
        assertEquals("Bearer access-token", result);
    }

    @Test
    void testGenerateAuthHeader_clientSegmentCodeSecret() {
        when(jsonNode.has("clientAuthUrl")).thenReturn(false);
        when(jsonNode.has("clientSegment")).thenReturn(true);
        when(jsonNode.has("clientCode")).thenReturn(true);
        when(jsonNode.has("clientSecret")).thenReturn(true);

        JsonNode clientSegment = mock(JsonNode.class);
        when(jsonNode.get("clientSegment")).thenReturn(clientSegment);
        when(clientSegment.asText()).thenReturn("segment");

        JsonNode clientCode = mock(JsonNode.class);
        when(jsonNode.get("clientCode")).thenReturn(clientCode);
        when(clientCode.asText()).thenReturn("code");

        JsonNode clientSecret = mock(JsonNode.class);
        when(jsonNode.get("clientSecret")).thenReturn(clientSecret);
        when(clientSecret.asText()).thenReturn("secret");

        String result = authPluginService.generateAuthHeader(jsonNode);

        assertTrue(result.startsWith("code."));
        assertEquals(3, result.split("\\.").length);
    }


    @Test
    void testGenerateAuthHeader_noMatchingKeys() {
        when(jsonNode.has("clientAuthUrl")).thenReturn(false);
        when(jsonNode.has("clientSegment")).thenReturn(false);

        String result = authPluginService.generateAuthHeader(jsonNode);

        assertEquals("", result);
    }

    @Test
    void testGenerateAuthHeader_MD5AlgorithmException() {

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            authPluginService = new AuthPluginService(objectMapper, cbServerProperties, callExternalService, dataTransformUtility) {
                @Override
                public String generateAuthHeader(JsonNode node) {
                    try {
                        // This is just to simulate failure on MD5
                        MessageDigest.getInstance("InvalidAlgo");
                        return super.generateAuthHeader(node);
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
            authPluginService.generateAuthHeader(jsonNode);
        });
    }

}
