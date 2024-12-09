package com.igot.service_locator.plugins.coursera;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.igot.service_locator.dto.IntegrationFrameworkDto;
import com.igot.service_locator.plugins.ContentPartnerPluginService;
import com.igot.service_locator.repository.CallExternalService;
import com.igot.service_locator.util.CbServerProperties;
import com.igot.service_locator.util.DataTransformUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class CourseraPluginServiceImpl implements ContentPartnerPluginService {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CbServerProperties cbServerProperties;

    @Autowired
    private CallExternalService callExternalService;

    @Autowired
    private DataTransformUtility dataTransformUtility;

    @Override
    public String generateAuthHeader() {
        log.info("CourseraPluginServiceImpl::generateAuthHeader");
        IntegrationFrameworkDto dto = new IntegrationFrameworkDto();
        Map<String, String> requestHeader = new HashMap<>();
        requestHeader.put("Authorization", "Basic " + cbServerProperties.getCourseraAuthorizationHeader());
        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("grant_type", "client_credentials");
        dto.setUrl(cbServerProperties.getCourseraAuthApiUrl());
        dto.setFormData(true);
        dto.setRequestMethod("POST");
        dto.setOperationType("PEER_TO_PEER");
        dto.setRequestHeader(requestHeader);
        dto.setRequestBody(requestBody);
        dto.setServiceCode("coursera-auth-api");
        dto.setServiceName("coursera-auth-api");
        dto.setServiceDescription("coursera-auth-api");
        dto.setStrictCache(true);
        dto.setStrictCacheTimeInMinutes(20);
        Object response = callExternalService.fetchResult(dataTransformUtility.getIntegrationFrameWorkUrl(),dto);
        JsonNode jsonResponse=objectMapper.convertValue(response, new TypeReference<JsonNode>() {
        });
        return "Bearer " + jsonResponse.path("responseData").get("access_token").asText();
    }
}
