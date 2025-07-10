package com.igot.service_locator.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.igot.service_locator.config.IntegrationConfig;
import com.igot.service_locator.entity.IntegrationModel;
import com.igot.service_locator.entity.ServiceLocatorEntity;
import com.igot.service_locator.plugins.AuthPluginService;
import com.igot.service_locator.repository.CallExternalService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class IntegrationFrameworkUtilTest {

    @Mock IntegrationConfig config;
    @Mock CallExternalService callExternalService;
    @Mock DataTransformUtility dataTransformUtility;
    @Mock AuthPluginService authPluginService;

    ObjectMapper mapper = new ObjectMapper();

    IntegrationFrameworkUtil util;

    @BeforeEach
    void setUp() {
        util = new IntegrationFrameworkUtil(config, mapper, callExternalService, dataTransformUtility, authPluginService);
    }

    ServiceLocatorEntity serviceLocatorMock() {
        ServiceLocatorEntity s = mock(ServiceLocatorEntity.class);
        when(s.getUrl()).thenReturn("http://test");
        when(s.isFormData()).thenReturn(false);
        when(s.getRequestMethod()).thenReturn(ServiceLocatorEntity.RequestMethod.POST);
        when(s.getOperationType()).thenReturn("op");
        when(s.isSecureHeader()).thenReturn(false);
        when(s.getServiceCode()).thenReturn("code");
        when(s.getServiceName()).thenReturn("name");
        when(s.getServiceDescription()).thenReturn("desc");
        when(s.isStrictCache()).thenReturn(false);
        when(s.getStrictCacheTimeInMinutes()).thenReturn(10L);
        return s;
    }

    ObjectNode responseObjectWithResponseData() {
        ObjectNode response = mapper.createObjectNode();
        ObjectNode responseData = response.putObject("responseData");
        responseData.put("k", "v");
        return response;
    }

    @Test
    void test_partnerCode_with_transformContentViaApi() throws Exception {
        IntegrationModel model = new IntegrationModel();
        model.setPartnerCode("partner");

        ServiceLocatorEntity s = serviceLocatorMock();

        when(dataTransformUtility.getIntegrationFrameWorkUrl()).thenReturn(new StringBuilder("url"));
        when(callExternalService.fetchResult(any(), any())).thenReturn(responseObjectWithResponseData());

        ObjectNode partnerResponse = mapper.createObjectNode();
        partnerResponse.set("transformContentViaApi", mapper.createArrayNode()
                .add(mapper.createObjectNode().put("spec", "val")));
        when(dataTransformUtility.callContentPartnerReadAPIByPartnerCode("partner")).thenReturn(partnerResponse);

        when(dataTransformUtility.transformData(any(), any())).thenReturn(Map.of("transformed", "data"));

        Object result = util.callExternalServiceApi(model, s);
        assertNotNull(result);
    }

    @Test
    void test_partnerCode_without_transformContentViaApi() throws Exception {
        IntegrationModel model = new IntegrationModel();
        model.setPartnerCode("partner");

        ServiceLocatorEntity s = serviceLocatorMock();

        when(dataTransformUtility.getIntegrationFrameWorkUrl()).thenReturn(new StringBuilder("url"));
        when(callExternalService.fetchResult(any(), any())).thenReturn(responseObjectWithResponseData());

        ObjectNode partnerResponse = mapper.createObjectNode(); // no transformContentViaApi
        when(dataTransformUtility.callContentPartnerReadAPIByPartnerCode("partner")).thenReturn(partnerResponse);

        Object result = util.callExternalServiceApi(model, s);
        assertNotNull(result);
    }

    @Test
    void test_serviceLocatorPartnerCode_with_transformProgressViaApi() throws Exception {
        IntegrationModel model = new IntegrationModel();

        ServiceLocatorEntity s = serviceLocatorMock();
        when(s.getPartnerCode()).thenReturn("locatorPartner");

        when(dataTransformUtility.getIntegrationFrameWorkUrl()).thenReturn(new StringBuilder("url"));
        when(callExternalService.fetchResult(any(), any())).thenReturn(responseObjectWithResponseData());

        ObjectNode partnerResponse = mapper.createObjectNode();
        partnerResponse.set("transformProgressViaApi", mapper.createArrayNode()
                .add(mapper.createObjectNode().put("spec", "val")));
        when(dataTransformUtility.callContentPartnerReadAPIByPartnerCode("locatorPartner")).thenReturn(partnerResponse);

        when(dataTransformUtility.transformData(any(), any())).thenReturn(Map.of("transformed", "data"));

        Object result = util.callExternalServiceApi(model, s);
        assertNotNull(result);
    }

    @Test
    void test_serviceLocatorPartnerCode_without_transformProgressViaApi() throws Exception {
        IntegrationModel model = new IntegrationModel();

        ServiceLocatorEntity s = serviceLocatorMock();
        when(s.getPartnerCode()).thenReturn("locatorPartner");

        when(dataTransformUtility.getIntegrationFrameWorkUrl()).thenReturn(new StringBuilder("url"));
        when(callExternalService.fetchResult(any(), any())).thenReturn(responseObjectWithResponseData());

        ObjectNode partnerResponse = mapper.createObjectNode(); // no transformProgressViaApi
        when(dataTransformUtility.callContentPartnerReadAPIByPartnerCode("locatorPartner")).thenReturn(partnerResponse);

        Object result = util.callExternalServiceApi(model, s);
        assertNotNull(result);
    }

    @Test
    void test_noPartnerCodes() throws Exception {
        IntegrationModel model = new IntegrationModel();

        ServiceLocatorEntity s = serviceLocatorMock();
        when(s.getPartnerCode()).thenReturn(null);

        when(dataTransformUtility.getIntegrationFrameWorkUrl()).thenReturn(new StringBuilder("url"));
        when(callExternalService.fetchResult(any(), any())).thenReturn(responseObjectWithResponseData());

        Object result = util.callExternalServiceApi(model, s);
        assertNotNull(result);
    }

    @Test
    void test_with_headerMap() throws Exception {
        IntegrationModel model = new IntegrationModel();
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("X-Test", "value");
        model.setHeaderMap(headerMap);

        ServiceLocatorEntity s = serviceLocatorMock();
        when(s.getPartnerCode()).thenReturn(null);

        when(dataTransformUtility.getIntegrationFrameWorkUrl()).thenReturn(new StringBuilder("url"));
        when(callExternalService.fetchResult(any(), any())).thenReturn(responseObjectWithResponseData());

        Object result = util.callExternalServiceApi(model, s);
        assertNotNull(result);
    }
}
