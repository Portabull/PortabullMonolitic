package com.portabull.execption;

/**
 * This exception is thrown when if already work around Initialized
 */

public class AlreadyInitializedExecption extends Exception {

    private AlreadyInitializedExecption() {

    }

    public AlreadyInitializedExecption(String message) {
        super(message);
    }


}
