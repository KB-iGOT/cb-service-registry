package com.igot.service_locator.validator;

import com.fasterxml.jackson.databind.JsonNode;
import com.igot.service_locator.entity.ServiceLocatorEntity;
import com.igot.service_locator.exceptions.CustomException;
import com.igot.service_locator.util.Constants;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.ValidationMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Set;

@Component
@Slf4j
public class ServiceLocatorValidator {


    public void validate(ServiceLocatorEntity entity) {
        log.info("ServiceLocatorValidator::validate");
        if(entity == null){
           throw new CustomException("SERVICE_LOCATOR_CONFIG","Service locator config is mandatory", HttpStatus.BAD_REQUEST);
        }
        if(StringUtils.isBlank(entity.getUrl())){
            throw new CustomException("URL","Url is mandatory",HttpStatus.BAD_REQUEST);
        }
        if(StringUtils.isBlank(entity.getServiceCode())){
            throw new CustomException("SERVICE_CODE","Service code is missing",HttpStatus.BAD_REQUEST);
        }
        if(StringUtils.isBlank(entity.getServiceName())){
            throw new CustomException("SERVICE_NAME","Service name is missing",HttpStatus.BAD_REQUEST);
        }
        if(StringUtils.isBlank(entity.getOperationType())){
            throw new CustomException("OPERATION_TYPE","Operation type is missing",HttpStatus.BAD_REQUEST);
        }
        if(entity.getRequestMethod() == null
                || !EnumUtils.isValidEnum(ServiceLocatorEntity.RequestMethod.class,entity.getRequestMethod().name())){
            throw new CustomException("REQUEST_METHOD","Request method is mandatory",HttpStatus.BAD_REQUEST);
        }

        if (entity.isSecureHeader()){
            if(entity != null && entity.getAuthPayload() != null && !entity.getAuthPayload().isMissingNode()){
                validatePayload(Constants.PAYLOAD_VALIDATION_FILE_AUTH_PAYLOAD, entity.getAuthPayload());
            }else{
                throw new CustomException("AUTH_PAYLOAD","Auth payload is mandatory",HttpStatus.BAD_REQUEST);

            }
        }

    }

    public void validatePayload(String fileName, JsonNode payload) {
        try {
            JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance();
            InputStream schemaStream = schemaFactory.getClass().getResourceAsStream(fileName);
            JsonSchema schema = schemaFactory.getSchema(schemaStream);
            validateObject(schema, payload);
        } catch (Exception e) {
            log.error("Failed to validate payload", e);
            throw new CustomException("Failed to validate payload", e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    private void validateObject(JsonSchema schema, JsonNode objectNode) {
        Set<ValidationMessage> validationMessages = schema.validate(objectNode);
        if (!validationMessages.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder("Validation error(s): \n");
            for (ValidationMessage message : validationMessages) {
                errorMessage.append(message.getMessage()).append("\n");
            }
            log.error("Validation Error", errorMessage.toString());
            throw new CustomException("Validation Error", errorMessage.toString(), HttpStatus.BAD_REQUEST);
        }
    }
}
