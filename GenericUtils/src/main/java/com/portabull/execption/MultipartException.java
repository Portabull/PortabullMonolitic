package com.portabull.execption;

import java.io.IOException;

/**
 * Multipart Request Exception
 */

public class MultipartException extends IOException {

    private MultipartException() {

    }

    public MultipartException(String message) {
        super(message);
    }


}
