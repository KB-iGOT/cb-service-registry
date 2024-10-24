package com.igot.service_locator.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.igot.service_locator.dto.ServiceRequestDto;
import com.igot.service_locator.entity.IntegrationModel;
import com.igot.service_locator.entity.ServiceLocatorEntity;
import com.igot.service_locator.exceptions.CustomException;
import com.igot.service_locator.repository.ServiceLocatorRepository;
import com.igot.service_locator.service.IntegrationModelService;
import com.igot.service_locator.service.ServiceLocatorService;
import com.igot.service_locator.util.Constants;
import com.igot.service_locator.util.IntegrationFrameworkUtil;
import com.igot.service_locator.util.IntegrationModelValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class IntegrationModelServiceImpl implements IntegrationModelService {

    @Autowired
    private ServiceLocatorRepository serviceLocatorRepository;

    @Autowired
    private IntegrationFrameworkUtil integrationFrameworkUtil;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private IntegrationModelValidator modelValidator;

    @Autowired
    private ServiceLocatorService serviceLocatorService;


    @Override
    public Object getDetailsFromExternalService(IntegrationModel integrationModel) throws IOException {
        log.info("IntegrationModelServiceImpl::callExternalServiceApi");
        try {
            modelValidator.validateModel(integrationModel);
            ServiceLocatorEntity serviceLocator = serviceLocatorService.readServiceConfigByServiceCode(integrationModel.getServiceCode());
            String resultantUrl = replaceUrlPlaceholders(serviceLocator, integrationModel.getUrlMap());
            log.info("The url {} to call the service", resultantUrl);
            serviceLocator.setUrl(resultantUrl);
            return integrationFrameworkUtil.callExternalServiceApi(integrationModel, serviceLocator);
        }catch (Exception e) {
            throw new CustomException(Constants.ERROR, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        throw new CustomException("SERVICE_CODE", "Service code is not configured in our system, please configure it first", HttpStatus.BAD_REQUEST);

    }

    @Override
    public Object getRequestPayloadByConfigId(ServiceRequestDto serviceRequestDto, String id) throws IOException {
        log.info("IntegrationModelServiceImpl::getRequestPayloadByConfigId");
        try {
            ServiceLocatorEntity serviceLocatorEntity = serviceLocatorService.readServiceConfig(id, true);
            JsonNode jsonNode = serviceLocatorEntity.getRequestPayload();
            if (!jsonNode.isMissingNode()) {
                IntegrationModel model = replaceServiceRequestDtoPlaceholders(jsonNode, serviceRequestDto);
                log.debug("model::{}", model);
                return getDetailsFromExternalService(model);
            } else {
                throw new CustomException(Constants.ERROR, "requestDto not present in Db with given Id ", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            throw new CustomException(Constants.ERROR, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private IntegrationModel replaceServiceRequestDtoPlaceholders(JsonNode model, ServiceRequestDto serviceRequestDto) {
        log.debug("Replacing placeholders in requestDto");
        try {
            if (serviceRequestDto != null) {
                // Replace placeholders in the requestBody with values from urlMap, requestMap, and headerMap
                if (serviceRequestDto.getRequestMap() != null && !serviceRequestDto.getRequestMap().isEmpty()) {
                    ObjectNode requestBody = (ObjectNode) model.path("requestBody");
                    replacePlaceholdersInMap(requestBody, serviceRequestDto.getRequestMap());
                }
                if (serviceRequestDto.getUrlMap() != null && !serviceRequestDto.getUrlMap().isEmpty()) {
                    ObjectNode requestBody = (ObjectNode) model.path("urlMap");
                    replacePlaceholdersInMap(requestBody, serviceRequestDto.getUrlMap());
                }
                if (serviceRequestDto.getHeaderMap() != null && !serviceRequestDto.getHeaderMap().isEmpty()) {
                    ObjectNode requestBody = (ObjectNode) model.path("headerMap");
                    replacePlaceholdersInMap(requestBody, serviceRequestDto.getHeaderMap());
                }
            }
            return mapper.treeToValue(model, IntegrationModel.class);
        } catch (Exception e) {
            log.error("Error converting JsonNode to IntegrationModel", e);
            throw new CustomException(Constants.ERROR, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    private void replacePlaceholdersInMap(ObjectNode requestBody, Map<String, String> valueMap) {
        valueMap.forEach((key, value) -> {
            // Iterate through all fields in requestBody and replace placeholders
            requestBody.fields().forEachRemaining(field -> {
                String fieldValue = field.getValue().asText();
                String placeholder = "{" + key + "}";
                if (fieldValue.contains(placeholder)) {
                    // Replace placeholder with the actual value
                    requestBody.put(field.getKey(), fieldValue.replace(placeholder, value));
                }
            });
        });
    }

    private String replaceUrlPlaceholders(ServiceLocatorEntity serviceLocator, Map<String, String> urlMap) {
        log.info("IntegrationModelServiceImpl::replaceUrlPlaceholders");
        String urlToModify = serviceLocator.getUrl();
        if (StringUtils.isNotBlank(serviceLocator.getUrlPlaceholder()) || !CollectionUtils.isEmpty(urlMap)) {
            String urlPlaceholder = serviceLocator.getUrlPlaceholder();

            String[] urlPlaceholderArr = urlPlaceholder.split(",");
            String placeholderValue = null;
            for (int i = 0; i < urlPlaceholderArr.length; i++) {
                String placeholder = urlPlaceholderArr[i];
                if (placeholder.equalsIgnoreCase("{hostAddress}")) {
                    placeholderValue = serviceLocator.getHostAddress();
                } else {
                    String placeholderWithoutCurlyBraces = placeholder.substring(1, placeholder.length() - 1);
                    if (urlMap.containsKey(placeholderWithoutCurlyBraces)) {
                        String value = urlMap.get(placeholderWithoutCurlyBraces);
                        if (StringUtils.isNotBlank(value)) {
                            placeholderValue = value;
                        }
                    } else {
                        placeholderValue = ""; // Assign an empty value if the field is not present in urlMap
                    }
                }
                if (placeholderValue != null) {
                    urlToModify = urlToModify.replace(placeholder, placeholderValue);
                } else {
                    // Replace the placeholder with an empty string if the value is null
                    urlToModify = urlToModify.replace(placeholder, "");
                }
            }
        }
        return urlToModify;
    }


}
