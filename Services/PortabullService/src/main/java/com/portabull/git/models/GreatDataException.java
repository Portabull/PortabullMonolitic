package com.portabull.git.models;

import java.util.Arrays;
import java.util.List;

public class GreatDataException extends Exception {
    private static final long serialVersionUID = 1L;
    private int code;
    private String errorMessage;
    private List<String> details;

    public GreatDataException(int code, String errorMessage) {
        this.errorMessage = errorMessage;
        this.code = code;
    }

    public GreatDataException(int code, String errorMessage, String details) {
        this.code = code;
        this.errorMessage = errorMessage;
        this.details = Arrays.asList(details);
    }

    public GreatDataException(int code, String errorMessage, List<String> details) {
        this.code = code;
        this.errorMessage = errorMessage;
        this.details = details;
    }

    public GreatDataException(int code, List<String> details) {
        this.code = code;
        this.details = details;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public List<String> getDetails() {
        return this.details;
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }

    public String toString() {
        StringBuilder errorMessageToPrint = new StringBuilder("GreatDataException : ");
        errorMessageToPrint.append(this.errorMessage);
        if (this.details != null && !this.details.isEmpty()) {
            this.details.forEach((detail) -> {
                errorMessageToPrint.append(":").append(detail);
            });
        }

        return errorMessageToPrint.toString();
    }
}
