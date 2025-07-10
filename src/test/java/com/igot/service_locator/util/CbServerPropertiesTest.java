package com.igot.service_locator.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CbServerPropertiesTest {

    @Test
    void testGettersAndSetters() {
        CbServerProperties props = new CbServerProperties();

        props.setContentPartnerBaseUrl("baseUrl");
        props.setContentPartnerReadApiUrl("readApiUrl");
        props.setCornellClientCode("clientCode");
        props.setCornellClientSecret("clientSecret");
        props.setCornellUrlSegment("urlSegment");
        props.setCourseraAuthorizationHeader("authHeader");
        props.setCourseraAuthApiUrl("apiUrl");
        props.setCourseraAuthApiCacheTtl(123L);

        assertEquals("baseUrl", props.getContentPartnerBaseUrl());
        assertEquals("readApiUrl", props.getContentPartnerReadApiUrl());
        assertEquals("clientCode", props.getCornellClientCode());
        assertEquals("clientSecret", props.getCornellClientSecret());
        assertEquals("urlSegment", props.getCornellUrlSegment());
        assertEquals("authHeader", props.getCourseraAuthorizationHeader());
        assertEquals("apiUrl", props.getCourseraAuthApiUrl());
        assertEquals(123L, props.getCourseraAuthApiCacheTtl());
    }
}

