package com.portabull.payloads;

import java.io.Serializable;

public class TokenData implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userID;

    private String userName;

    private Long startTime;

    private Long endTime;

    private String email;

    private Integer expirationTime;

    private boolean isTwoStepVerificationEnabled;

    private boolean validatedTwoStepAuth;

    private boolean singleSignOn;

    private String locationDetails;

    private String deviceDetails;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Integer getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Integer expirationTime) {
        this.expirationTime = expirationTime;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isTwoStepVerificationEnabled() {
        return isTwoStepVerificationEnabled;
    }

    public void setTwoStepVerificationEnabled(boolean twoStepVerificationEnabled) {
        isTwoStepVerificationEnabled = twoStepVerificationEnabled;
    }

    public boolean isValidatedTwoStepAuth() {
        return validatedTwoStepAuth;
    }

    public void setValidatedTwoStepAuth(boolean validatedTwoStepAuth) {
        this.validatedTwoStepAuth = validatedTwoStepAuth;
    }

    public boolean isSingleSignOn() {
        return singleSignOn;
    }

    public void setSingleSignOn(boolean singleSignOn) {
        this.singleSignOn = singleSignOn;
    }

    public String getLocationDetails() {
        return locationDetails;
    }

    public void setLocationDetails(String locationDetails) {
        this.locationDetails = locationDetails;
    }

    public String getDeviceDetails() {
        return deviceDetails;
    }

    public void setDeviceDetails(String deviceDetails) {
        this.deviceDetails = deviceDetails;
    }
}
