package com.portabull.um.controllers;

import com.portabull.constants.PortableConstants;
import com.portabull.constants.StatusCodes;
import com.portabull.payloads.ReturnReponseCode;
import com.portabull.response.PortableResponse;
import com.portabull.um.services.NotificationMFAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("UM/mfa")
public class NotificationMFAController {

    @Autowired
    NotificationMFAService notificationMFAService;


    @PostMapping("verify-random-token")
    public ResponseEntity<?> verifyRandomToken(@RequestBody Map<String, Object> payload) {

        PortableResponse response = notificationMFAService.verifyRandomToken(payload.get("token").toString());

        return new ResponseEntity<>(response, ReturnReponseCode.getHttpStatusCode(response.getStatusCode().intValue()));

    }

    @PostMapping("approve-deny-request")
    public ResponseEntity<?> approveDenyRequest(@RequestBody Map<String, Object> payload) {

        if (StringUtils.isEmpty(payload.get("token")) || StringUtils.isEmpty(payload.get("status"))) {
            return new ResponseEntity<>(new PortableResponse("", StatusCodes.C_400,
                    PortableConstants.FAILED, null), HttpStatus.BAD_REQUEST);
        }

        PortableResponse response = notificationMFAService.approveDenyRequest(payload.get("token").toString(),
                Integer.valueOf(payload.get("status").toString()));


        return new ResponseEntity<>(response, ReturnReponseCode.getHttpStatusCode(response.getStatusCode().intValue()));
    }

}
