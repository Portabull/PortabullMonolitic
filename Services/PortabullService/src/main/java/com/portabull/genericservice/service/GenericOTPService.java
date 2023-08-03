package com.portabull.genericservice.service;

import com.portabull.payloads.OTPPayload;
import com.portabull.response.OTPResponse;

public interface GenericOTPService {

    public OTPPayload generateOTP();

    public OTPResponse verifyOTP(OTPPayload otpPayload);

    public OTPResponse generateOTPResponse();

}
