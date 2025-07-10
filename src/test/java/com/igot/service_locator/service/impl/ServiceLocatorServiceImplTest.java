package com.igot.service_locator.service.impl;

import com.igot.service_locator.dto.PaginatedRequestDto;
import com.igot.service_locator.dto.PaginatedResponse;
import com.igot.service_locator.dto.ServiceLocatorDto;
import com.igot.service_locator.entity.ServiceLocatorEntity;
import com.igot.service_locator.exceptions.CustomException;
import com.igot.service_locator.repository.ServiceLocatorRepository;
import com.igot.service_locator.repository.rowMapper.ServiceLocatorMapper;
import com.igot.service_locator.repository.rowMapper.ServiceLocatorQueryBuilder;
import com.igot.service_locator.validator.ServiceLocatorValidator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.Duration;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServiceLocatorServiceImplTest {

    @InjectMocks
    private ServiceLocatorServiceImpl service = new ServiceLocatorServiceImpl();
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ServiceLocatorRepository repository;

    @Mock
    private ServiceLocatorValidator validator;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private ServiceLocatorQueryBuilder queryBuilder;

    @Mock
    private ServiceLocatorMapper mapper;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        service.cacheDataTtl = 10L;
    }

    @Test
    void testCreateServiceConfig_new() {
        ServiceLocatorEntity entity = new ServiceLocatorEntity();
        entity.setServiceCode("code");

        when(repository.findByServiceCodeAndIsActiveTrue("code")).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        ServiceLocatorEntity result = service.createOrUpdateServiceConfig(entity);

        assertNotNull(result.getId());
        verify(valueOperations, times(2)).set(contains("servicelocator_"), eq(result), any(Duration.class));
    }

    @Test
    void testCreateServiceConfig_duplicateServiceCode() {
        ServiceLocatorEntity existing = new ServiceLocatorEntity();
        existing.setServiceCode("code");

        ServiceLocatorEntity entity = new ServiceLocatorEntity();
        entity.setServiceCode("code");

        when(repository.findByServiceCodeAndIsActiveTrue("code")).thenReturn(Optional.of(existing));

        CustomException ex = assertThrows(CustomException.class,
                () -> service.createOrUpdateServiceConfig(entity));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatusCode());
    }

    @Test
    void testUpdateServiceConfig_existing() {
        ServiceLocatorEntity entity = new ServiceLocatorEntity();
        entity.setId("id");
        entity.setServiceCode("code");

        ServiceLocatorEntity dbEntity = new ServiceLocatorEntity();
        dbEntity.setId("id");

        when(repository.findById("id")).thenReturn(Optional.of(dbEntity));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        ServiceLocatorEntity result = service.createOrUpdateServiceConfig(entity);

        assertNotNull(result);
        verify(valueOperations, times(2)).set(contains("servicelocator_"), eq(result), any(Duration.class));
    }

    @Test
    void testUpdateServiceConfig_notFound() {
        ServiceLocatorEntity entity = new ServiceLocatorEntity();
        entity.setId("id");

        when(repository.findById("id")).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> service.createOrUpdateServiceConfig(entity));
    }

    @Test
    void testDeleteServiceConfig_found() {
        ServiceLocatorEntity dbEntity = new ServiceLocatorEntity();
        dbEntity.setId("id");
        dbEntity.setServiceCode("code");

        when(repository.findById("id")).thenReturn(Optional.of(dbEntity));
        when(repository.save(any())).thenReturn(dbEntity);

        String result = service.deleteServiceConfig("id");

        assertTrue(result.contains("Data deleted successfully"));
        verify(redisTemplate, times(2)).delete(anyString());
    }

    @Test
    void testDeleteServiceConfig_notFound() {
        when(repository.findById("id")).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> service.deleteServiceConfig("id"));
    }

    @Test
    void testSearchServiceConfig_valid() {
        ServiceLocatorDto dto = new ServiceLocatorDto();
        dto.setServiceCode("code");

        when(queryBuilder.getServiceLocatorQuery(any(), any()))
                .thenReturn("SELECT * FROM table");

        when(jdbcTemplate.query(anyString(), eq(mapper), any(Object[].class)))
                .thenReturn(List.of(new ServiceLocatorEntity()));

        List<ServiceLocatorEntity> result = service.searchServiceConfig(dto);

        assertFalse(result.isEmpty());
    }

    @Test
    void testSearchServiceConfig_noCriteria() {
        ServiceLocatorDto dto = new ServiceLocatorDto();

        assertThrows(CustomException.class, () -> service.searchServiceConfig(dto));
    }

    @Test
    void testSearchServiceConfig_noResult() {
        ServiceLocatorDto dto = new ServiceLocatorDto();
        dto.setServiceCode("code");

        when(queryBuilder.getServiceLocatorQuery(any(), any())).thenReturn("SELECT");
        when(jdbcTemplate.query(anyString(), eq(mapper), any(Object[].class))).thenReturn(Collections.emptyList());

        assertThrows(CustomException.class, () -> service.searchServiceConfig(dto));
    }

    @Test
    void testGetAllServiceConfig_success() {
        PaginatedRequestDto dto = new PaginatedRequestDto();
        dto.setOffset(0);
        dto.setLimit(10);
        dto.setIsActive(true);

        Page<Object> page = new PageImpl<>(List.of(new Object()), PageRequest.of(0, 10), 1);

        when(repository.fetchAll(anyBoolean(), any())).thenReturn(page);

        PaginatedResponse response = service.getAllServiceConfig(dto);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void testGetAllServiceConfig_dbError() {
        PaginatedRequestDto dto = new PaginatedRequestDto();
        dto.setOffset(0);
        dto.setLimit(10);
        dto.setIsActive(true);

        when(repository.fetchAll(anyBoolean(), any())).thenThrow(mock(DataAccessException.class));

        assertThrows(CustomException.class, () -> service.getAllServiceConfig(dto));
    }

    @Test
    void testReadServiceConfig_fromCache() {
        ServiceLocatorEntity cached = new ServiceLocatorEntity();
        cached.setId("id");

        when(valueOperations.get("servicelocator_id")).thenReturn(cached);

        ServiceLocatorEntity result = service.readServiceConfig("id", true);

        assertEquals(cached, result);
    }

    @Test
    void testReadServiceConfig_fromDb() {
        when(valueOperations.get("servicelocator_id")).thenReturn(null);

        ServiceLocatorEntity dbEntity = new ServiceLocatorEntity();
        dbEntity.setId("id");

        when(repository.findByIdAndIsActive("id", true)).thenReturn(Optional.of(dbEntity));

        ServiceLocatorEntity result = service.readServiceConfig("id", true);

        assertNotNull(result);
    }

    @Test
    void testReadServiceConfig_notFound() {
        when(valueOperations.get("servicelocator_id")).thenReturn(null);
        when(repository.findByIdAndIsActive("id", true)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> service.readServiceConfig("id", true));
    }

    @Test
    void testReadServiceConfigByServiceCode_fromCache() {
        ServiceLocatorEntity cached = new ServiceLocatorEntity();
        cached.setServiceCode("code");

        when(valueOperations.get("servicelocator_code")).thenReturn(cached);

        ServiceLocatorEntity result = service.readServiceConfigByServiceCode("code");

        assertEquals(cached, result);
    }

    @Test
    void testReadServiceConfigByServiceCode_fromDb() {
        when(valueOperations.get("servicelocator_code")).thenReturn(null);

        ServiceLocatorEntity dbEntity = new ServiceLocatorEntity();
        dbEntity.setServiceCode("code");

        when(repository.findByServiceCodeAndIsActiveTrue("code")).thenReturn(Optional.of(dbEntity));

        ServiceLocatorEntity result = service.readServiceConfigByServiceCode("code");

        assertNotNull(result);
    }

    @Test
    void testReadServiceConfigByServiceCode_notFound() {
        when(valueOperations.get("servicelocator_code")).thenReturn(null);
        when(repository.findByServiceCodeAndIsActiveTrue("code")).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> service.readServiceConfigByServiceCode("code"));
    }
}
