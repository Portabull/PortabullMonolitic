package com.portabull.utils;

import com.portabull.constants.PortableConstants;
import com.portabull.constants.StatusCodes;
import com.portabull.response.PortableResponse;
import com.portabull.utils.commonutils.CommonUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
public class GenericRoute {

    private final Map<String, String> buildDetails;

    public GenericRoute() {

        Map<String, String> response = new HashMap<>();

        response.put("bid", new StringBuilder("b").append(new Date().getTime()).toString());

        buildDetails = Collections.unmodifiableMap(response);

    }

    @GetMapping("/portabull-health-check")
    public ResponseEntity<PortableResponse> healthCheck() {
        CommonUtils.validateToken();
        return new ResponseEntity<>(new PortableResponse(
                "Successfully Logged In", StatusCodes.C_200, PortableConstants.SUCCESS,
                null), HttpStatus.OK);
    }

    @GetMapping("/build-id")
    public ResponseEntity<Map> buildId() {
        return new ResponseEntity<>(buildDetails, HttpStatus.OK);
    }


}
