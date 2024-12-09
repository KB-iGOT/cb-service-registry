package com.igot.service_locator.plugins.config;

import com.igot.service_locator.plugins.ContentPartnerPluginService;
import com.igot.service_locator.plugins.ContentSource;
import com.igot.service_locator.plugins.cornell.CornellPluginServiceImpl;

import com.igot.service_locator.plugins.coursera.CourseraPluginServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContentPartnerServiceFactoryImpl implements ContentPartnerServiceFactory {

    @Autowired
    private CornellPluginServiceImpl cornellPluginService;

    @Autowired
    private CourseraPluginServiceImpl courseraPluginService;

    @Override
    public ContentPartnerPluginService getContentPartnerPluginService(ContentSource contentSource) {
        switch (contentSource) {
            case CORNELL:
                return cornellPluginService;
            case COURSERA:
                return courseraPluginService;
            default:
                throw new IllegalArgumentException("Unsupported ContentSource: " + contentSource);
        }
    }
}
