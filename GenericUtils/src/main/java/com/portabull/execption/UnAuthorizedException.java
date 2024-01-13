package com.portabull.execption;

public class UnAuthorizedException extends RuntimeException {

    public UnAuthorizedException(String message) {
        super(message);
    }
    public UnAuthorizedException() {
        super("UnAuthorized Access");
    }

}
