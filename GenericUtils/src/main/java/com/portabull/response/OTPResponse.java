package com.portabull.response;

import java.io.Serializable;

public class OTPResponse implements Serializable {

    private Long otpID;

    private boolean isValidOTP;

    private boolean isExpiredOTP;

    private String message;

    private String status;

    private int statusCode;

    public Long getOtpID() {
        return otpID;
    }

    public void setOtpID(Long otpID) {
        this.otpID = otpID;
    }

    public boolean isValidOTP() {
        return isValidOTP;
    }

    public void setValidOTP(boolean validOTP) {
        isValidOTP = validOTP;
    }

    public boolean isExpiredOTP() {
        return isExpiredOTP;
    }

    public void setExpiredOTP(boolean expiredOTP) {
        isExpiredOTP = expiredOTP;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
