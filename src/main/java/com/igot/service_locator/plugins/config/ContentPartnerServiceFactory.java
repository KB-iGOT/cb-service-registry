package com.igot.service_locator.plugins.config;


import com.igot.service_locator.plugins.ContentPartnerPluginService;
import com.igot.service_locator.plugins.ContentSource;

public interface ContentPartnerServiceFactory {
    ContentPartnerPluginService getContentPartnerPluginService(ContentSource contentSource);
}
