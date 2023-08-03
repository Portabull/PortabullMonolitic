package com.portabull.response;

public class EmailResponse {

    private String message;

    private Long statusCode;

    private String status;

    private boolean hasErrors;

    private Object data;

    public String getMessage() {
        return message;
    }

    public EmailResponse setMessage(String message) {
        this.message = message;
        return this;
    }

    public Long getStatusCode() {
        return statusCode;
    }

    public EmailResponse setStatusCode(Long statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public EmailResponse setStatus(String status) {
        this.status = status;
        return this;
    }

    public boolean hasErrors() {
        return hasErrors;
    }

    public EmailResponse setHasErrors(boolean hasErrors) {
        this.hasErrors = hasErrors;
        return this;
    }

    public Object getData() {
        return data;
    }

    public EmailResponse setData(Object data) {
        this.data = data;
        return this;
    }
}
