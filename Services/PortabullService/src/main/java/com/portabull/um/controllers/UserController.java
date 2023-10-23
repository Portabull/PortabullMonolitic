package com.portabull.um.controllers;

import com.portabull.constants.LoggerErrorConstants;
import com.portabull.constants.MessageConstants;
import com.portabull.constants.PortableConstants;
import com.portabull.constants.StatusCodes;
import com.portabull.response.PortableResponse;
import com.portabull.um.services.MyUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("UM/users")
public class UserController {

    Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    MyUserDetailsService userDetailsService;

    @PostMapping("get-users")
    public ResponseEntity<?> getUsers(@RequestBody Map<String, String> payload) {
        try {

            String userName = payload.get("userName");

            return new ResponseEntity<>(userDetailsService.getUsers(userName, false), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(LoggerErrorConstants.EXCEPTION_OCCURRED, e);
            return new ResponseEntity<>(new PortableResponse(
                    MessageConstants.SERVICE_FAILED, StatusCodes.C_500,
                    PortableConstants.FAILED, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("get-users")
    public ResponseEntity<?> getUsers() {
        try {
            return new ResponseEntity<>(userDetailsService.getUsers(null, true), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(LoggerErrorConstants.EXCEPTION_OCCURRED, e);
            return new ResponseEntity<>(new PortableResponse(
                    MessageConstants.SERVICE_FAILED, StatusCodes.C_500,
                    PortableConstants.FAILED, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("get-logged-in-dtls")
    public ResponseEntity<?> getLoggedInDtls() {
        return new ResponseEntity<>(userDetailsService.getLoggedInDtls(), HttpStatus.OK);
    }

}
