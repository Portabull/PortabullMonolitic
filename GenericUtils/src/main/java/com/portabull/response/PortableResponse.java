package com.portabull.response;

import com.portabull.constants.PortableConstants;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

import java.util.Date;

public class PortableResponse {

    private Long timeStamp;

    private String message;

    private Long statusCode;

    private String status;

    private Object data;

    public PortableResponse() {
        this.timeStamp = new Date().getTime();
        this.status = PortableConstants.SUCCESS;
    }

    public PortableResponse(String message, Long statusCode, String status, Object data) {
        this.message = message;
        this.statusCode = statusCode;
        this.status = status;
        this.data = data;
        this.timeStamp = new Date().getTime();
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public PortableResponse setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public PortableResponse setMessage(String message) {
        this.message = message;
        return this;
    }

    public Long getStatusCode() {
        return statusCode;
    }

    public PortableResponse setStatusCode(Long statusCode) {
        this.statusCode = statusCode;
        this.setStatus(statusCode != null && statusCode != 200 ? PortableConstants.FAILED : PortableConstants.SUCCESS);
        return this;
    }

    public String getStatus() {
        return status;
    }

    public PortableResponse setStatus(String status) {
        this.status = status;
        return this;
    }

    public Object getData() {
        return data;
    }

    public PortableResponse setData(Object data) {
        this.data = data;
        return this;
    }

    public static HttpStatus getHttpCode(Object statusCode) {
        if (statusCode == null || StringUtils.isEmpty(statusCode.toString()))
            return HttpStatus.INTERNAL_SERVER_ERROR;


        return HttpStatus.resolve(Integer.parseInt(statusCode.toString()));
    }
}
