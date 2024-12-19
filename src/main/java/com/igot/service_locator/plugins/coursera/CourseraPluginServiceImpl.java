package com.igot.service_locator.plugins.coursera;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.igot.service_locator.dto.IntegrationFrameworkDto;
import com.igot.service_locator.plugins.ContentPartnerPluginService;
import com.igot.service_locator.repository.CallExternalService;
import com.igot.service_locator.util.CbServerProperties;
import com.igot.service_locator.util.Constants;
import com.igot.service_locator.util.DataTransformUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class CourseraPluginServiceImpl implements ContentPartnerPluginService {

    private final ObjectMapper objectMapper;
    private final CbServerProperties cbServerProperties;
    private final CallExternalService callExternalService;
    private final DataTransformUtility dataTransformUtility;

    public CourseraPluginServiceImpl(ObjectMapper objectMapper,
                     CbServerProperties cbServerProperties,
                     CallExternalService callExternalService,
                     DataTransformUtility dataTransformUtility) {
        this.objectMapper = objectMapper;
        this.cbServerProperties = cbServerProperties;
        this.callExternalService = callExternalService;
        this.dataTransformUtility = dataTransformUtility;
    }

    @Override
    public String generateAuthHeader(JsonNode jsonNode) {
        if(jsonNode.has("clientAuthUrl")&&jsonNode.has("clientCredentials")) {
            log.info("CourseraPluginServiceImpl::generateAuthHeader");
            IntegrationFrameworkDto dto = new IntegrationFrameworkDto();
            Map<String, String> requestHeader = new HashMap<>();
            requestHeader.put("Authorization", "Basic " + jsonNode.get("clientCredentials").asText());
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("grant_type", "client_credentials");
            dto.setUrl(jsonNode.get("clientAuthUrl").asText());
            dto.setFormData(true);
            dto.setRequestMethod("POST");
            dto.setOperationType("PEER_TO_PEER");
            dto.setRequestHeader(requestHeader);
            dto.setRequestBody(requestBody);
            dto.setServiceCode(Constants.COURSERA_AUTH_API);
            dto.setServiceName(Constants.COURSERA_AUTH_API);
            dto.setServiceDescription(Constants.COURSERA_AUTH_API);
            dto.setStrictCache(true);
            dto.setStrictCacheTimeInMinutes(cbServerProperties.courseraAuthApiCacheTtl);
            Object response = callExternalService.fetchResult(dataTransformUtility.getIntegrationFrameWorkUrl(), dto);
            JsonNode jsonResponse = objectMapper.convertValue(response, new TypeReference<JsonNode>() {
            });
            return "Bearer " + jsonResponse.path("responseData").get("access_token").asText();
        }else if(jsonNode.has("clientSegment")&&jsonNode.has("clientCode")&&jsonNode.has("clientSecret")) {
            String timestamp = String.valueOf(System.currentTimeMillis());
            String toHash = jsonNode.get("clientSegment").asText() + jsonNode.get("clientCode").asText() + timestamp + jsonNode.get("clientSecret").asText();
            MessageDigest md = null;
            try {
                md = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            byte[] hashInBytes = null;
            try {
                hashInBytes = md.digest(toHash.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            StringBuilder hashString = new StringBuilder();
            for (byte b : hashInBytes) {
                hashString.append(String.format("%02x", b));
            }
            String authHash = hashString.toString();
            return jsonNode.get("clientCode").asText() + "." + timestamp + "." + authHash;
        }
        return "";
    }
}
