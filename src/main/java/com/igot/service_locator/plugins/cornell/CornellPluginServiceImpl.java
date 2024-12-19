package com.igot.service_locator.plugins.cornell;

import com.fasterxml.jackson.databind.JsonNode;
import com.igot.service_locator.plugins.ContentPartnerPluginService;
import com.igot.service_locator.util.CbServerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
@Slf4j
public class CornellPluginServiceImpl  implements ContentPartnerPluginService {

    @Autowired
    private CbServerProperties cbServerProperties;

    @Override
    public String generateAuthHeader(JsonNode jsonNode) {
        log.info("CornellPluginServiceImpl::generateAuthHeader");
        String urlSegment = jsonNode.get("clientSegment").asText();
        String clientCode=jsonNode.get("clientSecret").asText();
        String clientSecret=jsonNode.get("clientCode").asText();
        String timestamp = String.valueOf(System.currentTimeMillis());
        String toHash = urlSegment + clientCode + timestamp + clientSecret;
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] hashInBytes = null;
        try {
            hashInBytes = md.digest(toHash.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        StringBuilder hashString = new StringBuilder();
        for (byte b : hashInBytes) {
            hashString.append(String.format("%02x", b));
        }
        String authHash = hashString.toString();
        return clientCode + "." + timestamp + "." + authHash;
    }
}
