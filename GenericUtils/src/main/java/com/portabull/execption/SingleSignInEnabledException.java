package com.portabull.execption;

public class SingleSignInEnabledException extends RuntimeException {

    public SingleSignInEnabledException(String message) {
        super(message);
    }

    public SingleSignInEnabledException(String message, Throwable cause) {
        super(message, cause);
    }

}
