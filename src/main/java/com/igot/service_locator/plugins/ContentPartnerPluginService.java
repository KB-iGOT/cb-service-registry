package com.igot.service_locator.plugins;


import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
@Service
public interface ContentPartnerPluginService {
    String generateAuthHeader(JsonNode jsonNode);
}
