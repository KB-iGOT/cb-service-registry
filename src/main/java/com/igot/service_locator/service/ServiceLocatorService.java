package com.igot.service_locator.service;

import com.igot.service_locator.dto.PaginatedResponse;
import com.igot.service_locator.dto.PaginatedRequestDto;
import com.igot.service_locator.dto.ServiceLocatorDto;
import com.igot.service_locator.entity.ServiceLocatorEntity;
import java.util.List;

public interface ServiceLocatorService {

  ServiceLocatorEntity createOrUpdateServiceConfig(ServiceLocatorEntity batchService);

  String deleteServiceConfig(String id);

  List<ServiceLocatorEntity> searchServiceConfig(ServiceLocatorDto serviceLocatorDto);

  PaginatedResponse getAllServiceConfig(PaginatedRequestDto dto);

  ServiceLocatorEntity readServiceConfig(String id);
}
