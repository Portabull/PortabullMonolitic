package com.portabull.utils;

import com.netflix.zuul.context.RequestContext;
import com.portabull.constants.HttpConstants;
import com.portabull.constants.MessageConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Component
public class RequestHelper {

    static Logger logger = LoggerFactory.getLogger(RequestHelper.class);

    private RequestHelper() {

    }

    public static String getAuthorizationToken() {
        String authorization = RequestHelper.getCurrentRequest().getHeader(HttpConstants.AUTHORIZATION);
        if (!isEmpty(authorization)) {
            return authorization.replace(HttpConstants.BEARER, "").trim();
        }
        return null;
    }

    public static String getBearerToken() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        return request.getHeader(HttpConstants.AUTHORIZATION);
    }

    public static boolean isEmpty(Object str) {
        return StringUtils.isEmpty(str);
    }

    public static String getAuthorizationToken(HttpServletRequest rawData) {
        if (rawData == null) {
            logger.info(MessageConstants.HTTP_SERVLET_EMPTY);
            return null;
        }
        String authorization = rawData.getHeader(HttpConstants.AUTHORIZATION);
        if (!isEmpty(authorization)) {
            return authorization.replace(HttpConstants.BEARER, "").trim();
        }
        return null;
    }

    public static HttpServletRequest getCurrentRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

}
