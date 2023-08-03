package com.portabull.um;

import com.portabull.constants.DatabaseSchema;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "logged_in_otp", schema = DatabaseSchema.USER_MANAGEMENT, catalog = DatabaseSchema.USER_MANAGEMENT)
public class LoggedInOTP {

    @Id
    @Column(name = "logged_in_otp_id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    @SequenceGenerator(name = "seq", sequenceName = ".seq_logged_in_otp_id")
    private Long loggedInOtpId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "otp", nullable = false)
    private String otp;

    @Column(name = "otp_created_date")
    private Date otpCreatedDate;

    @Column(name = "otp_expired_date")
    private Date otpExpiredDate;

    @Column(name = "type_of_otp")
    private String typeOfOtp;

    @Column(name = "otp_length")
    private Long otpLength;

    public Long getLoggedInOtpId() {
        return loggedInOtpId;
    }

    public void setLoggedInOtpId(Long loggedInOtpId) {
        this.loggedInOtpId = loggedInOtpId;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
}
