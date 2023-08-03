package com.portabull.payloads;

import java.io.Serializable;
import java.util.Date;

public class OTPPayload implements Serializable {

    private Long otpID;

    private String otp;

    private Date otpCreatedDate;

    private Date otpExpiredDate;

    private String typeOfOtp;

    private Long otpLength;

    public Long getOtpID() {
        return otpID;
    }

    public void setOtpID(Long otpID) {
        this.otpID = otpID;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public Date getOtpCreatedDate() {
        return otpCreatedDate;
    }

    public void setOtpCreatedDate(Date otpCreatedDate) {
        this.otpCreatedDate = otpCreatedDate;
    }

    public Date getOtpExpiredDate() {
        return otpExpiredDate;
    }

    public void setOtpExpiredDate(Date otpExpiredDate) {
        this.otpExpiredDate = otpExpiredDate;
    }

    public String getTypeOfOtp() {
        return typeOfOtp;
    }

    public void setTypeOfOtp(String typeOfOtp) {
        this.typeOfOtp = typeOfOtp;
    }

    public Long getOtpLength() {
        return otpLength;
    }

    public void setOtpLength(Long otpLength) {
        this.otpLength = otpLength;
    }

}
