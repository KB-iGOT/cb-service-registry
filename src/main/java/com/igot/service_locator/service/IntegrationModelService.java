package com.igot.service_locator.service;

import com.igot.service_locator.dto.ServiceRequestDto;
import com.igot.service_locator.entity.IntegrationModel;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


@Service
public interface IntegrationModelService {
     Object getDetailsFromExternalService(IntegrationModel integrationModel) throws IOException;
     Object getRequestPayloadByConfigId(ServiceRequestDto requestDto, String id) throws IOException;
}
