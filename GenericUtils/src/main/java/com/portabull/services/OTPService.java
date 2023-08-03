package com.portabull.services;

import com.portabull.payloads.OTPPayload;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.stream.IntStream;

@Component
public class OTPService {

    static Random random;

    static {
        random = new Random();
    }


    private static final String NUMBERS = "123456789";

    private static final String ALPHABETIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private static final String RAW = "123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*_=+-/.?<>)";

    public OTPPayload generateOTP(int size, int expirationTimeInMins) {
        OTPPayload otpPayload = new OTPPayload();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, expirationTimeInMins);
        otpPayload.setOtp(generateOTP(size));
        otpPayload.setOtpLength(Long.valueOf(size));
        otpPayload.setOtpCreatedDate(new Date());
        otpPayload.setOtpExpiredDate(calendar.getTime());
        return otpPayload;
    }

    public String generateOTP(int size) {
        StringBuilder otpBuilder = new StringBuilder();
        IntStream.range(0, size).forEach(
                index -> otpBuilder.append(NUMBERS.charAt(random.nextInt(NUMBERS.length())))
        );
        return otpBuilder.toString();
    }

    public String generateAlphabeticOTP(int size) {
        StringBuilder otpBuilder = new StringBuilder();
        IntStream.range(0, size).forEach(
                index -> otpBuilder.append(ALPHABETIC.charAt(random.nextInt(ALPHABETIC.length())))
        );
        return otpBuilder.toString();
    }

    public String generateRawOTP(int size) {
        StringBuilder otpBuilder = new StringBuilder();
        IntStream.range(0, size).forEach(
                index -> otpBuilder.append(RAW.charAt(random.nextInt(RAW.length())))
        );
        return otpBuilder.toString();
    }


}
