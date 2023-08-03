package com.portabull.genericservice.controllers;

import com.portabull.genericservice.service.GenericOTPService;
import com.portabull.payloads.OTPPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("gs/otp")
public class GenericOTPController {

    @Autowired
    GenericOTPService otpService;

    @PostMapping("/generateOTP")
    public ResponseEntity<OTPPayload> generateOTP() {
        return new ResponseEntity<>(
                otpService.generateOTP(),
                HttpStatus.OK
        );
    }


}
