package com.portabull.response;

public class SmsResponse extends PortableResponse {

    private String mobileNumber;

    public SmsResponse() {
        super();
    }

    public SmsResponse(String message, Long statusCode, String status, Object data) {
        super(message, statusCode, status, data);
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
}
