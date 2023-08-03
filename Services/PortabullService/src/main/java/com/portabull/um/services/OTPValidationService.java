package com.portabull.um.services;

import com.portabull.cache.LoginUtils;
import com.portabull.constants.MessageConstants;
import com.portabull.constants.PortableConstants;
import com.portabull.constants.StatusCodes;
import com.portabull.execption.BadRequestException;
import com.portabull.generic.dao.CommonDao;
import com.portabull.payloads.EmailPayload;
import com.portabull.response.EmailResponse;
import com.portabull.response.PortableResponse;
import com.portabull.um.LoggedInOTP;
import com.portabull.um.RegistrationOtps;
import com.portabull.um.UserCredentials;
import com.portabull.um.dao.UserCredentialsDao;
import com.portabull.utils.HTMLTemplete;
import com.portabull.utils.commonutils.CommonUtils;
import com.portabull.utils.emailutils.EmailUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.*;
import java.util.stream.IntStream;

@Service
public class OTPValidationService {

    static Random random;

    static {
        random = new Random();
    }

    private static final String NUMBERS = "123456789";

    @Autowired
    HTMLTemplete htmlTemplete;

    @Autowired
    EmailUtils emailUtils;

    @Autowired
    CommonDao commonDao;

    @Autowired
    UserCredentialsDao userCredentialsDao;

    @Autowired
    private MyUserDetailsService userDetailsService;

    public EmailResponse sendOTPToEmail(EmailPayload emailPayload, Long userId, boolean isRegistration) {
        LoggedInOTP otpPayload;
        if (userId != null) {
            otpPayload = generateOTP(userId);
        } else if (isRegistration) {
            return generateRegistrationOtp(emailPayload);
        } else {
            otpPayload = generateOTP();
        }

        String otpEmailTemplete = htmlTemplete.getOTPEmailTemplete(otpPayload.getOtp(), 10);

        emailPayload.setSubject("Generated OTP from Portabull");
        emailPayload.setBody(otpEmailTemplete);
        emailPayload.setHtmlTemplete(true);

        otpPayload.setOtp(null);

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        new Thread(() -> {

            RequestContextHolder.setRequestAttributes(requestAttributes, true);

            emailUtils.sendEmail(emailPayload);

        }).start();

        EmailResponse response = new EmailResponse();

        response.setStatus(PortableConstants.SUCCESS);

        response.setMessage(MessageConstants.EMAIL_SENT_SUCCESS);

        response.setStatusCode(StatusCodes.C_200);

        response.setHasErrors(false);

        response.setData(otpPayload);

        return response;
    }

    private EmailResponse generateRegistrationOtp(EmailPayload emailPayload) {
        String email = emailPayload.getTo().get(0);

        if (userDetailsService.isAlreadyUserExists(null, null, email))
            throw new BadRequestException("Already User Exists");

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 10);

        RegistrationOtps registrationOtps = new RegistrationOtps();
        registrationOtps.setOtp(generateOTP(6));
        registrationOtps.setOtpLength(Long.valueOf(6));
        registrationOtps.setOtpCreatedDate(new Date());
        registrationOtps.setOtpExpiredDate(calendar.getTime());
        registrationOtps.setToken(generateRegistrationToken());
        commonDao.saveOrUpdateEntity(registrationOtps);
        String otpEmailTemplete = htmlTemplete.getOTPEmailTemplete(registrationOtps.getOtp(), 10);

        emailPayload.setSubject("Generated OTP from Portabull");
        emailPayload.setBody(otpEmailTemplete);
        emailPayload.setHtmlTemplete(true);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("token", registrationOtps.getToken());
        return emailUtils.sendEmail(emailPayload).setData(data);
    }

    private String generateRegistrationToken() {
        StringBuilder time = new StringBuilder(String.valueOf(new Date().getTime()));
        return new String(Base64.getEncoder().encode(new StringBuilder(RandomStringUtils.random(10, true, false)).
                append(time.reverse().substring(0, 5)).
                append(RandomStringUtils.random(10, true, false)).
                append(time.reverse().substring(5, time.length())).toString().getBytes()));
    }

    private LoggedInOTP generateOTP() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 10);


        LoggedInOTP loggedInOTP = new LoggedInOTP();

        loggedInOTP.setUserId(CommonUtils.getLoggedInUserId());
        loggedInOTP.setOtp(generateOTP(6));
        loggedInOTP.setOtpLength(Long.valueOf(6));
        loggedInOTP.setOtpCreatedDate(new Date());
        loggedInOTP.setOtpExpiredDate(calendar.getTime());

        commonDao.saveOrUpdateEntity(loggedInOTP);
        return loggedInOTP;
    }

    private LoggedInOTP generateOTP(Long userId) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 10);


        LoggedInOTP loggedInOTP = new LoggedInOTP();

        loggedInOTP.setUserId(userId);
        loggedInOTP.setOtp(generateOTP(6));
        loggedInOTP.setOtpLength(Long.valueOf(6));
        loggedInOTP.setOtpCreatedDate(new Date());
        loggedInOTP.setOtpExpiredDate(calendar.getTime());

        commonDao.saveOrUpdateEntity(loggedInOTP);
        return loggedInOTP;
    }

    public String generateOTP(int size) {
        StringBuilder otpBuilder = new StringBuilder();
        IntStream.range(0, size).forEach(
                index -> otpBuilder.append(NUMBERS.charAt(random.nextInt(NUMBERS.length())))
        );
        return otpBuilder.toString();
    }

    public Object isValidOtp(String otp) {
        PortableResponse portableResponse = new PortableResponse();
        portableResponse.setMessage("Invalid Otp");
        portableResponse.setStatus(PortableConstants.FAILED);
        portableResponse.setStatusCode(401L);
        UserCredentials userCredential = userCredentialsDao.getUserCredential();
        if (userCredential != null && !CollectionUtils.isEmpty(userCredential.getLoggedInOTPS())) {
            userCredential.getLoggedInOTPS().forEach(loggedInOtp -> {
                if (otp.equals(loggedInOtp.getOtp())) {
                    LoginUtils.validatedTwoStepAuth();
                    portableResponse.setMessage("Validated Successfully");
                    portableResponse.setStatus(PortableConstants.SUCCESS);
                    portableResponse.setStatusCode(200L);
                }
            });
        }
        return portableResponse;
    }

    public EmailResponse sendOTPToEmailForRegistration(EmailPayload emailPayload) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 10);


        LoggedInOTP loggedInOTP = new LoggedInOTP();
        loggedInOTP.setOtp(generateOTP(6));
        loggedInOTP.setOtpLength(Long.valueOf(6));
        loggedInOTP.setOtpCreatedDate(new Date());
        loggedInOTP.setOtpExpiredDate(calendar.getTime());


        String otpEmailTemplete = htmlTemplete.getOTPEmailTemplete(loggedInOTP.getOtp(), 10);

        emailPayload.setSubject("Generated OTP from Portabull");
        emailPayload.setBody(otpEmailTemplete);
        emailPayload.setHtmlTemplete(true);


        EmailResponse emailResponse = emailUtils.sendEmail(emailPayload);
        emailResponse.setData(loggedInOTP);

        return emailResponse;
    }

}
