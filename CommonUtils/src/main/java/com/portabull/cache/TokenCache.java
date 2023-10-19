package com.portabull.cache;

import com.portabull.constants.DatabaseSchema;

import javax.persistence.*;


@Entity
@Table(name = "temp_token_cache", schema = DatabaseSchema.USER_MANAGEMENT, catalog = DatabaseSchema.USER_MANAGEMENT)
public class TokenCache {

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "seq", sequenceName = ".seq_token_id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    private Long id;

    @Column(name = "token_id", unique = true)
    private String token;

    @Column(name = "user_id")
    private Long userID;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "start_time")
    private Long startTime;

    @Column(name = "end_time")
    private Long endTime;

    @Column(name = "email")
    private String email;

    @Column(name = "expiration_time")
    private Integer expirationTime;

    @Column(name = "is_two_step_verification_enabled")
    private boolean isTwoStepVerificationEnabled;

    @Column(name = "validated_two_step_auth")
    private boolean validatedTwoStepAuth;

    @Column(name = "single_sign_on")
    private boolean singleSignOn;

    @Column(name = "location_details")
    private String locationDetails;

    @Column(name = "device_details")
    private String deviceDetails;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Integer expirationTime) {
        this.expirationTime = expirationTime;
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
