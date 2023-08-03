package com.portabull.um;

import com.portabull.constants.DatabaseSchema;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "registration_in_otp", schema = DatabaseSchema.USER_MANAGEMENT, catalog = DatabaseSchema.USER_MANAGEMENT)
public class RegistrationOtps {

    @Id
    @Column(name = "logged_in_otp_id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    @SequenceGenerator(name = "seq", sequenceName = ".seq_registration_otp_id")
    private Long registrationOtpId;

    @Column(name = "token")
    private String token;

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

    public Long getRegistrationOtpId() {
        return registrationOtpId;
    }

    public void setRegistrationOtpId(Long registrationOtpId) {
        this.registrationOtpId = registrationOtpId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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
