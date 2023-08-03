package com.portabull.response;

import com.portabull.constants.PortableConstants;

import java.util.Date;

public class DocumentResponse {

    private Long documentID;

    private FileResponse fileResponse;

    private Long timeStamp;

    private String message;

    private Long statusCode;

    private String status;

    private Object data;

    private boolean containErrors;

    public DocumentResponse() {
        this.timeStamp = new Date().getTime();
        this.status = PortableConstants.SUCCESS;
    }

    public DocumentResponse(String message, Long statusCode, String status, Object data, FileResponse fileResponse) {
        this.message = message;
        this.statusCode = statusCode;
        this.status = status;
        this.data = data;
        this.timeStamp = new Date().getTime();

        if (PortableConstants.SUCCESS.equalsIgnoreCase(status)) {
            this.containErrors = false;
        }

        if (PortableConstants.FAILED.equalsIgnoreCase(status)) {
            this.containErrors = true;
        }

        this.fileResponse = fileResponse;
    }

    public DocumentResponse(String message, Long statusCode, String status, Object data) {
        this.message = message;
        this.statusCode = statusCode;
        this.status = status;
        this.data = data;
        this.timeStamp = new Date().getTime();

        if (PortableConstants.SUCCESS.equalsIgnoreCase(status)) {
            this.containErrors = false;
        }

        if (PortableConstants.FAILED.equalsIgnoreCase(status)) {
            this.containErrors = true;
        }

    }


    public Long getDocumentID() {
        return documentID;
    }

    public DocumentResponse setDocumentID(Long documentID) {
        this.documentID = documentID;
        return this;
    }

    public FileResponse getFileResponse() {
        return fileResponse;
    }

    public DocumentResponse setFileResponse(FileResponse fileResponse) {
        this.fileResponse = fileResponse;
        return this;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public DocumentResponse setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public DocumentResponse setMessage(String message) {
        this.message = message;
        return this;
    }

    public Long getStatusCode() {
        return statusCode;
    }

    public DocumentResponse setStatusCode(Long statusCode) {
        this.statusCode = statusCode;
        this.setStatus(statusCode != null && statusCode != 200 ? PortableConstants.FAILED : PortableConstants.SUCCESS);
        return this;
    }

    public String getStatus() {
        return status;
    }

    public DocumentResponse setStatus(String status) {
        this.status = status;
        return this;
    }

    public Object getData() {
        return data;
    }

    public DocumentResponse setData(Object data) {
        this.data = data;
        return this;
    }

    public void setContainErrors(boolean containErrors) {
        this.containErrors = containErrors;
    }

    public boolean containErrors() {
        return this.containErrors;
    }

}
