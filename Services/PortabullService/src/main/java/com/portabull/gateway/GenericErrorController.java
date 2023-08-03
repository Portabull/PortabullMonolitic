package com.portabull.gateway;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@RestController
public class GenericErrorController implements ErrorController {


    private static final String PATH = "/error";

    @RequestMapping(value = PATH)
    public ModelAndView handlePageNotFound() {
        ModelAndView modelAndView = new ModelAndView();

        HttpServletRequest request = getRequest();

        modelAndView.setViewName("internalservererror.html");

        prepareNotFoundRequest(request, modelAndView);

        return modelAndView;
    }

    private void prepareNotFoundRequest(HttpServletRequest request, ModelAndView modelAndView) {
        if (request != null) {
            Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
            if (status != null) {
                Integer statusCode = Integer.valueOf(status.toString());
                if (statusCode == HttpStatus.NOT_FOUND.value()) {
                    modelAndView.setViewName("genericpagenotfound.html");
                }
            }
        }
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }

    private HttpServletRequest getRequest() {
        HttpServletRequest request = null;

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        if (requestAttributes != null)
            request = ((ServletRequestAttributes) requestAttributes).getRequest();

        return request;
    }

}
