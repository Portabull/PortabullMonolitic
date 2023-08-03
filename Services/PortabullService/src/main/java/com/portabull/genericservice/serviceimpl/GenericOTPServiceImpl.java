package com.portabull.genericservice.serviceimpl;

import com.portabull.constants.PortableConstants;
import com.portabull.generic.dao.OTPDao;
import com.portabull.genericservice.service.GenericOTPService;
import com.portabull.payloads.OTPPayload;
import com.portabull.response.OTPResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GenericOTPServiceImpl implements GenericOTPService {

    @Autowired
    OTPDao otpDao;

    @Override
    public OTPResponse generateOTPResponse() {
        OTPPayload otpPayload = otpDao.generateOTP(6, 10);
        OTPResponse response = new OTPResponse();
        response.setOtpID(otpPayload.getOtpID());
        response.setExpiredOTP(false);
        response.setMessage("OTP Generated Successfully");
        response.setStatusCode(200);
        response.setStatus(PortableConstants.SUCCESS);
        response.setValidOTP(true);
        return response;
    }

    @Override
    public OTPResponse verifyOTP(OTPPayload otpPayload) {
        boolean validOTP = otpDao.isValidOTP(otpPayload);
        OTPResponse response = new OTPResponse();
        response.setExpiredOTP(!validOTP);
        response.setMessage("Successfully validated");
        response.setStatusCode(200);
        response.setStatus(PortableConstants.SUCCESS);
        response.setValidOTP(validOTP);
        return response;
    }

    @Override
    public OTPPayload generateOTP() {
        return otpDao.generateOTP(6, 10);
    }

}
