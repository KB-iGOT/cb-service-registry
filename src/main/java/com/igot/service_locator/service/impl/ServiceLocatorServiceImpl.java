package com.igot.service_locator.service.impl;


import com.igot.service_locator.dto.PaginatedResponse;
import com.igot.service_locator.dto.PaginatedRequestDto;
import com.igot.service_locator.exceptions.CustomException;
import com.igot.service_locator.repository.ServiceLocatorRepository;
import com.igot.service_locator.repository.rowMapper.ServiceLocatorMapper;
import com.igot.service_locator.repository.rowMapper.ServiceLocatorQueryBuilder;
import com.igot.service_locator.dto.ServiceLocatorDto;
import com.igot.service_locator.entity.ServiceLocatorEntity;

import com.igot.service_locator.service.ServiceLocatorService;
import com.igot.service_locator.util.Constants;
import com.igot.service_locator.validator.ServiceLocatorValidator;
import com.fasterxml.uuid.Generators;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import java.time.Duration;
import java.util.*;


@Service
@Slf4j
public class ServiceLocatorServiceImpl implements ServiceLocatorService {
    @Value("${cache.data.ttl.in.minutes}")
    public Long cacheDataTtl;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ServiceLocatorRepository serviceLocaterRepository;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private ServiceLocatorValidator locatorValidator;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ServiceLocatorQueryBuilder queryBuilder;

    @Autowired
    private ServiceLocatorMapper locatorMapper;

    public static final String SERVICE_LOCATOR_KEY = "servicelocator_";


    private static final String ERROR_MESSAGE = "ERROR";

    @Override
    public ServiceLocatorEntity createOrUpdateServiceConfig(ServiceLocatorEntity locatorEntity) {
        locatorValidator.validate(locatorEntity);
        if (locatorEntity.getId() == null) {
            Optional<ServiceLocatorEntity> optSchemeDetails = serviceLocaterRepository.findByServiceCodeAndIsActiveTrue(locatorEntity.getServiceCode());
            if (optSchemeDetails.isPresent()) {
                throw new CustomException("SERVICE_CODE", "One service is already there in the system with service code : " + locatorEntity.getServiceCode() + " , " +
                        "Please create a service config with unique service code", HttpStatus.BAD_REQUEST);
            }
            UUID uuid = Generators.timeBasedGenerator().generate();
            String id = uuid.toString();
            locatorEntity.setId(id);
            locatorEntity.setActive(Boolean.TRUE);
            //save to the database
            ServiceLocatorEntity serviceLocatorEntity = serviceLocaterRepository.save(locatorEntity);
            // Save to Redis
            redisTemplate.opsForValue().set(SERVICE_LOCATOR_KEY + serviceLocatorEntity.getServiceCode(), serviceLocatorEntity, Duration.ofMinutes(cacheDataTtl));
            redisTemplate.opsForValue().set(SERVICE_LOCATOR_KEY + serviceLocatorEntity.getId(), serviceLocatorEntity, Duration.ofMinutes(cacheDataTtl));
            return serviceLocatorEntity;
        }else{
            Optional<ServiceLocatorEntity> optSchemeDetails = serviceLocaterRepository.findById(locatorEntity.getId());
            if (optSchemeDetails.isPresent()) {
                ServiceLocatorEntity batchService = optSchemeDetails.get();
                // Copy the property values from updatedSchemeDetails to schemeDetails, Exclude the "id" fields from being copied
                BeanUtils.copyProperties(locatorEntity, batchService, "id");
                redisTemplate.opsForValue().set(SERVICE_LOCATOR_KEY + batchService.getServiceCode(), batchService, Duration.ofMinutes(cacheDataTtl));
                redisTemplate.opsForValue().set(SERVICE_LOCATOR_KEY + batchService.getId(), batchService, Duration.ofMinutes(cacheDataTtl));
                return serviceLocaterRepository.save(batchService);
            }
        }
        return null;
    }

    @Override
    public String deleteServiceConfig(String id) {

        Optional<ServiceLocatorEntity> dataFromDb = serviceLocaterRepository.findById(id);
        if (dataFromDb.isPresent()) {
            ServiceLocatorEntity entity = dataFromDb.get();
            entity.setActive(false);
            serviceLocaterRepository.save(entity);
            String serviceCode = entity.getServiceCode();
            String redisKeyServiceCode = SERVICE_LOCATOR_KEY + serviceCode;
            String redisKeyId = SERVICE_LOCATOR_KEY + entity.getId();
            redisTemplate.delete(redisKeyServiceCode);
            redisTemplate.delete(redisKeyId);
            return "Data deleted successfully with id " + id;
        } else {
            throw new CustomException(ERROR_MESSAGE, "Data Not found to delete with given id " + id, HttpStatus.BAD_REQUEST);
        }

    }

    @Override
    public List<ServiceLocatorEntity> searchServiceConfig(ServiceLocatorDto searchCriteria) {

        if (CollectionUtils.isEmpty(searchCriteria.getIds())
                && StringUtils.isBlank(searchCriteria.getUrl())
                && StringUtils.isBlank(searchCriteria.getServiceCode())
                && StringUtils.isBlank(searchCriteria.getServiceName())
                && StringUtils.isBlank(searchCriteria.getOperationType())) {
            throw new CustomException("SEARCH_CRITERIA", "One search criteria must be provided.", HttpStatus.BAD_REQUEST);
        }

        List<Object> preparedStmtList = new ArrayList<>();

        String query = queryBuilder.getServiceLocatorQuery(searchCriteria, preparedStmtList);
        List<ServiceLocatorEntity> serviceLocatorEntityList = jdbcTemplate.query(query, locatorMapper, preparedStmtList.toArray());

        if (CollectionUtils.isEmpty(serviceLocatorEntityList)) {
            throw new CustomException(ERROR_MESSAGE, "No data available for the search result", HttpStatus.BAD_REQUEST);
        }
        return serviceLocatorEntityList;
    }


    @Override
    public PaginatedResponse getAllServiceConfig(PaginatedRequestDto dto) {
        try {
            Pageable pageable = PageRequest.of(dto.getOffset(), dto.getLimit());
            Page<Object> pageData = null;
            pageData = serviceLocaterRepository.fetchAll(dto.getIsActive(), pageable);
            return new PaginatedResponse<>(
                    pageData.getContent(),
                    pageData.getTotalPages(),
                    pageData.getTotalElements(),
                    pageData.getNumberOfElements(),
                    pageData.getSize(),
                    pageData.getNumber()
            );
        } catch (DataAccessException dae) {
            log.error("Database access error while fetching content", dae.getMessage());
            throw new CustomException(Constants.ERROR, "Database access error: " + dae.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            throw new CustomException(Constants.ERROR, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ServiceLocatorEntity readServiceConfig(String id,boolean isActive) {
        String cacheKey = SERVICE_LOCATOR_KEY + id;
        ServiceLocatorEntity cachedServiceLocator = (ServiceLocatorEntity) redisTemplate.opsForValue().get(cacheKey);
        if (cachedServiceLocator != null) {
            log.info("Config data reading from cache");
            return cachedServiceLocator;
        }
        try {
            Optional<ServiceLocatorEntity> optionalServiceLocator = serviceLocaterRepository.findByIdAndIsActive(id, isActive);
            if (optionalServiceLocator.isPresent()) {
                ServiceLocatorEntity serviceLocatorEntity = optionalServiceLocator.get();
                redisTemplate.opsForValue().set(cacheKey, serviceLocatorEntity, Duration.ofMinutes(cacheDataTtl));
                return serviceLocatorEntity;
            } else {
                throw new CustomException(Constants.ERROR, "Data not present in Db with given Id", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            throw new CustomException(Constants.ERROR, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ServiceLocatorEntity readServiceConfigByServiceCode(String serviceCode) {
        String cacheKey = SERVICE_LOCATOR_KEY + serviceCode;
        ServiceLocatorEntity cachedServiceLocator = (ServiceLocatorEntity) redisTemplate.opsForValue().get(cacheKey);
        if (cachedServiceLocator != null) {
            log.info("Config data reading from cache");
            return cachedServiceLocator;
        }
        try {
            Optional<ServiceLocatorEntity> optionalServiceLocator = serviceLocaterRepository.findByServiceCodeAndIsActiveTrue(serviceCode);
            if (optionalServiceLocator.isPresent()) {
                ServiceLocatorEntity serviceLocatorEntity = optionalServiceLocator.get();
                redisTemplate.opsForValue().set(cacheKey, serviceLocatorEntity, Duration.ofMinutes(cacheDataTtl));
                return serviceLocatorEntity;
            } else {
                throw new CustomException(Constants.ERROR, "Service code is not configured in our system, please configure it first "+serviceCode, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            throw new CustomException(Constants.ERROR, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
