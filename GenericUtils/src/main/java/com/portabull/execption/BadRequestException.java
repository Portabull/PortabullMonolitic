package com.portabull.execption;

public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable error) {
        super(message, error);
    }

}
