package com.portabull.um;


import com.portabull.constants.DatabaseSchema;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "user_profile", schema = DatabaseSchema.USER_MANAGEMENT, catalog = DatabaseSchema.USER_MANAGEMENT)
public class UserProfile implements Serializable {

    @Id
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private UserCredentials userCredentials;

    @Column(name = "mobile_number")
    private String mobileNumber;

    @Column(name = "alternative_mobile_number")
    private String alternativeMobileNumber;

    @Column(name = "address")
    private String address;

    @Column(name = "street_address")
    private String streetAddress;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "pincode")
    private String pincode;

    @Column(name = "landmark")
    private String landmark;

    @Column(name = "reporting_to")
    private Long reportingTO;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "reporting_to")
    private List<UserProfile> reporters;

    public UserCredentials getUserCredentials() {
        return userCredentials;
    }

    public void setUserCredentials(UserCredentials userCredentials) {
        this.userCredentials = userCredentials;
    }

    public Long getReportingTO() {
        return reportingTO;
    }

    public void setReportingTO(Long reportingTO) {
        this.reportingTO = reportingTO;
    }

    public List<UserProfile> getReporters() {
        return reporters;
    }

    public void setReporters(List<UserProfile> reporters) {
        this.reporters = reporters;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getAlternativeMobileNumber() {
        return alternativeMobileNumber;
    }

    public void setAlternativeMobileNumber(String alternativeMobileNumber) {
        this.alternativeMobileNumber = alternativeMobileNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }
}
