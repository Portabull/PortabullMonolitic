package com.portabull.um.filters;

import com.portabull.execption.UnAuthorizedException;
import com.portabull.utils.RequestHelper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class AdminFilter {

    @Before("execution(* com.portabull.um.controllers.admin.*.*(..))")
    public void beforeAdvice(JoinPoint joinPoint) {
        HttpServletRequest currentRequest = RequestHelper.getCurrentRequest();
        if (StringUtils.isEmpty(currentRequest.getHeader("latlong")) || !currentRequest.getHeader("latlong").contains(";")) {
            throw new UnAuthorizedException("Location Not Enabled, Location is mandatory");
        }

    }

}
