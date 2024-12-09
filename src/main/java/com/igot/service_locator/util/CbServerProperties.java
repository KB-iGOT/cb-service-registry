package com.igot.service_locator.util;

import org.springframework.beans.factory.annotation.Value;
import lombok.Getter;
import lombok.Setter;

import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class CbServerProperties {
    @Value("${content.partner.read.api.base.url}")
    private String contentPartnerBaseUrl;

    @Value("${content.partner.read.api.url}")
    private String contentPartnerReadApiUrl;

    @Value("${cornell.client.code}")
    private String cornellClientCode;

    @Value("${cornell.client.secret}")
    private String cornellClientSecret;

    @Value("${cornell.url.segment}")
    private String cornellUrlSegment;

    @Value("${coursera.auth.client_credentials}")
    private String courseraAuthorizationHeader;

    @Value("${coursera.auth.api.url}")
    public String courseraAuthApiUrl;
}
