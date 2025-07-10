package com.igot.service_locator.repository.rowMapper;

import com.igot.service_locator.entity.ServiceLocatorEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServiceLocatorMapperTest {

    private ServiceLocatorMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ServiceLocatorMapper();
    }

    @Test
    void testExtractData_withOneRow() throws SQLException {
        ResultSet rs = mock(ResultSet.class);

        // Simulate one row
        when(rs.next()).thenReturn(true, false);
        when(rs.getString("id")).thenReturn("test-id");
        when(rs.getBoolean("is_active")).thenReturn(true);
        when(rs.getString("operation_type")).thenReturn("CREATE");
        when(rs.getString("service_code")).thenReturn("service-code");
        when(rs.getString("service_name")).thenReturn("service-name");
        when(rs.getString("service_description")).thenReturn("service-description");
        when(rs.getInt("request_method")).thenReturn(0);
        when(rs.getString("url_value")).thenReturn("http://example.com");
        when(rs.getString("url_placeholder")).thenReturn("{id}");
        when(rs.getBoolean("is_secure_header")).thenReturn(true);

        List<ServiceLocatorEntity> result = mapper.extractData(rs);

        assertNotNull(result);
        assertEquals(1, result.size());

        ServiceLocatorEntity entity = result.get(0);
        assertEquals("test-id", entity.getId());
        assertEquals("CREATE", entity.getOperationType());
        assertEquals("service-code", entity.getServiceCode());
        assertEquals("service-name", entity.getServiceName());
        assertEquals("service-description", entity.getServiceDescription());
        assertEquals(ServiceLocatorEntity.RequestMethod.values()[0], entity.getRequestMethod());
        assertEquals("http://example.com", entity.getUrl());
        assertEquals("{id}", entity.getUrlPlaceholder());
        verify(rs, times(2)).next();
    }

    @Test
    void testExtractData_withNoRows() throws SQLException {
        ResultSet rs = mock(ResultSet.class);

        // No rows
        when(rs.next()).thenReturn(false);

        List<ServiceLocatorEntity> result = mapper.extractData(rs);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(rs, times(1)).next();
    }
}
