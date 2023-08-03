package com.portabull.generic.converters;


import com.portabull.generic.models.OneTimePassword;
import com.portabull.payloads.OTPPayload;

public class OtpConverter {

    private OtpConverter() {
    }

    public static OneTimePassword convertOTPPayloadToEntity(OTPPayload payload) {
        OneTimePassword oneTimePassword = new OneTimePassword();
        oneTimePassword.setOtp(payload.getOtp());
        oneTimePassword.setOtpID(payload.getOtpID());
        oneTimePassword.setOtpCreatedDate(payload.getOtpCreatedDate());
        oneTimePassword.setOtpExpiredDate(payload.getOtpExpiredDate());
        oneTimePassword.setOtpLength(payload.getOtpLength());
        oneTimePassword.setTypeOfOtp(payload.getTypeOfOtp());
        return oneTimePassword;
    }

    public static OTPPayload convertOTPPayloadToEntity(OneTimePassword oneTimePassword) {
        OTPPayload payload = new OTPPayload();
        payload.setOtp(oneTimePassword.getOtp());
        payload.setOtpID(oneTimePassword.getOtpID());
        payload.setOtpCreatedDate(oneTimePassword.getOtpCreatedDate());
        payload.setOtpExpiredDate(oneTimePassword.getOtpExpiredDate());
        payload.setOtpLength(oneTimePassword.getOtpLength());
        payload.setTypeOfOtp(oneTimePassword.getTypeOfOtp());
        return payload;
    }

}
