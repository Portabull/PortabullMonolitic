package com.portabull.utils.handler;

import com.portabull.constants.LoggerErrorConstants;
import com.portabull.constants.MessageConstants;
import com.portabull.constants.PortableConstants;
import com.portabull.constants.StatusCodes;
import com.portabull.execption.BadRequestException;
import com.portabull.execption.ServerException;
import com.portabull.execption.TokenNotFoundException;
import com.portabull.execption.UnAuthorizedException;
import com.portabull.response.PortableResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {

    String errorMessage = "Handler dispatch failed; nested exception is java.lang.OutOfMemoryError: Java heap space";

    Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @ExceptionHandler(UnAuthorizedException.class)
    public ResponseEntity<?> handleConflict(UnAuthorizedException e) {
        return new ResponseEntity<>(new PortableResponse(e.getMessage(), StatusCodes.C_401,
                PortableConstants.SUCCESS, null), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handleConflict(BadRequestException e) {
        return new ResponseEntity<>(new PortableResponse(e.getMessage(), StatusCodes.C_400,
                PortableConstants.FAILED, null), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ServerException.class)
    public ResponseEntity<?> handleConflict(ServerException e) {
        return new ResponseEntity<>(new PortableResponse(e.getMessage(), StatusCodes.C_400,
                PortableConstants.FAILED, null), HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<?> handleConflict(Throwable e) {
        if (errorMessage.equalsIgnoreCase(e.getMessage())) {
            System.gc();
        }
        logger.error(LoggerErrorConstants.EXCEPTION_OCCURRED, e);
        return new ResponseEntity<>(new PortableResponse(MessageConstants.SERVICE_FAILED, StatusCodes.C_500,
                PortableConstants.FAILED, null), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<?> handleConflict(TokenNotFoundException e) {
        return new ResponseEntity<>(new PortableResponse(HttpStatus.UNAUTHORIZED.name(), StatusCodes.C_401,
                PortableConstants.SUCCESS, null), HttpStatus.UNAUTHORIZED);
    }
}
