package com.portabull.utils;

import com.portabull.constants.PortableConstants;
import com.portabull.constants.StatusCodes;
import com.portabull.response.PortableResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GenericRoute {


    @GetMapping("/portabull-health-check")
    public ResponseEntity<PortableResponse> healthCheck() {
        return new ResponseEntity<>(new PortableResponse(
                "Successfully Logged In", StatusCodes.C_200, PortableConstants.SUCCESS,
                null), HttpStatus.OK);
    }

}
