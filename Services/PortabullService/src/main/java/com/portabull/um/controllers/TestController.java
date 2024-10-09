package com.portabull.um.controllers;

import com.portabull.constants.PortableConstants;
import com.portabull.generic.dao.CommonDao;
import com.portabull.response.PortableResponse;
import com.portabull.um.RegistrationOtps;
import com.portabull.um.services.MyUserDetailsService;
import com.portabull.um.services.OTPValidationService;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class TestController {

    @Autowired
    OTPValidationService service;

    @Autowired
    CommonDao commonDao;

    Logger logger = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private MyUserDetailsService userDetailsService;

    @PostMapping("test")
    public ResponseEntity addTestUsers(@RequestParam(required = false) String alias) {

        if (alias == null) {
            alias = "testportabull";
        } else {
            alias = alias + "testportabull";
        }

        logger.info("Request Submitted Alias: {}",alias);

        createUsers(alias);

        return new ResponseEntity("", HttpStatus.OK);
    }

    private void createUsers(String alias) {
        new Thread(() -> {

            createUsersInParllel(alias);

        }).start();
    }

    private void createUsersInParllel(String alias) {

        logger.info("Thread Submitted Alias: {}",alias);

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        for (int i = 1; i <= 10; i++) {
            int startRange = (i - 1) * 500000 + 1;
            int endRange = i * 500000;


            // Each thread will handle a range of users
            executorService.submit(() -> createUsersInRange(startRange, endRange, alias));

            // Shutdown the executor service once all tasks are submitted
            executorService.shutdown();
        }


    }

    private void createUsersInRange(int startRange, int endRange, String alias) {

        for (int i = startRange; i <= endRange; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, 10);

            RegistrationOtps registrationOtps = new RegistrationOtps();
            registrationOtps.setOtp(service.generateOTP(6));
            registrationOtps.setOtpLength(Long.valueOf(6));
            registrationOtps.setOtpCreatedDate(new Date());
            registrationOtps.setOtpExpiredDate(calendar.getTime());
            registrationOtps.setToken(generateRegistrationToken());
            commonDao.saveOrUpdateEntity(registrationOtps);

            String userName = alias + i + "@gmail.com";
            String password = "admin123";
            String email = userName;

            String otp = registrationOtps.getOtp();
            String token = registrationOtps.getToken();

            if (userDetailsService.isAlreadyUserExists(userName, password, email)) {
                continue;
            }

            userDetailsService.registration(userName, password, email, otp, token);

            logger.info("email registered: {}", email);

        }
    }

    private String generateRegistrationToken() {
        StringBuilder time = new StringBuilder(String.valueOf(new Date().getTime()));
        return new String(Base64.getEncoder().encode(new StringBuilder(RandomStringUtils.random(10, true, false)).
                append(time.reverse().substring(0, 5)).
                append(RandomStringUtils.random(10, true, false)).
                append(time.reverse().substring(5, time.length())).toString().getBytes()));
    }

}
