package com.igot.service_locator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Data
@Setter
@Getter
public class IntegrationFrameworkDto {
    @JsonProperty("url")
    private String url = null;

    @JsonProperty("requestMethod")
    private String requestMethod = null;

    @JsonProperty("requestHeader")
    private Map<String,String> requestHeader = null;

    @JsonProperty("requestBody")
    private Object requestBody = null;

    @JsonProperty("serviceCode")
    private String serviceCode = null;

    @JsonProperty("serviceName")
    private String serviceName = null;

    @JsonProperty("serviceDescription")
    private String serviceDescription = null;

    @JsonProperty("responseData")
    private Object responseData = null;

    @JsonProperty("operationType")
    private String operationType = null;

    @JsonProperty("strictCache")
    private boolean strictCache;

    @JsonProperty("strictCacheTimeInMinutes")
    private long strictCacheTimeInMinutes;

    @JsonProperty("alwaysDataReadFromCache")
    private boolean alwaysDataReadFromCache;

    @JsonProperty("isFormData")
    private boolean isFormData;
}
