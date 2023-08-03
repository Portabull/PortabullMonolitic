package com.portabull.utils;

import net.sf.uadetector.*;
import net.sf.uadetector.service.UADetectorServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginRequestDetails {

    private LoginRequestDetails() {
    }


    static Logger logger = LoggerFactory.getLogger(LoginRequestDetails.class);

    public static final UserAgentStringParser parser;

    static {
        parser = UADetectorServiceFactory.getResourceModuleParser();
    }

    public static String getRawLoginRequestDetails() {
        return RequestHelper.getCurrentRequest().getHeader("User-Agent");
    }

    public static void logUserDetails() {

        ReadableUserAgent agent = parser.parse(getRawLoginRequestDetails());

        logger.info("Browser type: {}", agent.getType().getName());
        logger.info("Browser name: {}", agent.getName());


        VersionNumber browserVersion = agent.getVersionNumber();

        logger.info("Browser version: {}", browserVersion.toVersionString());
        logger.info("Browser version major: {}", browserVersion.getMajor());
        logger.info("Browser version minor: {}", browserVersion.getMinor());
        logger.info("Browser version bug fix: {}", browserVersion.getBugfix());
        logger.info("Browser version extension: {}", browserVersion.getExtension());
        logger.info("Browser producer: {}", agent.getProducer());


        ReadableDeviceCategory cat = agent.getDeviceCategory();
        ReadableDeviceCategory.Category category = cat.getCategory();

        logger.info("\nCategory Name: {}", category.getName());

        String typeName = agent.getTypeName();
        UserAgentFamily userAgentFamily = agent.getFamily();
        logger.info("\nType Name: {}", typeName);
        logger.info("\nUser Agent Family Name: {}", userAgentFamily.getName());

        OperatingSystem os = agent.getOperatingSystem();
        logger.info("\nOS Name: {}", os.getName());
        logger.info("OS Producer: {}", os.getProducer());


        VersionNumber osVersion = os.getVersionNumber();
        logger.info("OS version: {}", osVersion.toVersionString());
        logger.info("OS version major: {}", osVersion.getMajor());
        logger.info("OS version minor: {}", osVersion.getMinor());
        logger.info("OS version bug fix: {}", osVersion.getBugfix());
        logger.info("OS version extension: {}", osVersion.getExtension());


        ReadableDeviceCategory device = agent.getDeviceCategory();
        logger.info("\nDevice: {}", device.getName());

    }


}
