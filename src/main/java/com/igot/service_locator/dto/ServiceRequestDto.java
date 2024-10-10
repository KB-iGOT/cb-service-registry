package com.igot.service_locator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Data
@Setter
@Getter
public class ServiceRequestDto {
    @JsonProperty("headerMap")
    private Map<String, String> headerMap;
    @JsonProperty("urlMap")
    private Map<String, String> urlMap;
    @JsonProperty("requestMap")
    private Map<String, String> requestMap;
}
