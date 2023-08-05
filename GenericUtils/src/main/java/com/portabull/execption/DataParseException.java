package com.portabull.execption;

public class DataParseException extends RuntimeException {

    public DataParseException(String message) {
        super(message);
    }

    public DataParseException(Throwable e, String message) {
        super(message, e);
    }
}
