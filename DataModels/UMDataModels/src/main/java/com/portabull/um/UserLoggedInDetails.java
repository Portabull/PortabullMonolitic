package com.portabull.um;

import com.portabull.constants.DatabaseSchema;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user_logged_in_details", schema = DatabaseSchema.USER_MANAGEMENT, catalog = DatabaseSchema.USER_MANAGEMENT)
public class UserLoggedInDetails {

    @Id
    @Column(name = "logged_in_user_details_id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    @SequenceGenerator(name = "seq", sequenceName = ".seq_logged_in_dtls_id")
    private Long loggedInUserDetailsId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "lattitude")
    private String lattitude;

    @Column(name = "longitude")
    private String longitude;

    @Column(name = "successfully_logged_in")
    private Boolean successfullyLoggedIn;

    @Column(name = "logged_in_date")
    private Date loggedInDate;

    public Date getLoggedInDate() {
        return loggedInDate;
    }

    public void setLoggedInDate(Date loggedInDate) {
        this.loggedInDate = loggedInDate;
    }

    public Long getLoggedInUserDetailsId() {
        return loggedInUserDetailsId;
    }

    public void setLoggedInUserDetailsId(Long loggedInUserDetailsId) {
        this.loggedInUserDetailsId = loggedInUserDetailsId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getLattitude() {
        return lattitude;
    }

    public void setLattitude(String lattitude) {
        this.lattitude = lattitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Boolean getSuccessfullyLoggedIn() {
        return successfullyLoggedIn;
    }

    public void setSuccessfullyLoggedIn(Boolean successfullyLoggedIn) {
        this.successfullyLoggedIn = successfullyLoggedIn;
    }

    public UserLoggedInDetails() {

    }

    public UserLoggedInDetails(Long userId, String lattitude, String longitude, Boolean successfullyLoggedIn, Date loggedInDate) {
        this.userId = userId;
        this.lattitude = lattitude;
        this.longitude = longitude;
        this.successfullyLoggedIn = successfullyLoggedIn;
        this.loggedInDate = loggedInDate;
    }
}
