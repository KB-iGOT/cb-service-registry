package com.igot.service_locator.plugins.config;

import com.igot.service_locator.plugins.ContentPartnerPluginService;
import com.igot.service_locator.plugins.ContentSource;
import com.igot.service_locator.plugins.cornell.CornellPluginServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContentPartnerServiceFactoryImpl implements ContentPartnerServiceFactory {
    private final CornellPluginServiceImpl cornellPluginService;
    @Autowired
    public ContentPartnerServiceFactoryImpl(CornellPluginServiceImpl cornellPluginService) {
        this.cornellPluginService = cornellPluginService;
    }
    @Override
    public ContentPartnerPluginService getContentPartnerPluginService(ContentSource contentSource) {
        switch (contentSource) {
            case CORNELL:
                return cornellPluginService;
            default:
                throw new IllegalArgumentException("Unsupported ContentSource: " + contentSource);
        }
    }
}
