package com.igot.service_locator.controller;

import com.igot.service_locator.dto.ServiceRequestDto;
import com.igot.service_locator.entity.IntegrationModel;
import com.igot.service_locator.service.IntegrationModelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

@RestController
@RequestMapping("serviceregistry")
@Slf4j
public class IntegrationController {

    @Autowired
    IntegrationModelService service;

    @PostMapping("/v1/callExternalApi")
    public Object callExternalApiService(@RequestBody IntegrationModel integrationModel) throws IOException {
        return service.getDetailsFromExternalService(integrationModel);
    }
    @PostMapping("/v1/callExternalApi/{id}")
    public Object callExternalApiServiceByConfigId(@RequestBody(required = false) ServiceRequestDto requestDto, @PathVariable String id) throws IOException {
        return service.getRequestPayloadByConfigId(requestDto,id);
    }
}
