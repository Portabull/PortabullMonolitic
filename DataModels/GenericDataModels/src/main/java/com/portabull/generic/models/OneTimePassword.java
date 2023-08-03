package com.portabull.generic.models;

import com.portabull.constants.DatabaseSchema;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "one_time_password", schema = DatabaseSchema.PORTABULL_GENERIC, catalog = DatabaseSchema.PORTABULL_GENERIC)
public class OneTimePassword implements Serializable {

    @Id
    @Column(name = "otp_id")
    @SequenceGenerator(name = "seq", sequenceName = ".seq_otp")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    private Long otpID;

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
