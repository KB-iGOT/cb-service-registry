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
}
