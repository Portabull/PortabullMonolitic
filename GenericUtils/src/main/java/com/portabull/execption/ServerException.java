package com.portabull.execption;

public class ServerException extends RuntimeException {

    public ServerException(String message) {
        super(message);

    }

    public ServerException(String message, Throwable error) {
        super(message, error);
    }
}
